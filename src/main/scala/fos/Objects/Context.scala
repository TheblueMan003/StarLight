package objects

import scala.collection.mutable
import objects.types.*
import fos.{Settings, Utils, LinkedVariableValue}
import fos.Expression

object Context{
    def getNew(name: String):Context = {
        new Context(name)
    }
}
class Context(name: String, parent: Context = null, _root: Context = null) {
    private lazy val path: String = if (parent == null){name}else{parent.path+"."+name}
    
    private val variables = mutable.Map[String, Variable]()
    private val functions = mutable.Map[String, List[Function]]()
    private val structs = mutable.Map[String, Struct]()
    private val enums = mutable.Map[String, Enum]()
    private val jsonfiles = mutable.Map[String, JSONFile]()
    
    
    private val child = mutable.Map[String, Context]()

    private lazy val fctCtx = root.push(Settings.functionFolder)
    private lazy val muxCtx = root.push(Settings.multiplexFolder)

    private var funcToCompile = List[ConcreteFunction]()
    private var constants = mutable.Set[Int]()
    private var muxIDs = mutable.Set[Int]()
    private var scoreboardIDs = mutable.Set[Int]()
    private val mux = mutable.Map[(List[Type], Type), MultiplexFunction]()

    private var varId = -1

    private var function: Function = null

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

    def addFunctionToCompile(fct: ConcreteFunction) = {
        val r = root
        r.synchronized{
            if (!r.funcToCompile.contains(fct)){
                r.funcToCompile = fct :: r.funcToCompile
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
    def getFreshBlock(content: List[String]): BlockFunction = {
        val r = fctCtx
        r.synchronized{
            r.varId += 1
            val fct = BlockFunction(r, "_"+r.varId.toString(), content)
            r.addFunction("_"+r.varId.toString(), fct)
            fct
        }
    }
    def getNamedBlock(name: String, content: List[String]): Function = {
        val name2 = getFunctionWorkingName(name)
        synchronized{
            addFunction(name, BlockFunction(this, name2, content))
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
            constants.add(value)
        }
    }
    def getAllConstant(): List[Int] = {
        val r = root
        r.synchronized{
            constants.toList
        }
    }

    /**
     * Return a new context for a sub block
     */
    def push(name: String, fct: Function = null): Context = {
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




    def getCurrentFunction(): Function = {
        if function == null then parent.getCurrentFunction() else function
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
        variables.addOne(variable.name, variable)
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
    def getAllVariable():List[Variable] = {
        variables.values.toList ::: child.map(_._2.getAllVariable()).foldLeft(List[Variable]())(_.toList ::: _.toList)
    }




    def getFunction(identifier: Identifier, args: List[Expression], concrete: Boolean = false): (Function, List[Expression]) = {
        val vari = tryGetVariable(identifier)
        vari match
            case Some(vari) if vari.getType().isInstanceOf[FuncType] => {
                val typ = vari.getType().asInstanceOf[FuncType]
                (getFunctionMux(typ.sources, typ.output)(this), LinkedVariableValue(vari)::args)
            }
            case _ => (getFunction(identifier, args.map(Utils.typeof(_)(this)), concrete), args)
    }
    def getFunction(identifier: Identifier, args: List[Type], concrete: Boolean): Function = {
        val fcts = getElementList(_.functions)(identifier)
        if (fcts.length == 0) throw new Exception(f"Unknown function: $identifier in context: $path")
        if (fcts.length == 1) return fcts.head
        val filtered = fcts.filter(fct => args.length >= fct.minArgCount && args.length <= fct.arguments.length && (fct.isInstanceOf[ConcreteFunction] || !concrete))
        if (filtered.length == 1) return filtered.head
        if (fcts.length == 0) throw new Exception(f"Unknown function: $identifier for args: $args in context: $path")
        val ret = filtered.sortBy(_.arguments.zip(args).map((a, v)=> v.getDistance(a.typ)(this)).sum).head
        ret
    }
    
    def addFunction(name: String, function: Function): Function = synchronized{
        if (!functions.contains(name)){
            functions.addOne(name, List())
        }
        functions(name) = function :: functions(name)
        function
    }
    private def createFunctionMux(source: List[Type], output: Type, key: (List[Type], Type))(implicit context: Context) = {
        if (!muxCtx.mux.contains(key)){
            val name = source.map(_.getName()).reduceOption(_ + "___"+ _).getOrElse("void") + "___to___" + output.getName()
            var args = fos.Argument(f"fct", IntType, None) :: source.zipWithIndex.map((t, i) => fos.Argument(f"a_$i", t, None))
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
        if (!functions.contains(name)){
            name
        }
        else{
            val fct = functions.get(name).get
            var c = 0
            while(fct.exists(_.name == name+f"__overloading__$c")){
                c+=1
            }
            return name+f"__overloading__$c"
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
    def getAllFunction():List[Function] = {
        functions.values.flatten.toList ::: child.map(_._2.getAllFunction()).foldLeft(List[Function]())(_.toList ::: _.toList)
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
        structs.addOne(struct.name, struct)
        struct
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
            case IdentifierType(identifier) => {
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
        jsonfiles.values.toList ::: child.map(_._2.getAllJsonFiles()).foldLeft(List[JSONFile]())(_.toList ::: _.toList)
    }




    private def tryGetElement[T](mapGetter: (Context)=>mutable.Map[String, T])(identifier: Identifier): Option[T] = {
        val map = mapGetter(this)
        // Check if single word
        if (identifier.isSingleton()){
            // Check if in context
            if (map.contains(identifier.head())){
                Some(map(identifier.head()))
            }
            // Check parent
            else if (parent != null){
                parent.tryGetElement(mapGetter)(identifier)
            }
            else{
                None
            }
        }
        else{
            // Check if child has begin
            if (child.contains(identifier.head())){
                child(identifier.head()).tryGetElement(mapGetter)(identifier.drop())
            }
            // Check parent
            else if (parent != null){
                parent.tryGetElement(mapGetter)(identifier)
            }
            else if (name == identifier.head()){
                child(identifier.drop().head()).tryGetElement(mapGetter)(identifier.drop())
            }
            else{
                None
            }
        }
    }

    private def getElementList[T](mapGetter: (Context)=>mutable.Map[String, List[T]])(identifier: Identifier): List[T] = {
        val map = mapGetter(this)
        // Check if single word
        if (identifier.isSingleton()){
            // Check if in context
            if (map.contains(identifier.head())){
                val self = map(identifier.head())
                if (parent != null){
                    self ::: parent.getElementList(mapGetter)(identifier)
                }
                else{
                    self
                }
            }
            // Check parent
            else if (parent != null){
                parent.getElementList(mapGetter)(identifier)
            }
            else{
                List()
            }
        }
        else{
            // Check if child has begin
            if (child.contains(identifier.head())){
                child(identifier.head()).getElementList(mapGetter)(identifier.drop())
            }
            // Check parent
            else if (parent != null){
                parent.getElementList(mapGetter)(identifier)
            }
            else if (name == identifier.head()){
                child(identifier.drop().head()).getElementList(mapGetter)(identifier.drop())
            }
            else{
                List()
            }
        }
    }
}
