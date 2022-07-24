package parser

import objects.Variable
import lexer.Positionable
import lexer.tokens.Token

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
case class InstructionList(instruction: List[Instruction]) extends Instruction

case class VanillaCommand(tokens: List[Token]) extends Instruction
case class VarOperationInstruction(op: String, variable: Variable, expression: Expression) extends Instruction