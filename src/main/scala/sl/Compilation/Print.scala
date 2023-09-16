package sl.Compilation

import sl.Compilation.Selector.Selector
import objects.{Variable, Context}
import sl.Utils
import sl.*
import objects.types.*
import sl.IR.*

private lazy val colorMap = Utils.getConfig("color.csv")
                            .map(l => l.split(";").toList)
                            .filter(_.size == 3)
                            .map{case a :: b :: c :: Nil => (a, (b,c)); case _ =>throw new Exception("WHAT")}
                            .toMap

object Print{
    def toRawJson(expr: List[Expression])(implicit ctx: Context): (List[IRTree], RawJsonValue) = {
        val res = expr.map(toRawJson(_, true))
        val prefix = res.map(_._1).flatten
        val json1 = res.map(_._2).flatten
        (prefix, RawJsonValue(json1))
    }
    def toRawJson(expr: Expression, top: Boolean = false)(implicit ctx: Context): (List[IRTree], List[Printable])= {
        var mod = TextModdifier(false, false, false, false, false, null)
        var col = Namecolor("white")
        Utils.simplify(expr) match
            case DefaultValue => (List(), List(PrintString("default", col, mod)))
            case NullValue => (List(), List(PrintString("null", col, mod)))
            case IntValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case EnumIntValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case FloatValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case BoolValue(value) => (List(), List(PrintString(f"$value", col, mod)))
            case LinkedFunctionValue(fct)=> (List(), List(PrintString(f"${fct.fullName}", col, mod)))
            case PositionValue(x, y, z) => toRawJson(JsonValue(JsonArray(List(JsonExpression(x, null), JsonExpression(y, null), JsonExpression(z, null)))))
            case TagValue(value) => (List(), List(PrintString(f"#$value", col, mod)))
            case LinkedTagValue(value) => (List(), List(PrintString(f"#$value", col, mod)))
            case StringValue(value) => 
                val reg = "[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)+".r
                val matched = reg.findFirstMatchIn(value) 
                matched match
                    case Some(v) if v.matched == value => {
                        (List(), List(PrintTranslate(f"$value", RawJsonValue(List()), col, mod)))
                    }
                    case _ => 
                        val str = value.replace("\\","\\\\")
                        (List(), List(PrintString(f"$str", col, mod)))
            case value: NamespacedName => (List(), List(PrintString(f"${value.getString()}", col, mod)))
            case VariableValue(name, sel) => {
                ctx.tryGetVariable(name) match
                    case Some(vari) => {
                        toRawJson(LinkedVariableValue(vari, sel))
                    }
                    case None => {
                        ???
                    }
            }
            case ArrayGetValue(name, index) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2, print)
            }
            case LinkedVariableValue(vari, sel) => {
                if (vari.modifiers.isLazy){
                    toRawJson(vari.lazyValue)
                }
                else{
                    ctx.addScoreboardUsedForce(vari.getIRSelector())
                    vari.getType() match
                        case ArrayType(inner, size) => {
                            val (p1, print) = vari.tupleVari.map(v => toRawJson(LinkedVariableValue(v), false)(ctx))
                            .reduce((a,b) => 
                                (a._1:::b._1, a._2::: List(PrintString(f",", col, mod)) :::b._2))
                            (p1, List(PrintString(f"[", col, mod)) ::: print ::: List(PrintString(f"]", col, mod)))
                        }
                        case TupleType(inner) => {
                            val (p1, print) = vari.tupleVari.map(v => toRawJson(LinkedVariableValue(v), false)(ctx))
                            .reduce((a,b) => 
                                (a._1:::b._1, a._2::: List(PrintString(f",", col, mod)) :::b._2))
                            (p1, List(PrintString(f"(", col, mod)) ::: print ::: List(PrintString(f")", col, mod)))
                        }
                        case FloatType => {
                            val variLeft = ctx.getFreshVariable(IntType)
                            val upCal = variLeft.assign("=", LinkedVariableValue(vari, sel))
                            val digit1 = ctx.getFreshVariable(IntType)
                            val digit2 = ctx.getFreshVariable(IntType)
                            val digit3 = ctx.getFreshVariable(IntType)

                            val digit1Cal = digit1.assign("=", CastValue(LinkedVariableValue(vari, sel), IntType)) ::: digit1.assign("/=", IntValue(100)) ::: digit1.assign("%=", IntValue(10))
                            val digit2Cal = digit2.assign("=", CastValue(LinkedVariableValue(vari, sel), IntType)) ::: digit2.assign("/=", IntValue(10)) ::: digit2.assign("%=", IntValue(10))
                            val digit3Cal = digit3.assign("=", CastValue(LinkedVariableValue(vari, sel), IntType)) ::: digit3.assign("%=", IntValue(10))

                            val leftPrint = toRawJson(LinkedVariableValue(variLeft, sel))
                            val digit1Print = toRawJson(LinkedVariableValue(digit1, sel))
                            val digit2Print = toRawJson(LinkedVariableValue(digit2, sel))
                            val digit3Print = toRawJson(LinkedVariableValue(digit3, sel))

                            val rightPrint = List(PrintString(".", col, mod)) ::: digit1Print._2 ::: digit2Print._2 ::: digit3Print._2

                            (upCal ::: digit1Cal ::: digit2Cal ::: digit3Cal, leftPrint._2 ::: rightPrint)
                        }
                        case StringType => (List(), List(PrintNBT(vari, col, mod)))
                        case EntityType => (List(), List(PrintSelector(vari.getEntityVariableSelector(), col, mod)))
                        case other => (List(), List(PrintVariable(vari, sel, col, mod)))
                }
            }
            case dot: DotValue => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2, print)
            }
            case LambdaValue(args, instr, ctx) => throw new Exception("Cannot transform lambda to rawjson")
            case RawJsonValue(value) => (List(), value)
            case FunctionCallValue(name, args, typeargs, sel) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2, print)
            }
            case ConstructorCall(name, args, generics) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2, print)
            }
            case RangeValue(min, max, delta) => (List(), List(PrintString(f"$min..$max by $delta", col, mod)))
            case SelectorValue(value) => (List(), List(PrintSelector(value, col, mod)))
            case BinaryOperation(op, left, right) => {
                op match{
                    case "+" if Utils.typeof(left) == StringType || Utils.typeof(right) == StringType => {
                        val (p1, print1) = toRawJson(left)
                        val (p2, print2) = toRawJson(right)
                        (p1:::p2, print1:::print2)
                    }
                    case _ => {
                        val (p1, vari) = Utils.simplifyToVariable(expr)
                        val (p2, print) = toRawJson(vari)
                        (p1:::p2,print)
                    }
                }
            }
            case UnaryOperation(op, left) => {
                val (p1, vari) = Utils.simplifyToVariable(expr)
                val (p2, print) = toRawJson(vari)
                (p1:::p2,print)
            }
            case JsonValue(content) => toRawJson(StringValue(content.getString().replace("\"", "\\\"")))
            case InterpolatedString(content) => {
                content.map(v => toRawJson(v, false)(ctx)).reduce((a,b) => (a._1:::b._1, a._2:::b._2))
            }
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
                                    case StringValue(link) if link.startsWith("http") => mod.link = link
                                    case other => throw new Exception(f"Arugment for rawJson not support: $other")
                            }
                            def convert(print: Printable) = {
                                print match
                                    case PrintSelector(selector, color, modifier) => PrintSelector(selector, col, mod)
                                    case PrintString(text, color, modifier) => PrintString(text, col, mod)
                                    case PrintVariable(vari, sel, color, modifier) => {
                                        ctx.addScoreboardUsedForce(vari.getIRSelector())
                                        PrintVariable(vari, sel, col, mod)
                                    }
                                    case PrintTranslate(key, rjv, color, modifier) => PrintTranslate(key, rjv, col, mod)
                                    case PrintNBT(vari, color, modifier) => PrintNBT(vari, col, mod)
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
    def getLength()(implicit ctx: Context): Int
    def sub(size: Int)(implicit ctx: Context): Printable
}
object Printable{
    val empty = new PrintString("", Namecolor("white"), TextModdifier(false, false, false, false, false, null))
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
case class TextModdifier(var bold: Boolean, var obfuscated: Boolean, var strikethrough: Boolean, var underlined: Boolean, var italic: Boolean, var link: String){
    def toJava()(implicit ctx: Context): String = {
        var ret = ""
        ret += f"\"bold\":\"$bold\","
        ret += f"\"obfuscated\":\"$obfuscated\","
        ret += f"\"strikethrough\":\"$strikethrough\","
        ret += f"\"underlined\":\"$underlined\","
        if (link != null && link != ""){
            ret += f"\"clickEvent\":{\"action\":\"open_url\",\"value\":\"$link\"},"
        }
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
    def getLength()(implicit ctx: Context): Int = text.length
    def sub(size: Int)(implicit ctx: Context): Printable = {
        if (size <= 0) Printable.empty
        else if (size >= text.length) this
        else PrintString(text.substring(0, size), color, modifier)
    }
}
case class PrintVariable(val vari: Variable, val selector: Selector, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = 
        f"{\"score\": { \"name\": \"${vari.getSelectorName()(selector)}\", \"objective\": \"${vari.getSelectorObjective()(selector)}\"}, ${modifier.toJava()}, ${color.toJava()}}"
    def toBedrock()(implicit ctx: Context): String = {
        f"{\"score\": { \"name\": \"${vari.getSelectorName()(selector)}\", \"objective\": \"${vari.getSelectorObjective()(selector)}\"}}"
    }
    def getLength()(implicit ctx: Context): Int = 1
    def sub(size: Int)(implicit ctx: Context): Printable = 
        if (size >= 1) this else PrintString("", color, modifier)
}
case class PrintSelector(val selector: Selector, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = 
        f"{\"selector\": \"${selector.getString()}\", ${modifier.toJava()}, ${color.toJava()}}"
    def toBedrock()(implicit ctx: Context): String = {
        f"{\"selector\":\"${selector.getString()}\"}"
    }
    def getLength()(implicit ctx: Context): Int = 1
    def sub(size: Int)(implicit ctx: Context): Printable = 
        if (size >= 1) this else PrintString("", color, modifier)
}
case class PrintNBT(val vari: Variable, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = 
        f"{\"nbt\": \"${vari.getSelectorName()}\", \"storage\":\"${vari.getSelectorObjective().replaceFirst("\\.",":")}\",${modifier.toJava()}, ${color.toJava()}}"
    def toBedrock()(implicit ctx: Context): String = {
        ???
    }
    def getLength()(implicit ctx: Context): Int = 1
    def sub(size: Int)(implicit ctx: Context): Printable = 
        if (size >= 1) this else PrintString("", color, modifier)
}

case class PrintTranslate(val key: String, val rjv: RawJsonValue, val color: PrintColor, val modifier: TextModdifier) extends Printable{
    def toJava()(implicit ctx: Context): String = {
        val RawJsonValue(lst) = rjv
        if (lst.size > 0){
            f"{\"translate\":\"${key}\", \"with\":${rjv.getString()}}"
        }
        else{
            f"{\"translate\":\"${key}\"}"
        }
    }
    def toBedrock()(implicit ctx: Context): String = {
        val RawJsonValue(lst) = rjv
        if (lst.size > 0){
            f"{\"translate\":\"${key}\", \"with\":${rjv.getString()}}"
        }
        else{
            f"{\"translate\":\"${key}\"}"
        }
    }
    def getLength()(implicit ctx: Context): Int = 1
    def sub(size: Int)(implicit ctx: Context): Printable = 
        if (size >= 1) this else PrintString("", color, modifier)
}