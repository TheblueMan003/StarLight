package objects

import types.*
import sl.{Instruction, InstructionList, Utils}

class Template(context: Context, name: String, _modifier: Modifier, val block: Instruction, val parent: Template) extends CObject(context, name, _modifier){
    def getBlock(): Instruction = {
        if (parent != null){
            InstructionList(List(parent.getBlock(), Utils.fix(block)(context, Set())))
        }
        else{
            Utils.fix(block)(context, Set())
        }
    }
    def getContext():Context={
        val sub = context.push(name)
        if (parent != null){
            sub.inherit(parent.getContext())
        }
        sub
    }
    
}