package objects

import scala.collection.mutable
import types.*
import sl.{Instruction, InstructionList}
import sl.JSONElement
import sl.Argument
import sl.Expression
import sl.Utils
import sl.Settings
import sl.IR.*

class Predicate(
    context: Context,
    name: String,
    val arguments: List[Argument],
    _modifier: Modifier,
    val block: JSONElement
) extends CObject(context, name, _modifier) {
  val minArgCount = getMinArgCount(arguments)

  private val predicates = mutable.Map[List[Expression], String]()
  private val files = mutable.Map[String, List[JsonIR]]()
  private var counter = 0
  var argumentsVariables: List[Variable] = List()

  private def getMinArgCount(
      args: List[Argument],
      stopped: Boolean = false
  ): Int = {
    args match {
      case head :: tail => {
        head.defValue match{
          case None =>
            if (stopped) {
              throw new Exception(
                f"${head.name} must have a default value in ${fullName}"
              )
            } else {
              getMinArgCount(tail, false) + 1
            }
          case Some(value) => {
            getMinArgCount(tail, true)
          }
        }
      }
      case Nil => 0
    }
  }
  def argMap(args: List[Expression]) = {
    argumentsVariables
      .filter(_.getType() != VoidType)
      .zip(arguments.map(_.defValue))
      .zipAll(args, null, null)
      .map(p => (p._1._1, if (p._2 == null) p._1._2.get else p._2))
  }
  def argMap2(args: List[Expression]) = {
    arguments
      .map(_.defValue)
      .zipAll(args, null, null)
      .map(p => (if (p._2 == null) p._1.get else p._2))
  }
  def generateArgument()(implicit ctx: Context): Unit = {
    val ctx2 = ctx.push(name)
    argumentsVariables = arguments.map(a => {
      val mod = new Modifier()
      mod.protection = Protection.Private
      mod.isLazy = true
      val vari = new Variable(ctx2, a.name, ctx.getType(a.typ), mod)

      ctx2.addVariable(vari)
      vari.generate(ctx.getCurrentVariable() != null)(ctx2)
      vari
    })
  }
  def getFiles(): List[(String, List[JsonIR])] = {
    files.toList
  }
  def call(args: List[Expression])(implicit ctx: Context): String = {
    val uargs = argMap2(args.map(Utils.simplify(_)(ctx)))
    if (predicates.contains(uargs)) return predicates(uargs)
    val va = argMap(args).map((v, e) => v.assign("=", e)(ctx))
    val compiled = Utils.compileJson(block)(context.push(name))
    val pname = context.getPath() + "." + name + "." + counter
    files.put(
      Settings.target.getPredicatePath(pname),
      List(JsonIR(compiled.getString()(context.push(name))))
    )
    predicates.put(uargs, Settings.target.getFunctionName(pname))
    counter += 1
    Settings.target.getFunctionName(pname)
  }

  def getIRFiles(): List[IRFile] = {
    files
      .map((name, content) => IRFile(name, name, content, List(), true))
      .toList
  }
}
