package objects

import types.*
import objects.ConcreteFunction
import sl.*
import sl.Compilation.Selector.Selector
import scala.collection.mutable

class Class(context: Context, name: String, val generics: List[String], _modifier: Modifier, val block: Instruction, val parent: Class, val entities: Map[String, Expression]) extends CObject(context, name, _modifier){
    var implemented = mutable.Map[List[Type], Class]()
    var virutalFunction: List[(String, Function)] = List()
    var virutalFunctionVariable: List[Variable] = List()
    var virutalFunctionBase: List[Function] = List()

    def get(typevars: List[Type]) ={
        if (generics.length == 0){
            this
        }else{
            generateForTypes(typevars)
            implemented(typevars)
        }
    }

    def generateForTypes(typevars: List[Type])={
        if (!implemented.contains(typevars)){
            if (typevars.size != generics.size) throw new Exception("Wrong number of type variables")
            val hash = scala.util.hashing.MurmurHash3.stringHash(typevars.mkString(","))
            val ctx = context.push(name+"--"+hash)
            generics.zip(typevars).foreach(pair => ctx.addTypeDef(pair._1, pair._2))
            val cls = ctx.addClass(new Class(ctx, "impl", List(), modifiers, block, parent, entities))
            cls.generate()
            implemented(typevars) = cls
        }
    }

    def generate()={
        if (generics.length == 0){
            val ctx = context.push(name, this)
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

            getAllFunctions()
            .filter(!_.modifiers.isStatic)
            .filter(_.modifiers.isVirtual)
            .filter(f => !f.modifiers.isOverride)
            .filter(f => f.context == ctx)
            .map(fct => {
                val typ = fct.getFunctionType()
                val vari = Variable(ctx, f"---${fct.name}", typ, Modifier.newPrivate())
                ctx.addVariable(vari)
                vari.generate()(ctx)

                virutalFunctionVariable = vari :: virutalFunctionVariable
                virutalFunctionBase = fct :: virutalFunctionBase
                
                val modifier = fct.modifiers.copy()
                modifier.isVirtual = false
                modifier.isOverride = false
                
                val fct2 = ConcreteFunction(ctx, f"--${fct.name}", fct.arguments, fct.getType(), modifier,
                    if (fct.getType() == VoidType) FunctionCall(vari.fullName,fct.arguments.map(arg => VariableValue(arg.name)), List())
                    else Return(FunctionCallValue(LinkedVariableValue(vari),fct.arguments.map(arg => VariableValue(arg.name)), List(), Selector.self)), false)
                fct2.generateArgument()(ctx)
                virutalFunction = (fct.name, fct2) :: virutalFunction
                ctx.addFunction(f"--${fct.name}", fct2)
            })
        }
    }
    def generateForVariable(vari: Variable, typevar: List[Type])(implicit ctx2: Context):Unit={
        if (generics.length == 0){
            val ctx = ctx2.push(vari.name, vari)
            getAllFunctions()
                .filter(!_.modifiers.isStatic)
                .filter(!_.modifiers.isVirtual)
                .map(fct => {
                    val deco = ClassFunction(vari, fct)
                    ctx.addFunction(fct.name, deco)
                })
            virutalFunction.map((name, fct) => {
                val deco = ClassFunction(vari, fct)
                ctx.addFunction(name, deco)
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
        else{
            generateForTypes(typevar)
            implemented(typevar).generateForVariable(vari, List())
        }
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
        virutalFunctionVariable.zip(virutalFunctionBase).flatMap((vari, fct) => vari.assign("=", LinkedFunctionValue(fct))(context.push(name))) :::
        List(f"tag @s add ${getTag()}") ::: (if (parent != null){
            parent.addClassTags()
        }
        else{
            Nil
        })
    }
    def getTag()=f"#class.$fullName"

    def getEntity(): Expression = {
        if (entities != null){
            if (Settings.target == MCJava && entities.contains("mcjava")){
                Utils.simplify(entities("mcjava"))(context)
            }
            else if (Settings.target == MCBedrock && entities.contains("mcbedrock")){
                Utils.simplify(entities("mcbedrock"))(context)
            }
            else if (parent != null){
                parent.getEntity()
            }
            else{
                null
            }
        }
        else if (parent != null){
            parent.getEntity()
        }
        else{
            null
        }
    }
}