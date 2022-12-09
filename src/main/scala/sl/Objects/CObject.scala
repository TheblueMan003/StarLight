package objects

class CObject(val context: Context, _name: String, _modifier: Modifier) {
  lazy val fullName = context.getPath() + "." + name
  lazy val fullPath = Identifier.fromString(fullName)
  val name = _name
  var modifiers = _modifier
}