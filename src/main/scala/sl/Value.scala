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

trait Expression extends Positional{
    def hasIntValue(): Boolean
    def getIntValue(): Int
    def hasFloatValue(): Boolean
    def getFloatValue(): Double
    def getString()(implicit context: Context): String
}

trait SmallValue{
}
trait Stringifyable{
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
case class IntValue(val value: Int) extends Expression with SmallValue with Stringifyable{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = value
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = true
    override def getFloatValue(): Double = value

    override def getString()(implicit context: Context): String = f"$value"
}
case class EnumIntValue(val value: Int) extends Expression with SmallValue{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = value
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = true
    override def getFloatValue(): Double = value

    override def getString()(implicit context: Context): String = f"$value"
}
case class FloatValue(val value: Double) extends Expression with SmallValue with Stringifyable{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = (value * Settings.floatPrec).toInt
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = true
    override def getFloatValue(): Double = value
    override def getString()(implicit context: Context): String = f"$value"
}
case class BoolValue(val value: Boolean) extends Expression with SmallValue with Stringifyable{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = if value then 1 else 0
    override def hasIntValue(): Boolean = true
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = f"$value"
}
case class StringValue(val value: String) extends Expression with SmallValue with Stringifyable{
    override def toString(): String = value
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = value
}
case class RawJsonValue(val value: List[Printable]) extends Expression with SmallValue with Stringifyable{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = {
        val json1 = value.map(_.getString()).reduce(_ + "," + _)
        Settings.target match
            case MCBedrock => f"{\"rawtext\" : [$json1]}"
            case MCJava => f"[$json1]"
    }
    def length()(implicit ctx: Context): Int = {
        value.map(v => v.getLength()).sum
    }
    def substring(end: Int)(implicit ctx: Context):RawJsonValue = {
        RawJsonValue(
            value.map(v => (v.getLength(), v))
                .scanLeft((end, Printable.empty: Printable))((a, b) => (a._1 - b._1, b._2.sub(a._1)))
                .map(v => v._2)
            )
    }
    def padLeft(finalSize: Int)(implicit ctx: Context): RawJsonValue = {
        val length = this.length()
        if length >= finalSize then this
        else RawJsonValue(value ::: List(new PrintString(" " * (finalSize - length), Namecolor("white"), TextModdifier(false, false, false, false, false))))
    }
    def padRight(finalSize: Int)(implicit ctx: Context): RawJsonValue = {
        val length = this.length()
        if length >= finalSize then this
        else RawJsonValue(List(new PrintString(" " * (finalSize - length), Namecolor("white"), TextModdifier(false, false, false, false, false))) ::: value)
    }
    def padCenter(finalSize: Int)(implicit ctx: Context): RawJsonValue = {
        val length = this.length()
        if length >= finalSize then this
        else {
            val left = (finalSize - length) / 2
            val right = finalSize - length - left
            RawJsonValue(List(new PrintString(" " * left, Namecolor("white"), TextModdifier(false, false, false, false, false))) ::: value ::: List(new PrintString(" " * right, Namecolor("white"), TextModdifier(false, false, false, false, false))))
        }
    }
}
case class NamespacedName(val value: String) extends Expression with SmallValue with Stringifyable{
    override def toString(): String = value
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = value
}
case class TagValue(val value: String) extends Expression with SmallValue{
    override def toString(): String = "#"+value
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = "#"+value
}
case class VariableValue(val name: Identifier, val selector: Selector = Selector.self) extends Expression with SmallValue{
    override def toString(): String = name.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String ={
        val vari = context.tryGetVariable(name)
        vari match
            case None => {
                try{
                    val fct = context.getFunction(name)
                    Settings.target.getFunctionName(fct.fullName)
                }
                catch{
                    case _ => name.toString()
                }
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
case class ArrayGetValue(val name: Expression, val index: List[Expression]) extends Expression with SmallValue{
    override def toString(): String = name.toString() + "[" + index.map(_.toString()).reduce(_ +", "+_) + "]"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String ={
        name.getString() + "[" + index.map(_.getString()).reduce(_ +", "+_) + "]"
    }
}
case class LinkedVariableValue(val vari: Variable, val selector: Selector = Selector.self) extends Expression with SmallValue{
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
case class LinkedFunctionValue(val fct: Function) extends Expression with SmallValue{
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
        block.markAsStringUsed()
        Settings.target.getFunctionName(block.fullName)
    }
}

case class FunctionCallValue(val name: Expression, val args: List[Expression], val typeargs: List[Type], val selector: Selector = Selector.self) extends Expression with SmallValue{
    override def toString() = f"${name}()"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = throw new Exception(f"Function call cannot be transformed to string: $name()")
}

case class ConstructorCall(val name: Identifier, val args: List[Expression], val typeArg: List[Type]) extends Expression{
    override def toString() = f"new ${name}()"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = throw new Exception(f"Constructor Function call cannot be transformed to string")
}

case class RangeValue(val min: Expression, val max: Expression, val jump: Expression) extends Expression with SmallValue{
    override def toString(): String = f"$min .. $max by $jump"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = f"${min.getString()}..${max.getString()}"
}
case class DotValue(val left: Expression, val right: Expression) extends Expression with SmallValue{
    override def toString(): String = f"$left.$right"
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = f"$left.$right"
}
case class SelectorValue(val value: Selector) extends Expression{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = value.getString()
}
case class PositionValue(val value: String) extends Expression{
    override def toString(): String = value.toString()
    override def getIntValue(): Int = ???
    override def hasIntValue(): Boolean = false
    override def hasFloatValue(): Boolean = false
    override def getFloatValue(): Double = ???
    override def getString()(implicit context: Context): String = value
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
        "{"+map.map((k, v) => f"${Utils.stringify(k)}:${v.getString()}").reduceOption(_ +", "+ _).getOrElse("")+"}"
    }
    def getNbt(): String = {
        "{"+map.map((k, v) => f"${k}:${v.getNbt()}").reduceOption(_ +", "+ _).getOrElse("")+"}"
    }
}
case class JsonArray(val content: List[JSONElement]) extends JSONElement{
    def getString()(implicit context: Context): String = {
        "["+content.map(v=> f"${v.getString()}").reduceOption(_ +", "+ _).getOrElse("")+"]"
    }
    def getNbt(): String = {
        "["+content.map(v => f"${v.getNbt()}").reduceOption(_ +", "+ _).getOrElse("")+"]"
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
case class JsonCall(val value: String, val args: List[Expression], val typeargs: List[Type]) extends JSONElement{
    def getString()(implicit context: Context): String = {
        Utils.stringify(value)
    }
    def getNbt(): String = {
        Utils.stringify(value)
    }
}
case object JsonNull extends JSONElement{
    def getString()(implicit context: Context): String = {
        "null"
    }
    def getNbt(): String = {
        "null"
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