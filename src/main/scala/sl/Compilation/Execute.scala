package sl.Compilation

import sl.{If, SmallValue, Expression, ElseIf}
import objects.{Context, Variable}
import objects.types.*
import sl.*
import objects.Identifier
import sl.Compilation.Selector.*
import scala.collection.parallel.CollectionConverters._
import sl.IR.*
import objects.Modifier
import objects.ConcreteFunction

object Execute{
    def ifs(ifb: If)(implicit context: Context): List[IRTree] = {
        def isMulti(lst: List[((List[IRTree], List[IFCase]), Instruction)]):Boolean = {
            lst.head._1._2.head match
                case IFTrue => false
                case IFFalse => isMulti(lst.tail)
                case _ => true
        }

        val cbs = ElseIf(ifb.cond, ifb.ifBlock) :: ifb.elseBlock
        val cbs2 = cbs.map(x => (getIfCase(Utils.simplify(x.cond)), x.ifBlock)).filter(x => x._1 != IFFalse)
        val multi = cbs2.length > 1 && isMulti(cbs2)

        val length = cbs.length
        var i = 0
        val vari = if multi then context.getFreshVariable(IntType) else null
        var content = if multi then vari.assign("=", IntValue(0)) else List[IRTree]()

        while(i < length){
            val exp = cbs2(i)._1
            val inner = cbs2(i)._2
            
            if (i == 0 || !multi){
                content = content ::: exp._1
            }
            else if (multi){
                content = content ::: 
                    makeExecute(getListCase(List(IFNotValueCase(IFValueCase(LinkedVariableValue(vari))))), exp._1)
            }

            if (exp._2.length == 1 && exp._2.head == IFTrue && !multi){
                content = content ::: sl.Compiler.compile(inner.unBlockify())
                return content
            }
            else if (exp._2.head == IFFalse){
            }
            else{
                val block = (if multi && i != length-1 then vari.assign("=", IntValue(1)) else List()) ::: sl.Compiler.compile(inner)
                if (i == 0){
                    content = content ::: makeExecute(getListCase(exp._2), block)
                }
                else{
                    content = content ::: makeExecute(getListCase(IFNotValueCase(IFValueCase(LinkedVariableValue(vari))) :: exp._2), block)
                }
            }

            i += 1
        }
        content
    }
    def doWhileLoop(whl: DoWhileLoop)(implicit context: Context):List[IRTree] = {
        val block = context.getFreshBlock(Compiler.compile(whl.block))
        block.append(ifs(If(whl.cond, LinkedFunctionCall(block, List(), null), List())))
        block.call(List())
    }
    def whileLoop(whl: WhileLoop)(implicit context: Context):List[IRTree] = {
        val block = context.getFreshBlock(Compiler.compile(whl.block))
        block.append(ifs(If(whl.cond, LinkedFunctionCall(block, List(), null), List())))
        ifs(If(whl.cond, LinkedFunctionCall(block, List(), null), List()))
    }
    def withInstr(ass: With)(implicit context: Context):List[IRTree] = {
        if (ass.elze != null){
            val vari = context.getFreshVariable(BoolType)
            return vari.assign("=", BoolValue(true))::: 
                withInstr(With(ass.expr, ass.isat, ass.cond, InstructionList(List(VariableAssigment(List((Right(vari), Selector.self)), "=", BoolValue(false)), ass.block)), null)):::
                Execute.ifs(If(LinkedVariableValue(vari), ass.elze, List()))
        }

        def apply(keyword: IRTree=>IRTree, ctx: Context)={
            val (pref2, cases) = getIfCase(Utils.simplify(ass.cond))
            val tail = getListCase(cases)

            val block = if (ctx == null){Compiler.compile(ass.block)}else{
                ass.block match
                    case bb: InstructionBlock => {
                            val id = context.getFreshId()
                            val sub = context.push(id)
                            sub.inherit(ctx)
                            Compiler.compile(bb.unBlockify())(sub)
                    }
                    case other => {
                        val id = context.getFreshId()
                        val sub = context.push(id)
                        sub.inherit(ctx)
                        Compiler.compile(other)(sub)
                    }
            }
            

            Utils.simplify(ass.isat) match
                case BoolValue(true) => pref2:::makeExecute(keyword, makeExecute(x => AtIR("@s", x), makeExecute(tail, block)))
                case BoolValue(false) => pref2:::makeExecute(keyword, makeExecute(tail, block))
                case other => {
                    val (pref, vari) = Utils.simplifyToVariable(other)
                    pref2:::pref ::: makeExecute(getListCase(List(IFValueCase(vari))), makeExecute(keyword, makeExecute(x => AtIR("@s", x), makeExecute(tail, block))))
                            ::: makeExecute(getListCase(List(IFNotValueCase(IFValueCase(vari)))), makeExecute(keyword, makeExecute(tail, block)))
                }
        }

        Utils.simplify(ass.expr) match{
            case VariableValue(Identifier(List("@attacker")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("attacker", x), null)
            case VariableValue(Identifier(List("@controller")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("controller", x), null)
            case VariableValue(Identifier(List("@leasher")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("leasher", x), null)
            case VariableValue(Identifier(List("@origin")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("origin", x), null)
            case VariableValue(Identifier(List("@owner")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("owner", x), null)
            case VariableValue(Identifier(List("@passengers")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("passengers", x), null)
            case VariableValue(Identifier(List("@target")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("target", x), null)
            case VariableValue(Identifier(List("@vehicle")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("vehicle", x), null)
            case VariableValue(variName, selector) => {
                val variOpt = context.tryGetVariable(variName)
                variOpt match{
                    case Some(vari) =>
                        vari.getType() match{
                            case EntityType if !vari.modifiers.isLazy => {
                                val (prefix, ctx, selector) = Utils.getSelector(ass.expr)
                                vari.tupleVari(0)
                                
                                prefix ::: makeExecute(f => IfScoreboardMatch(vari.tupleVari(0).getIRSelector(), 1, 1, f), apply(x => AsIR(selector.withPrefix("@a").getString(), x), ctx))
                                    ::: makeExecute(f => IfScoreboardMatch(vari.tupleVari(0).getIRSelector(), 0, 0, f), apply(x => AsIR(selector.getString(), x), ctx))
                            }
                            case _ => {
                                val (prefix, ctx, selector) = Utils.getSelector(ass.expr)
                                prefix:::apply(x => AsIR(selector.getString(), x), ctx)
                            }
                        }
                    case other => {
                        val (prefix, ctx, selector) = Utils.getSelector(ass.expr)
                        prefix:::apply(x => AsIR(selector.getString(), x), ctx)
                    }
                }
            }
            case other => {
                val (prefix, ctx, selector) = Utils.getSelector(ass.expr)
                prefix:::apply(x => AsIR(selector.getString(), x), ctx)
            }
        }
    }
    def executeInstr(exec: Execute)(implicit context: Context):List[IRTree] = {
        exec.typ match
            case AtType => {
                Utils.simplify(exec.exprs.head) match
                    case value: PositionValue => 
                        if (value.isRanged()){
                            var block = Compiler.compile(exec.block)
                            value.getAllPosition().flatMap(pos => makeExecute(x => PositionedIR(pos.getString(), x), block))
                        }
                        else{
                            makeExecute(x => PositionedIR(value.getString(), x), Compiler.compile(exec.block))
                        }
                    case SelectorValue(value) => {
                        val (prefix, _, selector) = Utils.getSelector(exec.exprs.head)
                        prefix:::makeExecute(x => AtIR(selector.getString(), x), Compiler.compile(exec.block))
                    }
                    case VariableValue(Identifier(List("@world_surface")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"world_surface", x), Compiler.compile(exec.block))
                    case VariableValue(Identifier(List("@motion_blocking")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"motion_blocking", x), Compiler.compile(exec.block))
                    case VariableValue(Identifier(List("@motion_blocking_no_leaves")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"motion_blocking_no_leaves", x), Compiler.compile(exec.block))
                    case VariableValue(Identifier(List("@ocean_floor")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"ocean_floor", x), Compiler.compile(exec.block))
                    case VariableValue(vari, selector) => {
                        try{
                            val (prefix, _, selector) = Utils.getSelector(exec.exprs.head)
                            prefix:::makeExecute(x => AtIR(selector.getString(), x), Compiler.compile(exec.block))
                        }
                        catch{
                            _ => Compiler.compile(FunctionCall("__at__", exec.exprs ::: List(LinkedFunctionValue(context.getFreshBlock(Compiler.compile(exec.block)))), List()))
                        }
                    }
                    case StringValue(value) => makeExecute(x => PositionedIR(value, x), Compiler.compile(exec.block))
                    case LinkedVariableValue(vari, selector) if vari.getType() == EntityType => {
                        val (prefix, _, selector) = Utils.getSelector(exec.exprs.head)
                        prefix:::makeExecute(x => AtIR(selector.getString(), x), Compiler.compile(exec.block))
                    }
                    case other => {
                        Compiler.compile(FunctionCall("__at__", exec.exprs ::: List(LinkedFunctionValue(context.getFreshBlock(Compiler.compile(exec.block)))), List()))
                    }
            }
            case RotatedType => {
                if (exec.exprs.length > 1){
                    val a = Utils.simplify(exec.exprs(0))
                    val b = Utils.simplify(exec.exprs(1))
                    makeExecute(x => RotatedIR(f"${a.getString()} ${b.getString()}", x), Compiler.compile(exec.block))
                }
                else{
                    val (prefix, _, selector) = Utils.getSelector(exec.exprs.head)
                    prefix:::makeExecute(x => RotatedEntityIR(selector.getString(), x), Compiler.compile(exec.block))
                }
            }
            case FacingType => {
                if (exec.exprs.length > 1){
                    val (prefix, _, selector) = Utils.getSelector(exec.exprs.head)
                    val anchor = 
                        Utils.simplify(exec.exprs(1)) match{
                            case StringValue("eyes") => "eyes"
                            case StringValue("feet") => "feet"
                            case other => throw new Exception(f"Illegal argument: $other for facing")
                        }
                    prefix:::makeExecute(x => FacingEntityIR(selector.getString(), anchor, x), Compiler.compile(exec.block))
                }
                else{
                    Utils.simplify(exec.exprs.head) match
                        case value: PositionValue => makeExecute(x => FacingIR(value.getString(), x), Compiler.compile(exec.block))
                        case _ => {
                            val (prefix, _, selector) = Utils.getSelector(exec.exprs.head)
                            prefix:::makeExecute(x => FacingEntityIR(selector.getString(), "eyes", x), Compiler.compile(exec.block))
                        }
                }
            }
            case AlignType => {
                val a = Utils.simplify(exec.exprs.head)
                makeExecute(x => AlignIR(a.toString(), x), Compiler.compile(exec.block))
            }
    }

    /**
     * call "block" with execute with content: "prefix"
     */
    def makeExecute(prefix: IRTree=>IRTree, block: List[IRTree])(implicit context: Context): List[IRTree] = {
        if (block.length == 1){
            List(prefix(block.head))
        }
        else if (block.length > 1){
            val fct = context.getFreshBlock(block)
            val call = fct.call(List()).head
            List(prefix(call))
        }
        else{
            List()
        }
    }



    private def checkComparaisonError(lType: Type, rType: Type, expr: Expression)(implicit context: Context):Unit ={
        if (!rType.isSubtypeOf(lType) && ! lType.isSubtypeOf(rType)) throw new Exception(f"Cannot compare $lType with $rType in $expr")
    }
    private def checkComparaison(lType: Type, rType: Type)(implicit context: Context):Boolean ={
        if (!rType.isSubtypeOf(lType) && ! lType.isSubtypeOf(rType)) return false
        return true
    }

    /**
     * get IfCase from Expression
     */
    private val comparator = List(">", "<", ">=", "<=", "==", "!=")
    private def getIfCase(expr: Expression)(implicit context: Context): (List[IRTree], List[IFCase]) = try{
        expr match
            case IntValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case EnumIntValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case FloatValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case BoolValue(value) => if value then (List(), List(IFTrue)) else (List(), List(IFFalse))
            case DefaultValue => (List(), List(IFFalse))

            case IsType(left, typ) => 
                val simp = Utils.simplify(left)
                val lType = Utils.typeof(simp)
                val rType = context.getType(typ)
                rType match
                    case ClassType(clazz, _) => {
                        simp match
                            case LinkedVariableValue(vari, sel) => {
                                val tmp = context.getFreshVariable(BoolType)
                                val selector = SelectorValue(JavaSelector("@e", List(("tag", SelectorIdentifier(clazz.getTag())))))

                                val prev = tmp.assign("=", BoolValue(false)):::Compiler.compile(With(
                                        selector, 
                                        BoolValue(false), 
                                        BinaryOperation("==", LinkedVariableValue(vari, sel), LinkedVariableValue(context.root.push("object").getVariable("__ref"))),
                                        VariableAssigment(List((Right(tmp), Selector.self)), "=", BoolValue(true)),
                                        null
                                    ))
                                val other = getIfCase(LinkedVariableValue(tmp, Selector.self))
                                (prev ::: other._1, other._2)
                            }
                            case other => (List(), List(IFFalse))
                    }

                    case _ if rType == lType => (List(), List(IFTrue))
                    case _ => (List(), List(IFFalse))

            case NullValue => (List(), List(IFFalse))
            case LinkedFunctionValue(fct) => (List(), List(IFTrue))
            case SelectorValue(value) => (List(), List(IFValueCase(expr)))
            case VariableValue(iden, sel) => {
                if (Settings.metaVariable.exists(_._1 == iden.toString())) {
                    if Settings.metaVariable.find(_._1 == iden.toString()).get._2() then (List(), List(IFTrue)) else (List(), List(IFFalse))
                }
                else if (iden.toString().startsWith("@")){
                    if context.getFunctionTags(iden).getFunctions().length == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
                }
                else{
                    getIfCase(context.resolveVariable(expr))
                }
            }
            case LinkedVariableValue(vari, sel) => {
                if (vari.canBeReduceToLazyValue){
                    getIfCase(vari.lazyValue)
                }
                else{
                    (List(), List(IFValueCase(expr)))
                }
            }
            case dot: DotValue => {
                val (prev1, rest) = Utils.unpackDotValue(dot)
                val (prev2, c) = getIfCase(rest)
                (prev1:::prev2, c)
            }

            case TernaryOperation(left, middle, right) => {
                val vari = context.getFreshVariable(Utils.typeof(expr))
                val (ir, ca) = getIfCase(LinkedVariableValue(vari, Selector.self))
                (vari.assign("=", expr):::ir, ca)
            }

            case SequenceValue(left, right) => {
                val (prev2, c2) = getIfCase(right)
                (Compiler.compile(left):::prev2, c2)
            }

            case BinaryOperation(op, VariableValue(left, sel), right) =>
                getIfCase(BinaryOperation(op, context.resolveVariable(VariableValue(left, sel)), right))
            case BinaryOperation(op, left, VariableValue(right, sel)) =>
                getIfCase(BinaryOperation(op, left, context.resolveVariable(VariableValue(right, sel))))

            // Tuple
            // Float with int
            case BinaryOperation(op @ ("==" | ">" | "<" | ">=" | "<="), LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2))
                if left.getType() == FloatType && right.getType() == IntType => {
                    val vari = context.getFreshVariable(FloatType)
                    (vari.assign("=", LinkedVariableValue(right, sel2)), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(left, sel1), LinkedVariableValue(vari)))))
                }

            case BinaryOperation(op @ ("==" | ">" | "<" | ">=" | "<="), LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2))
                if left.getType() == IntType && right.getType() == FloatType => {
                    val vari = context.getFreshVariable(FloatType)
                    (vari.assign("=", LinkedVariableValue(left, sel1)), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(vari), LinkedVariableValue(right, sel2)))))
                }

            case BinaryOperation(op @ ("==" | ">" | "<" | ">=" | "<="), LinkedVariableValue(left, sel), IntValue(right))
                if left.getType() == FloatType => 
                    (List(), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(left, sel), FloatValue(right)))))

            case BinaryOperation(op @ ("==" | ">" | "<" | ">=" | "<="), IntValue(right), LinkedVariableValue(left, sel))
                if left.getType() == FloatType => 
                    (List(), List(IFValueCase(BinaryOperation(op, FloatValue(right), LinkedVariableValue(left, sel)))))

            // Int with float
            case BinaryOperation(op @ (">" | "<" | ">=" | "<="), LinkedVariableValue(left, sel), FloatValue(right))
                if left.getType() == IntType => 
                    (List(), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(left, sel), IntValue(right.toInt)))))

            case BinaryOperation(op @ (">" | "<" | ">=" | "<="), FloatValue(right), LinkedVariableValue(left, sel))
                if left.getType() == IntType => 
                    (List(), List(IFValueCase(BinaryOperation(op, IntValue(right.toInt), LinkedVariableValue(left, sel)))))

            case BinaryOperation(op @ ("=="), LinkedVariableValue(left, sel), FloatValue(right))
                if left.getType() == IntType => 
                    if right == right.toInt then (List(), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(left, sel), IntValue(right.toInt))))) else (List(), List(IFFalse))

            case BinaryOperation(op @ ("=="), FloatValue(right), LinkedVariableValue(left, sel))
                if left.getType() == IntType => 
                    if right == right.toInt then (List(), List(IFValueCase(BinaryOperation(op, IntValue(right.toInt), LinkedVariableValue(left, sel))))) else (List(), List(IFFalse))

            case BinaryOperation(op @ ("!="), LinkedVariableValue(left, sel), FloatValue(right))
                if left.getType() == IntType => 
                    if right == right.toInt then (List(), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(left, sel), IntValue(right.toInt))))) else (List(), List(IFTrue))

            case BinaryOperation(op @ ("!="), FloatValue(right), LinkedVariableValue(left, sel))
                if left.getType() == IntType => 
                    if right == right.toInt then (List(), List(IFValueCase(BinaryOperation(op, IntValue(right.toInt), LinkedVariableValue(left, sel))))) else (List(), List(IFTrue))

            
            case BinaryOperation("in", value, LinkedVariableValue(vari, sel)) if vari.getType().isInstanceOf[RangeType] && Utils.typeof(value).isSubtypeOf(vari.getType()) =>
                (List(), List(IFValueCase(BinaryOperation("in", value, LinkedVariableValue(vari, sel)))))

            case BinaryOperation("in", left @ LinkedVariableValue(vari2, sel2), right @ LinkedVariableValue(vari, sel)) if vari.getType() == JsonType =>
                context.requestLibrary("standard.json")
                getIfCase(FunctionCallValue(VariableValue("standard.json.contains", Selector.self), List(right, left), List()))

            case BinaryOperation("in", value, LinkedVariableValue(vari, sel)) if vari.getType() == JsonType =>
                (List(), List(IFValueCase(BinaryOperation("in", value, LinkedVariableValue(vari, sel)))))

            case BinaryOperation("==", left, right) if Utils.typeof(left) == JsonType && Utils.typeof(right) == JsonType=>
                context.requestLibrary("standard.json")
                getIfCase(FunctionCallValue(VariableValue("standard.json.equals", Selector.self), List(left, right), List()))

            case BinaryOperation("==", LinkedVariableValue(left, sel), right) 
                if right.hasIntValue() && left.getType().isDirectComparable() => 
                    (List(), List(IFValueCase(expr)))

            case BinaryOperation(op, LinkedVariableValue(left, sel), BinaryOperation(op2, l, r)) if comparator.contains(op) =>
                val (p,v) = Utils.simplifyToVariable(BinaryOperation(op2, l, r))
                val (p2,c) = getIfCase(BinaryOperation(op, LinkedVariableValue(left, sel), v))
                (p:::p2, c)
            case BinaryOperation(op, BinaryOperation(op2, l, r), LinkedVariableValue(right, sel)) if comparator.contains(op) =>
                val (p,v) = Utils.simplifyToVariable(BinaryOperation(op2, l, r))
                val (p2,c) = getIfCase(BinaryOperation(op, LinkedVariableValue(right, sel), v))
                (p:::p2, c)

            // Directly Comparable Value (int, float, bool, etc...)
            case BinaryOperation(">" | "<" | ">=" | "<=", LinkedVariableValue(left, sel), LinkedVariableValue(right, sel2))
                if left.getType().isDirectComparable()=> 
                    checkComparaisonError(right.getType(), left.getType(), expr)
                    (List(), List(IFValueCase(expr)))

            case BinaryOperation(">" | "<" | ">=" | "<=", LinkedVariableValue(left, sel), right) 
                if left.getType().isDirectComparable() && right.hasIntValue() => 
                    checkComparaisonError(Utils.typeof(right), left.getType(), expr)
                    (List(), List(IFValueCase(expr)))

            case BinaryOperation(">" | "<" | ">=" | "<=", left, LinkedVariableValue(right, sel)) 
                if left.hasIntValue() && right.getType().isDirectComparable() => 
                    checkComparaisonError(right.getType(), Utils.typeof(left), expr)
                    (List(), List(IFValueCase(expr)))


            // Directly Equilable Value (int, float, bool, function, etc...)
            case BinaryOperation("==" | "!=", LinkedVariableValue(left, sel), LinkedVariableValue(right, sel2)) 
                if left.getType().isDirectEqualitable() && checkComparaison(left.getType(), right.getType()) => 

                    (List(), List(IFValueCase(expr)))

            case BinaryOperation("==" | "!=", LinkedVariableValue(left, sel), right) 
                if right.hasIntValue() && left.getType().isDirectEqualitable() && checkComparaison(left.getType(), Utils.typeof(right)) => 
                    checkComparaisonError(Utils.typeof(right), left.getType(), expr)
                    (List(), List(IFValueCase(expr)))

            case BinaryOperation("==" | "!=", left, LinkedVariableValue(right, sel)) 
                if left.hasIntValue() && right.getType().isDirectEqualitable() && checkComparaison(Utils.typeof(left), right.getType())=> 
                    checkComparaisonError(right.getType(), Utils.typeof(left), expr)
                    (List(), List(IFValueCase(expr)))


            case BinaryOperation("==", LinkedVariableValue(left, sel), NullValue) 
                if left.getType().isDirectEqualitable() => 
                    left.getType() match
                        case ClassType(clazz, _) => {
                            // check whether null or zero
                            val vari = context.getFreshVariable(BoolType)
                            val exec1 = makeExecute(getListCase(List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))), vari.assign("=", BoolValue(true)))
                            val exec2 = makeExecute(getListCase(List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), IntValue(0))))), vari.assign("=", BoolValue(true)))
                            (vari.assign("=", BoolValue(false)) ::: exec1 ::: exec2, List(IFValueCase(LinkedVariableValue(vari))))
                        }
                        case other => (List(), List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel))))))

            case BinaryOperation("!=", LinkedVariableValue(left, sel), NullValue) 
                if left.getType().isDirectEqualitable() => 
                    left.getType() match
                        case ClassType(clazz, _) => {
                            // check whether null or zero
                            val vari = context.getFreshVariable(BoolType)
                            val exec1 = makeExecute(getListCase(List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))), vari.assign("=", BoolValue(true)))
                            val exec2 = makeExecute(getListCase(List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), IntValue(0))))), vari.assign("=", BoolValue(true)))
                            (vari.assign("=", BoolValue(false)) ::: exec1 ::: exec2, List(IFNotValueCase(IFValueCase(LinkedVariableValue(vari)))))
                        }
                        case other => (List(), List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))

            case BinaryOperation("==", NullValue, LinkedVariableValue(left, sel)) 
                if left.getType().isDirectEqualitable() => 
                    left.getType() match
                        case ClassType(clazz, _) => {
                            // check whether null or zero
                            val vari = context.getFreshVariable(BoolType)
                            val exec1 = makeExecute(getListCase(List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))), vari.assign("=", BoolValue(true)))
                            val exec2 = makeExecute(getListCase(List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), IntValue(0))))), vari.assign("=", BoolValue(true)))
                            (vari.assign("=", BoolValue(false)) ::: exec1 ::: exec2, List(IFValueCase(LinkedVariableValue(vari))))
                        }
                        case other => (List(), List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel))))))

            case BinaryOperation("!=", NullValue, LinkedVariableValue(left, sel)) 
                if left.getType().isDirectEqualitable() => 
                    left.getType() match
                        case ClassType(clazz, _) => {
                            // check whether null or zero
                            val vari = context.getFreshVariable(BoolType)
                            val exec1 = makeExecute(getListCase(List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))), vari.assign("=", BoolValue(true)))
                            val exec2 = makeExecute(getListCase(List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), IntValue(0))))), vari.assign("=", BoolValue(true)))
                            (vari.assign("=", BoolValue(false)) ::: exec1 ::: exec2, List(IFNotValueCase(IFValueCase(LinkedVariableValue(vari)))))
                        }
                        case other => (List(), List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))

            // Special Case for class
            case BinaryOperation("==" | "!=", LinkedVariableValue(left, sel), LinkedVariableValue(right, sel2)) 
                if left.getType().isDirectEqualitable() && right.name == "__ref" => 
                    (List(), List(IFValueCase(expr)))


            // Comparable Value (class/struct)
            case BinaryOperation(">" | "<" | ">=" | "<=", LinkedVariableValue(left, sel), right)
                if left.getType().isComparaisonSupported() && !left.getType().isDirectComparable() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(left.fullName + "." + Utils.getOpFunctionName(op)), List(right), List()))

            case BinaryOperation(">" | "<" | ">=" | "<=", left, LinkedVariableValue(right, sel)) 
                if right.getType().isComparaisonSupported() && !right.getType().isDirectComparable()=> 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(right.fullName + "." + Utils.getOpFunctionName(Utils.invertOperator(op))), List(left), List()))


            // Directly Equilable Value (int, float, bool, function, etc...)
            case BinaryOperation("==" | "!=", LinkedVariableValue(left, sel), right) 
                if left.getType().isEqualitySupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(left.fullName + "." + Utils.getOpFunctionName(op)), List(right), List()))
            
            case BinaryOperation("==" | "!=", left, LinkedVariableValue(right, sel)) 
                if right.getType().isEqualitySupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(right.fullName + "." + Utils.getOpFunctionName(op)), List(left), List()))

            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", LinkedVariableValue(left, sel), right2)
                if (left.getType().isDirectComparable() || left.getType().isComparaisonSupported() || left.getType().isEqualitySupported()) =>
                    val op = expr.asInstanceOf[BinaryOperation].op
                    val (p1, right) = Utils.simplifyToVariable(right2)
                    val (p, c) = getIfCase(BinaryOperation(op, LinkedVariableValue(left, sel), right))
                    (p1:::p,c)
            
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left2, LinkedVariableValue(right, sel))
                if (right.getType().isDirectComparable() || right.getType().isComparaisonSupported() || right.getType().isEqualitySupported()) =>
                    val op = expr.asInstanceOf[BinaryOperation].op
                    val (p1, left) = Utils.simplifyToVariable(left2)
                    val (p, c) = getIfCase(BinaryOperation(op, left, LinkedVariableValue(right, sel)))
                    (p1:::p,c)
            

            // Special Case for Function
            case BinaryOperation("==", LinkedVariableValue(left, sel), LinkedFunctionValue(right: ConcreteFunction))
                if !left.getType().isEqualitySupported() && !left.getType().isComparaisonSupported() => 
                    (List(), List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), IntValue(right.getMuxID())))))

            case BinaryOperation("!=", LinkedVariableValue(left, sel), LinkedFunctionValue(right: ConcreteFunction))
                if !left.getType().isEqualitySupported() && !left.getType().isComparaisonSupported() => 
                    (List(), List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), IntValue(right.getMuxID()))))))

            case BinaryOperation("==", LinkedFunctionValue(left: ConcreteFunction), LinkedVariableValue(right, sel))
                if !right.getType().isEqualitySupported() && !right.getType().isComparaisonSupported() => 
                    (List(), List(IFValueCase(BinaryOperation("==", LinkedVariableValue(right, sel), IntValue(left.getMuxID())))))

            case BinaryOperation("!=", LinkedFunctionValue(left: ConcreteFunction), LinkedVariableValue(right, sel))
                if !right.getType().isEqualitySupported() && !right.getType().isComparaisonSupported() => 
                    (List(), List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(right, sel), IntValue(left.getMuxID()))))))

            case BinaryOperation(op @ ("==" | "!="), LinkedVariableValue(left, sel), StringValue(value)) if left.getType() == StringType => 
                (List(), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(left, sel), StringValue(value)))))

            case BinaryOperation(op @ ("==" | "!="), StringValue(value), LinkedVariableValue(left, sel)) if left.getType() == StringType => 
                (List(), List(IFValueCase(BinaryOperation(op, LinkedVariableValue(left, sel), StringValue(value)))))

            case BinaryOperation("in", left, right) if Utils.typeof(left) == StringType || Utils.typeof(right) == StringType => 
                context.requestLibrary("standard.string")
                getIfCase(FunctionCallValue(VariableValue("standard.string.contains", Selector.self), List(left, right), List()))

            case BinaryOperation("==", left, right) if Utils.typeof(left) == StringType || Utils.typeof(right) == StringType => 
                context.requestLibrary("standard.string")
                getIfCase(FunctionCallValue(VariableValue("standard.string.equals", Selector.self), List(left, right), List()))

            case BinaryOperation("!=", left, right) if Utils.typeof(left) == StringType || Utils.typeof(right) == StringType => 
                context.requestLibrary("standard.string")
                getIfCase(UnaryOperation("!", FunctionCallValue(VariableValue("standard.string.equals", Selector.self), List(left, right), List())))

            // Error Cases
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", LinkedVariableValue(left, sel), right) 
                if !left.getType().isEqualitySupported() && !left.getType().isComparaisonSupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    throw new Exception(f"Operation $op not supported for $left and $right")
            
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left, LinkedVariableValue(right, sel)) 
                if !right.getType().isEqualitySupported() && !right.getType().isComparaisonSupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    throw new Exception(f"Operation $op not supported for $left and $right")

            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left: FunctionCallValue, right)=> {
                val op = expr.asInstanceOf[BinaryOperation].op
                val (prev, vari) = Utils.simplifyToVariable(left)
                val (p2, c) = getIfCase(BinaryOperation(op, vari, right))
                (prev:::p2, c)
            }
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left, right: FunctionCallValue)=> {
                val op = expr.asInstanceOf[BinaryOperation].op
                val (prev, vari) = Utils.simplifyToVariable(right)
                val (p2, c) = getIfCase(BinaryOperation(op, left, vari))
                (prev:::p2, c)
            }
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left, right)=> {
                val op = expr.asInstanceOf[BinaryOperation].op
                val ll = Utils.simplifyToVariable(left)
                val rr = Utils.simplifyToVariable(right)
                (ll._1:::rr._1, List(IFValueCase(BinaryOperation(op, ll._2, rr._2))))
            }

            case BinaryOperation("in", SelectorValue(v), clz: ClassValue)=> {
                val (p, ctx, sel) = Utils.getSelector(clz)
                (p, List(IFValueCase(BinaryOperation("in", SelectorValue(v), SelectorValue(sel)))))
            }
            case BinaryOperation("in", SelectorValue(v), right)=> {
                val rr = Utils.simplifyToVariable(right)
                (rr._1, List(IFValueCase(BinaryOperation("in", SelectorValue(v), rr._2))))
            }
            case BinaryOperation("in", left, JsonValue(content))=> {
                content match{
                    case JsonDictionary(map) => {
                        (List(), List(if (map.contains(left.getString())){IFTrue}else{IFFalse}))
                    }
                    case other => throw new Exception("Illegal operation")
                }
            }

            case BinaryOperation("in", left, right)=> {
                val op = expr.asInstanceOf[BinaryOperation].op
                val ll = Utils.simplifyToVariable(left)
                val rr = Utils.simplify(right)

                val tr = Utils.typeof(rr)
                val tl = Utils.typeof(ll._2)

                rr match
                    case RangeValue(IntValue(min), IntValue(max), IntValue(1)) if tl == FloatType && tr == RangeType(IntType) =>
                         (ll._1, List(IFValueCase(BinaryOperation(op, ll._2, RangeValue(FloatValue(min), FloatValue(max), IntValue(1))))))
                    case RangeValue(min, max, IntValue(1)) => (ll._1, List(IFValueCase(BinaryOperation(op, ll._2, rr))))

                    case VariableValue(name, selector) => (ll._1, List(IFValueCase(BinaryOperation(op, ll._2, rr))))

                    case LinkedVariableValue(name, selector) if tl == FloatType && tr == RangeType(IntType) => {
                        val vari = context.getFreshVariable(RangeType(FloatType))
                        val prev = vari.assign("=", rr)
                        (ll._1:::prev, List(IFValueCase(BinaryOperation(op, ll._2, LinkedVariableValue(vari)))))
                    }
                    case LinkedVariableValue(name, selector) => (ll._1, List(IFValueCase(BinaryOperation(op, ll._2, rr))))
                    case other => {
                        val rr2 = Utils.simplifyToVariable(right)
                        (ll._1:::rr2._1, List(IFValueCase(BinaryOperation(op, ll._2, rr2._2))))
                    }
            }

            case BinaryOperation(op, lft, rght) => {
                op match{
                    case "&&" => {
                        val left = getIfCase(lft)
                        val right = getIfCase(rght)
                        if (left._2.head == IFFalse || right._2.head == IFFalse) then {
                            (List(), List(IFFalse))
                        }
                        else if (left._2.head == IFTrue){
                            right
                        }
                        else if (left._2.head == IFTrue){
                            left
                        }
                        else{
                            if (right._1.isEmpty){
                                (left._1:::right._1, left._2:::right._2)
                            }
                            else{
                                val vari = context.getFreshVariable(BoolType)
                                val exec1 = makeExecute(getListCase(left._2), 
                                            right._1 ::: makeExecute(getListCase(right._2), vari.assign("=", BoolValue(true))))
                                
                                (vari.assign("=", BoolValue(false)) ::: left._1 ::: exec1, List(IFValueCase(LinkedVariableValue(vari))))
                            }
                        }
                    }
                    case "||" => {
                        val left = getIfCase(lft)
                        val right = getIfCase(rght)
                        if (left._2.head == IFFalse && right._2.head == IFFalse) then {
                            (List(), List(IFFalse))
                        }
                        else if (right._2.head == IFTrue){
                            right
                        }
                        else if (left._2.head == IFTrue){
                            left
                        }
                        else{
                            val vari = context.getFreshVariable(BoolType)
                            val exec1 = makeExecute(getListCase(left._2), vari.assign("=", BoolValue(true)))
                            val exec2 = makeExecute(getListCase(List(IFNotValueCase(IFValueCase(LinkedVariableValue(vari))))), right._1 :::
                                                    makeExecute(getListCase(right._2), vari.assign("|=", BoolValue(true))))
                            (vari.assign("=", BoolValue(true)) ::: left._1 ::: exec1 ::: exec2, List(IFValueCase(LinkedVariableValue(vari))))
                        }
                    }
                    case "not in" => {
                        val (p, _, s) = Utils.getSelector(expr)
                        (p, List(IFValueCase(SelectorValue(s))))
                    }
                    case "in" => {
                        val (p, _, s) = Utils.getSelector(expr)
                        (p, List(IFValueCase(SelectorValue(s))))
                    }
                    case _ => {
                        val v = Utils.simplifyToVariable(expr)
                        (v._1, List(IFValueCase(v._2)))
                    }
                }
            }
            case UnaryOperation("!", left) => {
                val v = getIfCase(left)
                if v._2.length == 1 then 
                    (v._1, List(IFNotValueCase(v._2.head)))
                else
                    val v = Utils.simplifyToVariable(left)
                    (v._1, List(IFNotValueCase(IFValueCase(v._2))))
            }
            case UnaryOperation(op, left) => {
                val v = Utils.simplifyToVariable(expr)
                (v._1, List(IFValueCase(v._2)))
            }
            case FunctionCallValue(name, args, typeargs, sel) if Settings.metaVariable.exists(_._1 == name.toString()) => {
                if Settings.metaVariable.find(_._1 == name.toString()).get._2() then (List(), List(IFTrue)) else (List(), List(IFFalse))
            }
            case FunctionCallValue(name, args, typeargs, sel) if name.toString() == "Compiler.isEqualitySupported" => {
                val check = typeargs.head.isEqualitySupported()
                if check then (List(), List(IFTrue)) else (List(), List(IFFalse))
            }
            case FunctionCallValue(name, args, typeargs, sel) if name.toString() == "Compiler.isComparaisonSupported" => {
                val check = typeargs.head.isComparaisonSupported()
                if check then (List(), List(IFTrue)) else (List(), List(IFFalse))
            }
            case FunctionCallValue(name, args, typeargs, sel) if name.toString() == "Compiler.isVariable" => {
                val check = args.map(Utils.simplify).forall(arg =>
                    arg match
                        case VariableValue(name, sel) => true
                        case LinkedVariableValue(vari, sel) => true
                        case other => false
                    )
                if check then (List(), List(IFTrue)) else (List(), List(IFFalse))
            }
            case FunctionCallValue(name, args, typeargs, sel) if name.toString() == "loaded" && args.length > 0 => {
                if (Settings.target == MCJava){
                    args.map(Utils.simplify(_)) match
                        case (pos: PositionValue) :: Nil => (List(), List(IFLoaded(pos.getString())))
                        case (string: StringValue) :: Nil => (List(), List(IFLoaded(string.getString())))
                        case other => throw new Exception(f"Invalid argument to loaded: $other")
                }
                else{
                    throw new Exception(f"unsupported if loaded for target: ${Settings.target}")
                }
            }
            case FunctionCallValue(name, args, typeargs, sel) if name.toString() == "block" && args.length > 0 => {
                if (Settings.target == MCJava){
                    args.map(Utils.simplify(_)) match
                        case (pos:PositionValue)::(block: NamespacedName)::Nil => (List(), List(IFBlock(pos.getString()+" "+block.getString())))
                        case (block: NamespacedName)::Nil => (List(), List(IFBlock("~ ~ ~ "+block.getString())))
                        case (pos:PositionValue)::(block: StringValue)::Nil => (List(), List(IFBlock(pos.getString()+" "+block.getString())))
                        case (block: StringValue)::Nil => (List(), List(IFBlock("~ ~ ~ "+block.getString())))
                        case (pos:PositionValue)::TagValue(block)::Nil => (List(), List(IFBlock(pos.getString()+" "+context.getBlockTag(block).getTag())))
                        case TagValue(block)::Nil => (List(), List(IFBlock("~ ~ ~ "+context.getBlockTag(block).getTag())))
                        case (pos:PositionValue)::LinkedTagValue(block)::Nil => (List(), List(IFBlock(pos.getString()+" "+block.getTag())))
                        case LinkedTagValue(block)::Nil => (List(), List(IFBlock("~ ~ ~ "+block.getTag())))

                        case (pos:StringValue)::(block: NamespacedName)::Nil => (List(), List(IFBlock(pos.getString()+" "+block.getString())))
                        case (pos:StringValue)::(block: StringValue)::Nil => (List(), List(IFBlock(pos.getString()+" "+block.getString())))
                        case (pos:StringValue)::TagValue(block)::Nil => (List(), List(IFBlock(pos.getString()+" "+context.getBlockTag(block).getTag())))
                        case (pos:StringValue)::LinkedTagValue(block)::Nil => (List(), List(IFBlock(pos.getString()+" "+block.getTag())))

                        case other => throw new Exception(f"Invalid argument to block: $other")
                }
                else if (Settings.target == MCBedrock){
                    args.map(Utils.simplify(_)) match
                        case (pos:PositionValue)::(block: NamespacedName)::Nil => (List(), List(IFBlock(pos.getString()+" "+BlockConverter.getBlockName(block.getString())+" "+BlockConverter.getBlockID(block.getString()))))
                        case (block: NamespacedName)::Nil => (List(), List(IFBlock("~ ~ ~ "+BlockConverter.getBlockName(block.getString())+" "+BlockConverter.getBlockID(block.getString()))))

                        case (pos:PositionValue)::(block: StringValue)::Nil => (List(), List(IFBlock(pos.getString()+" "+BlockConverter.getBlockName(block.getString())+" "+BlockConverter.getBlockID(block.getString()))))
                        case (block: StringValue)::Nil => (List(), List(IFBlock("~ ~ ~ "+BlockConverter.getBlockName(block.getString())+" "+BlockConverter.getBlockID(block.getString()))))

                        case (pos:PositionValue)::TagValue(block)::Nil => {
                            val tag = context.getBlockTag(block)
                            val prev = makeExecute(f => PositionedIR(pos.getString(), f), tag.testFunction.call(List(), null, Selector.self, "="))
                            val (p, c) = getIfCase(LinkedVariableValue(tag.testFunction.returnVariable))
                            (prev ::: p, c)
                        }
                        case TagValue(block)::Nil => {
                            val tag = context.getBlockTag(block)
                            val prev = tag.testFunction.call(List(), null, Selector.self, "=")
                            val (p, c) = getIfCase(LinkedVariableValue(tag.testFunction.returnVariable))
                            (prev ::: p, c)
                        }
                        case (pos:PositionValue)::LinkedTagValue(tag)::Nil => {
                            val prev = makeExecute(f => PositionedIR(pos.getString(), f), tag.testFunction.call(List(), null, Selector.self, "="))
                            val (p, c) = getIfCase(LinkedVariableValue(tag.testFunction.returnVariable))
                            (prev ::: p, c)
                        }
                        case LinkedTagValue(tag)::Nil => {
                            val prev = tag.testFunction.call(List(), null, Selector.self, "=")
                            val (p, c) = getIfCase(LinkedVariableValue(tag.testFunction.returnVariable))
                            (prev ::: p, c)
                        }

                        case (pos:StringValue)::(block: NamespacedName)::Nil => (List(), List(IFBlock(pos.getString()+" "+BlockConverter.getBlockName(block.getString())+" "+BlockConverter.getBlockID(block.getString()))))
                        case (pos:StringValue)::(block: StringValue)::Nil => (List(), List(IFBlock(pos.getString()+" "+BlockConverter.getBlockName(block.getString())+" "+BlockConverter.getBlockID(block.getString()))))

                        case (pos:StringValue)::TagValue(block)::Nil => {
                            val tag = context.getBlockTag(block)
                            val prev = makeExecute(f => PositionedIR(pos.getString(), f), tag.testFunction.call(List(), null, Selector.self, "="))
                            val (p, c) = getIfCase(LinkedVariableValue(tag.testFunction.returnVariable))
                            (prev ::: p, c)
                        }
                        case (pos:StringValue)::LinkedTagValue(tag)::Nil => {
                            val prev = makeExecute(f => PositionedIR(pos.getString(), f), tag.testFunction.call(List(), null, Selector.self, "="))
                            val (p, c) = getIfCase(LinkedVariableValue(tag.testFunction.returnVariable))
                            (prev ::: p, c)
                        }

                        case other => throw new Exception(f"Invalid argument to block: $other")
                }
                else {
                    throw new Exception(f"unsupported if block for target: ${Settings.target}")
                }
            }
            case FunctionCallValue(name, args, typeargs, sel) if name.toString() == "blocks" && args.length > 0 => {
                args.map(Utils.simplify(_)) match
                    case (pos1: PositionValue)::(pos2: PositionValue)::(pos3: PositionValue)::StringValue(mask)::Nil => (List(), List(IFBlocks(pos1.getString()+" "+pos2.getString()+" "+pos3.getString()+" "+mask)))
                    case (pos1: PositionValue)::(pos2: PositionValue)::(pos3: PositionValue)::Nil => (List(), List(IFBlocks(pos1.getString()+" "+pos2.getString()+" "+pos3.getString()+" all")))
                    case other => throw new Exception(f"Invalid argument to blocks: $other")
            }
            case FunctionCallValue(VariableValue(name, sel), args, typeargs, _) => {
                val predicate = context.tryGetPredicate(name, args.map(Utils.typeof(_)))
                predicate match
                    case None => {
                        val (p, v) = Utils.simplifyToLazyVariable(expr)
                        val (p2, c) = getIfCase(v)
                        (p:::p2, c)
                    }
                    case Some(value) => {
                        (List(), List(IFPredicate(value.call(args))))
                    }
            }
            case FunctionCallValue(name, args, typeargs, sel) => {
                val (p, v) = Utils.simplifyToLazyVariable(expr)
                val (p2, c) = getIfCase(v)
                (p:::p2, c)
            }
            case ArrayGetValue(name, index) => {
                val v = Utils.simplifyToVariable(expr)
                (v._1, List(IFValueCase(v._2)))
            }
            case clz : ClassValue => {
                val (p, ctx, sel) = Utils.getSelector(clz)
                val (p2, c) = getIfCase(SelectorValue(sel))
                (p:::p2, c)
            }
            case LambdaValue(args, instr, ctx) => (List(), List(IFTrue))
            case StringValue(string) => throw new Exception("Can't use if with string")
            case JsonValue(json) => throw new Exception("Can't use if with json")
            case RangeValue(min, max, delta) => throw new Exception("Can't use if with range")
            case TupleValue(values) => throw new Exception("Can't use if with tuple")
            case RawJsonValue(value) => throw new Exception("Can't use if with rawjson")
            case NamespacedName(value, json) => throw new Exception("Can't use if with mcobject")
            case ConstructorCall(name, args, generics) => throw new Exception("Can't use if with constructor call")
            case pos: PositionValue => {
                if (Settings.target == MCJava){
                    (List(), List(IFLoaded(pos.getString())))
                }
                else{
                    throw new Exception(f"unsupported if position for target: ${Settings.target}")
                }
            }
            case TagValue(value) => throw new Exception("Can't use if with tag")
            case LinkedTagValue(value) => throw new Exception("Can't use if with tag")
            case ForSelect(expr, filter, selector) => {
                filter match{
                    case "all" => {
                        val vari = context.getFreshVariable(BoolType)
                        val prev = vari.assign("=", BoolValue(true)) ::: Compiler.compile(With(selector, BoolValue(true), BoolValue(true), If(UnaryOperation("!", expr), VariableAssigment(List((Right(vari), Selector.self)), "=", BoolValue(false)), List()), null))(context)
                        val (p, c) = getIfCase(LinkedVariableValue(vari))
                        (prev ::: p, c)
                    }
                    case "none" => {
                        val vari = context.getFreshVariable(BoolType)
                        val prev = vari.assign("=", BoolValue(false)) ::: Compiler.compile(With(selector, BoolValue(true), BoolValue(true), If(expr, VariableAssigment(List((Right(vari), Selector.self)), "=", BoolValue(true)), List()), null))(context)
                        val (p, c) = getIfCase(LinkedVariableValue(vari))
                        (prev ::: p, c)
                    }
                    case "any" => {
                        val vari = context.getFreshVariable(BoolType)
                        val prev = vari.assign("=", BoolValue(false)) ::: Compiler.compile(With(selector, BoolValue(true), BoolValue(true), If(expr, VariableAssigment(List((Right(vari), Selector.self)), "=", BoolValue(true)), List()), null))(context)
                        val (p, c) = getIfCase(LinkedVariableValue(vari))
                        (prev ::: p, c)
                    }
                }
                
            }
            case other => throw new Exception(f"unsupported if value: $other")
    }
    catch{
        e => {
            Reporter.error(f"${e.getMessage()} at ${expr.pos}\n${expr.pos.longString}")
            throw e
        }
    }
    def flattenSwitchCase(ori: List[SwitchElement])(implicit context: Context) = {
        ori.flatMap(c => 
            c match{
                case SwitchCase(expr, instr, cond) => List(SwitchCase(expr, instr, cond))
                case SwitchForGenerate(key, provider, SwitchCase(expr, instr, cond)) => {
                    val gcases = Utils.getForgenerateCases(key, provider)
                    
                    gcases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(SwitchCase(expr, instr, cond))
                        {case (SwitchCase(expr, instr, cond), elm) => 
                            SwitchCase(Utils.subst(expr, elm._1, elm._2), Utils.subst(instr, elm._1, elm._2), Utils.subst(cond, elm._1, elm._2))}
                    ).toList
                }
                case SwitchForEach(key, provider, SwitchCase(expr, instr, cond)) => {
                    val cases = Utils.getForeachCases(key.toString(), provider)
                    
                    var index = -1
                    cases.map(v =>{
                        val ctx = context.getFreshContext()
                        
                        val mod = Modifier.newPrivate()
                        mod.isLazy = true

                        v.map(v => {
                            val vari = new Variable(ctx, v._1, Utils.typeof(v._2), mod)
                            ctx.addVariable(Identifier.fromString(v._1), vari)
                            vari.assign("=", v._2)
                        })

                        val indx = new Variable(ctx, "index", IntType, mod)
                        ctx.push(v.head._1).addVariable(Identifier.fromString("index"), indx)
                        index += 1
                        indx.assign("=", IntValue(index))

                        SwitchCase(Utils.fix(expr)(ctx, Set()), Utils.fix(instr)(ctx, Set()), Utils.fix(cond)(ctx, Set()))
                    }).toList
                }
            }
        )
    }
    def switchComp(left: Expression, right: Expression, cond: Expression)(implicit context: Context): Expression = {
        if (cond == BoolValue(true)){
            Utils.typeof(right) match{
                case RangeType(_) => BinaryOperation("in", left, right)
                case _ => BinaryOperation("==", left, right)
            }
        }
        else{
            BinaryOperation("&&", cond, switchComp(left, right, BoolValue(true)))
        }
    }
    def switch(swit: Switch)(implicit context: Context):List[IRTree] = {
        swit.value match
            case ForSelect(expr, filter, selector) => {
                switch(Switch(expr, swit.cases.map(c => SwitchForEach(Identifier.fromString(filter), selector, c.asInstanceOf[SwitchCase]))))
            }
            case other => {        
                val expr = Utils.simplify(swit.value)
                Utils.typeof(expr) match
                    case IntType | FloatType | BoolType | StringType | FuncType(_, _) | EnumType(_) | StructType(_, _) | ClassType(_, _) => {
                        val cases2 = flattenSwitchCase(swit.cases).map(c => SwitchCase(Utils.simplify(c.expr), c.instr, Utils.simplify(c.cond)))

                        val hasDefaultCase = cases2.exists(c => c.expr == DefaultValue || c.cond != BoolValue(true))
                        var prev = List[IRTree]()
                        var variDone: Variable = null
                        val cases = if (!hasDefaultCase) cases2 else {
                            variDone = context.getFreshVariable(BoolType)
                            prev = variDone.assign("=", BoolValue(true))
                            cases2.filterNot(_.expr == DefaultValue).map(x => SwitchCase(x.expr, InstructionList(List(VariableAssigment(List((Right(variDone), Selector.self)), "=", BoolValue(false)), x.instr)), x.cond))
                        }
                        val defaultCases = cases2.filter(_.expr == DefaultValue)

                        prev ::: (expr match{
                            case VariableValue(name, sel) if !swit.copyVariable=> {
                                makeTree(context.getVariable(name), cases.par.filter(_.expr.hasIntValue()).map(x => (x.expr.getIntValue(), x.instr, x.cond)).toList):::
                                cases.par.filter(!_.expr.hasIntValue()).flatMap(x => ifs(If(switchComp(VariableValue(name), x.expr, x.cond), x.instr, List()))).toList
                            }
                            case LinkedVariableValue(vari, sel) if !swit.copyVariable=> {
                                makeTree(vari, cases.par.filter(_.expr.hasIntValue()).map(x => (x.expr.getIntValue(), x.instr, x.cond)).toList):::
                                cases.par.filter(!_.expr.hasIntValue()).flatMap(x => ifs(If(switchComp(LinkedVariableValue(vari), x.expr, x.cond), x.instr, List()))).toList
                            }
                            case IntValue(value) => {
                                val tail = cases.par.filter(!_.expr.hasIntValue()).toList
                                val head = cases.par.filter(c => c.expr.hasIntValue() && c.expr.getIntValue() == value).flatMap(x => Compiler.compile(x.instr)).toList
                                if (tail.isEmpty){
                                    head
                                }
                                else{
                                    val vari = context.getFreshVariable(Utils.typeof(swit.value))
                                    vari.assign("=", swit.value) ::: head ::: 
                                    tail.flatMap(x => ifs(If(switchComp(LinkedVariableValue(vari), x.expr, x.cond), x.instr, List())))
                                }
                            }
                            case FloatValue(value) => {
                                val tail = cases.filter(!_.expr.hasFloatValue())
                                val head = cases.filter(c => c.expr.hasFloatValue() && c.expr.getFloatValue() == value).flatMap(x => Compiler.compile(x.instr))
                                if (tail.isEmpty){
                                    head
                                }
                                else{
                                    val vari = context.getFreshVariable(Utils.typeof(swit.value))
                                    vari.assign("=", swit.value) ::: head ::: 
                                    tail.flatMap(x => ifs(If(switchComp(LinkedVariableValue(vari), x.expr, x.cond), x.instr, List())))
                                }
                            }
                            case EnumIntValue(value) => {
                                val tail = cases.filter(!_.expr.hasIntValue())
                                val head = cases.filter(c => c.expr.hasIntValue() && c.expr.getIntValue() == value).flatMap(x => Compiler.compile(x.instr))
                                if (tail.isEmpty){
                                    head
                                }
                                else{
                                    val vari = context.getFreshVariable(Utils.typeof(swit.value))
                                    vari.assign("=", swit.value) ::: head ::: 
                                    tail.flatMap(x => ifs(If(switchComp(LinkedVariableValue(vari), x.expr, x.cond), x.instr, List())))
                                }
                            }
                            case _ => {
                                val vari = context.getFreshVariable(Utils.typeof(swit.value))
                                vari.assign("=", swit.value) ::: makeTree(vari, cases.filter(_.expr.hasIntValue()).map(x => (x.expr.getIntValue(), x.instr, x.cond)))::: 
                                cases.filter(!_.expr.hasIntValue()).flatMap(x => ifs(If(switchComp(LinkedVariableValue(vari), x.expr, x.cond), x.instr, List())))
                            }
                        })::: defaultCases.flatMap(x =>
                            ifs(If(BinaryOperation("&&", x.cond, LinkedVariableValue(variDone)), x.instr, List()))
                        )
                    }
                    case TupleType(sub) => {
                        def getHead(expr: Expression): Expression = {
                            expr match
                                case TupleValue(values) if values.isDefinedAt(0) => values.head
                                case VariableValue(name, selector) => getHead(LinkedVariableValue(context.getVariable(name), selector))
                                case LinkedVariableValue(vari, selector) if vari.canBeReduceToLazyValue => {
                                    getHead(vari.lazyValue)
                                }
                                case LinkedVariableValue(vari, selector) => {
                                    LinkedVariableValue(vari.tupleVari.head)
                                }
                                case other => throw new Exception(f"Not a valid tupple: $other for a switch")
                        }
                        def getTail(expr: Expression): Expression = {
                            expr match
                                case TupleValue(values) => TupleValue(values.tail)
                                case VariableValue(name, selector) => getTail(LinkedVariableValue(context.getVariable(name), selector))
                                case LinkedVariableValue(vari, selector) if vari.canBeReduceToLazyValue => {
                                    getTail(vari.lazyValue)
                                }
                                case LinkedVariableValue(vari, selector) => {
                                    val TupleType(inner) = vari.getType(): @unchecked
                                    val fake = Variable(vari.context, vari.name, TupleType(inner.tail), vari.modifiers)
                                    fake.tupleVari = vari.tupleVari.tail
                                    LinkedVariableValue(fake)
                                }
                                case other => throw new Exception(f"Not a valid tupple: $other for a switch")
                        }
                        
                        if (sub.size == 1){
                            switch(Switch(getHead(expr), flattenSwitchCase(swit.cases).map(c => SwitchCase(getHead(c.expr), c.instr, c.cond))))
                        }
                        else{
                            val cases = flattenSwitchCase(swit.cases).groupBy(c => getHead(c.expr))
                                                .map((g, cases) => SwitchCase(g, Switch(getTail(expr), cases.map(c => SwitchCase(getTail(c.expr), c.instr, c.cond))), BoolValue(true)))
                                                .toList
                                                
                            switch(Switch(getHead(expr), cases))
                        }
                    }
                }
    }

    def makeTree(cond: Variable, values: List[(Int, Instruction, Expression)])(implicit context: Context):List[IRTree] = {
        if (values.length <= Settings.treeSize){
            values.flatMap((v, i, c) => ifs(If(
                    BinaryOperation("&&", c,BinaryOperation("==", LinkedVariableValue(cond), IntValue(v))), 
                    i,
                    List()
            )))
        }
        else{
            val values2 = values.sortBy(_._1)
            val length = values2.length
            val subTree = values2.zipWithIndex.groupBy(_._2 * Settings.treeSize / length).map((g,l)=>l).toList.sortBy(_.head._2)
            subTree.flatMap(x => {
                if (x.length > 1){
                    val block = context.getFreshBlock(makeTree(cond, x.map(p => p._1).sortBy(_._1)))
                    val min = x.map(p => p._1).head._1
                    val max = x.map(p => p._1).last._1
                    makeExecute(getListCase(List(IFValueCase(switchComp(LinkedVariableValue(cond), RangeValue(IntValue(min), IntValue(max), IntValue(1)), BoolValue(true))))), block.call(List()))
                }
                else{
                    makeExecute(getListCase(List(IFValueCase(switchComp(LinkedVariableValue(cond), IntValue(x.head._1._1), BoolValue(true))))), Compiler.compile(x.head._1._2))
                }
            }
            ).toList
        }
    }
}

def getListCase(lst: List[IFCase])(implicit context: Context): IRTree=>IRTree = {
    (x: IRTree) => lst.foldLeft(x)((f, g) => (g.get(f)))
}


trait IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree
}
case class IFValueCase(val value: Expression) extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = {
        value match{
            case SelectorValue(selector) => {
                IfEntity(selector.getString(), block)
            }
            case VariableValue(name, sel) => IFValueCase(context.resolveVariable(value)).get(block)
            case LinkedVariableValue(vari, sel) => {
                vari.getType() match{
                    case EntityType => IfEntity(f"@e[tag=${vari.tagName}]", block)
                    case _ => {
                        IfScoreboardMatch(
                            SBLink(vari.getSelectorName()(sel), vari.getSelectorObjective()(sel)), 
                            0, 
                            0,
                            block,
                            true)
                    }
                }
            }

            case BinaryOperation("==", LinkedVariableValue(left, sel1), StringValue(value)) => {
                IfStorage(left.getStoragePath(), f"{json:\"$value\"}", block)
            }
            case BinaryOperation("!=", LinkedVariableValue(left, sel1), StringValue(value)) => {
                IfStorage(left.getStoragePath(), f"{json:\"$value\"}", block, true)
            }

            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2))=> {
                val op = value.asInstanceOf[BinaryOperation].op
                val mcop = if op == "==" then "=" else op
                IfScoreboard(
                    SBLink(left.getSelectorName()(sel1), left.getSelectorObjective()(sel1)),
                    mcop,
                    SBLink(right.getSelectorName()(sel2), right.getSelectorObjective()(sel2)),
                    block)
            }
            case BinaryOperation("!=", LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2))=> {
                IfScoreboard(
                    SBLink(left.getSelectorName()(sel1), left.getSelectorObjective()(sel1)),
                    "=",
                    SBLink(right.getSelectorName()(sel2), right.getSelectorObjective()(sel2)),
                    block,
                    true)

            }


            case BinaryOperation(">", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    right.getIntValue()+1,
                    Int.MaxValue,
                    block)
            }
            case BinaryOperation(">=", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    right.getIntValue(),
                    Int.MaxValue,
                    block)
            }
            case BinaryOperation("<", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    Int.MinValue,
                    right.getIntValue() - 1,
                    block)
            }
            case BinaryOperation("<=", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    Int.MinValue,
                    right.getIntValue(),
                    block)
            }
            case BinaryOperation("==", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    right.getIntValue(),
                    right.getIntValue(),
                    block)
            }

            case BinaryOperation("==", right, LinkedVariableValue(left, sel)) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    right.getIntValue(),
                    right.getIntValue(),
                    block)
            }

            case BinaryOperation(op @ (">" | "<" | ">=" | "<="), right, LinkedVariableValue(left, sel)) if right.hasIntValue()=> {
                IFValueCase(BinaryOperation(Utils.invertOperator(op), LinkedVariableValue(left, sel), right)).get(block)
            }




            case BinaryOperation("!=", LinkedVariableValue(right, sel), left) if left.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(right.getSelectorName()(sel), right.getSelectorObjective()(sel)), 
                    left.getIntValue(),
                    left.getIntValue(), 
                    block,
                    true)
            }
            
            case BinaryOperation("!=", right, LinkedVariableValue(left, sel)) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    right.getIntValue(), 
                    right.getIntValue(), 
                    block,
                    true)
            }

            case BinaryOperation("==", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    right.getIntValue(), 
                    right.getIntValue(), 
                    block)
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(min, max, IntValue(1))) if min.hasIntValue() && max.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    min.getIntValue(), 
                    max.getIntValue(), 
                    block)
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(LinkedVariableValue(right, sel2), max, IntValue(1))) if max.hasIntValue()=> {
                IfScoreboard(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)),
                    ">=",
                    SBLink(right.getSelectorName()(sel2), right.getSelectorObjective()(sel2)),
                    IfScoreboardMatch(
                        SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                        Int.MinValue,
                        max.getIntValue(), 
                        block))
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(min, LinkedVariableValue(right, sel2), IntValue(1))) if min.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    min.getIntValue(), 
                    Int.MaxValue, 
                    IfScoreboard(
                        SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)),
                        "<=",
                        SBLink(right.getSelectorName()(sel2), right.getSelectorObjective()(sel2)),
                        block))
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(LinkedVariableValue(right1, sel1), LinkedVariableValue(right2, sel2), IntValue(1))) => {
                IfScoreboard(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)),
                    ">=",
                    SBLink(right1.getSelectorName()(sel1), right1.getSelectorObjective()(sel1)),
                    IfScoreboard(
                        SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)),
                        "<=",
                        SBLink(right2.getSelectorName()(sel2), right2.getSelectorObjective()(sel2)),
                        block))
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), value) if value.hasIntValue()=> {
                IfScoreboardMatch(
                    SBLink(left.getSelectorName()(sel), left.getSelectorObjective()(sel)), 
                    value.getIntValue(), 
                    value.getIntValue(), 
                    block)
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2)) => {
                right.getType() match
                    case RangeType(sub) => {
                        left.getType() match{
                            case other if other.isSubtypeOf(sub) => {
                                IfScoreboard(
                                    SBLink(right.tupleVari(0).getSelectorName()(sel2), right.tupleVari(0).getSelectorObjective()(sel2)),
                                    "<=",
                                    SBLink(left.getSelectorName()(sel1), left.getSelectorObjective()(sel1)),
                                    IfScoreboard(
                                        SBLink(left.getSelectorName()(sel1), left.getSelectorObjective()(sel1)),
                                        "<=",
                                        SBLink(right.tupleVari(1).getSelectorName()(sel2), right.tupleVari(1).getSelectorObjective()(sel2)),
                                        block))
                            }
                            case RangeType(sub2) => {
                                IfScoreboard(
                                    SBLink(right.tupleVari(0).getSelectorName()(sel2), right.tupleVari(0).getSelectorObjective()(sel2)),
                                    "<=",
                                    SBLink(left.tupleVari(0).getSelectorName()(sel1), left.tupleVari(0).getSelectorObjective()(sel1)),
                                    IfScoreboard(
                                        SBLink(left.tupleVari(1).getSelectorName()(sel1), left.tupleVari(1).getSelectorObjective()(sel1)),
                                        "<=",
                                        SBLink(right.tupleVari(1).getSelectorName()(sel2), right.tupleVari(1).getSelectorObjective()(sel2)),
                                        block))
                            }
                            case other => throw new Exception(f"ERROR: $other is not a subtype of $sub")
                        }
                    }
                    case other => throw new Exception(f"Unsupported operation: $value")
            }

            case BinaryOperation("in", IntValue(left), LinkedVariableValue(right, sel2)) => {
                right.getType() match
                    case RangeType(sub) => {
                        if (!IntType.isSubtypeOf(sub)){
                            throw new Exception(f"ERROR: int is not a subtype of $sub")
                        }
                        IfScoreboardMatch(
                            SBLink(right.tupleVari(0).getSelectorName()(sel2), right.tupleVari(0).getSelectorObjective()(sel2)),
                            left,
                            Int.MaxValue,
                            IfScoreboardMatch(
                                SBLink(right.tupleVari(1).getSelectorName()(sel2), right.tupleVari(1).getSelectorObjective()(sel2)),
                                Int.MinValue,
                                left,
                                block))
                    }
                    case other => throw new Exception(f"Unsupported operation: $value")
            }

            case BinaryOperation("in", SelectorValue(sel1), LinkedVariableValue(right, sel2)) if right.getType() == EntityType => {
                val sel = sel1.add("tag", SelectorIdentifier(right.tagName))
                IfEntity(sel.getString(), block)
            }
            case BinaryOperation("in", SelectorValue(sel1), SelectorValue(sel2)) => {
                IfEntity(sel1.merge(sel2).getString(), block)
            }
            case BinaryOperation("in", JsonValue(json), LinkedVariableValue(vari, sel2)) if vari.getType() == JsonType => {
                IfStorage(vari.getStoragePath(), json.getNbt(), block)
            }
            case BinaryOperation("in", StringValue(path), LinkedVariableValue(vari, sel2)) if vari.getType() == JsonType  => {
                IfStorage(vari.getStoragePath(), path, block)
            }

            case a => throw new Exception(f"ERROR IFVALUECASE NOT HANDLED: $a")
        }
    }
}

def getComparator(op: String, value: Int):String={
    op match
        case "<" => ".." + (value-1)
        case "<=" => ".." + (value)
        case ">" => (value+1) + ".."
        case ">=" => (value) + ".."
        case "==" => value.toString()
    
}

def getComparatorInverse(op: String, value: Int):String={
    op match
        case "<" => (value) + ".."
        case "<=" => (value+1) + ".."
        case ">" => ".." + (value)
        case ">=" => ".." + (value-1) 
        case "==" => value.toString()
    
}




case class IFNotValueCase(val value: IFCase) extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = {
        value.get(block) match{
            case IfScoreboard(left, op, right, block, invert) => IfScoreboard(left, op, right, block, !invert)
            case IfScoreboardMatch(left, min, max, block, invert) => IfScoreboardMatch(left, min, max, block, !invert)
            case IfEntity(selector, block, invert) => IfEntity(selector, block, !invert)
            case IfPredicate(predicate, block, invert) => IfPredicate(predicate, block, !invert)
            case IfBlock(value, block, invert) => IfBlock(value, block, !invert)
            case IfBlocks(value, block, invert) => IfBlocks(value, block, !invert)
            case IfLoaded(value, block, invert) => IfLoaded(value, block, !invert)
            case IfStorage(path, value, block, invert) => IfStorage(path, value, block, !invert)
            case EmptyIR => EmptyIR
            case other => other
        }
    }
}
case class IFPredicate(val value: String) extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = {
        IfPredicate(value, block, false)
    }
}
case class IFBlock(val value: String) extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = {
        IfBlock(value, block, false)
    }
}
case class IFBlocks(val value: String) extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = {
        IfBlocks(value, block, false)
    }
}
case class IFLoaded(val value: String) extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = {
        IfLoaded(value, block, false)
    }
}
case object IFTrue extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = block
}
case object IFFalse extends IFCase{
    def get(block: IRTree)(implicit context: Context): IRTree = EmptyIR
}