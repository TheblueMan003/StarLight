package objects

import types.*
import sl.*
import sl.Utils
import scala.collection.mutable
import sl.Compilation.Selector.*
import sl.Compilation.Print
import sl.IR.*

object Function {
  extension (str: (Function, List[Expression])) {
    def call(ret: Variable = null, op: String = "=")(implicit context: Context): List[IRTree] = {
        if (str._1 == null){
            List()
        }
        else if (str._1.hasRawJsonArg()){
            val prefix = str._2.take(str._1.arguments.length - 1)
            val sufix = str._2.drop(str._1.arguments.length - 1)
            val (prep, json) = Print.toRawJson(sufix)
            prep ::: str._1.call(prefix ::: List(json), ret, op)
        }
        else if (str._1.hasParamsArg()){
            val prefix = str._2.take(str._1.arguments.length - 1)
            val sufix = str._2.drop(str._1.arguments.length - 1)
            val tuple = TupleValue(sufix)
            str._1.call(prefix ::: List(tuple), ret, op)
        }
        else{
            str._1.call(str._2, ret, op)
        }
    }
    def markAsStringUsed()={
        str._1.markAsStringUsed()
    }
  }
}
abstract class Function(context: Context, name: String, val arguments: List[Argument], typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
    val parentFunction = context.getCurrentFunction()
    val parentVariable = context.getCurrentVariable()
    val parentClass = context.getCurrentClass()
    override lazy val fullName: String = if (context.isInLazyCall() || parentFunction != null || parentVariable != null || modifiers.protection==Protection.Private || Settings.obfuscate) then context.fctCtx.getFreshId()else context.getPath() + "." + name
    val minArgCount = getMinArgCount(arguments)
    val maxArgCount = getMaxArgCount(arguments)
    var argumentsVariables: List[Variable] = List()
    val clazz = context.getCurrentClass()
    var overridedFunction: Function = null
    var stringUsed = false

    if (modifiers.isVirtual){

    }
    if (modifiers.isConst){
        throw new Exception("Function cannot be marked as const")
    }
    def markAsStringUsed()={
        stringUsed = true
    }
    private def getMaxArgCount(args: List[Argument]): Int = {
        if args.length == 0 then 0 else
        args.last.typ match
            case ParamsType => Integer.MAX_VALUE
            case RawJsonType => Integer.MAX_VALUE
            case _ => args.length
    }

    def prototype() = {
        fullName + "(" +arguments.map(_.typ.toString()).foldRight("")(_ +","+_) +")"
    }

    def schema() = {
        modifiers.schema() + typ.toString() +" "+ fullName + "(" +arguments.map(a => a.typ.toString()+" "+a.name).foldRight("")(_ +","+_) +")"
    }

    def hasRawJsonArg(): Boolean = {
        if arguments.length == 0 then false else
        arguments.last.typ match
            case RawJsonType => true
            case _ => false
    }
    def hasParamsArg(): Boolean = {
        if arguments.length == 0 then false else
        arguments.last.typ match
            case ParamsType => true
            case _ => false
    }

    def canBeCallAtCompileTime = false

    private def getMinArgCount(args: List[Argument], stopped: Boolean = false): Int = {
        args match{
            case head::tail => {
                head.defValue match
                    case None => 
                        if (stopped){
                            throw new Exception(f"${head.name} must have a default value in ${fullName}")
                        }
                        else{
                            getMinArgCount(tail, false) + 1
                        }
                    case Some(value) => {
                        getMinArgCount(tail, true)
                    }
            }
            case Nil => 0
        }
    }
    def call(args: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[IRTree]
    def generateArgument()(implicit ctx: Context):Unit = {
        val ctx2 = ctx.push(name)
        argumentsVariables = arguments.map(a => {
            val mod = new Modifier()
            mod.protection = Protection.Private
            val vari = new Variable(ctx2, a.name, ctx.getType(a.typ), mod)
            vari.isFunctionArgument = true
            ctx2.addVariable(vari)
            vari.generate(ctx.getCurrentVariable() != null)(ctx2)
            vari
        })
    }
    def argMap(args: List[Expression]) = {
        if args.size > argumentsVariables.size then throw new Exception(f"Too much argument for $fullName got: $args expected: $arguments")
        if args.length < minArgCount then throw new Exception(f"Too few argument for $fullName got: $args expected: $arguments")
        argumentsVariables.filter(_.getType() != VoidType)
                .zip(arguments.map(_.defValue))
                .zipAll(args, null, null)
                .map(p => (p._1._1, if p._2 == null then p._1._2.get else p._2))
    }
    def exists(): Boolean
    def getName(): String
    def getContent(): List[IRTree]
    def getFunctionType() = FuncType(arguments.map(a => a.typ), typ)
    def getIRFile(): IRFile ={
        IRFile(getName(), fullName, getContent(), false, !(modifiers.isTicking || modifiers.isLoading || modifiers.protection == Protection.Public || stringUsed))
    }
}

class ConcreteFunction(context: Context, name: String, arguments: List[Argument], typ: Type, _modifier: Modifier, val body: Instruction, topLevel: Boolean) extends Function(context, name, arguments, typ, _modifier){
    private var _needCompiling = topLevel || Settings.allFunction
    private var wasCompiled = false
    
    protected var content = List[IRTree]()
    val returnVariable = {
        val ctx = context.push(name)
        val vari = new Variable(ctx, "_ret", typ, Modifier.newPrivate())
        vari.isFunctionArgument = true
        vari.generate()(context.push(name))
        ctx.addVariable(vari)
    }
    private lazy val muxID = context.getFunctionMuxID(this)

    if (needCompiling()) {
        context.addFunctionToCompile(this)
    }

    override def generateArgument()(implicit ctx: Context): Unit = {
        arguments.foreach(a => {
            if (a.name.contains("$")){
                throw new Exception(f"Illegal Arguement Name: ${a.name} in $fullName. Missing lazy key word?")
            }
        })
        super.generateArgument()
    }
    
    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[IRTree] = {
        markAsUsed()
        val r = argMap(args2).flatMap(p => p._1.assign("=", Utils.simplify(p._2))) :::
            List(BlockCall(Settings.target.getFunctionName(fullName), fullName))

        if (ret != null){
            r ::: ret.assign(op, LinkedVariableValue(returnVariable))
        }
        else{
            r
        }
    }

    def compile():Unit={
        if (!wasCompiled){
            val suf = sl.Compiler.compile(body)(context.push(name, this))
            content = content ::: suf
            wasCompiled = true
        }
    }

    def addMuxCleanUp(cnt: List[IRTree]): Unit = {
        content = cnt ::: content
    }
    def append(cnt: List[IRTree]): Unit = {
        content = content ::: cnt
    }

    def exists(): Boolean = true
    def getContent(): List[IRTree] = content
    def getName(): String = Settings.target.getFunctionPath(fullName)

    def needCompiling():Boolean = {
        _needCompiling && !wasCompiled
    }
    def markAsUsed():Unit = {
        _needCompiling = true
        if (needCompiling()) {
            context.addFunctionToCompile(this)
        }
    }

    def getMuxID(): Int = {
        muxID
    }
}

class BlockFunction(context: Context, name: String, arguments: List[Argument], var body: List[IRTree]) extends Function(context, name, arguments, VoidType, Modifier.newPrivate()){
    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[IRTree] = {
        argMap(args2).flatMap(p => p._1.assign("=", p._2)) :::
            List(BlockCall(Settings.target.getFunctionName(fullName), fullName))
    }

    def exists(): Boolean = true
    def getContent(): List[IRTree] = body
    def getName(): String = Settings.target.getFunctionPath(fullName)

    def append(cnt: List[IRTree]): Unit = {
        body = body ::: cnt
    }
}

class LazyFunction(context: Context, name: String, arguments: List[Argument], typ: Type, _modifier: Modifier, val body: Instruction) extends Function(context, name, arguments, typ, _modifier){
    def call(args: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[IRTree] = {
        var block = body
        val sub = ctx.push(ctx.getLazyCallId())
        sub.setLazyCall()
        sub.inherit(context)
        
        argMap(args).sortBy((a,v) => -a.name.length).foreach((a, v) => {
            val vari = Variable(sub, a.name, a.getType(), a.modifiers)
            vari.generate()(sub)
            sub.addVariable(vari).assign("=", v)
            block = if a.name.startsWith("$") then Utils.subst(block, a.name, v.getString()) else block
        })

        if (ret != null) sub.addVariable("_ret", ret)
        if (ret == null){
            sub.addVariable("_ret", new Variable(sub, "_ret", VoidType, Modifier.newPrivate()))
            sl.Compiler.compile(block)(if modifiers.hasAttributes("inline") then ctx else sub)
        }
        else if (op == "="){
            block = Utils.substReturn(block, ret)(!modifiers.hasAttributes("__returnCheck__"))
            sl.Compiler.compile(block)(if modifiers.hasAttributes("inline") then ctx else sub)
        }
        else{
            val vari = ctx.getFreshVariable(getType())
            block = Utils.substReturn(block, vari)(!modifiers.hasAttributes("__returnCheck__"))
            sl.Compiler.compile(block)(if modifiers.hasAttributes("inline") then ctx else sub) ::: (if ret == null then List() else ret.assign(op, LinkedVariableValue(vari)))
        }
    }
    override def generateArgument()(implicit ctx: Context):Unit = {
        super.generateArgument()
        argumentsVariables.foreach(_.modifiers.isLazy = true)
    }
    override def canBeCallAtCompileTime = true

    def exists(): Boolean = false
    def getContent(): List[IRTree] = List()
    def getName(): String = Settings.target.getFunctionPath(fullName)
}

class MultiplexFunction(context: Context, name: String, arguments: List[Argument], typ: Type) extends ConcreteFunction(context, name, arguments, typ, objects.Modifier.newPrivate(), sl.InstructionList(List()), false){
    private val functions = mutable.ListBuffer[ConcreteFunction]()
    override def needCompiling():Boolean = {
        false
    }

    def addFunctionToMux(fct: ConcreteFunction)(implicit context: Context) = {
        if (!functions.contains(fct)){
            functions.addOne(fct)
            fct.addMuxCleanUp(argumentsVariables.head.assign("=", IntValue(0)))
        }
    }

    override def exists()= true

    override def compile(): Unit = {
        val cases = 
            if typ == VoidType then
                functions.zipWithIndex.map((x, i) => SwitchCase(IntValue(x.getMuxID()), LinkedFunctionCall(x, argumentsVariables.tail.map(LinkedVariableValue(_))))).toList
            else
                functions.zipWithIndex.map((x, i) => SwitchCase(IntValue(x.getMuxID()), LinkedFunctionCall(x, argumentsVariables.tail.map(LinkedVariableValue(_)), returnVariable))).toList
        
        val switch = Switch(LinkedVariableValue(argumentsVariables.head), cases, false)
        content = sl.Compiler.compile(switch)(context.push(name, this))
    }
}

class TagFunction(context: Context, name: String, arguments: List[Argument]) extends ConcreteFunction(context, name, arguments, VoidType, objects.Modifier.newPrivate(), sl.InstructionList(List()), false){
    private val functions = mutable.Set[Function]()
    override def needCompiling():Boolean = {
        false
    }
    
    def addFunction(fct: Function) = {
        if (!functions.contains(fct)){
            functions.addOne(fct)
        }
    }

    def getFunctionsName(): List[String] = {
        functions.map(fct => Settings.target.getFunctionName(fct.fullName)).toList
    }

    def getCompilerFunctionsName(): List[String] = {
        functions.map(fct => fct.fullName).toList
    }


    override def exists()= true

    def getFunctions() = {
        functions.toList.sortBy(f => {
            Utils.simplify(f.modifiers.attributes.getOrElse("tag.order", IntValue(0)))(context) match {
                case IntValue(n) => n.toDouble
                case FloatValue(n) => n
                case _ => 0.0
            }
        })
    }

    override def compile(): Unit = {
        val normal: List[Instruction] = getFunctions().filter(_.clazz == null).map(LinkedFunctionCall(_, argumentsVariables.map(LinkedVariableValue(_)))).toList
        val clazz: List[Instruction] = getFunctions().filter(_.clazz != null).map(f => {
            With(SelectorValue(JavaSelector("@e",List(("tag", SelectorIdentifier(f.clazz.getTag()))))), BoolValue(true), BoolValue(true),
            LinkedFunctionCall(f, argumentsVariables.map(LinkedVariableValue(_))))}).toList

        content = sl.Compiler.compile(InstructionList(normal ::: clazz))(context.push(name, this))
    }
}

class ClassFunction(variable: Variable, function: Function) extends Function(function.context, function.name, function.arguments, function.getType(), function.modifiers){
    override def exists()= false

    override def getContent(): List[IRTree] = List()
    override def getName(): String = function.name

    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[IRTree] = {
        val selector = SelectorValue(JavaSelector("@e", List(("tag", SelectorIdentifier("__class__")))))

        def callNoEntity(comp: Variable, ret: Variable = null) = {
            Compiler.compile(With(
                selector, 
                BoolValue(false), 
                BinaryOperation("==", LinkedVariableValue(comp), LinkedVariableValue(ctx.root.push("object").getVariable("__ref"))),
                LinkedFunctionCall(function, args2, ret)
                ))
        }

        var pre = List[IRTree]()
        val comp = if (variable.modifiers.isEntity){
            val vari = ctx.getFreshVariable(IntType)
            pre = vari.assignForce(variable)
            vari
        }
        else{
            variable
        }

        if ((ret == null || !ret.modifiers.isEntity)){
            pre:::callNoEntity(comp, ret)
        }
        else{
            val vari = ctx.getFreshVariable(function.getType())
            pre:::callNoEntity(comp, vari) ::: ret.assign(op, LinkedVariableValue(vari))
        }
    }
}


class CompilerFunction(context: Context, name: String, arguments: List[Argument], typ: Type, _modifier: Modifier, val body: (List[Expression], Context)=>(List[IRTree],Expression)) extends Function(context, name, arguments, typ, _modifier){
    generateArgument()(context.push(name, this))
    argumentsVariables.foreach(_.modifiers.isLazy = true)
    override def exists()= false

    override def getContent(): List[IRTree] = List()
    override def getName(): String = name

    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[IRTree] = {
        val call = body(argMap(args2).map((v,e) => Utils.simplify(e)), ctx)
        if (ret != null){
            call._1 ::: ret.assign(op, call._2)
        }
        else{
            call._1
        }
    }

    override def canBeCallAtCompileTime: Boolean = true
}

class GenericFunction(context: Context, name: String, arguments: List[Argument], val generics: List[String], val typ: Type, _modifier: Modifier, val body: Instruction) extends Function(context, name, arguments, typ, _modifier){
    var implemented = mutable.Map[List[Type], Function]() 

    override def exists()= false

    override def getContent(): List[IRTree] = List()
    override def getName(): String = name

    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[IRTree] = {
        get(args2.map(Utils.typeof)).call(args2, ret, op)
    }

    override def generateArgument()(implicit ctx: Context): Unit = {}

    override def canBeCallAtCompileTime: Boolean = false

    def get(typevars: List[Type]) ={
        if (generics.length == 0){
            this
        }else{
            generateForTypes(typevars)
            implemented(typevars)
        }
    }

    def generateForTypes(typevars: List[Type])={
        if (!implemented.contains(typevars)){
            if (typevars.size != generics.size) throw new Exception("Wrong number of type variables")
            
            val hash = scala.util.hashing.MurmurHash3.stringHash(typevars.mkString(","))
            val ctx = context.push(name+"--"+hash, this)
            generics.zip(typevars).foreach(pair => ctx.addTypeDef(pair._1, pair._2))
            
            Compiler.compile(FunctionDecl("impl", body, typ, arguments, List(), modifiers))(ctx)
            val fct = ctx.getFunction("impl")
            implemented(typevars) = fct
        }
    }
}