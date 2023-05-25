package objects

import types.*
import sl.{Instruction, InstructionList}

class Struct(context: Context, name: String, val generics: List[String], _modifier: Modifier, val block: Instruction, val parent: Struct) extends CObject(context, name, _modifier){
    def getBlock(): Instruction = {
        if (parent != null){
            InstructionList(List(parent.getBlock(), block.unBlockify()))
        }
        else{
            block.unBlockify()
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