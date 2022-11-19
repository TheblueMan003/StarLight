package objects

import types.*
import fos.Instruction

class Struct(context: Context, name: String, _modifier: Modifier, val block: Instruction) extends CObject(context, name, _modifier) with Typed(IdentifierType(context.getPath()+"."+name)){
  
}