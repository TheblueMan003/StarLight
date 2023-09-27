package sl

import objects.Identifier
import objects.Context
import objects.types.*
import objects.{Variable, EnumValue, EnumField}
import scala.io.Source
import scala.collection.parallel.CollectionConverters._
import sl.Compilation.Selector.*
import java.io.File
import sys.process._
import sl.IR.*
import objects.CompilerFunction
import java.nio.charset.StandardCharsets
import scala.util.parsing.input.Positional

object Utils{
    val libaries = sl.files.FileUtils.getListOfFiles("./src/main/resources/libraries")
            .map(f => f.replace("\\","/").replace("./src/main/resources/libraries/",""))
            .map(f => (f.toLowerCase -> f))
            .toMap
    def getFile(path: String): String = {
        val source = scala.io.Source.fromFile(path, "UTF-8")
        source.getLines mkString "\n"
    }
    def getFileLines(path: String): List[String] = {
        val source = scala.io.Source.fromFile(path, "UTF-8")
        source.getLines.toList
    }
    def getLibPath(path: String)={
        libaries.get(path.toLowerCase) match
            case Some(p) => p
            case None => path
    }
    def preloadAll():Unit={
        sl.files.FileUtils.getListOfFiles("libraries").filter(p => p.endsWith(".sl")).par.foreach(f => {
            val path = f.replace("/","/").replace("\\","/").dropRight(3)
            Parser.parseFromFile(path, ()=> Utils.getFile(f.replace("/","/").replace("\\","/")))
        })
    }
    def getLib(path: String): Option[Instruction] = {
        val cpath = path.replace(".","/")
        val ipath = path.replace("/",".").replace("\\",".")
        
        Some(Parser.parseFromFile("libraries/"+path, ()=>(
            if (path == "__init__"){
                (Source.fromResource("libraries/"+getLibPath(cpath+".sl"))).getLines.reduce((x,y) => x + "\n" +y)
            }
            else{
                Library.Downloader.getLibrary(path)
            }
            )
        ))
    }
    def getConfig(path: String): List[String] = {
        val projectFile = new File("./configs/"+path)
        if (projectFile.exists())
            Source.fromFile("./configs/"+path).getLines.toList
        else
            Source.fromResource("configs/"+path).getLines.toList
    }
    def getResources(path: String)={
        Source.fromResource(path).getLines.reduce((x,y) => x + "\n" +y)
    }
    def stringify(string: String): String = {
        f"\"${string.replaceAllLiterally("\\\\", "\\\\").replaceAllLiterally("\"", "\\\"")}\""
    }
    def positioned[T <: Positional](original: T, newone: T): T = {
        if (original != null)
            newone.setPos(original.pos)
        newone
    }
    def substReturn(instr: Instruction, to: Variable)(implicit isFullReturn: Boolean, selector: Selector): Instruction = positioned(instr, {
        instr match
            case Package(name, block) => Package(name, substReturn(block, to))
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name, generics, substReturn(block, to), modifier, parent)
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => ClassDecl(name, generics, substReturn(block, to), modifier, parent, parentGenerics, interfaces, entity)
            case FunctionDecl(name, block, typ, args, typeargs, modifier) => FunctionDecl(name, substReturn(block, to), typ, args, typeargs, modifier)
            case PredicateDecl(name, args, block, modifier) => instr
            case TagDecl(name, values, modifier, typ) => instr
            case ForGenerate(key, provider, instr) => ForGenerate(key, provider, substReturn(instr, to))
            case ForEach(key, provider, instr) => ForEach(key, provider, substReturn(instr, to))
            case EnumDecl(name, fields, values, modifier) => instr
            case VariableDecl(name, _type, modifier, op, expr) => instr
            case JSONFile(name, json, mod) => instr
            case Import(lib, value, alias) => instr
            case Throw(expr) => instr
            case Try(block, catchBlock, finallyBlock) => Try(substReturn(block, to), substReturn(catchBlock, to), substReturn(finallyBlock, to))
            
            case InstructionList(list) => InstructionList(list.map(substReturn(_, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(substReturn(_, to)))

            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => TemplateDecl(name, substReturn(block, to), modifier, parent, generics, parentGenerics)
            case TemplateUse(iden, name, instr, values) => TemplateUse(iden, name, substReturn(instr, to), values)
            case TypeDef(defs) => instr

            case ElseIf(cond, ifBlock) => ElseIf(cond, substReturn(ifBlock, to))
            case If(cond, ifBlock, elseBlock) => If(cond, substReturn(ifBlock, to), elseBlock.map(substReturn(_,  to).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args, typeargs) => instr
            case FreeConstructorCall(expr) => instr
            case ArrayAssigment(name, index, op, value) => instr
            case LinkedFunctionCall(name, args, vari) => instr
            case VariableAssigment(name, op, expr) => instr
            case Return(value) => 
                if (isFullReturn){
                    VariableAssigment(List((Left(to.fullName), selector)), "=", value)
                }
                else{
                    InstructionList(List(
                        VariableAssigment(List((Left("__hasFunctionReturned__"), Selector.self)), "=", BoolValue(true)),
                        VariableAssigment(List((Left(to.fullName), selector)), "=", value)
                    ))
                }
            case WhileLoop(cond, instr) => WhileLoop(cond, substReturn(instr, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(cond, substReturn(instr, to))
            case Break => instr
            case Continue => instr

            case Execute(typ, expr, block) => Execute(typ, expr, substReturn(block, to))
            case With(expr, isAt, cond, block, elze) => With(expr, isAt, cond, substReturn(block, to), substReturn(elze, to))

            case Sleep(time, continuation) => Sleep(time, substReturn(continuation, to))
            case Await(func, continuation) => Await(func, substReturn(continuation, to))
            case Assert(cond, continuation) => Assert(cond, substReturn(continuation, to))

            case Switch(cond, cases, cv) => Switch(cond, cases.map{case x: SwitchCase => SwitchCase(x.expr, substReturn(x.instr, to), x.cond);
                                                                    case x: SwitchForGenerate => SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, substReturn(x.instr.instr, to), x.instr.cond));
                                                                    case x: SwitchForEach => SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, substReturn(x.instr.instr, to), x.instr.cond));
                                                                    }, cv)
            case null => null
    })


    def subst(instr: Instruction, from: Identifier, to: Identifier): Instruction = positioned(instr,{
        instr match
            case Package(name, block) => Package(name, subst(block, from, to))
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name, generics, subst(block, from, to), modifier, parent)
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => ClassDecl(name, generics, subst(block, from, to), modifier, parent, parentGenerics, interfaces, entity)
            case FunctionDecl(name, block, typ, args, typeargs, modifier) => FunctionDecl(name, subst(block, from, to), typ, args, typeargs, modifier)
            case PredicateDecl(name, args, block, modifier) => PredicateDecl(name, args, block, modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, _type, modifier, op, subst(expr, from, to))
            case TagDecl(name, values, modifier, typ) => TagDecl(name, values.map(subst(_, from, to)), modifier, typ)
            case ForGenerate(key, provider, instr) => ForGenerate(key, subst(provider, from, to), subst(instr, from, to))
            case ForEach(key, provider, instr) => ForEach(key, subst(provider, from, to), subst(instr, from, to))
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(subst(_, from, to)))), modifier)
            case Throw(expr) => Throw(subst(expr, from, to))
            case Try(block, catchBlock, finallyBlock) => Try(subst(block, from, to), subst(catchBlock, from, to), subst(finallyBlock, from, to))
            case JSONFile(name, json, mod) => instr
            case Import(lib, value, alias) => instr
            
            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => TemplateDecl(name, subst(block, from, to), modifier, parent, generics, parentGenerics.map(subst(_, from, to)))
            case TemplateUse(iden, name, instr, values) => TemplateUse(iden, name, subst(instr, from, to), values.map(subst(_, from, to)))
            case TypeDef(defs) => TypeDef(defs)

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => CMD(value)
            case FunctionCall(name, args, typeargs) => FunctionCall(name.replaceAllLiterally(from, to), args.map(subst(_, from, to)), typeargs)
            case FreeConstructorCall(expr) => FreeConstructorCall(subst(expr, from, to))
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
            case Break => instr
            case Continue => instr

            case Execute(typ, expr, block) => Execute(typ, expr.map(subst(_, from, to)), subst(block, from, to))
            case With(expr, isAt, cond, block, elze) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to), subst(elze, from, to))

            case Sleep(time, continuation) => Sleep(subst(time, from, to), subst(continuation, from, to))
            case Await(func, continuation) => Await(subst(func, from, to).asInstanceOf[FunctionCall], subst(continuation, from, to))
            case Assert(condition, continuation) => Assert(subst(condition, from, to), subst(continuation, from, to))
            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map{case x: SwitchCase => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to), subst(x.cond, from, to));
                                                                                    case x: SwitchForGenerate => SwitchForGenerate(x.key, subst(x.provider, from, to), SwitchCase(subst(x.instr.expr, from, to), subst(x.instr.instr, from, to), subst(x.instr.cond, from, to)));
                                                                                    case x: SwitchForEach => SwitchForEach(x.key, subst(x.provider, from, to), SwitchCase(subst(x.instr.expr, from, to), subst(x.instr.instr, from, to), subst(x.instr.cond, from, to)));
                                                                                    }, cv)
            case null => null
    })
    def subst(vari: Either[Identifier, Variable], from: Identifier, to: Identifier): Either[Identifier, Variable] = {
        vari match
            case Left(value) => Left(value.replaceAllLiterally(from, to))
            case Right(value) => Right(value)
    }

    def subst(instr: Expression, from: Identifier, to: Identifier): Expression = positioned(instr,{
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case JsonValue(content) => instr
            case StringValue(value) => instr
            case RawJsonValue(value) => instr
            case SelectorValue(content) => instr
            case InterpolatedString(content) => InterpolatedString(content.map(subst(_, from, to)))
            case CastValue(value, typ) => CastValue(subst(value, from, to), typ)
            case NamespacedName(value, json) => NamespacedName(value, subst(json, from, to))
            case EnumIntValue(value) => instr
            case LinkedFunctionValue(fct) => instr
            case PositionValue(x, y, z) => PositionValue(subst(x, from, to), subst(y, from, to), subst(z, from, to))
            case TagValue(value) => instr
            case LinkedTagValue(tag) => instr
            case ClassValue(value) => instr
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case IsType(value, typ) => IsType(subst(value, from, to), typ)
            case DotValue(left, right) => DotValue(subst(left, from, to), subst(right, from, to))
            case SequenceValue(left, right) => SequenceValue(subst(left, from, to), subst(right, from, to))
            case ArrayGetValue(name, index) => ArrayGetValue(subst(name, from, to), index.map(subst(_, from, to)))
            case VariableValue(name, sel) => VariableValue(name.replaceAllLiterally(from, to), sel)
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case TernaryOperation(left, middle, right) => TernaryOperation(subst(left, from, to), subst(middle, from, to), subst(right, from, to))
            case UnaryOperation(op, left) => UnaryOperation(op, subst(left, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args, typeargs, selector) => FunctionCallValue(subst(name, from, to), args.map(subst(_, from, to)), typeargs, selector)
            case ConstructorCall(name, args, generics) => ConstructorCall(name, args.map(subst(_, from, to)), generics)
            case RangeValue(min, max, delta) => RangeValue(subst(min, from, to), subst(max, from, to), subst(delta, from, to))
            case LambdaValue(args, instr, ctx) => LambdaValue(args, subst(instr, from, to), ctx)
            case ForSelect(expr, filter, selector) => ForSelect(subst(expr, from, to), filter, subst(selector, from, to))
            case lk: LinkedVariableValue => lk
    })


    def subst(instr: Instruction, from: String, to: String): Instruction = positioned(instr,{
        instr match
            case Package(name, block) => Package(name.replaceAllLiterally(from, to), subst(block, from, to))
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name.replaceAllLiterally(from, to), generics, subst(block, from, to), modifier, parent)
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) =>
                ClassDecl(name.replaceAllLiterally(from, to), generics, subst(block, from, to), modifier, parent, parentGenerics, interfaces, entity.map((k,v) => (k, subst(v, from, to))))
            case FunctionDecl(name, block, typ, args, typeargs, modifier) => {
                if (args.exists(x => x.name == from)){
                    instr
                }
                else{
                    FunctionDecl(name.replaceAllLiterally(from, to), subst(block, from, to), typ, args, typeargs, modifier)
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
            case TagDecl(name, values, modifier, typ) => TagDecl(name.replaceAllLiterally(from, to), values.map(subst(_, from, to)), modifier, typ)
            case Import(lib, value, alias) => instr
            case ForGenerate(key, provider, instr) => ForGenerate(key, subst(provider, from, to), subst(instr, from, to))
            case ForEach(key, provider, instr) => ForEach(key, subst(provider, from, to), subst(instr, from, to))
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name.replaceAllLiterally(from, to), fields, values.map(v => EnumValue(v.name.replaceAllLiterally(from, to), v.fields.map(subst(_, from, to)))), modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name.map(_.replaceAllLiterally(from, to)), _type, modifier, op, subst(expr, from, to))
            case JSONFile(name, json, mod) => JSONFile(name.replaceAllLiterally(from, to), subst(json, from, to), mod)

            case Throw(expr) => Throw(subst(expr, from, to))
            case Try(instr, catchBlock, finallyBlock) => Try(subst(instr, from, to), subst(catchBlock, from, to), subst(finallyBlock, from, to))

            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => TemplateDecl(name.replaceAllLiterally(from, to), subst(block, from, to), modifier, parent, generics, parentGenerics.map(subst(_, from, to)))
            case TemplateUse(iden, name, instr, values) => TemplateUse(iden, name.replaceAllLiterally(from, to), subst(instr, from, to), values.map(subst(_, from, to)))
            case TypeDef(defs) => TypeDef(defs.map{case (name, typ, ver) => (name.replaceAllLiterally(from, to), typ, ver)})

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => CMD(value.replaceAllLiterally(from, to))
            case FunctionCall(name, args, typeargs) => FunctionCall(name.toString().replaceAllLiterally(from, to), args.map(subst(_, from, to)), typeargs)
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case FreeConstructorCall(expr) => FreeConstructorCall(subst(expr, from, to))
            case VariableAssigment(name, op, expr) => VariableAssigment(name.map((l,s) => (subst(l, from, to), s)), op, subst(expr, from, to))
            case ArrayAssigment(name, index, op, value) => {
                ArrayAssigment(subst(name, from, to), index.map(subst(_, from, to)), op, subst(value, from, to))
            }
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))
            case Break => instr
            case Continue => instr

            case Execute(typ, expr, block) => Execute(typ, expr.map(subst(_, from, to)), subst(block, from, to))
            case With(expr, isAt, cond, block, elze) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to), subst(elze, from, to))

            case Sleep(time, continuation) => Sleep(subst(time, from, to), subst(continuation, from, to))
            case Await(func, continuation) => Await(subst(func, from, to).asInstanceOf[FunctionCall], subst(continuation, from, to))
            case Assert(cond, continuation) => Assert(subst(cond, from, to), subst(continuation, from, to))
            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map{case x: SwitchCase => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to), subst(x.cond, from, to));
                                                                                    case x: SwitchForGenerate => SwitchForGenerate(x.key, subst(x.provider, from, to), SwitchCase(subst(x.instr.expr, from, to), subst(x.instr.instr, from, to), subst(x.instr.cond, from, to)));
                                                                                    case x: SwitchForEach => SwitchForEach(x.key, subst(x.provider, from, to), SwitchCase(subst(x.instr.expr, from, to), subst(x.instr.instr, from, to), subst(x.instr.cond, from, to)));
                                                                                    }, cv)
            case null => null
    })
    def subst(vari: Either[Identifier, Variable], from: String, to: String): Either[Identifier, Variable] = {
        vari match
            case Left(value) => Left(value.toString().replaceAllLiterally(from, to))
            case Right(value) => Right(value)
    }

    def subst(instr: Expression, from: String, to: String): Expression = positioned(instr, {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case SelectorValue(content) => SelectorValue(content.subst(from,to))
            case InterpolatedString(content) => InterpolatedString(content.map(subst(_, from, to)))
            case NamespacedName(value, json) => NamespacedName(value.replaceAllLiterally(from, to), subst(json, from, to))
            case StringValue(value) => StringValue(value.replaceAllLiterally(from, to))
            case PositionValue(x, y, z) => PositionValue(subst(x, from, to), subst(y, from, to), subst(z, from, to))
            case TagValue(value) => TagValue(value.replaceAllLiterally(from, to))
            case LinkedTagValue(tag) => instr
            case DotValue(left, right) => DotValue(subst(left, from, to), subst(right, from, to))
            case SequenceValue(left, right) => SequenceValue(subst(left, from, to), subst(right, from, to))
            case RawJsonValue(value) => instr
            case EnumIntValue(value) => instr
            case ClassValue(value) => instr
            case LinkedFunctionValue(fct) => instr
            case CastValue(value, typ) => CastValue(subst(value, from, to), typ)
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case IsType(left, right) => IsType(subst(left, from, to), right)
            case ArrayGetValue(name, index) => ArrayGetValue(subst(name, from, to), index.map(subst(_, from, to)))
            case JsonValue(content) => JsonValue(subst(content, from, to))
            case VariableValue(name, sel) => VariableValue(name.toString().replaceAllLiterally(from, to), sel)
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case TernaryOperation(left, middle, right) => TernaryOperation(subst(left, from, to), subst(middle, from, to), subst(right, from, to))
            case UnaryOperation(op, left) => UnaryOperation(op, subst(left, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args, typeargs, selector) => FunctionCallValue(subst(name, from, to), args.map(subst(_, from, to)), typeargs, selector)
            case ConstructorCall(name, args, generics) => ConstructorCall(name, args.map(subst(_, from, to)), generics)
            case RangeValue(min, max, delta) => RangeValue(subst(min, from, to), subst(max, from, to), subst(delta, from, to))
            case LambdaValue(args, instr, ctx) => LambdaValue(args.map(_.replaceAllLiterally(from, to)), subst(instr, from, to), ctx)
            case lk: LinkedVariableValue => lk
            case ForSelect(expr, filter, selector) => ForSelect(subst(expr, from, to), filter.replaceAllLiterally(from, to), subst(selector, from, to))
            case null => null
    })

    def subst(json: JSONElement, from: String, to: String): JSONElement = {
        json match{
            case JsonArray(content) => JsonArray(content.map(subst(_, from, to)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k.replaceAllLiterally(from, to), subst(v, from, to))))
            case JsonString(value) => JsonString(value.replaceAllLiterally(from, to))
            case JsonBoolean(value) => JsonBoolean(value)
            case JsonInt(value, t) => JsonInt(value, t)
            case JsonFloat(value, t) => JsonFloat(value, t)
            case JsonIdentifier(value, t) => JsonIdentifier(value.replaceAllLiterally(from, to), t)
            case JsonExpression(value, t) => JsonExpression(subst(value, from, to), t)
            case JsonNull => JsonNull
        } 
    }

    def subst(typ: Type, from: String, to: Expression): Type = {
        typ match
            case FuncType(args, ret) => FuncType(args.map(subst(_, from, to)), subst(ret, from, to))
            case TupleType(values) => TupleType(values.map(subst(_, from, to)))
            case ArrayType(value, size) => ArrayType(subst(value, from, to), subst(size, from, to))
            case other => other
    }
    def subst(instr: Instruction, from: String, to: Expression): Instruction = positioned(instr, {
        instr match
            case Package(name, block) => Package(name, subst(block, from, to))
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name, generics, subst(block, from, to), modifier, parent)
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => ClassDecl(name, generics, subst(block, from, to), modifier, parent, parentGenerics, interfaces, entity)
            case FunctionDecl(name, block, typ, args, typeargs, modifier) => {
                if (args.exists(x => x.name == from)){
                    instr
                }
                else{
                    FunctionDecl(name, subst(block, from, to), typ, args, typeargs, modifier)
                }
            }
            case PredicateDecl(name, args, block, modifier) => instr
            case TagDecl(name, values, modifier, typ) => TagDecl(name, values.map(subst(_, from, to)), modifier, typ)
            case Import(lib, value, alias) => instr
            case ForGenerate(key, provider, instr) => ForGenerate(key, subst(provider, from, to), subst(instr, from, to))
            case ForEach(key, provider, instr) => ForEach(key, subst(provider, from, to), subst(instr, from, to))
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(subst(_, from, to)))), modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, subst(_type, from, to), modifier, op, subst(expr, from, to))
            case Throw(expr) => Throw(subst(expr, from, to))
            case Try(instr, catchBlock, finallyBlock) => Try(subst(instr, from, to), subst(catchBlock, from, to), subst(finallyBlock, from, to))

            case InstructionList(list) => InstructionList(list.map(subst(_, from, to)))
            case InstructionBlock(list) => InstructionBlock(list.map(subst(_, from, to)))

            case TypeDef(defs) => TypeDef(defs)

            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => TemplateDecl(name, subst(block, from, to), modifier, parent, generics, parentGenerics.map(subst(_, from, to)))
            case TemplateUse(iden, name, instr, values) => TemplateUse(iden, name, subst(instr, from, to), values.map(subst(_, from, to)))

            case ElseIf(cond, ifBlock) => ElseIf(subst(cond, from, to), subst(ifBlock, from, to))
            case If(cond, ifBlock, elseBlock) => If(subst(cond, from, to), subst(ifBlock, from, to), elseBlock.map(subst(_, from, to).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args, typeargs) => FunctionCall(name, args.map(subst(_, from, to)), typeargs)
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(subst(_, from, to)), vari)
            case FreeConstructorCall(expr) => FreeConstructorCall(subst(expr, from, to))
            case VariableAssigment(name, op, expr) => VariableAssigment(name, op, subst(expr, from, to))
            case ArrayAssigment(name, index, op, value) => {
                ArrayAssigment(name, index.map(subst(_, from, to)), op, subst(value, from, to))
            }
            case Return(value) => Return(subst(value, from, to))
            case WhileLoop(cond, instr) => WhileLoop(subst(cond, from, to), subst(instr, from, to))
            case DoWhileLoop(cond, instr) => DoWhileLoop(subst(cond, from, to), subst(instr, from, to))
            case Break => instr
            case Continue => instr
            case JSONFile(name, json, mod) => instr

            case Execute(typ, expr, block) => Execute(typ, expr.map(subst(_, from, to)), subst(block, from, to))
            case With(expr, isAt, cond, block, elze) => With(subst(expr, from, to), subst(isAt, from, to), subst(cond, from, to), subst(block, from, to), subst(elze, from, to))
            case Sleep(time, continuation) => Sleep(subst(time, from, to), subst(continuation, from, to))
            case Await(func, continuation) => Await(subst(func, from, to).asInstanceOf[FunctionCall], subst(continuation, from, to))
            case Assert(cond, continuation) => Assert(subst(cond, from, to), subst(continuation, from, to))
            case Switch(cond, cases, cv) => Switch(subst(cond, from, to), cases.map{case x: SwitchCase => SwitchCase(subst(x.expr, from, to), subst(x.instr, from, to), x.cond);
                                                                                    case x: SwitchForGenerate => SwitchForGenerate(x.key, subst(x.provider, from, to), SwitchCase(subst(x.instr.expr, from, to), subst(x.instr.instr, from, to), x.instr.cond));
                                                                                    case x: SwitchForEach => SwitchForEach(x.key, subst(x.provider, from, to), SwitchCase(subst(x.instr.expr, from, to), subst(x.instr.instr, from, to), x.instr.cond));
                                                                                    }, cv)
            case null => null
    })

    def rmFunctions(instr: Instruction)(implicit predicate: FunctionDecl=>Boolean = _ => true): Instruction = positioned(instr, {
        instr match
            case Package(name, block) => Package(name, rmFunctions(block))
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name, generics, rmFunctions(block), modifier, parent)
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => ClassDecl(name, generics, rmFunctions(block), modifier, parent, parentGenerics, interfaces, entity)
            case fct: FunctionDecl => if predicate(fct) then InstructionList(List()) else instr
            case PredicateDecl(name, args, block, modifier) => instr
            case TagDecl(name, values, modifier, typ) => instr
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values, modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, _type, modifier, op, expr)
            case ForGenerate(key, provider, instr) => ForGenerate(key, provider, rmFunctions(instr))
            case ForEach(key, provider, instr) => ForEach(key, provider, rmFunctions(instr))
            case Import(lib, value, alias) => instr

            case InstructionList(list) => InstructionList(list.map(rmFunctions(_)))
            case InstructionBlock(list) => InstructionBlock(list.map(rmFunctions(_)))
            case TypeDef(defs) => instr

            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => TemplateDecl(name, rmFunctions(block), modifier, parent, generics, parentGenerics)
            case TemplateUse(iden, name, instr, values) => TemplateUse(iden, name, rmFunctions(instr), values)

            case ElseIf(cond, ifBlock) => ElseIf(cond, rmFunctions(ifBlock))
            case If(cond, ifBlock, elseBlock) => If(cond, rmFunctions(ifBlock), elseBlock.map(rmFunctions(_).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args, typeargs) => instr
            case LinkedFunctionCall(name, args, vari) => instr
            case FreeConstructorCall(expr) => instr
            case VariableAssigment(name, op, expr) => instr
            case ArrayAssigment(name, index, op, value) => instr
            case Return(value) => instr
            case WhileLoop(cond, instr) => WhileLoop(cond, rmFunctions(instr))
            case DoWhileLoop(cond, instr) => DoWhileLoop(cond, rmFunctions(instr))
            case Break => instr
            case Continue => instr
            case JSONFile(name, json, mod) => instr

            case Throw(expr) => instr
            case Try(block, except, finallyBlock) => Try(rmFunctions(block), rmFunctions(except), rmFunctions(finallyBlock))


            case Execute(typ, expr, block) => Execute(typ, expr, rmFunctions(block))
            case With(expr, isAt, cond, block, elze) => With(expr, isAt, cond, rmFunctions(block), rmFunctions(elze))
            case Sleep(time, continuation) => Sleep(time, rmFunctions(continuation))
            case Await(func, continuation) => Await(func, rmFunctions(continuation))
            case Assert(cond, continuation) => Assert(cond, rmFunctions(continuation))
            case Switch(cond, cases, cv) => Switch(cond, cases.map{case x: SwitchCase => SwitchCase(x.expr, rmFunctions(x.instr), x.cond);
                                                                    case x: SwitchForGenerate => SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, rmFunctions(x.instr.instr), x.instr.cond));
                                                                    case x: SwitchForEach => SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, rmFunctions(x.instr.instr), x.instr.cond));
                                                                    }, cv)
            case null => null
    })

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

    def fix(instr: Instruction)(implicit context: Context, ignore: Set[Identifier]): Instruction = positioned(instr, {
        instr match
            case Package(name, block) => Package(name, fix(block))
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name, generics, fix(block), modifier, parent)
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => ClassDecl(name, generics, fix(block), modifier, parent, parentGenerics.map(fix), interfaces.map(x => (x._1, x._2.map(fix(_)))), entity)
            case FunctionDecl(name, block, typ, args, typeargs, modifier) => FunctionDecl(name, fix(block)(context, ignore ++ args.map(a => Identifier.fromString(a.name)).toSet), typ, args, typeargs, modifier)
            case PredicateDecl(name, args, block, modifier) => PredicateDecl(name, args, fix(block), modifier)
            case EnumDecl(name, fields, values, modifier) => EnumDecl(name, fields, values.map(v => EnumValue(v.name, v.fields.map(fix(_)))), modifier)
            case VariableDecl(name, _type, modifier, op, expr) => VariableDecl(name, fix(_type), modifier, op, fix(expr))
            case TagDecl(name, values, modifier, typ) => TagDecl(name, values.map(fix(_)), modifier, typ)
            case ForGenerate(key, provider, instr) => ForGenerate(key, fix(provider), fix(instr))
            case ForEach(key, provider, instr) => ForEach(key, fix(provider), fix(instr))
            case Import(lib, value, alias) => instr

            case InstructionList(list) => {
                val set2 = getFreeVar(instr) ++ ignore
                InstructionList(list.map(fix(_)(context, set2)))
            }
            case InstructionBlock(list) => {
                val set2 = getFreeVar(instr) ++ ignore
                InstructionBlock(list.map(fix(_)(context, set2)))
            }

            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => TemplateDecl(name, fix(block), modifier, parent, generics, parentGenerics.map(fix(_)(context, ignore ++ generics.map(Identifier.fromString(_)).toSet)))
            case TemplateUse(iden, name, instr, values) => TemplateUse(iden, name, fix(instr), values.map(fix(_)))
            case TypeDef(defs) => TypeDef(defs.map{case (name, typ, ver) => (name, fix(typ), ver)})

            case ElseIf(cond, ifBlock) => ElseIf(fix(cond), fix(ifBlock))
            case If(cond, ifBlock, elseBlock) => If(fix(cond), fix(ifBlock), elseBlock.map(fix(_).asInstanceOf[ElseIf]))
            case CMD(value) => instr
            case FunctionCall(name, args, typeargs) => if ignore.contains(name) || name.toString().startsWith("@") then FunctionCall(name, args.map(fix(_)), typeargs.map(fix(_))) else{ 
                val argF = args.map(fix(_))
                context.tryGetFunction(name, argF, typeargs, VoidType, false, true) match{
                    case Some((fct, vari)) => return LinkedFunctionCall(fct, vari)
                    case None => FunctionCall(name, argF, typeargs)
                }
            }
            case LinkedFunctionCall(name, args, vari) => LinkedFunctionCall(name, args.map(fix(_)), vari)
            case FreeConstructorCall(expr) => FreeConstructorCall(fix(expr))
            case VariableAssigment(name, op, expr) => VariableAssigment(name.map{case (v, s) => (fix(v),s)}, op, fix(expr))
            case ArrayAssigment(name, index, op, expr) => ArrayAssigment(fix(name), index.map(fix(_)), op, fix(expr))
            case Return(value) => Return(fix(value))
            case WhileLoop(cond, instr) => WhileLoop(fix(cond), fix(instr))
            case DoWhileLoop(cond, instr) => DoWhileLoop(fix(cond), fix(instr))
            case Break => instr
            case Continue => instr
            case JSONFile(name, json, mod) => instr

            case Throw(expr) => Throw(fix(expr))
            case Try(block, catches, finalBlock) => Try(fix(block), fix(catches), fix(finalBlock))

            case Execute(typ, expr, block) => Execute(typ, expr.map(fix(_)), fix(block))
            case With(expr, isAt, cond, block, elze) => With(fix(expr), fix(isAt), fix(cond), fix(block), fix(elze))
            case Sleep(time, continuation) => Sleep(fix(time), fix(continuation))
            case Await(func, continuation) => Await(fix(func).asInstanceOf[FunctionCall], fix(continuation))
            case Assert(cond, continuation) => Assert(fix(cond), fix(continuation))
            case Switch(cond, cases, cv) => Switch(fix(cond), cases.map{case x: SwitchCase => SwitchCase(fix(x.expr), fix(x.instr), fix(x.cond));
                                                                        case x: SwitchForGenerate => SwitchForGenerate(x.key, fix(x.provider), SwitchCase(fix(x.instr.expr), fix(x.instr.instr), fix(x.instr.cond)));
                                                                        case x: SwitchForEach => SwitchForEach(x.key, fix(x.provider), SwitchCase(fix(x.instr.expr), fix(x.instr.instr), fix(x.instr.cond)));
                                                                        }, cv)
            case null => null
    })
    def fix(typ: Type)(implicit context: Context, ignore: Set[Identifier]): Type = {
        typ match
            case TupleType(sub) => TupleType(sub.map(fix(_)))
            case ArrayType(inner, size) => ArrayType(fix(inner), size)
            case RangeType(sub) => RangeType(fix(sub))
            case IdentifierType("val", _) => typ
            case IdentifierType("var", _) => typ
            case IdentifierType(name, generics) => {
                context.getType(typ)
            }
            case other => other
        
    }
    def fix(instr: Expression)(implicit context: Context, ignore: Set[Identifier]): Expression = positioned(instr, {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case SelectorValue(content) => SelectorValue(content.fix)
            case InterpolatedString(content) => InterpolatedString(content.map(fix(_)))
            case NamespacedName(value, json) => NamespacedName(value, fix(json))
            case StringValue(value) => instr
            case RawJsonValue(value) => instr
            case EnumIntValue(value) => instr
            case ClassValue(value) => instr
            case CastValue(value, typ) => CastValue(fix(value), fix(typ))
            case LinkedFunctionValue(fct) => instr
            case TagValue(value) => {
                val vari = context.tryGetBlockTag(value)
                vari match
                    case None => instr
                    case Some(tag) => LinkedTagValue(tag)
            }
            case LinkedTagValue(tag) => instr
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case IsType(left, right) => IsType(fix(left), fix(right))
            case PositionValue(x, y, z) => PositionValue(fix(x), fix(y), fix(z))
            case DotValue(left, right) => DotValue(fix(left), fix(right))
            case SequenceValue(left, right) => SequenceValue(fix(left), fix(right))
            case JsonValue(content) => JsonValue(fix(content))
            case ArrayGetValue(name, index) => ArrayGetValue(fix(name), index.map(fix(_)))
            case VariableValue(name, sel) => if ignore.contains(name) then instr else
                context.tryGetVariable(name) match
                    case Some(vari) => LinkedVariableValue(vari, sel)
                    case None => 
                        try{
                            LinkedFunctionValue(context.getFunction(name))
                        }
                        catch{
                            case _ => VariableValue(name, sel)
                        }
            case BinaryOperation(op, left, right) => BinaryOperation(op, fix(left), fix(right))
            case TernaryOperation(left, middle, right) => TernaryOperation(fix(left), fix(middle), fix(right))
            case UnaryOperation(op, left) => UnaryOperation(op, fix(left))
            case TupleValue(values) => TupleValue(values.map(fix(_)))
            case FunctionCallValue(name, args, typeargs, sel) => FunctionCallValue(fix(name), args.map(fix(_)), typeargs.map(fix(_)), sel)
            case ConstructorCall(name, args, generics) => 
                try{
                if ignore.contains(name) then ConstructorCall(name, args.map(fix(_)), generics) else
                context.getType(IdentifierType(name.toString(), generics)) match
                    case StructType(struct, generics) => ConstructorCall(struct.fullName, args.map(fix(_)), generics.map(fix(_)))
                    case ClassType(clazz, generics) => ConstructorCall(clazz.fullName, args.map(fix(_)), generics.map(fix(_)))
                    case other => throw new Exception(f"Cannot constructor call $other")
                }
                catch{
                    case e: Exception => ConstructorCall(name, args.map(fix(_)), generics)
                }
            case RangeValue(min, max, delta) => RangeValue(fix(min), fix(max), fix(delta))
            case LambdaValue(args, instr, ctx) => LambdaValue(args, fix(instr), ctx)
            case lk: LinkedVariableValue => lk
            case ForSelect(expr, filter, selector) => ForSelect(fix(expr), filter, fix(selector))
            case null => null
    })
    def fix(json: JSONElement)(implicit context: Context, ignore: Set[Identifier]): JSONElement = {
        json match{
            case JsonArray(content) => JsonArray(content.map(fix(_)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k, fix(v))))
            case JsonString(value) => JsonString(value)
            case JsonBoolean(value) => JsonBoolean(value)
            case JsonInt(value, t) => JsonInt(value, t)
            case JsonFloat(value, t) => JsonFloat(value, t)
            case JsonIdentifier(value, t) => {
                if ignore.contains(Identifier.fromString(value)) then JsonIdentifier(value, t) else
                context.tryGetVariable(Identifier.fromString(value)) match{
                    case Some(vari) if vari.canBeReduceToLazyValue => toJson(vari.lazyValue, t)
                    case _ => JsonIdentifier(value, t)
                }
            }
            case JsonExpression(VariableValue(value, sel), t) => {
                if ignore.contains(value) then JsonIdentifier(value.toString(), t) else
                context.tryGetVariable(value) match{
                    case Some(vari) if vari.canBeReduceToLazyValue => toJson(vari.lazyValue, t)
                    case Some(vari) => JsonExpression(LinkedVariableValue(vari, sel), t)
                    case _ => JsonExpression(VariableValue(value, sel), t)
                }
            }
            case JsonExpression(value, t) => JsonExpression(fix(value), t)
            case JsonNull => JsonNull
        } 
    }

    def subst(instr: Expression, from: String, to: Expression): Expression = positioned(instr, {
        instr match
            case IntValue(value) => instr
            case FloatValue(value) => instr
            case BoolValue(value) => instr
            case StringValue(value) => instr
            case RawJsonValue(value) => instr
            case JsonValue(content) => instr
            case SelectorValue(content) => instr
            case InterpolatedString(content) => InterpolatedString(content.map(subst(_, from, to)))
            case NamespacedName(value, json) => NamespacedName(value, subst(json, from, to))
            case EnumIntValue(value) => instr
            case CastValue(value, typ) => CastValue(subst(value, from, to), typ)
            case ClassValue(value) => instr
            case LinkedFunctionValue(fct) => instr
            case PositionValue(x, y, z) => PositionValue(subst(x, from, to), subst(y, from, to), subst(z, from, to))
            case TagValue(value) => instr
            case LinkedTagValue(tag) => instr
            case IsType(left, right) => IsType(subst(left, from, to), right)
            case ArrayGetValue(name, index) => ArrayGetValue(subst(name, from, to), index.map(subst(_, from, to)))
            case DefaultValue => DefaultValue
            case NullValue => NullValue
            case DotValue(left, right) => DotValue(subst(left, from, to), subst(right, from, to))
            case VariableValue(name, sel) => if name.toString() == from then to else instr
            case BinaryOperation(op, left, right) => BinaryOperation(op, subst(left, from, to), subst(right, from, to))
            case TernaryOperation(left, middle, right) => TernaryOperation(subst(left, from, to), subst(middle, from, to), subst(right, from, to))
            case UnaryOperation(op, left) => UnaryOperation(op, subst(left, from, to))
            case TupleValue(values) => TupleValue(values.map(subst(_, from, to)))
            case FunctionCallValue(name, args, typeargs, selector) => FunctionCallValue(name, args.map(subst(_, from, to)), typeargs, selector)
            case ConstructorCall(name, args, generics) => ConstructorCall(name, args.map(subst(_, from, to)), generics)
            case RangeValue(min, max, delta) => RangeValue(subst(min, from, to), subst(max, from, to), subst(delta, from, to))
            case LambdaValue(args, instr, ctx) => LambdaValue(args, subst(instr, from, to), ctx)
            case lk: LinkedVariableValue => lk
            case ForSelect(expr, filter, selector) => ForSelect(subst(expr, from, to), filter, subst(selector, from, to))
            case null => null
    })


    def unpackDotValue(dot: DotValue)(implicit context: Context): (List[IRTree], Expression) = {
        dot match
            case DotValue(left, VariableValue(v, sel)) => {
                val (list, vari) = simplifyToVariable(left)
                (list, VariableValue(vari.vari.fullName+"."+v.toString(), sel))
            }
            case DotValue(left, FunctionCallValue(v, args, typeargs, sel)) => {
                val (list, vari) = simplifyToVariable(left)
                (list, FunctionCallValue(VariableValue(vari.vari.fullName+"."+v.toString()), args, typeargs, sel))
            }
            case DotValue(left, ArrayGetValue(VariableValue(v, sel), index)) => {
                val (list, vari) = simplifyToVariable(left)
                (list, ArrayGetValue(VariableValue(vari.vari.fullName+"."+v.toString(), sel), index))
            }
            case DotValue(left, ArrayGetValue(FunctionCallValue(v, args, typeargs, sel), index)) => {
                val (list, vari) = simplifyToVariable(left)
                (list, ArrayGetValue(FunctionCallValue(VariableValue(vari.vari.fullName+"."+v.toString()), args, typeargs, sel), index))
            }
            case DotValue(left, right) => throw new Exception(f"Cannot unpack $dot")

    }
    def simplifyToVariable(expr: Expression)(implicit context: Context): (List[IRTree], LinkedVariableValue) = {
        expr match
            case VariableValue(name, sel) => simplifyToVariable(context.resolveVariable(expr))
            case LinkedVariableValue(name, sel) => (List(), LinkedVariableValue(name, sel))
            case instr @ VariableAssigment(name, op, expr) => {
                val prev = Compiler.compile(instr)
                name match{
                    case (Left(vari), sel) :: Nil => (prev,LinkedVariableValue(context.getVariable(vari), sel))
                    case (Right(vari), sel) :: Nil => (prev,LinkedVariableValue(vari, sel))
                    case lst => {
                        val vari = context.getFreshVariable(typeof(expr))
                        (prev ::: vari.assign("=", expr), LinkedVariableValue(vari))
                    }
                }
            }
            case other => {
                val vari = context.getFreshVariable(typeof(other))
                (vari.assign("=", other), LinkedVariableValue(vari))
            }
    }
    def simplifyToLazyVariable(expr: Expression)(implicit context: Context): (List[IRTree], LinkedVariableValue) = {
        expr match
            case VariableValue(name, sel) => simplifyToLazyVariable(context.resolveVariable(expr))
            case LinkedVariableValue(name, sel) => (List(), LinkedVariableValue(name, sel))
            case FunctionCallValue(VariableValue(name, sel), args, typeargs, selector) => {
                val vari = context.getFreshVariable(typeof(expr))

                val fct = context.getFunction(name, args, typeargs, VoidType)

                vari.modifiers.isLazy = !fct._1.modifiers.hasAttributes("requiresVariable")
                
                (fct.call(vari), LinkedVariableValue(vari))
            }
            case FunctionCallValue(LinkedFunctionValue(fct), args, typeargs, selector) => {
                val vari = context.getFreshVariable(typeof(expr))

                vari.modifiers.isLazy = !fct.modifiers.hasAttributes("requiresVariable")
                ((fct,args).call(vari), LinkedVariableValue(vari))
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
            case InterpolatedString(content) => RawJsonType
            case LambdaValue(args, instr, ctx) => LambdaType(args.length)
            case EnumIntValue(value) => IntType
            case ClassValue(value) => TypeType
            case NamespacedName(value, json) => MCObjectType
            case PositionValue(x, y, z) => MCPositionType
            case TagValue(value) => MCObjectType
            case LinkedTagValue(value) => MCObjectType
            case IsType(left, right) => BoolType
            case CastValue(value, typ) => typ
            case dot : DotValue => {
                val (list, vari) = unpackDotValue(dot)
                typeof(vari)
            }
            case SequenceValue(left, right) => typeof(right)
            case ArrayGetValue(name, index) => {
                typeof(name) match
                    case ArrayType(inner, size) => inner
                    case MCObjectType => MCObjectType
                    case JsonType => JsonType
                    case TupleType(sub) => 
                        index match{
                            case IntValue(value) :: Nil => sub(value)
                            case head::Nil => sub.foldRight(sub.head)((a,b) => combineType(a, b))
                            case other => throw new Exception(f"Cannot access tuple with $other")
                        }
                    case other => throw new Exception(f"Illegal array access of $name of type $other")
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
            case TernaryOperation(left, middle, right) => typeof(middle)
            case UnaryOperation(op, left) => BoolType
            case TupleValue(values) => TupleType(values.map(typeof(_)))
            case FunctionCallValue(name, args, typeargs, selector) => {
                try{
                    name match
                        case VariableValue(name, sel) => context.getFunction(name, args, typeargs, AnyType)._1.getType()
                        case other => typeof(name) match
                            case FuncType(sources, output) => output
                            case other => throw new Exception(f"Cannot call $other")
                }catch{
                    _ => AnyType
                }
            }
            case ConstructorCall(name, args, generics) => {
                context.getType(IdentifierType(name.toString(), generics), args)
            }
            case RangeValue(min, max, delta) => RangeType(typeof(min))
            case LinkedVariableValue(vari, sel) => vari.getType()
            case ForSelect(expr, filter, selector) => BoolType
    }
    def forceString(expr: Expression)(implicit context: Context): String = {
        expr match
            case IntValue(value) => value.toString()
            case FloatValue(value) => value.toString()
            case StringValue(value) => value
            case JsonValue(IntValue(value)) => value.toString()
            case JsonValue(FloatValue(value)) => value.toString()
            case JsonValue(StringValue(value)) => value
            case VariableValue(name, sel) => name.toString()
            case BinaryOperation("+", left, right) => forceString(left) + forceString(right)
            case UnaryOperation("-", left) => "-"+forceString(left)
            case other => throw new Exception(f"Cannot convert $other to string")
    }

    def combineType(op: String, t1: Type, t2: Type, expr: Expression): Type = {
        op match{
            case "not in" => EntityType
            case "in" if t1 == EntityType => EntityType
            case "in" => BoolType
            case "==" | "<=" | "<" | ">" | ">=" => BoolType
            case "??" => t1
            case "::" if t1 == JsonType || t2 == JsonType => JsonType
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
                    case (IntType | FloatType | BoolType | JsonType, StringType) => StringType
                    case (StringType, IntType | FloatType | BoolType | JsonType) => StringType
                    case (StructType(struct, generics), other) => StructType(struct, generics)
                    case (other, StructType(struct, generics)) => StructType(struct, generics)
                    case (ClassType(name, generics), other) => ClassType(name, generics)
                    case (other, ClassType(name, generics)) => ClassType(name, generics)
            }
            case "&&" | "||" => {
                (t1, t2) match
                    case (BoolType | IntType | FloatType, BoolType | IntType | FloatType) => BoolType
                    case (a, b) => throw new Exception(f"Unexpect type in ${expr} found $a and $b, exptected: bool and bool") 
            }
            case _ => throw new Exception(f"Unexpect operator in ${expr} found $op")
        }
    }

    private def isPowerOfTwo(value: Int): Boolean = {
        value != 0 && (value & (value - 1)) == 0
    }
    def containsFunctionCall(expr: Expression): Boolean = {
        expr match{
            case FunctionCallValue(_, _, _, _) => true
            case BinaryOperation(_, left, right) => containsFunctionCall(left) || containsFunctionCall(right)
            case TernaryOperation(left, middle, right) => containsFunctionCall(left) || containsFunctionCall(middle) || containsFunctionCall(right)
            case UnaryOperation(_, left) => containsFunctionCall(left)
            case TupleValue(values) => values.exists(containsFunctionCall(_))
            case ArrayGetValue(name, index) => containsFunctionCall(name)
            case ConstructorCall(name, args, generics) => true
            case RangeValue(min, max, delta) => containsFunctionCall(min) || containsFunctionCall(max) || containsFunctionCall(delta)
            case _ => false
        }
    }

    def jsonToExpr(json: JSONElement)(implicit context: Context): Expression = {
        json match
            case JsonInt(value, t) => IntValue(value)
            case JsonFloat(value, t) => FloatValue(value)
            case JsonBoolean(value) => BoolValue(value)
            case JsonString(value) => StringValue(value)
            case JsonNull => NullValue
            case JsonArray(values) => JsonValue(json)
            case JsonExpression(value, _) => value
            case JsonDictionary(values) => JsonValue(json)
            case JsonIdentifier(value, t) => VariableValue(value)
    }

    def simplifyJsonExpression(j: JsonExpression): JSONElement = {
        j match
            case JsonExpression(JsonValue(value), t) => value
            case JsonExpression(StringValue(value), t) => JsonString(value)
            case JsonExpression(IntValue(value), t) => JsonInt(value, t)
            case JsonExpression(FloatValue(value), t) => JsonFloat(value, t)
            case JsonExpression(BoolValue(value), t) => JsonBoolean(value)
            case JsonExpression(NullValue, t) => JsonNull
            case other => other
    }
    def contains(instr: SwitchElement, predicate: Instruction=>Boolean): Boolean = { 
        instr match
            case SwitchCase(expr, instr, cond) => contains(instr, predicate) || predicate(instr)
            case SwitchForGenerate(key, provider, instr) => contains(instr, predicate) || predicate(instr.instr)
            case SwitchForEach(key, provider, instr) => contains(instr, predicate) || predicate(instr.instr)
    }
    def contains(instr: Instruction, predicate: Instruction=>Boolean): Boolean = {
        instr match
            case InstructionList(list) => list.exists(contains(_, predicate)) || predicate(instr)
            case InstructionBlock(list) => list.exists(contains(_, predicate)) || predicate(instr)
            case If(cond, ifBlock, elseBlock) => contains(ifBlock, predicate) || elseBlock.exists(contains(_, predicate)) || predicate(instr)
            case WhileLoop(cond, block) => contains(block, predicate) || predicate(instr)
            case DoWhileLoop(cond, block) => contains(block, predicate) || predicate(instr)
            case Switch(cond, cases, cv) => cases.exists(contains(_, predicate)) || predicate(instr)
            case Try(block, catches, finalBlock) => contains(block, predicate) || contains(catches, predicate) || contains(finalBlock, predicate) || predicate(instr)
            case ForGenerate(key, provider, instr) => contains(instr, predicate) || predicate(instr)
            case ForEach(key, provider, instr) => contains(instr, predicate) || predicate(instr)
            case With(expr, isAt, cond, block, elze) => contains(block, predicate) || contains(elze, predicate) || predicate(instr)
            case Execute(typ, expr, block) => contains(block, predicate) || predicate(instr)
            case TemplateUse(iden, name, instr, values) => contains(instr, predicate) || predicate(instr)
            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => contains(block, predicate) || predicate(instr)
            case FunctionDecl(name, block, typ, args, typeargs, modifier) => contains(block, predicate) || predicate(instr)
            case PredicateDecl(name, args, block, modifier) => predicate(instr)
            case EnumDecl(name, fields, values, modifier) => predicate(instr)
            case VariableDecl(name, _type, modifier, op, expr) => predicate(instr)
            case TagDecl(name, values, modifier, typ) => predicate(instr)
            case TypeDef(defs) => predicate(instr)
            case Import(lib, value, alias) => predicate(instr)
            case JSONFile(name, json, mod) => predicate(instr)
            case null => false
            case _ => predicate(instr)
    }

    def contains(expr: Expression, predicate: Expression=>Boolean): Boolean = {
        expr match
            case BinaryOperation(_, left, right) => contains(left, predicate) || contains(right, predicate) || predicate(expr)
            case TernaryOperation(left, middle, right) => contains(left, predicate) || contains(middle, predicate) || contains(right, predicate) || predicate(expr)
            case UnaryOperation(op, left) => contains(left, predicate) || predicate(expr)
            case ArrayGetValue(name, index) => contains(name, predicate) || index.exists(contains(_, predicate)) || predicate(expr)
            case TupleValue(values) => values.exists(contains(_, predicate)) || predicate(expr)
            case FunctionCallValue(name, args, generics, _) => args.exists(contains(_, predicate)) || contains(name, predicate) || predicate(expr)
            case ConstructorCall(name, args, generics) => args.exists(contains(_, predicate)) || predicate(expr)
            case RangeValue(min, max, delta) => contains(min, predicate) || contains(max, predicate) || contains(delta, predicate) || predicate(expr)
            case PositionValue(x, y, z) => contains(x, predicate) || contains(y, predicate) || contains(z, predicate) || predicate(expr)
            case IsType(expr, typ) => contains(expr, predicate) || predicate(expr)
            case CastValue(expr, typ) => contains(expr, predicate) || predicate(expr)
            case other => predicate(other)
    }

    def simplify(expr: Expression)(implicit context: Context): Expression = positioned(expr, {
        expr match
            case VariableValue(name, selector) if name == Identifier.fromString("this") => simplify(CastValue(VariableValue("__ref", selector), ClassType(context.getCurrentClass(), List())))
            case NamespacedName(value, json) => NamespacedName(value, simplify(json))
            case PositionValue(x, y, z) => PositionValue(simplify(x), simplify(y), simplify(z))
            case JsonValue(JsonArray(List(JsonExpression(ForSelect(expr, filter, selector), _)))) => {
                JsonValue(JsonArray(getForeachCases("_", selector).map(l => JsonExpression(l.foldLeft(expr)((a, b) => simplify(subst(a, filter, b._2))), "")).toList))
            }
            case InterpolatedString(values) => InterpolatedString(values.map(simplify(_)))
            case JsonValue(JsonString(value)) => StringValue(value)
            case JsonValue(JsonInt(value, t)) => IntValue(value)
            case JsonValue(JsonFloat(value, t)) => FloatValue(value)
            case JsonValue(JsonBoolean(value)) => BoolValue(value)
            case JsonValue(JsonNull) => NullValue
            case JsonValue(other) => JsonValue(fix(other)(context, Set()))
            case CastValue(value, typ) => {
                simplify(value) match
                    case CastValue(other, typ2) => CastValue(other, typ)
                    case other if typeof(other) == typ => other
                    case other => CastValue(other, typ)
            }
            case LambdaValue(args, instr, ctx) => LambdaValue(args, instr, if ctx == null then context else ctx)
            case SelectorValue(value) => Utils.fix(expr)(context, Set())
            case IsType(left, typ) => 
                val simpl = simplify(left)
                val typ2 = Utils.typeof(simpl)
                val typ1Con = context.getType(typ)
                
                typ1Con match{
                    case ClassType(clazz, args) => {
                        typ2 match
                            case ClassType(clazz2, args2) => if clazz2 == clazz && args == args2 then BoolValue(true) else IsType(simpl, ClassType(clazz, args))
                            case AnyType =>  IsType(simpl, ClassType(clazz, args))
                            case _ => BoolValue(false)
                    }
                    case _ if typ2 != AnyType => BoolValue(typ1Con == typ2)
                    case _ => IsType(simpl, typ)
                }

            case SequenceValue(left, right) => SequenceValue(left, simplify(right))
            case UnaryOperation(op, left) => {
                val inner = Utils.simplify(left)
                (op,inner) match {
                    case ("!",StringValue(value)) => StringValue(value.map(c => if c.isUpper then c.toLower else c.toUpper))
                    case ("!",IntValue(value)) => IntValue(~value)
                    case ("!",BoolValue(value)) => BoolValue(!value)
                    case (op, other) => UnaryOperation(op, other)
                }
            }
            case TernaryOperation(left, middle, right) => {
                val nl = simplify(left)
                val nm = simplify(middle)
                val nr = simplify(right)
                (nl, nm, nr) match
                    case (BoolValue(a), b, c) => if a then b else c
                    case (a, b, c) => TernaryOperation(a, b, c)
            }
            case BinaryOperation("??", left, right) => {
                val nl = simplify(left)
                val nr = simplify(right)
                nl match
                    case NullValue => nr
                    case v: LinkedVariableValue => BinaryOperation("??", v, nr)
                    case v: VariableValue => BinaryOperation("??", v, nr)
                    case other => nl
            }
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
                    case (NamespacedName(a, b), NamespacedName(c, d)) if op == "==" => BoolValue(a == c && b == d)
                    case (NamespacedName(a, b), NamespacedName(c, d)) if op == "!=" => BoolValue(a != c || b != d)
                    case (TagValue(a), TagValue(b)) if op == "==" => BoolValue(a == b)
                    case (TagValue(a), TagValue(b)) if op == "!=" => BoolValue(a != b)
                    case (LinkedTagValue(a), LinkedTagValue(b)) if op == "==" => BoolValue(a == b)
                    case (LinkedTagValue(a), LinkedTagValue(b)) if op == "!=" => BoolValue(a != b)
                    case (JsonValue(a), JsonValue(b)) if op == "==" => BoolValue(a == b)
                    case (JsonValue(a), JsonValue(b)) if op == "!=" => BoolValue(a != b)
                    case _ => BinaryOperation(op, nl, nr)
            }
            case BinaryOperation("+", a: Expression, b: Expression) if a == b && typeof(a).allowAdditionSimplification() => BinaryOperation("*", a, IntValue(2))
            case BinaryOperation("+", RawJsonValue(a), RawJsonValue(b)) => RawJsonValue(a ::: b)
            case BinaryOperation(op, left, right) => {
                val nl = simplify(left)
                val nr = simplify(right)
                (nl, nr) match
                    case (PositionValue(x1, y1, z1), PositionValue(x2, y2, z2)) if op == "+" => PositionValue(simplify(BinaryOperation("+", x1, x2)), simplify(BinaryOperation("+", y1, y2)), simplify(BinaryOperation("+", z1, z2)))
                    case (StringValue(a), JsonValue(JsonDictionary(dic))) if op == "in" => BoolValue(dic.contains(a))
                    case (StringValue(a), JsonValue(JsonArray(arr))) if op == "in" => BoolValue(arr.contains(StringValue(a)))
                    case (StringValue(a), StringValue(b)) if op == "in" => BoolValue(b.contains(a))
                    //case (sel: SelectorValue, other) if op == "in" => SelectorValue(getSelector(BinaryOperation(op, nl, nr), true)._3)
                    //case (sel: SelectorValue, other) if op == "not in" => SelectorValue(getSelector(BinaryOperation(op, nl, nr), true)._3)
                    case (StringValue(a), IntValue(b)) if op == "*" => StringValue(a * b)
                    case (StringValue(a), JsonValue(JsonString(b))) => StringValue(combine(op, a, b))
                    case (JsonValue(JsonString(a)), StringValue(b)) => StringValue(combine(op, a, b))
                    case (JsonValue(a), JsonValue(b)) => JsonValue(combine(op, a, b))
                    case (JsonValue(a), b) => JsonValue(combine(op, a, toJson(b)))
                    case (IntValue(a), IntValue(b)) => IntValue(combine(op, a, b))
                    case (FloatValue(a), FloatValue(b)) => FloatValue(combine(op, a, b))
                    case (IntValue(a), FloatValue(b)) => FloatValue(combine(op, a, b))
                    case (FloatValue(a), IntValue(b)) => FloatValue(combine(op, a, b))
                    case (BoolValue(a), BoolValue(b)) => BoolValue(combine(op, a, b))
                    case (StringValue(a), StringValue(b)) => StringValue(combine(op, a, b))
                    case (StringValue(a), b: Stringifyable) => StringValue(combine(op, a, b.getString()))
                    case (a: Stringifyable, StringValue(b)) => StringValue(combine(op, a.getString(), b))
                    case (SelectorValue(sel1), LinkedVariableValue(right, sel2)) if op == "in" &&  right.getType() == EntityType=> {
                        val sel = sel1.add("tag", SelectorIdentifier(right.tagName))
                        SelectorValue(sel)
                    }
                    case (a, IntValue(b)) if op == "<<" => BinaryOperation("*", a, IntValue(math.pow(2, b).toInt))
                    case (a, IntValue(b)) if op == ">>" => BinaryOperation("/", a, IntValue(math.pow(2, b).toInt))
                    case (a, IntValue(b)) if op == "&" && isPowerOfTwo(b+1) => BinaryOperation("%", a, IntValue(b+1))
                    case (a, b) if a == b && op == "+" && !containsFunctionCall(a) => BinaryOperation("*", a, IntValue(2))
                    case (a, b) if a == b && op == "-" && !containsFunctionCall(a) => IntValue(0)
                    case (a, b) if a == b && op == "/" && !containsFunctionCall(a) => IntValue(1)
                    case (a, b) if a == b && op == "%" && !containsFunctionCall(a) => IntValue(0)
                    case (a, b) if a == b && op == "&&" && !containsFunctionCall(a) => a
                    case (a, b) if a == b && op == "||" && !containsFunctionCall(a) => a
                    case (a, b) if a == b && op == "&" && !containsFunctionCall(a) => a
                    case (a, b) if a == b && op == "|" && !containsFunctionCall(a) => a
                    case _ => BinaryOperation(op, nl, nr)
            }
            case VariableValue(iden, sel) if iden.toString() == "Compiler.isJava" => {
                BoolValue(Settings.target == MCJava)
            }
            case VariableValue(iden, sel) if iden.toString() == "Compiler.isBedrock" => {
                BoolValue(Settings.target == MCBedrock)
            }
            case LinkedVariableValue(vari, sel) => {
                if vari.canBeReduceToLazyValue then vari.lazyValue else expr
            }
            case VariableValue(iden, sel) => {
                val vari = context.tryGetVariable(iden)
                vari match
                    case None => expr
                    case Some(vari) => if vari.canBeReduceToLazyValue then vari.lazyValue else LinkedVariableValue(vari, sel)
            }
            case TagValue(iden) => {
                val vari = context.tryGetBlockTag(iden)
                vari match
                    case None => expr
                    case Some(tag) => LinkedTagValue(tag)
            }
            case ArrayGetValue(name, index) => {
                val inner = Utils.simplify(name)
                val index2 = index.map(Utils.simplify(_))
                (inner, index2) match
                    case (TagValue(tag), List(IntValue(n))) => {
                        val blt = context.tryGetBlockTag(tag)
                        blt match
                            case Some(value) => value.content(n)
                            case None => throw new Exception(s"Unknown block tag $tag")
                    }
                    case (LinkedTagValue(tag), List(IntValue(n))) => {
                        tag.content(n)
                    }
                    case (TupleValue(values), List(IntValue(n))) => values(n)
                    case (LinkedVariableValue(vari, sel), List(IntValue(n))) if vari.typ.isInstanceOf[TupleType] => LinkedVariableValue(vari.tupleVari(n), sel)
                    case (TagValue(_) | LinkedTagValue(_), List(_)) => {
                        throw new Exception(s"Block tag can only be indexed with an integer")
                    }
                    case (JsonValue(JsonArray(content)), List(IntValue(n))) => jsonToExpr(content(n))
                    case (JsonValue(JsonDictionary(content)), List(StringValue(n))) => jsonToExpr(content(n))
                    case (StringValue(str), (List(IntValue(n)))) => 
                        if (n >= 0){
                            StringValue(str(n).toString())
                        }
                        else{
                            StringValue(str(str.length + n).toString())
                        }
                    case (StringValue(str), List(RangeValue(IntValue(min), IntValue(max), IntValue(delta)))) => {
                        var min2 = clamp(if min >= 0 then min else str.length + min, 0, str.length-1)
                        var max2 = clamp(if max >= 0 then max else str.length + max, 0, str.length-1)
                        val delta2 = if delta >= 0 then delta else -delta
                        if (delta < 0){
                            val tmp = min2
                            min2 = max2
                            max2 = tmp
                        }
                        val str2 = for (i <- min2 to max2 by delta2) yield str(i)
                        if (delta < 0){
                            StringValue(str2.mkString("").reverse)
                        }
                        else{
                            StringValue(str2.mkString(""))
                        }
                    }
                    case (_, _) => ArrayGetValue(inner, index2)
            }
            case TupleValue(values) => TupleValue(values.map(Utils.simplify(_)))
            case RangeValue(min, max, delta) => RangeValue(simplify(min), simplify(max), simplify(delta))
            case FunctionCallValue(VariableValue(Identifier(List("Compiler", "isJava")), sel), List(), typs, sel2) => {
                expr
            }
            case FunctionCallValue(VariableValue(Identifier(List("Compiler", "isBedrock")), sel), List(), typs, sel2) => {
                expr
            }
            case FunctionCallValue(LinkedFunctionValue(fct: CompilerFunction), args, typs, sel) if fct.isValue => {
                val args2 = args.map(Utils.simplify(_))
                fct.body(args2, context)._2
            }
            case FunctionCallValue(VariableValue(name, sel), args, typs, sel2) => {
                val sargs = args.map(Utils.simplify(_))
                context.tryGetFunction(name, sargs, typs, VoidType) match
                    case Some((fct: CompilerFunction, args2)) if fct.isValue => fct.body(args2, context)._2
                    case Some((other,args2)) => FunctionCallValue(LinkedFunctionValue(other), args2, typs, sel2)
                    case _ => FunctionCallValue(VariableValue(name, sel), sargs, typs, sel2)
            }
            case other => other
            
    })
    def clamp(value: Int, min: Int, max: Int): Int = {
        if value < min then min
        else if value > max then max
        else value
    }
    def combineJson(op: String, elm1: JSONElement, elm2: JSONElement): JSONElement = {
        if elm1 == JsonNull then elm2
        else if elm2 == JsonNull then elm1
        else
        elm1 match
            case JsonArray(content1) => {
                elm2 match
                    case JsonArray(content2) => 
                        op match
                            case "=" => JsonArray(content2)
                            case "::" | "::=" => JsonArray(content1.zipAll(content2, null, null).map((a, b) => if a == null then b else if b == null then a else combineJson(op, a, b)))
                            case "<:" | "<:=" => JsonArray(content2 ::: content1)
                            case ">:" | ">:=" | "+" | "+=" => JsonArray(content1 ::: content2)
                            case "-:" | "-:=" => JsonArray(content1.filterNot(content2.contains(_)))
                    case other => JsonArray(content1 ::: List(other))
            }
            case JsonDictionary(content1) => {
                elm2 match
                    case JsonDictionary(content2) => 
                        op match
                            case "::" | "::=" | "=" => JsonDictionary((content1.toList ++ content2.toList).groupBy(_._1).map((k, value) => (k, if value.length == 1 then value.head._2 else combineJson(op, value(0)._2, value(1)._2))).toMap)
                            case "<:" | "<:=" => JsonDictionary((content2.toList ++ content1.toList).groupBy(_._1).map((k, value) => (k, if value.length == 1 then value.head._2 else combineJson(op, value(0)._2, value(1)._2))).toMap)
                            case ">:" | ">:=" | "+" | "+=" => JsonDictionary((content1.toList ++ content2.toList).groupBy(_._1).map((k, value) => (k, if value.length == 1 then value.head._2 else combineJson(op, value(0)._2, value(1)._2))).toMap)
                            case "-:" | "-:=" => JsonDictionary(content1.filterNot(a => content2.contains(a._1)))
                    case _ => throw new Exception(f"Json Element doesn't match ${elm1} vs ${elm2}")
            }
            case other => elm1
    }

    def toJson(expr: Expression, typ: String = null)(implicit context: Context): JSONElement = {
        simplify(expr) match
            case JsonValue(content) => compileJson(content)
            case StringValue(value) => JsonString(value)
            case IntValue(value) => JsonInt(value, typ)
            case CastValue(left, right) => toJson(left, typ)
            case FloatValue(value) => JsonFloat(value, typ)
            case BoolValue(value) => JsonBoolean(value)
            case value: NamespacedName => JsonString(value.getString())
            case FunctionCallValue(VariableValue(name, sel), args, typeargs, selector) => {
                val fct = context.getFunction(name, args, typeargs, VoidType)
                if (fct._1 == null){
                    throw new Exception(f"Unknown function ${name}(${args.mkString(",")})")
                }
                else if (fct._1.modifiers.isLazy){
                    var vari = context.getFreshVariable(fct._1.getType())
                    vari.modifiers.isLazy = true
                    val call = fct.call(vari)
                    toJson(vari.lazyValue, typ)
                }
                else if (fct._1.isInstanceOf[CompilerFunction] && fct._1.asInstanceOf[CompilerFunction].isValue){
                    val args2 = args.map(Utils.simplify(_))
                    toJson(fct._1.asInstanceOf[CompilerFunction].body(args2, context)._2, typ)
                }
                else{
                    fct.markAsStringUsed()
                    if (Settings.target == MCJava){
                        JsonString(fct.call().last.getString())
                    }
                    else if (Settings.target == MCBedrock){
                        JsonArray(fct.call().map(f => JsonString("/"+f.getString())))
                    }
                    else{
                        throw new Exception(f"Unknown target ${Settings.target}")
                    }
                }
            }
            case FunctionCallValue(LinkedVariableValue(vari, sel), args, typeargs, selector) => {
                if (vari.canBeReduceToLazyValue){
                    toJson(vari.lazyValue, typ)
                }
                else{
                    ???
                }
            }
            case FunctionCallValue(LinkedFunctionValue(fct), args, typeargs, selector) => {
                if (fct.modifiers.isLazy){
                    var vari = context.getFreshVariable(fct.getType())
                    vari.modifiers.isLazy = true
                    val call = (fct,args).call(vari)
                    toJson(vari.lazyValue, typ)
                }
                else if (fct.isInstanceOf[CompilerFunction] && fct.asInstanceOf[CompilerFunction].isValue){
                    val args2 = args.map(Utils.simplify(_))
                    toJson(fct.asInstanceOf[CompilerFunction].body(args2, context)._2, typ)
                }
                else{
                    fct.markAsStringUsed()
                    if (Settings.target == MCJava){
                        JsonString((fct,args).call().last.getString())
                    }
                    else if (Settings.target == MCBedrock){
                        JsonArray((fct,args).call().map(f => JsonString("/"+f.getString())))
                    }
                    else{
                        throw new Exception(f"Unknown target ${Settings.target}")
                    }
                }
            }
            case FunctionCallValue(LambdaValue(largs, instr, ctx), args, typeargs, selector) => {
                val block = ctx.getFreshLambda(largs, List(), VoidType, instr, false)
                block.markAsStringUsed()
                if (Settings.target == MCJava){
                    JsonString((block, args).call().last.getString())
                }
                else if (Settings.target == MCBedrock){
                    JsonArray((block, args).call().map(f => JsonString("/"+f.getString())))
                }
                else{
                    throw new Exception(f"Unknown target ${Settings.target}")
                }
            }
            case LinkedFunctionValue(fct) => {fct.markAsStringUsed();JsonString(Settings.target.getFunctionName(fct.fullName))}
            case LinkedVariableValue(vari, selector) if vari.canBeReduceToLazyValue => toJson(vari.lazyValue, typ)
            case LinkedVariableValue(vari, selector) => JsonExpression(LinkedVariableValue(vari, selector), typ)
            
            case v => JsonExpression(fix(v)(context, Set()), typ)
    }

    def compileJson(elm: JSONElement)(implicit context: Context): JSONElement = {
        elm match
            case JsonArray(content) => JsonArray(content.map(compileJson(_)))
            case JsonDictionary(map) => JsonDictionary(map.map((k,v) => (k, compileJson(v))))
            case JsonExpression(FunctionCallValue(name, args, typeargs, sel), t) => {
                val fct = simplify(name) match
                    case LinkedFunctionValue(fct) => (fct, args)
                    case LinkedVariableValue(vari, sel) if vari.modifiers.isLazy => {
                        vari.lazyValue match
                            case LinkedFunctionValue(fct) => (fct, args)
                            case other => throw new Exception(f"Lazy value do not contains a function")
                    }
                    case VariableValue(name, sel) => context.getFunction(name, args, typeargs, VoidType)
                    case LambdaValue(largs, instr, ctx) => {
                        val block = ctx.getFreshLambda(largs, List(), VoidType, instr, false)
                        (block, args)
                    }
                    case other => throw new Exception(f"Not a function ${other}")

                Settings.target match
                    case MCJava => {
                        if (fct._1.modifiers.isLazy){
                            var vari = context.getFreshVariable(fct._1.getType())
                            vari.modifiers.isLazy = true
                            val call = fct.call(vari)
                            toJson(vari.lazyValue, t)
                        }
                        else if (fct._1.isInstanceOf[CompilerFunction] && fct._1.asInstanceOf[CompilerFunction].isValue){
                            val args2 = args.map(Utils.simplify(_))
                            toJson(fct._1.asInstanceOf[CompilerFunction].body(args2, context)._2, t)
                        }
                        else{
                            val fctCall = fct.call()
                            if (fctCall.length == 1){
                                fct.markAsStringUsed()
                                JsonString(fctCall.last.getString().replaceAll("function ", ""))
                            }
                            else{
                                val block = context.getFreshBlock(fctCall)
                                block.markAsStringUsed()
                                JsonString(MCJava.getFunctionName(block.fullName))
                            }
                        }
                    }
                    case MCBedrock => {
                        if (fct._1 == null){
                            JsonArray(List())
                        }
                        else if (fct._1.isInstanceOf[CompilerFunction] && fct._1.asInstanceOf[CompilerFunction].isValue){
                            val args2 = args.map(Utils.simplify(_))
                            toJson(fct._1.asInstanceOf[CompilerFunction].body(args2, context)._2, t)
                        }
                        else if (fct._1.modifiers.isLazy){
                            var vari = context.getFreshVariable(fct._1.getType())
                            vari.modifiers.isLazy = true
                            val call = fct.call(vari)
                            toJson(vari.lazyValue, t)
                        }
                        else{
                            fct.markAsStringUsed()
                            JsonArray(fct.call().map(v => JsonString("/"+v.getString())))
                        }
                    }
            }
            case JsonIdentifier(value, t) => {
                val vari = context.tryGetVariable(value)
                vari match
                    case Some(value) => {
                        if (value.canBeReduceToLazyValue){
                            toJson(value.lazyValue, t)
                        }
                        else{
                            throw new Exception(f"Cannot put not lazy variable ${value.fullName} in json")
                        }
                    }
                    case None => {
                        throw new Exception(f"No value for $value in ${context.fullPath}")
                    }
            }
            case JsonExpression(value, t) => toJson(Utils.simplify(value), t)
            case JsonFloat(value, t) => elm
            case JsonBoolean(value) => elm
            case JsonInt(value, t) => elm
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
        combineJson(op, a, b)
    }

    def combine(op: String, a: String, b: String): String = {
        op match
            case "+" => a + b
            case "::" => a + b
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
            case "<<" => a << b
            case ">>" => a >> b
            case "&" => a & b
            case "|" => a | b
            case "^" => math.pow(a, b).toInt
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
            case "^" => math.pow(a, b)
    }

    def getForgenerateCases(key: String, provider: Expression)(implicit context: Context): IterableOnce[List[(String, String)]] = {
        simplify(provider) match
            case RangeValue(IntValue(min), IntValue(max), IntValue(delta)) => Range(min, max+1, delta).map(elm => List((key, elm.toString())))
            case RangeValue(FloatValue(min), FloatValue(max), FloatValue(delta)) => (BigDecimal(min) to BigDecimal(max) by BigDecimal(delta)).map(elm => List((key, elm.toString())))
            case TupleValue(lst) => lst.map(elm => List((key, elm.toString())))
            case LinkedVariableValue(vari, sel) if vari.canBeReduceToLazyValue => getForgenerateCases(key, vari.lazyValue)
            case VariableValue(iden, sel) if iden.toString().startsWith("@") => {
                context.getFunctionTags(iden).getCompilerFunctionsName().map(name => List((key, name)))
            }
            case TagValue(iden) => {
                val blt = context.tryGetBlockTag(iden)
                blt match
                    case Some(value) => return value.content.par.map(v => List((key, v.toString()))).toList
                    case None => {}
                throw new Exception(f"Unknown Generator: $iden")
            }
            case LinkedTagValue(value) => {
                value.content.par.map(v => List((key, v.toString()))).toList
            }
            case VariableValue(iden, sel) => {
                val enm = context.tryGetEnum(iden)
                enm match
                    case Some(value) => return value.values.par.map(v => (key, v.name) :: v.fields.zip(value.fields).map((p, f) => (key+"."+f.name, p.getString()))).toList
                    case None => {}

                val blt = context.tryGetBlockTag(iden)
                blt match
                    case Some(value) => return value.content.par.map(v => List((key, v.toString()))).toList
                    case None => {}

                val vari = context.tryGetVariable(iden)
                vari match
                    case Some(vri) if vri.canBeReduceToLazyValue => getForgenerateCases(key, vri.lazyValue)
                    case _ => {}

                throw new Exception(f"Unknown Generator: $iden")
            }
            case JsonValue(content) => {
                content match{
                    case JsonArray(content) => content.map(v => List((key, JsonValue(v).getString())))
                    case JsonDictionary(map) => map.map(v => List((key+"."+v._1, JsonValue(v._2).getString())))
                    throw new Exception(f"JSON Generator Not supported: $provider $content")
                }
            }
            case FunctionCallValue(iden, args, typeargs, selector) =>{
                (iden, args.map(Utils.simplify(_))) match
                    case (VariableValue(Identifier(List("Compiler","csv")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            val lines = Source.fromFile(file).getLines()
                            val header = lines.next().split(",").toList
                            lines.map(line => line.split(",").toList.zip(header).map((v, h) => (key+"."+h, v))).toList
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "json")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            val json = Parser.parseJson(Source.fromFile(file).mkString)
                            json match{
                                case JsonArray(content) => content.map(v => List((key, JsonValue(v).getString())))
                                case JsonDictionary(map) => map.map(v => List((key+"."+v._1, JsonValue(v._2).getString())))
                                case _ => throw new Exception(f"JSON Generator Not supported: $provider")
                            }
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "yaml")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            // Get lines from file
                            val lines = Source.fromFile(file).getLines()
                            lines.map(line => line.split("=").toList.map(_.trim)).map(lst => List((key, lst.last))).toList
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "run")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            // run the script and get the output
                            val output = Process(filePath).!!
                            output.split("\n").map(line => line.trim).map(lst => List((key, lst))).toList
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "substring")),_), List(rjson@RawJsonValue(value))) => {
                        (0 to rjson.length()).map(i => List((key, rjson.substring(i).getString()))).toList
                    }
                    case _ => throw new Exception(f"Unknown generator: $provider")
                
            }
            case _ => throw new Exception(f"Unknown generator: $provider")
    }
    def getForeachCases(key: String, provider: Expression)(implicit context: Context): IterableOnce[List[(String, Expression)]] = {
        Utils.simplify(provider) match
            case RangeValue(IntValue(min), IntValue(max), IntValue(delta)) => Range(min, max+1, delta).map(elm => List((key,IntValue(elm))))
            case RangeValue(FloatValue(min), FloatValue(max), FloatValue(delta)) => (BigDecimal(min) to BigDecimal(max) by BigDecimal(delta)).map(elm => List((key, FloatValue(elm.toDouble))))
            case TupleValue(lst) if lst.forall(x => x.isInstanceOf[RangeValue]) => {
                var gen = lst.map(x => x.asInstanceOf[RangeValue])
                val gen2= gen.map{
                    case RangeValue(IntValue(min), IntValue(max), IntValue(delta)) => Range(min, max+1, delta).map(elm => IntValue(elm))
                    case RangeValue(FloatValue(min), FloatValue(max), FloatValue(delta)) => (BigDecimal(min) to BigDecimal(max) by BigDecimal(delta)).map(elm => FloatValue(elm.toDouble))
                }

                gen2.foldLeft(List[List[Expression]](List()))((acc, elm) => {
                    acc.flatMap(lst => 
                        elm.map(v => lst :+ v)
                    )
                }).zipWithIndex.map((lst, i) => List((key, TupleValue(lst))))
            }
            case TupleValue(lst) => lst.map(elm => List((key, elm)))
            case VariableValue(iden, sel) if iden.toString().startsWith("@") => {
                context.getFunctionTags(iden).getCompilerFunctionsName().map(name => List((key, VariableValue(name))))
            }
            case TagValue(iden) => {
                val blt = context.tryGetBlockTag(iden)
                blt match
                    case Some(value) => return value.content.par.map(v => List((key, v))).toList
                    case None => {}
                throw new Exception(f"Unknown Generator: $iden")
            }
            case LinkedTagValue(value) => value.content.par.map(v => List((key, v))).toList
            case LinkedVariableValue(vari, sel) if vari.canBeReduceToLazyValue => getForeachCases(key, vari.lazyValue)
            case LinkedVariableValue(vari, sel) =>
                vari.getType() match
                    case ArrayType(inner, sub) => vari.tupleVari.map(elm => List((key, LinkedVariableValue(elm))))
                    case TupleType(inners) => vari.tupleVari.map(elm => List((key, LinkedVariableValue(elm))))
                    case _ => throw new Exception(f"Unknown Generator: $vari")
            case VariableValue(iden, sel) => {
                val enm = context.tryGetEnum(iden)
                enm match
                    case Some(value) => return value.values.par.map(v => (key, VariableValue(value.fullName+"."+ v.name)) :: v.fields.zip(value.fields).map((p, f) => (key+"."+f.name, p))).toList
                    case None => {}
                val blt = context.tryGetBlockTag(iden)
                blt match
                    case Some(value) => return value.content.par.map(v => List((key, v))).toList
                    case None => {}

                val vari = context.tryGetVariable(iden)
                vari match
                    case Some(vri) if vri.canBeReduceToLazyValue => return getForeachCases(key, vri.lazyValue)
                    case _ => {}

                throw new Exception(f"Unknown Generator: $iden")
            }
            case JsonValue(content) => {
                content match{
                    case JsonArray(content) => content.map(v => List((key, jsonToExpr(v))))
                    case JsonDictionary(map) => map.map(v => List((key, StringValue(v._1)))).toList
                    case _ => throw new Exception(f"JSON Generator Not supported: $provider $content")
                }
            }
            case name: NamespacedName => {
                List(List((key, name)))
            }
            case FunctionCallValue(name, args, typeargs, selector) => {
                (name, args.map(Utils.simplify(_))) match
                    case (VariableValue(Identifier(List("Compiler","csv")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            val lines = Source.fromFile(file).getLines()
                            val header = lines.next().split(",").toList
                            lines.map(line => line.split(",").toList.zip(header).map((v, h) => (key+"."+h, Parser.parseExpression(v)))).toList
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "json")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            val json = Parser.parseJson(Source.fromFile(file).mkString)
                            json match{
                                case JsonArray(content) => content.map(v => List((key, JsonValue(v))))
                                case JsonDictionary(map) => map.map(v => List((key+"."+v._1, JsonValue(v._2))))
                                case _ => throw new Exception(f"JSON Generator Not supported: $provider")
                            }
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "yaml")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            // Get lines from file
                            val lines = Source.fromFile(file).getLines()
                            lines.map(line => line.split("=").toList.map(_.trim)).map(lst => List((key, Parser.parseExpression(lst.last)))).toList
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "run")), _), List(StringValue(filePath))) => {
                        val file = new File(filePath)
                        if(file.exists()){
                            // run the script and get the output
                            val output = Process(filePath).!!
                            output.split("\n").map(line => line.trim).map(lst => List((key, Parser.parseExpression(lst)))).toList
                        }
                        else{
                            throw new Exception(f"File $filePath does not exist")
                        }
                    }
                    case (VariableValue(Identifier(List("Compiler", "substring")),_), List(rjson@RawJsonValue(value))) => {
                        (0 to rjson.length()).map(i => List((key, rjson.substring(i)))).toList
                    }
                    case (vari, args) => throw new Exception(f"Unknown generator: $provider $args")
            }
            case _ => throw new Exception(f"Unknown generator: $provider")
    }
    def getSelector(expr: Expression, noSimplification: Boolean = false)(implicit context: Context): (List[IRTree], Context, Selector) = {
        def apply(selector: Expression): (List[IRTree], Context, Selector) = {
            val vari = context.getFreshVariable(EntityType)
            val (prefix, ctx, sel) = getSelector(LinkedVariableValue(vari))
            (sl.Compilation.Execute.withInstr(With(selector, BoolValue(false), BoolValue(true), 
                VariableAssigment(List((Right(vari), Selector.self)), "=", SelectorValue(Selector.self)), null)):::prefix, ctx, sel)
        }
        (if noSimplification then expr else Utils.simplify(expr)) match
            case VariableValue(Identifier(List("@attacker")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(Identifier(List("@controller")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(Identifier(List("@leasher")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(Identifier(List("@origin")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(Identifier(List("@owner")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(Identifier(List("@passengers")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(Identifier(List("@target")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(Identifier(List("@vehicle")), selector) if Settings.target.hasFeature("execute on") => apply(expr)
            case VariableValue(name, sel) => {
                getSelector(context.resolveVariable(VariableValue(name, sel)))
            }
            case LinkedVariableValue(vari, sel) => 
                vari.getType() match
                    case EntityType if !vari.modifiers.isEntity => (List(), null, JavaSelector("@e", List(("tag", SelectorIdentifier(vari.tagName)))))
                    case EntityType if vari.modifiers.isEntity => {
                        val e = context.getFreshVariable(EntityType)
                        (context.getFunction("__getBindEntity__", List(VariableValue(Identifier.fromString(vari.fullName+".binding"))), List(), VoidType, false).call(e), 
                        null,
                        JavaSelector("@e", List(("tag", SelectorIdentifier(e.tagName)))))
                    }
                    case ClassType(clazz, sub) => {
                        val e = context.getFreshVariable(EntityType)
                        val selector = SelectorValue(JavaSelector("@e", List(("tag", SelectorIdentifier(clazz.getTag())))))

                        (
                            Compiler.compile(With(
                                selector, 
                                BoolValue(false), 
                                BinaryOperation("==", LinkedVariableValue(vari, sel), LinkedVariableValue(context.root.push("object").getVariable("__ref"))),
                                VariableAssigment(List((Right(e), Selector.self)), "=", SelectorValue(Selector.self)),
                                null
                            )),
                            clazz.context.push(clazz.name),
                            JavaSelector("@e", List(("tag", SelectorIdentifier(e.tagName))))
                        )
                    }
                    case _ => throw new Exception(f"Not a selector: $expr")
            case TupleValue(values) => ???
            case SelectorValue(value) => (List(), null, value)
            case ClassValue(value) => 
                value.generate()
                (List(), value.context.push(value.name), JavaSelector("@e", List(("tag", SelectorIdentifier(value.getTag())))))
                
            case BinaryOperation("in", left, right) => 
                val (p1, ctx1, s1) = getSelector(left)
                val (p2, ctx2, s2) = getSelector(right)
                (p1 ::: p2, ctx2, s1.merge(s2))
            
            case BinaryOperation("not in", left, right) => 
                val (p1, ctx1, s1) = getSelector(left)
                val (p2, ctx2, s2) = getSelector(right)
                (p1 ::: p2, ctx2, s1.merge(s2.invert()))
            
            case BinaryOperation(op, left, right) => 
                val (prefix, vari) = Utils.simplifyToVariable(expr)
                val (p2, ctx, s) = getSelector(vari)
                (prefix ::: p2, ctx, s)
            case FunctionCallValue(name, args, typeargs, selector) => {
                val (prefix, vari) = Utils.simplifyToLazyVariable(expr)
                val (p2, ctx, s) = getSelector(vari)
                (prefix ::: p2, ctx, s)
            }
            case other => throw new Exception(f"Unexpected value in as $other")
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
            case "<" => ">"
            case "<=" => ">="
            case ">" => "<"
            case ">=" => "<="
        }
    }
    def combineType(typ1: Type, typ2: Type)(implicit context: Context): Type = {
        if (typ1.isSubtypeOf(typ2)){
            typ2
        }
        else if (typ2.isSubtypeOf(typ1)){
            typ1
        }
        else{
            throw new Exception(f"Cannot combine types $typ1 and $typ2")
        }
    }
    def resolveGenerics(names: List[String], args: List[(Argument, Type)])(implicit context: Context): List[Type] = {
        names.map(name => args.map(a => a._1.typ match
            case IdentifierType(name2, gen) if name == name => a._2
            case _ => null
        )
        .filter(_!=null)
        .reduce((a,b) => combineType(a, b))
        )
    }
}