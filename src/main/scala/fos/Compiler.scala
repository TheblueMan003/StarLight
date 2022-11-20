package fos

import objects.{Context, ConcreteFunction, LazyFunction, Modifier, Struct, Variable, Enum, EnumField}
import objects.Identifier
import objects.types.{VoidType, TupleType}
import fos.Compilation.Execute

object Compiler{
    def compile(context: Context):List[(String, List[String])] = {
        var fct = context.getFunctionToCompile()

        while(fct != null){
            fct.compile()
            fct = context.getFunctionToCompile()
        }

        context.getMuxes().foreach(x => x.compile())


        context.getAllFunction().filter(_.exists()).map(fct => (fct.getName(), fct.getContent())) ::: 
            context.getAllJsonFiles().filter(_.exists()).map(fct => (fct.getName(), fct.getContent())):::
            Settings.target.getExtraFiles(context)
    }
    def compile(instruction: Instruction, firstPass: Boolean = false)(implicit context: Context):List[String]={        
        instruction match{
            case FunctionDecl(name, block, typ, args, modifier) =>{
                val fname = context.getFunctionWorkingName(name)
                if (!modifier.isLazy){
                    val func = new ConcreteFunction(context, fname, args, context.getType(typ), modifier, block, firstPass)
                    context.addFunction(name, func)
                    func.generateArgument()(context)
                }
                else{
                    val func = new LazyFunction(context, name, args, context.getType(typ), modifier, block)
                    context.addFunction(name, func)
                    func.generateArgument()(context)
                }
                List()
            }
            case StructDecl(name, block, modifier) => {
                context.addStruct(new Struct(context, name, modifier, block))
                List()
            }
            case EnumDecl(name, fields, values, modifier) => {
                val enm = context.addEnum(new Enum(context, name, modifier, fields.map(x => EnumField(x.name, context.getType(x.typ)))))
                enm.addValues(values)
                List()
            }
            case VariableDecl(name, typ, modifier) => {
                val vari = new Variable(context, name, context.getType(typ), modifier)
                context.addVariable(vari)
                vari.generate()
                List()
            }
            case fos.JSONFile(name, json) => {
                context.addJsonFile(new objects.JSONFile(context, name, Modifier.newPrivate(), Utils.compileJson(json)))
                List()
            }


            case VariableAssigment(names, op, expr) => {
                if (names.length == 1){
                    context.getVariable(names.head).assign(op, Utils.simplify(expr))
                }
                else{
                    val simplied = Utils.simplify(expr)
                    val varis = names.map(context.getVariable(_))
                    simplied match
                        case TupleValue(lst) => varis.zip(lst).flatMap(p => p._1.assign(op, p._2))
                        case VariableValue(name) => {
                            val vari = context.getVariable(name) 
                            vari.getType() match
                                case TupleType(sub) => varis.zip(vari.tupleVari).flatMap(p => p._1.assign(op, LinkedVariableValue(p._2)))
                                case _ => varis.flatMap(_.assign(op, simplied))
                            
                        }
                        case _ => varis.flatMap(_.assign(op, simplied))
                }
            }
            case Return(value) => {
                context.getCurrentFunction() match
                    case cf: ConcreteFunction => cf.returnVariable.assign("=", value)
                    case _ => throw new Exception(f"Unexpected return at ${instruction.pos}")
            }
            case CMD(value) => List(value)
            case Package(name, block) => {
                val sub = context.push(name)
                val init = sub.getNamedBlock("__init__", compile(block, firstPass)(sub))
                init.modifiers.isLoading = true
                List()
            }
            case InstructionList(block) => {
                block.flatMap(inst => compile(inst, firstPass))
            }
            case InstructionBlock(block) => {
                block.flatMap(inst => compile(inst, firstPass))
            }
            case FunctionCall(name, args) => {
                context.getFunction(name, args).call()
            }
            case LinkedFunctionCall(name, args, ret) => {
                name.call(args, ret)
            }
            case ifb: If => Execute.ifs(ifb)
            case swit: Switch => Execute.switch(swit)
            case whl: WhileLoop => Execute.whileLoop(whl)
            case whl: DoWhileLoop => Execute.doWhileLoop(whl)
            case at: At => Execute.atInstr(at)
            case wth: With => Execute.withInstr(wth)
            case ElseIf(cond, ifBlock) => throw new Exception("Unexpected Instruction")
        }
    }
}