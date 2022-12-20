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

object Execute{
    def ifs(ifb: If)(implicit context: Context): List[String] = {
        def isMulti(lst: List[((List[String], List[IFCase]), Instruction)]):Boolean = {
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
        var content = if multi then vari.assign("=", IntValue(0)) else List[String]()

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
                content = content ::: sl.Compiler.compile(inner)
                return content
            }
            else if (exp._2.head == IFFalse){
            }
            else{
                val block = (if multi && i != length-1 then vari.assign("=", IntValue(i+1)) else List()) ::: sl.Compiler.compile(inner)
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
    def doWhileLoop(whl: DoWhileLoop)(implicit context: Context):List[String] = {
        val block = context.getFreshBlock(Compiler.compile(whl.block))
        block.append(ifs(If(whl.cond, LinkedFunctionCall(block, List(), null), List())))
        block.call(List())
    }
    def whileLoop(whl: WhileLoop)(implicit context: Context):List[String] = {
        val block = context.getFreshBlock(Compiler.compile(whl.block))
        block.append(ifs(If(whl.cond, LinkedFunctionCall(block, List(), null), List())))
        ifs(If(whl.cond, LinkedFunctionCall(block, List(), null), List()))
    }
    def withInstr(ass: With)(implicit context: Context):List[String] = {
        def apply(selector: String)={
            val (pref2, cases) = getIfCase(ass.cond)
            val tail = getListCase(cases)
            Utils.simplify(ass.isat) match
                case BoolValue(true) => pref2:::makeExecute(f" as $selector at @s"+tail, Compiler.compile(ass.block))
                case BoolValue(false) => pref2:::makeExecute(f" as $selector"+tail, Compiler.compile(ass.block))
                case other => {
                    val (pref, vari) = Utils.simplifyToVariable(other)
                    pref2:::pref ::: makeExecute(getListCase(List(IFValueCase(vari))) + f" as $selector at @s"+tail, Compiler.compile(ass.block))
                            ::: makeExecute(getListCase(List(IFNotValueCase(IFValueCase(vari)))) + f" as $selector"+tail, Compiler.compile(ass.block))
                }
        }

        val (prefix, selector) = Utils.getSelector(ass.expr)
        prefix:::apply(selector.getString())
    }
    def atInstr(ass: At)(implicit context: Context):List[String] = {
        def apply(selector: String)={
            makeExecute(f" at $selector", Compiler.compile(ass.block))
        }

        Utils.simplify(ass.expr) match
            case PositionValue(value) => makeExecute(f" positioned $value", Compiler.compile(ass.block))
            case _ => {
                val (prefix, selector) = Utils.getSelector(ass.expr)
                prefix:::apply(selector.getString())
            }
    }

    /**
     * call "block" with execute with content: "prefix"
     */
    def makeExecute(prefix: String, block: List[String])(implicit context: Context): List[String] = {
        if (prefix == "" || prefix == " "){
            if (block.length == 1){
                List(block.head)
            }
            else if (block.length > 1){
                val fct = context.getFreshBlock(block)
                val call = fct.call(List()).head
                List(call)
            }
            else{
                List()
            }
        }
        else if (block.length == 1){
            List("execute"+prefix+" run "+ block.head)
        }
        else if (block.length > 1){
            val fct = context.getFreshBlock(block)
            val call = fct.call(List()).head
            List("execute"+prefix+" run "+ call)
        }
        else{
            List()
        }
    }


    

    /**
     * Concat List of IFCase to String
     */
    private def getListCase(expr: List[IFCase])(implicit context: Context): String = {
        expr.filter(_ != IFTrue).filter(_ != IFFalse).map(_.get()).foldLeft("")(_ + " " + _)
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
    private def getIfCase(expr: Expression)(implicit context: Context): (List[String], List[IFCase]) = {
        expr match
            case IntValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case EnumIntValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case FloatValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case BoolValue(value) => if value then (List(), List(IFTrue)) else (List(), List(IFFalse))
            case DefaultValue => (List(), List(IFFalse))
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
            case BinaryOperation(op, VariableValue(left, sel), right) =>
                getIfCase(BinaryOperation(op, context.resolveVariable(VariableValue(left, sel)), right))
            case BinaryOperation(op, left, VariableValue(right, sel)) =>
                getIfCase(BinaryOperation(op, left, context.resolveVariable(VariableValue(right, sel))))

            // Tuple
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

            // Special Case for class
            case BinaryOperation("==" | "!=", LinkedVariableValue(left, sel), LinkedVariableValue(right, sel2)) 
                if left.getType().isDirectEqualitable() && right.name == "__ref" => 
                    (List(), List(IFValueCase(expr)))


            // Comparable Value (class/struct)
            case BinaryOperation(">" | "<" | ">=" | "<=", LinkedVariableValue(left, sel), right)
                if left.getType().isComparaisonSupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(left.fullName + "." + Utils.getOpFunctionName(op)), List(right)))

            case BinaryOperation(">" | "<" | ">=" | "<=", left, LinkedVariableValue(right, sel)) 
                if right.getType().isComparaisonSupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(right.fullName + "." + Utils.getOpFunctionName(Utils.invertOperator(op))), List(left)))


            // Directly Equilable Value (int, float, bool, function, etc...)
            case BinaryOperation("==" | "!=", LinkedVariableValue(left, sel), right) 
                if left.getType().isEqualitySupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(left.fullName + "." + Utils.getOpFunctionName(op)), List(right)))
            
            case BinaryOperation("==" | "!=", left, LinkedVariableValue(right, sel)) 
                if right.getType().isEqualitySupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    getIfCase(FunctionCallValue(VariableValue(right.fullName + "." + Utils.getOpFunctionName(op)), List(left)))

            // Error Cases
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", LinkedVariableValue(left, sel), right) 
                if left.getType().isEqualitySupported() && left.getType().isComparaisonSupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    throw new Exception(f"Operation $op not supported for $left and $right")
            
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left, LinkedVariableValue(right, sel)) 
                if right.getType().isEqualitySupported() && right.getType().isComparaisonSupported() => 
                    val op = expr.asInstanceOf[BinaryOperation].op
                    throw new Exception(f"Operation $op not supported for $left and $right")

            

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

            case BinaryOperation("in", left, right)=> {
                val op = expr.asInstanceOf[BinaryOperation].op
                val ll = Utils.simplifyToVariable(left)
                val rr = Utils.simplify(right)
                rr match
                    case RangeValue(min, max) => (ll._1, List(IFValueCase(BinaryOperation(op, ll._2, rr))))
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
            case FunctionCallValue(name, args, sel) if Settings.metaVariable.exists(_._1 == name.toString()) => {
                if Settings.metaVariable.find(_._1 == name.toString()).get._2() then (List(), List(IFTrue)) else (List(), List(IFFalse))
            }
            case FunctionCallValue(name, args, sel) if name.toString() == "Compiler.isVariable" => {
                val check = args.forall(arg =>
                    arg match
                        case VariableValue(name, sel) => true
                        case LinkedVariableValue(vari, sel) => true
                        case other => false
                    )
                if check then (List(), List(IFTrue)) else (List(), List(IFFalse))
            }
            case FunctionCallValue(name, args, sel) if name.toString() == "block" && args.length > 0 => {
                if (Settings.target == MCJava){
                    args.map(Utils.simplify(_)) match
                        case PositionValue(pos)::NamespacedName(block)::Nil => (List(), List(IFBlock(pos+" "+block)))
                        case NamespacedName(block)::Nil => (List(), List(IFBlock("~ ~ ~ "+block)))
                        case other => throw new Exception(f"Invalid argument to block: $other")
                }
                else if (Settings.target == MCBedrock){
                    args.map(Utils.simplify(_)) match
                        case PositionValue(pos)::NamespacedName(block)::Nil => (List(), List(IFBlock(pos+" "+BlockConverter.getBlockName(block)+" "+BlockConverter.getBlockID(block))))
                        case NamespacedName(block)::Nil => (List(), List(IFBlock("~ ~ ~ "+BlockConverter.getBlockName(block)+" "+BlockConverter.getBlockID(block))))
                        case other => throw new Exception(f"Invalid argument to block: $other")
                }
                else {
                    throw new Exception(f"unsupported if block for target: ${Settings.target}")
                }
            }
            case FunctionCallValue(VariableValue(name, sel), args, _) => {
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
            case FunctionCallValue(name, args, sel) => {
                val (p, v) = Utils.simplifyToLazyVariable(expr)
                val (p2, c) = getIfCase(v)
                (p:::p2, c)
            }
            case ArrayGetValue(name, index) => {
                val v = Utils.simplifyToVariable(expr)
                (v._1, List(IFValueCase(v._2)))
            }
            case LambdaValue(args, instr) => (List(), List(IFTrue))
            case StringValue(string) => throw new Exception("Can't use if with string")
            case JsonValue(json) => throw new Exception("Can't use if with json")
            case RangeValue(min, max) => throw new Exception("Can't use if with range")
            case TupleValue(values) => throw new Exception("Can't use if with tuple")
            case RawJsonValue(value) => throw new Exception("Can't use if with rawjson")
            case NamespacedName(value) => throw new Exception("Can't use if with mcobject")
            case ConstructorCall(name, args) => throw new Exception("Can't use if with constructor call")
            case PositionValue(value) => throw new Exception("Can't use if with position")
    }

    def switch(swit: Switch)(implicit context: Context):List[String] = {
        val expr = Utils.simplify(swit.value)
        Utils.typeof(expr) match
            case IntType | FloatType | BoolType | FuncType(_, _) | EnumType(_) => {
                val cases = swit.cases.map(c => SwitchCase(Utils.simplify(c.expr), c.instr))
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
                    switch(Switch(getHead(expr), swit.cases.map(c => SwitchCase(getHead(c.expr), c.instr))))
                }
                else{
                    val cases = swit.cases.groupBy(c => getHead(c.expr))
                                          .map((g, cases) => SwitchCase(g, Switch(getTail(expr), cases.map(c => SwitchCase(getTail(c.expr), c.instr)))))
                                          .toList
                                          
                    switch(Switch(getHead(expr), cases))
                }
            }
    }

    def makeTree(cond: Variable, values: List[(Int, Instruction)])(implicit context: Context):List[String] = {
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
                    makeExecute(getListCase(List(IFValueCase(BinaryOperation("in", LinkedVariableValue(cond), RangeValue(IntValue(min), IntValue(max)))))), block.call(List()))
                }
                else{
                    makeExecute(getListCase(List(IFValueCase(BinaryOperation("==", LinkedVariableValue(cond), IntValue(x.head._1._1))))), Compiler.compile(x.head._1._2))
                }
            }
            ).toList
        }
    }
}



trait IFCase{
    def get()(implicit context: Context): String
}
case class IFValueCase(val value: Expression) extends IFCase{
    def get()(implicit context: Context): String = {
        value match{
            case SelectorValue(selector) => {
                f"if entity ${selector.getString()}"
            }
            case VariableValue(name, sel) => IFValueCase(context.resolveVariable(value)).get()
            case LinkedVariableValue(vari, sel) => {
                vari.getType() match{
                    case EntityType => f"if entity @e[tag=${vari.tagName}]"
                    case _ => f"unless score ${vari.getSelector()(sel)} matches 0"
                }
            }

            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2))=> {
                val op = value.asInstanceOf[BinaryOperation].op
                val mcop = if op == "==" then "=" else op
                f"if score ${left.getSelector()(sel1)} $mcop ${right.getSelector()(sel2)}"
            }
            case BinaryOperation("!=", LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2))=> {
                f"if score ${left.getSelector()(sel1)} = ${left.getSelector()(sel2)}"
            }


            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"if score ${left.getSelector()(sel)} matches ${getComparator(op, right.getIntValue())}"
            }
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", right, LinkedVariableValue(left, sel)) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"if score ${left.getSelector()(sel)} matches ${getComparatorInverse(op, right.getIntValue())}"
            }




            case BinaryOperation("!=", LinkedVariableValue(right, sel), left) if left.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${right.getSelector()(sel)} matches ${left.getIntValue()}"
            }
            
            case BinaryOperation("!=", right, LinkedVariableValue(left, sel)) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${left.getSelector()(sel)} matches ${right.getIntValue()}"
            }

            case BinaryOperation("==", LinkedVariableValue(left, sel), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${left.getSelector()(sel)} matches ${getComparator(op, right.getIntValue())}"
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(min, max)) if min.hasIntValue() && max.hasIntValue()=> {
                f"if score ${left.getSelector()(sel)} matches ${min.getIntValue()}..${max.getIntValue()}"
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(LinkedVariableValue(right, sel2), max)) if max.hasIntValue()=> {
                f"if score ${left.getSelector()(sel)} >= ${right.getSelector()(sel2)} if score ${left.getSelector()(sel)} matches ..${max.getIntValue()}"
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(min, LinkedVariableValue(right, sel2))) if min.hasIntValue()=> {
                f"if score ${left.getSelector()(sel)} matches ${min.getIntValue()}.. if score ${left.getSelector()(sel)} <= ${right.getSelector()(sel2)} "
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), RangeValue(LinkedVariableValue(right1, sel1), LinkedVariableValue(right2, sel2))) => {
                f"if score ${left.getSelector()(sel1)} >= ${right1.getSelector()(sel1)} if score ${left.getSelector()(sel1)} <= ${right2.getSelector()(sel2)} "
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel), value) if value.hasIntValue()=> {
                f"if score ${left.getSelector()(sel)} matches ${value.getIntValue()}..${value.getIntValue()}"
            }

            case BinaryOperation("in", LinkedVariableValue(left, sel1), LinkedVariableValue(right, sel2)) => {
                f"if score ${left.getSelector()(sel1)} = ${right.getSelector()(sel2)}"
            }

            case BinaryOperation("in", SelectorValue(sel1), LinkedVariableValue(right, sel2)) if right.getType() == EntityType => {
                val sel = sel1.add("tag", SelectorIdentifier(right.tagName))
                f"if entity ${sel.getString()}"
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
    def get()(implicit context: Context): String = {
        val ret = value.get()
        if (ret.startsWith("if")){
            "unless"+ret.substring(2)
        }
        else if (ret.startsWith("unless")){
            "if"+ret.substring(6)
        }
        else{
            ret
        }
    }
}
case class IFPredicate(val value: String) extends IFCase{
    def get()(implicit context: Context): String = {
        f"if predicate $value"
    }
}
case class IFBlock(val value: String) extends IFCase{
    def get()(implicit context: Context): String = {
        f"if block $value"
    }
}
case object IFTrue extends IFCase{
    def get()(implicit context: Context): String = ""
}
case object IFFalse extends IFCase{
    def get()(implicit context: Context): String = ""
}