package sl.Compilation

import sl.{If, SmallValue, Expression, ElseIf}
import objects.{Context, Variable}
import objects.types.*
import sl.*
import objects.types.IntType
import objects.types.EntityType
import objects.Identifier
import objects.types.BoolType
import sl.Compilation.Selector.*
import scala.collection.parallel.CollectionConverters._
import sl.IR.*
import objects.Modifier

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
        def apply(keyword: IRTree=>IRTree)={
            val (pref2, cases) = getIfCase(ass.cond)
            val tail = getListCase(cases)
            Utils.simplify(ass.isat) match
                case BoolValue(true) => pref2:::makeExecute(keyword, makeExecute(x => AtIR("@s", x), makeExecute(tail, Compiler.compile(ass.block))))
                case BoolValue(false) => pref2:::makeExecute(keyword, makeExecute(tail, Compiler.compile(ass.block)))
                case other => {
                    val (pref, vari) = Utils.simplifyToVariable(other)
                    pref2:::pref ::: makeExecute(getListCase(List(IFValueCase(vari))), makeExecute(keyword, makeExecute(x => AtIR("@s", x), makeExecute(tail, Compiler.compile(ass.block)))))
                            ::: makeExecute(getListCase(List(IFNotValueCase(IFValueCase(vari)))), makeExecute(keyword, makeExecute(tail, Compiler.compile(ass.block))))
                }
        }

        ass.expr match{
            case VariableValue(Identifier(List("@attacker")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("attacker", x))
            case VariableValue(Identifier(List("@controller")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("controller", x))
            case VariableValue(Identifier(List("@leasher")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("leasher", x))
            case VariableValue(Identifier(List("@origin")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("origin", x))
            case VariableValue(Identifier(List("@owner")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("owner", x))
            case VariableValue(Identifier(List("@passengers")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("passengers", x))
            case VariableValue(Identifier(List("@target")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("target", x))
            case VariableValue(Identifier(List("@vehicle")), selector) if Settings.target.hasFeature("execute on") => apply(x => OnIR("vehicle", x))
            case VariableValue(variName, selector) => {
                val vari = context.getVariable(variName)
                vari.getType() match{
                    case EntityType if !vari.modifiers.isLazy => {
                        val (prefix, selector) = Utils.getSelector(ass.expr)
                        vari.tupleVari(0)
                        
                        prefix ::: makeExecute(f => IfScoreboardMatch(vari.tupleVari(0).getIRSelector(), 1, 1, f), apply(x => AsIR(selector.withPrefix("@a").getString(), x)))
                               ::: makeExecute(f => IfScoreboardMatch(vari.tupleVari(0).getIRSelector(), 0, 0, f), apply(x => AsIR(selector.getString(), x)))
                    }
                    case _ => {
                        val (prefix, selector) = Utils.getSelector(ass.expr)
                        prefix:::apply(x => AsIR(selector.getString(), x))
                    }
                }
            }
            case other => {
                val (prefix, selector) = Utils.getSelector(ass.expr)
                prefix:::apply(x => AsIR(selector.getString(), x))
            }
        }
    }
    def executeInstr(exec: Execute)(implicit context: Context):List[IRTree] = {
        exec.typ match
            case AtType => {
                Utils.simplify(exec.exprs.head) match
                    case PositionValue(value) => makeExecute(x => PositionedIR(value, x), Compiler.compile(exec.block))
                    case SelectorValue(value) => {
                        val (prefix, selector) = Utils.getSelector(exec.exprs.head)
                        prefix:::makeExecute(x => AtIR(selector.getString(), x), Compiler.compile(exec.block))
                    }
                    case VariableValue(Identifier(List("@world_surface")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"world_surface", x), Compiler.compile(exec.block))
                    case VariableValue(Identifier(List("@motion_blocking")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"motion_blocking", x), Compiler.compile(exec.block))
                    case VariableValue(Identifier(List("@motion_blocking_no_leaves")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"motion_blocking_no_leaves", x), Compiler.compile(exec.block))
                    case VariableValue(Identifier(List("@ocean_floor")), selector) if Settings.target.hasFeature("execute positioned over") => makeExecute(x => PositionedOverIR(f"ocean_floor", x), Compiler.compile(exec.block))
                    case VariableValue(vari, selector) => {
                        try{
                            val (prefix, selector) = Utils.getSelector(exec.exprs.head)
                            prefix:::makeExecute(x => AtIR(selector.getString(), x), Compiler.compile(exec.block))
                        }
                        catch{
                            _ => Compiler.compile(FunctionCall("__at__", exec.exprs ::: List(LinkedFunctionValue(context.getFreshBlock(Compiler.compile(exec.block)))), List()))
                        }
                    }
                    case LinkedVariableValue(vari, selector) if vari.getType() == EntityType => {
                        val (prefix, selector) = Utils.getSelector(exec.exprs.head)
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
                    makeExecute(x => RotatedIR(f"${a.getFloatValue()} ${b.getFloatValue()}", x), Compiler.compile(exec.block))
                }
                else{
                    val (prefix, selector) = Utils.getSelector(exec.exprs.head)
                    prefix:::makeExecute(x => RotatedEntityIR(selector.getString(), x), Compiler.compile(exec.block))
                }
            }
            case FacingType => {
                if (exec.exprs.length > 1){
                    val (prefix, selector) = Utils.getSelector(exec.exprs.head)
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
                        case PositionValue(value) => makeExecute(x => FacingIR(value, x), Compiler.compile(exec.block))
                        case _ => {
                            val (prefix, selector) = Utils.getSelector(exec.exprs.head)
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

            case IsType(left, typ) if Utils.typeof(Utils.simplify(left)) == context.getType(typ) => (List(), List(IFTrue))
            case IsType(left, typ) => (List(), List(IFFalse))

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
                if (vari.modifiers.isLazy){
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

            
            case BinaryOperation("in", value, LinkedVariableValue(vari, sel)) if vari.getType().isInstanceOf[RangeType] =>
                (List(), List(IFValueCase(BinaryOperation("in", value, LinkedVariableValue(vari, sel)))))

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
                    (List(), List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel))))))

            case BinaryOperation("!=", LinkedVariableValue(left, sel), NullValue) 
                if left.getType().isDirectEqualitable() => 
                    (List(), List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))

            case BinaryOperation("==", NullValue, LinkedVariableValue(left, sel)) 
                if left.getType().isDirectEqualitable() => 
                    (List(), List(IFNotValueCase(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel))))))

            case BinaryOperation("!=", NullValue, LinkedVariableValue(left, sel)) 
                if left.getType().isDirectEqualitable() => 
                    (List(), List(IFValueCase(BinaryOperation("==", LinkedVariableValue(left, sel), LinkedVariableValue(left, sel)))))

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
                rr match
                    case RangeValue(min, max, IntValue(1)) => (ll._1, List(IFValueCase(BinaryOperation(op, ll._2, rr))))
                    case VariableValue(name, selector) => (ll._1, List(IFValueCase(BinaryOperation(op, ll._2, rr))))
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
                            (left._1:::right._1, left._2:::right._2)
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
                        val (p, s) = Utils.getSelector(expr)
                        (p, List(IFValueCase(SelectorValue(s))))
                    }
                    case "in" => {
                        val (p, s) = Utils.getSelector(expr)
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
                        case PositionValue(pos)::Nil => (List(), List(IFLoaded(pos)))
                        case other => throw new Exception(f"Invalid argument to loaded: $other")
                }
                else{
                    throw new Exception(f"unsupported if loaded for target: ${Settings.target}")
                }
            }
            case FunctionCallValue(name, args, typeargs, sel) if name.toString() == "block" && args.length > 0 => {
                if (Settings.target == MCJava){
                    args.map(Utils.simplify(_)) match
                        case PositionValue(pos)::NamespacedName(block)::Nil => (List(), List(IFBlock(pos+" "+block)))
                        case NamespacedName(block)::Nil => (List(), List(IFBlock("~ ~ ~ "+block)))
                        case PositionValue(pos)::TagValue(block)::Nil => (List(), List(IFBlock(pos+" "+context.getBlockTag(block).getTag())))
                        case TagValue(block)::Nil => (List(), List(IFBlock("~ ~ ~ "+context.getBlockTag(block).getTag())))
                        case other => throw new Exception(f"Invalid argument to block: $other")
                }
                else if (Settings.target == MCBedrock){
                    args.map(Utils.simplify(_)) match
                        case PositionValue(pos)::NamespacedName(block)::Nil => (List(), List(IFBlock(pos+" "+BlockConverter.getBlockName(block)+" "+BlockConverter.getBlockID(block))))
                        case NamespacedName(block)::Nil => (List(), List(IFBlock("~ ~ ~ "+BlockConverter.getBlockName(block)+" "+BlockConverter.getBlockID(block))))
                        case PositionValue(pos)::TagValue(block)::Nil => {
                            val tag = context.getBlockTag(block)
                            val prev = makeExecute(f => PositionedIR(pos, f), tag.testFunction.call(List(), null, "="))
                            val (p, c) = getIfCase(LinkedVariableValue(tag.testFunction.returnVariable))
                            (prev ::: p, c)
                        }
                        case TagValue(block)::Nil => {
                            val tag = context.getBlockTag(block)
                            val prev = tag.testFunction.call(List(), null, "=")
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
                    case PositionValue(pos1)::PositionValue(pos2)::PositionValue(pos3)::StringValue(mask)::Nil => (List(), List(IFBlocks(pos1+" "+pos2+" "+pos3+" "+mask)))
                    case PositionValue(pos1)::PositionValue(pos2)::PositionValue(pos3)::Nil => (List(), List(IFBlocks(pos1+" "+pos2+" "+pos3+" all")))
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
            case LambdaValue(args, instr, ctx) => (List(), List(IFTrue))
            case StringValue(string) => throw new Exception("Can't use if with string")
            case JsonValue(json) => throw new Exception("Can't use if with json")
            case RangeValue(min, max, delta) => throw new Exception("Can't use if with range")
            case TupleValue(values) => throw new Exception("Can't use if with tuple")
            case RawJsonValue(value) => throw new Exception("Can't use if with rawjson")
            case NamespacedName(value) => throw new Exception("Can't use if with mcobject")
            case ConstructorCall(name, args, generics) => throw new Exception("Can't use if with constructor call")
            case PositionValue(value) => throw new Exception("Can't use if with position")
            case TagValue(value) => throw new Exception("Can't use if with tag")
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
                case SwitchCase(expr, instr) => List(SwitchCase(expr, instr))
                case SwitchForGenerate(key, provider, SwitchCase(expr, instr)) => {
                    val gcases = Utils.getForgenerateCases(key, provider)
                    
                    gcases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(SwitchCase(expr, instr))
                        {case (SwitchCase(expr, instr), elm) => 
                            SwitchCase(Utils.subst(expr, elm._1, elm._2), Utils.subst(instr, elm._1, elm._2))}
                    ).toList
                }
                case SwitchForEach(key, provider, SwitchCase(expr, instr)) => {
                    val cases = Utils.getForeachCases(key.toString(), provider)
                    
                    var index = -1
                    cases.flatMap(v =>{
                        val ctx = context.getFreshContext()
                        v.map(v => {
                            val mod = Modifier.newPrivate()
                            mod.isLazy = true
                            val vari = new Variable(ctx, "dummy", Utils.typeof(v._2), mod)
                            ctx.addVariable(Identifier.fromString(v._1), vari)
                            vari.assign("=", v._2)

                            val indx = new Variable(ctx, "dummy", IntType, mod)
                            ctx.push(v._1).addVariable(Identifier.fromString("index"), indx)
                            index += 1
                            indx.assign("=", IntValue(index))

                            SwitchCase(Utils.fix(expr)(ctx, Set()), Utils.fix(instr)(ctx, Set()))
                        })
                    }).toList
                }
            }
        )
    }
    def switch(swit: Switch)(implicit context: Context):List[IRTree] = {
        val expr = Utils.simplify(swit.value)
        Utils.typeof(expr) match
            case IntType | FloatType | BoolType | FuncType(_, _) | EnumType(_) => {
                val cases = flattenSwitchCase(swit.cases).map(c => SwitchCase(Utils.simplify(c.expr), c.instr))
                expr match{
                    case VariableValue(name, sel) if !swit.copyVariable=> {
                        makeTree(context.getVariable(name), cases.par.filter(_.expr.hasIntValue()).map(x => (x.expr.getIntValue(), x.instr)).toList):::
                        cases.par.filter(!_.expr.hasIntValue()).flatMap(x => ifs(If(BinaryOperation("in", VariableValue(name), x.expr), x.instr, List()))).toList
                    }
                    case LinkedVariableValue(vari, sel) if !swit.copyVariable=> {
                        makeTree(vari, cases.par.filter(_.expr.hasIntValue()).map(x => (x.expr.getIntValue(), x.instr)).toList):::
                        cases.par.filter(!_.expr.hasIntValue()).flatMap(x => ifs(If(BinaryOperation("in", LinkedVariableValue(vari), x.expr), x.instr, List()))).toList
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
                            tail.flatMap(x => ifs(If(BinaryOperation("in", LinkedVariableValue(vari), x.expr), x.instr, List())))
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
                            tail.flatMap(x => ifs(If(BinaryOperation("in", LinkedVariableValue(vari), x.expr), x.instr, List())))
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
                            tail.flatMap(x => ifs(If(BinaryOperation("in", LinkedVariableValue(vari), x.expr), x.instr, List())))
                        }
                    }
                    case _ => {
                        val vari = context.getFreshVariable(Utils.typeof(swit.value))
                        vari.assign("=", swit.value) ::: makeTree(vari, cases.filter(_.expr.hasIntValue()).map(x => (x.expr.getIntValue(), x.instr)))::: 
                        cases.filter(!_.expr.hasIntValue()).flatMap(x => ifs(If(BinaryOperation("in", LinkedVariableValue(vari), x.expr), x.instr, List())))
                    }
                }
            }
            case TupleType(sub) => {
                def getHead(expr: Expression): Expression = {
                    expr match
                        case TupleValue(values) => values.head
                        case VariableValue(name, selector) => getHead(LinkedVariableValue(context.getVariable(name), selector))
                        case LinkedVariableValue(vari, selector) => {
                            LinkedVariableValue(vari.tupleVari.head)
                        }
                        case other => throw new Exception(f"Not a valid tupple: $other for a switch")
                }
                def getTail(expr: Expression): Expression = {
                    expr match
                        case TupleValue(values) => TupleValue(values.tail)
                        case VariableValue(name, selector) => getTail(LinkedVariableValue(context.getVariable(name), selector))
                        case LinkedVariableValue(vari, selector) => {
                            val TupleType(inner) = vari.getType(): @unchecked
                            val fake = Variable(vari.context, vari.name, TupleType(inner.tail), vari.modifiers)
                            fake.tupleVari = vari.tupleVari.tail
                            LinkedVariableValue(fake)
                        }
                        case other => throw new Exception(f"Not a valid tupple: $other for a switch")
                }
                
                if (sub.size == 1){
                    switch(Switch(getHead(expr), flattenSwitchCase(swit.cases).map(c => SwitchCase(getHead(c.expr), c.instr))))
                }
                else{
                    val cases = flattenSwitchCase(swit.cases).groupBy(c => getHead(c.expr))
                                          .map((g, cases) => SwitchCase(g, Switch(getTail(expr), cases.map(c => SwitchCase(getTail(c.expr), c.instr)))))
                                          .toList
                                          
                    switch(Switch(getHead(expr), cases))
                }
            }
    }

    def makeTree(cond: Variable, values: List[(Int, Instruction)])(implicit context: Context):List[IRTree] = {
        if (values.length <= Settings.treeSize){
            values.flatMap((v, i) =>
                makeExecute(getListCase(List(IFValueCase(BinaryOperation("==", LinkedVariableValue(cond), IntValue(v))))), Compiler.compile(i))
            )
        }
        else{
            val length = values.length
            val subTree = values.zipWithIndex.groupBy(_._2 * Settings.treeSize / length).map((g,l)=>l).toList.sortBy(_.head._2)
            subTree.flatMap(x => {
                if (x.length > 1){
                    val block = context.getFreshBlock(makeTree(cond, x.map(p => p._1).sortBy(_._1)))
                    val min = x.map(p => p._1).head._1
                    val max = x.map(p => p._1).last._1
                    makeExecute(getListCase(List(IFValueCase(BinaryOperation("in", LinkedVariableValue(cond), RangeValue(IntValue(min), IntValue(max), IntValue(1)))))), block.call(List()))
                }
                else{
                    makeExecute(getListCase(List(IFValueCase(BinaryOperation("==", LinkedVariableValue(cond), IntValue(x.head._1._1))))), Compiler.compile(x.head._1._2))
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