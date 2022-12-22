package sl.Compilation

import objects.*
import objects.types.*
import objects.*
import sl.*
import java.util.Random

object DefaultFunction{
    def get()(implicit context: Context) = {
        val ctx = context.root.push("Compiler")
        ctx.addFunction("random", CompilerFunction(ctx, "random", 
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
        ctx.addFunction("variableExist", CompilerFunction(ctx, "variableExist", 
                List(Argument("class", MCObjectType, None)),
                BoolType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case VariableValue(vari, sel)::Nil => {
                            ctx.tryGetVariable(vari) match{
                                case Some(v) => (List(), BoolValue(true))
                                case a => (List(), BoolValue(false))
                            }
                        }
                        case LinkedVariableValue(vari, sel)::Nil => (List(), BoolValue(true))
                        case other => throw new Exception(f"Illegal Arguments $other for variableExist")
                    }
                }
            ))
        ctx.addFunction("addClassTags", CompilerFunction(ctx, "addClassTags", 
                List(Argument("class", MCObjectType, None)),
                MCObjectType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case StringValue(vari)::Nil => {
                            (ctx.getClass(vari).addClassTags(), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for addClassTags")
                    }
                }
            ))
        ctx.addFunction("pushUpward", CompilerFunction(ctx, "pushUpward", 
                List(Argument("class", MCObjectType, None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case VariableValue(vari, sel)::Nil => {
                            ctx.parent.addVariable(ctx.getVariable(vari))
                            (List(), NullValue)
                        }
                        case LinkedVariableValue(vari, sel)::Nil =>{
                            ctx.parent.addVariable(vari)
                            (List(), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for pushUpward")
                    }
                }
            ))
        ctx.addFunction("getObjective", CompilerFunction(ctx, "getObjective", 
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
                            ctx.requestConstant(sv.getIntValue())
                            (List(), NamespacedName(Settings.constScoreboard))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for random")
                    }
                }
            ))
        ctx.addFunction("getSelector", CompilerFunction(ctx, "getSelector", 
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
                        ctx.requestConstant(sv.getIntValue())
                        (List(), NamespacedName(sv.getIntValue().toString))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getSelector")
                }
            }
        ))
        ctx.addFunction("getVariableTag", CompilerFunction(ctx, "getVariableTag", 
            List(Argument("vari", MCObjectType, None)),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case LinkedVariableValue(vari, sel)::Nil => {
                        (List(), StringValue(vari.tagName))
                    }
                    case VariableValue(vari, sel)::Nil => {
                        (List(), StringValue(ctx.getVariable(vari).tagName))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getVariableTag")
                }
            }
        ))
        ctx.addFunction("toNBT", CompilerFunction(ctx, "toNBT", 
            List(Argument("vari", MCObjectType, None)),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case JsonValue(value)::Nil => {
                        (List(), StringValue(value.getNbt()))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for toNBT")
                }
            }
        ))

        ctx.addFunction("getProjectVersionMajor", CompilerFunction(ctx, "getProjectVersionMajor", 
            List(),
            IntType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), IntValue(Settings.version(0)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getProjectVersionMajor")
                }
            }
        ))

        ctx.addFunction("getProjectVersionMinor", CompilerFunction(ctx, "getProjectVersionMinor", 
            List(),
            IntType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), IntValue(Settings.version(1)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getProjectVersionMinor")
                }
            }
        ))

        ctx.addFunction("getProjectVersionPatch", CompilerFunction(ctx, "getProjectVersionPatch", 
            List(),
            IntType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), IntValue(Settings.version(2)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getProjectVersionPatch")
                }
            }
        ))

        ctx.addFunction("getProjectName", CompilerFunction(ctx, "getProjectName", 
            List(),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), StringValue(Settings.name))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getProjectName")
                }
            }
        ))

        ctx.addFunction("getJavaBlock", CompilerFunction(ctx, "getJavaBlock", 
            List(Argument("block", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(block) :: Nil => {
                        (List(), NamespacedName(block))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getJavaBlock")
                }
            }
        ))

        ctx.addFunction("getBedrockBlockName", CompilerFunction(ctx, "getBedrockBlockName", 
            List(Argument("block", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(block) :: Nil => {
                        (List(), NamespacedName(BlockConverter.getBlockName(block)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getBedrockBlockName")
                }
            }
        ))

        ctx.addFunction("getBedrockBlockID", CompilerFunction(ctx, "getBedrockBlockID", 
            List(Argument("block", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(block) :: Nil => {
                        (List(), IntValue(BlockConverter.getBlockID(block)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getBedrockBlockID")
                }
            }
        ))

        if (Settings.target == MCJava){
            ctx.addFunction("cmdstore", CompilerFunction(ctx, "cmdstore", 
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
            ctx.addFunction("random", CompilerFunction(ctx, "random", 
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