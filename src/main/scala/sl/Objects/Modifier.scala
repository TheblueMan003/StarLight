package objects

import scala.collection.mutable.ArrayBuffer
import sl.*
import IR.CommentsIR
import sl.IR.IRTree

object Modifier {
  def newPrivate() = {
    val mod = new Modifier()
    mod.protection = Protection.Private
    mod
  }
  def newPublic() = {
    val mod = new Modifier()
    mod.protection = Protection.Public
    mod
  }
  def newProtected() = {
    val mod = new Modifier()
    mod.protection = Protection.Protected
    mod
  }
}

class Modifier() extends Serializable {
  var protection: Protection = Protection.Protected
  var isAbstract = false
  var isVirtual = false
  var isOverride = false
  var isLazy = false
  var isMacro = false
  var isEntity = false
  var isConst = false
  var isTicking = false
  var isLoading = false
  var isStatic = false
  var isHelper = false
  var isAsync = false
  var tags = ArrayBuffer[String]()
  var attributes = Map[String, Expression]()
  var doc: String = ""

  def getDocAsIR(): List[IRTree] = {
    if (Settings.exportDoc && doc != "") {
      List(CommentsIR("=" * 50 + "\n" + doc + "\n" + "=" * 50))
    } else {
      List()
    }
  }

  def simplify()(implicit context: Context) = {
    attributes =
      attributes.map(pair => (pair._1, Utils.simplify(pair._2)(context)))
    this
  }

  def combine(other: Modifier): Modifier = {
    val ret = Modifier()
    ret.protection = protection
    ret.isVirtual = isVirtual | other.isVirtual
    ret.isAbstract = isAbstract | other.isAbstract
    ret.isOverride = isOverride | other.isOverride
    ret.isLazy = isLazy | other.isLazy
    ret.isMacro = isMacro | other.isMacro
    ret.isEntity = isEntity | other.isEntity
    ret.isConst = isConst | other.isConst
    ret.isTicking = isTicking | other.isTicking
    ret.isLoading = isLoading | other.isLoading
    ret.isHelper = isHelper | other.isHelper
    ret.isStatic = isStatic | other.isStatic
    ret.isAsync = isAsync | other.isAsync
    ret.tags = tags ++ other.tags
    ret.attributes = attributes ++ other.attributes
    ret
  }
  def copy() = {
    combine(new Modifier())
  }

  def getAttributesString(key: String, default: () => String)(implicit
      context: Context
  ): String = {
    attributes.getOrElse(key, null) match{
      case null               => default()
      case StringValue(value) => value
      case other              => Utils.simplify(other)(context).toString()
    }
  }

  def getAttributesFloat(key: String, default: () => Double)(implicit
      context: Context
  ): Double = {
    attributes.getOrElse(key, null) match{
      case null              => default()
      case FloatValue(value) => value
      case IntValue(value)   => value.toDouble
      case other             => ???
    }
  }

  def hasAttributes(key: String)(implicit context: Context): Boolean = {
    attributes.getOrElse(key, BoolValue(false)) match{
      case BoolValue(value) => value
      case other            => Utils.simplify(other)(context) == BoolValue(true)
    }
  }

  def addAtrribute(key: String, value: Expression) = {
    attributes += (key -> value)
    this
  }

  def withDoc(text: Option[String]) = {
    text match{
      case None        => {}
      case Some(value) => doc += value
    }

    this
  }

  def schema() = {
    var ret = protection match{
      case Protection.Public    => "public"
      case Protection.Protected => ""
      case Protection.Private   => "private"
    }
    if (isAbstract) ret += " abstract"
    if (isVirtual) ret += " virtual"
    if (isOverride) ret += " override"
    if (isLazy) ret += " lazy"
    if (isEntity) ret += " entity"
    if (isConst) ret += " const"
    if (isTicking) ret += " ticking"
    if (isLoading) ret += " loading"
    if (isStatic) ret += " static"
    if (isHelper) ret += " helper"
    if (isAsync) ret += " async"
    if (tags.length > 0) ret += " " + tags.mkString(" ")
    if (attributes.size > 0)
      ret += " [" + attributes.map(a => s"${a._1}=${a._2}").mkString(",") + "]"
    ret
  }
}

trait Protection
object Protection {
  case object Public extends Protection
  case object Protected extends Protection
  case object Private extends Protection
}
