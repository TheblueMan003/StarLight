package objects

import types.*
import objects.ConcreteFunction
import sl.*
import sl.Compilation.Selector.Selector

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
            vari.getter.generateArgument()(ctx)
            ctx.addFunction(f"__get_${vari.name}", vari.getter)

            vari.setter = ConcreteFunction(ctx, f"__set_${vari.name}", List(new Argument("value", vari.getType(), None)), VoidType, vari.modifiers, VariableAssigment(List((Right(vari), Selector.self)), "=", VariableValue("value")), false)
            vari.setter.generateArgument()(ctx)
            ctx.addFunction(f"__set_${vari.name}", vari.setter)
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
                val getter = ClassFunction(vari, vari.getter)
                ctx.addFunction(vari.getter.name, getter)
                val setter = ClassFunction(vari, vari.setter)
                ctx.addFunction(vari.setter.name, setter)
                ctx.addProperty(Property(vari.name, getter, setter, vari))
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
    def addClassTags():List[String] = {
        List(f"tag @s add #class.$fullName") ::: (if (parent != null){
            parent.addClassTags()
        }
        else{
            Nil
        })
    }
}