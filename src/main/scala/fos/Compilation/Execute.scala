package fos.Compilation

import fos.{If, SmallValue, Expression, ElseIf}
import objects.{Context, Variable}
import fos.*
import objects.types.IntType
import objects.types.EntityType

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
                    makeExecute(getListCase(List(IFNotValueCase(LinkedVariableValue(vari)))), exp._1)
            }

            if (exp._2.length == 1 && exp._2.head == IFTrue && !multi){
                content = content ::: fos.Compiler.compile(inner)
                return content
            }
            else if (exp._2.head == IFFalse){
            }
            else{
                val block = (if multi && i != length-1 then vari.assign("=", IntValue(i+1)) else List()) ::: fos.Compiler.compile(inner)
                if (i == 0){
                    content = content ::: makeExecute(getListCase(exp._2), block)
                }
                else{
                    content = content ::: makeExecute(getListCase(IFNotValueCase(LinkedVariableValue(vari)) :: exp._2), block)
                }
            }

            i += 1
        }
        content
    }

    /**
     * call "block" with execute with content: "prefix"
     */
    def makeExecute(prefix: String, block: List[String])(implicit context: Context): List[String] = {
        if (prefix == ""){
            List()
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
            case SelectorValue(value) => (List(), List(IFValueCase(expr)))
            case vari: VariableValue => (List(), List(IFValueCase(vari)))
            case vari: LinkedVariableValue => (List(), List(IFValueCase(vari)))
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
                        ???
                    }
                    case _ => {
                        val v = Utils.simplifyToVariable(expr)
                        (v._1, List(IFValueCase(v._2)))
                    }
                }
            }
            case FunctionCallValue(name, args) => {
                val v = Utils.simplifyToVariable(expr)
                (v._1, List(IFValueCase(v._2)))
            }
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

    def makeTree(cond: Variable, values: List[(Int, Instruction)])(implicit context: Context):List[String] = {
        if (values.length <= Settings.treeSize){
            values.flatMap((v, i) =>
                makeExecute(IFValueCase(BinaryOperation("==", LinkedVariableValue(cond), IntValue(v))).get(), Compiler.compile(i))
            )
        }
        else{
            val subTree = values.zipWithIndex.groupBy(_._2 * Settings.treeSize / values.length).map((g,l)=>l).toList.sortBy(_.head._2)
            subTree.flatMap(x => {
                if (x.length > 1){
                    val block = context.getFreshBlock(makeTree(cond, x.map(p => p._1).sortBy(_._1)))
                    val min = x.map(p => p._1).head._1
                    val max = x.map(p => p._1).last._1
                    makeExecute(IFValueCase(BinaryOperation("in", LinkedVariableValue(cond), RangeValue(IntValue(min), IntValue(max)))).get(), block.call(List()))
                }
                else{
                    makeExecute(IFValueCase(BinaryOperation("==", LinkedVariableValue(cond), IntValue(x.head._1._1))).get(), Compiler.compile(x.head._1._2))
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


            case BinaryOperation("!=", VariableValue(left), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${context.getVariable(left).getSelector()} matches ${getComparator(op, right.getIntValue())}"
            }
            case BinaryOperation("!=", right, VariableValue(left)) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${context.getVariable(left).getSelector()} matches ${getComparator(op, right.getIntValue())}"
            }

            case BinaryOperation("==", LinkedVariableValue(left), right) if right.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${left.getSelector()} matches ${getComparator(op, right.getIntValue())}"
            }

            case BinaryOperation("in", LinkedVariableValue(left), RangeValue(min, max)) if min.hasIntValue() && max.hasIntValue()=> {
                val op = value.asInstanceOf[BinaryOperation].op
                f"unless score ${left.getSelector()} matches ${min.getIntValue()}..${max.getIntValue()}"
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




case class IFNotValueCase(val value: Expression) extends IFCase{
    def get()(implicit context: Context): String = {
        val ret = IFValueCase(value).get()
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
case object IFTrue extends IFCase{
    def get()(implicit context: Context): String = ""
}
case object IFFalse extends IFCase{
    def get()(implicit context: Context): String = ""
}