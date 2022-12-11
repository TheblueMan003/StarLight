package objects

import types.*
import objects.ConcreteFunction
import sl.*

class Class(context: Context, name: String, _modifier: Modifier, val block: Instruction, val parent: Class) extends CObject(context, name, _modifier) with Typed(IdentifierType(context.getPath()+"."+name)){
    def generate()={
        val ctx = context.push(name)
        ctx.push("this", ctx)
        if (parent != null){
            ctx.inherit(parent.context.push(parent.name))
        }
        Compiler.compile(block)(ctx)
        
        val mod = new Modifier()
        mod.isEntity = true
        ctx.getAllVariable().filter(!_.isFunctionArgument).foreach(vari => vari.modifiers = vari.modifiers.combine(mod))

        getAllVariables().filter(!_.isFunctionArgument).filter(!_.modifiers.isStatic).filter(_.getter == null).foreach(vari => 
            vari.getter = ConcreteFunction(ctx, f"__get_${vari.name}", List(), vari.getType(), vari.modifiers, Return(LinkedVariableValue(vari)), false)
            ctx.addFunction(f"__get_${vari.name}", vari.getter)
        )
    }
    def generateForVariable(vari: Variable)={
        val ctx = context.push(vari.name, vari)
        getAllFunctions()
            .filter(!_.modifiers.isStatic)
            .map(fct => {
                val deco = ClassFunction(vari, fct)
                ctx.addFunction(fct.name, deco)
            })
        getAllVariables()
            .filter(!_.modifiers.isStatic)
            .filter(!_.isFunctionArgument)
            .map(vari => {
                val deco = ClassFunction(vari, vari.getter)
                ctx.addFunction(vari.getter.name, deco)
                ctx.addProperty(Property(vari.name, deco, deco))
            })
    }
    def getAllFunctions():List[Function] = {
        context.push(name).getAllFunction()
    }
    def getAllVariables():List[Variable] = {
        context.push(name).getAllVariable()
    }
    def getBlock(): Instruction = {
        if (parent != null){
            InstructionList(List(parent.getBlock(), block))
        }
        else{
            block
        }
    }
    def hasParent(clazz: Class):Boolean = {
        if (parent != null){
            if (parent == clazz){
                true
            }
            else{
                parent.hasParent(clazz)
            }
        }
        else{
            false
        }
    }
}