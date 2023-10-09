package sl

import objects.{Context, ConcreteFunction, LazyFunction, Struct, Class, Template, Enum, Modifier, Variable}
import objects.types.VoidType
import sl.Compilation.DefaultFunction
import objects.ObjectNotFoundException
import objects.Identifier
import scala.collection.parallel.CollectionConverters._

object ContextBuilder{
    val defaultLib = List("__init__")

    def build(name: String, insts: List[Instruction]):Context = {
        if (Settings.experimentalMultithread){
            val context = Context.getNew(name)
            val extra = defaultLib.map(Utils.getLib(_).get)

            DefaultFunction.get()(context)

            extra.par.foreach(a => buildRec(a)(context, Meta(true, false)))
            insts.par.foreach(inst => buildRec(inst)(context, Meta(true, false)))

            extra.par.foreach(a => Compiler.compile(a, Meta(true, false))(context))
            insts.par.foreach(inst => Compiler.compile(inst, Meta(true, false))(context))

            context
        }
        else{
            val context = Context.getNew(name)
            val extra = defaultLib.map(Utils.getLib(_).get)

            DefaultFunction.get()(context)

            extra.foreach(a => buildRec(a)(context, Meta(true, false)))
            insts.foreach(inst => buildRec(inst)(context, Meta(true, false)))

            extra.foreach(a => Compiler.compile(a, Meta(true, false))(context))
            insts.foreach(inst => Compiler.compile(inst, Meta(true, false))(context))

            context
        }
    }

    private def buildRec(inst: Instruction)(implicit context: Context, meta: Meta):Unit = {
        inst match{
            case StructDecl(name, generics, block, modifier, parent) => {
                modifier.simplify()
                val parentName = parent match
                    case None => null
                    case Some(p) => Identifier.fromString(p)
                
                context.addStruct(new Struct(context, name, generics, modifier, block.unBlockify(), parentName))
            }
            case ClassDecl(name, generics, block, modifier, parent, parentGenerics, interfaces, entity) => {
                modifier.simplify()
                val parentName = parent match
                    case None => null
                    case Some(p) => Identifier.fromString(p)
                
                val c = new Class(context, name, generics, modifier, block.unBlockify(), parentName, parentGenerics, interfaces.map(x => (Identifier.fromString(x._1), x._2)), entity)
                context.addClass(c)
                c.checkIfShouldGenerate()
            }
            case EnumDecl(name, fields, values, modifier) => {
                modifier.simplify()
                val enm = context.addEnum(new Enum(context, name, modifier, fields))
                List()
            }
            case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => {
                modifier.simplify()
                val parentName = parent match
                    case None => null
                    case Some(p) => Identifier.fromString(p)
                
                context.addTemplate(new Template(context, name, modifier, block.unBlockify(), parentName, generics, parentGenerics))
            }
            case ExtensionDecl(typ, block, modifier) => {
                val id = context.getFreshId()
                Compiler.compile(Package(id, block), Meta(true, false))(context)
                val ctyp = context.getType(typ)
                context.addExtension(ctyp, context.root.push(id).getAllFunction().map(f => f._2))
            }
            case TypeDef(defs) => {
                defs.foreach{
                    case (name, typ, ver) => {
                        ver match{
                            case "" => context.addTypeDef(name, typ)
                            case "mcbedrock" if Settings.target == MCBedrock => context.addTypeDef(name, typ)
                            case "mcjava" if Settings.target == MCJava => context.addTypeDef(name, typ)
                            case _ => List()
                        }
                    }
                }
            }
            case Package(name, block) => {
                if (name == "_"){
                    if (!meta.isLib){
                        buildRec(Settings.globalImport)(context.root, meta)
                    }
                    buildRec(block.unBlockify())(context.root, meta)
                }
                else{
                    if (!meta.isLib){
                        buildRec(Settings.globalImport)(context.root.push(name), meta)
                    }
                    buildRec(block.unBlockify())(context.root.push(name), meta)
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
                context.root.synchronized{
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
                            context.addObjectFrom(value, Identifier.fromString(if alias == null then value else alias), context.root.push(lib))
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
            }
            case _ => {
            }
        }
    }
}