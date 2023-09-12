package sl

import scala.util.parsing.input.Positional
import objects.types.Type
import objects.Modifier
import objects.{Identifier, Variable, Function, Context}
import objects.EnumField
import objects.EnumValue
import sl.Compilation.Selector.Selector
import scala.util.parsing.input.Position


case class Argument(val name: String, val typ: Type, val defValue: Option[Expression]){
  override def toString() = if (defValue.isDefined) then f"$typ $name = ${defValue.get}" else f"$typ $name"
}

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
  override def toString() = f"package ${name} \n${block}"
}

case class StructDecl(val name: String, val generics: List[String], val block: Instruction, val modifier: Modifier, val parent: Option[String]) extends Instruction {
  override def toString() = f"struct ${name}<${generics.mkString(", ")}> extends $parent ${block}"
}
case class ClassDecl(val name: String, val generics: List[String], val block: Instruction, val modifier: Modifier, val parent: Option[String], val parentGenerics: List[Type], val interfaces: List[(String, List[Type])], val entity: Map[String, Expression]) extends Instruction {
  override def toString() = f"class ${name}<${generics.mkString(", ")}> extends $parent<${parentGenerics.mkString(", ")}> implements <${interfaces.mkString(", ")} ${block}"
}
case class EnumDecl(val name: String, val fields: List[EnumField], val values: List[EnumValue], val modifier: Modifier) extends Instruction {
  override def toString() = f"enum ${name}(${fields}]{$values}"
}
case class PredicateDecl(val name: String, val args: List[Argument], val block: JSONElement, val modifier: Modifier) extends Instruction {
  override def toString() = f"predicate ${name} (${args}) $block"
}

case class FunctionDecl(val name: String, val block: Instruction, val typ: Type, val args: List[Argument], val typeArgs: List[String], val modifier: Modifier) extends Instruction {
  override def toString() = f"def ${name}(${args.mkString(", ")}) ${block}"
}
case class TemplateDecl(val name: String, val block: Instruction, val modifier: Modifier, val parent: Option[String], val generics: List[String], val parentGenerics: List[Expression]) extends Instruction {
  override def toString() = f"template ${name} ${block}"
}
case class BlocktagDecl(val name: String, val values: List[Expression], val modifier: Modifier) extends Instruction {
  override def toString() = f"blocktag $name {${values.mkString(", ")}}"
}

case class TypeDef(val defs: List[(String, Type, String)]) extends Instruction {
  override def toString() = f"typedef ${defs}"
}

case class ForGenerate(val key: String, val provider: Expression, val instr: Instruction) extends Instruction {
  override def toString() = f"forgenerate($key, $provider)$instr"
}
case class ForEach(val key: Identifier, val provider: Expression, val instr: Instruction) extends Instruction {
  override def toString() = f"foreach($key, $provider)$instr"
}

case class VariableDecl(val name: List[String], val _type: Type, val modifier: Modifier, val op: String, val expr: Expression) extends Instruction {
  override def toString() = f"${_type} ${name.mkString(", ")} $op $expr"
}
case class VariableAssigment(val name: List[(Either[Identifier, Variable], Selector)], val op: String, val expr: Expression) extends Instruction{
  override def toString() = 
    val nameText = name.map(n => {
      n match
        case (Left(id), sel) => if (sel == Selector.self) then id.toString() else sel.toString() +"."+ id.toString()
        case (Right(v), sel) => if (sel == Selector.self) then v.fullName else sel.toString() +"."+ v.fullName
    })
    f"${nameText.mkString(", ")} ${op} ${expr}"
}
case class ArrayAssigment(val name: Either[Identifier, Variable], val index: List[Expression], val op: String, val expr: Expression) extends Instruction{
  override def toString() = 
    val nameText = name match
        case Left(id) => id.toString()
        case Right(v) => v.fullName
    
    f"${nameText}[$index] ${op} ${expr}"
}

case class CMD(val value: String) extends Instruction{
  override def toString() = f"/$value"
}

case class FunctionCall(val name: Identifier, val args: List[Expression], val typeargs: List[Type]) extends Instruction {
  override def toString() = f"${name}<${typeargs.mkString(", ")}>(${args.mkString(", ")})"
}
case class TemplateUse(val template: Identifier, val name: String, val block: Instruction, val values: List[Expression]) extends Instruction {
  override def toString() = f"$template ${name}<${values.mkString(", ")}> $block"
}
case class LinkedFunctionCall(val fct: Function, val args: List[Expression], val ret: Variable = null) extends Instruction {
  override def toString() = f"${fct.fullName}()"
}

case class ElseIf(val cond: Expression, val ifBlock: Instruction) extends Instruction {
  override def toString() = f"else if ($cond)$ifBlock"
}

case class If(val cond: Expression, val ifBlock: Instruction, val elseBlock: List[ElseIf]) extends Instruction {
  override def toString() = f"if ($cond)$ifBlock\n${elseBlock.mkString("\n")}"
}

case class Return(val value: Expression) extends Instruction {
  override def toString() = f"return($value)"
}

case class Import(val lib: String, val value: String, val alias: String) extends Instruction {
  override def toString() = f"from $lib import $value as $alias"
}

case class Switch(val value: Expression, val cases: List[SwitchElement], val copyVariable: Boolean = true) extends Instruction {
  override def toString() = f"switch($value){\n${cases.mkString("\n")}\n}"
}
trait SwitchElement{
}
case class SwitchCase(val expr: Expression, val instr: Instruction) extends SwitchElement{
  override def toString(): String = f"$expr -> $instr"
}
case class SwitchForGenerate(val key: String, val provider: Expression, val instr: SwitchCase) extends CPositionable with SwitchElement {
  override def toString(): String = f"forgenerate($key, $provider)$instr"
}
case class SwitchForEach(val key: Identifier, val provider: Expression, val instr: SwitchCase) extends CPositionable with SwitchElement {
  override def toString(): String = f"foreach($key, $provider)$instr"
}

case class WhileLoop(val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"while($cond)$block"
}
case class DoWhileLoop(val cond: Expression, val block: Instruction) extends Instruction {
  override def toString() = f"do $block while($cond)"
}
case class Throw(val expr: Expression) extends Instruction {
  override def toString() = f"throw($expr)"
}
case class Try(val block: Instruction, val except: Instruction, val finallyBlock: Instruction) extends Instruction {
  override def toString() = f"try $block catch $except finally $finallyBlock"
}

case class InstructionList(val list: List[Instruction]) extends Instruction {
  override def toString() = f"${list.mkString("\n")}"
}
case class InstructionBlock(val list: List[Instruction]) extends Instruction {
  override def toString() = f"{\n${list.mkString("\n")}\n}"
  override def unBlockify(): Instruction = InstructionList(list)
}
case class FreeConstructorCall(val expr: Expression) extends Instruction{
    override def toString() = f"new ${expr}()"
}
trait ExecuteType
case object AtType extends ExecuteType{
  override def toString() = f"at"
}
case object FacingType extends ExecuteType{
  override def toString() = f"facing"
}
case object RotatedType extends ExecuteType{
  override def toString() = f"rotated"
}
case object AlignType extends ExecuteType{
  override def toString() = f"align"
}

case class Execute(val typ: ExecuteType, val exprs: List[Expression], val block: Instruction) extends Instruction {
  override def toString() = f"$typ($exprs) $block"
}
case class With(val expr: Expression, val isat: Expression, val cond: Expression, val block: Instruction, val elze: Instruction) extends Instruction {
  override def toString() = 
    if (elze == null) then f"with($expr, $isat, $cond) $block"
    else f"with($expr, $isat, $cond) $block else $elze"
}

case class JSONFile(val name: String, val json: Expression, val modifier: Modifier) extends Instruction {
  override def toString() = f"jsonfile $name"
}