package objects.types

import scala.util.parsing.input.Positional
import objects.Context
import objects.{Struct, Class, Variable, CompilerFunction, OptionalFunction}
import sl.Expression
import sl.*
import objects.Modifier
import objects.Identifier

trait Typed(typ: Type) {
  def getType(): Type = {
    typ
  }
}

private val outOfBound = 100000
abstract class Type extends Positional {
  def allowAdditionSimplification(): Boolean
  def getDistance(other: Type)(implicit context: Context): Int
  def isSubtypeOf(other: Type)(implicit context: Context): Boolean
  def getName()(implicit context: Context): String
  def isDirectComparable(): Boolean
  def isDirectEqualitable(): Boolean

  def isComparaisonSupported(): Boolean
  def isEqualitySupported(): Boolean

  def generateExtensionFunction(
      variable: Variable
  )(implicit context: Context): Unit = {
      context.addFunction(
        "toString",
        OptionalFunction(context, variable, "toString", "standard.string", Identifier.fromString("standard.string.cast"), List(), StringType, Modifier.newPublic())
      )
  }
}

object AnyType extends Type {
  override def toString(): String = "any"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int =
    outOfBound
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case AnyType => true
      case _       => false
    }
  }
  override def getName()(implicit context: Context): String = "any"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}

object MCObjectType extends Type {
  override def toString(): String = "mcobject"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case MCObjectType => 0
      case _            => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case MCObjectType => true
      case _            => false
    }
  }
  override def getName()(implicit context: Context): String = "mcobject"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}

object MCPositionType extends Type {
  override def toString(): String = "mcposition"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case MCPositionType => 0
      case _              => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case MCPositionType => true
      case _              => false
    }
  }
  override def getName()(implicit context: Context): String = "mcposition"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}

object IntType extends Type {
  override def toString(): String = "int"
  override def allowAdditionSimplification(): Boolean = true
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case IntType       => 0
      case FloatType     => 1
      case BoolType      => 2
      case EnumType(enm) => 2
      case AnyType       => 3
      case MCObjectType  => 10
      case _             => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case IntType       => true
      case BoolType      => true
      case FloatType     => true
      case EnumType(enm) => true
      case AnyType       => true
      case MCObjectType  => true
      case _             => false
    }
  }
  override def getName()(implicit context: Context): String = "int"
  override def isDirectComparable(): Boolean = true
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
object FloatType extends Type {
  override def toString(): String = "float"
  override def allowAdditionSimplification(): Boolean = true
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case FloatType    => 0
      case AnyType      => 1
      case MCObjectType => 10
      case _            => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case FloatType    => true
      case AnyType      => true
      case MCObjectType => true
      case _            => false
    }
  }
  override def getName()(implicit context: Context): String = "float"
  override def isDirectComparable(): Boolean = true
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
object StringType extends Type {
  override def toString(): String = "string"
  override def allowAdditionSimplification(): Boolean = true
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case StringType   => 0
      case AnyType      => 1
      case MCObjectType => 10
      case _            => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case StringType   => true
      case AnyType      => true
      case MCObjectType => true
      case _            => false
    }
  }
  override def getName()(implicit context: Context): String = "string"
  override def generateExtensionFunction(
      variable: Variable
  )(implicit context: Context): Unit = {
    super.generateExtensionFunction(variable)
  }
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = true
}
object BoolType extends Type {
  override def toString(): String = "bool"
  override def allowAdditionSimplification(): Boolean = true
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case BoolType     => 0
      case IntType      => 1
      case FloatType    => 2
      case MCObjectType => 10
      case AnyType      => 1000
      case _            => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case BoolType     => true
      case IntType      => true
      case FloatType    => true
      case AnyType      => true
      case MCObjectType => true
      case _            => false
    }
  }
  override def getName()(implicit context: Context): String = "Boolean"
  override def isDirectComparable(): Boolean = true
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
object VoidType extends Type {
  override def toString(): String = "void"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case _ => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case VoidType => true
      case _        => false
    }
  }
  override def getName()(implicit context: Context): String = "void"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
object ParamsType extends Type {
  override def toString(): String = "params"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case _ => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case ParamsType => true
      case _          => false
    }
  }
  override def getName()(implicit context: Context): String = "params"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
object RawJsonType extends Type {
  override def toString(): String = "rawjson"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case RawJsonType => 0
      case _           => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case RawJsonType => true
      case _           => false
    }
  }
  override def getName()(implicit context: Context): String = "rawjson"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
object EntityType extends Type {
  override def toString(): String = "entity"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case EntityType   => 0
      case MCObjectType => 10
      case AnyType      => 1000
      case _            => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case EntityType => true
      case _          => false
    }
  }
  override def getName()(implicit context: Context): String = "entity"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}

case class TupleType(sub: List[Type]) extends Type {
  override def toString(): String =
    f"(${sub.map(_.toString()).reduce(_ + "," + _)})"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case TupleType(sub2) if sub2.length == sub.length =>
        sub.zip(sub2).map((a, b) => a.getDistance(b)).sum
      case MCObjectType => 10
      case AnyType      => 1000
      case _            => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case TupleType(sub2) =>
        sub.size == sub2.size && sub
          .zip(sub2)
          .forall((a, b) => a.isSubtypeOf(b))
      case AnyType      => true
      case MCObjectType => true
      case _            => false
    }
  }
  override def getName()(implicit context: Context): String =
    f"(${sub.map(_.getName()).reduce(_ + ", " + _)})"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
case class ArrayType(inner: Type, size: Expression) extends Type {
  override def toString(): String = f"$inner[$size]"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case ArrayType(inner2, size2) if inner == inner2 && size == size2 => 0
      case MCObjectType                                                 => 10
      case AnyType                                                      => 1000
      case _ => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case ArrayType(sub, size2) => size == size2 && inner.isSubtypeOf(sub)
      case AnyType               => true
      case MCObjectType          => true
      case _                     => false
    }
  }
  override def getName()(implicit context: Context): String = f"$inner[$size]"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = true
}
case class LambdaType(val nb: Int) extends Type {
  override def toString(): String = "()=>"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case LambdaType(nb)              => 0
      case FuncType(sources2, output2) => 0
      case MCObjectType                => 10
      case AnyType                     => 1000
      case _                           => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case LambdaType(nb2)           => nb2 == nb
      case FuncType(sources, output) => true
      case AnyType                   => true
      case MCObjectType              => true
      case _                         => false
    }
  }
  override def getName()(implicit context: Context): String = f"lambda"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
case class FuncType(sources: List[Type], output: Type) extends Type {
  override def toString(): String = sources
    .map(_.toString())
    .reduceLeftOption(_ + ", " + _)
    .getOrElse("()") + "=>" + output
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case FuncType(sources2, output2)
          if sources2
            .filter(_ != VoidType)
            .length == sources.filter(_ != VoidType).length =>
        sources
          .filter(_ != VoidType)
          .zip(sources2.filter(_ != VoidType))
          .map((a, b) => b.getDistance(a))
          .sum + output.getDistance(output2)
      case LambdaType(nb) => 0
      case MCObjectType   => 10
      case AnyType        => 1000
      case _              => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case FuncType(s, o) =>
        sources.filter(_ != VoidType).size == s.filter(_ != VoidType).size &&
        s.filter(_ != VoidType)
          .zip(sources.filter(_ != VoidType))
          .forall((a, b) => a.isSubtypeOf(b)) && output.isSubtypeOf(o)
      case LambdaType(nb) => true
      case AnyType        => true
      case MCObjectType   => true
      case _              => false
    }
  }
  override def getName()(implicit context: Context): String =
    f"(${sources.map(_.getName()).reduce(_ + ", " + _)}) => $output"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
case class IdentifierType(name: String, sub: List[Type]) extends Type {
  override def toString(): String = f"$name?"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case IdentifierType(name2, sub2) => getDistance(context.getType(other))
      case MCObjectType                => 10
      case AnyType                     => 1000
      case _                           => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case AnyType      => true
      case MCObjectType => true
      case _            => false
    }
  }
  override def getName()(implicit context: Context): String = name
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
case class StructType(struct: Struct, sub: List[Type]) extends Type {
  override def toString(): String = struct.fullName
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case IdentifierType(name2, sub2) => getDistance(context.getType(other))
      case StructType(struct2, sub2) if struct2 == struct && sub2 == sub => 0
      case JsonType                                                      => 100
      case MCObjectType                                                  => 1000
      case AnyType                                                       => 2000
      case _ => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case StructType(str, sub2) => struct.hasParent(str) && sub2 == sub
      case JsonType              => true
      case AnyType               => true
      case MCObjectType          => true
      case _                     => false
    }
  }
  override def getName()(implicit context: Context): String = struct.fullName
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = true
  override def isEqualitySupported(): Boolean = true
}
case class ClassType(clazz: Class, sub: List[Type]) extends Type {
  override def toString(): String = clazz.fullName + sub.mkString("<", ",", ">")
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case IdentifierType(name2, sub2) => getDistance(context.getType(other))
      case ClassType(clazz2, sub2) if clazz2 == clazz && sub == sub2 => 0
      case MCObjectType                                              => 1000
      case AnyType                                                   => 10000
      case _ => outOfBound
    }
  }
  
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case ClassType(clz, sub2) => clazz.hasParent(clz) && sub == sub2
      case AnyType              => true
      case MCObjectType         => true
      case _                    => false
    }
  }
  override def getName()(implicit context: Context): String = clazz.fullName
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = true
  override def isEqualitySupported(): Boolean = true

  override def generateExtensionFunction(variable: Variable)(implicit context: Context): Unit = {}
}
case object TypeType extends Type {
  override def toString(): String = "type"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case TypeType => 0
      case _        => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean =
    other == this
  override def getName()(implicit context: Context): String = "type"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
case class EnumType(enm: objects.Enum) extends Type {
  override def toString(): String = enm.fullName
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case EnumType(sub2) if sub2 == enm => 0
      case IntType                       => 1
      case MCObjectType                  => 1000
      case AnyType                       => 10000
      case _                             => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case EnumType(other) => enm == other
      case IntType         => true
      case AnyType         => true
      case MCObjectType    => true
      case _               => false
    }
  }
  override def getName()(implicit context: Context): String = enm.fullName
  override def isDirectComparable(): Boolean = true
  override def isDirectEqualitable(): Boolean = true
  override def isComparaisonSupported(): Boolean = true
  override def isEqualitySupported(): Boolean = false
}
case class RangeType(sub: Type) extends Type {
  override def toString(): String = f"range<$sub>"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case RangeType(sub2) => sub.getDistance(sub2)
      case MCObjectType    => 1000
      case AnyType         => 10000
      case _               => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case RangeType(other) => sub.isSubtypeOf(other)
      case AnyType          => true
      case MCObjectType     => true
      case _                => false
    }
  }
  override def getName()(implicit context: Context): String = f"$sub..$sub"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}

case object JsonType extends Type {
  override def toString(): String = "json"
  override def allowAdditionSimplification(): Boolean = false
  override def getDistance(other: Type)(implicit context: Context): Int = {
    other match{
      case JsonType                => 0
      case StructType(struct, sub) => 100
      case MCObjectType            => 1000
      case AnyType                 => 10000
      case _                       => outOfBound
    }
  }
  override def isSubtypeOf(other: Type)(implicit context: Context): Boolean = {
    other match{
      case JsonType                => true
      case StructType(struct, sub) => true
      case AnyType                 => true
      case MCObjectType            => true
      case _                       => false
    }
  }
  override def getName()(implicit context: Context): String = "json"
  override def isDirectComparable(): Boolean = false
  override def isDirectEqualitable(): Boolean = false
  override def isComparaisonSupported(): Boolean = false
  override def isEqualitySupported(): Boolean = false
}
