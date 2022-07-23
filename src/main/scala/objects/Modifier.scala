package objects

import scala.collection.mutable.ArrayBuffer

class Modifier(){
    var protection: Protection = Protection.Public
    var isOverride = false
    var isLazy = false
    var isInline = false
    var isEntity = false
    var tags = ArrayBuffer[String]()
}

trait Protection
object Protection{
    case object Public extends Protection
    case object Protected extends Protection
    case object Private extends Protection
}