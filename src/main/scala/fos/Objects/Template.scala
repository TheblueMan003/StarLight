package objects

import types.*
import fos.{Instruction, InstructionList}

class Template(context: Context, name: String, _modifier: Modifier, val block: Instruction, val parent: Template) extends CObject(context, name, _modifier) with Typed(IdentifierType(context.getPath()+"."+name)){
    def getBlock(): Instruction = {
        if (parent != null){
            InstructionList(List(parent.getBlock(), block))
        }
        else{
            block
        }
    }
}