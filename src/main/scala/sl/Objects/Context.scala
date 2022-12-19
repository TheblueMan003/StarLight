package objects

import scala.collection.mutable
import objects.types.*
import sl.{Settings, Utils, LinkedVariableValue, FunctionCallValue, LinkedFunctionValue, Argument, FloatValue, IntValue}
import sl.Expression
import sl.Instruction
import sl.LambdaValue
import sl.VariableValue
import sl.Compilation.Selector.Selector

object Context{
    def getNew(name: String):Context = {
        new Context(name)
    }
}
class Context(name: String, val parent: Context = null, _root: Context = null) {
    private lazy val path: String = if (parent == null){name}else{parent.path+"."+name}
    
    private val variables = mutable.Map[String, Variable]()
    private val properties = mutable.Map[String, Property]()
    private val functions = mutable.Map[String, List[Function]]()
    private val structs = mutable.Map[String, Struct]()
    private val classes = mutable.Map[String, Class]()
    private val templates = mutable.Map[String, Template]()
    private val enums = mutable.Map[String, Enum]()
    private val typedefs = mutable.Map[String, Type]()
    private val jsonfiles = mutable.Map[String, JSONFile]()
    private val predicates = mutable.Map[String, List[Predicate]]()
    private val names = mutable.Set[String]()

    private val functionTags = mutable.Map[Identifier, TagFunction]()
    
    
    private val child = mutable.Map[String, Context]()
    private var inheritted: Context = null

    lazy val fctCtx = root.push(Settings.functionFolder)
    lazy val muxCtx = root.push(Settings.multiplexFolder)
    lazy val tagCtx = root.push(Settings.tagsFolder)


    private var funcToCompile = List[ConcreteFunction]()
    private var constants = mutable.Set[Int]()
    private var muxIDs = mutable.Set[Int]()
    private var scoreboardIDs = mutable.Set[Int]()
    private val mux = mutable.Map[(List[Type], Type), MultiplexFunction]()

    private var imports = List[String]()

    private var varId = -1

    private var function: Function = null
    private var variable: Variable = null
    private var clazz: Class = null
    private var inLazyCall: Boolean = false

    def getPath(): String ={
        return path
    }
    def root: Context = {
        if (_root == null){
            this
        }
        else{
            _root
        }
    }

    def importFile(string: String) = root.synchronized{
        if (!root.imports.contains(string)){
            root.imports = string::root.imports
            true
        }
        else{
            false
        }
    }

    def addFunctionToCompile(fct: ConcreteFunction) = {
        val r = root
        r.synchronized{
            if (!r.funcToCompile.contains(fct)){
                r.funcToCompile = (fct :: r.funcToCompile).sortBy(f => {
                    Utils.simplify(f.modifiers.attributes.getOrElse("compile.order", IntValue(0)))(this) match {
                        case IntValue(n) => n.toDouble
                        case FloatValue(n) => n
                        case _ => 0.0
                    }
                })
            }
        }
    }
    def getFunctionToCompile():ConcreteFunction = {
        val r = root
        var ret: ConcreteFunction = null
        r.synchronized{
            r.funcToCompile match{
                case head :: next => {
                    ret = head
                    r.funcToCompile = next
                    ret.markAsCompile()
                }
                case Nil => {
                    ret = null
                }
            }
        }
        ret
    }
    def getMuxes():List[MultiplexFunction] = {
        val r = muxCtx
        r.synchronized{
            r.mux.values.toList
        }
    }
    def getTags():List[TagFunction] = {
        val r = tagCtx
        r.synchronized{
            r.functionTags.values.toList
        }
    }


    def getFreshId(): String = {
        synchronized{
            varId += 1
            getPath()+"."+ varId
        }
    }
    def getLazyCallId(): String = {
        synchronized{
            varId += 1
            varId.toString()
        }
    }
    def getFreshVariable(typ: Type): Variable = {
        synchronized{
            varId += 1
            val vari = Variable(this, "_"+varId.toString(), typ, Modifier.newPrivate())
            addVariable(vari)
            vari.generate()(this)
            vari
        }
    }
    def getFreshLambda(argument: List[String], types: List[Type], output: Type, instr: Instruction, isLazy: Boolean = false): Function = synchronized{
        val args = argument.zipAll(types, "_", VoidType).map((v, t) => Argument(v, t, None))
        val name = "lambda_"+getLazyCallId()
        val ctx = push(name)
        val mod = Modifier.newPrivate()
        mod.isLazy = isLazy
        val fct = if isLazy then LazyFunction(this, name, args, output, mod, instr) else ConcreteFunction(this, name, args, output, mod, instr, true)
        fct.generateArgument()(this)
        addFunction(name, fct)
        fct
    }
    def getFreshBlock(content: List[String]): BlockFunction = {
        val r = fctCtx
        r.synchronized{
            r.varId += 1
            val fct = BlockFunction(r, r.varId.toString(), List(), content)
            r.addFunction(r.varId.toString(), fct)
            fct
        }
    }
    def getNamedBlock(name: String, content: List[String]): Function = {
        val name2 = getFunctionWorkingName(name)
        synchronized{
            addFunction(name, BlockFunction(this, name2, List(), content))
        }
    }
    def getFreshFunctionIdentifier(): Identifier = {
        val r = fctCtx
        r.synchronized{
            r.varId += 1
            r.path+ "._"+r.varId.toString()
        }
    }

    def requestConstant(value: Int): Unit = {
        val r = root
        r.synchronized{
            r.constants.add(value)
        }
    }
    def getAllConstant(): List[Int] = {
        val r = root
        r.synchronized{
            r.constants.toList
        }
    }

    /**
     * Return a new context for a sub block
     */
    def push(iden: Identifier, fct: Function = null): Context = {
        if (iden.isSingleton()){
            val name = iden.toString()
            val ret = if (child.contains(name)){
            child(name)
            }
            else{
                val n = new Context(name, this, root)
                child.addOne(name, n)
                n
            }
            ret.function = fct
            ret
        }
        else{
            push(iden.head()).push(iden.drop(), fct)
        }
    }
    /**
     * Return a new context for a sub block
     */
    def push(iden: Identifier, fct: Variable): Context = {
        if (iden.isSingleton()){
            val name = iden.toString()
            val ret = if (child.contains(name)){
            child(name)
            }
            else{
                val n = new Context(name, this, root)
                child.addOne(name, n)
                n
            }
            ret.variable = fct
            ret
        }
        else{
            push(iden.head()).push(iden.drop(), fct)
        }
    }
    /**
     * Return a new context for a sub block
     */
    def push(iden: Identifier, clz: Class): Context = {
        if (iden.isSingleton()){
            val name = iden.toString()
            val ret = if (child.contains(name)){
            child(name)
            }
            else{
                val n = new Context(name, this, root)
                child.addOne(name, n)
                n
            }
            ret.clazz = clz
            ret
        }
        else{
            push(iden.head()).push(iden.drop(), clz)
        }
    }

    /**
     * Add a context as a child
     */
    def push(iden: Identifier, context: Context): Context = {
        if (iden.isSingleton()){
            val name = iden.toString()
            val ret = if (child.contains(name)){
            child(name)
            }
            else{
                child.addOne(name, context)
                context
            }
            ret
        }
        else{
            push(iden.head()).push(iden.drop(), context)
        }
    }

    def inherit(context: Context) = {
        inheritted = context
    }

    def getContext(identifier: Identifier): Context = {
        tryGetElement(_.child)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown package: $identifier in context: $path\n${root.asPrettyString("")}")
        }
    }






    def getCurrentFunction(): Function = {
        if function == null && parent != null then parent.getCurrentFunction() else function
    }
    def getCurrentVariable(): Variable = {
        if variable == null && parent != null then parent.getCurrentVariable() else variable
    }
    def getCurrentClass(): Class = {
        if clazz == null && parent != null then parent.getCurrentClass() else clazz
    }
    def setLazyCall()={
        inLazyCall = true
    }
    def isInLazyCall(): Boolean = {
        if inLazyCall then true else if parent != null then parent.isInLazyCall() else false
    }


    def addName(name: String) = {
        if (names.contains(name)) throw new Exception(f"$name already defined in ${getPath()}")
        names.add(name)
    }




    def getVariable(identifier: Identifier): Variable = {
        tryGetElement(_.variables)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown variable: $identifier in context: $path")
        }
    }
    def tryGetVariable(identifier: Identifier): Option[Variable] = {
        tryGetElement(_.variables)(identifier)
    }
    def addVariable(variable: Variable): Variable = {
        addName(variable.name)
        variables.addOne(variable.name, variable)
        variable
    }
    def addVariable(name: String, variable: Variable): Variable = {
        addName(name)
        variables.addOne(name, variable)
        variable
    }
    def getScoreboardID(variable: Variable): Int = {
        val r = root
        r.synchronized{
            var hash = scala.util.hashing.MurmurHash3.stringHash(variable.fullName)
            while (r.scoreboardIDs.contains(hash)){
                hash += 1
            }
            r.scoreboardIDs.add(hash)
            hash
        }
    }
    def getAllVariable(set: mutable.Set[Context] = mutable.Set()):List[Variable] = {
        if set.contains(this) then return List()
        set.add(this)
        (if inheritted != null && !set.contains(inheritted) then inheritted.getAllVariable(set) else List()) :::
        variables.values.toList ::: child.filter(_._2.parent == this).map(_._2.getAllVariable(set)).foldLeft(List[Variable]())(_.toList ::: _.toList)
    }


    def getProperty(identifier: Identifier): Property = {
        tryGetElement(_.properties)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown property: $identifier in context: $path")
        }
    }
    def tryGetProperty(identifier: Identifier): Option[Property] = {
        tryGetElement(_.properties)(identifier)
    }
    def addProperty(property: Property): Property = {
        addName(property.name)
        properties.addOne(property.name, property)
        property
    }

    def resolveVariable(vari: Expression) = {
		val VariableValue(name, sel) = vari: @unchecked
		tryGetProperty(name) match
			case Some(Property(_, getter, setter, variable)) => FunctionCallValue(LinkedFunctionValue(getter), List(), sel)
			case _ => LinkedVariableValue(getVariable(name), sel)
	}




    def getFunction(identifier: Identifier, args: List[Expression], output: Type, concrete: Boolean = false): (Function, List[Expression]) = {
        if (identifier.toString().startsWith("@")){
            (getFunctionTags(identifier), args)
        }
        else{
            val vari = tryGetVariable(identifier)
            vari match
                case Some(vari) if vari.getType().isInstanceOf[FuncType] => {
                    if (vari.modifiers.isLazy){
                        vari.lazyValue match
                            case LambdaValue(args2, instr) => {
                                (getFreshLambda(args2, args.map(Utils.typeof(_)(this)), output, instr, true), args)
                            }
                            case VariableValue(name, sel) => getFunction(name, args, output, concrete)
                            case other => throw new Exception(f"Illegal call of ${other} with $args")
                    }
                    else{
                        val typ = vari.getType().asInstanceOf[FuncType]
                        (getFunctionMux(typ.sources, typ.output)(this), LinkedVariableValue(vari)::args)
                    }
                }
                case _ => (getFunction(identifier, args.map(Utils.typeof(_)(this)), output, concrete), args)
        }
    }
    def getFunction(identifier: Identifier, args: List[Type], output: Type, concrete: Boolean): Function = {
        if (identifier.toString().startsWith("@")) return getFunctionTags(identifier)
        val fcts = getElementList(_.functions)(identifier)
        if (fcts.size == 0) throw new Exception(f"Unknown function: $identifier in context: $path")
        if (fcts.size == 1) return fcts.head
        val filtered = fcts.filter(fct => args.size >= fct.minArgCount && args.size <= fct.maxArgCount && (fct.isInstanceOf[ConcreteFunction] || !concrete))
        if (filtered.length == 1) return filtered.head
        if (filtered.size == 0) throw new Exception(f"Unknown function: $identifier for args: $args in context: $path")
        val ret = filtered.sortBy(_.arguments.zip(args).map((a, v)=> v.getDistance(a.typ)(this)).reduceOption(_ + _).getOrElse(0)).head
        ret
    }
    def getFunction(identifier: Identifier): Function = {
        if (identifier.toString().startsWith("@")) return getFunctionTags(identifier)
        val fcts = getElementList(_.functions)(identifier)
        if (fcts.size == 0) throw new Exception(f"Unknown function: $identifier in context: $path")
        if (fcts.size == 1) return fcts.head
        throw new Exception(f"Ambiguity for function: $identifier in context: $path ${fcts.map(_.prototype())}")
    }
    def getFunctionTags(tag: Identifier, args: List[Argument] = List()) = {
        tagCtx.synchronized{
            if (!tagCtx.functionTags.contains(tag)){
                val name = tagCtx.getLazyCallId()
                val fct = new TagFunction(tagCtx, name, args)
                tagCtx.functionTags.addOne(tag, fct)
                tagCtx.addFunction(name, fct)
            }
            tagCtx.functionTags(tag)
        }
    }
    def addFunctionToTags(function: Function) = synchronized{
        function match
            case f: ConcreteFunction => f.markAsUsed()
            case _ => {}
        function.modifiers.tags.foreach(tagStr =>
            val tag = Identifier.fromString(tagStr)
            val fct = getFunctionTags(tag, function.arguments)
            fct.synchronized{
                fct.addFunction(function)
            }
        )
    }
    def addFunction(name: String, function: Function): Function = synchronized{
        if (!functions.contains(name)){
            functions.addOne(name, List())
        }
        functions(name) = function :: functions(name)
        if (function.modifiers.tags.length > 0){
            function match
                case cf: ConcreteFunction => tagCtx.addFunctionToTags(cf)
                case cf: ClassFunction => {}
                case other => throw new Exception(f"Function: ${other} cannot be put in a tag")
        }
        function
    }
    private def createFunctionMux(source: List[Type], output: Type, key: (List[Type], Type))(implicit context: Context) = muxCtx.synchronized{
        if (!muxCtx.mux.contains(key)){
            val name = source.map(_.getName()).reduceOption(_ + "___"+ _).getOrElse("void") + "___to___" + output.getName()
            var args = sl.Argument(f"__fct__", IntType, None) :: source.filter(_ != VoidType).zipWithIndex.map((t, i) => sl.Argument(f"a_$i", t, None))
            val muxFct = new MultiplexFunction(muxCtx, name, args, output)
            muxFct.generateArgument()(muxCtx)
            muxCtx.addFunction(name, muxFct)
            muxCtx.mux.addOne(key, muxFct)
        }
    }
    def addFunctionToMux(source: List[Type], output: Type, fct: ConcreteFunction)(implicit context: Context): Unit = {
        val r = muxCtx
        r.synchronized{
            val key = (source, output)
            createFunctionMux(source, output, key)
            val mux = muxCtx.mux(key)
            mux.addFunctionToMux(fct)
        }
    }
    def getFunctionMux(source: List[Type], output: Type)(implicit context: Context):MultiplexFunction = {
        muxCtx.synchronized{
            val key = (source, output)
            createFunctionMux(source, output, key)
            muxCtx.mux(key)
        }
    }
    def getFunctionWorkingName(name: String): String = synchronized{
        if (!functions.contains(name) && !predicates.contains(name)){
            name
        }
        else{
            val fct = functions.get(name).getOrElse(List())
            val pred = predicates.get(name).getOrElse(List())
            var c = 0
            while(fct.exists(_.name == name+f"-$c") || pred.exists(_.name == name+f"-$c")){
                c+=1
            }
            return name+f"-$c"
        }
    }
    def getFunctionMuxID(function: Function): Int = {
        val r = muxCtx
        r.synchronized{
            var hash = scala.util.hashing.MurmurHash3.stringHash(function.fullName)
            while (r.muxIDs.contains(hash)){
                hash += 1
            }
            r.muxIDs.add(hash)
            hash
        }
    }
    def getAllFunction(set: mutable.Set[Context] = mutable.Set()):List[Function] = {
        if set.contains(this) then return List()
        set.add(this)
        (if inheritted != null && !set.contains(inheritted) then inheritted.getAllFunction(set) else List()) ::: functions.values.flatten.toList :::
        child.filter(_._2.parent == this).map(_._2.getAllFunction(set)).foldLeft(List[Function]())(_.toList ::: _.toList)
    }



    def getStruct(identifier: Identifier): Struct = {
        tryGetElement(_.structs)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown struct: $identifier in context: $path")
        }
    }
    def tryGetStruct(identifier: Identifier): Option[Struct] = {
        tryGetElement(_.structs)(identifier)
    }
    def addStruct(struct: Struct): Struct = synchronized{
        addName(struct.name)
        structs.addOne(struct.name, struct)
        struct
    }


    def getClass(identifier: Identifier): Class = {
        tryGetElement(_.classes)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown class: $identifier in context: $path")
        }
    }
    def tryGetClass(identifier: Identifier): Option[Class] = {
        tryGetElement(_.classes)(identifier)
    }
    def addClass(clazz: Class): Class = synchronized{
        addName(clazz.name)
        classes.addOne(clazz.name, clazz)
        clazz
    }



    def tryGetPredicate(identifier: Identifier, args: List[Type]): Option[Predicate] = {
        val fcts = getElementList(_.predicates)(identifier)
        if (fcts.size == 0) return None
        if (fcts.size == 1) return Some(fcts.head)
        val filtered = fcts.filter(fct => args.size >= fct.minArgCount && args.size <= fct.arguments.size)
        if (filtered.length == 1) return Some(filtered.head)
        if (filtered.size == 0) return None
        val ret = filtered.sortBy(_.arguments.zip(args).map((a, v)=> v.getDistance(a.typ)(this)).reduceOption(_ + _).getOrElse(0))
        if (ret.length >= 1) return Some(ret.head)
        return None
    }
    def addPredicate(predicate: Predicate): Predicate = synchronized{
        if (!predicates.contains(name)){
            predicates.addOne(name, List())
        }
        predicates(name) = predicate :: predicates(name)
        predicate
    }
    def getAllPredicates():List[Predicate] = {
        predicates.values.flatten.toList ::: child.filter(_._2 != this).flatMap(_._2.getAllPredicates()).foldLeft(List[Predicate]())((b, p) => p :: b)
    }



    def getTemplate(identifier: Identifier): Template = {
        tryGetElement(_.templates)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown template: $identifier in context: $path")
        }
    }
    def tryGetTemplate(identifier: Identifier): Option[Template] = {
        tryGetElement(_.templates)(identifier)
    }
    def addTemplate(template: Template): Template = synchronized{
        templates.addOne(template.name, template)
        template
    }



    def getTypeDef(identifier: Identifier): Type = {
        tryGetElement(_.typedefs)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown typedef: $identifier in context: $path")
        }
    }
    def tryGetTypeDef(identifier: Identifier): Option[Type] = {
        tryGetElement(_.typedefs)(identifier)
    }
    def addTypeDef(name: String, typedef: Type): Type = synchronized{
        typedefs.addOne(name, typedef)
        typedef
    }



    def getEnum(identifier: Identifier): Enum = {
        tryGetElement(_.enums)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown enum: $identifier in context: $path")
        }
    }
    def tryGetEnum(identifier: Identifier): Option[Enum] = {
        tryGetElement(_.enums)(identifier)
    }
    def addEnum(enm: Enum): Enum = synchronized{
        enums.addOne(enm.name, enm)
        enm
    }

    def getType(typ: Type): Type = {
        typ match
            case ArrayType(sub, nb) => {
                ArrayType(getType(sub), Utils.simplify(nb)(this))
            }
            case FuncType(from, to) => FuncType(from.map(getType(_)), getType(to))
            case TupleType(from) => TupleType(from.map(getType(_)))
            case IdentifierType(identifier) => {
                val typdef = tryGetTypeDef(identifier)
                if (typdef.isDefined){
                    return getType(typdef.get)
                }


                val clazz = tryGetClass(identifier)
                if (clazz.isDefined){
                    return ClassType(clazz.get)
                }


                val struct = tryGetStruct(identifier)
                if (struct.isDefined){
                    return StructType(struct.get)
                }

                val enm = tryGetEnum(identifier)
                if (enm.isDefined){
                    return EnumType(enm.get)
                }

                throw new Exception(f"Unknown type: $identifier in context: $path")
            }
            case _ => typ
    }

    def asPrettyString(shift: String):String = {
        child.filter(_._2.parent == this).map(_._2.asPrettyString(shift + "  ")).foldLeft(shift +"►"+ name)(_ + "\n" + _)
    }


    def getJsonFile(identifier: Identifier): JSONFile = {
        tryGetElement(_.jsonfiles)(identifier) match{
            case Some(value) => value
            case None => throw new Exception(f"Unknown jsonfile: $identifier in context: $path")
        }
    }
    def tryGetJsonFile(identifier: Identifier): Option[JSONFile] = {
        tryGetElement(_.jsonfiles)(identifier)
    }
    def addJsonFile(jsonfile: JSONFile): JSONFile = synchronized{
        jsonfiles.addOne(jsonfile.name, jsonfile)
        jsonfile
    }
    def getAllJsonFiles():List[JSONFile] = {
        jsonfiles.values.toList ::: child.filter(_._2 != this).map(_._2.getAllJsonFiles()).foldLeft(List[JSONFile]())(_.toList ::: _.toList)
    }



    def addObjectFrom(name: String, alias: String, other: Context) = {
        if (name == "_"){
            other.classes.foreach((k, v) =>{
                classes.addOne(k, v)
                child.addOne(k, other.push(k))
            })
            other.templates.foreach((k, v) =>{
                templates.addOne(k, v)
                child.addOne(k, other.push(k))
            })
            other.structs.foreach((k, v) =>{
                structs.addOne(k, v)
                child.addOne(k, other.push(k))
            })
            other.enums.foreach((k, v) =>{
                enums.addOne(k, v)
                child.addOne(k, other.push(k))
            })

            other.predicates.foreach((k, v) =>{
                predicates.addOne(k, v)
            })
            other.functions.foreach((k, v) =>{
                functions.addOne(k, v)
            })
            other.variables.foreach((k, v) =>{
                variables.addOne(k, v)
            })
        }
        else{
            if (other.classes.contains(name)){
                classes.addOne(alias, other.classes(name))
                child.addOne(alias, other.push(name))
            }
            else if (other.templates.contains(name)){
                templates.addOne(alias, other.templates(name))
                child.addOne(alias, other.push(name))
            }
            else if (other.structs.contains(name)){
                structs.addOne(alias, other.structs(name))
                child.addOne(alias, other.push(name))
            }
            else if (other.enums.contains(name)){
                enums.addOne(alias, other.enums(name))
                child.addOne(alias, other.push(name))
            }
            else if (other.predicates.contains(name)){
                predicates.addOne(alias, other.predicates(name))
            }
            else if (other.functions.contains(name)){
                functions.addOne(alias, other.functions(name))
            }
            else if (other.variables.contains(name)){
                variables.addOne(alias, other.variables(name))
            }
            else{
                throw new Exception(f"$name Not Found in ${other.getPath()}")
            }
        }
    }


    def isChildOf(other: Context): Boolean={
        if (other == this || (other.child.contains(name) && other.child(name) == this)) then{ 
            true
        }
        else if (parent != null){
            parent.isChildOf(other)
        }
        else{
            false
        }
    }



    private def tryGetElement[T](mapGetter: (Context)=>mutable.Map[String, T])(identifier: Identifier, down: Boolean = false): Option[T] = {
        val value = tryGetElementNoCheck(mapGetter)(identifier, down)
        value match
            case None => None
            case Some(value) => {
                if (value.isInstanceOf[CObject]) then{
                    val obj = value.asInstanceOf[CObject]
                    if (obj.modifiers.protection == Protection.Private && !isChildOf(value.asInstanceOf[CObject].context)){
                        throw new Exception(f"Cannot access private object: $identifier")
                    }
                    Some(value)
                }
                Some(value)
            }
    }
    private def tryGetElementNoCheck[T](mapGetter: (Context)=>mutable.Map[String, T])(identifier: Identifier, down: Boolean = false): Option[T] = {
        val value = tryGetElementInner(mapGetter)(identifier, down)
        value match
            case None => if inheritted != null then inheritted.tryGetElementNoCheck(mapGetter)(identifier, down) else None
            case Some(_) => value
    }

    private def tryGetElementInner[T](mapGetter: (Context)=>mutable.Map[String, T])(identifier: Identifier, down: Boolean = false): Option[T] = {
        val map = mapGetter(this)
        // Check if single word
        
        if (identifier.isSingleton()){
            // Check if in context
            if (map.contains(identifier.head())){
                Some(map(identifier.head()))
            }
            // Check parent
            else if (parent != null && !down){
                parent.tryGetElementNoCheck(mapGetter)(identifier, down)
            }
            else{
                None
            }
        }
        else{
            // Check if child has begin
            if (child.contains(identifier.head())){
                val ret = child(identifier.head()).tryGetElementNoCheck(mapGetter)(identifier.drop(), true)
                if (ret != None) return ret
            }
            // Check parent
            if (parent != null && !down){
                val ret = parent.tryGetElementNoCheck(mapGetter)(identifier, down)
                if (ret != None) return ret
            }
            if (name == identifier.head() && child.contains(identifier.drop().head())){
                val ret = child(identifier.drop().head()).tryGetElementNoCheck(mapGetter)(identifier.drop().drop(), true)
                if (ret != None) return ret
            }
            if (root == this && child.contains(identifier.head())){
                val ret = child(identifier.head()).tryGetElementNoCheck(mapGetter)(identifier, true)
                if (ret != None) return ret
            }
            None
        }
    }

    private def getElementList[T](mapGetter: (Context)=>mutable.Map[String, List[T]])(identifier: Identifier, down: Boolean = false): List[T] = {
        val value = getElementListInner(mapGetter)(identifier, down)
        if inheritted != null then (inheritted.getElementList(mapGetter)(identifier, down) ::: value).distinct else value.distinct
    }

    private def getElementListInner[T](mapGetter: (Context)=>mutable.Map[String, List[T]])(identifier: Identifier, down: Boolean = false): List[T] = {
        val map = mapGetter(this)
        // Check if single word
        if (identifier.isSingleton()){
            // Check if in context
            if (map.contains(identifier.head())){
                val self = map(identifier.head())
                if (parent != null){
                    self ::: parent.getElementList(mapGetter)(identifier, down)
                }
                else{
                    self
                }
            }
            // Check parent
            else if (parent != null && !down){
                parent.getElementList(mapGetter)(identifier, down)
            }
            else{
                List()
            }
        }
        else{
            var lst = List[T]()
            // Check if child has begin
            if (child.contains(identifier.head())){
                lst = lst ::: child(identifier.head()).getElementList(mapGetter)(identifier.drop(), true)
            }
            // Check parent
            if (parent != null && !down){
                lst = lst ::: parent.getElementList(mapGetter)(identifier, down)
            }
            if (name == identifier.head() && child.contains(identifier.drop().head())){
                lst = lst ::: child(identifier.drop().head()).getElementList(mapGetter)(identifier.drop().drop(), true)
            }
            lst
        }
    }
}
