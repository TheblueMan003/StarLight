package sl

import objects.Identifier
import sl.Compilation.Selector.Selector
import objects.types.VoidType
import objects._
import objects.types.BoolType
import objects.types.IntType
import objects.types.Type
import objects.types.FuncType

enum ReturnState{
    case None
    case Partial
    case Full
}
object ReturnState{
    def follow(a: ReturnState, b: ReturnState)={
        if (a == Full || b == Full) Full
        else if (a == Partial || b == Partial) Partial
        else None
    }
    def combine(a: ReturnState, b: ReturnState)={
        if (a == Full && b == Full) Full
        else if (a == None && b == None) None
        else Partial
    }
}


object StaticAnalyser{
    /**
     * Checks the given instruction for any issues and returns a new instruction.
     * The returned instruction will have position information attached to it.
     *
     * @param instruction The instruction to check.
     * @return A new instruction.
     */
    def check(instruction: Instruction): Instruction = Utils.positioned(instruction, {
        instruction match{
            case Package(name, block) => Package(name, check(block))
            case InstructionBlock(instructions) => InstructionBlock(instructions.map(check))
            case InstructionList(instructions) => InstructionList(instructions.map(check))
            case If(cond, ifBlock, elseBlock) => If(cond, check(ifBlock), elseBlock.map(e => ElseIf(e.cond, check(e.ifBlock))))
            case WhileLoop(cond, block) => WhileLoop(cond, check(block))
            case DoWhileLoop(cond, block) => DoWhileLoop(cond, check(block))
            case Switch(value, cases, _) => Switch(value, cases.map{case x: SwitchCase => SwitchCase(x.expr, check(x.instr), x.cond);
                                                                    case x: SwitchForGenerate => SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, check(x.instr.instr), x.instr.cond));
                                                                    case x: SwitchForEach => SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, check(x.instr.instr), x.instr.cond));
                                                                    })
            case Execute(typ, exprs, block) => Execute(typ, exprs, check(block))
            case With(expr, isat, cond, block, elze) => With(expr, isat, cond, check(block), check(elze))
            case Try(block, except, finallyBlock) => Try(check(block), check(except), check(finallyBlock))
            case FunctionDecl(name, block, typ, args, typeArgs, modifier) => {
                val newBlock = check(block)
                val returnState = hasReturn(newBlock)
                if (returnState == ReturnState.None && typ != VoidType && !modifier.hasAttributes("noReturnCheck")(null))
                    if (Settings.consoleWarningReturn){Reporter.warning(f"Function $name does not return")}
                if (returnState == ReturnState.Partial && typ != VoidType && !modifier.hasAttributes("noReturnCheck")(null))
                    if (Settings.consoleWarningReturn){Reporter.warning(f"Function $name does not return in all cases")}
                val finalBlock = returnOne(newBlock)
                if ((finalBlock != newBlock))
                {
                    modifier.addAtrribute("__returnCheck__", BoolValue(true))
                    val block = InstructionList(List(getDeclaration("__hasFunctionReturned__", BoolValue(false)), finalBlock))
                    FunctionDecl(name, block, typ, args, typeArgs, modifier)
                }
                else{
                    FunctionDecl(name, finalBlock, typ, args, typeArgs, modifier)
                }
            }
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => ClassDecl(name, generics, check(block), modifier, parent, parentGenerics, interfaces, entity)
            case StructDecl(name, generics, block, modifier, parent) => StructDecl(name, generics, check(block), modifier, parent)
            case ForEach(key, provider, instr) => ForEach(key, provider, check(instr))
            case ForGenerate(key, provider, instr) => ForGenerate(key, provider, check(instr))
            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => TemplateDecl(name, check(block), modifier, parent, generics, parentGenerics)
            case TemplateUse(template, name, block, values) => TemplateUse(template, name, check(block), values)
            case _ => instruction
        }
    })
    /**
     * Determines if the given instruction has a return statement.
     *
     * @param instruction the instruction to check for a return statement
     * @return a ReturnState indicating if the instruction has a return statement and if it is reachable
     */
    def hasReturn(instruction: Instruction): ReturnState = {
        instruction match{
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
            case With(expr, isat, cond, block, elze) => ReturnState.combine(hasReturn(block), hasReturn(elze))
            case ForEach(key, provider, instr) => hasReturn(instr)
            case ForGenerate(key, provider, instr) => hasReturn(instr)
            case _ => ReturnState.None
        }
    }
    /**
     * Returns an instruction that return only once per path.
     * @param instruction the instruction to be positioned
     * @return an instruction that return only once per path.
     */
    def returnOne(instruction: Instruction): Instruction = Utils.positioned(instruction, {
        instruction match{
            case Package(name, block) => throw new Exception("Package cannot return")
            case InstructionBlock(instructions) => InstructionBlock(returnOnce(instructions))
            case InstructionList(instructions) =>  InstructionList(returnOnce(instructions))
            case If(cond, ifBlock, elseBlock) => If(cond, returnOne(ifBlock), elseBlock.map(e => ElseIf(e.cond, returnOne(e.ifBlock))))
            case WhileLoop(cond, block) => {
                hasReturn(block) match{
                    case ReturnState.None => WhileLoop(cond, block)
                    case _ => WhileLoop(BinaryOperation("&&", cond, getComparaison("__hasFunctionReturned__", BoolValue(false))), returnOne(block))
                }
            }
            case DoWhileLoop(cond, block) => {
                hasReturn(block) match{
                    case ReturnState.None => DoWhileLoop(cond, block)
                    case _ => DoWhileLoop(BinaryOperation("&&", cond, getComparaison("__hasFunctionReturned__", IntValue(0))), returnOne(block))
                }
            }
            case Switch(value, cases, default) => {
                val newCases = cases.map{case x: SwitchCase => SwitchCase(x.expr, InstructionList(returnOnce(List(x.instr))), x.cond);
                                        case x: SwitchForGenerate => SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, InstructionList(returnOnce(List(x.instr.instr))), x.instr.cond));
                                        case x: SwitchForEach => SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, InstructionList(returnOnce(List(x.instr.instr))), x.instr.cond));
                                        }
                Switch(value, newCases, default)
            }
            case Execute(typ, exprs, block) => Execute(typ, exprs, returnOne(block))
            case With(expr, isat, cond, block, elze) => With(expr, isat, cond, returnOne(block), returnOne(elze))
            case ForEach(key, provider, instr) => 
                hasReturn(instr) match{
                    case ReturnState.None => ForEach(key, provider, instr)
                    case _ => ForEach(key, provider, If(getComparaison("__hasFunctionReturned__", IntValue(0)), instr, List()))
                }
            case ForGenerate(key, provider, instr) => 
                hasReturn(instr) match{
                    case ReturnState.None => ForGenerate(key, provider, instr)
                    case _ => ForGenerate(key, provider, If(getComparaison("__hasFunctionReturned__", IntValue(0)), instr, List()))
                }
            case Return(_) => instruction
            case other => other
        }
    })
    def returnOnce(instructions: List[Instruction])={
        var buffer = instructions
        var lst = List[Instruction]()
        while(buffer.nonEmpty){
            val head = buffer.head
                //__hasFunctionReturned__
            hasReturn(head) match{
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
        }
        lst.reverse
    }
    /**
     * Returns a variable declaration instruction with the given name, value, and type.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     * @param typ The type of the variable. Defaults to IntType.
     * @return The variable declaration instruction.
     */
    def getDeclaration(name: String, value: Expression, typ: Type = IntType): Instruction = {
        VariableDecl(List(name), typ, Modifier.newPrivate(), "=", value)
    }

    /**
     * Returns a variable assignment instruction with the given name and value.
     *
     * @param name The name of the variable.
     * @param value The value to assign to the variable.
     * @return The variable assignment instruction.
     */
    def getAssignment(name: String, value: Expression): Instruction = {
        VariableAssigment(List((Left(Identifier.fromString(name)), Selector.self)), "=", value)
    }

    /**
     * Returns a comparison expression that compares the given variable with the given value using the "==" operator.
     *
     * @param name The name of the variable to compare.
     * @param value The value to compare the variable with.
     * @return The comparison expression.
     */
    def getComparaison(name: String, value: Expression): Expression = {
        BinaryOperation("==", VariableValue(name), value)
    }

    /**
     * Handles a sleep instruction by returning a new instruction that waits for the sleep to complete.
     *
     * @param instruction The sleep instruction to handle.
     * @return The new instruction that waits for the sleep to complete.
     */
    def handleSleep(instruction: Instruction):Instruction = {
        val (newInstr, changed) = handleSleep(instruction, List())
        newInstr
    }

    /**
     * Converts a while loop into an asynchronous function that can be awaited.
     *
     * @param loop The while loop to convert.
     * @return The new instruction block that contains the asynchronous function and the call to it.
     */
    def convertWhileToFunction(loop: WhileLoop): Instruction = {
        val mod = Modifier.newPrivate()
        mod.isAsync = true
        val loopExit = InstructionList(List(getAssignment("--await_callback--", VariableValue("-exit-loop-"))))
        val init = If(getComparaison("-setup-loop-", BoolValue(false)), InstructionList(List(getAssignment("-setup-loop-", BoolValue(true)), getDeclaration("-exit-loop-", VariableValue("--await_callback--"), FuncType(List(), VoidType)))), List())
        val block =  InstructionList(List(init, loop.block, If(loop.cond, InstructionList(List(Await(FunctionCall(Identifier.fromString("--async_while--"), List(), List()), InstructionList(List())))), List(ElseIf(BoolValue(true), loopExit)))))
        val func = FunctionDecl("--async_while--", block, VoidType, List(), List(), mod)
        val call = Await(FunctionCall(Identifier.fromString("--async_while--"), List(), List()), InstructionList(List()))

        val newBlock = InstructionBlock(List(getDeclaration("-setup-loop-", BoolValue(false), BoolType), func, If(loop.cond, call, List())))
        newBlock
    }
    /**
     * Converts a DoWhileLoop to an asynchronous function call using Await and FunctionDecl.
     *
     * @param loop The DoWhileLoop to convert.
     * @return The converted InstructionBlock.
     */
    def convertDoWhileToFunction(loop: DoWhileLoop): Instruction = {
        val mod = Modifier.newPrivate()
        mod.isAsync = true
        mod.isLazy = true
        val loopExit = InstructionList(List(getAssignment("--await_callback--", VariableValue("-exit-loop-"))))
        val init = If(getComparaison("-setup-loop-", BoolValue(false)), InstructionList(List(getAssignment("-setup-loop-", BoolValue(true)), getDeclaration("-exit-loop-", VariableValue("--await_callback--"), FuncType(List(), VoidType)))), List())
        val block =  InstructionList(List(init, loop.block, If(loop.cond, InstructionList(List(Await(FunctionCall(Identifier.fromString("--async_while--"), List(), List()), InstructionList(List())))), List(ElseIf(BoolValue(true), loopExit)))))
        val func = FunctionDecl("--async_while--", block, VoidType, List(), List(), mod)
        val call = Await(FunctionCall(Identifier.fromString("--async_while--"), List(), List()), InstructionList(List()))

        val newBlock = InstructionBlock(List(getDeclaration("-setup-loop-", BoolValue(false), BoolType), func, call))
        newBlock
    }

    /**
     * Determines if an Instruction needs to be converted to an asynchronous function call.
     *
     * @param inst The Instruction to check.
     * @return True if the Instruction needs to be converted, false otherwise.
     */
    def needConversion(inst: Instruction): Boolean = {
        Utils.contains(inst, x => x match{
            case Await(_, _) => true
            case Sleep(_, _) => true
            case _ => false
        })
    }

    /**
     * Handles a sleep instruction by returning the instruction and a boolean indicating the rest of the program was consumed.
     *
     * @param instruction The sleep instruction to handle.
     * @param rest The remaining instructions in the program.
     * @return A tuple containing the instruction and a boolean indicating whether the rest of the program was consumed.
     */
    def handleSleep(instruction: Instruction, rest: List[Instruction]): (Instruction, Boolean) = {
        val ret = instruction match{
            case Sleep(time, continuation) => 
                val (newContinuation, changed) = handleSleep(continuation, rest)
                if (changed)
                    (Sleep(time, newContinuation), true)
                else
                    (Sleep(time, InstructionList(newContinuation :: rest)), true)
            case Await(func, continuation) => 
                val (newContinuation, changed) = handleSleep(continuation, rest)
                if (changed)
                    (Await(func, newContinuation), true)
                else
                    (Await(func, InstructionList(newContinuation :: rest)), true)
            case Assert(func, continuation) => 
                val (newContinuation, changed) = handleSleep(continuation, rest)
                if (changed)
                    (Assert(func, newContinuation), true)
                else
                    (Assert(func, InstructionList(newContinuation :: rest)), true)
            case FunctionDecl(name, block, typ, args, typeArgs, modifier) => 
                if ((modifier.isAsync))
                    (FunctionDecl(name, handleSleep(InstructionList(List(block, FunctionCall(Identifier.fromString("--await_callback--"), List(), List())))), typ, args, typeArgs, modifier), false)
                else
                    (FunctionDecl(name, handleSleep(block), typ, args, typeArgs, modifier), false)
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => 
                (ClassDecl(name, generics, handleSleep(block), modifier, parent, parentGenerics, interfaces, entity), false)
            case StructDecl(name, generics, block, modifier, parent) => 
                (StructDecl(name, generics, handleSleep(block), modifier, parent), false)
            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => 
                (TemplateDecl(name, handleSleep(block), modifier, parent, generics, parentGenerics), false)
            case TemplateUse(template, name, block, values) => 
                (TemplateUse(template, name, handleSleep(block), values), false)
            case Package(name, block) => 
                (Package(name, handleSleep(block)), false)
            case InstructionList(list) => {
                list.foldRight((InstructionList(List()), false))((instr, acc) => {
                    val (newInstr, changed) = handleSleep(instr, acc._1 match{
                        case InstructionList(list) => list ::: rest
                    })
                    
                    if (changed){
                        (InstructionList(newInstr :: Nil), changed || acc._2)
                    }
                    else{
                        (InstructionList(newInstr :: acc._1.list), changed || acc._2)
                    }
                })
            }
            case InstructionBlock(list) => {
                list.foldRight((InstructionBlock(List()), false))((instr, acc) => {
                    val (newInstr, changed) = handleSleep(instr, acc._1 match{
                        case InstructionBlock(list) => list ::: rest
                    })
                    
                    if (changed){
                        (InstructionBlock(newInstr :: Nil), changed || acc._2)
                    }
                    else{
                        (InstructionBlock(newInstr :: acc._1.list), changed || acc._2)
                    }
                })
            }
            case loop@WhileLoop(cond, block) => {
                if (needConversion(block))
                    handleSleep(convertWhileToFunction(loop), rest)
                else
                    (WhileLoop(cond, block), false)
            }
            case loop@DoWhileLoop(cond, block) => {
                if (needConversion(block))
                    handleSleep(convertDoWhileToFunction(loop), rest)
                else
                    (DoWhileLoop(cond, block), false)
            }
            case If(cond, ifBlock, elseBlock) => {
                val needed = needConversion(ifBlock) || elseBlock.exists(e => needConversion(e.ifBlock))
                if (needed){
                    var (newIfBlock, ifChanged) = handleSleep(ifBlock, rest)

                    if (!ifChanged){
                        newIfBlock = InstructionList(newIfBlock :: rest)
                    }

                    val elze = elseBlock.map(e => {
                        val (newElseBlock, elseChanged) = handleSleep(e.ifBlock, rest)
                        if (elseChanged){
                            (ElseIf(e.cond, newElseBlock), elseChanged)
                        }
                        else{
                            (ElseIf(e.cond, InstructionList(newElseBlock :: rest)), elseChanged)
                        }
                    })


                    (If(cond, newIfBlock, elze.map(_._1):::List(ElseIf(BoolValue(true), InstructionList(rest)))), true)
                }
                else{
                    (instruction, false)
                }
            }
            case Switch(value, cases, copyVariable) => {
                val needed = cases.exists{
                    case x: SwitchCase => needConversion(x.instr)
                    case x: SwitchForGenerate => needConversion(x.instr.instr)
                    case x: SwitchForEach => needConversion(x.instr.instr)
                }

                if (needed){
                    val newCases = cases.map{
                        case x: SwitchCase => {
                            val (newInstr, changed) = handleSleep(x.instr, rest)
                            if (changed){
                                SwitchCase(x.expr, newInstr, x.cond)
                            }
                            else{
                                SwitchCase(x.expr, InstructionList(newInstr :: rest), x.cond)
                            }
                        }
                        case x: SwitchForGenerate => {
                            val (newInstr, changed) = handleSleep(x.instr.instr, rest)
                            if (changed){
                                SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, newInstr, x.instr.cond))
                            }
                            else{
                                SwitchForGenerate(x.key, x.provider, SwitchCase(x.instr.expr, InstructionList(newInstr :: rest), x.instr.cond))
                            }
                        }
                        case x: SwitchForEach => {
                            val (newInstr, changed) = handleSleep(x.instr.instr, rest)
                            if (changed){
                                SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, newInstr, x.instr.cond))
                            }
                            else{
                                SwitchForEach(x.key, x.provider, SwitchCase(x.instr.expr, InstructionList(newInstr :: rest), x.instr.cond))
                            }
                        }
                    }
                    (Switch(value, newCases, copyVariable), true)
                }
                else{
                    (instruction, false)
                }
            }
            case Execute(typ, exprs, block) => 
                val (newBlock, changed) = handleSleep(block, rest)
                if (changed){
                    (Execute(typ, exprs, newBlock), true)
                }
                else{
                    (instruction, false)
                }
            case With(expr, isat, cond, block, elze) => 
                val (newBlock, changed) = handleSleep(block, rest)
                val (newElze, elzeChanged) = handleSleep(elze, rest)
                if (changed || elzeChanged){
                    (With(expr, isat, cond, newBlock, newElze), true)
                }
                else{
                    (instruction, false)
                }
            case ForEach(key, provider, instr) => 
                val (newInstr, changed) = handleSleep(instr, rest)
                if (changed){
                    (ForEach(key, provider, newInstr), true)
                }
                else{
                    (instruction, false)
                }
            case ForGenerate(key, provider, instr) =>
                val (newInstr, changed) = handleSleep(instr, rest)
                if (changed){
                    (ForGenerate(key, provider, newInstr), true)
                }
                else{
                    (instruction, false)
                }
            
            case other => (other, false)
        }
        (Utils.positioned(instruction, ret._1), ret._2)
    }
}