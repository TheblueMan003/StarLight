package objects

import types.*
import sl.*
import sl.Compilation.Execute
import sl.Compilation.Print
import sl.Compilation.Selector.{Selector, JavaSelector, SelectorIdentifier}
import sl.Compilation.Array
import sl.IR.*

private val entityTypeSubVariable = List((BoolType, "isPlayer"), (IntType, "binding"))
/**
 * This object provides an extension method for the Either type, which is used to represent a value that can be one of two types.
 * The extension method provides two functions:
 * 1. get() - returns the Variable object represented by the Either value, using the given context to resolve any identifiers or properties.
 * 2. path() - returns a string representation of the path to the Variable object represented by the Either value, using the given context to resolve any identifiers or properties.
 */
object Variable {
	extension (value: Either[Identifier, Variable]) {
		/**
		 * Returns the Variable object represented by the Either value, using the given context to resolve any identifiers or properties.
		 *
		 * @param context The context to use for resolving any identifiers or properties.
		 * @return The Variable object represented by the Either value.
		 */
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

		/**
		 * Returns a string representation of the path to the Variable object represented by the Either value, using the given context to resolve any identifiers or properties.
		 *
		 * @param context The context to use for resolving any identifiers or properties.
		 * @return A string representation of the path to the Variable object represented by the Either value.
		 */
		def path()(implicit context: Context):String = {
			value match{
				case Left(value) => value.toString()
				case Right(value) => value.fullName
			}
		}
	}
}
/**
 * Represents a variable in the StarLight programming language.
 * @param context The context in which the variable is defined.
 * @param name The name of the variable.
 * @param typ The type of the variable.
 * @param _modifier The modifier of the variable.
 */
class Variable(context: Context, name: String, var typ: Type, _modifier: Modifier) extends CObject(context, name, _modifier) with Typed(typ){
	val parentFunction = context.getCurrentFunction()
	var tupleVari: List[Variable] = List()
	var indexedVari: List[(Variable, Expression)] = List()
	val variName = if modifiers.hasAttributes("versionSpecific")(context) then fullName+"_"+Settings.version.mkString("_") else fullName
	val tagName = modifiers.getAttributesString("tag", ()=>variName)(context)
	var wasAssigned = false
	lazy val scoreboard = if modifiers.isEntity then modifiers.getAttributesString("name", ()=>context.getScoreboardID(this))(context) else ""
	lazy val variableScoreboard = modifiers.getAttributesString("scoreboard", ()=>Settings.variableScoreboard)(context)
	lazy val inGameName = modifiers.getAttributesString("name", ()=>variName)(context)
	lazy val criterion = modifiers.getAttributesString("criterion", ()=>"dummy")(context)
	var lazyValue: Expression = DefaultValue
	var isFunctionArgument = false

	var getter: Function = null
	var setter: Function = null
	var wasGenerated = false
	var jsonArrayKey: String = if modifiers.isEntity then modifiers.getAttributesString("nbt", ()=>"json")(context) else modifiers.getAttributesString("path", ()=>"json")(context)
	
	def canBeReduceToLazyValue = modifiers.isLazy && !getType().isInstanceOf[StructType]

	override def toString(): String = f"$modifiers ${getType()} $fullName"

	/**
	 * Returns a new Variable object with the specified key.
	 *
	 * @param key The key to set for the new Variable object.
	 * @return A new Variable object with the specified key.
	 */
	def withKey(key: String)={
		val vari = Variable(context, name, typ, _modifier)
		vari.jsonArrayKey = key
		vari
	}

	/**
	 * Returns the subkey for the specified key.
	 *
	 * @param key The key to get the subkey for.
	 * @return The subkey for the specified key.
	 */
	def getSubKey(key: String) = {
		if (key.endsWith("]") && key.startsWith("[")){
			jsonArrayKey + key
		}
		else{
			jsonArrayKey+"."+key
		}
	}

	/**
	 * This method generates a JSON object for the variable with the given prefix.
	 * @param prefix The prefix to be stripped from the full name of the variable.
	 */
	def makeJson(prefix: String) = {
		typ = JsonType
		jsonArrayKey = fullName.stripPrefix(prefix+".")
	}
	/**
	 * This method generates code for the variable.
	 * @param isStructFunctionArgument A boolean indicating whether the variable is a struct function argument.
	 * @param skipClassCheck A boolean indicating whether to skip the class check.
	 * @param context The context in which the variable is being generated.
	 */
	def generate(isStructFunctionArgument: Boolean = false, skipClassCheck: Boolean = false)(implicit context: Context):Unit = {
		if (wasGenerated)return
		val parent = context.getCurrentVariable()
		val parentClass = context.getCurrentClass()
		val parentStruct = context.getCurrentStructUse()

		if (context.getCurrentFunction() == null || isFunctionArgument){
			if (parentClass != null && getType() == parentClass.definingType && !skipClassCheck) {
				return
			}
		}
		//if (parentStruct != null && getType() == parent.getType())return
		
		wasGenerated = true
		

		if (parent != null){
			if (!isFunctionArgument && !parent.tupleVari.contains(this)){
				parent.tupleVari = parent.tupleVari ::: List(this)
			}
			isFunctionArgument |= parent.isFunctionArgument
		}
		
		typ.generateExtensionFunction(this)(context.push(name))
		context.getAllExtension(typ).foreach((n,f) => {
			context.push(name).addFunction(Identifier.fromString(f.name), ExtensionFunction(f.context, this, f))
		})

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

				val block = Utils.subst(struct.getBlock().unBlockify(), "$this", fullName)
				
				if (parent != null && parent.getType() == getType()){
					sl.Compiler.compile(Utils.rmFunctions(block)(f => f.name != "__init__" && f.name != "this"))(ctx)
				}
				else{
					sl.Compiler.compile(block)(ctx)

					// Add __eq__ & __ne__ functions if needed
					if (!ctx.hasName("__eq__")){
						val inner = tupleVari.map(v => BinaryOperation("==", LinkedVariableValue(v), VariableValue(Identifier.fromString("other."+v.name))))
							.reduceOption((a,b) => BinaryOperation("&&", a, b)).getOrElse(BoolValue(true))

						val fct = LazyFunction(ctx, ctx.getPath()+".__eq__", "__eq__", List(Argument("other", StructType(struct, sub), None)), BoolType, Modifier.newPublic(), Return(inner))
						fct.generateArgument()(ctx)
						ctx.addFunction(Identifier.fromString("__eq__"), fct)
					}
					if (!ctx.hasName("__ne__")){
						val fct = LazyFunction(ctx, ctx.getPath()+".__ne__", "__ne__", List(Argument("other", StructType(struct, sub), None)), BoolType, Modifier.newPublic(), Return(UnaryOperation("!", FunctionCallValue(VariableValue(Identifier.fromString("__eq__")), List(VariableValue(Identifier.fromString("other"))), List(), Selector.self))))
						fct.generateArgument()(ctx)
						ctx.addFunction(Identifier.fromString("__ne__"), fct)
					}
				}
				
				tupleVari.foreach(vari => vari.modifiers = vari.modifiers.combine(modifiers))
			}
			case ClassType(clazz, sub) => {
				clazz.generateForVariable(this, sub)
			}
			case ArrayType(inner, size) => {
				size match
					case IntValue(size) => {
						val ctx = context.push(name, this)
						tupleVari = Range(0, size).map(i => ctx.addVariable(new Variable(ctx, f"$i", inner, _modifier))).toList
						tupleVari.map(_.generate()(ctx))

						indexedVari = tupleVari.zip(Range(0, size).map(i => IntValue(i))).toList

						Array.generate(this)
					}
					case TupleValue(values) if values.forall(x => x.isInstanceOf[IntValue])=> {
						val sizes = values.map(x => x.asInstanceOf[IntValue].value)
						val size = sizes.reduce(_ * _)
						val ctx = context.push(name, this)
						
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
				val ctx = context.push(name, this)
				tupleVari = sub.zipWithIndex.map((t, i) => ctx.addVariable(new Variable(ctx, f"_$i", t, _modifier)))
				tupleVari.map(_.generate()(ctx))
			}
			case EntityType => {
				val ctx = context.push(name, this)
				tupleVari = entityTypeSubVariable.map((t, n) => ctx.addVariable(new Variable(ctx, n, t, _modifier)))
				tupleVari.map(_.generate()(ctx))
			}
			case RangeType(sub) => {
				val ctx = context.push(name, this)
				tupleVari = List(ctx.addVariable(new Variable(ctx, f"min", sub, _modifier)), ctx.addVariable(new Variable(ctx, f"max", sub, _modifier)))
				tupleVari.map(_.generate()(ctx))
			}
			case _ => {
			}
	}



	/**
	 * Assigns a value to this variable.
	 *
	 * @param op The assignment operator.
	 * @param value The value to assign.
	 * @param allowReassign Whether reassignment is allowed.
	 * @param context The context in which the assignment is made.
	 * @param selector The selector for the variable.
	 * @return A list of intermediate representation trees representing the assignment.
	 */
	def assign(op: String, value: Expression, allowReassign: Boolean = true)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if (modifiers.isConst && wasAssigned && !allowReassign) throw new Exception(f"Cannot reassign variable $fullName at \n${value.pos.longString}")
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
										return fct.call(vari, selector, "=") ::: assign(op, LinkedVariableValue(vari))
									}
									else{
										return fct.call(this, selector, op)
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
									return (fct,args).call(vari, selector, "=") ::: assign(op, LinkedVariableValue(vari))
								}
								else{
									return (fct,args).call(this, selector, op)
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
								getType() match
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
									case VoidType if other == NullValue => List()
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

	/**
	 * Checks if the variable is null and run the tree if it is.
	 * 
	 * @param tree The tree to run if the variable is null.
	 * @param selector The Selector to use for the instruction. Defaults to Selector.self.
	 * @return A List of instructions.
	 */
	def checkNull(tree: IRTree)(implicit selector: Selector = Selector.self) = 
		getType() match
			case EntityType => IfEntity(f"@e[tag=$tagName]", tree, true)
			case _ => IfScoreboard(getIRSelector(), "=", getIRSelector(), tree, true)

	/**
	 * Generates a default assignment instruction for the Variable.
	 * 
	 * @param expr The Expression to assign to the Variable.
	 * @param context The Context to use for the instruction.
	 * @param selector The Selector to use for the instruction. Defaults to Selector.self.
	 * @return A List of instructions.
	 */
	def defaultAssign(expr: Expression)(implicit context: Context, selector: Selector = Selector.self) = {
		if (!modifiers.isEntity && parentFunction != null && expr == DefaultValue){
			assign("=", DefaultValue)
		}
		else{
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
				getType() match
					case TupleType(_) => Execute.makeExecute(checkNull, assign("=", expr) ::: assign("=", CastValue(IntValue(1), getType())))
					case StructType(_, _) => Execute.makeExecute(checkNull, assign("=", expr) ::: assign("=", CastValue(IntValue(1), getType())))
					case _ => Execute.makeExecute(checkNull, assign("=", expr))
			}
			else{
				???
			}
		}
	}

	/**
	 * Generates an assignment instruction for the Variable without checking for types.
	 * 
	 * @param vari The LinkedVariableValue to assign to the Variable.
	 * @param context The Context to use for the instruction.
	 * @param selector The Selector to use for the instruction. Defaults to Selector.self.
	 * @return A List of instructions.
	 */
	def assignUnchecked(vari: LinkedVariableValue)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		val LinkedVariableValue(v, sl) = vari
		List(ScoreboardOperation(getIRSelector(), "=", v.getIRSelector()(sl)))
	}

	
	/** Assigns a temporary variable to the result of an expression and then assigns the temporary variable to this variable using the given operator.
	*
	* @param op The operator to use for the assignment.
	* @param expr The expression to assign to the temporary variable.
	* @param context The context in which the assignment is being made.
	* @param selector The selector for the variable being assigned.
	* @return A list of IRTree objects representing the assignment.
	*/
	def assignTmp(op: String, expr: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		val vari = context.getFreshVariable(getType())
		vari.assign("=", expr) ::: assign(op, LinkedVariableValue(vari))
	}

	/** Compiles the left-hand side of a sequence assignment and then assigns the right-hand side to this variable using the given operator.
	*
	* @param op The operator to use for the assignment.
	* @param value The sequence value to assign to this variable.
	* @param context The context in which the assignment is being made.
	* @param selector The selector for the variable being assigned.
	* @return A list of IRTree objects representing the assignment.
	*/
	def assignSequence(op: String, value: SequenceValue)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		Compiler.compile(value.left) ::: assign(op, value.right)
	}

	/** Compiles the left-hand side of a sequence assignment and then assigns the right-hand side to this variable using the given operator.
	*
	* @param op The operator to use for the assignment.
	* @param value The sequence value to assign to this variable.
	* @param context The context in which the assignment is being made.
	* @param selector The selector for the variable being assigned.
	* @return A list of IRTree objects representing the assignment.
	*/
	def assignSequencePost(op: String, value: SequencePostValue)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		assign(op, value.left) ::: Compiler.compile(value.right)
	}

	/** Compiles a ternary operation and assigns the result to this variable using the given operator.
	*
	* @param op The operator to use for the assignment.
	* @param value The ternary operation to assign to this variable.
	* @param context The context in which the assignment is being made.
	* @param selector The selector for the variable being assigned.
	* @return A list of IRTree objects representing the assignment.
	*/
	def assignTernaryOperator(op: String, value: TernaryOperation)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		Compiler.compile(If(value.left,
			VariableAssigment(List((Right(this), selector)), op, value.middle),
			List(
				ElseIf(BoolValue(true), VariableAssigment(List((Right(this), selector)), op, value.right))
			)))
	}

	/** Compiles a binary operation and assigns the result to this variable using the given operator.
	*
	* @param op The operator to use for the assignment.
	* @param value The binary operation to assign to this variable.
	* @param context The context in which the assignment is being made.
	* @param selector The selector for the variable being assigned.
	* @return A list of IRTree objects representing the assignment.
	*/
	def assignBinaryOperator(op: String, value: BinaryOperation)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if (value.op == "??"){
			return Compiler.compile(If(BinaryOperation("==", value.left, NullValue), 
				VariableAssigment(List((Right(this), selector)), "=", value.right),
				List(ElseIf(BoolValue(true), VariableAssigment(List((Right(this), selector)), "=", value.left)))))
		}
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
	 * Assigns values to a variable of type int based on the given operator and expression.
	 *
	 * @param op The operator to use for the assignment (e.g. "+=", "-=", etc.).
	 * @param valueE The expression representing the value to assign.
	 * @param context The context in which the assignment is being made.
	 * @param selector The selector to assign the value to. Defaults to Selector.self.
	 * @return A list of IRTree objects representing the assignment operation.
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
			case ter: TernaryOperation => assignTernaryOperator(op, ter)
			case seq: SequenceValue => assignSequence(op, seq)
			case seq: SequencePostValue => assignSequencePost(op, seq)
			case _ => throw new Exception(f"Unknown cast to int: $valueE at \n${valueE.pos.longString}")
	}


	/**
	 * Assigns values to a variable of type range based on the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param valueE The expression representing the range of values to assign.
	 * @param context The context in which the assignment is being made.
	 * @param selector The selector specifying the variable to assign to.
	 * @return A list of intermediate representation trees representing the assignment operation.
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
			case NullValue => throw new Exception("Cannot assign null to range")
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
			case ter: TernaryOperation => assignTernaryOperator(op, ter)
			case seq: SequenceValue => assignSequence(op, seq)
			case seq: SequencePostValue => assignSequencePost(op, seq)
			case _ => throw new Exception(f"Unknown cast to int: $valueE at \n${valueE.pos.longString}")
	}

	
	/**
	 * Handles a function call for a variable.
	 *
	 * @param op The operator used in the function call.
	 * @param name The name of the function being called.
	 * @param args The arguments passed to the function call.
	 * @param typeargs The type arguments passed to the function call.
	 * @param sel The selector used to access the function.
	 * @param context The context in which the function call is being made.
	 * @param selector The selector used to access the variable (defaults to Selector.self).
	 * @return A list of IRTree nodes representing the function call.
	 */
	def handleFunctionCall(op: String, name: Expression, args: List[Expression], typeargs: List[Type], sel: Selector)(implicit context: Context, selector: Selector = Selector.self):List[IRTree] = {
		if (sel != Selector.self && modifiers.isEntity){
			val (pref,vari) = Utils.simplifyToVariable(FunctionCallValue(name, args, typeargs, sel))
			pref ::: assign(op, vari)
		}
		else if (sel != Selector.self){
			Compiler.compile(With(SelectorValue(sel), BoolValue(true), BoolValue(true), VariableAssigment(List((Right(this),selector)), op, FunctionCallValue(name, args, List(), Selector.self)), null))
		}
		else{
			name match
				case VariableValue(iden, sel) => 
					context.tryGetVariable(iden) match
						case Some(vari) if vari.getType().isInstanceOf[StructType] => {
							context.getFunction(iden.child("__apply__"), args, typeargs, getType()).call(this, selector, op)
						}
						case other => context.getFunction(iden, args, typeargs, getType()).call(this, selector, op)
				case LinkedFunctionValue(fct) => (fct, args).call(this, selector, op)
				case other =>{
					val (t, v) = Utils.simplifyToVariable(other)
					v.vari.getType() match
						case FuncType(sources, output) =>
							t ::: (context.getFunctionMux(sources, output)(context), v::args).call(this, selector, op)
						case other => throw new Exception(f"Cannot call $other at \n${name.pos.longString}")
				}
		}
	}

	
	/**
	 * Assigns a value to variable of type string using the given operator and expression.
	 * Throws an exception if the "string" feature is not enabled in the current target settings.
	 *
	 * @param op The operator to use for the assignment.
	 * @param valueE The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
	 * @throws Exception If the "string" feature is not enabled in the current target settings.
	 */
	def assignString(op: String, valueE: Expression)(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		if (!Settings.target.hasFeature("string"))throw new Exception("Cannot use string without string feature")
		op match{
			case "=" =>
				valueE match{
					case StringValue(value) => 
						List(StringSet(getStorage(), Utils.stringify(value)))
					case ArrayGetValue(name, List(RangeValue(IntValue(min), IntValue(max), IntValue(1)))) => {
						val (prev,vari) = Utils.simplifyToVariable(name)
						prev ::: List(StringCopy(getStorage(), vari.vari.getStorage(), min, max))
					}
					case ArrayGetValue(name, List(RangeValue(a, b, IntValue(1)))) if Utils.typeof(a).isSubtypeOf(IntType) && Utils.typeof(b).isSubtypeOf(IntType) => {
						context.requestLibrary("standard.string")
						handleFunctionCall("=", VariableValue("standard.string.slice"), List(name, a, b), List(), Selector.self)
					}
					case ArrayGetValue(name, List(IntValue(min))) => {
						if (min >= 0){
							val (prev,vari) = Utils.simplifyToVariable(name)
							prev ::: List(StringCopy(getStorage(), vari.vari.getStorage(), min, min))
						}
						else{
							context.requestLibrary("standard.string")
							handleFunctionCall("=", VariableValue("standard.string.charAt"), List(name, IntValue(min)), List(), Selector.self)
						}
					}
					case ArrayGetValue(name, List(a)) if Utils.typeof(a).isSubtypeOf(IntType)=> {
						context.requestLibrary("standard.string")
						handleFunctionCall("=", VariableValue("standard.string.charAt"), List(name, a), List(), Selector.self)
					}
					case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
					case LinkedVariableValue(vari, selector) => {
						vari.getType() match
							case StringType => List(StringCopy(getStorage(), vari.getStorage()(context, selector)))
							case other => 
								context.requestLibrary("standard.string")
								handleFunctionCall("=", VariableValue("standard.string.cast"), List(LinkedVariableValue(vari, selector)), List(), Selector.self)
							//case other => throw new Exception(f"Cannot assign $other to string at \n${valueE.pos.longString}")
					}
					case DefaultValue => List(StringSet(getStorage(), Utils.stringify("")))
					case JsonValue(JsonString(value)) => List(StringSet(getStorage(), Utils.stringify(value)))
					case bin: BinaryOperation => assignBinaryOperator(op, bin)
					case ter: TernaryOperation => assignTernaryOperator(op, ter)
					case seq: SequenceValue => assignSequence(op, seq)
					case seq: SequencePostValue => assignSequencePost(op, seq)
					case other => 
						context.requestLibrary("standard.string")
						handleFunctionCall("=", VariableValue("standard.string.cast"), List(other), List(), Selector.self)
				}
			case "+=" => {
				valueE match{
					case StringValue(value) => 
						context.requestLibrary("standard.string")
						handleFunctionCall("=", VariableValue("standard.string.concat"), List(LinkedVariableValue(this), StringValue(value)), List(), Selector.self)
					case LinkedVariableValue(vari, selector) => {
						context.requestLibrary("standard.string")
						handleFunctionCall("=", VariableValue("standard.string.concat"), List(LinkedVariableValue(this), LinkedVariableValue(vari, selector)), List(), Selector.self)
					}
					case _ => {
						val vari = context.getFreshVariable(StringType)
						vari.assign("=", valueE) ::: assign("+=", LinkedVariableValue(vari))
					}
				}
			}
			case "*=" => {
				Utils.typeof(valueE) match
					case IntType => 
						context.requestLibrary("standard.string")
						handleFunctionCall("=", VariableValue("standard.string.multiply"), List(LinkedVariableValue(this), valueE), List(), Selector.self)
				
			}
			case other => throw new Exception(f"Cannot assign use op: $other with int at \n${valueE.pos.longString}")
		}
	}

	/**
	 * Handles getting the value of an array variable.
	 *
	 * @param op the operator used to get the value (e.g. "+=")
	 * @param name2 the name of the variable
	 * @param index the index of the array element to get
	 * @param context the current context
	 * @return a list of IRTree nodes representing the operation
	 */
	def handleArrayGetValue(op: String, name2: Expression, index: List[Expression])(implicit context: Context):List[IRTree] = {
		if (name2 == VariableValue(Identifier(List("this")), Selector.self)){
			assign(op, FunctionCallValue(VariableValue("__get__"), index, List()))
		}
		else{
			name2 match
				case pos: PositionValue => {
					getType() match
						case JsonType => {
							index match
								case List(StringValue(key2)) if  op == "=" => List(StorageSet(getStorage(jsonArrayKey), StorageBlock(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == "<:=" => List(StoragePrepend(getStorage(jsonArrayKey), StorageBlock(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == ":>=" => List(StorageAppend(getStorage(jsonArrayKey), StorageBlock(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == "::=" => List(StorageMerge(getStorage(jsonArrayKey), StorageBlock(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == "-:=" => List(StorageRemove(getStorage(jsonArrayKey), StorageBlock(pos.getString(), key2)))
								case _ => throw new Exception(f"Cannot use $index as index for json")
						}
						case other => {
							val vari = context.getFreshVariable(JsonType)
							vari.handleArrayGetValue("=", name2, index) ::: assign(op, LinkedVariableValue(vari))
						}
				}
				case pos: SelectorValue => {
					getType() match
						case JsonType => {
							index match
								case List(StringValue(key2)) if  op == "=" => List(StorageSet(getStorage(jsonArrayKey), StorageEntity(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == "<:=" => List(StoragePrepend(getStorage(jsonArrayKey), StorageEntity(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == ":>=" => List(StorageAppend(getStorage(jsonArrayKey), StorageEntity(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == "::=" => List(StorageMerge(getStorage(jsonArrayKey), StorageEntity(pos.getString(), key2)))
								case List(StringValue(key2)) if  op == "-:=" => List(StorageRemove(getStorage(jsonArrayKey), StorageEntity(pos.getString(), key2)))
								case _ => throw new Exception(f"Cannot use $index as index for json")
						}
						case other => {
							val vari = context.getFreshVariable(JsonType)
							vari.handleArrayGetValue("=", name2, index) ::: assign(op, LinkedVariableValue(vari))
						}
				}
				case other => {
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
								case JsonType if op == "=" => {
									index match{
										case List(StringValue(str)) => 
											getType() match
												case IntType | EnumType(_) => List(StorageRead(getIRSelector(), vari.getStorage(vari.getSubKey(str)), 1))
												case FloatType => List(StorageRead(getIRSelector(), vari.getStorage(vari.getSubKey(str)), Settings.floatPrec))
												case BoolType => List(StorageRead(getIRSelector(), vari.getStorage(vari.getSubKey(str)), 1))
												case StringType => assign(op, LinkedVariableValue(vari.withKey(vari.getSubKey(str))))
												case StructType(struct, sub) => {
													tupleVari.flatMap(x => x.handleArrayGetValue(op, name, List(StringValue(str+"."+x.name))))
												}
												case JsonType => {
													assignJson("=", LinkedVariableValue(vari.withKey(vari.getSubKey(str))))
												}
												case FuncType(sources, output) => List(StorageRead(getIRSelector(), vari.getStorage(vari.getSubKey(str)), 1))
										case other => assign(op, FunctionCallValue(VariableValue(vari.fullName+".get"), index, List()))
									}
								}
								case TupleType(sub) => {
									index match
										case List(IntValue(i)) => assign(op, LinkedVariableValue(vari.tupleVari(i)))
										case index::Nil => 
											val id=Identifier.fromString("key")
											Compiler.compile(Switch(index, List(SwitchForEach(id, RangeValue(IntValue(0), IntValue(sub.size-1), IntValue(1)), SwitchCase(VariableValue(id), VariableAssigment(List((Right(this), sel)), op, ArrayGetValue(LinkedVariableValue(vari), List(VariableValue(id)))), BoolValue(true))))))
										case other => throw new Exception(f"Cannot use $other as index for tuple")
								}
								case other => assign(op, FunctionCallValue(VariableValue(vari.fullName+".__get__"), index, List()))
						}
					}
					)
				}
			}
	}

	/**
	 * Assigns a linked variable to the variable on the given operator and selector.
	 *
	 * @param op The operator to use for the assignment.
	 * @param vari The variable to assign the value from.
	 * @param oselector The selector to use for the assignment.
	 * @param context The context in which the assignment is being made.
	 * @param selector The selector to use for the assignment (defaults to Selector.self).
	 * @return A list of IRTree objects representing the assignment.
	 */
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
			case JsonType => {
				op match{
					case "=" => List(StorageRead(getIRSelector(), vari.getStorage()))
					case other => {
						val tmp = context.getFreshVariable(getType())
						StorageRead(tmp.getIRSelector(), vari.getStorage())::
							assign(op, LinkedVariableValue(tmp))
					}
				}
			}
			case other => castVariable(op, vari, oselector)
		}
	}

	def castVariable(op: String, vari: Variable, oselector: Selector)(implicit context: Context, selector: Selector = Selector.self) = {
		try{
			val fct = vari.context.push(vari.name).getFunction(Identifier.fromString("__cast__"), List(), List(), getType())
			if (fct._1.getType().isSubtypeOf(getType()) || fct._1.getType() == getType()){
				if (vari.modifiers.isEntity && modifiers.isEntity && (oselector != Selector.self || selector != Selector.self)){
					val tmp = context.getFreshVariable(getType())
					Compiler.compile(With(SelectorValue(oselector), BoolValue(false), BoolValue(true), VariableAssigment(List((Right(tmp), Selector.self)), "=", FunctionCallValue(LinkedFunctionValue(fct._1), fct._2, List(), Selector.self)), null)):::
						assign(op, LinkedVariableValue(tmp))
				}
				else if (vari.modifiers.isEntity && oselector != Selector.self){
					if (op == "="){
						Compiler.compile(With(SelectorValue(oselector), BoolValue(false), BoolValue(true), VariableAssigment(List((Right(this), selector)), op, FunctionCallValue(LinkedFunctionValue(fct._1), fct._2, List(), selector)), null))
					}
					else{
						val tmp = context.getFreshVariable(getType())
						Compiler.compile(With(SelectorValue(oselector), BoolValue(false), BoolValue(true), VariableAssigment(List((Right(tmp), Selector.self)), "=", FunctionCallValue(LinkedFunctionValue(fct._1), fct._2, List(), Selector.self)), null)):::
							assign(op, LinkedVariableValue(tmp))
					}
				}
				else{
					if (op == "="){
						fct.call(this, selector, op)
					}
					else{
						val tmp = context.getFreshVariable(getType())
						fct.call(tmp, Selector.self, "=") ::: assign(op, LinkedVariableValue(tmp))
					}
				}
			}
			else{
				throw new Exception(f"Cannot assign ${vari.fullName} of type ${vari.getType()} to $fullName of type ${getType()}. Cast found but not compatible. ${fct._1.getType()} vs ${getType()}")
			}
		}
		catch{
			case e: FunctionNotFoundException => throw new Exception(f"Cannot assign ${vari.fullName} of type ${vari.getType()} to $fullName of type ${getType()}: $e")
		}
	}


	/**
	 * Assigns a value to variable of type enum using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param value The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
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
	 * Assigns a value to variable of type float using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param value The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
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
						if (value.toInt == value){
							assignFloat(op, IntValue(value.toInt))
						}
						else{
							context.requestConstant(fvalue)
							context.requestConstant(Settings.floatPrec)
							List(ScoreboardOperation(getIRSelector(), op, SBLink(f"c${fvalue}", Settings.constScoreboard)),
								ScoreboardOperation(getIRSelector(), "/=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)))
						}
					}
					case "/=" => {
						if (value.toInt == value){
							assignFloat(op, IntValue(value.toInt))
						}
						else{
							context.requestConstant(fvalue)
							context.requestConstant(Settings.floatPrec)
							List(ScoreboardOperation(getIRSelector(), "*=", SBLink(f"c${Settings.floatPrec}", Settings.constScoreboard)),
								ScoreboardOperation(getIRSelector(), op, SBLink(f"c${fvalue}", Settings.constScoreboard)))
						}

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
			case ter: TernaryOperation => assignTernaryOperator(op, ter)
			case seq: SequenceValue => assignSequence(op, seq)
			case seq: SequencePostValue => assignSequencePost(op, seq)
			case _ => throw new Exception(f"Unknown cast to float for $fullName and value $valueE at \n${valueE.pos.longString}")
	}

	/**
	 * Assigns a value to variable of type bool using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param value The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
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
					case _ if isPresentIn(value)=> {
						val vari = context.getFreshVariable(BoolType)
						vari.assign("=", UnaryOperation("!", value)) ::: assign(op, LinkedVariableValue(vari))
					}
					case "=" => assignBool("=", BoolValue(true)) ::: Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(false)), List()))
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
					case JsonType => {
						op match{
							case "=" => List(StorageRead(getIRSelector(), vari.getStorage()))
							case other => {
								val tmp = context.getFreshVariable(getType())
								StorageRead(tmp.getIRSelector(), vari.getStorage())::
									assign(op, LinkedVariableValue(tmp))
							}
						}
					}
					case EntityType => ScoreboardSet(getIRSelector(), 0)::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
					case other => throw new Exception(f"Cannot assign $value of type $other to $fullName of type ${getType()} at \n${value.pos.longString}")
			case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case BinaryOperation(op @ ("==" | "<=" | "<" | ">" | ">=" | "!=" | "||" | "&&" ), left, right) => {
				ScoreboardSet(getIRSelector(), 0)::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
			}
			case bin: BinaryOperation => assignBinaryOperator(op, bin)
			case ter: TernaryOperation => assignTernaryOperator(op, ter)
			case seq: SequenceValue => assignSequence(op, seq)
			case seq: SequencePostValue => assignSequencePost(op, seq)
			case SelectorValue(sel) => ScoreboardSet(getIRSelector(), 0)::Compiler.compile(If(value, VariableAssigment(List((Right(this), selector)), "=", BoolValue(true)), List()))
			case other if Utils.typeof(other) == BoolType || Utils.typeof(other) == IntType || Utils.typeof(other) == EntityType => 
				var vari2 = context.getFreshVariable(BoolType)
				Compiler.compile(If(other, 
					VariableAssigment(List((Right(vari2), Selector.self)), "=", BoolValue(true)), 
					List(ElseIf(BoolValue(true), VariableAssigment(List((Right(vari2), Selector.self)), "=", BoolValue(false)))))):::assignBool(op, LinkedVariableValue(vari2, Selector.self))
			case _ => throw new Exception(f"Unknown cast to bool $value at \n${value.pos.longString}")
	}

	/**
	 * Assigns a linked Variable of type float using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param vari The variable to assign the value from.
	 * @param sel The selector to use for the assignment.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
	 */
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
			case JsonType => {
				op match{
					case "=" => List(StorageRead(getIRSelector(), vari.getStorage(), Settings.floatPrec))
					case other => {
						val tmp = context.getFreshVariable(getType())
						StorageRead(tmp.getIRSelector(), vari.getStorage(), Settings.floatPrec)::
							assign(op, LinkedVariableValue(tmp))
					}
				}
			}
			case a if a.isSubtypeOf(getType()) => 
				op match
					case "=" | "+=" | "-=" | "*=" | "/=" | "%=" => List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					case otherOp => throw new Exception(f"Cannot assign ${vari.fullName} of type ${vari.getType()} to $fullName of type ${getType()} with operator $otherOp")
			case other => castVariable(op, vari, sel)
		}
	}

	/**
	 * Assigns a value to variable of type tuple using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param expr2 The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
	 */
	def assignTuple(op: String, expr2: Expression)(implicit context: Context, selector: Selector = Selector.self)={
		val expr = Utils.simplify(expr2)
		expr match
			case TupleValue(value) => tupleVari.zip(value).flatMap((t, v) => t.assign(op, v))
			case LinkedVariableValue(vari, sel) => {
				if (vari.getType().isSubtypeOf(getType())){
					tupleVari.zip(vari.tupleVari).flatMap((t,v) => t.assign(op, LinkedVariableValue(v, sel)))
				}
				else{
					try{
						castVariable(op, vari, sel)
					}
					catch{
						case e: Exception => tupleVari.flatMap(t => t.assign(op, expr))
					}
				}
			}
			case VariableValue(vari, sel) => assign(op, context.resolveVariable(expr))
			case FunctionCallValue(fct, args, typeargs, selector) => handleFunctionCall(op, fct, args, typeargs, selector)
			case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
			case v => tupleVari.flatMap(t => t.assign(op, v))
	}

	/**
	 * Assigns a value to variable of type array using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param expr2 The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
	 */
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
				vari.getType() match
					case JsonType => tupleVari.zipWithIndex.flatMap((t, i) => t.assign(op, LinkedVariableValue(vari.withKey(vari.getSubKey(f"[$i]")), sel)))
					case other => {
						if (vari.getType().isSubtypeOf(getType())){
							tupleVari.zip(vari.tupleVari).flatMap((t,v) => t.assign(op, LinkedVariableValue(v, sel)))
						}
						else{
							try{
								castVariable(op, vari, sel)
							}
							catch{
								case e: Exception => tupleVari.flatMap(t => t.assign(op, expr))
							}
						}
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


	/**
	 * Assigns a value to variable of type function using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param expr The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
	 */
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
					case JsonType => List(StorageRead(getIRSelector(), vari.getStorage()))
					case other => castVariable(op, vari, sel)
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

	/**
	 * Removes the entity tag from all the entity.
	 *
	 * @param context the context in which the entity tag is to be removed
	 * @return the compiled code to remove the entity tag
	 */
	def removeEntityTag()(implicit context: Context)={
		Compiler.compile(If(LinkedVariableValue(tupleVari(0)), 
						CMD(f"tag @a[tag=${tagName}] remove $tagName"), 
						List(ElseIf(BoolValue(true), CMD(f"tag @e[tag=${tagName}] remove $tagName")))))
	}

	/**
	 * Assigns a value to variable of type entity using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param expr The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
	 */
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
								case ClassType(clazz, sub) => {
									// Remove tag to previous entities
									removeEntityTag():::
									// Add tag to new entities
									Compiler.compile(With(LinkedVariableValue(vari, sel), BoolValue(false), BoolValue(true), VariableAssigment(List((Right(this), selector)), "=", SelectorValue(Selector.self)), EmptyInstruction))
								}
								case other => castVariable(op, vari, sel)
						}
						case NullValue => {
							removeEntityTag()
						}
						case DefaultValue => removeEntityTag()
						case SelectorValue(value) => {
							// Remove tag to previous entities
							removeEntityTag():::
							// copy fields
							tupleVari.zip(List(BoolValue(value.isPlayer))).flatMap((t, v) => t.assign(op, v)) ::: 
							// Add tag to new entities
							List(CommandIR(f"tag ${value.getString()} add $tagName"))
						}
						case bin: BinaryOperation => assignBinaryOperator(op, bin)
						case ter: TernaryOperation => assignTernaryOperator(op, ter)
						case seq: SequenceValue => assignSequence(op, seq)
						case seq: SequencePostValue => assignSequencePost(op, seq)
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
						case ter: TernaryOperation => assignTernaryOperator(op, ter)
						case seq: SequenceValue => assignSequence(op, seq)
						case seq: SequencePostValue => assignSequencePost(op, seq)
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
						case ter: TernaryOperation => assignTernaryOperator(op, ter)
						case seq: SequenceValue => assignSequence(op, seq)
						case seq: SequencePostValue => assignSequencePost(op, seq)
						case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
						case _ => throw new Exception(f"No cast from ${expr} to entity at \n${expr.pos.longString}")
				}
				case _ => throw new Exception(f"Illegal operation with ${name}: $op at \n${expr.pos.longString}")
			}
		}
	}

	/**
	 * Returns the storage object for the current variable.
	 * @param keya The key to use for the storage object. Defaults to "json".
	 * @param context The context in which the storage object is being retrieved.
	 * @param selector The selector for the storage object. Defaults to Selector.self.
	 * @return The storage object for the current variable.
	 */
	def getStorage(keya: String = "json")(implicit context: Context, selector: Selector = Selector.self) = {
		val key = if keya == "json" then jsonArrayKey else keya
		if (modifiers.isEntity){
			StorageEntity(getSelectorName().toString(), if (key.startsWith("json."))then key.substring(5) else key)
		}
		else{
			StorageStorage(fullName.replaceAll("([A-Z])","-$1").toLowerCase(), key)
		}
	}

	/**
	 * Returns the storage object for the current variable.
	 * @param keya The key to use for the storage object. Defaults to "json".
	 * @param context The context in which the storage object is being retrieved.
	 * @param selector The selector for the storage object. Defaults to Selector.self.
	 * @return The storage object for the current variable.
	 */
	def getStorageValue(keya: String = "json")(implicit context: Context, selector: Selector = Selector.self) = {
		getType() match{
			case JsonType => {
				val key = if keya == "json" then jsonArrayKey else keya
				if (modifiers.isEntity){
					StorageEntity(getSelectorName().toString(), if (key.startsWith("json."))then key.substring(5) else key)
				}
				else{
					StorageStorage(fullName.replaceAll("([A-Z])","-$1").toLowerCase(), key)
				}
			}
			case FloatType => StorageScoreboard(getIRSelector(), "float", 1/Settings.floatPrec)
			case other => StorageScoreboard(getIRSelector(), "int", 1)
		}
	}

	/**
	 * Returns the storage path for the current variable.
	 * @param keya The key to use for the storage object. Defaults to "json".
	 * @param context The context in which the storage object is being retrieved.
	 * @param selector The selector for the storage object. Defaults to Selector.self.
	 * @return The storage path for the current variable.
	 */
	def getStoragePath(keya: String = "json")(implicit context: Context, selector: Selector = Selector.self) = {
		val key = if keya == "json" then jsonArrayKey else keya
		if (modifiers.isEntity){
			getSelectorName().toString()
		}
		else{
			fullName.replaceAll("([A-Z])","-$1").toLowerCase()
		}
	}

	/**
	 * Assigns a value to variable of type json using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param value The expression representing the value to assign.
	 * @param keya The key to use for the storage object. Defaults to "json" meaning to use the default key.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
	 */
	def assignJson(op: String, value: Expression, keya: String = "json")(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		val key = if jsonArrayKey != "json" then jsonArrayKey else keya
		if (Settings.target == MCBedrock){
			throw new Exception(f"Dynamic Json Variable Not Supported in Bedrock ($fullName) at \n${value.pos.longString}")
		}

		//if (!key.startsWith("json") && !modifiers.isEntity)throw new Exception(f"Cannot assign to json key ${key} at \n${value.pos.longString}")
		value match
			case JsonValue(json) => 
				op match{
					case "=" => {
						try{
							List(StorageSet(getStorage(key), StorageString(json.getNbt())))
						}
						catch{
							_ => {
								val vari = context.getFreshVariable(JsonType)
								val a = json.forceNBT()(vari, "json", context)
								vari.assign("=", JsonValue(a._2)) ::: a._1 ::: List(StorageSet(getStorage(key), vari.getStorage()))
							}
						}
					}
					case ">:=" => {
						try{
							List(StorageAppend(getStorage(key), StorageString(json.getNbt())))
						}
						catch{
							_ => {
								val vari = context.getFreshVariable(JsonType)
								val a = json.forceNBT()(vari, "json", context)
								vari.assign("=", JsonValue(a._2)) ::: a._1 ::: List(StorageAppend(getStorage(key), vari.getStorage()))
							}
						}
					}
					case "<:=" => {
						try{
							List(StoragePrepend(getStorage(key), StorageString(json.getNbt())))
						}
						catch{
							_ => {
								val vari = context.getFreshVariable(JsonType)
								val a = json.forceNBT()(vari, "json", context)
								vari.assign("=", JsonValue(a._2)) ::: a._1 ::: List(StoragePrepend(getStorage(key), vari.getStorage()))
							}
						}
					}
					case "::=" => {
						try{
							List(StorageMerge(getStorage(key), StorageString(json.getNbt())))
						}
						catch{
							_ => {
								val vari = context.getFreshVariable(JsonType)
								val a = json.forceNBT()(vari, "json", context)
								vari.assign("=", JsonValue(a._2)) ::: a._1 ::: List(StorageMerge(getStorage(key), vari.getStorage()))
							}
						}
					}
					case "-:=" => {
						try{
							List(StorageRemove(getStorage(key), StorageString(json.getNbt())))
						}
						catch{
							_ => {
								val vari = context.getFreshVariable(JsonType)
								val a = json.forceNBT()(vari, "json", context)
								vari.assign("=", JsonValue(a._2)) ::: a._1 ::: List(StorageRemove(getStorage(key), vari.getStorage()))
							}
						}
					}
					case other => throw new Exception(f"Illegal operation with json ${name}: $op at \n${value.pos.longString}")
				}
			case DefaultValue => List(StorageSet(getStorage(key), StorageString("{}")))
			case VariableValue(name, sel) => withKey(key).assign(op, context.resolveVariable(value))
			case NullValue => List(StorageSet(getStorage(key), StorageString("{}")))
			case LinkedVariableValue(vari, sel) => 
				vari.getType() match{
					case JsonType => {
						op match{
							case "=" => List(StorageSet(getStorage(key), vari.getStorage()(context,sel)))
							case ">:=" => List(StorageAppend(getStorage(key), vari.getStorage()(context,sel)))
							case "<:=" => List(StoragePrepend(getStorage(key), vari.getStorage()(context,sel)))
							case "::=" => List(StorageMerge(getStorage(key), vari.getStorage()(context,sel)))
							case "-:=" => List(StorageRemove(getStorage(key), vari.getStorage()(context,sel)))
							case other => jsonUnpackedOperation(op, value, key)
						}
					}
					case IntType | BoolType | EnumType(_) | FuncType(_, _) => {
						op match{
							case "=" => List(StorageSet(getStorage(key), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "int"), 1)))
							case ">:=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "int"), 1)), 
											StorageAppend(getStorage(key), getStorage("tmp")))
							case "<:=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "int"), 1)), 
											StoragePrepend(getStorage(key), getStorage("tmp")))
							case "::=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "int"), 1)), 
											StorageMerge(getStorage(key), getStorage("tmp")))
							case "-:=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "int"), 1)), 
											StorageRemove(getStorage(key), getStorage("tmp")))
							case other => jsonUnpackedOperation(op, value, key)
						}
					}
					case FloatType => {
						op match{
							case "=" => List(StorageSet(getStorage(key), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "float"), 1f/Settings.floatPrec)))
							case ">:=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "float"), 1f/Settings.floatPrec)), 
											StorageAppend(getStorage(key), getStorage("tmp")))
							case "<:=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "float"), 1f/Settings.floatPrec)), 
											StoragePrepend(getStorage(key), getStorage("tmp")))
							case "::=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "float"), 1f/Settings.floatPrec)),	
											StorageMerge(getStorage(key), getStorage("tmp")))
							case "-:=" => List(StorageSet(getStorage("tmp"), StorageScoreboard(vari.getIRSelector()(sel), modifiers.getAttributesString("type", ()=> "float"), 1f/Settings.floatPrec)), 
											StorageRemove(getStorage(key), getStorage("tmp")))
							case other => jsonUnpackedOperation(op, value, key)
						}
					}
					case StringType => {
						op match{
							case "=" => List(StorageSet(getStorage(key), vari.getStorage()))
							case ">:=" => List(StorageAppend(getStorage(key), vari.getStorage()))
							case "<:=" => List(StoragePrepend(getStorage(key), vari.getStorage()))
							case "::=" => List(StorageMerge(getStorage(key), vari.getStorage()))
							case "-:=" => List(StorageRemove(getStorage(key), vari.getStorage()))
							case other => jsonUnpackedOperation(op, value, key)
						}
					}
					case StructType(struct, sub) => {
						try{
							castVariable(op, vari, sel)
						}
						catch{
							case e: Exception => 
								op match{
									case "=" => 
										vari.tupleVari.flatMap(v => withKey(key + "." + v.name).assignJson(op, LinkedVariableValue(v, sel)))
									case other => {
										val tmp = context.getFreshVariable(JsonType)
										tmp.assign("=", value):::assignJson(op, LinkedVariableValue(tmp), keya)
									}
								}
						}
					}
					case ArrayType(inner, size) => {
						op match{
							case "=" => 
								assign("=", JsonValue(JsonArray(List()))):::
								vari.tupleVari.flatMap(v => assignJson(">:=", LinkedVariableValue(v, sel)))
							case other => {
								val tmp = context.getFreshVariable(JsonType)
								tmp.assign("=", value):::assignJson("=", LinkedVariableValue(tmp), keya)
							}
						}
					}
					case other => castVariable(op, vari, sel)
				}
			case FunctionCallValue(name, args, typeargs, selector) => withKey(key).handleFunctionCall(op, name, args, typeargs, selector)
			case ArrayGetValue(name, index) => withKey(key).handleArrayGetValue(op, name, index)
			case bin: BinaryOperation => {
				if (keya == "json"){
					assignBinaryOperatorJson(op, bin)
				}
				else {
					withKey(key).assignJson(op, bin, "json")
				}
			}
			case ter: TernaryOperation => {
				if (keya == "json"){
					assignTernaryOperator(op, ter)
				}
				else {
					withKey(key).assignJson(op, ter, "json")
				}
			}
			case SequenceValue(left, right) => {
				Compiler.compile(left):::assignJson(op, right, key)
			}
			case StringValue(str) => 
				op match{
					case "=" => List(StorageSet(getStorage(key), StorageString(Utils.stringify(str))))
					case ">:=" => List(StorageAppend(getStorage(key), StorageString(Utils.stringify(str))))
					case "<:=" => List(StoragePrepend(getStorage(key), StorageString(Utils.stringify(str))))
					case "::=" => List(StorageMerge(getStorage(key), StorageString(Utils.stringify(str))))
					case "-:=" => List(StorageRemove(getStorage(key), StorageString(Utils.stringify(str))))
					case other => jsonUnpackedOperation(op, value, key)
				}
			case IntValue(_) | FloatValue(_) | BoolValue(_) => 
				op match{
					case "=" => List(StorageSet(getStorage(key), StorageString(value.getString())))
					case ">:=" => List(StorageAppend(getStorage(key), StorageString(value.getString())))
					case "<:=" => List(StoragePrepend(getStorage(key), StorageString(value.getString())))
					case "::=" => List(StorageMerge(getStorage(key), StorageString(value.getString())))
					case "-:=" => List(StorageRemove(getStorage(key), StorageString(value.getString())))
					case other => jsonUnpackedOperation(op, value, key)
				}
			case PositionValue(_,_,_) | SelectorValue(_) | TagValue(_) | LinkedTagValue(_) | NamespacedName(_, _) => 
				op match{
					case "=" => List(StorageSet(getStorage(key), StorageString(Utils.stringify(value.getString()))))
					case ">:=" => List(StorageAppend(getStorage(key), StorageString(Utils.stringify(value.getString()))))
					case "<:=" => List(StoragePrepend(getStorage(key), StorageString(Utils.stringify(value.getString()))))
					case "::=" => List(StorageMerge(getStorage(key), StorageString(Utils.stringify(value.getString()))))
					case "-:=" => List(StorageRemove(getStorage(key), StorageString(Utils.stringify(value.getString()))))
					case other => jsonUnpackedOperation(op, value, key)
				}
			case _ => throw new Exception(f"Unknown cast to json $value at \n${value.pos.longString}")
	}

	/**
	 * Assigns a binary operator to a JSON value.
	 *
	 * @param op The binary operator to assign.
	 * @param value The binary operation to assign the operator to.
	 * @param context The context in which the assignment is made.
	 * @param selector The selector for the assignment.
	 * @return A list of IRTree objects representing the assignment.
	 */
	def assignBinaryOperatorJson(op: String, value: BinaryOperation)(implicit context: Context, selector: Selector = Selector.self):List[IRTree]={
		value match
			case BinaryOperation(op2 @ ("+" | "*"), left, right) if Utils.typeof(right) == JsonType && Utils.typeof(left) != JsonType => {
				assignBinaryOperator(op, BinaryOperation(op2, right, left))
			}
			case BinaryOperation(op2, left, right) if Utils.typeof(right) == JsonType && Utils.typeof(left) != JsonType => {
				val vari = context.getFreshVariable(Utils.typeof(left))
				vari.assign("=", left) ::: vari.assign(op2, right) ::: assignJson(op, LinkedVariableValue(vari, selector))
			}
			case other => {
				assignBinaryOperator(op, other)
			}
	}

	/**
	 * Performs an operation by unpacking the JSON value.
	 *
	 * @param op The operation to perform.
	 * @param value The value to perform the operation on.
	 * @param keya The key for the operation.
	 * @param context The context in which the operation is performed.
	 * @param selector The selector for the operation.
	 * @return A list of IRTree objects representing the operation.
	 */
	def jsonUnpackedOperation(op: String, value: Expression, keya: String = "json")(implicit context: Context, selector: Selector = Selector.self): List[IRTree] = {
		value match
			case BinaryOperation("+" | "*", left, right) if Utils.typeof(right) == JsonType && Utils.typeof(left) != JsonType && Utils.typeof(left) != StringType => {
				jsonUnpackedOperation(op, BinaryOperation(op, right, left), keya)
			}
			case BinaryOperation(op, left, right) if Utils.typeof(right) == JsonType && Utils.typeof(left) != JsonType => {
				val vari = context.getFreshVariable(Utils.typeof(left))
				vari.assign("=", left) ::: vari.assign(op, right) ::: assignJson("=", LinkedVariableValue(vari, selector), keya)
			}
			case other => {
				val vari = context.getFreshVariable(Utils.typeof(value))
				vari.assign("=", LinkedVariableValue(withKey(keya), selector)) ::: vari.assign(op, value) ::: assign("=", LinkedVariableValue(vari))
			}
	}

	/**
	 * Assigns a value to variable of type struct using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param value The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
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
							vari.getType() match
								case JsonType => {
									tupleVari.flatMap(v => v.assign("=", LinkedVariableValue(vari.withKey(vari.getSubKey(v.name)), sel)))
								}
								case other => {
									try{
										context.getFunction(this.name + ".__set__", List(value), List(), getType(), false).call()
									}
									catch{
										case e: Exception => castVariable(op, vari, sel)
									}
								}
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
					case bin: BinaryOperation => assignBinaryOperator("=", bin)
					case ter: TernaryOperation => assignTernaryOperator(op, ter)
					case seq: SequenceValue => assignSequence(op, seq)
					case seq: SequencePostValue => assignSequencePost(op, seq)
					case JsonValue(JsonDictionary(map)) if map.forall(x => tupleVari.exists(y => y.name == x._1)) => {
						map.flatMap(x => tupleVari.find(y => y.name == x._1).get.assign("=", Utils.jsonToExpr(x._2))).toList
					}
					case ArrayGetValue(name, index) if Utils.typeof(value) == JsonType || Utils.typeof(value) == getType() => {
						handleArrayGetValue(op, name, index)
					}
					case _ => context.getFunction(fullName + ".__set__", List(value), List(), getType(), false).call()
			}
			case op => 
				value match
					case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
					case bin: BinaryOperation => assignBinaryOperator(op, bin)
					case ter: TernaryOperation => assignTernaryOperator(op, ter)
					case seq: SequenceValue => assignSequence(op, seq)
					case seq: SequencePostValue => assignSequencePost(op, seq)
					case _ => context.getFunction(fullName + "." + Utils.getOpFunctionName(op),  List(value), List(), getType(), false).call()
	}

	/** 
	 * Dereferences the variable if it is not a temporary variable or a function argument.
	 * @param context The context in which the variable is being dereferenced.
	 * @return An empty list if the variable is a temporary variable or a function argument, otherwise the result of calling the "__remRef" function.
	 */
	def deref()(implicit context: Context) = 
		if (modifiers.hasAttributes("variable.isTemp") || isFunctionArgument) List() else
		context.getFunction(this.fullName + ".__remRef", List[Expression](), List(), getType(), false).call()

	/** 
	 * Adds a reference to the variable if it is not a temporary variable or a function argument.
	 * @param context The context in which the variable is being referenced.
	 * @return An empty list if the variable is a temporary variable or a function argument, otherwise the result of calling the "__addRef" function.
	 */
	def addref()(implicit context: Context)= 
		if (modifiers.hasAttributes("variable.isTemp")|| isFunctionArgument) List() else
		context.getFunction(this.fullName + ".__addRef", List[Expression](), List(), getType(), false).call()

	/**
	 * Assigns a value to variable of type class using the given operator and expression.
	 *
	 * @param op The operator to use for the assignment.
	 * @param value The expression representing the value to assign.
	 * @param context The current context.
	 * @param selector The current selector.
	 * @return A list of IRTree nodes representing the assignment operation.
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
					case LinkedVariableValue(vari, sel) if vari.name == "__totalRefCount" => {
						List(ScoreboardOperation(getIRSelector(), op, vari.getIRSelector()(sel)))
					}
					case LinkedVariableValue(vari, sel) => {
						try{
							context.getFunction(name + ".__set__", List(value), List(), getType(), false).call()
						}
						catch{
							case e: Exception => castVariable(op, vari, sel)
						}
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
											throw new Exception(f"Cannot find constructor for class ${clazz.fullName} at \n${value.pos.longString} ${e.getMessage()}")
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
					case FunctionCallValue(name, args, typeargs, selector) => handleFunctionCall(op, name, args, typeargs, selector)
					case ArrayGetValue(name, index) => handleArrayGetValue(op, name, index)
					case bin: BinaryOperation => assignBinaryOperator("=", bin)
					case ter: TernaryOperation => assignTernaryOperator(op, ter)
					case seq: SequenceValue => assignSequence(op, seq)
					case seq: SequencePostValue => assignSequencePost(op, seq)
					case JsonValue(JsonDictionary(map)) if map.forall(x => tupleVari.exists(y => y.name == x._1)) => {
						map.flatMap(x => tupleVari.find(y => y.name == x._1).get.assign("=", Utils.jsonToExpr(x._2))).toList
					}
					case _ => context.getFunction(name + ".__set__", List(value), List(), getType(), false).call()
						
			}
			case _ => assignStruct(op, value)
	}


	/**
	 * Checks if the variable is present in the given expression.
	 *
	 * @param expr The expression to check for the variable's presence.
	 * @param context The context in which the variable is defined.
	 * @param selector The selector used to select the variable.
	 * @return True if the variable is present in the expression, false otherwise.
	 */
	def isPresentIn(expr: Expression)(implicit context: Context, selector: Selector): Boolean = {
		expr match
			case IntValue(value) => false
			case FloatValue(value) => false
			case BoolValue(value) => false
			case JsonValue(content) => false
			case SelectorValue(value) => false
			case InterpolatedString(content) => content.exists(isPresentIn(_))
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
			case TernaryOperation(left, middle, right) => isPresentIn(left) || isPresentIn(middle) || isPresentIn(right)
			case UnaryOperation(op, left) => isPresentIn(left)
			case TupleValue(values) => values.exists(isPresentIn(_))
			case FunctionCallValue(name, args, typeargs, selector) => args.exists(isPresentIn(_)) || isPresentIn(name)
			case ConstructorCall(name, args, typevars) => args.exists(isPresentIn(_))
			case RangeValue(min, max, delta) => isPresentIn(min) || isPresentIn(max) || isPresentIn(delta)
			case CastValue(left, right) => isPresentIn(left)
			case ForSelect(expr, filter, selector) => isPresentIn(expr) || isPresentIn(selector)
			case SequenceValue(left, right) => isPresentIn(right)
			case SequencePostValue(left, right) => isPresentIn(left)
		}


	/**
	 * Checks if the selector is being used correctly.
	 * @param selector The selector to check. Defaults to Selector.self.
	 * @throws Exception if the selector is not Selector.self and the variable is not a scoreboard variable, or if the variable is a lazy variable, or if the variable is of type JsonType or RawJsonType.
	 */
	def checkSelectorUse()(implicit selector: Selector = Selector.self) = {
		if (selector != Selector.self) {
			if (!modifiers.isEntity) throw new Exception("Cannot have selector with not scoreboard variable")
			if (modifiers.isLazy) throw new Exception("Cannot have selector with lazy variable")
			getType() match {
				case JsonType => throw new Exception("Cannot have selector for json type")
				case RawJsonType => throw new Exception("Cannot have selector for rawjson type")
				case other => {}
			}
		}
	}

	/**
	 * Gets the selector string for the variable.
	 * @param selector The selector to use. Defaults to Selector.self.
	 * @return The selector string for the variable.
	 * @throws Exception if the selector is not Selector.self and the variable is not a scoreboard variable, or if the variable is a lazy variable, or if the variable is of type JsonType or RawJsonType.
	 */
	def getSelector()(implicit selector: Selector = Selector.self): String = {
		checkSelectorUse()

		if (modifiers.isEntity) {
			f"${selector} ${scoreboard}"
		} else {
			f"${inGameName} ${variableScoreboard}"
		}
	}

	/**
	 * Gets the scoreboard link for the variable.
	 * @param selector The selector to use. Defaults to Selector.self.
	 * @return The scoreboard link for the variable.
	 * @throws Exception if the selector is not Selector.self and the variable is not a scoreboard variable, or if the variable is a lazy variable, or if the variable is of type JsonType or RawJsonType.
	 */
	def getIRSelector()(implicit selector: Selector = Selector.self): SBLink = {
		checkSelectorUse()

		if (modifiers.isEntity) {
			SBLink(selector.getString()(context), scoreboard)
		} else {
			SBLink(inGameName, variableScoreboard)
		}
	}

	/**
	 * Gets the selector name for the variable.
	 * @param selector The selector to use. Defaults to Selector.self.
	 * @return The selector name for the variable.
	 * @throws Exception if the selector is not Selector.self and the variable is not a scoreboard variable, or if the variable is a lazy variable, or if the variable is of type JsonType or RawJsonType.
	 */
	def getSelectorName()(implicit selector: Selector = Selector.self): String = {
		checkSelectorUse()

		if (modifiers.isEntity) {
			f"${selector}"
		} else {
			f"${inGameName}"
		}
	}

	/**
	 * Gets the scoreboard objective for the variable.
	 * @param selector The selector to use. Defaults to Selector.self.
	 * @return The scoreboard objective for the variable.
	 * @throws Exception if the selector is not Selector.self and the variable is not a scoreboard variable, or if the variable is a lazy variable, or if the variable is of type JsonType or RawJsonType.
	 */
	def getSelectorObjective()(implicit selector: Selector = Selector.self): String = {
		checkSelectorUse()

		if (modifiers.isEntity) {
			f"${scoreboard}"
		} else {
			f"${variableScoreboard}"
		}
	}

	/**
	 * Gets the selector for an entity variable.
	 * @return The selector for an entity variable.
	 */
	def getEntityVariableSelector(): Selector = {
		JavaSelector("@e", List(("tag", SelectorIdentifier(tagName))))
	}
}

/**
 * Represents a property set variable that extends the `Variable` class.
 *
 * @param context The context of the variable.
 * @param getter The getter function of the variable.
 * @param setter The setter function of the variable.
 * @param variable The variable to be set.
 */
class PropertySetVariable(context: Context, getter: Function, setter: Function, variable: Variable) extends Variable(context, "", VoidType, Modifier.newPublic()){
	override def assign(op: String, value: Expression, allowReassign: Boolean = true)(implicit context: Context, selector: Compilation.Selector.Selector): List[IRTree] = {
		if (op == ":=") throw new Exception("Operator not supported for Properties")
		if (setter == null) throw new Exception("Cannot assign to a read-only property")
		if (op == "="){
			if (selector != Selector.self){
				Compiler.compile(With(SelectorValue(selector), BoolValue(true), BoolValue(true), LinkedFunctionCall(setter, List(value)), null))
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