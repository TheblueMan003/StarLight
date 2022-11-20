package fos.Compilation
package Selector

import objects.Context
import objects.Identifier
import fos.Settings
import fos.MCBedrock
import fos.MCJava

object Selector{
    private val bedrockElement = List("x", "y", "z", "dx", "dy", "dz", "r", "rm", "scores", "tag", "name", "type", "familly", "rx", "rxm", "ry", "rym", "l", "lm", "m", "c")
    private val javaElement = List("x", "y", "z", "dx", "dy", "dz", "distance", "scores", "tag", "team", "name", "type", "predicate", "x_rotation‌", "y_rotation‌", "level", "gamemode", "advancements‌", "limit", "sort")

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
            ???
        }
        else{
            throw new Exception(f"Unsupported target: ${Settings.target}")
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
            ???
        }
        else{
            throw new Exception(f"Unsupported target: ${Settings.target}")
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
case class SelectorNumber(val value: Double) extends SelectorFilterValue{
    override def toString(): String = f"$value"
}
case class SelectorString(val value: String) extends SelectorFilterValue{
    override def toString(): String = f"\"${value.replaceAll("\\\\","\\\\")}\""
}
case class SelectorIdentifier(val value: String) extends SelectorFilterValue{
    override def toString(): String = value
}