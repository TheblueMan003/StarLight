package sl.Compilation

import objects.*
import objects.types.*
import objects.*
import sl.*

object DefaultFunction{
    def get()(implicit context: Context) = {
        val ctx = context.root.push("Compiler")
        if (Settings.target == MCJava){
            ctx.addFunction("cmdstore", CompilerFunction(context, "cmdstore", 
                    List(Argument("vari", MCObjectType, None), Argument("cmd", FuncType(List(), VoidType), None)),
                    VoidType,
                    Modifier.newPublic(),
                    (args: List[Expression]) => {
                        args match{
                            case LinkedVariableValue(vari)::LambdaValue(arg, instr)::Nil => {
                                val ret = Compiler.compile(instr)
                                (ret.take(ret.length - 1) ::: List(f"execute store result score ${vari.getSelector()} run "+ret.last), NullValue)
                            }
                            case VariableValue(vari)::LambdaValue(arg, instr)::Nil => {
                                val ret = Compiler.compile(instr)
                                (ret.take(ret.length - 1) ::: List(f"execute store result score ${context.getVariable(vari).getSelector()} run "+ret.last), NullValue)
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for substring")
                        }
                    }
                ))
        }
        if (Settings.target == MCBedrock){
            ctx.addFunction("random", CompilerFunction(context, "random", 
                    List(Argument("vari", MCObjectType, None), Argument("min", IntType, None), Argument("max", IntType, None)),
                    VoidType,
                    Modifier.newPublic(),
                    (args: List[Expression]) => {
                        args match{
                            case LinkedVariableValue(vari)::IntValue(min)::IntValue(max)::Nil => {
                                (List(f"scoreboard players random ${vari.getSelector()} $min $max"), NullValue)
                            }
                            case VariableValue(vari)::IntValue(min)::IntValue(max)::Nil => {
                                (List(f"scoreboard players random ${context.getVariable(vari).getSelector()} $min $max"), NullValue)
                            }
                            case other => throw new Exception(f"Illegal Arguments $other for substring")
                        }
                    }
                ))
        }
    }
}