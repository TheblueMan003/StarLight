package objects.types

import scala.util.parsing.input.Positional
import objects.Context
import objects.Struct

trait Typed(typ: Type){
    def getType(): Type ={
        typ
    }
}


private val outOfBound = 100000
abstract class Type extends Positional {
    def allowAdditionSimplification(): Boolean
    def getDistance(other: Type)(implicit context: Context): Int
    def getName()(implicit context: Context): String
}

object AnyType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = outOfBound
    override def getName()(implicit context: Context): String = "any"
}
object IntType extends Type{
    override def allowAdditionSimplification(): Boolean = true
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case IntType => 0
            case FloatType => 1
            case AnyType => 2
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "int"
}
object FloatType extends Type{
    override def allowAdditionSimplification(): Boolean = true
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case FloatType => 0
            case AnyType => 1
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "float"
}
object StringType extends Type{
    override def allowAdditionSimplification(): Boolean = true
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case StringType => 0            
            case AnyType => 1
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "string"
}
object BoolType extends Type{
    override def allowAdditionSimplification(): Boolean = true
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case BoolType => 0
            case IntType => 1
            case FloatType => 2
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "bool"
}
object VoidType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "void"
}
object EntityType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case EntityType => 0
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "entity"
}

case class TupleType(sub: List[Type]) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case TupleType(sub2) if sub2.length == sub.length => sub.zip(sub2).map((a,b)=>a.getDistance(b)).sum
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"(${sub.map(_.getName()).reduce(_ + ", " + _)})"
}
case class ArrayType(inner: Type, size: String) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case ArrayType(inner2, size2) if inner == inner2 && size == size2 => 0
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"$inner[$size]"
}
case class FuncType(sources: List[Type], output: Type) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case FuncType(sources2, output2) if sources2.length == sources.length => sources.zip(sources2).map((a,b)=>b.getDistance(a)).sum + output.getDistance(output2)
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"(${sources.map(_.getName()).reduce(_ + ", " + _)}) => $output"
}
case class IdentifierType(name: String) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case IdentifierType(sub2) => ???
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = name
}
case class StructType(struct: Struct) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case StructType(sub2) if sub2 == struct => 0
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = struct.fullName
}
case class EnumType(enm: objects.Enum) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case EnumType(sub2) if sub2 == enm => 0
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = enm.fullName
}
case class RangeType(sub: Type) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case RangeType(sub2) => sub.getDistance(sub2)
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"$sub..$sub"
}


case object JsonType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case JsonType => 0
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "json"
}