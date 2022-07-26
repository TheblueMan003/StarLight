package sl.Compilation

import sl.Compilation.Selector.Selector
import objects.{Variable, Context}
import sl.Utils
import sl.*
import objects.types.EntityType


private val colorMap = Utils.getConfig("color.csv")
                            .map(l => l.split(";").toList)
                            .filter(_.size == 3)
                            .map{case a :: b :: c :: Nil => (a, (b,c)); case _ =>throw new Exception("WHAT")}
                            .toMap

object Print{
    def toRawJson(expr: List[Expression])(implicit ctx: Context): (List[String], RawJsonValue) = {
        val res = expr.map(toRawJson(_, true))
        val prefix = res.map(_._1).flatten
        val json1 = res.map(_._2).flatten
        (prefix, RawJsonValue(json1))
    }
    def toRawJson(expr: Expression, top: Boolean = false)(implicit ctx: Context): (List[String], List[Printable])= {
        var mod = TextModdifier(false, false, false, false, false)
        var col = Namecolor("white")
        expr match
            case DefaultValue => (List(), List(PrintString("default", col, mod)))
            case NullValue => (List(), List(PrintString("null", col, mod)))
            case IntValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case EnumIntValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case FloatValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case BoolValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case LinkedFunctionValue(fct)=> (List(), List(PrintString(f"${fct.fullName}", col, mod)))
            case PositionValue(value) => throw new Exception("Cannot use position inside rawjson")
            case StringValue(value) => 
                val reg = "[a-zA-Z0-9\\.]+".r
                reg.findFirstMatchIn(value) match
                    case Some(v) if v.matched == value => {
                        (List(), List(PrintTranslate(f"$value", RawJsonValue(List()), col, mod)))
                    }
                    case _ => (List(), List(PrintString(f"$value", col, mod)))
            case NamespacedName(value) => (List(), List(PrintString(f"$value", col, mod)))
            case VariableValue(name, sel) => {
                ctx.tryGetVariable(name) match
                    case Some(vari) => {
                        toRawJson(LinkedVariableValue(vari, sel))
                    }
                    case None => {
                        ???
                    }
            }
            case ArrayGetValue(name, index) => ???
            case LinkedVariableValue(vari, sel) => {
                if (vari.modifiers.isLazy){
                    toRawJson(vari.lazyValue)
                }
                else{
                    vari.getType() match
                        case EntityType => (List(), List(PrintSelector(vari.getEntityVariableSelector(), col, mod)))
                        case other => (List(), List(PrintVariable(vari, sel, col, mod)))
                }
            }
            case LambdaValue(args, instr) => throw new Exception("Cannot transform lambda to rawjson")
            case RawJsonValue(value) => (List(), value)
            case FunctionCallValue(name, args, sel) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2, print)
            }
            case ConstructorCall(name, args) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2, print)
            }
            case RangeValue(min, max) => (List(), List(PrintString(f"$min..$max", col, mod)))
            case SelectorValue(value) => (List(), List(PrintSelector(value, col, mod)))
            case BinaryOperation(op, left, right) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2,print)
            }
            case UnaryOperation(op, left) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2,print)
            }
            case JsonValue(content) => ???
            case TupleValue(values) => {
                if (top){
                    val (p2, print) = toRawJson(values(0))
                    print match
                        case PrintTranslate(k, rjv, c, m)::Nil => {
                            val (p3, rjv2) = toRawJson(values.drop(1))
                            (p2:::p3, List(PrintTranslate(k, rjv2, c, m)))
                        }
                        case _ =>{
                            def checkArg(value: Expression):Unit = {
                                Utils.simplify(value) match
                                    case StringValue("strikethrough") => mod.strikethrough = true
                                    case StringValue("bold") => mod.bold = true
                                    case StringValue("italic") => mod.italic = true
                                    case StringValue("obfuscated") => mod.obfuscated = true
                                    case StringValue("underlined") => mod.underlined = true
                                    case StringValue(color) if colorMap.contains(color) => col = Namecolor(color)
                                    case other => throw new Exception(f"Arugment for rawJson not support: $other")
                            }
                            def convert(print: Printable) = {
                                print match
                                    case PrintSelector(selector, color, modifier) => PrintSelector(selector, col, mod)
                                    case PrintString(text, color, modifier) => PrintString(text, col, mod)
                                    case PrintVariable(vari, sel, color, modifier) => PrintVariable(vari, sel, col, mod)
                            }
                            values.drop(1).foreach(checkArg(_));

                            (p2, print.map(convert(_)))
                        }
                
                }
                else{
                    values.map(toRawJson(_)).reduce((l,r) => (l._1 ::: r._1, l._2 ::: List(PrintString(f",", col, mod)) ::: r._2))
                }
            }
        
    }
}

trait Printable{
    def toBedrock()(implicit ctx: Context): String
    def toJava()(implicit ctx: Context): String
    def getString()(implicit ctx: Context): String = {
        Settings.target match
            case MCBedrock => toBedrock()
            case MCJava => toJava()
    }
}
trait PrintColor{
    def toBedrock()(implicit ctx: Context): String
    def toJava()(implicit ctx: Context): String
}
case class Namecolor(val color: String) extends PrintColor{
    def toJava()(implicit ctx: Context): String = {
        f"\"color\":\"${colorMap(color)(0)}\""
    }
    def toBedrock()(implicit ctx: Context): String = {
        colorMap(color)(1)
    }
}
case class TextModdifier(var bold: Boolean, var obfuscated: Boolean, var strikethrough: Boolean, var underlined: Boolean, var italic: Boolean){
    def toJava()(implicit ctx: Context): String = {
        var ret = ""
        ret += f"\"bold\":\"$bold\","
        ret += f"\"obfuscated\":\"$obfuscated\","
        ret += f"\"strikethrough\":\"$strikethrough\","
        ret += f"\"underlined\":\"$underlined\","
        ret += f"\"italic\":\"$italic\""
        ret
    }
    def toBedrock()(implicit ctx: Context): String = {
        var ret = ""
        if bold then ret += "§l"
        if obfuscated then ret += "§k"
        if strikethrough then ret += "§m"
        if underlined then ret += "§n"
        if italic then ret += "§o"
        ret
    }
}

case class PrintString(val text: String, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = 
        f"{\"text\": \"$text\", ${modifier.toJava()}, ${color.toJava()}}"
    def toBedrock()(implicit ctx: Context): String =
        f"{\"text\":\"§r${modifier.toBedrock()}§${color.toBedrock()}$text\"}"
}
case class PrintVariable(val vari: Variable, val selector: Selector, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = 
        f"{\"score\": { \"name\": \"${vari.getSelectorName()(selector)}\", \"objective\": \"${vari.getSelectorObjective()(selector)}\"}, ${modifier.toJava()}, ${color.toJava()}}"
    def toBedrock()(implicit ctx: Context): String = {
        f"{\"score\": { \"name\": \"${vari.getSelectorName()(selector)}\", \"objective\": \"${vari.getSelectorObjective()(selector)}\"}}"
    }
}
case class PrintSelector(val selector: Selector, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = 
        f"{\"selector\": \"${selector.getString()}\", ${modifier.toJava()}, ${color.toJava()}}"
    def toBedrock()(implicit ctx: Context): String = {
        f"{\"selector\":\"${selector.getString()}\"}"
    }
}

case class PrintTranslate(val key: String, val rjv: RawJsonValue, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = {
        val RawJsonValue(lst) = rjv
        if (lst.size > 0){
            f"{\"translate\":\"${key}\", \"with\":\"${rjv.getString()}\", ${modifier.toJava()}, ${color.toJava()}}"
        }
        else{
            f"{\"translate\":\"${key}\", ${modifier.toJava()}, ${color.toJava()}}"
        }
    }
    def toBedrock()(implicit ctx: Context): String = {
        val RawJsonValue(lst) = rjv
        if (lst.size > 0){
            f"{\"translate\":\"${key}\", \"with\":\"${rjv.getString()}\"}"
        }
        else{
            f"{\"translate\":\"${key}\"}"
        }
    }
}