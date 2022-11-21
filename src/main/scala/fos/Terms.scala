package fos

import scala.util.parsing.input.Positional
import objects.types.Type
import objects.Modifier
import objects.{Identifier, Variable, Function, Context}
import objects.EnumField
import objects.EnumValue


case class Argument(val name: String, val typ: Type, val defValue: Option[Expression])

/** Abstract Syntax Trees for terms. */
sealed abstract class Instruction extends Positional

case class Package(val name: String, val block: Instruction) extends Instruction {
  override def toString() = f"package ${name} {${block}}"
}

case class StructDecl(val name: String, val block: Instruction, val modifier: Modifier) extends Instruction {
  override def toString() = f"struct ${name} ${block}"
}
case class EnumDecl(val name: String, val fields: List[EnumField], val values: List[EnumValue], val modifier: Modifier) extends Instruction {
  override def toString() = f"enum ${name}(${fields}]{$values}"
}

case class FunctionDecl(val name: String, val block: Instruction, val typ: Type, val args: List[Argument], val modifier: Modifier) extends Instruction {
  override def toString() = f"def ${name} ${block}"
}

case class VariableDecl(val name: String, val _type: Type, val modifier: Modifier) extends Instruction {
  override def toString() = f"${_type} ${name}"
}
case class VariableAssigment(val name: List[Either[Identifier, Variable]], val op: String, val expr: Expression) extends Instruction {
  override def toString() = f"${name} ${op} ${expr}"
}

case class CMD(val value: String) extends Instruction{
  override def toString() = f"/$value"
}

case class FunctionCall(val name: Identifier, val args: List[Expression]) extends Instruction {
  override def toString() = f"${name}()"
}
case class LinkedFunctionCall(val fct: Function, val args: List[Expression], val ret: Variable = null) extends Instruction {
  override def toString() = f"${fct.fullName}()"
}

case class ElseIf(val cond: Expression, val ifBlock: Instruction) extends Instruction {
  override def toString() = f"else if ($cond)$ifBlock"
}

case class If(val cond: Expression, val ifBlock: Instruction, val elseBlock: List[ElseIf]) extends Instruction {
  override def toString() = f"if ($cond)$ifBlock $elseBlock"
}

case class Return(val value: Expression) extends Instruction {
  override def toString() = f"return($value)"
}

case class Switch(val value: Expression, val cases: List[SwitchCase], val copyVariable: Boolean = true) extends Instruction {
  override def toString() = f"switch($value)"
}
case class SwitchCase(val expr: Expression, val instr: Instruction)

case class WhileLoop(val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"while($cond)$block"
}
case class DoWhileLoop(val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"do$block while($cond)"
}

case class InstructionList(val list: List[Instruction]) extends Instruction {
  override def toString() = f"${list}"
}
case class InstructionBlock(val list: List[Instruction]) extends Instruction {
  override def toString() = f"{${list}}"
}
case class At(val expr: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"at $expr $block"
}
case class With(val expr: Expression, val isat: Expression, val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"with($expr, $isat, $cond) $block"
}

case class JSONFile(val name: String, val json: JSONElement) extends Instruction {
  override def toString() = f"jsonfile $name"
}