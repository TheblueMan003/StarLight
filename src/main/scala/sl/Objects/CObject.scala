package objects

class CObject(val context: Context, _name: String, _modifier: Modifier) {
  lazy val fullName = context.getPath() + "." + name
  lazy val fullPath = Identifier.fromString(fullName)
  val name = _name
  var modifiers = _modifier

  def isUseAllowed: Boolean = {
    if (modifiers.hasAttributes("bedrockOnly")(context) && sl.Settings.target != sl.MCBedrock)
    {
        return false
    }
    if (modifiers.hasAttributes("javaOnly")(context) && sl.Settings.target != sl.MCJava)
    {
        return false
    }
    return true
  }
}