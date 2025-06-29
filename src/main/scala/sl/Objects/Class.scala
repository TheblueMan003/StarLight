package objects

import types.*
import objects.ConcreteFunction
import sl.*
import sl.Compilation.Selector.Selector
import scala.collection.mutable
import sl.IR.*

class Class(context: Context, name: String, val generics: List[String], _modifier: Modifier, val block: Instruction, val parentName: Identifier, val parentGenerics: List[Type], val interfaceNames: List[(Identifier, List[Type])], val entities: Map[String, Expression]) extends CObject(context, name, _modifier){
    lazy val parent = if (parentName == null) {if (name != "object") {context.getClass("object")} else null} else context.getClass(parentName).get(parentGenerics)
    lazy val interfaces = interfaceNames.map(x => context.getClass(x._1).get(x._2))
    var implemented = mutable.Map[List[Type], Class]()
    var virutalFunction: List[(String, Function)] = List()
    var virutalFunctionVariable: List[Variable] = List()
    var virutalFunctionBase: List[Function] = List()
    var definingType = ClassType(this, List())
    private var wasGenerated = false

    def checkIfShouldGenerate()={
        if (Utils.contains(block, x => x match {case FunctionDecl(name, block, typ, args, typeArgs, modifier) => modifier.isStatic; case _ => false})){
            generate()
        }
    }

    lazy val hasOwnInnit = getAllFunctions().exists(f => f._1 == "__init__" && f._2.clazz == this)

    lazy val cacheGVFunctions = getAllFunctions()
                .filter(!_._2.modifiers.isStatic)
                .filter(!_._2.modifiers.isVirtual)
                .filter(!_._2.isVirtualOverride)
                .filter(x => isNotClassFunction(x._2))
                .filter(f => f._2.context == context.push(name) || context.push(name).isInheriting(f._2.context))
                .filter(f => f._2.parentVariable == null)
                .filterNot(f => f._1 == "__init__" && f._2.clazz != this && hasOwnInnit)
                .filterNot(f => f._1 == "__init__" && interfaces.contains(f._2.clazz))
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
            val cls = ctx.addClass(new Class(ctx, "impl", List(), modifiers, block, parentName, parentGenerics, interfaceNames, entities))
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
            interfaces.foreach(_.generate())
            if (generics.length == 0){
                val ctx = context.push(name, this)
                ctx.push("this", ctx)
                if (parent != null){
                    ctx.push("super", parent.context.push(parent.name))
                    ctx.inherit(parent.context.push(parent.name))
                }

                interfaces.foreach(intf => {
                    ctx.inherit(intf.context.push(intf.name))
                })

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
                    var mod = Modifier.newPrivate()
                    mod = mod.combine(vari.modifiers) 
                    mod.protection = Protection.Private
                    vari.getter = ConcreteFunction(ctx, ctx.getPath()+f".__get_${vari.name}", f"__get_${vari.name}", List(), vari.getType(), mod, Return(LinkedVariableValue(vari)), false)
                    vari.getter.generateArgument()(ctx)
                    ctx.addFunction(f"__get_${vari.name}", vari.getter)

                    vari.setter = ConcreteFunction(ctx, ctx.getPath()+f".__set_${vari.name}", f"__set_${vari.name}", List(new Argument("value", vari.getType(), None)), VoidType, mod, VariableAssigment(List((Right(vari), Selector.self)), "=", VariableValue("value")), false)
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
                    modifier.isAbstract = false
                    
                    val fct2 = ConcreteFunction(ctx, ctx.getPath()+f".--${fct.name}", f"--${fct.name}", fct.arguments, fct.getType(), modifier,
                        if (fct.getType() == VoidType) FunctionCall(vari.fullName,fct.arguments.map(arg => VariableValue(arg.name)), List())
                        else Return(FunctionCallValue(LinkedVariableValue(vari),fct.arguments.map(arg => VariableValue(arg.name)), List(), Selector.self)), false)
                    fct2.generateArgument()(ctx)
                    fct2.overridedFunction = fct
                    fct2.isVirtualDispatch = true
                    virutalFunction = (name, fct2) :: virutalFunction
                    
                    ctx.addFunction(name, fct2)
                })

                val a = cacheGVFunctions
                val b = cacheGVVariables
                
                context.push(name).getAllVariable().filter(!_.wasGenerated).map(vari => vari.generate(false, true)(vari.context))
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
                    val deco = ClassFunction(ctx.getPath()+"."+name, vari, fct, this)
                    ctx.addFunction(name, deco)
                })
            cacheGVVariables.map(vari2 => {
                    val getter = ClassFunction(ctx.getPath()+"."+vari2.getter.name, vari, vari2.getter, this)
                    ctx.addFunction(vari2.getter.name, getter)
                    val setter = ClassFunction(ctx.getPath()+"."+vari2.setter.name, vari, vari2.setter, this)
                    ctx.addFunction(vari2.setter.name, setter)
                    ctx.addProperty(Property(vari2.name, getter, setter, vari2))
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
            .flatMap(_.assign("=", DefaultValue)(context)) ::: interfaces.flatMap(intf => intf.addVariableDefaultAssign())
    }
    def addClassTags(context: Option[Context] = None):List[IRTree] = {
        val ctx = context.getOrElse(this.context.push(name))

        val recent = virutalFunctionBase.map(fct => getMostRecentFunction(fct)(ctx))
        if (recent.exists(x => x.modifiers.isAbstract)){
            throw new Exception(f"Cannot instantiate class ${fullName} with abstract functions: \n"+recent.filter(x => x.modifiers.isAbstract).map(x => x.name).mkString("\n")+"\n")
        }

        (virutalFunctionVariable.zip(recent).flatMap((vari, fct) => vari.assign("=", LinkedFunctionValue(fct))(ctx)) :::
        List(CommandIR(f"tag @s add ${getTag()}")) ::: (if (parent != null){
            parent.addClassTags(Some(ctx))
        }
        else{
            Nil
        })
        ::: interfaces.flatMap(intf => intf.addClassTags(Some(ctx)))).distinct
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
        f match{
            case a: ClassFunction => false
            case _ => true
        }
    }
}