package objects

import types.*
import sl.{Instruction, InstructionList, Utils}
import sl.Expression
import sl.TemplateArgument

class Template(
    context: Context,
    name: String,
    _modifier: Modifier,
    val block: Instruction,
    val parentName: Identifier,
    val generics: List[TemplateArgument],
    val parentGenerics: List[Expression]
) extends CObject(context, name, _modifier) {
  lazy val parent =
    if (parentName == null) null else context.getTemplate(parentName)

  private def getMinArgCount(
      args: List[TemplateArgument],
      stopped: Boolean = false
  ): Int = {
    args match {
      case head :: tail => {
        head.defValue match
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
      case Nil => 0
    }
  }

  def getSelfBlock(values: List[Expression]) = {
    val min = getMinArgCount(generics)
    if (values.size < min || values.size > generics.size) {
      throw new Exception("Wrong number of arguments")
    }
    generics
      .zipAll(values, null, null)
      .foldLeft(Utils.fix(block.unBlockify())(context, Set()))((b, v) =>
        Utils.subst(
          b,
          v._1.name,
          if (v._2 == null) then v._1.defValue.get else v._2
        )
      )
  }

  def getBlock(values: List[Expression]): Instruction = {
    if (parent != null) {
      var p = parentGenerics.map(g =>
        generics
          .zip(values)
          .foldLeft(Utils.fix(g)(context, Set()))((b, v) =>
            Utils.subst(
              b,
              v._1.name,
              if (v._2 == null) then v._1.defValue.get else v._2
            )
          )
      )
      InstructionList(List(parent.getBlock(p), getSelfBlock(values)))
    } else {
      getSelfBlock(values)
    }
  }
  def getContext(): Context = {
    val sub = context.push(name)
    if (parent != null) {
      sub.inherit(parent.getContext())
    }
    sub
  }

}
