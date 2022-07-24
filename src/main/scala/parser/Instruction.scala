package parser

import objects.Variable
import lexer.Positionable

trait Instruction{
}
trait Expression extends Positionable{
}
case class IntValue(value: Int) extends Expression
case class FloatValue(value: Float) extends Expression
case class BooleanValue(value: Boolean) extends Expression
case class StringValue(value: String) extends Expression
case class BinarayExpr(op: String, left: Expression, right: Expression) extends Expression
case class Not(value: Expression) extends Expression


case object EmptyInstruction extends Instruction
case class VarAssigmentInstruction(variable: Variable, expression: Expression) extends Instruction