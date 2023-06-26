package sl

import objects.{Context, ConcreteFunction, GenericFunction, LazyFunction, Modifier, Struct, Class, Template, Variable, Enum, EnumField, Predicate, Property}
import objects.Identifier
import objects.types.{VoidType, TupleType, IdentifierType, ArrayType, IntType}
import sl.Compilation.Execute
import sl.Compilation.Selector.Selector
import objects.types.JsonType
import sl.IR.*
import objects.types.RangeType
import objects.types.StructType

object Compiler{
    def compile(context: Context):List[IRFile] = {
        var fct = context.getFunctionToCompile()

        while(fct != null){
            try{
                fct.compile()
                fct = context.getFunctionToCompile()
            }
            catch{
                e => {
                    Reporter.error(f"Error in ${fct.fullName}")
                    throw e
                }
            }
        }

        context.getMuxes().foreach(x => x.compile())
        context.getTags().foreach(x => x.compile())


        context.getAllFunction().map(_._2).filter(_.exists()).map(_.getIRFile()) ::: 
            context.getAllJsonFiles().filter(f => f.exists() && f.isDatapack()).map(fct => (fct.getIRFile())):::
            context.getAllBlockTag().filter(_.exists()).map(fct => (fct.getIRFile())):::
            context.getAllPredicates().flatMap(_.getIRFiles()):::
            Settings.target.getExtraFiles(context)
    }
    def compile(instruction: Instruction, meta: Meta = Meta(false, false))(implicit context: Context):List[IRTree]={   
        try{
            instruction match{
                case FunctionDecl(name2, block, typ2, args, typevars, modifier) =>{
                    val name = if (name2 == "~") then context.getFreshLambdaName() else name2
                    var fname = context.getFunctionWorkingName(name)
                    if (typevars.length > 0){
                        val func = new GenericFunction(context, context.getPath()+"."+name, fname, args, typevars, typ2, modifier, block.unBlockify())
                        func.overridedFunction = if modifier.isOverride then context.getFunction(Identifier.fromString(name), args.map(_.typ), List(), typ2, false) else null
                        func.modifiers.isVirtual |= modifier.isOverride
                        context.addFunction(name, func)
                    }
                    else{
                        val typ = context.getType(typ2)

                        if (Settings.target == MCBedrock && modifier.isLoading){
                            modifier.tags.addOne("@__loading__")
                        }
                        val clazz = context.getCurrentClass()
                        if (!modifier.isLazy){
                            val func = new ConcreteFunction(context, context.getPath()+"."+name, fname, args, context.getType(typ), modifier, block.unBlockify(), meta.firstPass)
                            func.overridedFunction = if modifier.isOverride then context.getFunction(Identifier.fromString(name), args.map(_.typ), List(), typ, false) else null
                            func.modifiers.isVirtual |= modifier.isOverride
                            context.addFunction(name, func)
                            func.generateArgument()(context)
                        }
                        else{
                            val func = new LazyFunction(context, context.getPath()+"."+name, fname, args, context.getType(typ), modifier, Utils.fix(block)(context, args.map(a => Identifier.fromString(a.name)).toSet))
                            func.overridedFunction = if modifier.isOverride then context.getFunction(Identifier.fromString(name), args.map(_.typ), List(), typ, false) else null
                            context.addFunction(name, func)
                            func.generateArgument()(context)
                        }
                    }
                    List()
                }
                case StructDecl(name, generics, block, modifier, parent) => {
                    if (!meta.firstPass){
                        val parentStruct = parent match
                            case None => null
                            case Some(p) => context.getStruct(p)
                        
                        context.addStruct(new Struct(context, name, generics, modifier, block.unBlockify(), parentStruct))
                    }
                    List()
                }
                case ClassDecl(name, generics, block, modifier, parent, entity) => {
                    if (!meta.firstPass){
                        val parentClass = parent match
                            case None => if name != "object" then context.getClass("object") else null
                            case Some(p) => context.getClass(p)
                        
                        context.addClass(new Class(context, name, generics, modifier, block.unBlockify(), parentClass, entity))
                    }
                    List()
                }
                case TemplateDecl(name, block, modifier, parent, generics, parentGenerics) => {
                    if (!meta.firstPass){
                        val parentTemplate = parent match
                            case None => null
                            case Some(p) => context.getTemplate(p)
                        
                        context.addTemplate(new Template(context, name, modifier, block.unBlockify(), parentTemplate, generics, parentGenerics))
                    }
                    List()
                }
                case PredicateDecl(name, args, block, modifier) => {
                    val fname = context.getFunctionWorkingName(name)
                    val pred = context.addPredicate(name, new Predicate(context, fname, args, modifier, block))
                    pred.generateArgument()(context)
                    List()
                }
                case TypeDef(name, typ) => {
                    if (!meta.firstPass){
                        context.addTypeDef(name, typ)
                    }
                    List()
                }
                case EnumDecl(name, fields, values, modifier) => {
                    if (!meta.firstPass){
                        val enm = context.addEnum(new Enum(context, name, modifier, fields.map(x => EnumField(x.name, context.getType(x.typ)))))
                        enm.addValues(values)
                    }
                    else{
                        context.getEnum(name).addValues(values)
                    }
                    List()
                }
                case ForGenerate(key, provider, instr) => {
                    val cases = Utils.getForgenerateCases(key, provider)
                    
                    cases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(instr.unBlockify())((instr, elm) => Utils.subst(instr, elm._1, elm._2))).flatMap(Compiler.compile(_, meta)).toList
                }
                case ForEach(key, provider, instr) => {
                    Utils.simplify(provider) match{
                        case LinkedVariableValue(vari, sel) if vari.getType().isInstanceOf[RangeType] && !vari.modifiers.isLazy => {
                            val RangeType(subtype) = vari.getType(): @unchecked
                            val keystr = key.toString()
                            compile(InstructionBlock(List(VariableDecl(List(keystr), subtype, Modifier.newPrivate(), "=", LinkedVariableValue(vari.tupleVari(0), sel)), 
                            WhileLoop(BinaryOperation("<=", VariableValue(keystr), LinkedVariableValue(vari.tupleVari(0), sel)), 
                            InstructionList(List(instr, VariableAssigment(List((Left(key), Selector.self)), "+=", IntValue(1))))))))
                        }
                        case other => {
                            val cases = Utils.getForeachCases(key.toString(), provider)
                    
                            //cases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(instr)((instr, elm) => Utils.subst(instr, elm._1, elm._2))).flatMap(Compiler.compile(_)).toList
                            var index = -1
                            cases.flatMap(v =>{
                                val ctx = context.getFreshContext()
                                v.flatMap(v => {
                                    val mod = Modifier.newPrivate()
                                    mod.isLazy = true
                                    val vari = new Variable(ctx, "dummy", Utils.typeof(v._2), mod)
                                    ctx.addVariable(Identifier.fromString(v._1), vari)

                                    val indx = new Variable(ctx, "dummy", IntType, mod)
                                    ctx.push(v._1).addVariable(Identifier.fromString("index"), indx)
                                    index += 1

                                    vari.assign("=", v._2):::
                                    indx.assign("=", IntValue(index))
                                }):::Compiler.compile(instr.unBlockify())(ctx)
                            }).toList
                        }
                    }
                }
                case VariableDecl(names2, typ, modifier, op, expr) => {
                    val names = names2.map(n => if n == "@@@" then context.getFreshId() else n)
                    if (typ == IdentifierType("val", List()) || typ == IdentifierType("var", List())){
                        if (typ == IdentifierType("val", List())) modifier.isConst = true
                        Utils.simplify(expr) match
                            case TupleValue(values) if values.size == names.size => {
                                names.zip(values.map(Utils.typeof(_))).map((name, typ2) => {
                                    val vari = new Variable(context, name, context.getType(typ2), modifier)
                                    context.addVariable(vari)
                                    vari.generate()
                                })
                            }
                            case other => {
                                val typ = Utils.typeof(other)
                                names.map(name => {
                                    val vari = new Variable(context, name, context.getType(typ), modifier)
                                    context.addVariable(vari)
                                    vari.generate()
                                })
                            }
                        compile(VariableAssigment(names.map(f => (Left[Identifier, Variable](Identifier.fromString(f)), Selector.self)), op, expr))
                    }
                    else{
                        names.map(name => {
                            val vari = new Variable(context, name, context.getType(typ), modifier)
                            context.addVariable(vari)
                            vari.generate()
                        })
                        if (expr != null){
                            compile(VariableAssigment(names.map(f => (Left[Identifier, Variable](Identifier.fromString(f)), Selector.self)), op, expr))
                        }
                        else{
                            List()
                        }
                    }
                }
                case sl.JSONFile(name, expr, mod) => {
                    val json = Utils.simplify(expr) match
                        case JsonValue(json) => json
                        case other => throw new Exception("JSON file must be a JSON value")
                    
                    context.addJsonFile(new objects.JSONFile(context, name, mod, Utils.compileJson(json)))
                    List()
                }
                case sl.BlocktagDecl(name, value, mod) => {
                    context.addBlockTag(new objects.Tag(context, name, mod, value.map(Utils.fix(_)(context, Set())), objects.BlockTag))
                    List()
                }
                case Import(lib, value, alias) => {
                    val ret = if (context.importFile(lib)){
                        compile(Utils.getLib(lib).get, meta.withLib)(context.root)
                    }
                    else{
                        List()
                    }
                    if (value != null){
                        context.addObjectFrom(value, if alias == null then value else alias, context.root.push(lib))
                    }
                    else{
                        val last = Identifier.fromString(lib).values.last
                        context.hasObject(lib+"."+last) match
                            case true => context.addObjectFrom(last, if alias == null then last else alias, context.root.push(lib))
                            case false if alias!=null => context.push(alias, context.getContext(lib))
                            case false => {}
                    }
                    ret
                }
                case TemplateUse(iden, name, block, values) => {
                    if (iden.toString() == "property"){
                        val sub = context.push(name)

                        compile(block.unBlockify(), meta.withFirstPass)(sub)

                        context.addProperty(Property(name, sub.getFunction("get"), sub.getFunction("set"), null))
                        List()
                    }
                    else{
                        val template = context.getTemplate(iden)
                        val sub = context.push(name)

                        sub.inherit(template.getContext())
                        sub.push("this", sub)
                        sub.setTemplateUse()
                        compile(Utils.fix(template.getBlock(values))(template.context, Set()), meta.withFirstPass)(sub) ::: compile(block.unBlockify(), meta.withFirstPass)(sub)
                    }
                }


                case VariableAssigment(names, op, expr) => {
                    if (names.length == 1){
                        val (i,s) = names.head
                        i.get().assign(op, Utils.simplify(expr))(context, s)
                    }
                    else{
                        val simplied = Utils.simplify(expr)
                        val varis = names.map((i,s) => (i.get(), s))
                        simplied match
                            case TupleValue(lst) => varis.zip(lst).flatMap(p => (p._1._1.assign(op, p._2)(context, p._1._2)))
                            case VariableValue(name, sel) => {
                                val vari = context.getVariable(name) 
                                vari.getType() match
                                    case TupleType(sub) => varis.zip(vari.tupleVari).flatMap(p => p._1._1.assign(op, LinkedVariableValue(p._2, sel))(context, p._1._2))
                                    case _ => varis.flatMap(l => l._1.assign(op, simplied)(context, l._2))
                                
                            }
                            case _ => varis.flatMap(l => l._1.assign(op, simplied)(context, l._2))
                    }
                }
                case ArrayAssigment(name, index, op, value) => {
                    def call()={
                        if (op == "="){
                            compile(FunctionCall(name.path()+"."+"set", index ::: List(value), List()))
                        }
                        else{
                            compile(FunctionCall(name.path()+"."+"set", index:::List(BinaryOperation("+", FunctionCallValue(VariableValue(name.path()+"."+"get"), index, List()), value)), List()))
                        }
                    }
                    def indexed(index: Int)={
                        compile(VariableAssigment(List((Left(Identifier.fromString(name.path()+"."+index.toString())), Selector.self)), op, value))
                    }
                    if (name == Identifier(List("this"))){
                        compile(FunctionCall("set", index ::: List(value), List()))
                    }
                    else if (index.length == 1){
                        val vari = name.get()
                        val typ = Utils.typeof(LinkedVariableValue(vari))

                        if (vari.modifiers.isLazy && typ == JsonType){
                            vari.lazyValue = JsonValue(Utils.combineJson(Utils.toJson(vari.lazyValue), JsonDictionary(Map(index.head.getString() -> Utils.toJson(Utils.simplify(value))))))
                            List()
                        }
                        else{
                            (typ, Utils.simplify(index.head)) match
                                case (ArrayType(sub, v), IntValue(index)) => indexed(index)
                                case (ArrayType(sub, v), EnumIntValue(index)) => indexed(index)
                                case _ =>{
                                    call()
                                }
                        }
                    }
                    else{
                        call()
                    }
                }
                case Return(value) => {
                    context.getCurrentFunction() match
                        case cf: ConcreteFunction => 
                            if (cf.modifiers.hasAttributes("__returnCheck__")) then
                                Compiler.compile(VariableAssigment(List((Left(Identifier.fromString("__hasFunctionReturned__")), Selector.self)), "=", IntValue(1)), meta):::
                                cf.returnVariable.assign("=", value)
                            else cf.returnVariable.assign("=", value)
                        case _ => throw new Exception(f"Unexpected return at ${instruction.pos}")
                }
                case Throw(expr) => {
                    context.requestLibrary("standard.Exception")
                    context.getCurrentFunction() match
                        case cf: ConcreteFunction => 
                            if (cf.modifiers.hasAttributes("__returnCheck__")) then
                                Compiler.compile(VariableAssigment(List((Left(Identifier.fromString("__exceptionThrown")), Selector.self)), "=", expr), meta):::
                                Compiler.compile(VariableAssigment(List((Left(Identifier.fromString("__hasFunctionReturned__")), Selector.self)), "=", IntValue(2)), meta)
                            else Compiler.compile(VariableAssigment(List((Left(Identifier.fromString("__exceptionThrown")), Selector.self)), "=", expr), meta)
                        case _ => Compiler.compile(VariableAssigment(List((Left(Identifier.fromString("__exceptionThrown")), Selector.self)), "=", expr), meta)
                }
                case Try(block, except, finallyBlock) => {
                    context.requestLibrary("standard.Exception")
                    compile(block, meta) ::: 
                    compile(If(BinaryOperation("!=",VariableValue("__exceptionThrown"), IntValue(0)), except, List()), meta) :::
                    compile(finallyBlock, meta)
                }
                case CMD(value) => List(CommandIR(value.replaceAllLiterally("\\\"","\"")))
                case Package(name, block) => {
                    val sub = if (name == "_") then context.root else context.root.push(name)
                    if (!meta.isLib){
                        compile(Settings.globalImport, meta)(sub)
                    }
                    val content = compile(block, meta)(sub)
                    if (content.length > 0){
                        val init = sub.getNamedBlock("__init__", content)
                        init.modifiers.isLoading = true
                        if (Settings.target == MCBedrock){
                            init.modifiers.tags.addOne("@__loading__")
                            sub.addFunctionToTags(init)
                        }
                    }
                    
                    List()
                }
                case InstructionList(block) => {
                    block.flatMap(inst => compile(inst, meta))
                }
                case InstructionBlock(block) => {
                    var ctx = context.getFreshContext()
                    block.flatMap(inst => compile(inst, meta)(ctx))
                }
                case FunctionCall(name, args, typeargs) => {
                    context.tryGetVariable(name) match
                        case Some(vari) if vari.getType().isInstanceOf[StructType] => {
                            Compiler.compile(FunctionCall(name.child("__apply__"), args, typeargs))
                        }
                        case other => {
                            val (fct,cargs) = context.getFunction(name, args, typeargs, VoidType)
                            if (fct != null && fct.modifiers.hasAttributes("compileAtCall")){
                                fct.asInstanceOf[ConcreteFunction].compile()
                            }
                            (fct, cargs).call()
                    }
                }
                case LinkedFunctionCall(name, args, ret) => {
                    (name, args).call(ret)
                }
                case If(BinaryOperation("||", left, right), ifBlock, elseBlock) => {
                    compile(If(left, ifBlock, ElseIf(right, ifBlock) :: elseBlock), meta)
                }
                case ifb: If => Execute.ifs(ifb)
                case swit: Switch => Execute.switch(swit)
                case whl: WhileLoop => Execute.whileLoop(whl)
                case whl: DoWhileLoop => Execute.doWhileLoop(whl)
                case at: Execute => Execute.executeInstr(at)
                case wth: With => Execute.withInstr(wth)
                case ElseIf(cond, ifBlock) => throw new Exception("Unexpected Instruction")
            }
        }
        catch{
            e => {
                Reporter.error(f"${e.getMessage()} at ${instruction.pos}\n${instruction.pos.longString}")
                throw e
            }
        }
    }
}

case class Meta(firstPass: Boolean, isLib: Boolean){
    def withLib = Meta(firstPass, true)
    def withFirstPass = Meta(true, isLib)
}