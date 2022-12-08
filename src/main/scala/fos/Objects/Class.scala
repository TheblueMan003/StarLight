package objects

import types.*
import fos.{Instruction, InstructionList, Compiler}

class Class(context: Context, name: String, _modifier: Modifier, val block: Instruction, val parent: Class) extends CObject(context, name, _modifier) with Typed(IdentifierType(context.getPath()+"."+name)){
    def generate()={
        val ctx = context.push(name)
        if (parent != null){
            ctx.inherit(parent.context.push(parent.name))
        }
        Compiler.compile(block)(ctx)
        
        val mod = new Modifier()
        mod.isEntity = true
        ctx.getAllVariable().filter(!_.isFunctionArgument).foreach(vari => vari.modifiers = vari.modifiers.combine(mod))
    }
    def getAllFunctions():List[Function] = {
        context.push(name).getAllFunction()
    }
    def getBlock(): Instruction = {
        if (parent != null){
            InstructionList(List(parent.getBlock(), block))
        }
        else{
            block
        }
    }
}