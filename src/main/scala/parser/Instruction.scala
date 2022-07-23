package parser

import objects.Variable

trait Instruction{
}
trait Expression{
}

case object EmptyInstruction extends Instruction
case class VarAssigmentInstruction(variable: Variable, expression: Expression) extends Instruction