package objects

import types.*
import fos.*
import fos.Utils
import scala.collection.mutable

object Function {
  extension (str: (Function, List[Expression])) {
    def call()(implicit context: Context) = {
      str._1.call(str._2)
    }
    def call(ret: Variable)(implicit context: Context) = {
      str._1.call(str._2, ret)
    }
  }
}
abstract class Function(context: Context, name: String, val arguments: List[Argument], typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
    val minArgCount = getMinArgCount(arguments)
    var argumentsVariables: List[Variable] = List()

    private def getMinArgCount(args: List[Argument], stopped: Boolean = false): Int = {
        args match{
            case head::tail => {
                head.defValue match
                    case None => getMinArgCount(tail, true)
                    case Some(value) => {
                        if (stopped){
                            throw new Exception(f"${head.name} must have a default value in ${fullName}")
                        }
                        else{
                            getMinArgCount(tail, true) + 1
                        }
                    }
            }
            case Nil => 0
        }
    }
    def call(args: List[Expression], ret: Variable = null)(implicit ctx: Context): List[String]
    def generateArgument()(implicit ctx: Context):Unit = {
        val ctx2 = ctx.push(name)
        argumentsVariables = arguments.map(a => {
            val mod = new Modifier()
            mod.protection = Protection.Private
            val vari = new Variable(ctx2, a.name, a.typ, mod)

            ctx2.addVariable(vari)
            vari.generate()(ctx2)
            vari
    })
    }
    def argMap(args: List[Expression]) = {
        argumentsVariables
                 .zip(arguments.map(_.defValue))
                 .zipAll(args, null, null)
                 .map(p => (p._1._1, if p._2 == null then p._1._2.get else p._2))
    }
    def exists(): Boolean
    def getName(): String
    def getContent(): List[String]
}

class ConcreteFunction(context: Context, name: String, arguments: List[Argument], typ: Type, _modifier: Modifier, val body: Instruction, topLevel: Boolean) extends Function(context, name, arguments, typ, _modifier){
    private var _needCompiling = topLevel
    private var wasCompiled = false
    protected var content = List[String]()
    val returnVariable = {
        val ctx = context.push(name)
        ctx.addVariable(new Variable(ctx, "_ret", typ, Modifier.newPrivate()))
    }
    private lazy val muxID = context.getFunctionMuxID(this)

    if (needCompiling()) {
        context.addFunctionToCompile(this)
    }
    
    def call(args2: List[Expression], ret: Variable = null)(implicit ctx: Context): List[String] = {
        _needCompiling = true
        if (needCompiling()) {
            context.addFunctionToCompile(this)
        }
        val r = argMap(args2).flatMap(p => p._1.assign("=", p._2)) :::
            List("function " + fullName.replaceAllLiterally(".","/"))

        if (ret != null){
            r ::: ret.assign("=", LinkedVariableValue(returnVariable))
        }
        else{
            r
        }
    }

    def compile():Unit={
        val suf = fos.Compiler.compile(body)(context.push(name, this))
        content = content ::: suf
    }

    def addMuxCleanUp(cnt: List[String]): Unit = {
        content = cnt ::: content
    }
    def append(cnt: List[String]): Unit = {
        content = content ::: cnt
    }

    def exists(): Boolean = wasCompiled
    def getContent(): List[String] = content
    def getName(): String = Settings.target.getFunctionPath(fullName)

    def needCompiling():Boolean = {
        _needCompiling && !wasCompiled
    }
    def markAsCompile(): Unit = {
        wasCompiled = true
    }

    def getMuxID(): Int = {
        muxID
    }
}

class BlockFunction(context: Context, name: String, var body: List[String]) extends Function(context, name, List(), VoidType, Modifier.newPrivate()){
    def call(args2: List[Expression], ret: Variable = null)(implicit ctx: Context): List[String] = {
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
    def call(args: List[Expression], ret: Variable = null)(implicit ctx: Context): List[String] = {
        var block = body
        argMap(args).foreach((a, v) => {
            block = Utils.substReturn(Utils.subst(block, a.name, v), ret)
        })
        fos.Compiler.compile(block)
    }
    override def generateArgument()(implicit ctx: Context):Unit = {
    }

    def exists(): Boolean = false
    def getContent(): List[String] = List()
    def getName(): String = Settings.target.getFunctionPath(fullName)
}

class MultiplexFunction(context: Context, name: String, arguments: List[Argument], typ: Type) extends ConcreteFunction(context, name, arguments, typ, objects.Modifier.newPrivate(), fos.InstructionList(List()), false){
    private val functions = mutable.Set[ConcreteFunction]()
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
                functions.map(x => SwitchCase(IntValue(x.getMuxID()), LinkedFunctionCall(x, argumentsVariables.tail.map(LinkedVariableValue(_))))).toList
            else
                functions.map(x => SwitchCase(IntValue(x.getMuxID()), LinkedFunctionCall(x, argumentsVariables.tail.map(LinkedVariableValue(_)), returnVariable))).toList
        
        val switch = Switch(LinkedVariableValue(argumentsVariables.head), cases, false)
        content = fos.Compiler.compile(switch)(context.push(name, this))
    }
}