package fos

import objects.Identifier
import objects.Context
import objects.types.*
import objects.{Variable, EnumValue, EnumField}

object Utils{
    def stringify(string: String): String = {
        f"\"${string.replaceAllLiterally("\\\\", "\\\\")}\""
    }
    def substReturn(instr: Instruction, to: Variable): Instruction = {
        instr match
            case Package(name, block) => Package(name, substReturn(block, to))
            case StructDecl(name, block, modifier) => StructDecl(name, substReturn(block, to), modifier)
            case FunctionDecl(name, block, typ, args, modifier) => FunctionDecl(name, substReturn(block, to), typ, args, modifier)
            case EnumDecl(name, fields, values, modifier) => instr
            case VariableDecl(name, _type, modifier) => instr
            case JSONFile(name, json) => instr
            
            case InstructionList(list) => InstructionList(list.map(substReturn(_, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(substReturn(_, to)))

            case ElseIf(cond, ifBlock) => ElseIf(cond, substReturn(ifBlock, to))
            case If(cond, ifBlock, elseBlock) => If(cond, substReturn(ifBlock, to), elseBlock.map(substReturn(_,  to).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args) => instr
            case LinkedFunctionCall(name, args, vari) => instr
            case VariableAssigment(name, op, expr) => instr
            case Return(value) => VariableAssigment(List(to.fullName), "=", value)
            case WhileLoop(cond, instr) => WhileLoop(cond, substReturn(instr, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(cond, substReturn(instr, to))

            case At(expr, block) => At(expr, substReturn(block, to))
            case With(expr, isAt, cond, block) => With(expr, isAt, cond, substReturn(block, to))

            case Switch(cond, cases, cv) => Switch(cond, cases.map(x => SwitchCase(x.expr, substReturn(x.instr, to))), cv)
    }


    def subst(instr: Instruction, from: Identifier, to: Identifier): Instruction = {
        instr match
            case Package(name, block) => Package(name, subst(block, from, to))
            case StructDecl(name, block, modifier) => StructDecl(name, subst(block, from, to), modifier)
            case FunctionDecl(name, block, typ, args, modifier) => FunctionDecl(name, subst(block, from, to), typ, args, modifier)
            case VariableDecl(name, _type, modifier) => instr
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(subst(_, from, to)))), modifier)
            case JSONFile(name, json) => instr
            
            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => CMD(value)
            case FunctionCall(name, args) => FunctionCall(name.replaceAllLiterally(from, to), args.map(subst(_, from, to)))
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case VariableAssigment(name, op, expr) => {
                VariableAssigment(name.map(_.replaceAllLiterally(from, to)), op, subst(expr, from, to))
            }
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))

            case At(expr, block) => At(subst(expr, from, to), subst(block, from, to))
            case With(expr, isAt, cond, block) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to))

            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map(x => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to))), cv)
    }

    def subst(instr: Expression, from: Identifier, to: Identifier): Expression = {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case JsonValue(content) => instr
            case StringValue(value) => instr
            case SelectorValue(content) => instr
            case DefaultValue => DefaultValue
            case VariableValue(name) => VariableValue(name.replaceAllLiterally(from, to))
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args) => FunctionCallValue(name.replaceAllLiterally(from, to), args.map(subst(_, from, to)))
            case RangeValue(min, max) => RangeValue(subst(min, from, to), subst(max, from, to))
            case lk: LinkedVariableValue => lk
    }


    def subst(instr: Instruction, from: String, to: String): Instruction = {
        instr match
            case Package(name, block) => Package(name.replaceAllLiterally(from, to), subst(block, from, to))
            case StructDecl(name, block, modifier) => StructDecl(name.replaceAllLiterally(from, to), subst(block, from, to), modifier)
            case FunctionDecl(name, block, typ, args, modifier) => {
                if (args.exists(x => x.name == from)){
                    instr
                }
                else{
                    FunctionDecl(name.replaceAllLiterally(from, to), subst(block, from, to), typ, args, modifier)
                }
            }
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name.replaceAllLiterally(from, to), fields, values.map(v => EnumValue(v.name.replaceAllLiterally(from, to), v.fields.map(subst(_, from, to)))), modifier)
            case VariableDecl(name, _type, modifier) => VariableDecl(name.replaceAllLiterally(from, to), _type, modifier)
            case JSONFile(name, json) => JSONFile(name.replaceAllLiterally(from, to), subst(json, from, to))

            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => CMD(value.replaceAllLiterally(from, to))
            case FunctionCall(name, args) => FunctionCall(name.toString().replaceAllLiterally(from, to), args.map(subst(_, from, to)))
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case VariableAssigment(name, op, expr) => VariableAssigment(name.map(_.toString().replaceAllLiterally(from, to)), op, subst(expr, from, to))
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))

            case At(expr, block) => At(subst(expr, from, to), subst(block, from, to))
            case With(expr, isAt, cond, block) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to))

            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map(x => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to))), cv)
    }

    def subst(instr: Expression, from: String, to: String): Expression = {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case SelectorValue(content) => instr
            case StringValue(value) => StringValue(value.replaceAllLiterally(from, to))
            case DefaultValue => DefaultValue
            case JsonValue(content) => JsonValue(subst(content, from, to))
            case VariableValue(name) => VariableValue(name.toString().replaceAllLiterally(from, to))
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args) => FunctionCallValue(name.replaceAllLiterally(from, to), args.map(subst(_, from, to)))
            case RangeValue(min, max) => RangeValue(subst(min, from, to), subst(max, from, to))
            case lk: LinkedVariableValue => lk
    }

    def subst(json: JSONElement, from: String, to: String): JSONElement = {
        json match{
            case JsonArray(content) => JsonArray(content.map(subst(_, from, to)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k.replaceAllLiterally(from, to), subst(v, from, to))))
            case JsonString(value) => JsonString(value.replaceAllLiterally(from, to))
            case JsonBoolean(value) => JsonBoolean(value)
            case JsonInt(value) => JsonInt(value)
            case JsonFloat(value) => JsonFloat(value)
        }
            
    }


    def subst(instr: Instruction, from: String, to: Expression): Instruction = {
        instr match
            case Package(name, block) => Package(name, subst(block, from, to))
            case StructDecl(name, block, modifier) => StructDecl(name, subst(block, from, to), modifier)
            case FunctionDecl(name, block, typ, args, modifier) => {
                if (args.exists(x => x.name == from)){
                    instr
                }
                else{
                    FunctionDecl(name, subst(block, from, to), typ, args, modifier)
                }
            }
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(subst(_, from, to)))), modifier)
            case VariableDecl(name, _type, modifier) => VariableDecl(name, _type, modifier)

            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args) => FunctionCall(name, args.map(subst(_, from, to)))
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case VariableAssigment(name, op, expr) => VariableAssigment(name, op, subst(expr, from, to))
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))
            case JSONFile(name, json) => instr

            case At(expr, block) => At(subst(expr, from, to), subst(block, from, to))
            case With(expr, isAt, cond, block) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to))

            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map(x => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to))), cv)

    }

    def subst(instr: Expression, from: String, to: Expression): Expression = {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case StringValue(value) => instr
            case JsonValue(content) => instr
            case SelectorValue(content) => instr
            case DefaultValue => DefaultValue
            case VariableValue(name) => if name.toString() == from then to else instr
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args) => FunctionCallValue(name, args.map(subst(_, from, to)))
            case RangeValue(min, max) => RangeValue(subst(min, from, to), subst(max, from, to))
            case lk: LinkedVariableValue => lk
    }


    def simplifyToVariable(expr: Expression)(implicit context: Context): (List[String], LinkedVariableValue) = {
        expr match
            case VariableValue(name) => (List(), LinkedVariableValue(context.getVariable(name)))
            case other => {
                val vari = context.getFreshVariable(typeof(other))
                (vari.assign("=", other), LinkedVariableValue(vari))
            }
    }

    def typeof(expr: Expression)(implicit context: Context): Type = {
        expr match
            case IntValue(value) => IntType
            case FloatValue(value) => FloatType
            case BoolValue(value) => BoolType
            case StringValue(value) => StringType
            case JsonValue(content) => JsonType
            case SelectorValue(content) => EntityType
            case DefaultValue => throw new Exception("default value has no type")
            case VariableValue(name) => context.getVariable(name).getType()
            case BinaryOperation(op, left, right) => combineType(op, typeof(left), typeof(right), expr)
            case TupleValue(values) => TupleType(values.map(typeof(_)))
            case FunctionCallValue(name, args) => context.getFunction(name, args)._1.getType()
            case RangeValue(min, max) => RangeType(typeof(min))
            case LinkedVariableValue(vari) => vari.getType()
    }

    def combineType(op: String, t1: Type, t2: Type, expr: Expression): Type = {
        op match{
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
            }
            case "&&" | "||" => {
                (t1, t2) match
                    case (BoolType, BoolType) => BoolType
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
                    case _ => BinaryOperation(op, nl, nr)
            }
            case LinkedVariableValue(vari) => {
                if vari.modifiers.isLazy then vari.lazyValue else expr
            }
            case VariableValue(iden) => {
                val vari = context.getVariable(iden)
                if vari.modifiers.isLazy then vari.lazyValue else expr
            }
            case other => other
    }

    def compileJson(elm: JSONElement)(implicit context: Context): JSONElement = {
        elm match
            case JsonArray(content) => JsonArray(content.map(compileJson(_)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k, compileJson(v))))
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
}