package sl.Compilation
package Selector

import objects.Context
import objects.Identifier
import sl.*

object Selector{
    private val bedrockElement = List("x", "y", "z", "dx", "dy", "dz", "r", "rm", "scores", "tag", "name", "type", "family", "rx", "rxm", "ry", "rym", "l", "lm", "m", "c", "hasitem")
    private val javaElement = List("x", "y", "z", "dx", "dy", "dz", "distance", "scores", "tag", "team", "name", "type", "predicate", "x_rotation‌", "y_rotation‌", "level", "gamemode", "advancements‌", "limit", "sort", "nbt")

    def parse(prefix: String, filters: List[(String, SelectorFilterValue)]): Selector = {
        if (filters.forall((k,v) => javaElement.contains(k))){
            JavaSelector(prefix, filters)
        }
        else if (filters.forall((k,v) => bedrockElement.contains(k))){
            BedrockSelector(prefix, filters)
        }
        else{
            throw new Exception(f"Invalid Selector: $prefix${filters}")
        }
    }

    def self = {
        JavaSelector("@s",List())
    }    
}
trait Selector{
    def getString()(implicit context: Context): String
    def makeUnique(): Selector
    def isPlayer: Boolean

    def makeRange(v1: SelectorFilterValue, v2: SelectorFilterValue): SelectorFilterValue = {
        SelectorRange(v1, v2)
    }

    def makeLower(v1: SelectorFilterValue): SelectorFilterValue = {
        SelectorLowerRange(v1)
    }

    def makeGreater(v1: SelectorFilterValue): SelectorFilterValue = {
        SelectorGreaterRange(v1)
    }

    def add(name: String, s: SelectorFilterValue):Selector

    def merge(s: Selector): Selector

    def invert(): Selector

    protected def mergePrefix(prefix1: String, prefix2: String): String = {
        prefix1 match{
            case "@s" => "@s"
            case "@p" => "@p"
            case "@r" => "@r"
            case "@a" => {
                prefix2 match{
                    case "@s" => "@s"
                    case "@p" => "@p"
                    case "@r" => "@r"
                    case _ => "@a"
                }
            }
            case "@e" => prefix2
        }
    }

    def withPrefix(prefix: String): Selector
    def fix(implicit context: Context, ignore: Set[Identifier]): Selector
    def subst(from: String, to: String): Selector
}
val EmptySelector = JavaSelector("@s", List(("tag", SelectorIdentifier("sl_empty"))))

case class BedrockSelector(val prefix: String, val filters: List[(String, SelectorFilterValue)]) extends Selector{
    override def getString()(implicit context: Context): String = {
        if (filters.size == 0){
            prefix
        }
        else if (Settings.target == MCBedrock){
            prefix + "[" + filters.map((k,v) => f"$k=${v.getString()}").reduce(_ + "," + _) + "]"
        }
        else if (Settings.target == MCJava){
            prefix + "[" + filters.flatMap((k,v)=>(filterToJava(k, v, filters))).map((k,v) => f"$k=${v.getString()}").reduce(_ + "," + _) + "]"
        }
        else{
            throw new Exception(f"Unsupported target: ${Settings.target}")
        }
    }
    override def fix(implicit context: Context, ignore: Set[Identifier]): Selector ={
        BedrockSelector(prefix, filters.map((k,v) => (k, v.fix(context, ignore))))
    }
    override def subst(from: String, to: String): Selector = {
        BedrockSelector(prefix, filters.map((k,v) => (k, v.subst(from, to))))
    }
    def filterToJava(key: String, value: SelectorFilterValue, lst: List[(String, SelectorFilterValue)]): List[(String, SelectorFilterValue)] = {
        key match{
            case "c" => List(("limit", value), ("sort", SelectorIdentifier("nearest")))
            case "r" => {
                lst.find(_._1 == "rm") match
                    case None => List(("distance", makeLower(value)))
                    case Some(v2) => List(("distance", makeRange(value, v2._2)))
            }
            case "m" => {
                List(("gamemode", value))
            }
            case "rx" => {
                lst.find(_._1 == "rxm") match
                    case None => List(("x_rotation‌", makeLower(value)))
                    case Some(v2) => List(("x_rotation‌", makeRange(value, v2._2)))
            }
            case "ry" => {
                lst.find(_._1 == "rym") match
                    case None => List(("y_rotation‌", makeLower(value)))
                    case Some(v2) => List(("y_rotation‌", makeRange(value, v2._2)))
            }
            case "l" => {
                lst.find(_._1 == "lm") match
                    case None => List(("level", makeLower(value)))
                    case Some(v2) => List(("level", makeRange(value, v2._2)))
            }


            case "rm" => {
                lst.find(_._1 == "r") match
                    case None => List(("distance", makeGreater(value)))
                    case Some(v2) => List()
            }
            case "rxm" => {
                lst.find(_._1 == "rx") match
                    case None => List(("x_rotation‌", makeGreater(value)))
                    case Some(v2) => List()
            }
            case "rym" => {
                lst.find(_._1 == "ry") match
                    case None => List(("y_rotation‌", makeGreater(value)))
                    case Some(v2) => List()
            }
           case "lm" => {
                lst.find(_._1 == "l") match
                    case None => List(("level", makeGreater(value)))
                    case Some(v2) => List()
            }
            case "familly" => throw new Exception("familly not supported on java")
            case _ => List((key, value))
        }
    }

    override def makeUnique(): Selector = BedrockSelector(prefix, ("c",SelectorNumber(1))::filters.filter((k,v) => k != "c"))
    override def isPlayer: Boolean = 
        prefix match{
            case "@p" => true
            case "@a" => true
            case _ => false
        }
    override def toString(): String = getString()(Context.getNew("default"))
    def add(name: String, s: SelectorFilterValue):Selector = {
        BedrockSelector(prefix, (name -> s)::filters)
    }
    def merge(s: Selector): Selector = {
        s match{
            case BedrockSelector(prefix2, filters2) => 
                BedrockSelector(mergePrefix(prefix, prefix2), filters ++ filters2)
            case jsel @ JavaSelector(prefix2, filters2) => 
                val bedrock = filters2.flatMap((k,v)=>(jsel.filterToBedrock(k, v, filters2)))
                BedrockSelector(mergePrefix(prefix, prefix2), filters ++ bedrock)
        }
    }
    def invert(): Selector = {
        if (filters.length == 0){
            EmptySelector
        }
        else{
            val newFilter = filters.map{ case (s, v) => 
                v match
                    case SelectorInvert(v2) => (s, v2)
                    case _ => (s, SelectorInvert(v))
            }
            BedrockSelector(prefix, newFilter)
        }
    }
    def withPrefix(prefix: String): Selector = BedrockSelector(prefix, filters)
}
case class JavaSelector(val prefix: String, val filters: List[(String, SelectorFilterValue)]) extends Selector{
    override def getString()(implicit context: Context): String = {
        if (filters.size == 0){
            prefix
        }
        else if (Settings.target == MCJava){
            prefix + "[" + filters.map((k,v) => f"$k=${v.getString()}").reduce(_ + "," + _) + "]"
        }
        else if (Settings.target == MCBedrock){
            prefix + "[" + filters.flatMap((k,v)=>(filterToBedrock(k, v, filters))).map((k,v) => f"$k=${v.getString()}").reduce(_ + "," + _) + "]"
        }
        else{
            throw new Exception(f"Unsupported target: ${Settings.target}")
        }
    }
    override def fix(implicit context: Context, ignore: Set[Identifier]): Selector ={
        JavaSelector(prefix, filters.map((k,v) => (k, v.fix(context, ignore))))
    }
    override def subst(from: String, to: String): Selector = {
        JavaSelector(prefix, filters.map((k,v) => (k, v.subst(from, to))))
    }
    def filterToBedrock(key: String, value: SelectorFilterValue, lst: List[(String, SelectorFilterValue)]): List[(String, SelectorFilterValue)] = {
        key match{
            case "limit" => {
                value match
                    case SelectorNumber(value) => List(("c", SelectorNumber(value)))
                    case SelectorIdentifier(value) => List(("c", SelectorIdentifier(value)))
            }
            case "distance" => {
                value match
                    case SelectorNumber(value) => List(("r", SelectorNumber(value)), ("rm", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("r", v2), ("rm", v1))
                    case SelectorGreaterRange(value) => List(("rm", value))
                    case SelectorLowerRange(value) => List(("r", value))
                    case SelectorIdentifier(value) => List(("r", SelectorIdentifier(value)), ("rm", SelectorIdentifier(value)))
            }
            case "rotation_x" => {
                value match
                    case SelectorNumber(value) => List(("rx", SelectorNumber(value)), ("rxm", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("rx", v2), ("rxm", v1))
                    case SelectorGreaterRange(value) => List(("rxm", value))
                    case SelectorLowerRange(value) => List(("rx", value))
                    case SelectorIdentifier(value) => List(("rx", SelectorIdentifier(value)), ("rxm", SelectorIdentifier(value)))
            }
            case "rotation_y" => {
                value match
                    case SelectorNumber(value) => List(("ry", SelectorNumber(value)), ("rym", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("ry", v2), ("rym", v1))
                    case SelectorGreaterRange(value) => List(("rym", value))
                    case SelectorLowerRange(value) => List(("ry", value))
                    case SelectorIdentifier(value) => List(("ry", SelectorIdentifier(value)), ("rym", SelectorIdentifier(value)))
            }
            case "level" => {
                value match
                    case SelectorNumber(value) => List(("l", SelectorNumber(value)), ("lm", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("l", v2), ("lm", v1))
                    case SelectorGreaterRange(value) => List(("lm", value))
                    case SelectorLowerRange(value) => List(("l", value))
                    case SelectorIdentifier(value) => List(("l", SelectorIdentifier(value)), ("lm", SelectorIdentifier(value)))
            }
            case "sort" if value == SelectorIdentifier("nearest") => List()
            case "predicate" | "advancements‌" | "sort" => {
                throw new Exception(f"$key not Supported for bedrock")
            }
            case "gamemode" => List(("m", value))
            case _: String => List((key, value))
        }
    }

    override def makeUnique(): Selector = BedrockSelector(prefix, ("limit",SelectorNumber(1))::filters.filter((k,v) => k != "limit"))
    override def isPlayer: Boolean = 
        prefix match{
            case "@p" => true
            case "@a" => true
            case "@r" => true
            case _ => false
        }
    override def toString(): String = getString()(Context.getNew("default"))
    def add(name: String, s: SelectorFilterValue):Selector = {
        JavaSelector(prefix, (name -> s)::filters)
    }
    def merge(s: Selector): Selector = {
        s match{
            case JavaSelector(prefix2, filters2) => 
                JavaSelector(mergePrefix(prefix, prefix2), filters ++ filters2)
            case bsel @ BedrockSelector(prefix2, filters2) => 
                val bedrock = filters2.flatMap((k,v)=>(bsel.filterToJava(k, v, filters2)))
                BedrockSelector(mergePrefix(prefix, prefix2), filters ++ bedrock)
        }
    }
    def invert(): Selector = {
        if (filters.length == 0){
            EmptySelector
        }
        else{
            val newFilter = filters.map{ case (s, v) => 
                v match
                    case SelectorInvert(v2) => (s, v2)
                    case _ => (s, SelectorInvert(v))
            }
            BedrockSelector(prefix, newFilter)
        }
    }
    def withPrefix(prefix: String): Selector = JavaSelector(prefix, filters)
}


trait SelectorFilterValue{
    def getString()(implicit context: Context): String
    def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue
    def subst(from: String, to: String): SelectorFilterValue
}
case class SelectorRange(val min: SelectorFilterValue, val max: SelectorFilterValue) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = f"${min.getString()}..${max.getString()}"
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = SelectorRange(min.fix, max.fix)
    override def subst(from: String, to: String): SelectorFilterValue = SelectorRange(min.subst(from, to), max.subst(from, to))
}
case class SelectorLowerRange(val max: SelectorFilterValue) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = f"..${max.getString()}"
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = SelectorLowerRange(max.fix)
    override def subst(from: String, to: String): SelectorFilterValue = SelectorLowerRange(max.subst(from, to))
}
case class SelectorGreaterRange(val min: SelectorFilterValue) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = f"${min.getString()}.."
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = SelectorGreaterRange(min.fix)
    override def subst(from: String, to: String): SelectorFilterValue = SelectorGreaterRange(min.subst(from, to))
}
case class SelectorNumber(val value: Double) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = 
        val int = value.toInt
        if int == value then f"$int" else f"$value"
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = this
    override def subst(from: String, to: String): SelectorFilterValue = this
}
case class SelectorString(val value: String) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = f"\"${value.replaceAll("\\\\","\\\\")}\""
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = this
    override def subst(from: String, to: String): SelectorFilterValue = SelectorString(value.replaceAll(from, to))
}
case class SelectorIdentifier(val value: String) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = {
        context.tryGetVariable(value) match
            case Some(vari) if vari.modifiers.isLazy => {
                vari.lazyValue match
                    case IntValue(n) => SelectorNumber(n).getString()
                    case FloatValue(n) => SelectorNumber(n).getString()
                    case n: NamespacedName => n.getString()
                    case StringValue(value) => SelectorString(value).getString()
                    case JsonValue(value) => SelectorNbt(value).getString()
                    case _ => value
            }
            case _ => value
    }
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = {
        context.tryGetVariable(value) match
            case Some(vari) if vari.modifiers.isLazy => {
                vari.lazyValue match
                    case IntValue(n) => SelectorNumber(n)
                    case FloatValue(n) => SelectorNumber(n)
                    case n: NamespacedName => SelectorString(n.getString())
                    case StringValue(value) => SelectorString(value)
                    case JsonValue(value) => SelectorNbt(value)
                    case other => throw new Exception(f"Lazy value not supported: $other")
            }
            case _ => this
    }
    override def subst(from: String, to: String): SelectorFilterValue = SelectorIdentifier(value.replaceAll(from, to))
}
case class SelectorTag(val value: Identifier) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = {
        context.getBlockTag(value).getTag()
    }
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = {
        SelectorIdentifier(context.getBlockTag(value).getTag())
    }
    override def subst(from: String, to: String): SelectorFilterValue = SelectorTag(Identifier.fromString(value.toString().replaceAll(from, to)))
}
case class SelectorNbt(val value: JSONElement) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = value.getNbt()
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = this
    override def subst(from: String, to: String): SelectorFilterValue = SelectorNbt(Utils.subst(value, from, to))
}

case class SelectorInvert(val value: SelectorFilterValue) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = f"!${value.getString()}"
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = SelectorInvert(value.fix)
    override def subst(from: String, to: String): SelectorFilterValue = SelectorInvert(value.subst(from, to))
}

case class SelectorComposed(val value: Map[String, SelectorFilterValue]) extends SelectorFilterValue{
    override def getString()(implicit context: Context): String = "{" + value.map((k,v) => f"$k=${v.getString()}").reduce(_ +", "+_) + "}"
    override def fix(implicit context: Context, ignore: Set[Identifier]): SelectorFilterValue = SelectorComposed(value.map((k,v) => (k, v.fix)))
    override def subst(from: String, to: String): SelectorFilterValue = SelectorComposed(value.map((k,v) => (k, v.subst(from, to))))
}