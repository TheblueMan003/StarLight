package objects

import types.*
import sl.*
import sl.Compilation.Execute
import sl.Compilation.Print
import sl.Compilation.Selector.{Selector, JavaSelector, SelectorIdentifier}
import sl.Compilation.Array

private val entityTypeSubVariable = List((BoolType, "isPlayer"))
object Variable {
	extension (value: Either[Identifier, Variable]) {
		def get()(implicit context: Context):Variable = {
			value match{
				case Left(value) => {
					context.tryGetProperty(value) match
						case Some(Property(name, getter, setter, variable)) => PropertySetVariable(context, getter, setter, variable)
						case None => context.getVariable(value)
				}
				case Right(value) => value
			}
		}
		def path()(implicit context: Context):String = {
			value match{
				case Left(value) => value.toString()
				case Right(value) => value.fullName
			}
		}
	}
}
class Variable(context: Context, name: String, typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
	var tupleVari: List[Variable] = List()
	val tagName = fullName
	var wasAssigned = false
	lazy val scoreboard = if modifiers.isEntity then modifiers.getAttributesString("name", ()=>"s"+context.getScoreboardID(this))(context) else ""
	lazy val inGameName = modifiers.getAttributesString("name", ()=>fullName)(context)
	lazy val criterion = modifiers.getAttributesString("criterion", ()=>"dummy")(context)
	var lazyValue: Expression = DefaultValue
	var isFunctionArgument = false

	var getter: Function = null
	var setter: Function = null

	def generate(isStructFunctionArgument: Boolean = false)(implicit context: Context):Unit = {
		val parent = context.getCurrentVariable()
		if (parent != null){
			if (!isFunctionArgument){
				parent.tupleVari = parent.tupleVari ::: List(this)
			}
			isFunctionArgument |= parent.isFunctionArgument
		}
		if (!context.getCurrentFunction().isInstanceOf[CompilerFunction]){
			//typ.generateCompilerFunction(this)(context.push(name))
		}

		typ match
			case StructType(struct) => {
				val ctx = context.push(name, this)
				ctx.inherit(struct.context)
				ctx.push("this", ctx)
				val block = Utils.subst(struct.getBlock(), "$this", fullName)
				if (isStructFunctionArgument && context.getCurrentVariable().getType() == getType()){
					sl.Compiler.compile(Utils.rmFunctions(block))(ctx)
				}
				else{
					sl.Compiler.compile(block)(ctx)
				}
				tupleVari.foreach(vari => vari.modifiers = vari.modifiers.combine(modifiers))
			}
			case ClassType(clazz) => {
				clazz.generateForVariable(this)
			}
			case ArrayType(inner, size) => {
				size match
					case IntValue(size) => {
						val ctx = context.push(name)
						tupleVari = Range(0, size).map(i => ctx.addVariable(new Variable(ctx, f"$i", inner, _modifier))).toList
						tupleVari.map(_.generate()(ctx))

						Array.generate(this)
					}
					case other => throw new Exception(f"Cannot have a array with size: $other")
			}
			case TupleType(sub) => {
				val ctx = context.push(name)
				tupleVari = sub.zipWithIndex.map((t, i) => ctx.addVariable(new Variable(ctx, f"_$i", t, _modifier)))
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



	def assign(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
		if (modifiers.isConst && wasAssigned) throw new Exception(f"Cannot reassign variable $fullName")
		wasAssigned = true
		if (modifiers.isLazy){
			getType() match{
				case StructType(struct) => {
					value match
						case VariableValue(name, sel) => {
							val vari = context.getVariable(name)
							if (vari.getType() == StructType(struct)){
								tupleVari.zip(vari.tupleVari).map((a,v) => a.assign(op, LinkedVariableValue(v, sel)))
							}
							else{
								throw new Exception(f"Lazy assignment of $vari not supported for struct")
							}
						}
						case LinkedVariableValue(vari, sel) if vari.getType() == StructType(struct) => {
							tupleVari.zip(vari.tupleVari).map((a,v) => a.assign(op, LinkedVariableValue(v, sel)))
						}
						case a => throw new Exception(f"Lazy assignment of $a not supported for struct")
				}
				case RawJsonType => {
					Utils.typeof(value) match
						case TupleType(sub) => {
							Utils.simplify(value) match
								case TupleValue(lst) =>
									val value2 = Print.toRawJson(lst)
									op match{
										case "=" => lazyValue = value2._2
										case "+=" => lazyValue = Utils.simplify(BinaryOperation("+", lazyValue, value2._2))
										case other => throw new Exception(f"Unsupported operation: $fullName $op $value")
									}
									return value2._1
								case other => throw new Exception(f"Unsupported operation: $fullName $op $value")
						}
						case RawJsonType => {
							op match{
								case "=" => lazyValue = value
								case "+=" => lazyValue = Utils.simplify(BinaryOperation("+", lazyValue, value))
								case other => throw new Exception(f"Unsupported operation: $fullName $op $value")
							}
						}
						case other => {
							val value2 = Print.toRawJson(List(value))
							op match{
								case "=" => lazyValue = value2._2
								case "+=" => lazyValue = Utils.simplify(BinaryOperation("+", lazyValue, value2._2))
								case other => throw new Exception(f"Unsupported operation: $fullName $op $value")
							}
							return value2._1
						}
				}
				case other => {
					value match{
						case FunctionCallValue(VariableValue(name, sel), args, _) => 
							try{
								val res = context.getFunction(name, args, getType(), false)
								if (res._1.canBeCallAtCompileTime){
									return res.call(this, op)
								}
							}
							catch{
								case ObjectNotFoundException(e) =>
									lazyValue = value
									if (!e.contains("ret_"))
									throw ObjectNotFoundException(e)
									List()
							}
						case _ => {
						}
					}

					val fixed = Utils.fix(value)(context, Set())

					op match{
						case "=" => lazyValue = Utils.simplify(fixed)
						case "+=" => lazyValue = Utils.simplify(BinaryOperation("+", LinkedVariableValue(this), fixed))
						case "-=" => lazyValue = Utils.simplify(BinaryOperation("-", LinkedVariableValue(this), fixed))
						case "/=" => lazyValue = Utils.simplify(BinaryOperation("/", LinkedVariableValue(this), fixed))
						case "*=" => lazyValue = Utils.simplify(BinaryOperation("*", LinkedVariableValue(this), fixed))
						case "%=" => lazyValue = Utils.simplify(BinaryOperation("%", LinkedVariableValue(this), fixed))
						case "&=" => lazyValue = Utils.simplify(BinaryOperation("&", LinkedVariableValue(this), fixed))
						case "|=" => lazyValue = Utils.simplify(BinaryOperation("|", LinkedVariableValue(this), fixed))
						case "^=" => lazyValue = Utils.simplify(BinaryOperation("^", LinkedVariableValue(this), fixed))
						case ":=" => {}
						case other => throw new Exception(f"Unsupported operation: $fullName $op $value")
					}
				}
			}
			List()
		}
		else{
			op match{
				case ":=" => defaultAssign(value)
				case _ => {
					value match{
						case VariableValue(nm, sel) if op == "=" && context.tryGetVariable(nm) == Some(this) && sel == selector =>{
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
									case StructType(struct) => assignStruct(op, value)
									case ClassType(clazz) => assignClass(op, value)
									case ArrayType(innter, size) => assignArray(op, value)
									case EntityType => assignEntity(op, value)
									case EnumType(enm) => assignEnum(op, value)
									case other => throw new Exception(f"Cannot Assign to $fullName of type $other")
							}
						}
					}
				}
			}
		}
	}

	def checkNull()(implicit selector: Selector = Selector.self) = 
		getType() match
			case EntityType => f" unless entity @e[tag=$tagName]"
			case _ => f" unless score ${getSelector()} = ${getSelector()}"
	

	def defaultAssign(expr: Expression)(implicit context: Context, selector: Selector = Selector.self) = {
		if (Settings.target == MCBedrock){
			List()
		}
		else if (Settings.target == MCJava){
			Execute.makeExecute(checkNull(), assign("=", expr))
		}
		else{
			???
		}
	}


	/**
	 * Assign the value to a tmp variable then apply op with the variable.
	 */
	def assignTmp(op: String, expr: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
		val vari = context.getFreshVariable(getType())
		vari.assign("=", expr) ::: assign(op, LinkedVariableValue(vari))
	}


	/**
	 * Assign binary operator to the variable.
	 */
	def assignBinaryOperator(op: String, value: BinaryOperation)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
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
	def assignInt(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
		value match
			case IntValue(value) => {
				op match{
					case "=" => List(f"scoreboard players set ${getSelector()} ${value}")
					case "+=" => List(f"scoreboard players add ${getSelector()} ${value}")
					case "-=" => List(f"scoreboard players remove ${getSelector()} ${value}")
					case "*=" | "/=" | "%=" => {
						context.requestConstant(value)
						List(f"scoreboard players operation ${getSelector()} ${op} c${value} ${Settings.constScoreboard}")
					}
				}
			}
			case EnumIntValue(value) => assignInt(op, IntValue(value))
			case DefaultValue => List(f"scoreboard players set ${getSelector()} 0")
			case BoolValue(value) => assignInt(op, IntValue(if value then 1 else 0))
			case FloatValue(value) => assignInt(op, IntValue(value.toInt))
			case VariableValue(name, sel) => assignInt(op, context.resolveVariable(value))
			case LinkedVariableValue(vari, sel) => assignIntLinkedVariable(op, vari, sel)
			case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case _ => throw new Exception(f"Unknown cast to int $value")
	}

	def handleFunctionCall(op: String, name: Expression, args: List[Expression], sel: Selector)(implicit context: Context, selector: Selector = Selector.self):List[String] = {
		if (sel != Selector.self && modifiers.isEntity){
			val (pref,vari) = Utils.simplifyToVariable(FunctionCallValue(name, args, sel))
			pref ::: assign(op, vari)
		}
		else if (sel != Selector.self){
			Compiler.compile(With(SelectorValue(sel), BoolValue(true), BoolValue(true), VariableAssigment(List((Right(this),selector)), op, FunctionCallValue(name, args, Selector.self))))
		}
		else{
			name match
				case VariableValue(iden, sel) => context.getFunction(iden, args, getType()).call(this, op)
				case LinkedFunctionValue(fct) => (fct, args).call(this, op)
				case other =>{
					val (t, v) = Utils.simplifyToVariable(other)
					v.vari.getType() match
						case FuncType(sources, output) =>
							t ::: (context.getFunctionMux(sources, output)(context), v::args).call(this, op)
						case other => throw new Exception(f"Cannot call $other")
				}
		}
	}

	def handleArrayGetValue(op: String, name2: Expression, index: List[Expression])(implicit context: Context):List[String] = {
		val (prev,name) = Utils.simplifyToVariable(name2)
		prev ::: (
		name match{
			case LinkedVariableValue(vari, sel) => {
				vari.getType() match
					case ArrayType(sub, IntValue(size)) => {
						index.map(Utils.simplify(_)) match
							case IntValue(i)::Nil => {
								if (i >= size || i < 0) then throw new Exception(f"Index out of Bound for array $name: $i not in 0..$size")
								assign(op, LinkedVariableValue(vari.tupleVari(i)))
							}
							case _ => assign(op, FunctionCallValue(VariableValue(vari.fullName+".get"), index))
					}
					case other => assign(op, FunctionCallValue(VariableValue(vari.fullName+".__get__"), index))
			}
		}
		)
	}

	def assignIntLinkedVariable(op: String, vari: Variable, oselector: Selector)(implicit context: Context, selector: Selector = Selector.self) = {
		vari.getType() match{
			case FloatType => {
				op match{
					case "=" => {
						context.requestConstant(Settings.floatPrec)
						List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(oselector)}",
								f"scoreboard players operation ${getSelector()} /= c${Settings.floatPrec} ${Settings.constScoreboard}")
					}
					case other => {
						val tmp = context.getFreshId()
						context.requestConstant(Settings.floatPrec)
						List(f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} ${op} ${vari.getSelector()(oselector)}",
								f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} /= c${Settings.floatPrec} ${Settings.constScoreboard}",
								f"scoreboard players operation ${getSelector()} = ${tmp} ${Settings.tmpScoreboard}"
						)
					}
				}
			}
			case other if other.isSubtypeOf(getType()) => List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(oselector)}")
			case FuncType(sources, output) => List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(oselector)}")
			case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
		}
	}


	/**
	 * Assign a value to the enum variable
	 */
	def assignEnum(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
		value match
			case VariableValue(name, sel) => {
				val a = getType().asInstanceOf[EnumType].enm.values.indexWhere(_.name == name.toString())
				if (a >= 0 && sel == Selector.self){
					if (op != "=") throw new Exception(f"Illegal operation: $op on enum")
					assignInt(op, IntValue(a))
				}
				else{
					assignInt(op, context.resolveVariable(value))
				}
			}
			case EnumIntValue(value) => assignInt(op, IntValue(value))
			case _ => assignInt(op, value)
	}





	/**
	 * Assign a value to the float variable
	 */
	def assignFloat(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
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
						List(f"scoreboard players operation ${getSelector()} *= c${fvalue} ${Settings.constScoreboard}",
							f"scoreboard players operation ${getSelector()} /= c${Settings.floatPrec} ${Settings.constScoreboard}")
					}
					case "/=" => {
						context.requestConstant(fvalue)
						context.requestConstant(Settings.floatPrec)
						List(f"scoreboard players operation ${getSelector()} /= c${fvalue} ${Settings.constScoreboard}",
							f"scoreboard players operation ${getSelector()} *= c${Settings.floatPrec} ${Settings.constScoreboard}")
					}
					case "%=" => {
						context.requestConstant(fvalue)
						List(f"scoreboard players operation ${getSelector()} %%= c${fvalue} ${Settings.constScoreboard}")
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
			case VariableValue(name, sel) => assignFloat(op, context.resolveVariable(value))
			case LinkedVariableValue(vari, sel) => assignFloatLinkedVariable(op, vari, sel)
			case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case _ => throw new Exception(f"Unknown cast to float $value")
	}

	/**
	 * Assign a value to the float variable
	 */
	def assignBool(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
		value match
			case BoolValue(value) => 
				op match{
					case "=" => List(f"scoreboard players set ${getSelector()} ${if value then 1 else 0}")
					case "|=" => if value then List(f"scoreboard players set ${getSelector()} 1") else List()
					case "||=" => if value then List(f"scoreboard players set ${getSelector()} 1") else List()
					case "&=" => if value then List() else List(f"scoreboard players set ${getSelector()} 0")
					case "&&=" => if value then List() else List(f"scoreboard players set ${getSelector()} 0")
				}
			case DefaultValue => List(f"scoreboard players set 0")
			case VariableValue(name, sel) => assignBool(op, context.resolveVariable(value))
			case LinkedVariableValue(vari, sel) => 
				vari.getType() match
					case a if a.isSubtypeOf(BoolType) => {
						op match
							case "&=" => List(f"scoreboard players operation ${getSelector()} *= ${vari.getSelector()(sel)}")
							case "&&=" => List(f"scoreboard players operation ${getSelector()} *= ${vari.getSelector()(sel)}")
							case "|=" => List(f"scoreboard players operation ${getSelector()} += ${vari.getSelector()(sel)}")
							case "||=" => List(f"scoreboard players operation ${getSelector()} += ${vari.getSelector()(sel)}")
							case _ => List(f"scoreboard players operation ${getSelector()} $op ${vari.getSelector()(sel)}")
					}
					case EntityType => f"scoreboard players set ${getSelector()} 0"::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
					case other => throw new Exception(f"Cannot assign $value of type $other to $fullName of type ${getType()}")
			case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case SelectorValue(sel) => f"scoreboard players set ${getSelector()} 0"::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
			case _ => throw new Exception(f"Unknown cast to bool $value")
	}

	def assignFloatLinkedVariable(op: String, vari: Variable, sel: Selector)(implicit context: Context, selector: Selector = Selector.self)={
		vari.getType() match{
			case FloatType => {
				op match{
					case "=" | "+=" | "-=" => {
						List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(sel)}")
					}
					case "*=" => {
						context.requestConstant(Settings.floatPrec)
						List(f"scoreboard players operation ${getSelector()} *= ${vari.getSelector()(sel)}",
							f"scoreboard players operation ${getSelector()} /= c${Settings.floatPrec} ${Settings.constScoreboard}")
					}
					case "/=" => {
						context.requestConstant(Settings.floatPrec)
						List(f"scoreboard players operation ${getSelector()} /= ${vari.getSelector()(sel)}",
							f"scoreboard players operation ${getSelector()} *= c${Settings.floatPrec} ${Settings.constScoreboard}")
					}
					case "%=" => {
						List(f"scoreboard players operation ${getSelector()} %%= ${vari.getSelector()(sel)}")
					}
				}
			}
			case IntType => {
				op match{
					case "=" => {
						context.requestConstant(Settings.floatPrec)
						List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(sel)}",
								f"scoreboard players operation ${getSelector()} *= c${Settings.floatPrec} ${Settings.constScoreboard}")
					}
					case "*=" | "/=" => {
						List(f"scoreboard players operation ${getSelector()} $op ${vari.getSelector()(sel)}")
					}
					case other => {
						val tmp = context.getFreshId()
						context.requestConstant(Settings.floatPrec)
						List(f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} = ${vari.getSelector()(sel)}",
								f"scoreboard players operation ${tmp} ${Settings.tmpScoreboard} *= c${Settings.floatPrec} ${Settings.constScoreboard}",
								f"scoreboard players operation ${getSelector()} ${op} ${tmp} ${Settings.tmpScoreboard}"
						)
					}
				}
			}
			case a if a.isSubtypeOf(getType()) => List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(sel)}")
			case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
		}
	}

	def assignTuple(op: String, expr2: Expression)(implicit context: Context, selector: Selector = Selector.self)={
		val expr = Utils.simplify(expr2)
		expr match
			case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
			case LinkedVariableValue(vari, sel) => {
				if (vari.getType().isSubtypeOf(getType())){
					tupleVari.zip(vari.tupleVari).flatMap((t,v) => t.assign(op, LinkedVariableValue(v, sel)))
				}
				else{
					tupleVari.flatMap(t => t.assign(op, expr))
				}
			}
			case VariableValue(vari, sel) => assign(op, context.resolveVariable(expr))
			case FunctionCallValue(fct, args, selector) => handleFunctionCall(op, fct, args, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case v => tupleVari.flatMap(t => t.assign(op, v))
	}

	def assignArray(op: String, expr2: Expression)(implicit context: Context, selector: Selector = Selector.self)={
		val ArrayType(sub, IntValue(size)) = getType().asInstanceOf[ArrayType]: @unchecked
		val expr = Utils.simplify(expr2)
		expr match
			case TupleValue(value) if size == value.size => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
			case LinkedVariableValue(vari, sel) => {
				if (vari.getType().isSubtypeOf(getType())){
					tupleVari.zip(vari.tupleVari).flatMap((t,v) => t.assign(op, LinkedVariableValue(v, sel)))
				}
				else{
					tupleVari.flatMap(t => t.assign(op, expr))
				}
			}
			case VariableValue(vari, sel) => assign(op, context.resolveVariable(expr))
			case FunctionCallValue(fct, args, selector) => handleFunctionCall(op, fct, args, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case v => tupleVari.flatMap(t => t.assign(op, v))
	}


	def assignFunc(op: String, expr: Expression)(implicit context: Context, selector: Selector = Selector.self):List[String]={
		if (op != "=") throw new Exception(f"Illegal operation with ${name}: $op")
		
		expr match
			case VariableValue(name, sel) => {
				val vari = context.tryGetVariable(name)
				vari match
					case Some(value) => List(f"scoreboard players operation ${getSelector()} = ${value.getSelector()(sel)}")
					case None =>{
						val typ = getType().asInstanceOf[FuncType]
						val fct = context.getFunction(name, typ.sources, typ.output, true).asInstanceOf[ConcreteFunction]
						fct.markAsUsed()
						context.addFunctionToMux(typ.sources, typ.output, fct)
						List(f"scoreboard players set ${getSelector()} ${fct.getMuxID()}")
				}
			}
			case LinkedVariableValue(vari, sel) => {
				vari.getType() match
					case other if other.isSubtypeOf(getType()) => List(f"scoreboard players operation ${getSelector()} = ${vari.getSelector()(sel)}")
					case IntType => List(f"scoreboard players operation ${getSelector()} = ${vari.getSelector()(sel)}")
					case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
			}
			case LambdaValue(args, instr) => {
				val typ = getType().asInstanceOf[FuncType]
				val fct = context.getFreshLambda(args, typ.sources, typ.output, instr).asInstanceOf[ConcreteFunction]
				fct.markAsUsed()
				context.addFunctionToMux(typ.sources, typ.output, fct)
				List(f"scoreboard players set ${getSelector()} ${fct.getMuxID()}")
			}
			case NullValue => List(f"scoreboard players set ${getSelector()} 0")
			case DefaultValue => List(f"scoreboard players set ${getSelector()} 0")
			case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
			case _ => throw new Exception("Unsupported Operation")
	}
	def removeEntityTag()(implicit context: Context)={
		Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
						CMD(f"tag @a[tag=${tagName}] remove $tagName"), 
						List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${tagName}] remove $tagName")))))
	}
	def assignEntity(op: String, expr: Expression)(implicit context: Context, selector: Selector = Selector.self):List[String]={
		op match{
			case "=" => {
				expr match
					case VariableValue(name, sel) => {
						val vari = context.getVariable(name)
						assignEntity(op, LinkedVariableValue(vari, sel))
					}
					case LinkedVariableValue(vari, sel) => {
						vari.getType() match
							case EntityType => {
								// Remove tag to previous entities
								removeEntityTag():::
								// copy fields
								tupleVari.zip(vari.tupleVari).flatMap((t, v) => t.assign(op, LinkedVariableValue(v))) :::
								// Add tag to new entities
								Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
													CMD(f"tag @a[tag=${vari.tagName}] add $tagName"), 
													List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${vari.tagName}] add $tagName")))))
							}
							case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
					}
					case NullValue => {
						val vari = context.getVariable(name)

						// Remove tag to previous entities
						removeEntityTag()
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
					case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
					case _ => throw new Exception(f"No cast from ${expr} to entity")
			}
			case "+=" | "&=" => {
				expr match
					case VariableValue(name, sel) => {
						val vari = context.getVariable(name)
						assignEntity(op, LinkedVariableValue(vari, sel))
					}
					case LinkedVariableValue(vari, sel) => {
						vari.getType() match
							case EntityType => {
								// copy fields
								tupleVari.zip(vari.tupleVari).flatMap((t, v) => t.assign(op, LinkedVariableValue(v, sel))) :::
								// Add tag to new entities
								Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
													CMD(f"tag @a[tag=${vari.tagName}] add $tagName"), 
													List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${vari.tagName}] add $tagName")))))
							}
							case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
					}
					case IntValue(0) => List()
					case SelectorValue(value) => {
						// Remove copy fields
						tupleVari.zip(List(BoolValue(value.isPlayer))).flatMap((t, v) => t.assign(op, v)) ::: 
						// Add tag to new entities
						List(f"tag ${value.getString()} add $tagName")
					}
					case NullValue => List()
					case bin: BinaryOperation => assignBinaryOperator(op, bin)
					case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
					case _ => throw new Exception(f"No cast from ${expr} to entity")
			}
			case "-=" | "/=" => {
				expr match
					case VariableValue(name, sel) => {
						val vari = context.getVariable(name)
						assignEntity(op, LinkedVariableValue(vari, sel))
					}
					case LinkedVariableValue(vari, sel) => {
						vari.getType() match
							case EntityType => {
								// Add tag to new entities
								Compiler.compile(If(LinkedVariableValue(tupleVari(0), sel), 
													CMD(f"tag @a[tag=${vari.tagName}] remove $tagName"), 
													List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${vari.tagName}] remove $tagName")))))
							}
							case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
					}
					case IntValue(0) => List()
					case SelectorValue(value) => {
						// Add tag to new entities
						List(f"tag ${value.getString()} remove $tagName")
					}
					case NullValue => List()
					case bin: BinaryOperation => assignBinaryOperator(op, bin)
					case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
					case _ => throw new Exception(f"No cast from ${expr} to entity")
			}
			case _ => throw new Exception(f"Illegal operation with ${name}: $op")
		}
	}

	/**
	 * Assign a value to the float variable
	 */
	def assignJson(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
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
				case DefaultValue => List(f"data modify storage ${fullName} json set value {}")
				case VariableValue(name, sel) => assignBool(op, context.resolveVariable(value))
				case LinkedVariableValue(vari, sel) => 
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
								case "=" => List(f"execute store result storage ${fullName} json int 1 run scoreboard players get ${vari.getSelector()(sel)}")
								case "+=" => List(f"execute store result storage ${fullName} tmp int 1 run scoreboard players get ${vari.getSelector()(sel)}", 
												f"data modify storage ${fullName} json append from storage ${fullName} tmp")
								case "&=" => List(f"execute store result storage ${fullName} tmp int 1 run scoreboard players get ${vari.getSelector()(sel)}", 
												f"data modify storage ${fullName} json merge from storage ${fullName} tmp")
							}
						}
						case FloatType => {
							op match{
								case "=" => List(f"execute store result storage ${fullName} json float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()(sel)}")
								case "+=" => List(f"execute store result storage ${fullName} tmp float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()(sel)}", 
												f"data modify storage ${fullName} json append from storage ${fullName} tmp")
								case "&=" => List(f"execute store result storage ${fullName} tmp float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()(sel)}", 
												f"data modify storage ${fullName} json merge from storage ${fullName} tmp")
							}
						}
						case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
					}
				case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
				case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
				case bin: BinaryOperation => assignBinaryOperator(op, bin)
				case _ => throw new Exception(f"Unknown cast to json $value")
		}
	}

	/**
	 * Assign a value to the struct variable
	 */
	def assignStruct(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
		if value == DefaultValue then return List()
		op match
			case "=" => {
				value match
					case LinkedVariableValue(vari, sel) => {
						if (vari.getType() == getType()){
							tupleVari.zip(vari.tupleVari).flatMap((a,v) => a.assign(op, LinkedVariableValue(v, sel)))
						}
						else{
							context.getFunction(this.name + ".__set__", List(value), getType(), false).call()
						}
					}
					case VariableValue(name, sel) => assignStruct(op, context.resolveVariable(value))
					case ConstructorCall(name2, args) => {
						context.getType(IdentifierType(name2.toString())) match
							case ClassType(clazz) => context.getFunction(this.name + ".__set__", List(value), getType(), false).call()
							case typ@StructType(struct) => {
								if (typ == getType()){
									context.getFunction(name + ".__init__", args, getType(), false).call()
								}
								else{
									val vari = context.getFreshVariable(typ)
									vari.assign("=", value)
									assign("=", LinkedVariableValue(vari))
								}
							}
							case other => throw new Exception(f"Cannot constructor call $other")
					}
					case _ => context.getFunction(name + ".__set__", List(value), getType(), false).call()
			}
			case op => context.getFunction(name + "." + Utils.getOpFunctionName(op),  List(value), getType(), false).call()
	}

	def deref()(implicit context: Context) = context.getFunction(this.name + ".__remRef", List[Expression](), getType(), false).call()
	def addref()(implicit context: Context)= context.getFunction(this.name + ".__addRef", List[Expression](), getType(), false).call()

	/**
	 * Assign a value to the struct variable
	 */
	def assignClass(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[String] = {
		if value == DefaultValue then return List()
		op match
			case "=" => {
				value match
					case LinkedVariableValue(vari, sel) if vari.getType() == getType() => {
						if (vari.getType() == getType()){
							deref() ::: List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(sel)}") ::: addref()
						}
						else{
							context.getFunction(this.name + ".__set__", List(value), getType(), false).call()
						}
					}
					case VariableValue(name, sel) => assignClass(op, context.resolveVariable(value))
					case ConstructorCall(name2, args) => {
						context.getType(IdentifierType(name2.toString())) match
							case StructType(struct) => throw new Exception("Cannot call struct constructor for class")
							case typ@ClassType(clazz) => {
								if (typ == getType()){
									val entity = clazz.getEntity()
									val initarg = List(StringValue(clazz.fullName))::: (if entity != null then List(entity) else List())
									deref()
									::: assign("=", FunctionCallValue(VariableValue("object.__initInstance"), initarg))
									::: context.getFunction(name + ".__init__", args, getType(), false).call()
								}
								else{
									val vari = context.getFreshVariable(typ)
									vari.assign("=", value)
									assign("=", LinkedVariableValue(vari))
								}
							}
							case other => throw new Exception(f"Cannot constructor call $other")
					}
					case NullValue => deref() ::: List(f"scoreboard players set ${getSelector()} 0")
					case DefaultValue => List(f"scoreboard players set ${getSelector()} 0")
					case LinkedVariableValue(vari, sel) if vari.name == "__totalRefCount" => {
						List(f"scoreboard players operation ${getSelector()} ${op} ${vari.getSelector()(sel)}")
					}
					case FunctionCallValue(name, args, selector) => handleFunctionCall(op, name, args, selector)
					case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
					case _ => context.getFunction(name + ".__set__", List(value), getType(), false).call()
			}
			case _ => assignStruct(op, value)
	}


	def isPresentIn(expr: Expression)(implicit context: Context, selector: Selector): Boolean = {
		expr match
			case IntValue(value) => false
			case FloatValue(value) => false
			case BoolValue(value) => false
			case JsonValue(content) => false
			case SelectorValue(value) => false
			case StringValue(value) => false
			case DefaultValue => false
			case NullValue => false
			case EnumIntValue(value) => false
			case NamespacedName(value) => false
			case LinkedFunctionValue(fct) => false
			case PositionValue(value) => false
			case ArrayGetValue(name, index) => isPresentIn(name)
			case TagValue(value) => false
			case LambdaValue(args, instr) => false
			case VariableValue(name1, sel) => context.tryGetVariable(name1) == Some(this) && sel == selector
			case LinkedVariableValue(vari, sel) => vari == this && sel == selector
			case RawJsonValue(value) => false
			case BinaryOperation(op, left, right) => isPresentIn(left) || isPresentIn(right)
			case UnaryOperation(op, left) => isPresentIn(left)
			case TupleValue(values) => values.exists(isPresentIn(_))
			case FunctionCallValue(name, args, selector) => args.exists(isPresentIn(_)) || isPresentIn(name)
			case ConstructorCall(name, args) => args.exists(isPresentIn(_))
			case RangeValue(min, max) => isPresentIn(min) || isPresentIn(max)
		}


	def checkSelectorUse()(implicit selector: Selector = Selector.self) = {
		if (selector != Selector.self) {
			if (!modifiers.isEntity)throw new Exception("Cannot have selector with not scoreboard variable")
			if (modifiers.isLazy) throw new Exception("Cannot have selector with lazy variable")
			getType() match
				case JsonType => throw new Exception("Cannot have selector for json type")
				case RawJsonType => throw new Exception("Cannot have selector for rawjson type")
				case other => {}
		}
	}
	def getSelector()(implicit selector: Selector = Selector.self): String = {
		checkSelectorUse()

		if (modifiers.isEntity){
			f"${selector} ${scoreboard}"
		}
		else{
			f"${inGameName} ${Settings.variableScoreboard}"
		}
	}

	def getSelectorName()(implicit selector: Selector = Selector.self): String = {
		checkSelectorUse()

		if (modifiers.isEntity){
			f"${selector}"
		}
		else{
			f"${inGameName}"
		}
	}

	def getSelectorObjective()(implicit selector: Selector = Selector.self): String = {
		checkSelectorUse()

		if (modifiers.isEntity){
			f"${scoreboard}"
		}
		else{
			f"${Settings.variableScoreboard}"
		}
	} 

	def getEntityVariableSelector(): Selector = {
		JavaSelector("@e", List(("tag", SelectorIdentifier(tagName))))
	}
}

class PropertySetVariable(context: Context, getter: Function, setter: Function, variable: Variable) extends Variable(context, "", VoidType, Modifier.newPublic()){
	override def assign(op: String, value: Expression)(implicit context: Context, selector: Compilation.Selector.Selector): List[String] = {
		if (op == ":=") throw new Exception("Operator not supported for Properties")
		if (op == "="){
			if (selector != Selector.self){
				Compiler.compile(With(SelectorValue(selector), BoolValue(true), BoolValue(true), LinkedFunctionCall(setter, List(value))))
			}
			else{
				(setter,List(value)).call()
			}
		}
		else{
			if (variable == null){
				val set = BinaryOperation(op.replace("=",""), FunctionCallValue(LinkedFunctionValue(getter), List()), value)
				assign("=", set)
			}
			else{
				val set = BinaryOperation(op.replace("=",""), LinkedVariableValue(variable), value)
				assign("=", set)
			}
		}
	}
}