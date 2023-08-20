package objects

import types.*
import sl.{Instruction, InstructionList}

class Struct(context: Context, name: String, val generics: List[String], _modifier: Modifier, val block: Instruction, val parentName: Identifier) extends CObject(context, name, _modifier){
    lazy val parent = if (parentName == null) null else context.getStruct(parentName)
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