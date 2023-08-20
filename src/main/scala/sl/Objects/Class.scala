package objects

import types.*
import objects.ConcreteFunction
import sl.*
import sl.Compilation.Selector.Selector
import scala.collection.mutable
import sl.IR.*

class Class(context: Context, name: String, val generics: List[String], _modifier: Modifier, val block: Instruction, val parentName: Identifier, val entities: Map[String, Expression]) extends CObject(context, name, _modifier){
    lazy val parent = if (parentName == null) {if (name != "object") {context.getClass("object")} else null} else context.getClass(parentName)
    var implemented = mutable.Map[List[Type], Class]()
    var virutalFunction: List[(String, Function)] = List()
    var virutalFunctionVariable: List[Variable] = List()
    var virutalFunctionBase: List[Function] = List()
    var definingType = ClassType(this, List())
    private var wasGenerated = false

    lazy val cacheGVFunctions = getAllFunctions()
                .filter(!_._2.modifiers.isStatic)
                .filter(!_._2.modifiers.isVirtual)
                .filter(!_._2.isVirtualOverride)
                .filter(x => isNotClassFunction(x._2))
                .filter(f => f._2.context == context.push(name) || context.push(name).isInheriting(f._2.context))
                .toList

    lazy val cacheGVVariables = getAllVariables()
                .filter(!_.modifiers.isStatic)
                .filter(!_.isFunctionArgument)
                .filter(_.modifiers.protection == Protection.Public)
                .filter(_.getter != null)
                .filter(_.setter != null)
                .toList

    def get(typevars2: List[Type]) ={
        val typevars = typevars2.map(context.push(name).getType(_))
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
            generics.zip(typevars).foreach(pair => {
                ctx.addTypeDef(pair._1, pair._2)
            })
            val cls = ctx.addClass(new Class(ctx, "impl", List(), modifiers, block, parentName, entities))
            cls.definingType = ClassType(this, typevars)
            implemented(typevars) = cls
            cls.generate()
        }
    }

    def generate():Unit={
        if (!wasGenerated){
            wasGenerated = true
            if (parent != null){
                parent.generate()
            }
            if (generics.length == 0){
                val ctx = context.push(name, this)
                ctx.push("this", ctx)
                if (parent != null){
                    ctx.push("super", parent.context.push(parent.name))
                    ctx.inherit(parent.context.push(parent.name))
                }

                {
                    val mod = Modifier.newProtected()
                    mod.isLazy = true
                    val vari = new Variable(ctx, "__MCEntity", MCObjectType, mod)
                    vari.assign("=", getEntity())(ctx)
                    ctx.addVariable(vari)
                }
                Compiler.compile(block.unBlockify())(ctx)
                
                val mod = new Modifier()
                mod.isEntity = true
                getAllVariables().filter(!_.isFunctionArgument).foreach(vari => vari.modifiers = vari.modifiers.combine(mod))

                getAllVariables()
                .filter(!_.isFunctionArgument)
                .filter(!_.modifiers.isStatic)
                .filter(_.getter == null)
                .filter(_.modifiers.protection == Protection.Public)
                .foreach(vari => 
                    vari.getter = ConcreteFunction(ctx, ctx.getPath()+f".__get_${vari.name}", f"__get_${vari.name}", List(), vari.getType(), vari.modifiers, Return(LinkedVariableValue(vari)), false)
                    vari.getter.generateArgument()(ctx)
                    ctx.addFunction(f"__get_${vari.name}", vari.getter)

                    vari.setter = ConcreteFunction(ctx, ctx.getPath()+f".__set_${vari.name}", f"__set_${vari.name}", List(new Argument("value", vari.getType(), None)), VoidType, vari.modifiers, VariableAssigment(List((Right(vari), Selector.self)), "=", VariableValue("value")), false)
                    vari.setter.generateArgument()(ctx)
                    ctx.addFunction(f"__set_${vari.name}", vari.setter)
                )

                getAllFunctions()
                .filter(!_._2.modifiers.isStatic)
                .filter(_._2.modifiers.isVirtual)
                .filter(f => !f._2.modifiers.isOverride)
                .filter(f => f._2.context == ctx)
                .map((name,fct) => {
                    val typ = fct.getFunctionType()
                    val vmod = Modifier.newPrivate()
                    vmod.isEntity = true
                    val vari = Variable(ctx, f"---${fct.name}", typ, vmod)
                    ctx.addVariable(vari)
                    vari.generate()(ctx)

                    virutalFunctionVariable = vari :: virutalFunctionVariable
                    virutalFunctionBase = fct :: virutalFunctionBase
                    
                    val modifier = fct.modifiers.copy()
                    modifier.isVirtual = false
                    modifier.isOverride = false
                    
                    val fct2 = ConcreteFunction(ctx, ctx.getPath()+f".--${fct.name}", f"--${fct.name}", fct.arguments, fct.getType(), modifier,
                        if (fct.getType() == VoidType) FunctionCall(vari.fullName,fct.arguments.map(arg => VariableValue(arg.name)), List())
                        else Return(FunctionCallValue(LinkedVariableValue(vari),fct.arguments.map(arg => VariableValue(arg.name)), List(), Selector.self)), false)
                    fct2.generateArgument()(ctx)
                    fct2.overridedFunction = fct
                    fct2.isVirtualDispatch = true
                    virutalFunction = (name, fct2) :: virutalFunction
                    ctx.addFunction(name, fct2)
                })

                getAllVariables().filter(!_.wasGenerated).map(_.generate(false, true)(ctx))
            }
        }
    }
    def getVirtualFunction():List[(String, Function)] = {
        if (parent != null){
            parent.getVirtualFunction() ++ virutalFunction
        }
        else{
            virutalFunction
        }
    }
    def generateForVariable(vari: Variable, typevar: List[Type])(implicit ctx2: Context):Unit={
        if (generics.length == 0){
            generate()
            val ctx = ctx2.push(vari.name, vari)
            cacheGVFunctions.map((name, fct) => {
                    val deco = ClassFunction(ctx.getPath()+"."+name, vari, fct)
                    ctx.addFunction(name, deco)
                })
            cacheGVVariables.map(vari => {
                    val getter = ClassFunction(ctx.getPath()+"."+vari.getter.name, vari, vari.getter)
                    ctx.addFunction(vari.getter.name, getter)
                    val setter = ClassFunction(ctx.getPath()+"."+vari.setter.name, vari, vari.setter)
                    ctx.addFunction(vari.setter.name, setter)
                    ctx.addProperty(Property(vari.name, getter, setter, vari))
                })
        }
        else{
            generateForTypes(typevar)
            implemented(typevar).generateForVariable(vari, List())
        }
    }
    def getAllFunctions():List[(String,Function)] = {
        context.push(name).getAllFunction()
    }
    def isVariableOwned(vari: Variable):Boolean = {
        vari.context == context.push(name) || parent != null && parent.isVariableOwned(vari)
    }
    def getAllVariables():List[Variable] = {
        context.push(name).getAllVariable().filter(isVariableOwned(_)).flatMap(getAllVariablesFrom)
    }
    def getAllVariablesFrom(vari: Variable):List[Variable] = {
        vari :: vari.tupleVari.flatMap(getAllVariablesFrom)
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
    def getMostRecentFunction(function: Function)(implicit ctx: Context): Function = {
        val found = ctx.getAllFunction().filter(_._2.overridedFunction == function)
        if (found.length == 0){
            if (virutalFunctionBase.contains(function.overridedFunction)){
                function.overridedFunction
            }
            else{
                function
            }
        }
        else{
            getMostRecentFunction(found.head._2)
        }
    }
    def addVariableDefaultAssign():List[IRTree] = {
        getAllVariables()
            .filter(!_.modifiers.isStatic)
            .filter(!_.isFunctionArgument)
            .filter(!_.modifiers.isLazy)
            .filter(f => f.modifiers.protection == Protection.Public || f.modifiers.protection == Protection.Protected)
            .filter(_.modifiers.isEntity)
            .flatMap(_.assign("=", DefaultValue)(context))
    }
    def addClassTags(context: Option[Context] = None):List[IRTree] = {
        val ctx = context.getOrElse(this.context.push(name))
        virutalFunctionVariable.zip(virutalFunctionBase).flatMap((vari, fct) => vari.assign("=", LinkedFunctionValue(getMostRecentFunction(fct)(ctx)))(ctx)) :::
        List(CommandIR(f"tag @s add ${getTag()}")) ::: (if (parent != null){
            parent.addClassTags(Some(ctx))
        }
        else{
            Nil
        })
    }
    def getTag()=f"--class.$fullName"

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
    def isNotClassFunction(f: Function) = {
        f match
            case a: ClassFunction => false
            case _ => true
    }
}