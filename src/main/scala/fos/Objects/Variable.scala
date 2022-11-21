package objects

import types.*
import fos.*
import fos.Compilation.Execute

private val entityTypeSubVariable = List((BoolType, "isPlayer"))

class Variable(context: Context, name: String, typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
	var tupleVari: List[Variable] = List()
	val tagName = fullName
	val scoreboard = if modifiers.isEntity then "s"+context.getScoreboardID(this) else ""
	var lazyValue: Expression = DefaultValue

	def generate()(implicit context: Context):Unit = {
		typ match
			case StructType(struct) => {
				fos.Compiler.compile(struct.block)(context.push(name))
			}
			case TupleType(sub) => {
				val ctx = context.push(name)
				tupleVari = sub.zipWithIndex.map((t, i) => ctx.addVariable(new Variable(ctx, f"$i", t, _modifier)))
				tupleVari.map(_.generate()(ctx))
			}
			case EntityType => {
				val ctx = context.push(name)
				tupleVari = entityTypeSubVariable.map((t, n) => ctx.addVariable(new Variable(ctx, n, t, _modifier)))
				tupleVari.map(_.generate()(ctx))
			}
			case _ => {
			}
	}
	def assign(op: String, value: Expression)(implicit context: Context): List[String] = {
		if (modifiers.isLazy){
			op match{
				case "=" => lazyValue = Utils.simplify(value)
				case "+=" => lazyValue = Utils.simplify(BinaryOperation("+", LinkedVariableValue(this), value))
				case "-=" => lazyValue = Utils.simplify(BinaryOperation("-", LinkedVariableValue(this), value))
				case "/=" => lazyValue = Utils.simplify(BinaryOperation("/", LinkedVariableValue(this), value))
				case "*=" => lazyValue = Utils.simplify(BinaryOperation("*", LinkedVariableValue(this), value))
				case "%=" => lazyValue = Utils.simplify(BinaryOperation("%", LinkedVariableValue(this), value))
				case "&=" => lazyValue = Utils.simplify(BinaryOperation("&", LinkedVariableValue(this), value))
				case "|=" => lazyValue = Utils.simplify(BinaryOperation("|", LinkedVariableValue(this), value))
				case "^=" => lazyValue = Utils.simplify(BinaryOperation("^", LinkedVariableValue(this), value))
			}
			List()
		}
		else{
			op match{
				case ":=" => defaultAssign(value)
				case _ => {
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
									case JsonType => assignJson(op, value)									
									case FuncType(source, out) => assignFunc(op, value)
									case EntityType => assignEntity(op, value)
									case EnumType(enm) => assignEnum(op, value)
							}
						}
					}
				}
			}
		}
	}

	def checkNull() = 
		getType() match
			case EntityType => f" unless entity @e[tag=$tagName]"
			case _ => f" unless score ${getSelector()} = ${getSelector()}"
	

	def defaultAssign(expr: Expression)(implicit context: Context) = {
		Execute.makeExecute(checkNull(), assign("=", expr))
	}


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
				}
			}
			case DefaultValue => List(f"scoreboard players set ${getSelector()} 0")
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
			case _ => List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()}")
		}
	}


	/**
	 * Assign a value to the enum variable
	 */
	def assignEnum(op: String, value: Expression)(implicit context: Context): List[String] = {
		value match
			case VariableValue(name) => {
				val a = getType().asInstanceOf[EnumType].enm.values.indexWhere(_.name == name.toString())
				if (a >= 0){
					if (op != "=") throw new Exception(f"Illegal operation: $op on enum")
					assignInt(op, IntValue(a))
				}
				else{
					assignInt(op, LinkedVariableValue(context.getVariable(name)))
				}
			}
			case _ => assignInt(op, value)
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
				}
			}
			case DefaultValue => List(f"scoreboard players set ${getSelector()} 0")
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
				}
			case DefaultValue => List(f"scoreboard players set 0")
			case VariableValue(name) => assignBool(op, LinkedVariableValue(context.getVariable(name)))
			case LinkedVariableValue(vari) => 
				op match
					case "&=" => List(f"scoreboard players operation ${getSelector()} *= ${vari.getSelector()}")
					case "|=" => List(f"scoreboard players operation ${getSelector()} += ${vari.getSelector()}")
					case _ => List(f"scoreboard players operation ${getSelector()} $op ${vari.getSelector()}")
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
			case _ => List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()}")
		}
	}

	def assignTuple(op: String, expr: Expression)(implicit context: Context)={
		expr match
			case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
			case v => tupleVari.flatMap(t => t.assign(op, v))
	}


	def assignFunc(op: String, expr: Expression)(implicit context: Context):List[String]={
		if (op != "=") throw new Exception(f"Illegal operation with ${name}: $op")
		
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
			case DefaultValue => List(f"scoreboard players set ${getSelector()} 0")
			case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
			case v => tupleVari.flatMap(t => t.assign(op, v))
	}
	def removeEntityTag()(implicit context: Context)={
		Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
						CMD(f"tag @a[tag=${tagName}] remove $tagName"), 
						List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${tagName}] remove $tagName")))))
	}
	def assignEntity(op: String, expr: Expression)(implicit context: Context):List[String]={
		op match{
			case "=" => {
				expr match
					case VariableValue(name) => {
						val vari = context.getVariable(name)

						// Remove tag to previous entities
						removeEntityTag():::
						// copy fields
						tupleVari.zip(vari.tupleVari).flatMap((t, v) => t.assign(op, LinkedVariableValue(v))) :::
						// Add tag to new entities
						Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
											CMD(f"tag @a[tag=${vari.tagName}] add $tagName"), 
											List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${vari.tagName}] add $tagName")))))
					}
					case DefaultValue => List()
					case SelectorValue(value) => {
						// Remove tag to previous entities
						removeEntityTag():::
						// copy fields
						tupleVari.zip(List(BoolValue(value.isPlayer))).flatMap((t, v) => t.assign(op, v)) ::: 
						// Add tag to new entities
						List(f"tag ${value.getString()} add $tagName")
					}
					case bin: BinaryOperation => assignBinaryOperator(op, bin)
					case _ => throw new Exception(f"No cast from ${expr} to entity")
			}
			case "+=" | "&=" => {
				expr match
					case VariableValue(name) => {
						val vari = context.getVariable(name)

						// copy fields
						tupleVari.zip(vari.tupleVari).flatMap((t, v) => t.assign(op, LinkedVariableValue(v))) :::
						// Add tag to new entities
						Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
											CMD(f"tag @a[tag=${vari.tagName}] add $tagName"), 
											List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${vari.tagName}] add $tagName")))))
					}
					case IntValue(0) => List()
					case SelectorValue(value) => {
						// Remove copy fields
						tupleVari.zip(List(BoolValue(value.isPlayer))).flatMap((t, v) => t.assign(op, v)) ::: 
						// Add tag to new entities
						List(f"tag ${value.getString()} add $tagName")
					}
					case bin: BinaryOperation => assignBinaryOperator(op, bin)
					case _ => throw new Exception(f"No cast from ${expr} to entity")
			}
			case "-=" | "/=" => {
				expr match
					case VariableValue(name) => {
						val vari = context.getVariable(name)

						// Add tag to new entities
						Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
											CMD(f"tag @a[tag=${vari.tagName}] remove $tagName"), 
											List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${vari.tagName}] remove $tagName")))))
					}
					case IntValue(0) => List()
					case SelectorValue(value) => {
						// Add tag to new entities
						List(f"tag ${value.getString()} remove $tagName")
					}
					case bin: BinaryOperation => assignBinaryOperator(op, bin)
					case _ => throw new Exception(f"No cast from ${expr} to entity")
			}
			case _ => throw new Exception(f"Illegal operation with ${name}: $op")
		}
	}

	/**
	 * Assign a value to the float variable
	 */
	def assignJson(op: String, value: Expression)(implicit context: Context): List[String] = {
		if (Settings.target == MCBedrock){
			throw new Exception("Dynamic Json Variable Not Supported in Bedrock")
		}
		if (modifiers.isEntity){
			throw new Exception("Not Supported")
		}
		else{
			value match
				case JsonValue(value) => 
					op match{
						case "=" => List(f"data modify storage ${fullName} json set value ${value.getNbt()}")
						case "+=" => List(f"data modify storage ${fullName} json append value ${value.getNbt()}")
						case "&=" => List(f"data modify storage ${fullName} json merge value ${value.getNbt()}")
					}
				case DefaultValue => List(f"scoreboard players set 0")
				case VariableValue(name) => assignBool(op, LinkedVariableValue(context.getVariable(name)))
				case LinkedVariableValue(vari) => 
					vari.getType() match{
						case JsonType => {
							op match{
								case "=" => List(f"data modify storage ${fullName} json set from storage ${vari.fullName} json")
								case "+=" => List(f"data modify storage ${fullName} json append from storage ${vari.fullName} json")
								case "&=" => List(f"data modify storage ${fullName} json merge from storage ${vari.fullName} json")
							}
						}
						case IntType => {
							op match{
								case "=" => List(f"execute store result storage ${fullName} json int 1 run scoreboard players get ${vari.getSelector()}")
								case "+=" => List(f"execute store result storage ${fullName} tmp int 1 run scoreboard players get ${vari.getSelector()}", 
												f"data modify storage ${fullName} json append from storage ${fullName} tmp")
								case "&=" => List(f"execute store result storage ${fullName} tmp int 1 run scoreboard players get ${vari.getSelector()}", 
												f"data modify storage ${fullName} json merge from storage ${fullName} tmp")
							}
						}
						case FloatType => {
							op match{
								case "=" => List(f"execute store result storage ${fullName} json float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()}")
								case "+=" => List(f"execute store result storage ${fullName} tmp float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()}", 
												f"data modify storage ${fullName} json append from storage ${fullName} tmp")
								case "&=" => List(f"execute store result storage ${fullName} tmp float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()}", 
												f"data modify storage ${fullName} json merge from storage ${fullName} tmp")
							}
						}
					}
				case FunctionCallValue(name, args) => context.getFunction(name, args).call(this)
				case bin: BinaryOperation => assignBinaryOperator(op, bin)
				case _ => throw new Exception(f"Unknown cast to json $value")
		}
	}



	def isPresentIn(expr: Expression)(implicit context: Context): Boolean = {
		expr match
			case IntValue(value) => false
			case FloatValue(value) => false
			case BoolValue(value) => false
			case JsonValue(content) => false
			case SelectorValue(value) => false
			case StringValue(value) => false
			case DefaultValue => false
			case VariableValue(name1) => context.tryGetVariable(name1) == Some(this)
			case LinkedVariableValue(vari) => vari == this
			case BinaryOperation(op, left, right) => isPresentIn(left) || isPresentIn(right)
			case TupleValue(values) => values.exists(isPresentIn(_))
			case FunctionCallValue(name, args) => args.exists(isPresentIn(_))
			case RangeValue(min, max) => isPresentIn(min) || isPresentIn(max)
		}

	def getSelector(): String = {
		if (modifiers.isEntity){
			f"@s ${scoreboard}"
		}
		else{
			f"${fullName} ${Settings.variableScoreboard}"
		}
	}
}