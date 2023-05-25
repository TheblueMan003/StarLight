package sl

import scala.util.parsing.input.Positional
import objects.types.Type
import objects.Modifier
import objects.{Identifier, Variable, Function, Context}
import objects.EnumField
import objects.EnumValue
import sl.Compilation.Selector.Selector
import scala.util.parsing.input.Position


case class Argument(val name: String, val typ: Type, val defValue: Option[Expression])

case class CPosition(_line: Int, _column: Int, _lineContents: String) extends Position{
  override def lineContents = _lineContents
  override def column = _column
  override def line = _line
  override def toString() = f"line ${line} at ${column}"
}
trait CPositionable extends Positional{
  override def setPos(newpos: Position): this.type = {
    super.setPos(CPosition(newpos.line, newpos.column, newpos.longString.split("\n")(0)))
    this
  }
}

sealed abstract class Instruction extends CPositionable{
  def unBlockify(): Instruction = this
}

case class Package(val name: String, val block: Instruction) extends Instruction {
  override def toString() = f"package ${name} {${block}}"
}

case class StructDecl(val name: String, val generics: List[String], val block: Instruction, val modifier: Modifier, val parent: Option[String]) extends Instruction {
  override def toString() = f"struct ${name} ${block}"
}
case class ClassDecl(val name: String, val generics: List[String], val block: Instruction, val modifier: Modifier, val parent: Option[String], val entity: Map[String, Expression]) extends Instruction {
  override def toString() = f"struct ${name} ${block}"
}
case class EnumDecl(val name: String, val fields: List[EnumField], val values: List[EnumValue], val modifier: Modifier) extends Instruction {
  override def toString() = f"enum ${name}(${fields}]{$values}"
}
case class PredicateDecl(val name: String, val args: List[Argument], val block: JSONElement, val modifier: Modifier) extends Instruction {
  override def toString() = f"predicate ${name} (${args}) $block"
}

case class FunctionDecl(val name: String, val block: Instruction, val typ: Type, val args: List[Argument], val typeArgs: List[String], val modifier: Modifier) extends Instruction {
  override def toString() = f"def ${name} ${block}"
}
case class TemplateDecl(val name: String, val block: Instruction, val modifier: Modifier, val parent: Option[String]) extends Instruction {
  override def toString() = f"template ${name} ${block}"
}
case class BlocktagDecl(val name: String, val values: List[Expression], val modifier: Modifier) extends Instruction {
  override def toString() = f"blocktag $name"
}

case class TypeDef(val name: String, val typ: Type) extends Instruction {
  override def toString() = f"typedef ${typ} ${name}"
}

case class ForGenerate(val key: String, val provider: Expression, val instr: Instruction) extends Instruction {
  override def toString() = f"forgenerate($key, $provider)$instr"
}
case class ForEach(val key: Identifier, val provider: Expression, val instr: Instruction) extends Instruction {
  override def toString() = f"foreach($key, $provider)$instr"
}

case class VariableDecl(val name: List[String], val _type: Type, val modifier: Modifier, val op: String, val expr: Expression) extends Instruction {
  override def toString() = f"${_type} ${name}"
}
case class VariableAssigment(val name: List[(Either[Identifier, Variable], Selector)], val op: String, val expr: Expression) extends Instruction{
  override def toString() = f"${name} ${op} ${expr}"
}
case class ArrayAssigment(val name: Either[Identifier, Variable], val index: List[Expression], val op: String, val expr: Expression) extends Instruction{
  override def toString() = f"${name}[$index] ${op} ${expr}"
}

case class CMD(val value: String) extends Instruction{
  override def toString() = f"/$value"
}

case class FunctionCall(val name: Identifier, val args: List[Expression], val typeargs: List[Type]) extends Instruction {
  override def toString() = f"${name}()"
}
case class TemplateUse(val template: Identifier, val name: String, val block: Instruction) extends Instruction {
  override def toString() = f"template ${name}()"
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

case class Import(val lib: String, val value: String, val alias: String) extends Instruction {
  override def toString() = f"from $lib import $value as $alias"
}

case class Switch(val value: Expression, val cases: List[SwitchElement], val copyVariable: Boolean = true) extends Instruction {
  override def toString() = f"switch($value)"
}
trait SwitchElement{
}
case class SwitchCase(val expr: Expression, val instr: Instruction) extends SwitchElement
case class SwitchForGenerate(val key: String, val provider: Expression, val instr: SwitchCase) extends CPositionable with SwitchElement {
}
case class SwitchForEach(val key: Identifier, val provider: Expression, val instr: SwitchCase) extends CPositionable with SwitchElement {
}

case class WhileLoop(val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"while($cond)$block"
}
case class DoWhileLoop(val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"do$block while($cond)"
}
case class Throw(val expr: Expression) extends Instruction {
  override def toString() = f"throw($expr)"
}
case class Try(val block: Instruction, val except: Instruction, val finallyBlock: Instruction) extends Instruction {
  override def toString() = f"try$block catch $except finally$finallyBlock"
}

case class InstructionList(val list: List[Instruction]) extends Instruction {
  override def toString() = f"${list}"
}
case class InstructionBlock(val list: List[Instruction]) extends Instruction {
  override def toString() = f"{${list}}"
  override def unBlockify(): Instruction = InstructionList(list)
}
trait ExecuteType
case object AtType extends ExecuteType
case object FacingType extends ExecuteType
case object RotatedType extends ExecuteType
case object AlignType extends ExecuteType

case class Execute(val typ: ExecuteType, val exprs: List[Expression], val block: Instruction) extends Instruction {
  override def toString() = f"$typ $exprs $block"
}
case class With(val expr: Expression, val isat: Expression, val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"with($expr, $isat, $cond) $block"
}

case class JSONFile(val name: String, val json: Expression, val modifier: Modifier) extends Instruction {
  override def toString() = f"jsonfile $name"
}