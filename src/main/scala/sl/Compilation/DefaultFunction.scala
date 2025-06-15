package sl.Compilation

import objects.*
import objects.types.*
import objects.*
import sl.*
import java.util.Random
import sys.process._
import java.nio.file.Files
import java.nio.file.Paths
import sl.IR.*
import scala.io.Source
import Selector._

object DefaultFunction{
    def get()(implicit context: Context) = {
        val ctx = context.root.push("Compiler")

        ctx.addFunction("readJson", CompilerFunction(ctx, "readJson", 
                    List(Argument("path", StringType, None)),
                    JsonType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args.map(_ match{
                            case StringValue(path) => {
                                (List(), JsonValue(Parser.parseJson(Source.fromFile(path, "UTF-8").mkString)))
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for readJson")
                        }).reduce((a,b) => (a._1:::b._1, JsonValue(Utils.combineJson(">:", a._2.asInstanceOf[JsonValue].content, b._2.asInstanceOf[JsonValue].content))))
                    }
                ))
        ctx.addFunction("mergeSelector", CompilerFunction(ctx, "mergeSelector", 
                    List(Argument("selector1", EntityType, None), Argument("selector2", EntityType, None)),
                    EntityType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case SelectorValue(sel1)::SelectorValue(sel2)::Nil => {
                                (List(), SelectorValue(sel1.merge(sel2)))
                            }
                            case SelectorValue(sel1)::LinkedVariableValue(sel2, _)::Nil => {
                                (List(), SelectorValue(sel1.merge(sel2.getEntityVariableSelector())))
                            }
                            case LinkedVariableValue(sel1, _)::SelectorValue(sel2)::Nil => {
                                (List(), SelectorValue(sel1.getEntityVariableSelector().merge(sel2)))
                            }
                            case LinkedVariableValue(sel1, _)::LinkedVariableValue(sel2, _)::Nil => {
                                (List(), SelectorValue(sel1.getEntityVariableSelector().merge(sel2.getEntityVariableSelector())))
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for mergeSelector")
                        }
                    }
                ))
        ctx.addFunction("rgb", CompilerFunction(ctx, "rgb", 
                    List(Argument("r", EntityType, None), Argument("g", EntityType, None), Argument("b", EntityType, None)),
                    EntityType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case IntValue(r)::IntValue(g)::IntValue(b)::Nil => {
                                val red = Math.min(255, Math.max(0, r))
                                val green = Math.min(255, Math.max(0, g))
                                val blue = Math.min(255, Math.max(0, b))
                                
                                (List(), StringValue(f"#${red}%02X${green}%02X${blue}%02X"))
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for rgb")
                        }
                    }
                ))
        ctx.addFunction("randomCompileTime", CompilerFunction(ctx, "randomCompileTime", 
                    List(),
                    IntType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case Nil => {
                                (List(), IntValue(Random().nextInt()))
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for randomCompileTime")
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
                                case Some(v) => (List(), BoolValue(v.getType() != VoidType))
                                case a => (List(), BoolValue(false))
                            }
                        }
                        case LinkedVariableValue(vari, sel)::Nil => (List(), BoolValue(vari.getType() != VoidType))
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
                            val clzz = ctx.getClass(vari)
                            (clzz.addClassTags() ::: clzz.addVariableDefaultAssign(), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for addClassTags")
                    }
                },
                false
            ))
        ctx.addFunction("getTemplateName", CompilerFunction(ctx, "getTemplateName", 
                List(),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case Nil => {
                            (List(), StringValue(ctx.getCurrentTemplateUse()))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for getTemplateName")
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
        ctx.addFunction("hash", CompilerFunction(ctx, "hash", 
                List(Argument("v", StringType, None)),
                IntType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case StringValue(value)::Nil => {
                            (List(), IntValue(scala.util.hashing.MurmurHash3.stringHash(value)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for hash")
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
                            ctx.addScoreboardUsedForce(vari.getIRSelector())
                            (List(), NamespacedName(vari.getSelectorObjective()))
                        }
                        case VariableValue(vari, sel)::Nil => {
                            val varj = ctx.getVariable(vari)
                            ctx.addScoreboardUsedForce(varj.getIRSelector())
                            (List(), NamespacedName(varj.getSelectorObjective()))
                        }
                        case sv::Nil if sv.hasIntValue() => {
                            ctx.requestConstant(sv.getIntValue())
                            (List(), NamespacedName(Settings.constScoreboard))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for getObjective")
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
                        ctx.addScoreboardUsedForce(vari.getIRSelector())
                        (List(), NamespacedName(vari.getSelectorName()(sel)))
                    }
                    case VariableValue(vari, sel)::Nil => {
                        val varj = ctx.getVariable(vari)
                        ctx.addScoreboardUsedForce(varj.getIRSelector())
                        (List(), NamespacedName(varj.getSelectorName()(sel)))
                    }
                    case sv::Nil if sv.hasIntValue() => {
                        ctx.requestConstant(sv.getIntValue())
                        (List(), NamespacedName(sv.getIntValue().toString))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getSelector")
                }
            }
        ))
        ctx.addFunction("getEntitySelector", CompilerFunction(ctx, "getEntitySelector", 
            List(Argument("vari", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case value::Nil => {
                        val (ir, _, sel) = Utils.getSelector(value)(ctx)
                        (ir, SelectorValue(sel))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getEntitySelector")
                }
            },
            false
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
                        val compiled = Utils.compileJson(value)
                        var nbt = compiled.getNbt()
                        (List(), StringValue(nbt))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for toNBT")
                }
            }
        ))

        ctx.addFunction("opNBT", CompilerFunction(ctx, "opNBT", 
            List(Argument("op", StringType, None), Argument("vari", MCObjectType, None)),
            VoidType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case StringValue(op) :: JsonValue(value)::Nil => {
                        val compiled = Utils.compileJson(value)
                        var nbt = ""
                        var lst = List[IRTree]()
                        try{
                            nbt = "value "+compiled.getNbt()
                        }
                        catch{
                            _ => {
                                val vari = ctx.getFreshVariable(JsonType)
                                nbt = f"from ${vari.getStorage()}"
                                lst = vari.assign("=", JsonValue(compiled))
                            }
                        }

                        (lst ::: List(CommandIR(f"data $op $nbt")), NullValue)
                    }
                    case StringValue(op) :: LinkedVariableValue(vari, sel) :: Nil =>{
                        (List(CommandIR(f"data $op from ${vari.getStorage()(ctx, sel)}")), NullValue)
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for opNBT")
                }
            }
        ))

        ctx.addFunction("getProjectVersionType", CompilerFunction(ctx, "getProjectVersionType", 
            List(),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        if (Settings.version(0) == -3){
                            (List(), StringValue("pre-alpha"))
                        }else if (Settings.version(0) == -2){
                            (List(), StringValue("alpha"))
                        }else if (Settings.version(0) == -1){
                            (List(), StringValue("beta"))
                        }else if (Settings.version(0) == 0){
                            (List(), StringValue("pre-release"))
                        }else{
                            (List(), StringValue("release"))
                        }
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getProjectVersionType")
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

        ctx.addFunction("getProjectFullName", CompilerFunction(ctx, "getProjectFullName", 
            List(),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), StringValue(Settings.outputName))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getProjectFullName")
                }
            }
        ))

        ctx.addFunction("getProjectAuthor", CompilerFunction(ctx, "getProjectAuthor", 
            List(),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), StringValue(Settings.author))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getProjectAuthor")
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

        ctx.addFunction("getCompilerVersionMajor", CompilerFunction(ctx, "getCompilerVersionMajor", 
            List(),
            IntType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), IntValue(Main.version(0)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getCompilerVersionMajor")
                }
            }
        ))

        ctx.addFunction("getCompilerVersionMinor", CompilerFunction(ctx, "getCompilerVersionMinor", 
            List(),
            IntType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), IntValue(Main.version(1)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getCompilerVersionMinor")
                }
            }
        ))

        ctx.addFunction("getCompilerVersionPatch", CompilerFunction(ctx, "getCompilerVersionPatch", 
            List(),
            IntType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        (List(), IntValue(Main.version(2)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getCompilerVersionPatch")
                }
            }
        ))

        ctx.addFunction("getJavaBlock", CompilerFunction(ctx, "getJavaBlock", 
            List(Argument("block", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(block, json) :: Nil => {
                        (List(), NamespacedName(block, json))
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
                    case (block: NamespacedName) :: Nil => {
                        (List(), NamespacedName(BlockConverter.getBlockName(block.getString())))
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
                    case (block: NamespacedName) :: Nil => {
                        (List(), StringValue(BlockConverter.getBlockID(block.getString())))
                    }
                    case StringValue(block) :: Nil => {
                        (List(), StringValue(BlockConverter.getBlockID(block)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getBedrockBlockID")
                }
            }
        ))

        ctx.addFunction("getBedrockItemID", CompilerFunction(ctx, "getBedrockItemID", 
            List(Argument("block", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case (block: NamespacedName) :: Nil => {
                        (List(), StringValue(BlockConverter.getItemID(block.getString())))
                    }
                    case StringValue(block) :: Nil => {
                        (List(), StringValue(BlockConverter.getItemID(block)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getBedrockItemID")
                }
            }
        ))


        ctx.addFunction("getJavaSound", CompilerFunction(ctx, "getJavaSound", 
            List(Argument("sound", MCObjectType, None)),
            MCObjectType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(sound, _) :: Nil => {
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
                    case NamespacedName(sound, _) :: Nil => {
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
                    case NamespacedName(name, _) :: Nil => {
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
                    case NamespacedName(name, _) :: Nil => {
                        (List(), StringValue(name.split(":")(1)))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getNamespaceName")
                }
            }
        ))
        ctx.addFunction("getNamespaceJson", CompilerFunction(ctx, "getNamespaceJson", 
            List(Argument("name", MCObjectType, None)),
            JsonType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case NamespacedName(name, json) :: Nil => {
                        (List(), json)
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getNamespaceJson")
                }
            }
        ))
        ctx.addFunction("getClassName", CompilerFunction(ctx, "getClassName", 
            List(),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case Nil => {
                        val clazz = ctx.getCurrentClass()
                        clazz match{
                            case c => (List(), StringValue(c.fullName))
                            case null => throw new Exception("Not in a class")
                        }
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getClassName")
                }
            }
        ))
        ctx.addFunction("getStorage", CompilerFunction(ctx, "getStorage", 
            List(Argument("name", MCObjectType, None)),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case LinkedVariableValue(name, _) :: Nil => {
                        (List(), StringValue(name.getStorage().toString()))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for getStorage")
                }
            }
        ))
        ctx.addFunction("insert", CompilerFunction(ctx, "insert", 
                List(Argument("name", MCObjectType, None), Argument("vari", MCObjectType, None), Argument("cmd", FuncType(List(), VoidType), None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case VariableValue(name, sel1):: s ::LambdaValue(arg, instr, ctx2)::Nil => {
                            val ret = Compiler.compile(Utils.subst(instr, name.toString(), s.getString()))(ctx2)
                            (ret, NullValue)
                        }
                        case TupleValue(names):: TupleValue(replaces) ::LambdaValue(arg, instr, ctx2)::Nil => {
                            val substed = names.zip(replaces).foldLeft(instr){
                                case (instr, (VariableValue(name, sel1), replace)) => 
                                    Utils.subst(instr, name.toString(), replace.getString());
                                case _ => throw new Exception("Illegal Arguments")}

                            val ret = Compiler.compile(substed)(ctx2)
                            (ret, NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for insert")
                    }
                },
                false
            ))
        ctx.addFunction("insertMacro", CompilerFunction(ctx, "insertMacro", 
                List(Argument("name", MCObjectType, None), Argument("vari", MCObjectType, None), Argument("cmd", FuncType(List(), VoidType), None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case VariableValue(args2, sel1):: s ::LambdaValue(arg, instr, ctx2)::Nil => {
                            val name = ctx.getFreshLambdaName()
                            val args = List(Argument(args2.toString().replace("$",""), MCObjectType, None))
                            val mod = Modifier.newPublic()
                            mod.isMacro = true
                            val gen = InstructionList(List(
                                FunctionDecl(name, instr, VoidType, args, List(), mod),
                                FunctionCall(Identifier.fromString(name), List(s), List())
                            ))
                            val ret = Compiler.compile(gen)(ctx2)
                            (ret, NullValue)
                        }
                        case TupleValue(names):: TupleValue(replaces) ::LambdaValue(arg, instr, ctx2)::Nil => {
                            val name = ctx.getFreshId()
                            val args = names.map{
                                case VariableValue(name, sel1) => Argument(name.toString().replace("$",""), MCObjectType, None)
                                case other => throw new Exception("Illegal Arguments")
                            }
                            val mod = Modifier.newPublic()
                            mod.isMacro = true
                            val gen = InstructionList(List(
                                FunctionDecl(name, instr, VoidType, args, List(), mod),
                                FunctionCall(Identifier.fromString(name), replaces, List())
                            ))
                            val ret = Compiler.compile(gen)(ctx2)
                            (ret, NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for macro")
                    }
                },
                false
            ))
        ctx.addFunction("print", CompilerFunction(ctx, "print", 
                List(Argument("name", MCObjectType, None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    Reporter.debug(args)
                    (List(), NullValue)
                },
                false
            ))
        ctx.addFunction("toString", CompilerFunction(ctx, "toString", 
                List(Argument("value", MCObjectType, None)),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case value::Nil => {
                            (List(), StringValue(value.getString()))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for toString")
                    }
                }
            ))
        ctx.addFunction("stringify", CompilerFunction(ctx, "stringify", 
                List(Argument("value", StringType, None)),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case StringValue(value)::Nil => {
                            (List(), StringValue(Utils.stringify(value)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for stringify")
                    }
                }
            ))
        ctx.addFunction("replace", CompilerFunction(ctx, "replace", 
                List(Argument("src", StringType, None), Argument("from", StringType, None), Argument("to", StringType, None)),
                StringType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case StringValue(src)::StringValue(from)::StringValue(to)::Nil => {
                            (List(), StringValue(src.replace(from, to)))
                        }
                        case RawJsonValue(value)::StringValue(from)::StringValue(to)::Nil => {
                            (List(), RawJsonValue(
                                value.map{
                                    case PrintString(text, color, modifier) => PrintString(text.replace(from, to), color, modifier)
                                    case other => other
                                }
                            ))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for replace")
                    }
                }
            ))
        ctx.addFunction("toRadians", CompilerFunction(ctx, "toRadians", 
                List(Argument("value", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case FloatValue(src)::Nil => {
                            (List(), FloatValue(Math.toRadians(src)))
                        }
                        case IntValue(src)::Nil => {
                            (List(), FloatValue(Math.toRadians(src)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for toRadians")
                    }
                }
            ))
        ctx.addFunction("toDegrees", CompilerFunction(ctx, "toDegrees", 
                List(Argument("value", FloatType, None)),
                FloatType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case FloatValue(src)::Nil => {
                            (List(), FloatValue(Math.toDegrees(src)))
                        }
                        case IntValue(src)::Nil => {
                            (List(), FloatValue(Math.toDegrees(src)))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for toDegrees")
                    }
                }
            ))
        ctx.addFunction("makeUnique", CompilerFunction(ctx, "makeUnique", 
                List(Argument("entity", EntityType, None)),
                EntityType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case SelectorValue(sel)::Nil => {
                            (List(), SelectorValue(sel.makeUnique()))
                        }
                        case LinkedVariableValue(vari, sel)::Nil => {
                            (List(), SelectorValue(vari.getEntityVariableSelector().makeUnique()))
                        }
                        case VariableValue(vari, sel)::Nil => {
                            (List(), SelectorValue(ctx.getVariable(vari).getEntityVariableSelector().makeUnique()))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for makeUnique")
                    }
                }
            ))
        ctx.addFunction("callToArray", CompilerFunction(ctx, "callToArray", 
            List(Argument("cmd", FuncType(List(), VoidType), None)),
            VoidType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case LambdaValue(arg, instr, ctx2)::Nil => {
                        val ret = Compiler.compile(instr)(ctx2)
                        (List(), JsonValue(JsonArray(ret.map(v => JsonString(v.getString())))))
                    }
                    case VariableValue(vari, sel)::Nil => {
                        val ret = Compiler.compile(FunctionCall(vari, List(), List()))(ctx)
                        (List(), JsonValue(JsonArray(ret.map(v => JsonString(v.getString())))))
                    }
                    case LinkedFunctionValue(func)::Nil => {
                        val ret = Compiler.compile(LinkedFunctionCall(func, List(), null))(ctx)
                        (List(), JsonValue(JsonArray(ret.map(v => JsonString(v.getString())))))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for callToArray")
                }
            }
        ))
        ctx.addFunction("interpreterException", CompilerFunction(ctx, "interpreterException", 
                List(Argument("msg", StringType, None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case StringValue(value)::Nil => {
                            (List(InterpreterException(value)), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for interpreterException")
                    }
                },
                false
            ))
        if (Settings.target == MCJava){
            ctx.addFunction("blockbenchSummon", CompilerFunction(ctx, "blockbenchSummon", 
                List(Argument("name", MCObjectType, None)),
                EntityType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case NamespacedName(name, _) :: Nil => {
                            val e = name.split(":")(1)
                            val splitted = e.split("\\.")
                            
                            if (splitted.length == 1){
                                (List(CommandIR(f"function animated_java:${splitted(0)}/summon")), (SelectorValue(JavaSelector("@e", List(("tag", SelectorIdentifier(f"aj.${splitted(0)}.root")))))))
                            }
                            else{
                                (List(CommandIR(f"function animated_java:${splitted(0)}/summon/${splitted(1)}")), (SelectorValue(JavaSelector("@e", List(("tag", SelectorIdentifier(f"aj.${splitted(0)}.root")))))))
                            }
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for blockbenchSummon")
                    }
                },
                false
            ))
            ctx.addFunction("getBlockbenchEntityName", CompilerFunction(ctx, "getBlockbenchEntityName", 
                List(),
                EntityType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case Nil => {
                            val clz = ctx.getCurrentClass()
                            val name = clz.getEntity().getString()
                            val e = name.split(":")(1)
                            val splitted = e.split("\\.")
                            
                            (List(), (StringValue(splitted(0))))
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for getBlockbenchEntityName")
                    }
                },
                false
            ))
            ctx.addFunction("cmdstore", CompilerFunction(ctx, "cmdstore", 
                    List(Argument("vari", MCObjectType, None), Argument("cmd", FuncType(List(), VoidType), None)),
                    VoidType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case LinkedVariableValue(vari, sel)::LambdaValue(arg, instr, ctx2)::Nil => {
                                val ret = Compiler.compile(instr)(ctx2)
                                ctx.addScoreboardUsedForce(vari.getIRSelector())
                                (ret.take(ret.length - 1) ::: List(CommandIR(f"execute store result score ${vari.getSelector()(sel)} run "+ret.last.getString())), NullValue)
                            }
                            case VariableValue(vari, sel)::LambdaValue(arg, instr, ctx2)::Nil => {
                                val ret = Compiler.compile(instr)(ctx2)
                                val varj = ctx.getVariable(vari)
                                ctx.addScoreboardUsedForce(varj.getIRSelector())
                                (ret.take(ret.length - 1) ::: List(CommandIR(f"execute store result score ${varj.getSelector()(sel)} run "+ret.last.getString())), NullValue)
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for cmdstore")
                        }
                    },
                false
                ))
            ctx.addFunction("cmdsuccess", CompilerFunction(ctx, "cmdsuccess", 
                    List(Argument("vari", MCObjectType, None), Argument("cmd", FuncType(List(), VoidType), None)),
                    VoidType,
                    Modifier.newPublic(),
                    (args: List[Expression],ctx: Context) => {
                        args match{
                            case LinkedVariableValue(vari, sel)::LambdaValue(arg, instr, ctx2)::Nil => {
                                val ret = Compiler.compile(instr)(ctx2)
                                ctx.addScoreboardUsedForce(vari.getIRSelector())
                                (ret.take(ret.length - 1) ::: List(CommandIR(f"execute store success score ${vari.getSelector()(sel)} run "+ret.last.getString())), NullValue)
                            }
                            case VariableValue(vari, sel)::LambdaValue(arg, instr, ctx2)::Nil => {
                                val ret = Compiler.compile(instr)(ctx2)
                                val varj = ctx.getVariable(vari)
                                ctx.addScoreboardUsedForce(varj.getIRSelector())
                                (ret.take(ret.length - 1) ::: List(CommandIR(f"execute store success score ${varj.getSelector()(sel)} run "+ret.last.getString())), NullValue)
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for cmdsuccess")
                        }
                    },
                false
                ))
            ctx.addFunction("schedule", CompilerFunction(ctx, "schedule", 
                List(Argument("fct", MCObjectType, None), Argument("time", IntType, None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case LinkedFunctionValue(vari)::IntValue(time)::Nil => {
                            (List(ScheduleCall(Settings.target.getFunctionName(vari.fullName),vari.fullName, time)), NullValue)
                        }
                        case VariableValue(fct, sel)::IntValue(time)::Nil => {
                            val vari = ctx.getFunction(fct)
                            (List(ScheduleCall(Settings.target.getFunctionName(vari.fullName),vari.fullName, time)), NullValue)
                        }
                        case LambdaValue(args, instr, ctx2)::IntValue(time)::Nil => {
                            val block = ctx2.getFreshLambda(args, List(), VoidType, instr, false)
                            block.markAsStringUsed()
                            (List(ScheduleCall(Settings.target.getFunctionName(block.fullName), block.fullName, time)), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for schedule")
                    }
                },
                false
            ))
            ctx.addFunction("scheduleClear", CompilerFunction(ctx, "scheduleClear", 
                List(Argument("fct", MCObjectType, None)),
                VoidType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case LinkedFunctionValue(vari)::Nil => {
                            (List(ScheduleClear(Settings.target.getFunctionName(vari.fullName),vari.fullName)), NullValue)
                        }
                        case VariableValue(fct, sel)::Nil => {
                            val vari = ctx.getFunction(fct)
                            (List(ScheduleClear(Settings.target.getFunctionName(vari.fullName),vari.fullName)), NullValue)
                        }
                        case other => throw new Exception(f"Illegal Arguments $other for scheduleClear")
                    }
                },
                false
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
                                ctx.addScoreboardUsedForce(vari.getIRSelector())
                                (List(CommandIR(f"scoreboard players random ${vari.getSelector()(sel)} $min $max")), NullValue)
                            }
                            case VariableValue(vari, sel)::IntValue(min)::IntValue(max)::Nil => {
                                val varj = ctx.getVariable(vari)
                                ctx.addScoreboardUsedForce(varj.getIRSelector())
                                (List(CommandIR(f"scoreboard players random ${varj.getSelector()(sel)} $min $max")), NullValue)
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for random")
                        }
                    },
                false
                ))
        }
        ctx.addFunction("runPython", CompilerFunction(ctx, "runPython", 
            List(Argument("file", StringType, None), Argument("arguments", StringType, None)),
            StringType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case StringValue(file) :: StringValue(args) :: Nil => {
                        (List(), StringValue(Process("python "+file+" "+args).!!))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for exists")
                }
            }
        ))
        ctx.addFunction("length", CompilerFunction(ctx, "length", 
            List(Argument("value", MCObjectType, None)),
            IntType,
            Modifier.newPublic(),
            (args: List[Expression],ctx: Context) => {
                args match{
                    case StringValue(value) :: Nil => {
                        (List(), IntValue(value.length))
                    }
                    case JsonValue(JsonArray(value)) :: Nil => {
                        (List(), IntValue(value.length))
                    }
                    case JsonValue(JsonDictionary(value)) :: Nil => {
                        (List(), IntValue(value.size))
                    }
                    case TupleValue(value) :: Nil => {
                        (List(), IntValue(value.length))
                    }
                    case (value: RawJsonValue) :: Nil => {
                        (List(), IntValue(value.length()(ctx)))
                    }
                    case PositionValue(px, py, pz)::Nil => {
                        val x = px.getFloatValue()
                        val y = py.getFloatValue()
                        val z = pz.getFloatValue()
                        (List(), FloatValue(Math.sqrt(x*x+y*y+z*z).toFloat))
                    }
                    case other => throw new Exception(f"Illegal Arguments $other for length")
                }
            }
        ))
        getFile()
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

    def getInt(ctx: Context, name: String, getter: ()=>Int)={
        CompilerFunction(ctx, name, 
                List(),
                BoolType,
                Modifier.newPublic(),
                (args: List[Expression],ctx: Context) => {
                    args match{
                        case Nil => (List(), IntValue(getter()))
                        case other => throw new Exception(f"Illegal Arguments $other for $name")
                    }
                }
            )
    }
}