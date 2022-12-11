package objects

import types.*
import sl.{Instruction, InstructionList}

class Struct(context: Context, name: String, _modifier: Modifier, val block: Instruction, val parent: Struct) extends CObject(context, name, _modifier) with Typed(IdentifierType(context.getPath()+"."+name)){
    def getBlock(): Instruction = {
        if (parent != null){
            InstructionList(List(parent.getBlock(), block))
        }
        else{
            block
        }
    }
    def hasParent(struct: Struct):Boolean = {
        if (parent != null){
            if (parent == struct){
                true
            }
            else{
                parent.hasParent(struct)
            }
        }
        else{
            false
        }
    }
}