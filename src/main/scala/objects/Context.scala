package objects

import scala.collection.mutable

object Context{
    def getNew(name: String):Context = {
        val root = Context("")
        Context(name, root, root)
    }
}
class Context(name: String, parent: Context = null, _root: Context = null) {
    private lazy val path: String = if (parent == null){name}else{parent.path+"."+name}
    
    private val variables = mutable.Map[String, Variable]()
    private val functions = mutable.Map[String, Function]()
    
    
    private val child = mutable.Map[String, Context]()

    def getPath(): String ={
        return path
    }
    def root: Context = {
        _root
    }

    /**
     * Return a new context for a sub block
     */
    def push(name: String): Context = {
        val n = Context(name, this, _root)
        child.addOne(name, n)
        n
    }


    def getVariable(identifier: Identifier): Variable = {
        getElement(_.variables)(identifier)
    }
    def addVariable(variable: Variable) = {
        variables.addOne(variable.name, variable)
    }

    def getFunction(identifier: Identifier): Function = {
        getElement(_.functions)(identifier)
    }
    def addFunction(function: Function) = {
        functions.addOne(function.name, function)
    }


    private def getElement[T](mapGetter: (Context)=>mutable.Map[String, T])(identifier: Identifier): T = {
        val map = mapGetter(this)
        // Check if single word
        if (identifier.isSingleton()){
            // Check if in context
            if (map.contains(identifier.head())){
                map(identifier.head())
            }
            // Check parent
            else if (parent != null){
                parent.getElement(mapGetter)(identifier)
            }
            else{
                throw new Exception(f"Unknown Variable: $identifier in context: $path")
            }
        }
        else{
            // Check if child has begin
            if (child.contains(identifier.head())){
                child(identifier.head()).getElement(mapGetter)(identifier.drop())
            }
            // Check parent
            else if (parent != null){
                parent.getElement(mapGetter)(identifier)
            }
            else{
                throw new Exception(f"Unknown Variable: $identifier in context: $path")
            }
        }
    }
}
