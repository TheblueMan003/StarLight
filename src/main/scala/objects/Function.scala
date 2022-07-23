package objects

import types.*

class Function(context: Context, name: String, arguments: List[Variable], typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
  
}
