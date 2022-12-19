package objects

import types.*
import sl.*
import sl.Utils
import scala.collection.mutable
import sl.Compilation.Selector.*
import sl.Compilation.Print

object Function {
  extension (str: (Function, List[Expression])) {
    def call(ret: Variable = null, op: String = "=")(implicit context: Context) = {
        if (str._1.hasRawJsonArg()){
            val prefix = str._2.take(str._1.arguments.length - 1)
            val sufix = str._2.drop(str._1.arguments.length - 1)
            val (prep, json) = Print.toRawJson(sufix)
            prep ::: str._1.call(prefix ::: List(json), ret, op)
        }
        else{
            str._1.call(str._2, ret, op)
        }
    }
  }
}
abstract class Function(context: Context, name: String, val arguments: List[Argument], typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
    val parentFunction = context.getCurrentFunction()
    val parentVariable = context.getCurrentVariable()
    val parentClass = context.getCurrentClass()
    override lazy val fullName: String = if (context.isInLazyCall() || parentFunction != null || parentVariable != null) then context.fctCtx.getFreshId()else context.getPath() + "." + name
    val minArgCount = getMinArgCount(arguments)
    val maxArgCount = getMaxArgCount(arguments)
    var argumentsVariables: List[Variable] = List()
    val clazz = context.getCurrentClass()

    if (modifiers.isVirtual){

    }
    if (modifiers.isConst){
        throw new Exception("Function cannot be marked as const")
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

    def hasRawJsonArg(): Boolean = {
        if arguments.length == 0 then false else
        arguments.last.typ match
            case RawJsonType => true
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
    def call(args: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[String]
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
    def getContent(): List[String]
}

class ConcreteFunction(context: Context, name: String, arguments: List[Argument], typ: Type, _modifier: Modifier, val body: Instruction, topLevel: Boolean) extends Function(context, name, arguments, typ, _modifier){
    private var _needCompiling = topLevel || Settings.allFunction
    private var wasCompiled = false
    
    protected var content = List[String]()
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
    
    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[String] = {
        markAsUsed()
        val r = argMap(args2).flatMap(p => p._1.assign("=", p._2)) :::
            List("function " + Settings.target.getFunctionName(fullName))

        if (ret != null){
            r ::: ret.assign(op, LinkedVariableValue(returnVariable))
        }
        else{
            r
        }
    }

    def compile():Unit={
        val suf = sl.Compiler.compile(body)(context.push(name, this))
        content = content ::: suf
    }

    def addMuxCleanUp(cnt: List[String]): Unit = {
        content = cnt ::: content
    }
    def append(cnt: List[String]): Unit = {
        content = content ::: cnt
    }

    def exists(): Boolean = wasCompiled || Settings.allFunction
    def getContent(): List[String] = content
    def getName(): String = Settings.target.getFunctionPath(fullName)

    def needCompiling():Boolean = {
        _needCompiling && !wasCompiled
    }
    def markAsCompile(): Unit = {
        wasCompiled = true
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

class BlockFunction(context: Context, name: String, arguments: List[Argument], var body: List[String]) extends Function(context, name, arguments, VoidType, Modifier.newPrivate()){
    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[String] = {
        argMap(args2).flatMap(p => p._1.assign("=", p._2)) :::
            List("function " + fullName.replaceAllLiterally(".","/"))
    }

    def exists(): Boolean = true
    def getContent(): List[String] = body
    def getName(): String = Settings.target.getFunctionPath(fullName)

    def append(cnt: List[String]): Unit = {
        body = body ::: cnt
    }
}

class LazyFunction(context: Context, name: String, arguments: List[Argument], typ: Type, _modifier: Modifier, val body: Instruction) extends Function(context, name, arguments, typ, _modifier){
    def call(args: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[String] = {
        var block = body
        val sub = ctx.push(ctx.getLazyCallId())
        sub.setLazyCall()
        sub.inherit(context)
        
        argMap(args).sortBy((a,v) => -a.name.length).foreach((a, v) => {
            val vari = Variable(sub, a.name, a.getType(), a.modifiers)
            vari.generate()(sub)
            sub.addVariable(vari).assign("=", v)
            block = if a.name.startsWith("$") then Utils.subst(block, a.name, v.getString()) else Utils.subst(block, a.name, v)
        })

        if (ret != null) sub.addVariable("_ret", ret)
        if (ret == null){
            sl.Compiler.compile(block)(if modifiers.hasAttributes("inline") then ctx else sub)
        }
        else if (op == "="){
            block = Utils.substReturn(block, ret)
            sl.Compiler.compile(block)(if modifiers.hasAttributes("inline") then ctx else sub)
        }
        else{
            val vari = ctx.getFreshVariable(getType())
            block = Utils.substReturn(block, vari)
            sl.Compiler.compile(block)(if modifiers.hasAttributes("inline") then ctx else sub) ::: (if ret == null then List() else ret.assign(op, LinkedVariableValue(vari)))
        }
    }
    override def generateArgument()(implicit ctx: Context):Unit = {
        super.generateArgument()
        argumentsVariables.foreach(_.modifiers.isLazy = true)
    }

    def exists(): Boolean = false
    def getContent(): List[String] = List()
    def getName(): String = Settings.target.getFunctionPath(fullName)
}

class MultiplexFunction(context: Context, name: String, arguments: List[Argument], typ: Type) extends ConcreteFunction(context, name, arguments, typ, objects.Modifier.newPrivate(), sl.InstructionList(List()), false){
    private val functions = mutable.ListBuffer[ConcreteFunction]()
    override def needCompiling():Boolean = {
        false
    }
    override def markAsCompile(): Unit = {
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
    override def markAsCompile(): Unit = {
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

    override def getContent(): List[String] = List()
    override def getName(): String = function.name

    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[String] = {
        val selector = SelectorValue(JavaSelector("@e", List(("tag", SelectorIdentifier("__class__")))))

        def callNoEntity(ret: Variable = null) = {
            Compiler.compile(With(
                selector, 
                BoolValue(true), 
                BinaryOperation("==", LinkedVariableValue(variable), LinkedVariableValue(ctx.root.push("object").getVariable("__ref"))),
                LinkedFunctionCall(function, args2, ret)
                ))
        }

        if (ret == null || !ret.modifiers.isEntity){
            callNoEntity(ret)
        }
        else{
            val vari = ctx.getFreshVariable(function.getType())
            callNoEntity(vari) ::: ret.assign(op, LinkedVariableValue(vari))
        }
    }
}


class CompilerFunction(context: Context, name: String, arguments: List[Argument], typ: Type, _modifier: Modifier, val body: (List[Expression], Context)=>(List[String],Expression)) extends Function(context, name, arguments, typ, _modifier){
    generateArgument()(context.push(name, this))
    override def exists()= false

    override def getContent(): List[String] = List()
    override def getName(): String = name

    def call(args2: List[Expression], ret: Variable = null, op: String = "=")(implicit ctx: Context): List[String] = {
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