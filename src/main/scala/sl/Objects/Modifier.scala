package objects

import scala.collection.mutable.ArrayBuffer
import sl.*

object Modifier{
    def newPrivate()= {
        val mod = new Modifier()
        mod.protection = Protection.Private
        mod
    }
    def newPublic()= {
        val mod = new Modifier()
        mod.protection = Protection.Public
        mod
    }
}

class Modifier(){
    var protection: Protection = Protection.Protected
    var isAbstract = false
    var isVirtual = false
    var isOverride = false
    var isLazy = false
    var isEntity = false
    var isConst = false
    var isTicking = false
    var isLoading = false
    var isStatic = false
    var isHelper = false
    var tags = ArrayBuffer[String]()
    var attributes = Map[String,Expression]()
    var doc: String = ""

    def combine(other: Modifier): Modifier = {
        val ret = Modifier()
        ret.protection = protection
        ret.isVirtual = isVirtual | other.isVirtual
        ret.isAbstract = isAbstract | other.isAbstract
        ret.isOverride = isOverride | other.isOverride
        ret.isLazy = isLazy | other.isLazy        
        ret.isEntity = isEntity | other.isEntity
        ret.isConst = isConst | other.isConst
        ret.isTicking = isTicking | other.isTicking
        ret.isLoading = isLoading | other.isLoading
        ret.isHelper = isHelper | other.isHelper
        ret.isStatic = isStatic | other.isStatic
        ret.tags = tags ++ other.tags
        ret.attributes = attributes ++ other.attributes
        ret
    }

    def getAttributesString(key: String, default: ()=>String)(implicit context: Context): String = {
        attributes.getOrElse(key, null) match
            case null => default()
            case StringValue(value) => value
            case other => Utils.simplify(other)(context).toString()
    }

    def hasAttributes(key: String)(implicit context: Context): Boolean = {
        attributes.getOrElse(key, BoolValue(false)) match
            case BoolValue(value) => value
            case other => Utils.simplify(other)(context) == BoolValue(true)
    }

    def withDoc(text: Option[String])={
        text match
            case None => {}
            case Some(value) => doc += value
        
        this
    }
}

trait Protection
object Protection{
    case object Public extends Protection
    case object Protected extends Protection
    case object Private extends Protection
}