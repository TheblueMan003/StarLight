package sl

import scala.util.parsing.input.Positional
import objects.Identifier
import objects.Variable
import sl.Compilation.Selector.Selector
import objects.Context
import objects.types.*
import sl.Compilation.*
import objects.Function
import sl.Compilation.Printable
import objects.Tag
import scala.quoted.Expr
import sl.IR.IRTree

trait Expression extends CPositionable {
  def hasIntValue(): Boolean
  def getIntValue(): Int
  def hasFloatValue(): Boolean
  def getFloatValue(): Double
  def getString()(implicit context: Context): String
}

trait SmallValue {}
trait Stringifyable {}
case object DefaultValue extends Expression with SmallValue {
  override def toString(): String = "default"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???

  override def getString()(implicit context: Context): String = "default"
}
case object NullValue extends Expression with SmallValue {
  override def toString(): String = "null"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???

  override def getString()(implicit context: Context): String = "null"
}
case class IntValue(val value: Int)
    extends Expression
    with SmallValue
    with Stringifyable {
  override def toString(): String = value.toString()
  override def getIntValue(): Int = value
  override def hasIntValue(): Boolean = true
  override def hasFloatValue(): Boolean = true
  override def getFloatValue(): Double = value

  override def getString()(implicit context: Context): String = f"$value"
}
case class EnumIntValue(val value: Int) extends Expression with SmallValue {
  override def toString(): String = value.toString()
  override def getIntValue(): Int = value
  override def hasIntValue(): Boolean = true
  override def hasFloatValue(): Boolean = true
  override def getFloatValue(): Double = value

  override def getString()(implicit context: Context): String = f"$value"
}
case class FloatValue(val value: Double)
    extends Expression
    with SmallValue
    with Stringifyable {
  override def toString(): String = value.toString()
  override def getIntValue(): Int = (value * Settings.floatPrec).toInt
  override def hasIntValue(): Boolean = true
  override def hasFloatValue(): Boolean = true
  override def getFloatValue(): Double = value
  override def getString()(implicit context: Context): String = f"$value"
}
case class BoolValue(val value: Boolean)
    extends Expression
    with SmallValue
    with Stringifyable {
  override def toString(): String = value.toString()
  override def getIntValue(): Int = if value then 1 else 0
  override def hasIntValue(): Boolean = true
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = f"$value"
}
case class StringValue(val value: String)
    extends Expression
    with SmallValue
    with Stringifyable {
  override def toString(): String = value
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = value
}
case class InterpolatedString(val value: List[Expression])
    extends Expression
    with SmallValue
    with Stringifyable {
  override def toString(): String = value.toString()
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    value.map(_.getString()).reduce(_ + _)
}
object InterpolatedString {
  def build(string: String) = {
    val patern = "\\$\\{([^\\}]+)\\}".r
    var text = string
    var ended = false
    var values = List[Expression]()
    while (!ended) {
      patern.findFirstMatchIn(text) match
        case None => ended = true
        case Some(value) => {
          value.matched
          val expr = value.group(1)
          val befor = value.before.toString()
          values = values ::: (if (befor.size > 0) then
                                 List(
                                   StringValue(befor),
                                   Parser.parseExpression(expr, true)
                                 )
                               else List(Parser.parseExpression(expr, true)))
          text = value.after.toString()
        }
    }
    values match {
      case List(StringValue(s)) => StringValue(s)
      case _                    => InterpolatedString(values)
    }
  }
}
case class RawJsonValue(val value: List[Printable])
    extends Expression
    with SmallValue
    with Stringifyable {
  override def toString(): String = value.toString()
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    val json1 = value.map(_.getString()).reduce(_ + "," + _)
    Settings.target match
      case MCBedrock => f"{\"rawtext\" : [$json1]}"
      case MCJava    => f"[$json1]"
  }
  def length()(implicit ctx: Context): Int = {
    value.map(v => v.getLength()).sum
  }
  def substring(end: Int)(implicit ctx: Context): RawJsonValue = {
    RawJsonValue(
      value
        .map(v => (v.getLength(), v))
        .scanLeft((end, Printable.empty: Printable))((a, b) =>
          (a._1 - b._1, b._2.sub(a._1))
        )
        .map(v => v._2)
    )
  }
  def padLeft(finalSize: Int)(implicit ctx: Context): RawJsonValue = {
    val length = this.length()
    if length >= finalSize then this
    else
      RawJsonValue(
        value ::: List(
          new PrintString(
            " " * (finalSize - length),
            Namecolor("white"),
            TextModdifier(false, false, false, false, false, null)
          )
        )
      )
  }
  def padRight(finalSize: Int)(implicit ctx: Context): RawJsonValue = {
    val length = this.length()
    if length >= finalSize then this
    else
      RawJsonValue(
        List(
          new PrintString(
            " " * (finalSize - length),
            Namecolor("white"),
            TextModdifier(false, false, false, false, false, null)
          )
        ) ::: value
      )
  }
  def padCenter(finalSize: Int)(implicit ctx: Context): RawJsonValue = {
    val length = this.length()
    if length >= finalSize then this
    else {
      val left = (finalSize - length) / 2
      val right = finalSize - length - left
      RawJsonValue(
        List(
          new PrintString(
            " " * left,
            Namecolor("white"),
            TextModdifier(false, false, false, false, false, null)
          )
        ) ::: value ::: List(
          new PrintString(
            " " * right,
            Namecolor("white"),
            TextModdifier(false, false, false, false, false, null)
          )
        )
      )
    }
  }
}
case class NamespacedName(
    val value: String,
    val json: Expression = JsonValue(JsonNull)
) extends Expression
    with SmallValue
    with Stringifyable {
  override def toString(): String = value
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    json match
      case JsonValue(JsonNull)          => value
      case JsonExpression(NullValue, _) => value
      case NullValue                    => value
      case _                            => value + json.getString()
  }
}
case class TagValue(val value: String) extends Expression with SmallValue {
  override def toString(): String = "#" + value
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = "#" + value
}
case class LinkedTagValue(val value: Tag) extends Expression with SmallValue {
  override def toString(): String = "#" + value.fullName
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    "#" + value.fullName
}
case class VariableValue(
    val name: Identifier,
    val selector: Selector = Selector.self
) extends Expression
    with SmallValue {
  override def toString(): String = name.toString()
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    val vari = context.tryGetVariable(name)
    vari match
      case None => {
        try {
          val fct = context.getFunction(name)
          Settings.target.getFunctionName(fct.fullName)
        } catch {
          case _ => name.toString()
        }
      }
      case Some(vari) => {
        if (vari.modifiers.isLazy) {
          vari.lazyValue.getString()
        } else {
          throw new Exception(f"Variable $name cannot be transformed to string")
        }
      }
  }
}
case class ArrayGetValue(val name: Expression, val index: List[Expression])
    extends Expression
    with SmallValue {
  override def toString(): String =
    name.toString() + "[" + index.map(_.toString()).reduce(_ + ", " + _) + "]"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    name.getString() + "[" + index.map(_.getString()).reduce(_ + ", " + _) + "]"
  }
}
case class LinkedVariableValue(
    val vari: Variable,
    val selector: Selector = Selector.self
) extends Expression
    with SmallValue {
  override def toString(): String = vari.fullName
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    if (vari.modifiers.isLazy) {
      vari.lazyValue.getString()
    } else {
      throw new Exception(
        f"Variable ${vari.name} cannot be transformed to string"
      )
    }
  }
}
case class ForSelect(
    val expr: Expression,
    val filter: String,
    val selector: Expression
) extends Expression {
  override def toString(): String = f"$expr for $filter in $selector"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    val expr = Utils.forceString(this.expr)
    val selector = Utils.forceString(this.selector)
    f"$expr for $filter in $selector"
  }
}
case class LinkedFunctionValue(val fct: Function)
    extends Expression
    with SmallValue {
  override def toString(): String = fct.fullName
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    fct.markAsStringUsed()
    Settings.target.getFunctionName(fct.fullName)
  }
}

case class TupleValue(val values: List[Expression])
    extends Expression
    with SmallValue {
  override def toString(): String =
    "(" + values.map(_.toString()).reduce(_ + ", " + _) + ")"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    "(" + values.map(_.getString()).reduce(_ + ", " + _) + ")"
}

case class LambdaValue(
    val args: List[String],
    val instr: Instruction,
    val context: Context
) extends Expression
    with SmallValue {
  override def toString() = f"($args)=>$instr"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = {
    val block = context.getFreshLambda(args, List(), VoidType, instr, false)
    block.markAsStringUsed()
    Settings.target.getFunctionName(block.fullName)
  }
}

case class FunctionCallValue(
    val name: Expression,
    val args: List[Expression],
    val typeargs: List[Type],
    val selector: Selector = Selector.self
) extends Expression
    with SmallValue {
  override def toString() = f"${name}()"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    throw new Exception(
      f"Function call cannot be transformed to string: $name()"
    )
}

case class ConstructorCall(
    val name: Identifier,
    val args: List[Expression],
    val typeArg: List[Type]
) extends Expression {
  override def toString() = f"new ${name}()"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    throw new Exception(
      f"Constructor Function call cannot be transformed to string"
    )
}

case class RangeValue(
    val min: Expression,
    val max: Expression,
    val jump: Expression
) extends Expression
    with SmallValue {
  override def toString(): String = f"$min .. $max by $jump"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    f"${min.getString()}..${max.getString()}"
}
case class DotValue(val left: Expression, val right: Expression)
    extends Expression
    with SmallValue {
  override def toString(): String = f"$left.$right"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = f"$left.$right"
}
case class SequenceValue(val left: Instruction, val right: Expression)
    extends Expression
    with SmallValue {
  override def toString(): String = f"$left;$right"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = f"$left;$right"
}
case class SequencePostValue(val left: Expression, val right: Instruction)
    extends Expression
    with SmallValue {
  override def toString(): String = f"$left;$right"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String = f"$left;$right"
}
case class SelectorValue(val value: Selector) extends Expression {
  override def toString(): String = value.toString()
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    value.getString()
}
case class PositionValue(
    val x: Expression,
    val y: Expression,
    val z: Expression
) extends Expression {
  override def toString(): String = f"$x $y $z"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    val px = Utils.forceString(x)
    val py = Utils.forceString(y)
    val pz = Utils.forceString(z)

    f"${px} ${py} ${pz}"
  def isRanged(): Boolean = {
    List(x, y, z)
      .map(p => {
        p match {
          case RangeValue(_, _, _)                            => true
          case BinaryOperation(op, left, RangeValue(_, _, _)) => true
          case _                                              => false
        }
      })
      .exists(p => p)
  }
  private def getRanged(
      expr: Expression
  )(implicit context: Context): List[Expression] = {
    expr match {
      case rg: RangeValue =>
        Utils
          .getForeachCases("_", rg)
          .flatMap(f => f.filter(_._1 == "_").map(_._2))
          .toList
      case BinaryOperation(op, left, rg: RangeValue) =>
        Utils
          .getForeachCases("_", rg)
          .flatMap(f =>
            f.filter(_._1 == "_").map(g => BinaryOperation(op, left, g._2))
          )
          .toList
      case _ => List(expr)
    }
  }
  def getAllPosition()(implicit context: Context) = {
    val px = getRanged(x)
    val py = getRanged(y)
    val pz = getRanged(z)
    for {
      x <- px
      y <- py
      z <- pz
    } yield PositionValue(x, y, z)
  }
}

case class BinaryOperation(
    val op: String,
    val left: Expression,
    val right: Expression
) extends Expression {
  override def toString(): String = f"($left $op $right)"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    f"(${left.getString()} $op ${right.getString()})"
}

case class TernaryOperation(
    val left: Expression,
    val middle: Expression,
    val right: Expression
) extends Expression {
  override def toString(): String = f"($left ? $middle : $right)"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    f"(${left.getString()} ? ${middle.getString()} : ${right.getString()})"
}

case class UnaryOperation(val op: String, val left: Expression)
    extends Expression {
  override def toString(): String = f"($op $left)"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    f"($op ${left.getString()})"
}

case class IsType(val left: Expression, val right: Type) extends Expression {
  override def toString(): String = f"($left is $right)"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    f"(${left.getString()} is ${right})"
}

case class ClassValue(val left: objects.Class) extends Expression {
  override def toString(): String = f"(class::$left)"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    f"(${left.fullName})"
}

case class CastValue(val left: Expression, val right: Type) extends Expression {
  override def toString(): String = f"(${left} as ${right})"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    f"(${left} as ${right})"
}

case class JsonValue(val content: JSONElement)
    extends Expression
    with Stringifyable {
  override def toString(): String = f"$content"
  override def getIntValue(): Int = ???
  override def hasIntValue(): Boolean = false
  override def hasFloatValue(): Boolean = false
  override def getFloatValue(): Double = ???
  override def getString()(implicit context: Context): String =
    content match
      case JsonString(s) => s
      case _             => content.getString()
}
trait JSONElement {
  def getString()(implicit context: Context): String
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement)
  def getNbt(): String
  def getStringValue: String = throw new Exception(f"$this is not a string")
  def getIntValue: Int = throw new Exception(f"$this is not an int")
  def getFloatValue: Double = throw new Exception(f"$this is not a float")
  def getBooleanValue: Boolean = throw new Exception(f"$this is not a bool")
  def getDictionary: JsonDictionary = throw new Exception(f"$this is not a dic")
  def getArray: JsonArray = throw new Exception(f"$this is not an array")
}

case class JsonDictionary(val map: Map[String, JSONElement])
    extends JSONElement {
  def getString()(implicit context: Context): String = {
    "{" + map
      .map((k, v) => f"${Utils.stringify(k)}:${v.getString()}")
      .reduceOption(_ + ", " + _)
      .getOrElse("") + "}"
  }
  def getNbt(): String = {
    "{" + map
      .map((k, v) => f"${k}:${v.getNbt()}")
      .reduceOption(_ + ", " + _)
      .getOrElse("") + "}"
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    val m = map.map((k, v) => (k, v.forceNBT()(vari, stack + "." + k, context)))
    (m.flatMap(_._2._1).toList, JsonDictionary(m.map((k, v) => (k, v._2))))
  }
  def apply(key: String): JSONElement = map(key)
  override def getDictionary: JsonDictionary = this
  def contains(key: String): Boolean = map.contains(key)
}
case class JsonArray(val content: List[JSONElement]) extends JSONElement {
  def getString()(implicit context: Context): String = {
    "[" + content
      .map(v => f"${v.getString()}")
      .reduceOption(_ + ", " + _)
      .getOrElse("") + "]"
  }
  def getNbt(): String = {
    "[" + content
      .map(v => f"${v.getNbt()}")
      .reduceOption(_ + ", " + _)
      .getOrElse("") + "]"
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    val m = content.zipWithIndex.map((v, k) =>
      v.forceNBT()(vari, stack + "." + k, context)
    )
    (m.flatMap(_._1).toList, JsonArray(m.map(_._2)))
  }
  def apply(key: Int): JSONElement = if (key >= 0) { content(key) }
  else { content(content.length + key) }
  override def getArray: JsonArray = this
}
case class JsonString(val value: String) extends JSONElement {
  def getString()(implicit context: Context): String = {
    Utils.stringify(value)
  }
  def getNbt(): String = {
    Utils.stringify(value)
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    (List(), this)
  }
  override def getStringValue: String = value
}
case class JsonIdentifier(val value: String, val typ: String)
    extends JSONElement {
  def getString()(implicit context: Context): String = {
    Utils.stringify(value)
  }
  def getNbt(): String = {
    Utils.stringify(value)
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    (List(), this)
  }
}
case object JsonNull extends JSONElement {
  def getString()(implicit context: Context): String = {
    "null"
  }
  def getNbt(): String = {
    "null"
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    (List(), this)
  }
  override def getStringValue: String = null
}
case class JsonInt(val value: Int, val typ: String) extends JSONElement {
  def getString()(implicit context: Context): String = {
    value.toString()
  }
  def getNbt(): String = {
    if (typ != null) {
      value.toString() + typ
    } else {
      value.toString()
    }
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    (List(), this)
  }
  override def getIntValue: Int = value
}
case class JsonFloat(val value: Double, val typ: String) extends JSONElement {
  def getString()(implicit context: Context): String = {
    value.toString()
  }
  def getNbt(): String = {
    if (typ != null) {
      value.toString() + typ
    } else {
      value.toString()
    }
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    (List(), this)
  }
  override def getFloatValue: Double = value
}
case class JsonBoolean(val value: Boolean) extends JSONElement {
  def getString()(implicit context: Context): String = {
    value.toString()
  }
  def getNbt(): String = {
    if value then "1b" else "0b"
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    (List(), this)
  }
  override def getBooleanValue: Boolean = value
}

case class JsonExpression(val value: Expression, val typ: String)
    extends JSONElement {
  def getString()(implicit context: Context): String = {
    Utils.compileJson(this).getString()
  }
  def forceNBT()(implicit
      vari: Variable,
      stack: String,
      context: Context
  ): (List[IRTree], JSONElement) = {
    (vari.withKey(stack).assign("=", value), JsonNull)
  }
  def getNbt(): String = {
    throw new Exception("Illegal cast!")
  }
}
