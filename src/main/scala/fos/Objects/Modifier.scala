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
    var tags = ArrayBuffer[String]()
}

trait Protection
object Protection{
    case object Public extends Protection
    case object Protected extends Protection
    case object Private extends Protection
}