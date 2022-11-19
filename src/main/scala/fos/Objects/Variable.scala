package objects

import types.*
import fos.*

class Variable(context: Context, name: String, typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
  var tupleVari: List[Variable] = List()

  def generate()(implicit context: Context):Unit = {
    typ match
        case IdentifierType(typeName) => {
            val struct = context.getStruct(typeName)
            fos.Compiler.compile(struct.block)(context.push(name))
        }
        case TupleType(sub) => {
            val ctx = context.push(name)
            tupleVari = sub.zipWithIndex.map((t, i) => ctx.addVariable(new Variable(ctx, f"$i", t, _modifier)))
            tupleVari.map(_.generate()(ctx))
        }
        case _ => {
            List()
        }
    
  }
  def assign(op: String, value: Expression)(implicit context: Context): List[String] = {
    value match{
        case VariableValue(nm) if op == "=" && context.tryGetVariable(nm) == Some(this) =>{
            List()
        }
        case _ => {
            if (isPresentIn(value) && value.isInstanceOf[BinaryOperation]){
                val tmp = context.getFreshVariable(getType())
                tmp.assign("=", value) ::: assign(op, LinkedVariableValue(tmp))
            }
            else{
                typ match
                    case IntType => assignInt(op, value)
                    case FloatType => assignFloat(op, value)
                    case BoolType => assignBool(op, value)
                    case TupleType(sub) => assignTuple(op, value)                    
                    case FuncType(source, out) => assignFunc(op, value)
            }
        }
    }
  }

  def checkNull() = f"execute unless score ${getSelector()} = ${getSelector()} run"


  /**
   * Assign the value to a tmp variable then apply op with the variable.
   */
  def assignTmp(op: String, expr: Expression)(implicit context: Context): List[String] = {
    val vari = context.getFreshVariable(getType())
    vari.assign("=", expr) ::: assign(op, LinkedVariableValue(vari))
  }


  /**
   * Assign binary operator to the variable.
   */
  def assignBinaryOperator(op: String, value: BinaryOperation)(implicit context: Context): List[String] = {
    op match{
        case "=" => assign("=", value.left) ::: assign(value.op+"=", value.right)
        case "+=" => {
            value.op match
                case "+" => assign("+=", value.left) ::: assign("+=", value.right)
                case "-" => assign("+=", value.left) ::: assign("-=", value.right)
                case _ => assignTmp(op, value)
        }
        case "-=" => {
            value.op match
                case "+" => assign("-=", value.left) ::: assign("-=", value.right)
                case "-" => assign("-=", value.left) ::: assign("+=", value.right)
                case _ => assignTmp(op, value)
        }
        case "*=" => {
            value.op match
                case "*" => assign("*=", value.left) ::: assign("*=", value.right)
                case "/" => assign("*=", value.left) ::: assign("/=", value.right)
                case _ => assignTmp(op, value)
        }
        case "/=" => {
            value.op match
                case "*" => assign("/=", value.left) ::: assign("*=", value.right)
                case "/" => assign("/=", value.left) ::: assign("*=", value.right)
                case _ => assignTmp(op, value)
        }
        case other => assignTmp(op, value)
    }
  }


  /**
   * Assign a value to the int variable
   */
  def assignInt(op: String, value: Expression)(implicit context: Context): List[String] = {
    value match
        case IntValue(value) => {
            op match{
                case "=" => List(f"scoreboard players set ${getSelector()} ${value}")
                case "+=" => List(f"scoreboard players add ${getSelector()} ${value}")
                case "-=" => List(f"scoreboard players remove ${getSelector()} ${value}")
                case "*=" | "/=" | "%=" => {
                    context.requestConstant(value)
                    List(f"scoreboard players operation ${getSelector()} ${op} ${value} ${Settings.constScoreboard}")
                }
                case ":=" => List(f"${checkNull()} scoreboard players set ${getSelector()} ${value}")
            }
        }
        case BoolValue(value) => assignInt(op, IntValue(if value then 1 else 0))
        case FloatValue(value) => assignInt(op, IntValue(value.toInt))
        case VariableValue(name) => assignInt(op, LinkedVariableValue(context.getVariable(name)))
        case LinkedVariableValue(vari) => assignIntLinkedVariable(op, vari)
        case FunctionCallValue(name, args) => context.getFunction(name, args).call(this)
        case bin: BinaryOperation => assignBinaryOperator(op, bin)
        case _ => throw new Exception(f"Unknown cast to int $value")
  }

  def assignIntLinkedVariable(op: String, vari: Variable)(implicit context: Context) = {
    vari.getType() match{
        case FloatType => {
            op match{
                case "=" => {
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()}",
                            f"scoreboard players operation ${getSelector()} /= ${Settings.floatPrec}")
                }
                case other => {
                    val tmp = context.getFreshId()
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} ${op} ${vari.getSelector()}",
                            f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} /= ${Settings.floatPrec}",
                            f"scoreboard players operation ${getSelector()} = ${tmp} ${Settings.tmpScoreboard}"
                    )
                }
            }
        }
        case _ => {
            op match{
                case ":=" => List(f"${checkNull()} scoreboard players operation ${getSelector()} = ${vari.getSelector()}")
                case other => {
                    List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()}")
                }
            }
        }
    }
  }





  /**
   * Assign a value to the float variable
   */
  def assignFloat(op: String, value: Expression)(implicit context: Context): List[String] = {
    value match
        case FloatValue(value) => {
            val fvalue = (value * Settings.floatPrec).toInt
            op match{
                case "=" => List(f"scoreboard players set ${getSelector()} ${fvalue}")
                case "+=" => List(f"scoreboard players add ${getSelector()} ${fvalue}")
                case "-=" => List(f"scoreboard players remove ${getSelector()} ${fvalue}")
                case "*=" => {
                    context.requestConstant(fvalue)
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${getSelector()} *= ${fvalue} ${Settings.constScoreboard}",
                        f"scoreboard players operation ${getSelector()} /= ${Settings.floatPrec} ${Settings.constScoreboard}")
                }
                case "/=" => {
                    context.requestConstant(fvalue)
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${getSelector()} /= ${fvalue} ${Settings.constScoreboard}",
                        f"scoreboard players operation ${getSelector()} *= ${Settings.floatPrec} ${Settings.constScoreboard}")
                }
                case "%=" => {
                    context.requestConstant(fvalue)
                    List(f"scoreboard players operation ${getSelector()} %%= ${fvalue} ${Settings.constScoreboard}")
                }
                case ":=" => List(f"${checkNull()} scoreboard players set ${getSelector()} ${fvalue}")
            }
        }
        case IntValue(value) => {
            val fvalue = (value * Settings.floatPrec).toInt
            op match{
                case "=" => List(f"scoreboard players set ${getSelector()} ${fvalue}")
                case "+=" => List(f"scoreboard players add ${getSelector()} ${fvalue}")
                case "-=" => List(f"scoreboard players remove ${getSelector()} ${fvalue}")
                case "*=" |"/="|"%=" => {
                    context.requestConstant(fvalue)
                    List(f"scoreboard players operation ${getSelector()} $op ${value} ${Settings.constScoreboard}")
                }
                case ":=" => List(f"${checkNull()} scoreboard players set ${getSelector()} ${fvalue}")
            }
        }
        case BoolValue(value) => assignFloat(op, IntValue(if value then 1 else 0))
        case VariableValue(name) => assignFloat(op, LinkedVariableValue(context.getVariable(name)))
        case LinkedVariableValue(vari) => assignFloatLinkedVariable(op, vari)
        case FunctionCallValue(name, args) => context.getFunction(name, args).call(this)
        case bin: BinaryOperation => assignBinaryOperator(op, bin)
        case _ => throw new Exception(f"Unknown cast to float $value")
  }

  /**
   * Assign a value to the float variable
   */
  def assignBool(op: String, value: Expression)(implicit context: Context): List[String] = {
    value match
        case BoolValue(value) => 
            op match{
                case "=" => List(f"scoreboard players set ${getSelector()} ${if value then 1 else 0}")
                case "|=" => if value then List(f"scoreboard players set ${getSelector()} 1") else List()
                case "&=" => if value then List() else List(f"scoreboard players set ${getSelector()} 0")
                case ":=" => List(f"${checkNull()} scoreboard players set ${getSelector()} ${if value then 1 else 0}")
            }
        case VariableValue(name) => assignBool(op, LinkedVariableValue(context.getVariable(name)))
        case LinkedVariableValue(vari) => 
            op match
                case "&=" => List(f"scoreboard players operation ${getSelector()} *= ${vari.getSelector()}")
                case "|=" => List(f"scoreboard players operation ${getSelector()} += ${vari.getSelector()}")
                case ":=" => List(f"${checkNull()} scoreboard players operation ${getSelector()} $op  ${vari.getSelector()}")
                case _ => List(f"scoreboard players operation ${getSelector()} $op ${value} ${vari.getSelector()}")
        case FunctionCallValue(name, args) => context.getFunction(name, args).call(this)
        case bin: BinaryOperation => assignBinaryOperator(op, bin)
        case _ => throw new Exception(f"Unknown cast to bool $value")
  }

  def assignFloatLinkedVariable(op: String, vari: Variable)(implicit context: Context)={
    vari.getType() match{
        case FloatType => {
            op match{
                case "=" | "+=" | "-=" => {
                    List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()}")
                }
                case "*=" => {
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${getSelector()} *= ${vari.getSelector()}",
                        f"scoreboard players operation ${getSelector()} /= ${Settings.floatPrec} ${Settings.constScoreboard}")
                }
                case "/=" => {
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${getSelector()} /= ${vari.getSelector()}",
                        f"scoreboard players operation ${getSelector()} *= ${Settings.floatPrec} ${Settings.constScoreboard}")
                }
                case "%=" => {
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${getSelector()} %%= ${vari.getSelector()}")
                }

                case ":=" => List(f"${checkNull()} scoreboard players operation ${getSelector()} = ${vari.getSelector()}")
            }
        }
        case IntType => {
            op match{
                case "=" => {
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()}",
                            f"scoreboard players operation ${getSelector()} *= ${Settings.floatPrec}")
                }
                case "*=" | "/=" => {
                    List(f"scoreboard players operation ${getSelector()} $op ${vari.getSelector()}")
                }
                case other => {
                    val tmp = context.getFreshId()
                    context.requestConstant(Settings.floatPrec)
                    List(f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} = ${vari.getSelector()}",
                            f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} *= ${Settings.floatPrec}",
                            f"scoreboard players operation ${getSelector()} ${op} ${tmp} ${Settings.tmpScoreboard}"
                    )
                }
            }
        }
        case _ => {
            op match{
                case ":=" => List(f"${checkNull()} scoreboard players operation ${getSelector()} = ${vari.getSelector()}")
                case other => {
                    List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()}")
                }
            }
        }
    }
  }

  def assignTuple(op: String, expr: Expression)(implicit context: Context)={
    expr match
        case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
        case v => tupleVari.flatMap(t => t.assign(op, v))
  }


  def assignFunc(op: String, expr: Expression)(implicit context: Context):List[String]={
    op match{
        case "=" => {}
        case ":=" => { return List(f"${checkNull()} ${assignFunc("=", expr).head}")}
        case _ => throw new Exception(f"Illegal operation with ${name}: $op")
    }
    expr match
        case VariableValue(name) => {
            val vari = context.tryGetVariable(name)
            vari match
                case Some(value) => List(f"scoreboard players operation ${getSelector()} = ${value.getSelector()}")
                case None =>{
                    val typ = getType().asInstanceOf[FuncType]
                    val fct = context.getFunction(name, typ.sources, true).asInstanceOf[ConcreteFunction]
                    context.addFunctionToMux(typ.sources, typ.output, fct)
                    List(f"scoreboard players set ${getSelector()} ${fct.getMuxID()}")
            }
        }
        case IntValue(0) => List(f"scoreboard players set ${getSelector()} 0")
        case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
        case v => tupleVari.flatMap(t => t.assign(op, v))
  }



  def isPresentIn(expr: Expression)(implicit context: Context): Boolean = {
    expr match
        case IntValue(value) => false
        case FloatValue(value) => false
        case BoolValue(value) => false
        case JsonValue(content) => false
        case VariableValue(name1) => context.tryGetVariable(name1) == Some(this)
        case LinkedVariableValue(vari) => vari == this
        case BinaryOperation(op, left, right) => isPresentIn(left) || isPresentIn(right)
        case TupleValue(values) => values.exists(isPresentIn(_))
        case FunctionCallValue(name, args) => args.exists(isPresentIn(_))
        case RangeValue(min, max) => isPresentIn(min) || isPresentIn(max)
  }

  def getSelector(): String = {
    if (modifiers.isEntity){
        f"@s ${fullName}"
    }
    else{
        f"${fullName} ${Settings.variableScoreboard}"
    }
  }
}