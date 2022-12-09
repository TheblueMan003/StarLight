package sl.Compilation
package Selector

import objects.Context
import objects.Identifier
import sl.Settings
import sl.MCBedrock
import sl.MCJava
import sl.JsonValue
import sl.JSONElement

object Selector{
    private val bedrockElement = List("x", "y", "z", "dx", "dy", "dz", "r", "rm", "scores", "tag", "name", "type", "familly", "rx", "rxm", "ry", "rym", "l", "lm", "m", "c")
    private val javaElement = List("x", "y", "z", "dx", "dy", "dz", "distance", "scores", "tag", "team", "name", "type", "predicate", "x_rotation‌", "y_rotation‌", "level", "gamemode", "advancements‌", "limit", "sort", "nbt")

    def parse(prefix: String, filters: List[(String, SelectorFilterValue)]): Selector = {
        if (filters.forall((k,v) => javaElement.contains(k))){
            JavaSelector(prefix, filters.toMap)
        }
        else if (filters.forall((k,v) => bedrockElement.contains(k))){
            BedrockSelector(prefix, filters.toMap)
        }
        else{
            throw new Exception(f"Invalid Selector: $prefix${filters}")
        }
    }
}
trait Selector{
    def getString()(implicit context: Context): String
    def makeUnique(): Selector
    def isPlayer: Boolean

    def makeRange(v1: SelectorFilterValue, v2: SelectorFilterValue): SelectorFilterValue = {
        SelectorRange(v1.asInstanceOf[SelectorNumber].value, v2.asInstanceOf[SelectorNumber].value)
    }

    def makeLower(v1: SelectorFilterValue): SelectorFilterValue = {
        SelectorLowerRange(v1.asInstanceOf[SelectorNumber].value)
    }

    def makeGreater(v1: SelectorFilterValue): SelectorFilterValue = {
        SelectorGreaterRange(v1.asInstanceOf[SelectorNumber].value)
    }
}

case class BedrockSelector(val prefix: String, val filters: Map[String, SelectorFilterValue]) extends Selector{
    override def getString()(implicit context: Context): String = {
        if (filters.size == 0){
            prefix
        }
        else if (Settings.target == MCBedrock){
            prefix + "[" + filters.map((k,v) => f"$k=$v").reduce(_ + "," + _) + "]"
        }
        else if (Settings.target == MCJava){
            prefix + "[" + filters.flatMap((k,v)=>(filterToJava(k, v, filters))).map((k,v) => f"$k=$v").reduce(_ + "," + _) + "]"
        }
        else{
            throw new Exception(f"Unsupported target: ${Settings.target}")
        }
    }
    def filterToJava(key: String, value: SelectorFilterValue, lst: Map[String, SelectorFilterValue]): List[(String, SelectorFilterValue)] = {
        key match{
            case "c" => List(("limit", value), ("sort", SelectorIdentifier("nearest")))
            case "r" => {
                lst.find(_._1 == "rm") match
                    case None => List(("distance", makeLower(value)))
                    case Some(v2) => List(("distance", makeRange(value, v2._2)))
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

    override def makeUnique(): Selector = ???
    override def isPlayer: Boolean = 
        prefix match{
            case "@p" => true
            case "@a" => true
            case _ => false
        }
    override def toString(): String = getString()(Context.getNew("default"))
}
case class JavaSelector(val prefix: String, val filters: Map[String, SelectorFilterValue]) extends Selector{
    override def getString()(implicit context: Context): String = {
        if (filters.size == 0){
            prefix
        }
        else if (Settings.target == MCJava){
            prefix + "[" + filters.map((k,v) => f"$k=$v").reduce(_ + "," + _) + "]"
        }
        else if (Settings.target == MCBedrock){
            prefix + "[" + filters.flatMap((k,v)=>(filterToBedrock(k, v, filters))).map((k,v) => f"$k=$v").reduce(_ + "," + _) + "]"
        }
        else{
            throw new Exception(f"Unsupported target: ${Settings.target}")
        }
    }

    def filterToBedrock(key: String, value: SelectorFilterValue, lst: Map[String, SelectorFilterValue]): List[(String, SelectorFilterValue)] = {
        key match{
            case "limit" => {
                value match
                    case SelectorNumber(value) => List(("c", SelectorNumber(value)))
            }
            case "distance" => {
                value match
                    case SelectorNumber(value) => List(("r", SelectorNumber(value)), ("rm", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("r", SelectorNumber(v2)), ("rm", SelectorNumber(v1)))
                    case SelectorGreaterRange(value) => List(("rm", SelectorNumber(value)))
                    case SelectorLowerRange(value) => List(("r", SelectorNumber(value)))
            }
            case "rotation_x" => {
                value match
                    case SelectorNumber(value) => List(("rx", SelectorNumber(value)), ("rxm", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("rx", SelectorNumber(v2)), ("rxm", SelectorNumber(v1)))
                    case SelectorGreaterRange(value) => List(("rxm", SelectorNumber(value)))
                    case SelectorLowerRange(value) => List(("rx", SelectorNumber(value)))
            }
            case "rotation_y" => {
                value match
                    case SelectorNumber(value) => List(("ry", SelectorNumber(value)), ("rym", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("ry", SelectorNumber(v2)), ("rym", SelectorNumber(v1)))
                    case SelectorGreaterRange(value) => List(("rym", SelectorNumber(value)))
                    case SelectorLowerRange(value) => List(("ry", SelectorNumber(value)))
            }
            case "level" => {
                value match
                    case SelectorNumber(value) => List(("l", SelectorNumber(value)), ("lm", SelectorNumber(value)))
                    case SelectorRange(v1, v2) => List(("l", SelectorNumber(v2)), ("lm", SelectorNumber(v1)))
                    case SelectorGreaterRange(value) => List(("lm", SelectorNumber(value)))
                    case SelectorLowerRange(value) => List(("l", SelectorNumber(value)))
            }
            case "predicate" | "advancements‌" | "sort" => {
                throw new Exception(f"$key not Supported for bedrock")
            }
            case _: String => List((key, value))
        }
    }

    override def makeUnique(): Selector = ???
    override def isPlayer: Boolean = 
        prefix match{
            case "@p" => true
            case "@a" => true
            case "@r" => true
            case _ => false
        }
    override def toString(): String = getString()(Context.getNew("default"))
}


trait SelectorFilterValue{
}
case class SelectorRange(val min: Double, val max: Double) extends SelectorFilterValue{
    override def toString(): String = f"$min..$max"
}
case class SelectorLowerRange(val max: Double) extends SelectorFilterValue{
    override def toString(): String = f"..$max"
}
case class SelectorGreaterRange(val min: Double) extends SelectorFilterValue{
    override def toString(): String = f"$min.."
}
case class SelectorNumber(val value: Double) extends SelectorFilterValue{
    override def toString(): String = f"$value"
}
case class SelectorString(val value: String) extends SelectorFilterValue{
    override def toString(): String = f"\"${value.replaceAll("\\\\","\\\\")}\""
}
case class SelectorIdentifier(val value: String) extends SelectorFilterValue{
    override def toString(): String = value
}

case class SelectorNbt(val value: JSONElement) extends SelectorFilterValue{
    override def toString(): String = value.getNbt()
}