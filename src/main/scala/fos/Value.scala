package fos

import scala.util.parsing.input.Positional
import objects.Identifier
import objects.Variable
import fos.Compilation.Selector.Selector

sealed abstract class Expression extends Positional{
    def hasIntValue(): Boolean
    def getIntValue(): Int
}

trait SmallValue{
}
case class IntValue(val value: Int) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = value
    override def hasIntValue(): Boolean = true
}
case class FloatValue(val value: Double) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = (value * Settings.floatPrec).toInt
    override def hasIntValue(): Boolean = true
}
case class BoolValue(val value: Boolean) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = if value then 1 else 0
    override def hasIntValue(): Boolean = true
}
case class VariableValue(val name: Identifier) extends Expression with SmallValue{
    override def toString(): String = name.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}
case class LinkedVariableValue(val vari: Variable) extends Expression with SmallValue{
    override def toString(): String = vari.fullName
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}

case class TupleValue(val values: List[Expression]) extends Expression with SmallValue{
    override def toString(): String = "("+values.map(_.toString()).reduce(_ + ", " + _)+")"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}

case class FunctionCallValue(val name: Identifier, val args: List[Expression]) extends Expression with SmallValue{
  override def toString() = f"${name}()"
  override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}
case class RangeValue(val min: Expression, val max: Expression) extends Expression with SmallValue{
    override def toString(): String = f"$min .. $max"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}
case class SelectorValue(val value: Selector) extends Expression{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}



case class BinaryOperation(val op: String, val left: Expression, val right: Expression) extends Expression{
    override def toString(): String = f"($left $op $right)"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}


case class JsonValue(val content: JSONElement) extends Expression{
    override def toString(): String = f"$content"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
}
trait JSONElement{
    def getString(): String
    def getNbt(): String
}

case class JsonDictionary(val map: Map[String, JSONElement]) extends JSONElement{
    def getString(): String = {
        map.map((k, v) => f"{$k:${v.getString()}}").reduceOption(_ +", "+ _).getOrElse("")
    }
    def getNbt(): String = {
        map.map((k, v) => f"{$k:${v.getNbt()}}").reduceOption(_ +", "+ _).getOrElse("")
    }
}
case class JsonArray(val content: List[JSONElement]) extends JSONElement{
    def getString(): String = {
        content.map(v=> f"[${v.getString()}]").reduceOption(_ +", "+ _).getOrElse("")
    }
    def getNbt(): String = {
        content.map(v => f"[${v.getNbt()}]").reduceOption(_ +", "+ _).getOrElse("")
    }
}
case class JsonString(val value: String) extends JSONElement{
    def getString(): String = {
        "\""+value.replaceAll("\\\\","\\\\")+"\""
    }
    def getNbt(): String = {
        "\""+value.replaceAll("\\\\","\\\\")+"\""
    }
}
case class JsonInt(val value: Int) extends JSONElement{
    def getString(): String = {
        value.toString()
    }
    def getNbt(): String = {
        value.toString()
    }
}
case class JsonFloat(val value: Double) extends JSONElement{
    def getString(): String = {
        value.toString()
    }
    def getNbt(): String = {
        value.toString()
    }
}
case class JsonBoolean(val value: Boolean) extends JSONElement{
    def getString(): String = {
        value.toString()
    }
    def getNbt(): String = {
        value.toString()
    }
}