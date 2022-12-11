package sl

import objects.{Context, ConcreteFunction, LazyFunction, Struct, Class, Template, Enum, Modifier, Variable}
import objects.types.VoidType
import sl.Compilation.DefaultFunction

object ContextBuilder{
    def build(name: String, inst: Instruction):Context = {
        val context = Context.getNew(name)
        val extra = Utils.getLib("__init__").get

        DefaultFunction.get()(context)

        buildRec(extra)(context)
        buildRec(inst)(context)

        Compiler.compile(extra, true)(context)
        Compiler.compile(inst, true)(context)

        context
    }

    private def buildRec(inst: Instruction)(implicit context: Context):Unit = {
        inst match{
            case StructDecl(name, block, modifier, parent) => {
                val parentStruct = parent match
                    case None => null
                    case Some(p) => context.getStruct(p)
                
                context.addStruct(new Struct(context, name, modifier, block, parentStruct))
                List()
            }
            case ClassDecl(name, block, modifier, parent) => {
                val parentClass = parent match
                    case None => if name != "object" then context.getClass("object") else null
                    case Some(p) => context.getClass(p)
                
                context.addClass(new Class(context, name, modifier, block, parentClass)).generate()
                List()
            }
            case EnumDecl(name, fields, values, modifier) => {
                val enm = context.addEnum(new Enum(context, name, modifier, fields))
                enm.addValues(values)
                List()
            }
            case TemplateDecl(name, block, modifier, parent) => {
                val parentTemplate = parent match
                    case None => null
                    case Some(p) => context.getTemplate(p)
                
                context.addTemplate(new Template(context, name, modifier, block, parentTemplate))
                List()
            }
            case TypeDef(name, typ) => {
                context.addTypeDef(name, typ)
                List()
            }
            case Package(name, block) => {
                buildRec(block)(context.push(name))
            }
            case InstructionList(block) => {
                block.foreach(p => buildRec(p)(context))
            }
            case InstructionBlock(block) => {
                block.foreach(p => buildRec(p)(context))
            }
            case ForGenerate(key, provider, instr) => {
                val cases = Utils.getForgenerateCases(key, provider)
                
                cases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(instr)((instr, elm) => Utils.subst(instr, elm._1, elm._2))).flatMap(Compiler.compile(_)).toList
            }
            case ForEach(key, provider, instr) => {
                val cases = Utils.getForeachCases(provider)
                
                cases.map(elm => Utils.subst(instr, key.toString(), elm)).flatMap(Compiler.compile(_)).toList
            }
            case Import(lib, value, alias) => {
                val ret = if (context.importFile(lib)){
                    buildRec(Utils.getLib(lib).get)(context.root)
                }
                else{
                    List()
                }
                if (value != null){
                    try{
                        context.addObjectFrom(value, if alias == null then value else alias, context.root.push(lib))
                    }catch{case _ =>{}}
                }
                else if (alias != null){
                    context.push(alias, context.getContext(lib))
                }
                ret
            }
            case _ => {
            }
        }
    }
}