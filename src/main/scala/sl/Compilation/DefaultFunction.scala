package sl.Compilation

import objects.*
import objects.types.*
import objects.*
import sl.*
import java.util.Random

object DefaultFunction{
    def get()(implicit context: Context) = {
        val ctx = context.root.push("Compiler")
        ctx.addFunction("random", CompilerFunction(context, "random", 
                    List(),
                    IntType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case Nil => {
                                (List(), IntValue(Random().nextInt()))
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for random")
                        }
                    }
                ))
        ctx.addFunction("addClassTags", CompilerFunction(context, "addClassTags", 
                List(Argument("class", MCObjectType, None)),
                MCObjectType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case StringValue(vari)::Nil => {
                            (ctx.getClass(vari).addClassTags(), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for random")
                    }
                }
            ))
        ctx.addFunction("getObjective", CompilerFunction(context, "getObjective", 
                List(Argument("vari", MCObjectType, None)),
                MCObjectType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case LinkedVariableValue(vari, sel)::Nil => {
                            (List(), NamespacedName(vari.getSelectorObjective()))
                        }
                        case VariableValue(vari, sel)::Nil => {
                            (List(), NamespacedName(ctx.getVariable(vari).getSelectorObjective()))
                        }
                        case sv::Nil if sv.hasIntValue() => {
                            context.requestConstant(sv.getIntValue())
                            (List(), NamespacedName(Settings.constScoreboard))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for random")
                    }
                }
            ))
        ctx.addFunction("getSelector", CompilerFunction(context, "getSelector", 
                List(Argument("vari", MCObjectType, None)),
                MCObjectType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case LinkedVariableValue(vari, sel)::Nil => {
                            (List(), NamespacedName(vari.getSelectorName()(sel)))
                        }
                        case VariableValue(vari, sel)::Nil => {
                            (List(), NamespacedName(ctx.getVariable(vari).getSelectorName()(sel)))
                        }
                        case sv::Nil if sv.hasIntValue() => {
                            context.requestConstant(sv.getIntValue())
                            (List(), NamespacedName(sv.getIntValue().toString))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for random")
                    }
                }
            ))

        if (Settings.target == MCJava){
            ctx.addFunction("cmdstore", CompilerFunction(context, "cmdstore", 
                    List(Argument("vari", MCObjectType, None), Argument("cmd", FuncType(List(), VoidType), None)),
                    VoidType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case LinkedVariableValue(vari, sel)::LambdaValue(arg, instr)::Nil => {
                                val ret = Compiler.compile(instr)
                                (ret.take(ret.length - 1) ::: List(f"execute store result score ${vari.getSelector()(sel)} run "+ret.last), NullValue)
                            }
                            case VariableValue(vari, sel)::LambdaValue(arg, instr)::Nil => {
                                val ret = Compiler.compile(instr)
                                (ret.take(ret.length - 1) ::: List(f"execute store result score ${ctx.getVariable(vari).getSelector()(sel)} run "+ret.last), NullValue)
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for cmdstore")
                        }
                    }
                ))
        }
        if (Settings.target == MCBedrock){
            ctx.addFunction("random", CompilerFunction(context, "random", 
                    List(Argument("vari", MCObjectType, None), Argument("min", IntType, None), Argument("max", IntType, None)),
                    VoidType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case LinkedVariableValue(vari, sel)::IntValue(min)::IntValue(max)::Nil => {
                                (List(f"scoreboard players random ${vari.getSelector()(sel)} $min $max"), NullValue)
                            }
                            case VariableValue(vari, sel)::IntValue(min)::IntValue(max)::Nil => {
                                (List(f"scoreboard players random ${ctx.getVariable(vari).getSelector()(sel)} $min $max"), NullValue)
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for random")
                        }
                    }
                ))
        }
    }
}