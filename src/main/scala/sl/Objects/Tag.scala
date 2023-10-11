package objects

import objects.*
import sl.Settings
import sl.*
import sl.Utils
import sl.MCJava
import sl.MCBedrock
import objects.types.BoolType
import sl.IR.*

trait TagType {
  def path(fullName: String): String
}
case object BlockTag extends TagType {
  override def path(fullName: String): String =
    fullName.replaceFirst("\\.", ".tags.blocks.")

}
case object ItemTag extends TagType {
  override def path(fullName: String): String =
    fullName.replaceFirst("\\.", ".tags.items.")
}
case object EntityTag extends TagType {
  override def path(fullName: String): String =
    fullName.replaceFirst("\\.", ".tags.entity_types.")
}
class Tag(
    context: Context,
    _name: String,
    _modifier: Modifier,
    _content: List[Expression],
    typ: TagType
) extends CObject(context, _name, _modifier) {
    
  val content = _content
  def exists(): Boolean = Settings.target == MCJava
  def getContent(): List[JsonIR] = List(
    JsonIR(
      """{"values":[""" + content
        .map(v => Utils.stringify(v.getString()(Context.getNew(""))))
        .reduceOption(_ + ", " + _)
        .getOrElse("") + "]}"
    )
  )
  def getName(): String = Settings.target.getJsonPath(typ.path(fullName))
  def getTag(): String = "#" + Settings.target.getFunctionName(fullName)

  val testFunction = if (Settings.target == MCBedrock) {
    val sub = context.push(name)
    val mod = Modifier.newPublic()
    mod.attributes = mod.attributes + (("compile.order", IntValue(999999)))
    val body = Parser.parse(f"""
        _ret = false
        foreach(block in $name)
            if (block(block))_ret = true
        """)
    val fct = ConcreteFunction(
      sub,
      sub.getPath() + ".test",
      "test",
      List(),
      BoolType,
      mod,
      body,
      true
    )
    sub.addFunction("test", fct)
    fct.generateArgument()(context)
    fct
  } else {
    null
  }

  def getIRFile(): IRFile = {
    IRFile(getName(), name, getContent(), List(), true)
  }
}
