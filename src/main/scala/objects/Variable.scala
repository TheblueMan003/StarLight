package objects

import types.*

class Variable(context: Context, name: String, typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
  
}