package objects

class CObject(context: Context, _name: String, _modifier: Modifier) {
  val fullName = context.getPath() + "." + name
  val name = _name
  var modifiers = _modifier
}