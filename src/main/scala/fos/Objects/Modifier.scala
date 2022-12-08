package objects

import scala.collection.mutable.ArrayBuffer

object Modifier{
    def newPrivate()= {
        val mod = new Modifier()
        mod.protection = Protection.Private
        mod
    }
}

class Modifier(){
    var protection: Protection = Protection.Protected
    var isOverride = false
    var isLazy = false
    var isInline = false
    var isEntity = false
    var isConst = false
    var isTicking = false
    var isLoading = false
    var isHelper = false
    var tags = ArrayBuffer[String]()
    var attributes = Map[String,String]()

    def combine(other: Modifier): Modifier = {
        val ret = Modifier()
        ret.protection = protection
        ret.isOverride = isOverride | other.isOverride
        ret.isLazy = isLazy | other.isLazy
        ret.isInline = isInline | other.isInline
        ret.isEntity = isEntity | other.isEntity
        ret.isConst = isConst | other.isConst
        ret.isTicking = isTicking | other.isTicking
        ret.isLoading = isLoading | other.isLoading
        ret.isHelper = isHelper | other.isHelper
        ret.tags = tags ++ other.tags
        ret.attributes = attributes
        ret
    }
}

trait Protection
object Protection{
    case object Public extends Protection
    case object Protected extends Protection
    case object Private extends Protection
}