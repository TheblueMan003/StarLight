package objects.types

trait Typed(typ: Type){
    def getType(): Type ={
        typ
    }
}



abstract class Type {
}


object IntType extends Type
object FloatType extends Type
object StringType extends Type
object BoolType extends Type
object VoidType extends Type


case class TuppleType(sub: List[Type]) extends Type
case class ArrayType(inner: Type, size: Integer) extends Type
case class FuncType(sources: List[Type], output: Type) extends Type