package sl

import objects.Identifier
import sl.Compilation.Selector.Selector
import objects.types.VoidType
import objects._
import objects.types.BoolType
import objects.types.IntType

enum ReturnState{
    case None
    case Partial
    case Full
}
object ReturnState{
    def follow(a: ReturnState, b: ReturnState)={
        if a == Full || b == Full then Full
        else if a == Partial || b == Partial then Partial
        else None
    }
    def combine(a: ReturnState, b: ReturnState)={
        if a == Full && b == Full then Full
        else if a == None && b == None then None
        else Partial
    }
}


object StaticAnalyser{
    def check(instruction: Instruction): Instruction = Utils.positioned(instruction, {
        instruction match
            case Package(name, block) => Package(name, check(block))
            case InstructionBlock(instructions) => InstructionBlock(instructions.map(check))
            case InstructionList(instructions) => InstructionList(instructions.map(check))
            case If(cond, ifBlock, elseBlock) => If(cond, check(ifBlock), elseBlock.map(e => ElseIf(e.cond, check(e.ifBlock))))
            case WhileLoop(cond, block) => WhileLoop(cond, check(block))
            case DoWhileLoop(cond, block) => DoWhileLoop(cond, check(block))
            case Switch(value, cases, _) => Switch(value, cases.map{case x: SwitchCase => SwitchCase(x.expr, check(x.instr));
                                                                    case x: SwitchForGenerate => SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, check(x.instr.instr)));
                                                                    case x: SwitchForEach => SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, check(x.instr.instr)));
                                                                    })
            case Execute(typ, exprs, block) => Execute(typ, exprs, check(block))
            case With(expr, isat, cond, block) => With(expr, isat, cond, check(block))
            case Try(block, except, finallyBlock) => Try(check(block), check(except), check(finallyBlock))
            case FunctionDecl(name, block, typ, args, typeArgs, modifier) => 
                val newBlock = check(block)
                val returnState = hasReturn(newBlock)
                if returnState == ReturnState.None && typ != VoidType && !modifier.hasAttributes("noReturnCheck")(null) then
                    Reporter.warning(f"Function $name does not return")
                if returnState == ReturnState.Partial && typ != VoidType && !modifier.hasAttributes("noReturnCheck")(null) then
                    Reporter.warning(f"Function $name does not return in all cases")
                val finalBlock = returnOne(newBlock)
                if (finalBlock != newBlock) then
                    modifier.addAtrribute("__returnCheck__", BoolValue(true))
                    val block = InstructionList(List(getDeclaration("__hasFunctionReturned__", BoolValue(false)), finalBlock))
                    FunctionDecl(name, block, typ, args, typeArgs, modifier)
                else
                    FunctionDecl(name, finalBlock, typ, args, typeArgs, modifier)
            case ClassDecl(name, generics, block, modifier, parent, entity) => ClassDecl(name, generics, check(block), modifier, parent, entity)
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name, generics, check(block), modifier, parent)
            case ForEach(key, provider, instr) => ForEach(key, provider, check(instr))
            case ForGenerate(key, provider, instr) => ForGenerate(key, provider, check(instr))
            case TemplateDecl(name, block, modifier, parent) => TemplateDecl(name, check(block), modifier, parent)
            case TemplateUse(template, name, block) => TemplateUse(template, name, check(block))
            case _ => instruction
    })
    def hasReturn(instruction: Instruction): ReturnState = {
        instruction match
            case InstructionBlock(instructions) => 
                instructions.map(hasReturn).foldLeft(ReturnState.None)(ReturnState.follow)
            case InstructionList(instructions) => 
                instructions.map(hasReturn).foldLeft(ReturnState.None)(ReturnState.follow)
            case Return(_) => ReturnState.Full
            case Throw(expr) => ReturnState.Full
            case If(cond, ifBlock, elseBlock) => 
                val hasElse = elseBlock.exists(x => x.cond == BoolValue(true))
                if (hasElse){
                    (hasReturn(ifBlock) :: elseBlock.map(b => hasReturn(b.ifBlock))).reduce(ReturnState.combine)
                }
                else{
                    (hasReturn(ifBlock) :: elseBlock.map(b => hasReturn(b.ifBlock))).foldLeft(ReturnState.None)(ReturnState.combine)
                }
            case WhileLoop(cond, block) => hasReturn(block)
            case DoWhileLoop(cond, block) => hasReturn(block)
            case Switch(value, cases, _) => cases.map{case x: SwitchCase => hasReturn(x.instr);
                                                    case x: SwitchForGenerate => hasReturn(x.instr.instr);
                                                    case x: SwitchForEach => hasReturn(x.instr.instr);
                                                    }.foldLeft(ReturnState.None)(ReturnState.combine)
            case Execute(typ, exprs, block) => hasReturn(block)
            case With(expr, isat, cond, block) => hasReturn(block)
            case ForEach(key, provider, instr) => hasReturn(instr)
            case ForGenerate(key, provider, instr) => hasReturn(instr)
            case _ => ReturnState.None
    }
    def returnOne(instruction: Instruction): Instruction = Utils.positioned(instruction, {
        instruction match
            case Package(name, block) => throw new Exception("Package cannot return")
            case InstructionBlock(instructions) => InstructionBlock(returnOnce(instructions))
            case InstructionList(instructions) =>  InstructionList(returnOnce(instructions))
            case If(cond, ifBlock, elseBlock) => If(cond, returnOne(ifBlock), elseBlock.map(e => ElseIf(e.cond, returnOne(e.ifBlock))))
            case WhileLoop(cond, block) => {
                hasReturn(block) match
                    case ReturnState.None => WhileLoop(cond, block)
                    case _ => WhileLoop(BinaryOperation("&&", cond, getComparaison("__hasFunctionReturned__", BoolValue(false))), returnOne(block))
            }
            case DoWhileLoop(cond, block) => {
                hasReturn(block) match
                    case ReturnState.None => DoWhileLoop(cond, block)
                    case _ => DoWhileLoop(BinaryOperation("&&", cond, getComparaison("__hasFunctionReturned__", IntValue(0))), returnOne(block))
            }
            case Switch(value, cases, default) => {
                val newCases = cases.map{case x: SwitchCase => SwitchCase(x.expr, InstructionList(returnOnce(List(x.instr))));
                                        case x: SwitchForGenerate => SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, InstructionList(returnOnce(List(x.instr.instr)))));
                                        case x: SwitchForEach => SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, InstructionList(returnOnce(List(x.instr.instr)))));
                                        }
                Switch(value, newCases, default)
            }
            case Execute(typ, exprs, block) => Execute(typ, exprs, returnOne(block))
            case With(expr, isat, cond, block) => With(expr, isat, cond, returnOne(block))
            case ForEach(key, provider, instr) => 
                hasReturn(instr) match
                    case ReturnState.None => ForEach(key, provider, instr)
                    case _ => ForEach(key, provider, If(getComparaison("__hasFunctionReturned__", IntValue(0)), instr, List()))
            case ForGenerate(key, provider, instr) => 
                hasReturn(instr) match
                    case ReturnState.None => ForGenerate(key, provider, instr)
                    case _ => ForGenerate(key, provider, If(getComparaison("__hasFunctionReturned__", IntValue(0)), instr, List()))
            case Return(_) => instruction
            case other => other
    })
    def returnOnce(instructions: List[Instruction])={
        var buffer = instructions
        var lst = List[Instruction]()
        while(buffer.nonEmpty){
            val head = buffer.head
                //__hasFunctionReturned__
            hasReturn(head) match
                case ReturnState.Full => 
                    lst = returnOne(head) :: lst
                    buffer = List()
                case ReturnState.Partial => 
                    lst = If(getComparaison("__hasFunctionReturned__", IntValue(0)), InstructionList(buffer.tail), List())::
                            returnOne(head) :: lst
                    buffer = List()
                case ReturnState.None => 
                    lst = returnOne(head) :: lst
                    buffer = buffer.tail
            }
            lst.reverse
    }
    def getDeclaration(name: String, value: Expression): Instruction = {
        VariableDecl(List(name), IntType, Modifier.newPrivate(), "=", value)
    }
    def getAssignment(name: String, value: Expression): Instruction = {
        VariableAssigment(List((Left(Identifier.fromString(name)), Selector.self)), "=", value)
    }
    def getComparaison(name: String, value: Expression): Expression = {
        BinaryOperation("==", VariableValue(name), value)
    }
}