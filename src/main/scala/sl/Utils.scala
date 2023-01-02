package sl

import objects.Identifier
import objects.Context
import objects.types.*
import objects.{Variable, EnumValue, EnumField}
import scala.io.Source
import scala.collection.parallel.CollectionConverters._
import sl.Compilation.Selector.*

object Utils{
    def getFile(path: String): String = {
        val source = scala.io.Source.fromFile(path)
        source.getLines mkString "\n"
    }
    def getFileLines(path: String): List[String] = {
        val source = scala.io.Source.fromFile(path)
        source.getLines.toList
    }
    def getLib(path: String): Option[Instruction] = {
        val cpath = path.replace(".","/")
        val ipath = path.replace("/",".").replace("\\",".")
        Parser.parse(path, Source.fromResource("libraries/"+cpath+".sl").getLines.reduce((x,y) => x + "\n" +y))
    }
    def getConfig(path: String): List[String] = {
        Source.fromResource("configs/"+path).getLines.toList
    }
    def getResources(path: String)={
        Source.fromResource(path).getLines.reduce((x,y) => x + "\n" +y)
    }
    def stringify(string: String): String = {
        f"\"${string.replaceAllLiterally("\\\\", "\\\\").replaceAllLiterally("\"", "\\\"")}\""
    }
    def substReturn(instr: Instruction, to: Variable): Instruction = {
        instr match
            case Package(name, block) => Package(name, substReturn(block, to))
            case StructDecl(name, block, modifier, parent) => StructDecl(name, substReturn(block, to), modifier, parent)
            case ClassDecl(name, block, modifier, parent, entity) => ClassDecl(name, substReturn(block, to), modifier, parent, entity)
            case FunctionDecl(name, block, typ, args, modifier) => FunctionDecl(name, substReturn(block, to), typ, args, modifier)
            case PredicateDecl(name, args, block, modifier) => instr
            case BlocktagDecl(name, values, modifier) => instr
            case ForGenerate(key, provider, instr) => ForGenerate(key, provider, substReturn(instr, to))
            case ForEach(key, provider, instr) => ForEach(key, provider, substReturn(instr, to))
            case EnumDecl(name, fields, values, modifier) => instr
            case VariableDecl(name, _type, modifier, op, expr) => instr
            case JSONFile(name, json, mod) => instr
            case Import(lib, value, alias) => instr
            
            case InstructionList(list) => InstructionList(list.map(substReturn(_, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(substReturn(_, to)))

            case TemplateDecl(name, block, modifier, parent) => TemplateDecl(name, substReturn(block, to), modifier, parent)
            case TemplateUse(iden, name, instr) => TemplateUse(iden, name, substReturn(instr, to))
            case TypeDef(name, typ) => instr

            case ElseIf(cond, ifBlock) => ElseIf(cond, substReturn(ifBlock, to))
            case If(cond, ifBlock, elseBlock) => If(cond, substReturn(ifBlock, to), elseBlock.map(substReturn(_,  to).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args) => instr
            case ArrayAssigment(name, index, op, value) => instr
            case LinkedFunctionCall(name, args, vari) => instr
            case VariableAssigment(name, op, expr) => instr
            case Return(value) => VariableAssigment(List((Left(to.fullName), Selector.self)), "=", value)
            case WhileLoop(cond, instr) => WhileLoop(cond, substReturn(instr, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(cond, substReturn(instr, to))

            case Execute(typ, expr, block) => Execute(typ, expr, substReturn(block, to))
            case With(expr, isAt, cond, block) => With(expr, isAt, cond, substReturn(block, to))

            case Switch(cond, cases, cv) => Switch(cond, cases.map(x => SwitchCase(x.expr, substReturn(x.instr, to))), cv)
    }


    def subst(instr: Instruction, from: Identifier, to: Identifier): Instruction = {
        instr match
            case Package(name, block) => Package(name, subst(block, from, to))
            case StructDecl(name, block, modifier, parent) => StructDecl(name, subst(block, from, to), modifier, parent)
            case ClassDecl(name, block, modifier, parent, entity) => ClassDecl(name, subst(block, from, to), modifier, parent, entity)
            case FunctionDecl(name, block, typ, args, modifier) => FunctionDecl(name, subst(block, from, to), typ, args, modifier)
            case PredicateDecl(name, args, block, modifier) => PredicateDecl(name, args, block, modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, _type, modifier, op, subst(expr, from, to))
            case BlocktagDecl(name, values, modifier) => BlocktagDecl(name, values.map(subst(_, from, to)), modifier)
            case ForGenerate(key, provider, instr) => ForGenerate(key, subst(provider, from, to), subst(instr, from, to))
            case ForEach(key, provider, instr) => ForEach(key, subst(provider, from, to), subst(instr, from, to))
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(subst(_, from, to)))), modifier)
            case JSONFile(name, json, mod) => instr
            case Import(lib, value, alias) => instr
            
            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case TemplateDecl(name, block, modifier, parent) => TemplateDecl(name, subst(block, from, to), modifier, parent)
            case TemplateUse(iden, name, instr) => TemplateUse(iden, name, subst(instr, from, to))
            case TypeDef(name, typ) => TypeDef(name, typ)

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => CMD(value)
            case FunctionCall(name, args) => FunctionCall(name.replaceAllLiterally(from, to), args.map(subst(_, from, to)))
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case ArrayAssigment(name, index, op, value) => {
                ArrayAssigment(subst(name, from, to), index.map(subst(_, from, to)), op, subst(value, from, to))
            }
            case VariableAssigment(name, op, expr) => {
                VariableAssigment(name.map((l,s) => (subst(l, from, to), s)), op, subst(expr, from, to))
            }
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))

            case Execute(typ, expr, block) => Execute(typ, expr.map(subst(_, from, to)), subst(block, from, to))
            case With(expr, isAt, cond, block) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to))

            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map(x => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to))), cv)
    }
    def subst(vari: Either[Identifier, Variable], from: Identifier, to: Identifier): Either[Identifier, Variable] = {
        vari match
            case Left(value) => Left(value.replaceAllLiterally(from, to))
            case Right(value) => Right(value)
    }

    def subst(instr: Expression, from: Identifier, to: Identifier): Expression = {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case JsonValue(content) => instr
            case StringValue(value) => instr
            case RawJsonValue(value) => instr
            case SelectorValue(content) => instr
            case NamespacedName(value) => instr
            case EnumIntValue(value) => instr
            case LinkedFunctionValue(fct) => instr
            case PositionValue(value) => instr
            case TagValue(value) => instr
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case ArrayGetValue(name, index) => ArrayGetValue(subst(name, from, to), index.map(subst(_, from, to)))
            case VariableValue(name, sel) => VariableValue(name.replaceAllLiterally(from, to), sel)
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case UnaryOperation(op, left) => UnaryOperation(op, subst(left, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args, selector) => FunctionCallValue(subst(name, from, to), args.map(subst(_, from, to)), selector)
            case ConstructorCall(name, args) => ConstructorCall(name, args.map(subst(_, from, to)))
            case RangeValue(min, max) => RangeValue(subst(min, from, to), subst(max, from, to))
            case LambdaValue(args, instr) => LambdaValue(args, subst(instr, from, to))
            case lk: LinkedVariableValue => lk
    }


    def subst(instr: Instruction, from: String, to: String): Instruction = {
        instr match
            case Package(name, block) => Package(name.replaceAllLiterally(from, to), subst(block, from, to))
            case StructDecl(name, block, modifier, parent) => StructDecl(name.replaceAllLiterally(from, to), subst(block, from, to), modifier, parent)
            case ClassDecl(name, block, modifier, parent, Some(NamespacedName(entity))) =>
                ClassDecl(name.replaceAllLiterally(from, to), subst(block, from, to), modifier, parent, Some(NamespacedName(entity.replaceAllLiterally(from, to))))
            case ClassDecl(name, block, modifier, parent, entity) =>
                ClassDecl(name.replaceAllLiterally(from, to), subst(block, from, to), modifier, parent, entity)
            case FunctionDecl(name, block, typ, args, modifier) => {
                if (args.exists(x => x.name == from)){
                    instr
                }
                else{
                    FunctionDecl(name.replaceAllLiterally(from, to), subst(block, from, to), typ, args, modifier)
                }
            }
            case PredicateDecl(name, args, block, modifier) => {
                if (args.exists(x => x.name == from)){
                    instr
                }
                else{
                    PredicateDecl(name.replaceAllLiterally(from, to), args, subst(block, from, to), modifier)
                }
            }
            case BlocktagDecl(name, values, modifier) => BlocktagDecl(name.replaceAllLiterally(from, to), values.map(subst(_, from, to)), modifier)
            case Import(lib, value, alias) => instr
            case ForGenerate(key, provider, instr) => ForGenerate(key, subst(provider, from, to), subst(instr, from, to))
            case ForEach(key, provider, instr) => ForEach(key, subst(provider, from, to), subst(instr, from, to))
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name.replaceAllLiterally(from, to), fields, values.map(v => EnumValue(v.name.replaceAllLiterally(from, to), v.fields.map(subst(_, from, to)))), modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name.map(_.replaceAllLiterally(from, to)), _type, modifier, op, subst(expr, from, to))
            case JSONFile(name, json, mod) => JSONFile(name.replaceAllLiterally(from, to), subst(json, from, to), mod)

            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case TemplateDecl(name, block, modifier, parent) => TemplateDecl(name.replaceAllLiterally(from, to), subst(block, from, to), modifier, parent)
            case TemplateUse(iden, name, instr) => TemplateUse(iden, name.replaceAllLiterally(from, to), subst(instr, from, to))
            case TypeDef(name, typ) => TypeDef(name.replaceAllLiterally(from, to), typ)

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => CMD(value.replaceAllLiterally(from, to))
            case FunctionCall(name, args) => FunctionCall(name.toString().replaceAllLiterally(from, to), args.map(subst(_, from, to)))
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case VariableAssigment(name, op, expr) => VariableAssigment(name.map((l,s) => (subst(l, from, to), s)), op, subst(expr, from, to))
            case ArrayAssigment(name, index, op, value) => {
                ArrayAssigment(subst(name, from, to), index.map(subst(_, from, to)), op, subst(value, from, to))
            }
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))

            case Execute(typ, expr, block) => Execute(typ, expr.map(subst(_, from, to)), subst(block, from, to))
            case With(expr, isAt, cond, block) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to))

            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map(x => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to))), cv)
    }
    def subst(vari: Either[Identifier, Variable], from: String, to: String): Either[Identifier, Variable] = {
        vari match
            case Left(value) => Left(value.toString().replaceAllLiterally(from, to))
            case Right(value) => Right(value)
    }

    def subst(instr: Expression, from: String, to: String): Expression = {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case SelectorValue(content) => instr
            case NamespacedName(value) => NamespacedName(value.replaceAllLiterally(from, to))
            case StringValue(value) => StringValue(value.replaceAllLiterally(from, to))
            case PositionValue(value) => PositionValue(value.replaceAllLiterally(from, to))
            case TagValue(value) => TagValue(value.replaceAllLiterally(from, to))
            case RawJsonValue(value) => instr
            case EnumIntValue(value) => instr
            case LinkedFunctionValue(fct) => instr
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case ArrayGetValue(name, index) => ArrayGetValue(subst(name, from, to), index.map(subst(_, from, to)))
            case JsonValue(content) => JsonValue(subst(content, from, to))
            case VariableValue(name, sel) => VariableValue(name.toString().replaceAllLiterally(from, to), sel)
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case UnaryOperation(op, left) => UnaryOperation(op, subst(left, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args, selector) => FunctionCallValue(subst(name, from, to), args.map(subst(_, from, to)), selector)
            case ConstructorCall(name, args) => ConstructorCall(name, args.map(subst(_, from, to)))
            case RangeValue(min, max) => RangeValue(subst(min, from, to), subst(max, from, to))
            case LambdaValue(args, instr) => LambdaValue(args.map(_.replaceAllLiterally(from, to)), subst(instr, from, to))
            case lk: LinkedVariableValue => lk
            case null => null
    }

    def subst(json: JSONElement, from: String, to: String): JSONElement = {
        json match{
            case JsonArray(content) => JsonArray(content.map(subst(_, from, to)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k.replaceAllLiterally(from, to), subst(v, from, to))))
            case JsonString(value) => JsonString(value.replaceAllLiterally(from, to))
            case JsonBoolean(value) => JsonBoolean(value)
            case JsonInt(value) => JsonInt(value)
            case JsonFloat(value) => JsonFloat(value)
            case JsonIdentifier(value) => JsonIdentifier(value.replaceAllLiterally(from, to))
            case JsonCall(value, args) => JsonCall(value.replaceAllLiterally(from, to), args.map(subst(_, from, to)))
        } 
    }


    def subst(instr: Instruction, from: String, to: Expression): Instruction = {
        instr match
            case Package(name, block) => Package(name, subst(block, from, to))
            case StructDecl(name, block, modifier, parent) => StructDecl(name, subst(block, from, to), modifier, parent)
            case ClassDecl(name, block, modifier, parent, entity) => ClassDecl(name, subst(block, from, to), modifier, parent, entity)
            case FunctionDecl(name, block, typ, args, modifier) => {
                if (args.exists(x => x.name == from)){
                    instr
                }
                else{
                    FunctionDecl(name, subst(block, from, to), typ, args, modifier)
                }
            }
            case PredicateDecl(name, args, block, modifier) => instr
            case BlocktagDecl(name, values, modifier) => BlocktagDecl(name, values.map(subst(_, from, to)), modifier)
            case Import(lib, value, alias) => instr
            case ForGenerate(key, provider, instr) => ForGenerate(key, subst(provider, from, to), subst(instr, from, to))
            case ForEach(key, provider, instr) => ForEach(key, subst(provider, from, to), subst(instr, from, to))
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(subst(_, from, to)))), modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, _type, modifier, op, subst(expr, from, to))

            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case TypeDef(name, typ) => TypeDef(name, typ)

            case TemplateDecl(name, block, modifier, parent) => TemplateDecl(name, subst(block, from, to), modifier, parent)
            case TemplateUse(iden, name, instr) => TemplateUse(iden, name, subst(instr, from, to))

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args) => FunctionCall(name, args.map(subst(_, from, to)))
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case VariableAssigment(name, op, expr) => VariableAssigment(name, op, subst(expr, from, to))
            case ArrayAssigment(name, index, op, value) => {
                ArrayAssigment(name, index.map(subst(_, from, to)), op, subst(value, from, to))
            }
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))
            case JSONFile(name, json, mod) => instr

            case Execute(typ, expr, block) => Execute(typ, expr.map(subst(_, from, to)), subst(block, from, to))
            case With(expr, isAt, cond, block) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to))

            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map(x => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to))), cv)
    }

    def rmFunctions(instr: Instruction): Instruction = {
        instr match
            case Package(name, block) => Package(name, rmFunctions(block))
            case StructDecl(name, block, modifier, parent) => StructDecl(name, rmFunctions(block), modifier, parent)
            case ClassDecl(name, block, modifier, parent, entity) => ClassDecl(name, rmFunctions(block), modifier, parent, entity)
            case FunctionDecl(name, block, typ, args, modifier) => InstructionList(List())
            case PredicateDecl(name, args, block, modifier) => instr
            case BlocktagDecl(name, values, modifier) => instr
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values, modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, _type, modifier, op, expr)
            case ForGenerate(key, provider, instr) => ForGenerate(key, provider, rmFunctions(instr))
            case ForEach(key, provider, instr) => ForEach(key, provider, rmFunctions(instr))
            case Import(lib, value, alias) => instr

            case InstructionList(list) => InstructionList(list.map(rmFunctions(_)))
            case InstructionBlock(list) => InstructionBlock(list.map(rmFunctions(_)))
            case TypeDef(name, typ) => instr

            case TemplateDecl(name, block, modifier, parent) => TemplateDecl(name, rmFunctions(block), modifier, parent)
            case TemplateUse(iden, name, instr) => TemplateUse(iden, name, rmFunctions(instr))

            case ElseIf(cond, ifBlock) => ElseIf(cond, rmFunctions(ifBlock))
            case If(cond, ifBlock, elseBlock) => If(cond, rmFunctions(ifBlock), elseBlock.map(rmFunctions(_).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args) => instr
            case LinkedFunctionCall(name, args, vari) => instr
            case VariableAssigment(name, op, expr) => instr
            case ArrayAssigment(name, index, op, value) => instr
            case Return(value) => instr
            case WhileLoop(cond, instr) => WhileLoop(cond, rmFunctions(instr))
            case DoWhileLoop(cond, instr) => DoWhileLoop(cond, rmFunctions(instr))
            case JSONFile(name, json, mod) => instr

            case Execute(typ, expr, block) => Execute(typ, expr, rmFunctions(block))
            case With(expr, isAt, cond, block) => With(expr, isAt, cond, rmFunctions(block))

            case Switch(cond, cases, cv) => Switch(cond, cases.map(x => SwitchCase(x.expr, rmFunctions(x.instr))), cv)
    }

    def fix(name: Either[Identifier, Variable])(implicit context: Context, ignore: Set[Identifier]) = {
        name match
            case Left(iden) if ignore.contains(iden) => name
            case Left(iden) => {
                context.tryGetVariable(iden) match
                    case None => name
                    case Some(value) => Right(value)
            }
            case Right(vari) => Right(vari)
    }

    def getFreeVar(instr: Instruction): Set[Identifier]= {
        instr match
            case VariableDecl(name, _type, modifier, op, expr) => name.map(Identifier.fromString(_)).toSet
            case InstructionList(list) => list.flatMap(getFreeVar(_)).toSet
            case InstructionBlock(list) => list.flatMap(getFreeVar(_)).toSet
            case _ => Set()
    }

    def fix(instr: Instruction)(implicit context: Context, ignore: Set[Identifier]): Instruction = {
        instr match
            case Package(name, block) => Package(name, fix(block))
            case StructDecl(name, block, modifier, parent) => StructDecl(name, fix(block), modifier, parent)
            case ClassDecl(name, block, modifier, parent, entity) => ClassDecl(name, fix(block), modifier, parent, entity)
            case FunctionDecl(name, block, typ, args, modifier) => FunctionDecl(name, fix(block)(context, ignore ++ args.map(a => Identifier.fromString(a.name)).toSet), typ, args, modifier)
            case PredicateDecl(name, args, block, modifier) => PredicateDecl(name, args, fix(block), modifier)
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(fix(_)))), modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, fix(_type), modifier, op, fix(expr))
            case BlocktagDecl(name, values, modifier) => BlocktagDecl(name, values.map(fix(_)), modifier)
            case ForGenerate(key, provider, instr) => ForGenerate(key, fix(provider), fix(instr))
            case ForEach(key, provider, instr) => ForEach(key, fix(provider), fix(instr))
            case Import(lib, value, alias) => instr

            case InstructionList(list) => {
                val set2 = getFreeVar(instr) ++ ignore
                InstructionList(list.map(fix(_)(context, set2)))
            }
            case InstructionBlock(list) => {
                val set2 = getFreeVar(instr) ++ ignore
                InstructionList(list.map(fix(_)(context, set2)))
            }

            case TemplateDecl(name, block, modifier, parent) => TemplateDecl(name, fix(block), modifier, parent)
            case TemplateUse(iden, name, instr) => TemplateUse(iden, name, fix(instr))
            case TypeDef(name, typ) => TypeDef(name, fix(typ))

            case ElseIf(cond, ifBlock) => ElseIf(fix(cond), fix(ifBlock))
            case If(cond, ifBlock, elseBlock) => If(fix(cond), fix(ifBlock), elseBlock.map(fix(_).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args) => if ignore.contains(name) then FunctionCall(name, args.map(fix(_))) else{ 
                val argF = args.map(fix(_))
                try{
                    val fct = context.getFunction(name, argF, VoidType)
                    LinkedFunctionCall(fct._1, fct._2)
                }
                catch{
                    case _ => FunctionCall(name, argF)
                }
            }
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(fix(_)), vari)
            case VariableAssigment(name, op, expr) => VariableAssigment(name.map{case (v, s) => (fix(v),s)}, op, fix(expr))
            case ArrayAssigment(name, index, op, expr) => ArrayAssigment(fix(name), index.map(fix(_)), op, fix(expr))
            case Return(value) => Return(fix(value))
            case WhileLoop(cond, instr) => WhileLoop(fix(cond), fix(instr))
            case DoWhileLoop(cond, instr) => DoWhileLoop(fix(cond), fix(instr))
            case JSONFile(name, json, mod) => instr

            case Execute(typ, expr, block) => Execute(typ, expr.map(fix(_)), fix(block))
            case With(expr, isAt, cond, block) => With(fix(expr), fix(isAt), fix(cond), fix(block))

            case Switch(cond, cases, cv) => Switch(fix(cond), cases.map(x => SwitchCase(fix(x.expr), fix(x.instr))), cv)
    }
    def fix(typ: Type)(implicit context: Context, ignore: Set[Identifier]): Type = {
        typ match
            case TupleType(sub) => TupleType(sub.map(fix(_)))
            case ArrayType(inner, size) => ArrayType(fix(inner), size)
            case RangeType(sub) => RangeType(fix(sub))
            case IdentifierType("val") => typ
            case IdentifierType("var") => typ
            case IdentifierType(name) => {
                context.getType(typ)
            }
            case other => other
        
    }
    def fix(instr: Expression)(implicit context: Context, ignore: Set[Identifier]): Expression = {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case SelectorValue(content) => instr
            case NamespacedName(value) => instr
            case StringValue(value) => instr
            case RawJsonValue(value) => instr
            case EnumIntValue(value) => instr
            case LinkedFunctionValue(fct) => instr
            case TagValue(value) => instr
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case PositionValue(value) => instr
            case JsonValue(content) => JsonValue(fix(content))
            case ArrayGetValue(name, index) => ArrayGetValue(fix(name), index.map(fix(_)))
            case VariableValue(name, sel) => if ignore.contains(name) then instr else
                context.tryGetVariable(name) match
                    case Some(vari) => LinkedVariableValue(vari, sel)
                    case None => VariableValue(name, sel)
            case BinaryOperation(op, left, right) => BinaryOperation(op, fix(left), fix(right))
            case UnaryOperation(op, left) => UnaryOperation(op, fix(left))
            case TupleValue(values) => TupleValue(values.map(fix(_)))
            case FunctionCallValue(name, args, sel) => FunctionCallValue(fix(name), args.map(fix(_)), sel)
            case ConstructorCall(name, args) => if ignore.contains(name) then ConstructorCall(name, args.map(fix(_))) else
                context.getType(IdentifierType(name.toString())) match
                    case StructType(struct) => ConstructorCall(struct.fullName, args.map(fix(_)))
                    case ClassType(clazz) => ConstructorCall(clazz.fullName, args.map(fix(_)))
                    case other => throw new Exception(f"Cannot constructor call $other")
            case RangeValue(min, max) => RangeValue(fix(min), fix(max))
            case LambdaValue(args, instr) => LambdaValue(args, fix(instr))
            case lk: LinkedVariableValue => lk
            case null => null
    }
    def fix(json: JSONElement)(implicit context: Context, ignore: Set[Identifier]): JSONElement = {
        json match{
            case JsonArray(content) => JsonArray(content.map(fix(_)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k, fix(v))))
            case JsonString(value) => JsonString(value)
            case JsonBoolean(value) => JsonBoolean(value)
            case JsonInt(value) => JsonInt(value)
            case JsonFloat(value) => JsonFloat(value)
            case JsonIdentifier(value) => {
                if ignore.contains(Identifier.fromString(value)) then JsonIdentifier(value) else
                context.tryGetVariable(Identifier.fromString(value)) match{
                    case Some(vari) if vari.modifiers.isLazy => toJson(vari.lazyValue)
                    case _ => JsonIdentifier(value)
                }
            }
            case JsonCall(value, args) => {
                if ignore.contains(Identifier.fromString(value)) then JsonIdentifier(value) else{
                    val (fct, args2) = context.getFunction(value, args.map(fix(_)), JsonType)
                    JsonCall(fct.fullName, args2)
                }
            }
        } 
    }

    def subst(instr: Expression, from: String, to: Expression): Expression = {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case StringValue(value) => instr
            case RawJsonValue(value) => instr
            case JsonValue(content) => instr
            case SelectorValue(content) => instr
            case NamespacedName(value) => instr
            case EnumIntValue(value) => instr
            case LinkedFunctionValue(fct) => instr
            case PositionValue(value) => instr
            case TagValue(value) => instr
            case ArrayGetValue(name, index) => ArrayGetValue(subst(name, from, to), index.map(subst(_, from, to)))
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case VariableValue(name, sel) => if name.toString() == from then to else instr
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case UnaryOperation(op, left) => UnaryOperation(op, subst(left, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args, selector) => FunctionCallValue(name, args.map(subst(_, from, to)), selector)
            case ConstructorCall(name, args) => ConstructorCall(name, args.map(subst(_, from, to)))
            case RangeValue(min, max) => RangeValue(subst(min, from, to), subst(max, from, to))
            case LambdaValue(args, instr) => LambdaValue(args, subst(instr, from, to))
            case lk: LinkedVariableValue => lk
    }


    def simplifyToVariable(expr: Expression)(implicit context: Context): (List[String], LinkedVariableValue) = {
        expr match
            case VariableValue(name, sel) => simplifyToVariable(context.resolveVariable(expr))
            case LinkedVariableValue(name, sel) => (List(), LinkedVariableValue(name, sel))
            case other => {
                val vari = context.getFreshVariable(typeof(other))
                (vari.assign("=", other), LinkedVariableValue(vari))
            }
    }
    def simplifyToLazyVariable(expr: Expression)(implicit context: Context): (List[String], LinkedVariableValue) = {
        expr match
            case VariableValue(name, sel) => simplifyToLazyVariable(context.resolveVariable(expr))
            case LinkedVariableValue(name, sel) => (List(), LinkedVariableValue(name, sel))
            case FunctionCallValue(VariableValue(name, sel), args, selector) => {
                val vari = context.getFreshVariable(typeof(expr))
                vari.modifiers.isLazy = true
                (context.getFunction(name, args, VoidType).call(vari), LinkedVariableValue(vari))
            }
            case other => {
                val vari = context.getFreshVariable(typeof(other))
                vari.modifiers.isLazy = true
                (vari.assign("=", other), LinkedVariableValue(vari))
            }
    }

    def typeof(expr: Expression)(implicit context: Context): Type = {
        expr match
            case IntValue(value) => IntType
            case FloatValue(value) => FloatType
            case BoolValue(value) => BoolType
            case StringValue(value) => StringType
            case RawJsonValue(value) => RawJsonType
            case JsonValue(content) => JsonType
            case SelectorValue(content) => EntityType
            case LambdaValue(args, instr) => LambdaType(args.length)
            case EnumIntValue(value) => IntType
            case NamespacedName(value) => MCObjectType
            case PositionValue(value) => MCPositionType
            case TagValue(value) => MCObjectType
            case ArrayGetValue(name, index) => {
                typeof(name) match
                    case ArrayType(inner, size) => inner
                    case other => throw new Exception(f"Illegal access of $other")
            }
            case LinkedFunctionValue(fct) => FuncType(fct.arguments.map(_.typ), fct.getType())
            case DefaultValue => throw new Exception(f"default value has no type")
            case NullValue => AnyType
            case VariableValue(name, sel) => {
                val vari = context.tryGetVariable(name)
                vari match
                    case None => {
                        val property = context.tryGetProperty(name)
                        property match
                            case None => {
                                try{
                                    val fct = context.getFunction(name)
                                    FuncType(fct.arguments.map(_.typ), fct.getType())
                                }catch{
                                    _ => AnyType
                                }
                            }
                            case Some(v) => v.getter.getType()
                    }
                    case Some(value) => value.getType()
            }
            case BinaryOperation(op, left, right) => combineType(op, typeof(left), typeof(right), expr)
            case UnaryOperation(op, left) => BoolType
            case TupleValue(values) => TupleType(values.map(typeof(_)))
            case FunctionCallValue(name, args, selector) => {
                try{
                    name match
                        case VariableValue(name, sel) => context.getFunction(name, args, AnyType)._1.getType()
                        case other => typeof(name) match
                            case FuncType(sources, output) => output
                            case other => throw new Exception(f"Cannot call $other")
                }catch{
                    _ => AnyType
                }
            }
            case ConstructorCall(name, args) => {
                context.getType(IdentifierType(name.toString()))
            }
            case RangeValue(min, max) => RangeType(typeof(min))
            case LinkedVariableValue(vari, sel) => vari.getType()
    }

    def combineType(op: String, t1: Type, t2: Type, expr: Expression): Type = {
        op match{
            case "in" => BoolType
            case "==" | "<=" | "<" | ">" | ">=" => BoolType
            case "+" | "-" | "*" | "/" | "%" | "^" => {
                (t1, t2) match
                    case (IntType, IntType) => IntType
                    case (IntType, FloatType) => FloatType
                    case (FloatType, IntType) => IntType
                    case (FloatType, FloatType) => FloatType

                    case (BoolType, BoolType) => BoolType

                    case (EntityType, EntityType) => EntityType
                    case (StringType, StringType) => StringType
                    case (RawJsonType, RawJsonType) => RawJsonType
            }
            case "&&" | "||" => {
                (t1, t2) match
                    case (BoolType | IntType | FloatType, BoolType | IntType | FloatType) => BoolType
                    case (a, b) => throw new Exception(f"Unexpect type in ${expr} found $a and $b, exptected: bool and bool") 
            }
        }
    }

    def simplify(expr: Expression)(implicit context: Context): Expression = {
        expr match
            case BinaryOperation("<" | "<=" | "==" | "!=" | ">=" | ">", left, right) => {
                val op = expr.asInstanceOf[BinaryOperation].op
                val nl = simplify(left)
                val nr = simplify(right)
                (nl, nr) match
                    case (IntValue(a), IntValue(b)) => BoolValue(compare(op, a, b))
                    case (FloatValue(a), FloatValue(b)) => BoolValue(compare(op, a, b))
                    case (IntValue(a), FloatValue(b)) => BoolValue(compare(op, a, b))
                    case (FloatValue(a), IntValue(b)) => BoolValue(compare(op, a, b))
                    case (BoolValue(a), BoolValue(b)) => BoolValue(compare(op, a, b))
                    case (StringValue(a), StringValue(b)) => BoolValue(compare(op, a, b))
                    case _ => BinaryOperation(op, nl, nr)
            }
            case BinaryOperation("+", a: Expression, b: Expression) if a == b && typeof(a).allowAdditionSimplification() => BinaryOperation("*", a, IntValue(2))
            case BinaryOperation("+", RawJsonValue(a), RawJsonValue(b)) => RawJsonValue(a ::: b)
            case BinaryOperation(op, left, right) => {
                val nl = simplify(left)
                val nr = simplify(right)
                (nl, nr) match
                    case (IntValue(a), IntValue(b)) => IntValue(combine(op, a, b))
                    case (FloatValue(a), FloatValue(b)) => FloatValue(combine(op, a, b))
                    case (IntValue(a), FloatValue(b)) => FloatValue(combine(op, a, b))
                    case (FloatValue(a), IntValue(b)) => FloatValue(combine(op, a, b))
                    case (BoolValue(a), BoolValue(b)) => BoolValue(combine(op, a, b))
                    case (StringValue(a), StringValue(b)) => StringValue(combine(op, a, b))
                    case (JsonValue(a), JsonValue(b)) => JsonValue(combine(op, a, b))
                    case (JsonValue(a), b) => JsonValue(combine(op, a, toJson(b)))
                    case _ => BinaryOperation(op, nl, nr)
            }
            case VariableValue(iden, sel) if iden.toString() == "Compiler.isJava" => {
                BoolValue(Settings.target == MCJava)
            }
            case VariableValue(iden, sel) if iden.toString() == "Compiler.isBedrock" => {
                BoolValue(Settings.target == MCBedrock)
            }
            case LinkedVariableValue(vari, sel) => {
                if vari.modifiers.isLazy then vari.lazyValue else expr
            }
            case VariableValue(iden, sel) => {
                val vari = context.tryGetVariable(iden)
                vari match
                    case None => expr
                    case Some(vari) => if vari.modifiers.isLazy then vari.lazyValue else LinkedVariableValue(vari, sel)
            }
            case RangeValue(min, max) => RangeValue(simplify(min), simplify(max))
            case other => other
    }

    def combineJson(elm1: JSONElement, elm2: JSONElement): JSONElement = {
        elm1 match
            case JsonArray(content1) => {
                elm2 match
                    case JsonArray(content2) => JsonArray(content1 ::: content2)
                    case JsonString(value) => JsonArray(content1 ::: List(JsonString(value)))
                    case JsonInt(value) => JsonArray(content1 ::: List(JsonInt(value)))
                    case JsonFloat(value) => JsonArray(content1 ::: List(JsonFloat(value)))
                    case JsonDictionary(value) => JsonArray(content1 ::: List(JsonDictionary(value)))
                    case JsonIdentifier(value) => JsonArray(content1 ::: List(JsonIdentifier(value)))
                    case JsonCall(value, arg) => JsonArray(content1 ::: List(elm2))
                    case _ => throw new Exception(f"Json Element doesn't match ${elm1} vs ${elm2}")
            }
            case JsonDictionary(content1) => {
                elm2 match
                    case JsonDictionary(content2) => JsonDictionary((content1.toList ++ content2.toList).groupBy(_._1).map((k, value) => (k, if value.length == 1 then value.head._2 else combineJson(value(0)._2, value(1)._2))).toMap)
                    case _ => throw new Exception(f"Json Element doesn't match ${elm1} vs ${elm2}")
            }
            case other => elm1
    }

    def toJson(expr: Expression)(implicit context: Context): JSONElement = {
        expr match
            case JsonValue(content) => compileJson(content)
            case StringValue(value) => JsonString(value)
            case IntValue(value) => JsonInt(value)
            case FloatValue(value) => JsonFloat(value)
            case BoolValue(value) => JsonBoolean(value)
            case v => throw new Exception(f"Cannot cast $v to json")
    }

    def compileJson(elm: JSONElement)(implicit context: Context): JSONElement = {
        elm match
            case JsonArray(content) => JsonArray(content.map(compileJson(_)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k, compileJson(v))))
            case JsonCall(value, args) => {
                Settings.target match
                    case MCJava => {
                        val fct = context.getFunction(value, args, VoidType)
                        if (fct._1.modifiers.isLazy){
                            var vari = context.getFreshVariable(fct._1.getType())
                            vari.modifiers.isLazy = true
                            val call = context.getFunction(value, args, VoidType).call(vari)
                            toJson(vari.lazyValue)
                        }
                        else{
                            val fct = context.getFunction(value, args, VoidType).call()
                            if (fct.length == 1){
                                JsonString(fct.last.replaceAll("function ", ""))
                            }
                            else{
                                val block = context.getFreshBlock(fct)
                                JsonString(MCJava.getFunctionName(block.fullName))
                            }
                        }
                    }
                    case MCBedrock => {
                        val fct = context.getFunction(value, args, VoidType)
                        if (fct._1 == null){
                            JsonArray(List())
                        }
                        else if (fct._1.modifiers.isLazy){
                            var vari = context.getFreshVariable(fct._1.getType())
                            vari.modifiers.isLazy = true
                            val call = context.getFunction(value, args, VoidType).call(vari)
                            vari.lazyValue match
                                case JsonValue(content) => compileJson(content)
                                case StringValue(value) => JsonString(value)
                                case IntValue(value) => JsonInt(value)
                                case FloatValue(value) => JsonFloat(value)
                                case BoolValue(value) => JsonBoolean(value)
                                case v => throw new Exception(f"Cannot cast $v (from variable: ${vari.fullName}) to json")
                        }
                        else{
                            JsonArray(fct.call().map(v => JsonString(f"/$v")))
                        }
                    }
            }
            case JsonIdentifier(value) => {
                val vari = context.tryGetVariable(value)
                vari match
                    case Some(value) => {
                        if (value.modifiers.isLazy){
                            value.lazyValue match
                                case IntValue(value) => JsonInt(value)
                                case FloatValue(value) => JsonFloat(value)
                                case BoolValue(value) => JsonBoolean(value)
                                case StringValue(value) => JsonString(value)
                                case JsonValue(content) => compileJson(content)
                                case other => throw new Exception(f"Cannot put not $other in json") 
                        }
                        else{
                            throw new Exception(f"Cannot put not lazy variable ${value.fullName} in json")
                        }
                    }
                    case None => {
                        throw new Exception(f"No value for $value")
                    }
            }
            case JsonFloat(value) => elm
            case JsonBoolean(value) => elm
            case JsonInt(value) => elm
            case JsonString(value) => elm
        
    }

    def compare(op: String, a: String, b: String): Boolean = {
        op match
            case "<"  => a < b
            case "<=" => a <= b
            case "==" => a == b
            case "!=" => a != b
            case ">=" => a >= b
            case ">"  => a > b
    }

    def compare(op: String, a: Double, b: Double): Boolean = {
        op match
            case "<"  => a < b
            case "<=" => a <= b
            case "==" => a == b
            case "!=" => a != b
            case ">=" => a >= b
            case ">"  => a > b
    }

    def compare(op: String, a: Boolean, b: Boolean): Boolean = {
        op match
            case "<"  => a < b
            case "<=" => a <= b
            case "==" => a == b
            case "!=" => a != b
            case ">=" => a >= b
            case ">"  => a > b
    }

    def combine(op: String, a: JSONElement, b: JSONElement): JSONElement = {
        op match
            case "+" => combineJson(a, b)
    }

    def combine(op: String, a: String, b: String): String = {
        op match
            case "+" => a + b
    }

    def combine(op: String, a: Boolean, b: Boolean): Boolean = {
        op match
            case "+" => a || b
            case "-" => a != b
            case "*" => a && b
            case "/" => !a && !b
            case "&&" => a && b
            case "||" => a || b
    }

    def combine(op: String, a: Int, b: Int): Int = {
        op match
            case "+" => a + b
            case "-" => a - b
            case "*" => a * b
            case "/" => a / b
            case "%" => a % b
            case "&&" => if a != 0 && b != 0 then 1 else 0
            case "||" => if a != 0 || b != 0 then 1 else 0
    }

    def combine(op: String, a: Double, b: Double): Double = {
        op match
            case "+" => a + b
            case "-" => a - b
            case "*" => a * b
            case "/" => a / b
            case "%" => a % b
            case "&&" => if a != 0 && b != 0 then 1 else 0
            case "||" => if a != 0 || b != 0 then 1 else 0
    }

    def getForgenerateCases(key: String, provider: Expression)(implicit context: Context): IterableOnce[List[(String, String)]] = {
        simplify(provider) match
            case RangeValue(IntValue(min), IntValue(max)) => Range(min, max+1).map(elm => List((key, elm.toString())))
            case TupleValue(lst) => lst.map(elm => List((key, elm.toString())))
            case VariableValue(iden, sel) if iden.toString().startsWith("@") => {
                context.getFunctionTags(iden).getCompilerFunctionsName().map(name => List((key, name)))
            }
            case VariableValue(iden, sel) => {
                val enm = context.tryGetEnum(iden)
                enm match
                    case Some(value) => value.values.par.map(v => (key, v.name) :: v.fields.zip(value.fields).map((p, f) => (key+"."+f.name, p.getString()))).toList
                    case None => {
                        throw new Exception(f"Unknown generator: $provider")
                    }
            }
            case JsonValue(content) => {
                content match{
                    case JsonArray(content) => content.map(v => List((key, JsonValue(v).getString())))
                    case JsonDictionary(map) => map.map(v => List((key+"."+v._1, JsonValue(v._2).getString())))
                    throw new Exception(f"JSON Generator Not supported: $provider")
                }
            }
            case _ => throw new Exception(f"Unknown generator: $provider")
    }
    def getForeachCases(key: String, provider: Expression)(implicit context: Context): IterableOnce[List[(String, Expression)]] = {
        Utils.simplify(provider) match
            case RangeValue(IntValue(min), IntValue(max)) => Range(min, max+1).map(elm => List((key,IntValue(elm))))
            case TupleValue(lst) => lst.map(elm => List((key, elm)))
            case VariableValue(iden, sel) if iden.toString().startsWith("@") => {
                context.getFunctionTags(iden).getCompilerFunctionsName().map(name => List((key, VariableValue(name))))
            }
            case VariableValue(iden, sel) => {
                val enm = context.tryGetEnum(iden)
                enm match
                    case Some(value) => value.values.par.map(v => (key, VariableValue(value.fullName+"."+ v.name)) :: v.fields.zip(value.fields).map((p, f) => (key+"."+f.name, p))).toList
                    case None => {
                        throw new Exception(f"Unknown generator: $provider")
                    }
            }
            case JsonValue(content) => {
                content match{
                    case JsonArray(content) => content.map(v => List((key, JsonValue(v))))
                    throw new Exception(f"JSON Generator Not supported: $provider")
                }
            }
            case _ => throw new Exception(f"Unknown generator: $provider")
    }
    def getSelector(expr: Expression)(implicit context: Context): (List[String],Selector) = {
        Utils.simplify(expr) match
            case VariableValue(name, sel) => {
                context.tryGetClass(name) match
                    case None => getSelector(context.resolveVariable(expr))
                    case Some(value) => {
                        (List(),JavaSelector("@e", List(("tag", SelectorIdentifier(value.getTag())))))
                    }
            }
            case LinkedVariableValue(vari, sel) => 
                vari.getType() match
                    case EntityType => (List(),JavaSelector("@e", List(("tag", SelectorIdentifier(vari.tagName)))))
                    case _ => throw new Exception(f"Not a selector: $expr")
            case TupleValue(values) => ???
            case SelectorValue(value) => (List(), value)
                
        
            case BinaryOperation(op, left, right) => 
                val (prefix, vari) = Utils.simplifyToVariable(expr)
                val (p2, s) = getSelector(vari)
                (prefix ::: p2, s)
            case _ => throw new Exception(f"Unexpected value in as $expr")
    }
    def getOpFunctionName(op: String)={
        op match{
            case "+=" => "__add__"
            case "-=" => "__sub__"
            case "*=" => "__mul__"
            case "/=" => "__div__"
            case "^=" => "__pow__"
            case "%=" => "__mod__"
            case "&=" => "__and__"
            case "|=" => "__or__"
            case "<"  => "__lt__"
            case "<=" => "__le__"
            case "==" => "__eq__"
            case "!=" => "__ne__"
            case ">"  => "__gt__"
            case ">=" => "__ge__"
        }
    }
    def invertOperator(op: String)={
        op match{
            case "<" => ">="
            case "<=" => ">"
            case ">" => "<="
            case ">=" => "<"
        }
    }
}