package fos

import scala.util.parsing.input.Positional
import objects.Identifier
import objects.Variable
import fos.Compilation.Selector.Selector
import objects.Context
import objects.types.*

sealed abstract class Expression extends Positional{
    def hasIntValue(): Boolean
    def getIntValue(): Int
    def hasFloatValue(): Boolean
    def getFloatValue(): Double
    def getString()(implicit context: Context): String
}

trait SmallValue{
}
case object DefaultValue extends Expression with SmallValue{
    override def toString(): String = "default"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???

    override def getString()(implicit context: Context): String = "default"
}
case object NullValue extends Expression with SmallValue{
    override def toString(): String = "null"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???

    override def getString()(implicit context: Context): String = "null"
}
case class IntValue(val value: Int) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = value
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = true
    override def getFloatValue(): Double = value

    override def getString()(implicit context: Context): String = f"$value"
}
case class FloatValue(val value: Double) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = (value * Settings.floatPrec).toInt
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = true
    override def getFloatValue(): Double = value
    override def getString()(implicit context: Context): String = f"$value"
}
case class BoolValue(val value: Boolean) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = if value then 1 else 0
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = f"$value"
}
case class StringValue(val value: String) extends Expression with SmallValue{
    override def toString(): String = value
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = Utils.stringify(value)
}
case class NamespacedName(val value: String) extends Expression with SmallValue{
    override def toString(): String = value
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = value
}
case class VariableValue(val name: Identifier) extends Expression with SmallValue{
    override def toString(): String = name.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String ={
        val vari = context.tryGetVariable(name)
        vari match
            case None => {
                val fct = context.getFunction(name)
                Settings.target.getFunctionName(fct.fullName)
            }
            case Some(vari) => {
                if (vari.modifiers.isLazy){
                    vari.lazyValue.getString()
                }
                else{
                    throw new Exception(f"Variable $name cannot be transformed to string")
                }
            }
    }
}
case class ArrayGetValue(val name: Expression, val index: Expression) extends Expression with SmallValue{
    override def toString(): String = name.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String ={
        name.getString() + "[" + index.getString() + "]"
    }
}
case class LinkedVariableValue(val vari: Variable) extends Expression with SmallValue{
    override def toString(): String = vari.fullName
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = {
        if (vari.modifiers.isLazy){
            vari.lazyValue.getString()
        }
        else{
            throw new Exception(f"Variable ${vari.name} cannot be transformed to string")
        }
    }
}

case class TupleValue(val values: List[Expression]) extends Expression with SmallValue{
    override def toString(): String = "("+values.map(_.toString()).reduce(_ + ", " + _)+")"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = "("+values.map(_.getString()).reduce(_ + ", " + _)+")"
}

case class LambdaValue(val args: List[String], val instr: Instruction) extends Expression with SmallValue{
    override def toString() = f"($args)=>$instr"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = {
        val block = context.getFreshLambda(args, List(), VoidType, instr, false)
        Settings.target.getFunctionName(block.fullName)
    }
}

case class FunctionCallValue(val name: Expression, val args: List[Expression]) extends Expression with SmallValue{
    override def toString() = f"${name}()"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = throw new Exception(f"Function call cannot be transformed to string")
}
case class RangeValue(val min: Expression, val max: Expression) extends Expression with SmallValue{
    override def toString(): String = f"$min .. $max"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = f"${min.getString()}..${max.getString()}"
}
case class SelectorValue(val value: Selector) extends Expression{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = value.getString()
}



case class BinaryOperation(val op: String, val left: Expression, val right: Expression) extends Expression{
    override def toString(): String = f"($left $op $right)"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = f"(${left.getString()} $op ${right.getString()})"
}


case class UnaryOperation(val op: String, val left: Expression) extends Expression{
    override def toString(): String = f"($op $left)"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = f"($op ${left.getString()})"
}


case class JsonValue(val content: JSONElement) extends Expression{
    override def toString(): String = f"$content"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = content.getString()
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
        if value then "1b" else "0b"
    }
}