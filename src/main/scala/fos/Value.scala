package fos

import scala.util.parsing.input.Positional
import objects.Identifier
import objects.Variable
import fos.Compilation.Selector.Selector
import objects.Context

sealed abstract class Expression extends Positional{
    def hasIntValue(): Boolean
    def getIntValue(): Int
    def hasFloatValue(): Boolean
    def getFloatValue(): Double
}

trait SmallValue{
}
case object DefaultValue extends Expression with SmallValue{
    override def toString(): String = "default"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}
case class IntValue(val value: Int) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = value
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = true
    override def getFloatValue(): Double = value
}
case class FloatValue(val value: Double) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = (value * Settings.floatPrec).toInt
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = true
    override def getFloatValue(): Double = value
}
case class BoolValue(val value: Boolean) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = if value then 1 else 0
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}
case class StringValue(val value: String) extends Expression with SmallValue{
    override def toString(): String = value
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}
case class VariableValue(val name: Identifier) extends Expression with SmallValue{
    override def toString(): String = name.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}
case class LinkedVariableValue(val vari: Variable) extends Expression with SmallValue{
    override def toString(): String = vari.fullName
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}

case class TupleValue(val values: List[Expression]) extends Expression with SmallValue{
    override def toString(): String = "("+values.map(_.toString()).reduce(_ + ", " + _)+")"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}

case class FunctionCallValue(val name: Identifier, val args: List[Expression]) extends Expression with SmallValue{
    override def toString() = f"${name}()"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}
case class RangeValue(val min: Expression, val max: Expression) extends Expression with SmallValue{
    override def toString(): String = f"$min .. $max"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}
case class SelectorValue(val value: Selector) extends Expression{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}



case class BinaryOperation(val op: String, val left: Expression, val right: Expression) extends Expression{
    override def toString(): String = f"($left $op $right)"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}


case class JsonValue(val content: JSONElement) extends Expression{
    override def toString(): String = f"$content"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
}
trait JSONElement{
    def getString()(implicit context: Context): String
    def getNbt(): String
}

case class JsonDictionary(val map: Map[String, JSONElement]) extends JSONElement{
    def getString()(implicit context: Context): String = {
        map.map((k, v) => f"{${Utils.stringify(k)}:${v.getString()}}").reduceOption(_ +", "+ _).getOrElse("")
    }
    def getNbt(): String = {
        map.map((k, v) => f"{${Utils.stringify(k)}:${v.getNbt()}}").reduceOption(_ +", "+ _).getOrElse("")
    }
}
case class JsonArray(val content: List[JSONElement]) extends JSONElement{
    def getString()(implicit context: Context): String = {
        content.map(v=> f"[${v.getString()}]").reduceOption(_ +", "+ _).getOrElse("")
    }
    def getNbt(): String = {
        content.map(v => f"[${v.getNbt()}]").reduceOption(_ +", "+ _).getOrElse("")
    }
}
case class JsonString(val value: String) extends JSONElement{
    def getString()(implicit context: Context): String = {
        Utils.stringify(value)
    }
    def getNbt(): String = {
        Utils.stringify(value)
    }
}
case class JsonIdentifier(val value: String) extends JSONElement{
    def getString()(implicit context: Context): String = {
        Utils.stringify(value)
    }
    def getNbt(): String = {
        Utils.stringify(value)
    }
}
case class JsonCall(val value: String, val args: List[Expression]) extends JSONElement{
    def getString()(implicit context: Context): String = {
        Utils.stringify(value)
    }
    def getNbt(): String = {
        Utils.stringify(value)
    }
}
case class JsonInt(val value: Int) extends JSONElement{
    def getString()(implicit context: Context): String = {
        value.toString()
    }
    def getNbt(): String = {
        value.toString()
    }
}
case class JsonFloat(val value: Double) extends JSONElement{
    def getString()(implicit context: Context): String = {
        value.toString()
    }
    def getNbt(): String = {
        value.toString()
    }
}
case class JsonBoolean(val value: Boolean) extends JSONElement{
    def getString()(implicit context: Context): String = {
        value.toString()
    }
    def getNbt(): String = {
        value.toString()
    }
}