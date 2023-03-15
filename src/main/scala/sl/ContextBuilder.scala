package sl

import objects.{Context, ConcreteFunction, LazyFunction, Struct, Class, Template, Enum, Modifier, Variable}
import objects.types.VoidType
import sl.Compilation.DefaultFunction
import objects.ObjectNotFoundException
import objects.Identifier

object ContextBuilder{
    val defaultLib = List("__init__")
    def build(name: String, inst: Instruction):Context = {
        val context = Context.getNew(name)
        val extra = defaultLib.map(Utils.getLib(_).get)

        DefaultFunction.get()(context)

        extra.foreach(a => buildRec(a)(context, Meta(true, false)))
        buildRec(inst)(context, Meta(true, false))

        extra.foreach(a => Compiler.compile(a, Meta(true, false))(context))
        Compiler.compile(inst, Meta(true, false))(context)

        context
    }

    private def buildRec(inst: Instruction)(implicit context: Context, meta: Meta):Unit = {
        inst match{
            case StructDecl(name, generics, block, modifier, parent) => {
                val parentStruct = parent match
                    case None => null
                    case Some(p) => context.getStruct(p)
                
                context.addStruct(new Struct(context, name, generics, modifier, block, parentStruct))
            }
            case ClassDecl(name, generics, block, modifier, parent, entity) => {
                val parentClass = parent match
                    case None => if name != "object" then context.getClass("object") else null
                    case Some(p) => context.getClass(p)
                
                context.addClass(new Class(context, name, generics, modifier, block, parentClass, entity))
            }
            case EnumDecl(name, fields, values, modifier) => {
                val enm = context.addEnum(new Enum(context, name, modifier, fields))
                List()
            }
            case TemplateDecl(name, block, modifier, parent) => {
                val parentTemplate = parent match
                    case None => null
                    case Some(p) => context.getTemplate(p)
                
                context.addTemplate(new Template(context, name, modifier, block, parentTemplate))
            }
            case TypeDef(name, typ) => {
                context.addTypeDef(name, typ)
            }
            case Package(name, block) => {
                if (name == "_"){
                    if (!meta.isLib){
                        buildRec(Settings.globalImport)(context.root, meta)
                    }
                    buildRec(block)(context.root, meta)
                }
                else{
                    if (!meta.isLib){
                        buildRec(Settings.globalImport)(context.root.push(name), meta)
                    }
                    buildRec(block)(context.root.push(name), meta)
                }
            }
            case InstructionList(block) => {
                block.foreach(p => buildRec(p)(context, meta))
            }
            case InstructionBlock(block) => {
                block.foreach(p => buildRec(p)(context, meta))
            }
            case ForGenerate(key, provider, instr) => {
                val cases = Utils.getForgenerateCases(key, provider)
                
                cases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(instr)((instr, elm) => Utils.subst(instr, elm._1, elm._2))).flatMap(Compiler.compile(_)).toList
            }
            case ForEach(key, provider, instr) => {
                val cases = Utils.getForeachCases(key.toString(), provider)
                
                cases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(instr)((instr, elm) => Utils.subst(instr, elm._1, elm._2))).flatMap(Compiler.compile(_)).toList
            }
            case Import(lib, value, alias) => {
                if (context.importFile(lib)){
                    val libp = Utils.getLib(lib)
                    if (libp.isEmpty){
                        throw new ObjectNotFoundException(f"Cannot find package: $lib")
                    }
                    val libC = libp.get
                    buildRec(libC)(context.root, meta)
                    Compiler.compile(libC, meta)(context.root)
                }

                if (value != null){
                    try{
                        context.addObjectFrom(value, if alias == null then value else alias, context.root.push(lib))
                    }catch{case _ =>{}}
                }
                else{
                        val last = Identifier.fromString(lib).values.last
                        context.hasObject(lib+"."+last) match
                            case true => context.addObjectFrom(last, if alias == null then last else alias, context.root.push(lib))
                            case false if alias!=null => context.push(alias, context.getContext(lib))
                            case false => {}
                    }
            }
            case _ => {
            }
        }
    }
}