package objects

case class Property(name: String, val getter: Function, val setter: Function, val variable: Variable)