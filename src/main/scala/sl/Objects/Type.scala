package objects.types

import scala.util.parsing.input.Positional
import objects.Context
import objects.{Struct, Class, Variable, CompilerFunction}
import sl.Expression
import sl.*
import objects.Modifier

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

    def generateCompilerFunction(variable: Variable)(implicit context: Context) = {
        if (variable.modifiers.isLazy){
            context.addFunction("toString", CompilerFunction(context, "toString", 
                List(),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression]) => {
                    args match{
                        case Nil => (List(), StringValue(variable.lazyValue.toString()))
                        case other => throw new Exception(f"Illegal Arguments $other for toString")
                    }
                }
            ))
        }
    }
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
            case MCObjectType => 10
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "int"
}
object MCObjectType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case MCObjectType => 0
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "mcobject"
}
object FloatType extends Type{
    override def allowAdditionSimplification(): Boolean = true
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case FloatType => 0
            case AnyType => 1
            case MCObjectType => 10
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
            case MCObjectType => 10
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "string"
    override def generateCompilerFunction(variable: Variable)(implicit context: Context): Unit = {
        super.generateCompilerFunction(variable)
        if (variable.modifiers.isLazy){
            context.addFunction("substring", CompilerFunction(context, "substring", 
                List(Argument("start", IntType, None)),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression]) => {
                    args match{
                        case IntValue(start)::Nil => (List(), StringValue(variable.lazyValue.asInstanceOf[StringValue].value.substring(start)))
                        case other => throw new Exception(f"Illegal Arguments $other for substring")
                    }
                }
            ))

            context.addFunction("substring", CompilerFunction(context, "substring", 
                List(Argument("start", IntType, None), Argument("end", IntType, None)),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression]) => {
                    args match{
                        case IntValue(start)::IntValue(end)::Nil => (List(), StringValue(variable.lazyValue.asInstanceOf[StringValue].value.substring(start, end)))
                        case other => throw new Exception(f"Illegal Arguments $other for substring")
                    }
                }
            ))

            context.addFunction("length", CompilerFunction(context, "length", 
                List(),
                IntType,
                Modifier.newPublic(),
                (args: List[Expression]) => {
                    args match{
                        case Nil => (List(), IntValue(variable.lazyValue.asInstanceOf[StringValue].value.length))
                        case other => throw new Exception(f"Illegal Arguments $other for length")
                    }
                }
            ))

            context.addFunction("contains", CompilerFunction(context, "contains", 
                List(Argument("predicate", StringType, None)),
                BoolType,
                Modifier.newPublic(),
                (args: List[Expression]) => {
                    args match{
                        case StringValue(pred)::Nil => (List(), BoolValue(variable.lazyValue.asInstanceOf[StringValue].value.contains(pred)))
                        case other => throw new Exception(f"Illegal Arguments $other for contains")
                    }
                }
            ))

            context.addFunction("replace", CompilerFunction(context, "replace", 
                List(Argument("predicate", StringType, None), Argument("to", StringType, None)),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression]) => {
                    args match{
                        case StringValue(pred)::StringValue(to)::Nil => (List(), StringValue(variable.lazyValue.asInstanceOf[StringValue].value.replaceAllLiterally(pred, to)))
                        case other => throw new Exception(f"Illegal Arguments $other for replace")
                    }
                }
            ))

            context.addFunction("hash", CompilerFunction(context, "hash", 
                List(),
                IntType,
                Modifier.newPublic(),
                (args: List[Expression]) => {
                    args match{
                        case Nil => (List(), IntValue(scala.util.hashing.MurmurHash3.stringHash(variable.lazyValue.asInstanceOf[StringValue].value)))
                        case other => throw new Exception(f"Illegal Arguments $other for hash")
                    }
                }
            ))
        }
    }
}
object BoolType extends Type{
    override def allowAdditionSimplification(): Boolean = true
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case BoolType => 0
            case IntType => 1
            case FloatType => 2
            case MCObjectType => 10
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
object ParamsType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "params"
}
object RawJsonType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case RawJsonType => 0
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "rawjson"
}
object EntityType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case EntityType => 0
            case MCObjectType => 10
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
            case MCObjectType => 10
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"(${sub.map(_.getName()).reduce(_ + ", " + _)})"
}
case class ArrayType(inner: Type, size: Expression) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case ArrayType(inner2, size2) if inner == inner2 && size == size2 => 0
            case MCObjectType => 10
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"$inner[$size]"
}
case class LambdaType(val nb: Int) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case FuncType(sources2, output2) if sources2.length == nb => 0
            case MCObjectType => 10
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"lambda"
}
case class FuncType(sources: List[Type], output: Type) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case FuncType(sources2, output2) if sources2.length == sources.length => sources.zip(sources2).map((a,b)=>b.getDistance(a)).sum + output.getDistance(output2)
            case MCObjectType => 10
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"(${sources.map(_.getName()).reduce(_ + ", " + _)}) => $output"
}
case class IdentifierType(name: String) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case IdentifierType(sub2) => getDistance(context.getType(other))
            case MCObjectType => 10
            case AnyType => 1000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = name
}
case class StructType(struct: Struct) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case IdentifierType(sub2) => getDistance(context.getType(other))
            case StructType(sub2) if sub2 == struct => 0
            case MCObjectType => 1000
            case AnyType => 2000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = struct.fullName
}
case class ClassType(clazz: Class) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case IdentifierType(sub2) => getDistance(context.getType(other))
            case ClassType(sub2) if sub2 == clazz => 0
            case MCObjectType => 1000
            case AnyType => 10000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = clazz.fullName
}
case class EnumType(enm: objects.Enum) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case EnumType(sub2) if sub2 == enm => 0
            case MCObjectType => 1000
            case AnyType => 10000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = enm.fullName
}
case class RangeType(sub: Type) extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case RangeType(sub2) => sub.getDistance(sub2)
            case MCObjectType => 1000
            case AnyType => 10000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = f"$sub..$sub"
}


case object JsonType extends Type{
    override def allowAdditionSimplification(): Boolean = false
    override def getDistance(other: Type)(implicit context: Context): Int = {
        other match
            case JsonType => 0
            case MCObjectType => 1000
            case AnyType => 10000
            case _ => outOfBound
    }
    override def getName()(implicit context: Context): String = "json"
}