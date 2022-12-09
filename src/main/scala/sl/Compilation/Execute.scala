package sl.Compilation

import sl.{If, SmallValue, Expression, ElseIf}
import objects.{Context, Variable}
import sl.*
import objects.types.IntType
import objects.types.EntityType
import javax.rmi.CORBA.Util
import objects.Identifier
import objects.types.BoolType

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

        ass.expr match
            case VariableValue(name) => withInstr(With(LinkedVariableValue(context.getVariable(name)), ass.isat, ass.cond, ass.block))
            case LinkedVariableValue(vari) => 
                vari.getType() match
                    case EntityType => apply(f"@e[tag=${vari.tagName}]")
                    case _ => throw new Exception(f"Unexpected value in as $ass")
            case TupleValue(values) => values.flatMap(p => withInstr(With(p, ass.isat, ass.cond, ass.block)))
            case SelectorValue(value) => apply(value.getString())
                
        
            case BinaryOperation(op, left, right) => 
                val (prefix, vari) = Utils.simplifyToVariable(ass.expr)
                prefix ::: withInstr(With(vari, ass.isat, ass.cond, ass.block))
            case _ => throw new Exception(f"Unexpected value in as $ass")
    }
    def atInstr(ass: At)(implicit context: Context):List[String] = {
        def apply(selector: String)={
            makeExecute(f" @s $selector", Compiler.compile(ass.block))
        }

        Utils.simplify(ass.expr) match
            case VariableValue(name) => atInstr(At(LinkedVariableValue(context.getVariable(name)), ass.block))
            case LinkedVariableValue(vari) => 
                vari.getType() match
                    case EntityType => apply(f"@e[tag=${vari.tagName}]")
                    case _ => throw new Exception(f"Unexpected value in as $ass")
            case TupleValue(List(x, y, z)) if x.hasFloatValue() && y.hasFloatValue() && z.hasFloatValue() => {
                makeExecute(f" positioned ${x.getFloatValue()} ${y.getFloatValue()} ${z.getFloatValue()}", Compiler.compile(ass.block))
            }
            case TupleValue(values) => values.flatMap(p => atInstr(At(p, ass.block)))
            case SelectorValue(value) => apply(value.getString())
                
        
            case BinaryOperation(op, left, right) => 
                val (prefix, vari) = Utils.simplifyToVariable(ass.expr)
                prefix ::: atInstr(At(vari, ass.block))
            case _ => throw new Exception(f"Unexpected value in as $ass")
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


    /**
     * get IfCase from Expression
     */
    private def getIfCase(expr: Expression)(implicit context: Context): (List[String], List[IFCase]) = {
        expr match
            case IntValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case FloatValue(value) => if value == 0 then (List(), List(IFFalse)) else (List(), List(IFTrue))
            case BoolValue(value) => if value then (List(), List(IFTrue)) else (List(), List(IFFalse))
            case DefaultValue => (List(), List(IFFalse))
            case NullValue => (List(), List(IFFalse))
            case SelectorValue(value) => (List(), List(IFValueCase(expr)))
            case VariableValue(iden) => {
                val vari = context.getVariable(iden)
                if (vari.modifiers.isLazy){
                    getIfCase(vari.lazyValue)
                }
                else{
                    (List(), List(IFValueCase(LinkedVariableValue(vari))))
                }
            }
            case LinkedVariableValue(vari) => {
                if (vari.modifiers.isLazy){
                    getIfCase(vari.lazyValue)
                }
                else{
                    (List(), List(IFValueCase(expr)))
                }
            }
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", VariableValue(left), right) if right.hasIntValue() => (List(), List(IFValueCase(expr)))
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left, VariableValue(right)) if left.hasIntValue() => (List(), List(IFValueCase(expr)))

            case BinaryOperation(">" | "<" | ">=" | "<=" | "==" | "!=", left, right)=> {
                val op = expr.asInstanceOf[BinaryOperation].op
                val ll = Utils.simplifyToVariable(left)
                val rr = Utils.simplifyToVariable(right)
                (ll._1:::rr._1, List(IFValueCase(BinaryOperation(op, ll._2, rr._2))))
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
            case FunctionCallValue(name, args) if name.toString() == "Compiler.isBedrock" => {
                if Settings.target == MCBedrock then (List(), List(IFFalse)) else (List(), List(IFTrue))
            }
            case FunctionCallValue(name, args) if name.toString() == "Compiler.isJava" => {
                if Settings.target == MCJava then (List(), List(IFFalse)) else (List(), List(IFTrue))
            }
            case FunctionCallValue(name, args) if name.toString() == "Compiler.isVariable" => {
                val check = args.forall(arg =>
                    arg match
                        case VariableValue(name) => true
                        case LinkedVariableValue(vari) => true
                        case other => false
                    )
                if check then (List(), List(IFTrue)) else (List(), List(IFFalse))
            }
            case FunctionCallValue(name, args) if name.toString() == "block" && args.length > 0 => {
                if (args.length > 1){
                    (List(), List(IFBlock(args.map(_.getString()).reduce(_ +" "+ _))))
                }
                else{
                    (List(), List(IFBlock("~ ~ ~ "+args.head.toString())))
                }
            }
            case FunctionCallValue(VariableValue(name), args) => {
                val predicate = context.tryGetPredicate(name, args.map(Utils.typeof(_)))
                predicate match
                    case None => {
                        val v = Utils.simplifyToVariable(expr)
                        (v._1, List(IFValueCase(v._2)))
                    }
                    case Some(value) => {
                        (List(), List(IFPredicate(value.call(args))))
                    }
            }
            case FunctionCallValue(name, args) => {
                val v = Utils.simplifyToVariable(expr)
                (v._1, List(IFValueCase(v._2)))
            }
            case LambdaValue(args, instr) => (List(), List(IFTrue))
            case StringValue(string) => throw new Exception("Can't use if with string")
            case JsonValue(json) => throw new Exception("Can't use if with json")
            case RangeValue(min, max) => throw new Exception("Can't use if with range")
            case TupleValue(values) => throw new Exception("Can't use if with tuple")
    }

    def switch(swit: Switch)(implicit context: Context):List[String] = {
        val expr = Utils.simplify(swit.value)
        expr match{
            case VariableValue(name) if !swit.copyVariable=> {
                makeTree(context.getVariable(name), swit.cases.map(x => (x.expr.getIntValue(), x.instr)))
            }
            case LinkedVariableValue(vari) if !swit.copyVariable=> {
                makeTree(vari, swit.cases.map(x => (x.expr.getIntValue(), x.instr)))
            }
            case _ => {
                val vari = context.getFreshVariable(Utils.typeof(swit.value))
                vari.assign("=", swit.value) ::: makeTree(vari, swit.cases.map(x => (x.expr.getIntValue(), x.instr)))
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
            val subTree = values.zipWithIndex.groupBy(_._2 * Settings.treeSize / values.length).map((g,l)=>l).toList.sortBy(_.head._2)
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
            case VariableValue(name) => {
                IFValueCase(LinkedVariableValue(context.getVariable(name))).get()
            }
            case LinkedVariableValue(vari) => {
                vari.getType() match{
                    case EntityType => f"if entity @e[tag=${vari.tagName}]"
                    case _ => f"unless score ${vari.getSelector()} matches 0"
                }
            }

            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", LinkedVariableValue(left), LinkedVariableValue(right))=> {
                val op = value.asInstanceOf[BinaryOperation].op
                val mcop = if op == "==" then "=" else op
                f"if score ${left.getSelector()} $mcop ${right.getSelector()}"
            }
            case BinaryOperation("!=", LinkedVariableValue(left), LinkedVariableValue(right))=> {
                f"if score ${left.getSelector()} = ${left.getSelector()}"
            }

            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", VariableValue(left), VariableValue(right))=> {
                val op = value.asInstanceOf[BinaryOperation].op
                val mcop = if op == "==" then "=" else op
                f"if score ${context.getVariable(left).getSelector()} $mcop ${context.getVariable(right).getSelector()}"
            }
            case BinaryOperation("!=", VariableValue(left), VariableValue(right))=> {
                f"if score ${context.getVariable(left).getSelector()} = ${context.getVariable(left).getSelector()}"
            }


            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", VariableValue(left), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"if score ${context.getVariable(left).getSelector()} matches ${getComparator(op, right.getIntValue())}"
            }
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", right, VariableValue(left)) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"if score ${context.getVariable(left).getSelector()} matches ${getComparatorInverse(op, right.getIntValue())}"
            }
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", LinkedVariableValue(left), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"if score ${left.getSelector()} matches ${getComparator(op, right.getIntValue())}"
            }
            case BinaryOperation(">" | "<" | ">=" | "<=" | "==", right, LinkedVariableValue(left)) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"if score ${left.getSelector()} matches ${getComparatorInverse(op, right.getIntValue())}"
            }


            case BinaryOperation("!=", VariableValue(left), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${context.getVariable(left).getSelector()} matches ${right.getIntValue()}"
            }
            case BinaryOperation("!=", right, VariableValue(left)) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${context.getVariable(left).getSelector()} matches ${right.getIntValue()}"
            }

            case BinaryOperation("==", LinkedVariableValue(left), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${left.getSelector()} matches ${getComparator(op, right.getIntValue())}"
            }

            case BinaryOperation("in", LinkedVariableValue(left), RangeValue(min, max)) if min.hasIntValue() && max.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"if score ${left.getSelector()} matches ${min.getIntValue()}..${max.getIntValue()}"
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