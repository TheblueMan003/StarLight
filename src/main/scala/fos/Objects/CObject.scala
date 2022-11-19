package objects

class CObject(val context: Context, _name: String, _modifier: Modifier) {
  lazy val fullName = context.getPath() + "." + name
  val name = _name
  var modifiers = _modifier
}