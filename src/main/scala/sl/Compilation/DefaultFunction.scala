package sl.Compilation

import objects.*
import objects.types.*
import objects.*
import sl.*
import java.util.Random
import sys.process._
import java.nio.file.Files
import java.nio.file.Paths

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
                            ctx.parent.addVariable(ctx.name, ctx.getVariable(vari))
                            (List(), NullValue)
                        }
                        case LinkedVariableValue(vari, sel)::Nil =>{
                            ctx.parent.addVariable(ctx.name, vari)
                            (List(), NullValue)
                        }
                        case StringValue(vari)::Nil => {
                            ctx.parent.addVariable(ctx.name, ctx.getVariable(vari))
                            (List(), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for pushUpward")
                    }
                }
            ))
        ctx.addFunction("sqrt", CompilerFunction(ctx, "sqrt", 
                List(Argument("v", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case FloatValue(v)::Nil => {
                            (List(), FloatValue(math.sqrt(v)))
                        }
                        case IntValue(v)::Nil =>{
                            (List(), FloatValue(math.sqrt(v)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for sqrt")
                    }
                }
            ))
        ctx.addFunction("pow", CompilerFunction(ctx, "pow", 
                List(Argument("x", FloatType, None), Argument("y", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case FloatValue(x)::FloatValue(y)::Nil => {
                            (List(), FloatValue(math.pow(x,y)))
                        }
                        case FloatValue(x)::IntValue(y)::Nil => {
                            (List(), FloatValue(math.pow(x,y)))
                        }
                        case IntValue(x)::FloatValue(y)::Nil => {
                            (List(), FloatValue(math.pow(x,y)))
                        }
                        case IntValue(x)::IntValue(y)::Nil => {
                            (List(), FloatValue(math.pow(x,y)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for pow")
                    }
                }
            ))
        ctx.addFunction("powInt", CompilerFunction(ctx, "powInt", 
                List(Argument("x", FloatType, None), Argument("y", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case IntValue(x)::IntValue(y)::Nil => {
                            (List(), IntValue(math.pow(x,y).toInt))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for powInt")
                    }
                }
            ))
        ctx.addFunction("sin", CompilerFunction(ctx, "sin", 
                List(Argument("v", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case FloatValue(v)::Nil => {
                            (List(), FloatValue(math.sin(v)))
                        }
                        case IntValue(v)::Nil =>{
                            (List(), FloatValue(math.sin(v)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for sin")
                    }
                }
            ))
        ctx.addFunction("cos", CompilerFunction(ctx, "cos", 
                List(Argument("v", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case FloatValue(v)::Nil => {
                            (List(), FloatValue(math.cos(v)))
                        }
                        case IntValue(v)::Nil =>{
                            (List(), FloatValue(math.cos(v)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for cos")
                    }
                }
            ))
        ctx.addFunction("tan", CompilerFunction(ctx, "tan", 
                List(Argument("v", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case FloatValue(v)::Nil => {
                            (List(), FloatValue(math.tan(v)))
                        }
                        case IntValue(v)::Nil =>{
                            (List(), FloatValue(math.tan(v)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for tan")
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
        ctx.addFunction("getContextName", CompilerFunction(ctx, "getContextName", 
            List(),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), StringValue(ctx.name))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getContextName")
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
                    case StringValue(block) :: Nil => {
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
                    case StringValue(block) :: Nil => {
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
                    case StringValue(block) :: Nil => {
                        (List(), IntValue(BlockConverter.getBlockID(block)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getBedrockBlockID")
                }
            }
        ))


        ctx.addFunction("getJavaSound", CompilerFunction(ctx, "getJavaSound", 
            List(Argument("sound", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(sound) :: Nil => {
                        (List(), NamespacedName(Sounds.getJava(sound.split(":")(1))))
                    }
                    case StringValue(sound) :: Nil => {
                        if (sound.contains(":")){
                            (List(), NamespacedName(Sounds.getJava(sound.split(":")(1))))
                        }
                        else{
                            (List(), NamespacedName(Sounds.getJava(sound)))
                        }
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getJavaSound")
                }
            }
        ))

        ctx.addFunction("getBedrockSound", CompilerFunction(ctx, "getBedrockSound", 
            List(Argument("sound", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(sound) :: Nil => {
                        (List(), NamespacedName(Sounds.getBedrock(sound.split(":")(1))))
                    }
                    case StringValue(sound) :: Nil => {
                        if (sound.contains(":")){
                            (List(), NamespacedName(Sounds.getBedrock(sound.split(":")(1))))
                        }
                        else{
                            (List(), NamespacedName(Sounds.getBedrock(sound)))
                        }
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getBedrock")
                }
            }
        ))

        ctx.addFunction("getNamespace", CompilerFunction(ctx, "getNamespace", 
            List(Argument("name", MCObjectType, None)),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(name) :: Nil => {
                        (List(), StringValue(name.split(":")(0)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getNamespace")
                }
            }
        ))
        ctx.addFunction("getNamespaceName", CompilerFunction(ctx, "getNamespaceName", 
            List(Argument("name", MCObjectType, None)),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(name) :: Nil => {
                        (List(), StringValue(name.split(":")(1)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getNamespaceName")
                }
            }
        ))

        if (Settings.target == MCJava){
            ctx.addFunction("blockbenchSummon", CompilerFunction(ctx, "blockbenchSummon", 
                List(Argument("name", MCObjectType, None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case NamespacedName(name) :: Nil => {
                            val e = name.split(":")(1)
                            val splitted = e.split("\\.")
                            if (splitted.length == 1){
                                (List(f"function ${splitted(0)}:summon/default"), (NullValue))
                            }
                            else{
                                (List(f"function ${splitted(0)}:summon/${splitted(1)}"), (NullValue))
                            }
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for blockbenchSummon")
                    }
                }
            ))
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
    def getFile()(implicit context: Context)={
        val ctx = context.root.push("File")
        ctx.addFunction("exists", CompilerFunction(ctx, "exists", 
            List(Argument("file", StringType, None)),
            BoolType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case StringValue(file) :: Nil => {
                        (List(), BoolValue(java.io.File(file).exists()))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for exists")
                }
            }
        ))
        ctx.addFunction("isDirectory", CompilerFunction(ctx, "isDirectory", 
            List(Argument("file", StringType, None)),
            BoolType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case StringValue(file) :: Nil => {
                        (List(), BoolValue(java.io.File(file).isDirectory()))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for isDirectory")
                }
            }
        ))
        ctx.addFunction("isFile", CompilerFunction(ctx, "isFile", 
            List(Argument("file", StringType, None)),
            BoolType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case StringValue(file) :: Nil => {
                        (List(), BoolValue(java.io.File(file).isFile()))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for isFile")
                }
            }
        ))
        ctx.addFunction("run", CompilerFunction(ctx, "run", 
            List(Argument("file", StringType, None)),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case StringValue(file) :: Nil => {
                        (List(), StringValue(Process(file).!!))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for run")
                }
            }
        ))
    }
}