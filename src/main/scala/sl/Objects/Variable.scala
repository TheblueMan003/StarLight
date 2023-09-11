package objects

import types.*
import sl.*
import sl.Compilation.Execute
import sl.Compilation.Print
import sl.Compilation.Selector.{Selector, JavaSelector, SelectorIdentifier}
import sl.Compilation.Array
import sl.IR.*

private val entityTypeSubVariable = List((BoolType, "isPlayer"), (IntType, "binding"))
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
	var indexedVari: List[(Variable, Expression)] = List()
	val variName = if modifiers.hasAttributes("versionSpecific")(context) then fullName+"_"+Settings.version.mkString("_") else fullName
	val tagName = modifiers.getAttributesString("tag", ()=>variName)(context)
	var wasAssigned = false
	lazy val scoreboard = if modifiers.isEntity then modifiers.getAttributesString("name", ()=>context.getScoreboardID(this))(context) else ""
	lazy val inGameName = modifiers.getAttributesString("name", ()=>variName)(context)
	lazy val criterion = modifiers.getAttributesString("criterion", ()=>"dummy")(context)
	var lazyValue: Expression = DefaultValue
	var isFunctionArgument = false

	var getter: Function = null
	var setter: Function = null
	var wasGenerated = false

	def canBeReduceToLazyValue = modifiers.isLazy && !getType().isInstanceOf[StructType]

	def generate(isStructFunctionArgument: Boolean = false, skipClassCheck: Boolean = false)(implicit context: Context):Unit = {
		val parent = context.getCurrentVariable()
		val parentClass = context.getCurrentClass()

		//if (parentClass!=null && getType() == parentClass.definingType && !skipClassCheck) return

		wasGenerated = true

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
			case StructType(struct, sub) => {
				val ctx = context.push(name, this)
				ctx.inherit(struct.context)
				ctx.push("this", ctx)
				ctx.setStructUse()

				if (struct.generics.size != sub.size){
					throw new Exception(f"Struct ${struct.name} has ${struct.generics.size} generics, but ${sub.size} were given")
				}

				struct.generics.zip(sub).foreach((name, typee) => ctx.addTypeDef(name, typee))

				val block = Utils.subst(struct.getBlock(), "$this", fullName)
				if (isStructFunctionArgument && context.getCurrentVariable().getType() == getType()){
					sl.Compiler.compile(Utils.rmFunctions(block))(ctx)
				}
				else{
					sl.Compiler.compile(block)(ctx)
				}
				tupleVari.foreach(vari => vari.modifiers = vari.modifiers.combine(modifiers))
			}
			case ClassType(clazz, sub) => {
				clazz.generateForVariable(this, sub)
			}
			case ArrayType(inner, size) => {
				size match
					case IntValue(size) => {
						val ctx = context.push(name)
						tupleVari = Range(0, size).map(i => ctx.addVariable(new Variable(ctx, f"$i", inner, _modifier))).toList
						tupleVari.map(_.generate()(ctx))

						indexedVari = tupleVari.zip(Range(0, size).map(i => IntValue(i))).toList

						Array.generate(this)
					}
					case TupleValue(values) if values.forall(x => x.isInstanceOf[IntValue])=> {
						val sizes = values.map(x => x.asInstanceOf[IntValue].value)
						val size = sizes.reduce(_ * _)
						val ctx = context.push(name)
						
						val indexes = sizes.map(Range(0, _))
									.foldLeft(List[List[Int]](List()))((acc, b) => b.flatMap(v => acc.map(_ ::: List(v))).toList)
						tupleVari = Range(0, size).map(i => ctx.addVariable(new Variable(ctx, f"$i", inner, _modifier))).toList
						tupleVari.map(_.generate()(ctx))
						
						indexedVari = tupleVari.zip(indexes.map(i => TupleValue(i.map(IntValue(_))))).toList

						Array.generate(this, indexedVari, values.size)
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
			case RangeType(sub) => {
				val ctx = context.push(name)
				tupleVari = List(ctx.addVariable(new Variable(ctx, f"min", sub, _modifier)), ctx.addVariable(new Variable(ctx, f"max", sub, _modifier)))
				tupleVari.map(_.generate()(ctx))
			}
			case _ => {
			}
	}



	def assign(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if (modifiers.isConst && wasAssigned) throw new Exception(f"Cannot reassign variable $fullName at \n${value.pos.longString}")
		val ret = if (modifiers.isLazy){
			getType() match{
				case StructType(struct, sub) => {
					assignStruct(op, Utils.simplify(value))
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
										case other => throw new Exception(f"Unsupported operation: $fullName $op $value at \n${value.pos.longString}")
									}
									return value2._1
								case other => throw new Exception(f"Unsupported operation: $fullName $op $value at \n${value.pos.longString}")
						}
						case RawJsonType => {
							op match{
								case "=" => lazyValue = value
								case "+=" => lazyValue = Utils.simplify(BinaryOperation("+", lazyValue, value))
								case other => throw new Exception(f"Unsupported operation: $fullName $op $value at \n${value.pos.longString}")
							}
						}
						case other => {
							val value2 = Print.toRawJson(List(value))
							op match{
								case "=" => lazyValue = value2._2
								case "+=" => lazyValue = Utils.simplify(BinaryOperation("+", lazyValue, value2._2))
								case other => throw new Exception(f"Unsupported operation: $fullName $op $value at \n${value.pos.longString}")
							}
							return value2._1
						}
				}
				case other => {
					Utils.simplify(value) match{
						case LambdaValue(args, body, ctx) if args.size == 0 => {
							getType() match{
								case FuncType(sources, output) if sources.filter(_ != VoidType).size > 0 => {
									val filtered = sources.filter(_ != VoidType)
									if (filtered.size == 1){
										val block = ctx.getFreshLambda(filtered.map(x => "_"), filtered, output, body, false)
										return assign(op, LinkedFunctionValue(block))
									}
									else{
										val block = ctx.getFreshLambda(filtered.zipWithIndex.map("_."+_._2), filtered, output, body, false)
										return assign(op, LinkedFunctionValue(block))
									}
								}
								case FuncType(sources, output) => {
									val block = ctx.getFreshLambda(args, List(), output, body, false)
									return assign(op, LinkedFunctionValue(block))
								}
								case other => {}
							}
						}
						case LambdaValue(args, body, ctx) => {
							getType() match{
								case FuncType(sources, output) => {
									val block = ctx.getFreshLambda(args, List(), output, body, false)
									return assign(op, LinkedFunctionValue(block))
								}
								case other => {}
							}
						}
						case FunctionCallValue(VariableValue(name, sel), args, typeargs, _) => 
							try{
								val fct = context.getFunction(name, args, typeargs, getType(), false)
								if (fct._1.canBeCallAtCompileTime){
									if (fct._1.modifiers.hasAttributes("requiresVariable")){
										val vari = context.getFreshVariable(getType())
										return fct.call(vari, "=") ::: assign(op, LinkedVariableValue(vari))
									}
									else{
										return fct.call(this, op)
									}
								}
							}
							catch{
								case ObjectNotFoundException(e) =>
									lazyValue = value
									List()
								case FunctionNotFoundException(e) =>
									lazyValue = value
									List()
							}
						case FunctionCallValue(LinkedFunctionValue(fct), args, typeargs, _) => 
							if (fct.canBeCallAtCompileTime){
								if (fct.modifiers.hasAttributes("requiresVariable")){
									val vari = context.getFreshVariable(getType())
									return (fct,args).call(vari, "=") ::: assign(op, LinkedVariableValue(vari))
								}
								else{
									return (fct,args).call(this, op)
								}
							}
						case ArrayGetValue(LinkedVariableValue(vari, sl), index) if vari.modifiers.isLazy => {
							vari.lazyValue match{
								case JsonDictionary(map) => {
									val key = Utils.simplify(index.head)
									val value = map.get(key.getString())
									if (value.isDefined){
										assign(op, JsonValue(value.get))
										return List()
									}
									else{
										return List()
									}
								}
								case JsonArray(lst) => {
									val key = Utils.simplify(index.head)
									val value = lst(key.asInstanceOf[IntValue].value)
									
									assign(op, JsonValue(value))
									return List()
								}
								case other => throw new Exception(f"${other} is not a Dictionary or an Array")
							}
						}
						case ArrayGetValue(JsonValue(value), index) => {
							value match{
								case JsonDictionary(map) => {
									val key = Utils.simplify(index.head)
									val value = map.get(key.getString())
									if (value.isDefined){
										assign(op, JsonValue(value.get))
										return List()
									}
									else{
										return List()
									}
								}
								case JsonArray(lst) => {
									val key = Utils.simplify(index.head)
									val value = lst(key.asInstanceOf[IntValue].value)
									
									assign(op, JsonValue(value))
									return List()
								}
								case other => throw new Exception(f"${other} is not a Dictionary or an Array")
							}
						}
						case ArrayGetValue(a, index) => {
							a match{
								case LinkedVariableValue(vari, sl) if vari.modifiers.isLazy => {
									vari.lazyValue match{
										case JsonValue(JsonDictionary(map)) => {
											val key = Utils.simplify(index.head)
											val value = map.get(key.getString())
											if (value.isDefined){
												assign(op, JsonValue(value.get))
												return List()
											}
											else{
												return List()
											}
										}
										case JsonValue(JsonArray(lst)) => {
											val key = Utils.simplify(index.head)
											val value = lst(key.asInstanceOf[IntValue].value)
											
											assign(op, JsonValue(value))
											return List()
										}
										case _ =>{
										}
									}
								}
								case LinkedVariableValue(vari, sl) => {

								}
								case _ => {
									val (prev, LinkedVariableValue(vari, sl))=Utils.simplifyToVariable(a)
									return prev:::assign(op, ArrayGetValue(LinkedVariableValue(vari, sl), index))
								}
							}
						}
						case value => {
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
								case other => throw new Exception(f"Unsupported operation: $fullName $op $value at \n${value.pos.longString}")
							}
						}
					}
				}
			}
			List()
		}
		else{
			op match{
				case ":=" => defaultAssign(value)
				case _ => {
					Utils.simplify(value) match{
						case CastValue(left, right) if op == "=" => {
							val (prefix, vari) = Utils.simplifyToVariable(left)
							prefix ::: assignUnchecked(vari)
						}
						case VariableValue(nm, sel) if op == "=" && context.tryGetVariable(nm) == Some(this) && sel == selector =>{
							List()
						}
						case dot: DotValue => {
							val (lst,upacked) = Utils.unpackDotValue(dot)
							lst ::: assign(op, upacked)
						}
						case other => {
							if (isPresentIn(value) && value.isInstanceOf[BinaryOperation]){
								val tmp = context.getFreshVariable(getType())
								tmp.assign("=", value) ::: assign(op, LinkedVariableValue(tmp))
							}
							else{
								typ match
									case IntType => assignInt(op, other)
									case FloatType => assignFloat(op, other)
									case BoolType => assignBool(op, other)
									case TupleType(sub) => assignTuple(op, other)	
									case JsonType => assignJson(op, other)									
									case FuncType(source, out) => assignFunc(op, other)
									case StructType(struct, sub) => assignStruct(op, other)
									case ClassType(clazz, sub) => assignClass(op, other)
									case ArrayType(innter, size) => assignArray(op, other)
									case EntityType => assignEntity(op, other)
									case EnumType(enm) => assignEnum(op, other)
									case StringType => assignString(op, other)
									case RangeType(sub) => assignRange(op, other)
									case other => throw new Exception(f"Cannot Assign to $fullName of type $other at \n${value.pos.longString}")
							}
						}
					}
				}
			}
		}
		wasAssigned = true
		ret
	}

	def checkNull(tree: IRTree)(implicit selector: Selector = Selector.self) = 
		getType() match
			case EntityType => IfEntity(f"@e[tag=$tagName]", tree, true)
			case _ => IfScoreboard(getIRSelector(), "=", getIRSelector(), tree, true)
	

	def defaultAssign(expr: Expression)(implicit context: Context, selector: Selector = Selector.self) = {
		if (Settings.target == MCBedrock){
			if (modifiers.isEntity){
				Compiler.compile(If(BinaryOperation("!=", LinkedVariableValue(this, selector), NullValue), 
				InstructionList(List()),
				List(ElseIf(BoolValue(true), VariableAssigment(List((Right(this), selector)), "=", expr)))))
			}
			else{
				List()
			}
		}
		else if (Settings.target == MCJava){
			Execute.makeExecute(checkNull, assign("=", expr))
		}
		else{
			???
		}
	}

	def assignUnchecked(vari: LinkedVariableValue)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		val LinkedVariableValue(v, sl) = vari
		List(ScoreboardOperation(getIRSelector(), "=", v.getIRSelector()(sl)))
	}

	/**
	 * Assign the value to a tmp variable then apply op with the variable.
	 */
	def assignTmp(op: String, expr: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		val vari = context.getFreshVariable(getType())
		vari.assign("=", expr) ::: assign(op, LinkedVariableValue(vari))
	}


	/**
	 * Assign binary operator to the variable.
	 */
	def assignBinaryOperator(op: String, value: BinaryOperation)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
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


	def assignForce(vari: Variable)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		List(ScoreboardOperation(getIRSelector(), "=", vari.getIRSelector()))
	}
	/**
	 * Assign a value to the int variable
	 */
	def assignInt(op: String, valueE: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		valueE match
			case IntValue(value) => {
				op match{
					case "=" => List(ScoreboardSet(getIRSelector(), value))
					case "+=" => List(ScoreboardAdd(getIRSelector(), value))
					case "-=" => List(ScoreboardRemove(getIRSelector(), value))
					case "*=" | "/=" | "%=" => {
						context.requestConstant(value)
						List(ScoreboardOperation(getIRSelector(), op, SBLink(f"c$value", Settings.constScoreboard)))
					}
					case "&=" => {
						context.requestLibrary("standard.int")
						handleFunctionCall("=", VariableValue("standard.int.bitwiseAnd"), List(LinkedVariableValue(this, selector), IntValue(value)), List(), Selector.self)
					}
					case "|=" => {
						context.requestLibrary("standard.int")
						handleFunctionCall("=", VariableValue("standard.int.bitwiseOr"), List(LinkedVariableValue(this, selector), IntValue(value)), List(), Selector.self)
					}
					case "^=" => {
						context.requestLibrary("standard.int")
						handleFunctionCall("=", VariableValue("standard.int.pow"), List(LinkedVariableValue(this, selector), IntValue(value)), List(), Selector.self)
					}
					case other => throw new Exception(f"Cannot assign use op: $other with int at \n${valueE.pos.longString}")
				}
			}
			case EnumIntValue(value) => assignInt(op, IntValue(value))
			case DefaultValue => List(ScoreboardSet(getIRSelector(), 0))
			case NullValue => List(ScoreboardReset(getIRSelector()))
			case BoolValue(value) => assignInt(op, IntValue(if value then 1 else 0))
			case FloatValue(value) => assignInt(op, IntValue(value.toInt))
			case VariableValue(name, sel) => assignInt(op, context.resolveVariable(valueE))
			case LinkedVariableValue(vari, sel) => assignIntLinkedVariable(op, vari, sel)
			case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case _ => throw new Exception(f"Unknown cast to int: $valueE at \n${valueE.pos.longString}")
	}

	/**
	 * Assign a value to the int variable
	 */
	def assignRange(op: String, valueE: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		valueE match
			case RangeValue(min, max, IntValue(1)) => {
				op match
					case "=" => tupleVari(0).assign(op, min) ::: tupleVari(1).assign(op, max)
					case _ => throw new Exception(f"Cannot assign use op: $op with ranges")
			}
			case IntValue(value) => tupleVari.flatMap(_.assign(op, valueE))
			case BoolValue(value) => tupleVari.flatMap(_.assign(op, valueE))
			case FloatValue(value) => tupleVari.flatMap(_.assign(op, valueE))
			case EnumIntValue(value) => tupleVari.flatMap(_.assign(op, valueE))
			case DefaultValue => ???
			case NullValue => ???
			case VariableValue(name, sel) => tupleVari.flatMap(_.assign(op, valueE))
			case LinkedVariableValue(vari, sel) => 
				if (vari.getType().isInstanceOf[RangeType]) {
					tupleVari.zip(vari.tupleVari).flatMap((a,b) => a.assign(op, LinkedVariableValue(b, sel)))
				}
				else{
					tupleVari.flatMap(_.assign(op, valueE))
				}
			case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case _ => throw new Exception(f"Unknown cast to int: $valueE at \n${valueE.pos.longString}")
	}

	def handleFunctionCall(op: String, name: Expression, args: List[Expression], typeargs: List[Type], sel: Selector)(implicit context: Context, selector: Selector = Selector.self):List[IRTree] = {
		if (sel != Selector.self && modifiers.isEntity){
			val (pref,vari) = Utils.simplifyToVariable(FunctionCallValue(name, args, typeargs, sel))
			pref ::: assign(op, vari)
		}
		else if (sel != Selector.self){
			Compiler.compile(With(SelectorValue(sel), BoolValue(true), BoolValue(true), VariableAssigment(List((Right(this),selector)), op, FunctionCallValue(name, args, List(), Selector.self))))
		}
		else{
			name match
				case VariableValue(iden, sel) => 
					context.tryGetVariable(iden) match
						case Some(vari) if vari.getType().isInstanceOf[StructType] => {
							context.getFunction(iden.child("__apply__"), args, typeargs, getType()).call(this, op)
						}
						case other => context.getFunction(iden, args, typeargs, getType()).call(this, op)
				case LinkedFunctionValue(fct) => (fct, args).call(this, op)
				case other =>{
					val (t, v) = Utils.simplifyToVariable(other)
					v.vari.getType() match
						case FuncType(sources, output) =>
							t ::: (context.getFunctionMux(sources, output)(context), v::args).call(this, op)
						case other => throw new Exception(f"Cannot call $other at \n${name.pos.longString}")
				}
		}
	}

	/**
	 * Assign a value to the string variable
	 */
	def assignString(op: String, valueE: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if (!Settings.target.hasFeature("string"))throw new Exception("Cannot use string without string feature")
		op match{
			case "=" =>
				valueE match{
					case StringValue(value) => List(StringSet(getIRSelector(), value))
					case ArrayGetValue(name, List(RangeValue(IntValue(min), IntValue(max), IntValue(1)))) => {
						val (prev,vari) = Utils.simplifyToVariable(name)
						prev ::: List(StringCopy(getIRSelector(), vari.vari.getIRSelector(), min, max))
					}
					case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
					case _ => throw new Exception(f"Unknown cast to string: $valueE at \n${valueE.pos.longString}")
				}
			case other => throw new Exception(f"Cannot assign use op: $other with int at \n${valueE.pos.longString}")
		}
	}

	def handleArrayGetValue(op: String, name2: Expression, index: List[Expression])(implicit context: Context):List[IRTree] = {
		if (name2 == VariableValue(Identifier(List("this")), Selector.self)){
			assign(op, FunctionCallValue(VariableValue("__get__"), index, List()))
		}
		else{
			val (prev,name) = Utils.simplifyToVariable(name2)
			prev ::: (
			name match{
				case LinkedVariableValue(vari, sel) => {
					vari.getType() match
						case ArrayType(sub, IntValue(size)) => {
							index.map(Utils.simplify(_)) match
								case IntValue(i)::Nil => {
									if (i >= size || i < 0) then throw new Exception(f"Index out of Bound for array $name: $i not in 0..$size at \n${name2.pos.longString}")
									assign(op, LinkedVariableValue(vari.tupleVari(i)))
								}
								case _ => assign(op, FunctionCallValue(VariableValue(vari.fullName+".get"), index, List()))
						}
						case ArrayType(sub, TupleValue(sizes)) => {
							index.map(Utils.simplify(_)) match
								case TupleValue(indexes)::Nil if indexes.forall(_.isInstanceOf[IntValue]) => {
									vari.indexedVari.find(x => x._2 == TupleValue(indexes)) match{
										case Some((vari, _)) => assign(op, LinkedVariableValue(vari))
										case None => throw new Exception(f"Index out of Bound for array $name: $indexes not in $sizes at \n${name2.pos.longString}")
									}
								}
								case TupleValue(indexes)::Nil => {
									assign(op, FunctionCallValue(VariableValue(vari.fullName+".get"), indexes, List()))
								}
								case _ => assign(op, FunctionCallValue(VariableValue(vari.fullName+".get"), index, List()))
						}
						case other => assign(op, FunctionCallValue(VariableValue(vari.fullName+".__get__"), index, List()))
				}
			}
			)
		}
	}

	def assignIntLinkedVariable(op: String, vari: Variable, oselector: Selector)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		vari.getType() match{
			case FloatType => {
				op match{
					case "=" => {
						context.requestConstant(Settings.floatPrec)
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(oselector)),
							ScoreboardOperation(getIRSelector(), "/=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)))
					}
					case other => {
						val tmp = context.getFreshId()
						context.requestConstant(Settings.floatPrec)

						List(ScoreboardOperation(SBLink(tmp, Settings.tmpScoreboard), op, vari.getIRSelector()(oselector)),
							ScoreboardOperation(SBLink(tmp, Settings.tmpScoreboard), "/=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)),
							ScoreboardOperation(getIRSelector(), op, SBLink(tmp, Settings.tmpScoreboard))
							)
					}
				}
			}
			case other if other.isSubtypeOf(getType()) => 
				op match{
					case "=" | "+=" | "-=" | "*=" | "/=" | "%=" => 
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(oselector)))
					case "&=" => {
						context.requestLibrary("standard.int")
						handleFunctionCall("=", VariableValue("standard.int.bitwiseAnd"), List(LinkedVariableValue(this, selector), LinkedVariableValue(vari, oselector)), List(), Selector.self)
					}
					case "|=" => {
						context.requestLibrary("standard.int")
						handleFunctionCall("=", VariableValue("standard.int.bitwiseOr"), List(LinkedVariableValue(this, selector), LinkedVariableValue(vari, oselector)), List(), Selector.self)
					}
					case "^=" => {
						context.requestLibrary("standard.int")
						handleFunctionCall("=", VariableValue("standard.int.pow"), List(LinkedVariableValue(this, selector), LinkedVariableValue(vari, oselector)), List(), Selector.self)
					}
					case other => throw new Exception(f"Cannot use $other with $fullName & ${vari.fullName}")
				}
			case FuncType(sources, output) => List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(oselector)))
			case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()}")
		}
	}


	/**
	 * Assign a value to the enum variable
	 */
	def assignEnum(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		value match
			case VariableValue(name, sel) => {
				val a = getType().asInstanceOf[EnumType].enm.values.indexWhere(_.name == name.toString())
				if (a >= 0 && sel == Selector.self){
					if (op != "=") throw new Exception(f"Illegal operation: $op on enum at \n${value.pos.longString}")
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
	def assignFloat(op: String, valueE: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		valueE match
			case FloatValue(value) => {
				val fvalue = (value * Settings.floatPrec).toInt
				op match{
					case "=" => List(ScoreboardSet(getIRSelector(), fvalue))
					case "+=" => List(ScoreboardAdd(getIRSelector(), fvalue))
					case "-=" => List(ScoreboardRemove(getIRSelector(), fvalue))
					case "*=" => {
						context.requestConstant(fvalue)
						context.requestConstant(Settings.floatPrec)
						List(ScoreboardOperation(getIRSelector(), op, SBLink(f"c${fvalue}", Settings.constScoreboard)),
							ScoreboardOperation(getIRSelector(), "/=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)))
					}
					case "/=" => {
						context.requestConstant(fvalue)
						context.requestConstant(Settings.floatPrec)
						List(ScoreboardOperation(getIRSelector(), op, SBLink(f"c${fvalue}", Settings.constScoreboard)),
							ScoreboardOperation(getIRSelector(), "*=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)))
					}
					case "%=" => {
						context.requestConstant(fvalue)
						List(ScoreboardOperation(getIRSelector(), op, SBLink(f"c${fvalue}", Settings.constScoreboard)))
					}
					case "^=" => {
						context.requestLibrary("standard.float")
						handleFunctionCall("=", VariableValue("standard.float.pow"), List(LinkedVariableValue(this, selector), FloatValue(value)), List(), Selector.self)
					}
					case other => throw new Exception(f"Cannot use $other with $fullName at \n${valueE.pos.longString}")
				}
			}
			case DefaultValue => List(ScoreboardSet(getIRSelector(), 0))
			case IntValue(value) => {
				val fvalue = (value * Settings.floatPrec).toInt
				op match{
					case "=" => List(ScoreboardSet(getIRSelector(), fvalue))
					case "+=" => List(ScoreboardAdd(getIRSelector(), fvalue))
					case "-=" => List(ScoreboardRemove(getIRSelector(), fvalue))
					case "*=" |"/=" => {
						context.requestConstant(value)
						List(ScoreboardOperation(getIRSelector(), op, SBLink(f"c$value", Settings.constScoreboard)))
					}
					case "%=" => {
						context.requestConstant(fvalue)
						List(ScoreboardOperation(getIRSelector(), op, SBLink(f"c$fvalue", Settings.constScoreboard)))
					}
					case "^=" => {
						context.requestLibrary("standard.float")
						handleFunctionCall("=", VariableValue("standard.float.pow"), List(LinkedVariableValue(this, selector), FloatValue(value)), List(), Selector.self)
					}
					case other => throw new Exception(f"Cannot use $other with $fullName at \n${valueE.pos.longString}")
				}
			}
			case NullValue => List(ScoreboardReset(getIRSelector()))
			case BoolValue(value) => assignFloat(op, IntValue(if value then 1 else 0))
			case VariableValue(name, sel) => assignFloat(op, context.resolveVariable(valueE))
			case LinkedVariableValue(vari, sel) => assignFloatLinkedVariable(op, vari, sel)
			case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case _ => throw new Exception(f"Unknown cast to float for $fullName and value $valueE at \n${valueE.pos.longString}")
	}

	/**
	 * Assign a value to the float variable
	 */
	def assignBool(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		value match
			case IntValue(0) => assignBool(op, BoolValue(false))
			case IntValue(1) => assignBool(op, BoolValue(true))
			case BoolValue(value) => 
				op match{
					case "=" => List(ScoreboardSet(getIRSelector(), if value then 1 else 0))
					case "|=" => if value then List(ScoreboardSet(getIRSelector(), 1)) else List()
					case "||=" => if value then List(ScoreboardSet(getIRSelector(), 1)) else List()
					case "+=" => if value then List(ScoreboardSet(getIRSelector(), 1)) else List()
					case "&=" => if value then List() else List(ScoreboardSet(getIRSelector(), 0))
					case "&&=" => if value then List() else List(ScoreboardSet(getIRSelector(), 0))
				}
			case UnaryOperation("!", value) => {
				op match{
					case _ if isPresentIn(value )=> {
						val vari = context.getFreshVariable(BoolType)
						vari.assign("=", UnaryOperation("!", value)) ::: assign(op, LinkedVariableValue(vari))
					}
					case "=" => assignBool("=", BoolValue(true)) ::: Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
					case other => 
						val (prev, vari) = Utils.simplifyToVariable(value)
						prev ::: assignBool(op, vari)
				}
			}
			case NullValue => List(ScoreboardReset(getIRSelector()))
			case DefaultValue => List(ScoreboardSet(getIRSelector(), 0))
			case VariableValue(name, sel) => assignBool(op, context.resolveVariable(value))
			case LinkedVariableValue(vari, sel) => 
				vari.getType() match
					case a if a.isSubtypeOf(BoolType) => {
						op match
							case "&=" => List(ScoreboardOperation(getIRSelector(), "*=", vari.getIRSelector()(sel)))
							case "&&=" => List(ScoreboardOperation(getIRSelector(), "*=", vari.getIRSelector()(sel)))
							case "|=" => List(ScoreboardOperation(getIRSelector(), "+=", vari.getIRSelector()(sel)))
							case "||=" => List(ScoreboardOperation(getIRSelector(), "+=", vari.getIRSelector()(sel)))
							case "+=" => List(ScoreboardOperation(getIRSelector(), "+=", vari.getIRSelector()(sel)))
							case "=" => List(ScoreboardOperation(getIRSelector(), "=", vari.getIRSelector()(sel)))
							case _ => List(ScoreboardOperation(getIRSelector(), "*=", vari.getIRSelector()(sel)))
					}
					case EntityType => ScoreboardSet(getIRSelector(), 0)::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
					case other => throw new Exception(f"Cannot assign $value of type $other to $fullName of type ${getType()} at \n${value.pos.longString}")
			case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case BinaryOperation(op @ ("==" | "<=" | "<" | ">" | ">=" | "!=" | "||" | "&&" ), left, right) => {
				ScoreboardSet(getIRSelector(), 0)::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
			}
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case SelectorValue(sel) => ScoreboardSet(getIRSelector(), 0)::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
			case other if Utils.typeof(other) == BoolType || Utils.typeof(other) == IntType || Utils.typeof(other) == EntityType => 
				var vari2 = context.getFreshVariable(BoolType)
				Compiler.compile(If(other, 
					VariableAssigment(List((Right(vari2), Selector.self)), "=", BoolValue(true)), 
					List(ElseIf(BoolValue(true), VariableAssigment(List((Right(vari2), Selector.self)), "=", BoolValue(false)))))):::assignBool(op, LinkedVariableValue(vari2, Selector.self))
			case _ => throw new Exception(f"Unknown cast to bool $value at \n${value.pos.longString}")
	}

	def assignFloatLinkedVariable(op: String, vari: Variable, sel: Selector)(implicit context: Context, selector: Selector = Selector.self):List[IRTree]={
		vari.getType() match{
			case FloatType => {
				op match{
					case "=" | "+=" | "-=" => {
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					}
					case "*=" => {
						context.requestConstant(Settings.floatPrec)
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)),
							ScoreboardOperation(getIRSelector(), "/=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)))
					}
					case "/=" => {
						context.requestConstant(Settings.floatPrec)
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)),
							ScoreboardOperation(getIRSelector(), "*=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)))
					}
					case "%=" => {
						List(ScoreboardOperation(getIRSelector(), "%=", vari.getIRSelector()(sel)))
					}
					case "^=" => {
						context.requestLibrary("standard.float")
						handleFunctionCall(op, VariableValue("standard.float.pow"), List(LinkedVariableValue(this, selector), LinkedVariableValue(vari, sel)), List(), selector)
					}
					case other => throw new Exception(f"Unknown operator $other")
				}
			}
			case IntType => {
				op match{
					case "=" => {
						context.requestConstant(Settings.floatPrec)
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)),
							ScoreboardOperation(getIRSelector(), "*=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)))
					}
					case "*=" | "/=" => {
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					}
					case "^=" => {
						context.requestLibrary("standard.float")
						handleFunctionCall("=", VariableValue("standard.float.pow"), List(LinkedVariableValue(this, selector), LinkedVariableValue(vari, sel)), List(), selector)
					}
					case other => {
						val tmp = context.getFreshId()
						context.requestConstant(Settings.floatPrec)
						List(
							ScoreboardOperation(SBLink(tmp, Settings.tmpScoreboard), "=", vari.getIRSelector()(sel)),
							ScoreboardOperation(SBLink(tmp, Settings.tmpScoreboard), "*=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)),
							ScoreboardOperation(getIRSelector(), op, SBLink(tmp, Settings.tmpScoreboard))
						)
					}
				}
			}
			case a if a.isSubtypeOf(getType()) => 
				op match
					case "=" | "+=" | "-=" | "*=" | "/=" | "%=" => List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					case otherOp => throw new Exception(f"Cannot assign ${vari.fullName} of type ${vari.getType()} to $fullName of type ${getType()} with operator $otherOp")
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
			case FunctionCallValue(fct, args, typeargs, selector) => handleFunctionCall(op, fct, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case v => tupleVari.flatMap(t => t.assign(op, v))
	}

	def assignArray(op: String, expr2: Expression)(implicit context: Context, selector: Selector = Selector.self)={
		val ArrayType(sub, size) = getType().asInstanceOf[ArrayType]: @unchecked
		val expr = Utils.simplify(expr2)
		expr match
			case TupleValue(value) => {
				size match
					case IntValue(size) if size == value.size => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
					case _ if sub == Utils.typeof(expr) => tupleVari.flatMap(t => t.assign(op, expr))
					case _ => throw new Exception(f"Cannot assign tuple of size ${value.size} to array of size $size at \n${expr2.pos.longString}")
			}
			case LinkedVariableValue(vari, sel) => {
				if (vari.getType().isSubtypeOf(getType())){
					tupleVari.zip(vari.tupleVari).flatMap((t,v) => t.assign(op, LinkedVariableValue(v, sel)))
				}
				else{
					tupleVari.flatMap(t => t.assign(op, expr))
				}
			}
			case JsonValue(JsonArray(array)) => {
				if (array.size != tupleVari.size) throw new Exception(f"Cannot assign array of size ${array.size} to array of size ${tupleVari.size} at \n${expr2.pos.longString}")
				tupleVari.zip(array).flatMap((t, v) => t.assign(op, Utils.jsonToExpr(v)))
			}
			case ConstructorCall(Identifier(List("standard","array","Array")), List(IntValue(n)), typeArg) if op == "=" && List(sub) == typeArg && IntValue(n) == size => {
				tupleVari.flatMap(t => t.assign("=", DefaultValue))
			}
			case ConstructorCall(Identifier(List("standard","array","Array")), List(), typeArg) if op == "=" && List(sub) == typeArg => {
				tupleVari.flatMap(t => t.assign("=", DefaultValue))
			}
			case ConstructorCall(Identifier(List("@@@")), List(IntValue(n)), typeArg) if op == "=" && List(sub) == typeArg && IntValue(n) == size => {
				tupleVari.flatMap(t => t.assign("=", DefaultValue))
			}
			case ConstructorCall(Identifier(List("@@@")), List(), typeArg) if op == "=" && List(sub) == typeArg => {
				tupleVari.flatMap(t => t.assign("=", DefaultValue))
			}
			case VariableValue(vari, sel) => assign(op, context.resolveVariable(expr))
			case FunctionCallValue(fct, args, typeargs, selector) => {
				Utils.typeof(expr) match{
					case ArrayType(inner, size) => handleFunctionCall(op, fct, args, typeargs, selector)
					case other => tupleVari.flatMap(t => t.assign(op, expr))
				}
			}
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case v => tupleVari.flatMap(t => t.assign(op, v))
	}


	def assignFunc(op: String, expr: Expression)(implicit context: Context, selector: Selector = Selector.self):List[IRTree]={
		if (op != "=") throw new Exception(f"Illegal operation with ${name}: $op at \n${expr.pos.longString}")
		
		expr match
			case VariableValue(name, sel) => {
				val vari = context.tryGetVariable(name)
				vari match
					case Some(vari) => List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					case None =>{
						val typ = getType().asInstanceOf[FuncType]
						val fct = context.getFunction(name, typ.sources, List(), typ.output, true, false).asInstanceOf[ConcreteFunction]
						fct.markAsUsed()
						context.addFunctionToMux(typ.sources, typ.output, fct)
						List(ScoreboardSet(getIRSelector(), fct.getMuxID()))
				}
			}
			case LinkedVariableValue(vari, sel) => {
				vari.getType() match
					case other if Utils.fix(other)(context, Set()).isSubtypeOf(Utils.fix(getType())(context, Set())) => List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					case IntType => List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()} at \n${expr.pos.longString}")
			}
			case LambdaValue(args, instr, ctx) => {
				val typ = getType().asInstanceOf[FuncType]
				val fct = ctx.getFreshLambda(args, typ.sources, typ.output, instr).asInstanceOf[ConcreteFunction]
				fct.markAsUsed()
				context.addFunctionToMux(typ.sources, typ.output, fct)
				List(ScoreboardSet(getIRSelector(), fct.getMuxID()))
			}
			case NullValue => List(ScoreboardSet(getIRSelector(), 0))
			case DefaultValue => List(ScoreboardSet(getIRSelector(), 0))
			case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
			case LinkedFunctionValue(fct: ConcreteFunction) => {
				fct.markAsUsed()
				context.addFunctionToMux(fct.arguments.map(_.typ), fct.getType(), fct)
				List(ScoreboardSet(getIRSelector(), fct.getMuxID()))
			}
			case _ => throw new Exception(f"Unsupported Operation at \n${expr.pos.longString}")
	}
	def removeEntityTag()(implicit context: Context)={
		Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
						CMD(f"tag @a[tag=${tagName}] remove $tagName"), 
						List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${tagName}] remove $tagName")))))
	}
	def assignEntity(op: String, expr: Expression)(implicit context: Context, selector: Selector = Selector.self):List[IRTree]={
		expr match{
			case BinaryOperation("not in", left, right) => {
				val (p, _, s) = Utils.getSelector(expr)
				return p:::assignEntity(op, SelectorValue(s))
			}
			case BinaryOperation("in", left, right) => {
				val (p, _, s) = Utils.getSelector(expr)
				return p:::assignEntity(op, SelectorValue(s))
			}
			case _ => {}
		}
		if (modifiers.isEntity){
			op match
				case "=" => {
					context.getFunction("__clearBindEntity__", List(VariableValue(Identifier.fromString(fullName+".binding"))), List(), VoidType, false).call():::
					context.getFunction("__addBindEntity__", List(VariableValue(Identifier.fromString(fullName+".binding")), expr), List(), VoidType, false).call()
				}
				case "+=" | "|=" => {
					context.getFunction("__addBindEntity__", List(VariableValue(Identifier.fromString(fullName+".binding")), expr), List(), VoidType, false).call()
				}
				case _: String => throw new Exception(f"Unsupported operation $op on entity $fullName at \n${expr.pos.longString}")
		}
		else{
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
								case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()} at \n${expr.pos.longString}")
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
							List(CommandIR(f"tag ${value.getString()} add $tagName"))
						}
						case bin: BinaryOperation => assignBinaryOperator(op, bin)
						case FunctionCallValue(name, args, typeargs, selector) => 
							//removeEntityTag():::
							tupleVari.zip(List(BoolValue(false))).flatMap((t, v) => t.assign(op, v)) ::: 
							handleFunctionCall(op, name, args, typeargs, selector)
						case _ => throw new Exception(f"No cast from ${expr} to entity at \n${expr.pos.longString}")
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
								case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()} at \n${expr.pos.longString}")
						}
						case IntValue(0) => List()
						case IntValue(1) => tupleVari.flatMap(t => t.assign(op, BoolValue(false)))
						case SelectorValue(value) => {
							// Remove copy fields
							tupleVari.zip(List(BoolValue(value.isPlayer))).flatMap((t, v) => t.assign("&=", v)) ::: 
							// Add tag to new entities
							List(CommandIR(f"tag ${value.getString()} add $tagName"))
						}
						case NullValue => List()
						case bin: BinaryOperation => assignBinaryOperator(op, bin)
						case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
						case _ => throw new Exception(f"No cast from ${expr} to entity at \n${expr.pos.longString}")
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
								case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()} at \n${expr.pos.longString}")
						}
						case IntValue(0) => List()
						case SelectorValue(value) => {
							// Add tag to new entities
							List(CommandIR(f"tag ${value.getString()} remove $tagName"))
						}
						case NullValue => List()
						case bin: BinaryOperation => assignBinaryOperator(op, bin)
						case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
						case _ => throw new Exception(f"No cast from ${expr} to entity at \n${expr.pos.longString}")
				}
				case _ => throw new Exception(f"Illegal operation with ${name}: $op at \n${expr.pos.longString}")
			}
		}
	}

	/**
	 * Assign a value to the float variable
	 */
	def assignJson(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if (Settings.target == MCBedrock){
			throw new Exception(f"Dynamic Json Variable Not Supported in Bedrock ($fullName) at \n${value.pos.longString}")
		}
		if (modifiers.isEntity){
			throw new Exception(f"Not Supported at \n${value.pos.longString}")
		}
		else{
			value match
				case JsonValue(value) => 
					op match{
						case "=" => List(CommandIR(f"data modify storage ${fullName} json set value ${value.getNbt()}"))
						case "+=" => List(CommandIR(f"data modify storage ${fullName} json append value ${value.getNbt()}"))
						case "&=" => List(CommandIR(f"data modify storage ${fullName} json merge value ${value.getNbt()}"))
					}
				case DefaultValue => List(CommandIR(f"data modify storage ${fullName} json set value {}"))
				case VariableValue(name, sel) => assignBool(op, context.resolveVariable(value))
				case LinkedVariableValue(vari, sel) => 
					vari.getType() match{
						case JsonType => {
							op match{
								case "=" => List(CommandIR(f"data modify storage ${fullName} json set from storage ${vari.fullName} json"))
								case "+=" => List(CommandIR(f"data modify storage ${fullName} json append from storage ${vari.fullName} json"))
								case "&=" => List(CommandIR(f"data modify storage ${fullName} json merge from storage ${vari.fullName} json"))
							}
						}
						case IntType => {
							op match{
								case "=" => List(CommandIR(f"execute store result storage ${fullName} json int 1 run scoreboard players get ${vari.getSelector()(sel)}"))
								case "+=" => List(CommandIR(f"execute store result storage ${fullName} tmp int 1 run scoreboard players get ${vari.getSelector()(sel)}"), 
												CommandIR(f"data modify storage ${fullName} json append from storage ${fullName} tmp"))
								case "&=" => List(CommandIR(f"execute store result storage ${fullName} tmp int 1 run scoreboard players get ${vari.getSelector()(sel)}"), 
												CommandIR(f"data modify storage ${fullName} json merge from storage ${fullName} tmp"))
							}
						}
						case FloatType => {
							op match{
								case "=" => List(CommandIR(f"execute store result storage ${fullName} json float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()(sel)}"))
								case "+=" => List(CommandIR(f"execute store result storage ${fullName} tmp float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()(sel)}"), 
												CommandIR(f"data modify storage ${fullName} json append from storage ${fullName} tmp"))
								case "&=" => List(CommandIR(f"execute store result storage ${fullName} tmp float ${1/Settings.floatPrec} run scoreboard players get ${vari.getSelector()(sel)}"), 
												CommandIR(f"data modify storage ${fullName} json merge from storage ${fullName} tmp"))
							}
						}
						case other => throw new Exception(f"Cannot assign ${vari.fullName} of type $other to $fullName of type ${getType()} at \n${value.pos.longString}")
					}
				case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
				case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
				case bin: BinaryOperation => assignBinaryOperator(op, bin)
				case _ => throw new Exception(f"Unknown cast to json $value at \n${value.pos.longString}")
		}
	}

	/**
	 * Assign a value to the struct variable
	 */
	def assignStruct(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if value == DefaultValue then return List()
		op match
			case "=" => {
				value match
					case LinkedVariableValue(vari, sel) => {
						if (vari.getType() == getType()){
							tupleVari.zip(vari.tupleVari).flatMap((a,v) => a.assign(op, LinkedVariableValue(v, sel)))
						}
						else{
							context.getFunction(this.name + ".__set__", List(value), List(), getType(), false).call()
						}
					}
					case VariableValue(name, sel) => {
						assignStruct(op, context.resolveVariable(value))
					}
					case ConstructorCall(name2, args, typeArg) if name2.toString() == "@@@" => 
						context.getFunction(fullName + ".__init__", args, List(), getType(), false).call()
					case ConstructorCall(name2, args, typevars) => {
						context.getType(IdentifierType(name2.toString(), typevars)) match
							case ClassType(clazz, sub) => context.getFunction(fullName + ".__set__", List(value), List(), getType(), false).call()
							case typ@StructType(struct, sub) => {
								if (typ == getType()){
									context.getFunction(fullName + ".__init__", args, List(), getType(), false).call()
								}
								else{
									val vari = context.getFreshVariable(typ)
									vari.assign("=", value)
									assign("=", LinkedVariableValue(vari))
								}
							}
							case other => throw new Exception(f"Cannot constructor call $other at \n${value.pos.longString}")
					}
					case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
					case BinaryOperation(op, left, right) => assignBinaryOperator("=", BinaryOperation(op, left, right))
					case _ => context.getFunction(fullName + ".__set__", List(value), List(), getType(), false).call()
			}
			case op => 
				value match
					case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
					case BinaryOperation(op2, left, right) => assignBinaryOperator(op, BinaryOperation(op2, left, right))
					case _ => context.getFunction(fullName + "." + Utils.getOpFunctionName(op),  List(value), List(), getType(), false).call()
	}

	def deref()(implicit context: Context) = 
		if (modifiers.hasAttributes("variable.isTemp") || isFunctionArgument) List() else
		context.getFunction(this.name + ".__remRef", List[Expression](), List(), getType(), false).call()
	def addref()(implicit context: Context)= 
		if (modifiers.hasAttributes("variable.isTemp")|| isFunctionArgument) List() else
		context.getFunction(this.name + ".__addRef", List[Expression](), List(), getType(), false).call()

	/**
	 * Assign a value to the struct variable
	 */
	def assignClass(op: String, value: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if value == DefaultValue then return List()
		op match
			case "nullPointerAssign" => {
				value match
					case ConstructorCall(name2, args, typevars) => {
						context.getType(IdentifierType(name2.toString(), typevars)) match
							case typ@ClassType(clazz2, sub) => {
								val clazz = clazz2.get(sub)
								try{
									val entity = clazz.getEntity()
									val initarg = List(StringValue(clazz.fullName))::: (if entity != null then List(entity) else List())
									
									assign("=", FunctionCallValue(VariableValue("object.__initUnbounded"), initarg, List()))
									::: context.getFunction(name + ".__init__", args, List(), getType(), false).call()
								}
								catch{
									case e: FunctionNotFoundException => {
										throw e
									}
									case e: ObjectNotFoundException =>{
										val (pre,vari) =  Utils.simplifyToVariable(value)
										pre ::: assign("=", vari)
									}
								}
							}
							case other => throw new Exception(f"Cannot constructor call $other at \n${value.pos.longString}")
					}
			}
			case "=" => {
				value match
					case LinkedVariableValue(vari, sel) if vari.getType().isSubtypeOf(getType()) || vari.getType() == getType()=> {
						deref() ::: List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel))) ::: addref()
					}
					case VariableValue(name, sel) => assignClass(op, context.resolveVariable(value))
					case ConstructorCall(name2, args, typeArg) if name2.toString() == "@@@" => {
						val ClassType(clazz, sub) = getType(): @unchecked
						try{
							val entity = clazz.getEntity()
							val initarg = List(StringValue(clazz.fullName))::: (if entity != null then List(entity) else List())
							deref()
							::: assign("=", FunctionCallValue(VariableValue("object.__initInstance"), initarg, List()))
							::: context.getFunction(name + ".__init__", args, List(), getType(), false).call()
						}
						catch{
							case e: ObjectNotFoundException =>{
								val (pre,vari) =  Utils.simplifyToVariable(value)
								pre ::: assign("=", vari)
							}
							case e: FunctionNotFoundException => {
								val (pre,vari) =  Utils.simplifyToVariable(value)
								pre ::: assign("=", vari)
							}
						}
					}
					case ConstructorCall(name2, args, typevars) => {
						context.getType(IdentifierType(name2.toString(), typevars)) match
							case StructType(struct, sub) => throw new Exception(f"Cannot call struct constructor for class at \n${value.pos.longString}")
							case typ@ClassType(clazz2, sub) => {
								val clazz = clazz2.get(sub)
								if (typ == getType() && typevars.map(context.getType(_)) == sub && !isFunctionArgument){
									try{
										val entity = clazz.getEntity()
										val initarg = List(StringValue(clazz.fullName))::: (if entity != null then List(entity) else List())
										deref()
										::: assign("=", FunctionCallValue(VariableValue("object.__initInstance"), initarg, List()))
										::: context.getFunction(name + ".__init__", args, List(), getType(), false).call()
									}
									catch{
										case e: FunctionNotFoundException => {
											throw new Exception(f"Cannot find constructor for class ${clazz.fullName} at \n${value.pos.longString}")
										}
										case e: ObjectNotFoundException => {
											val (pre,vari) =  Utils.simplifyToVariable(value)
											pre ::: assign("=", vari)
										}
									}
								}
								else{
									val vari = context.getFreshVariable(typ)
									vari.assign("=", value):::assign("=", LinkedVariableValue(vari))
								}
							}
							case other => throw new Exception(f"Cannot constructor call $other at \n${value.pos.longString}")
					}
					case NullValue => deref() ::: List(ScoreboardSet(getIRSelector(), 0))
					case DefaultValue => List(ScoreboardSet(getIRSelector(), 0))
					case LinkedVariableValue(vari, sel) if vari.name == "__totalRefCount" => {
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					}
					case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
					case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
					case BinaryOperation(op, left, right) => assignBinaryOperator("=", BinaryOperation(op, left, right))
					case _ => context.getFunction(name + ".__set__", List(value), List(), getType(), false).call()
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
			case NamespacedName(value, json) => false
			case LinkedFunctionValue(fct) => false
			case PositionValue(x, y, z) => isPresentIn(x) || isPresentIn(y) || isPresentIn(z)
			case ClassValue(value) => false
			case DotValue(left, right) => isPresentIn(left) || isPresentIn(right)
			case ArrayGetValue(name, index) => isPresentIn(name)
			case TagValue(value) => false
			case LinkedTagValue(tag) => false
			case LambdaValue(args, instr, ctx) => false
			case VariableValue(name1, sel) => context.tryGetVariable(name1) == Some(this) && sel == selector
			case LinkedVariableValue(vari, sel) => vari == this && sel == selector
			case RawJsonValue(value) => false
			case BinaryOperation(op, left, right) => isPresentIn(left) || isPresentIn(right)
			case UnaryOperation(op, left) => isPresentIn(left)
			case TupleValue(values) => values.exists(isPresentIn(_))
			case FunctionCallValue(name, args, typeargs, selector) => args.exists(isPresentIn(_)) || isPresentIn(name)
			case ConstructorCall(name, args, typevars) => args.exists(isPresentIn(_))
			case RangeValue(min, max, delta) => isPresentIn(min) || isPresentIn(max) || isPresentIn(delta)
			case CastValue(left, right) => isPresentIn(left)
			case ForSelect(expr, filter, selector) => isPresentIn(expr) || isPresentIn(selector)
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
	def getIRSelector()(implicit selector: Selector = Selector.self): SBLink = {
		checkSelectorUse()

		if (modifiers.isEntity){
			SBLink(selector.getString()(context), scoreboard)
		}
		else{
			SBLink(inGameName, Settings.variableScoreboard)
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
	override def assign(op: String, value: Expression)(implicit context: Context, selector: Compilation.Selector.Selector): List[IRTree] = {
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
				val set = BinaryOperation(op.replace("=",""), FunctionCallValue(LinkedFunctionValue(getter), List(), List()), value)
				assign("=", set)
			}
			else{
				val set = BinaryOperation(op.replace("=",""), LinkedVariableValue(variable), value)
				assign("=", set)
			}
		}
	}
}