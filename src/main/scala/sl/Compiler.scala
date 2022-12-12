package sl

import objects.{Context, ConcreteFunction, LazyFunction, Modifier, Struct, Class, Template, Variable, Enum, EnumField, Predicate, Property}
import objects.Identifier
import objects.types.{VoidType, TupleType}
import sl.Compilation.Execute

object Compiler{
    def compile(context: Context):List[(String, List[String])] = {
        var fct = context.getFunctionToCompile()

        while(fct != null){
            fct.compile()
            fct = context.getFunctionToCompile()
        }

        context.getMuxes().foreach(x => x.compile())
        context.getTags().foreach(x => x.compile())


        context.getAllFunction().filter(_.exists()).map(fct => (fct.getName(), fct.getContent())) ::: 
            context.getAllJsonFiles().filter(_.exists()).map(fct => (fct.getName(), fct.getContent())):::
            context.getAllPredicates().flatMap(_.getFiles()):::
            Settings.target.getExtraFiles(context)
    }
    def compile(instruction: Instruction, firstPass: Boolean = false)(implicit context: Context):List[String]={   
        instruction match{
            case FunctionDecl(name, block, typ, args, modifier) =>{
                val fname = context.getFunctionWorkingName(name)
                if (Settings.target == MCBedrock && modifier.isLoading){
                    modifier.tags.addOne("__loading__")
                }
                if (!modifier.isLazy){
                    val func = new ConcreteFunction(context, fname, args, context.getType(typ), modifier, block, firstPass)
                    context.addFunction(name, func)
                    func.generateArgument()(context)
                }
                else{
                    val func = new LazyFunction(context, name, args, context.getType(typ), modifier, Utils.fix(block))
                    context.addFunction(name, func)
                    func.generateArgument()(context)
                }
                List()
            }
            case StructDecl(name, block, modifier, parent) => {
                if (!firstPass){
                    val parentStruct = parent match
                        case None => null
                        case Some(p) => context.getStruct(p)
                    
                    context.addStruct(new Struct(context, name, modifier, block, parentStruct))
                }
                List()
            }
            case ClassDecl(name, block, modifier, parent, entity) => {
                if (!firstPass){
                    val parentClass = parent match
                        case None => if name != "object" then context.getClass("object") else null
                        case Some(p) => context.getClass(p)
                    
                    context.addClass(new Class(context, name, modifier, block, parentClass, entity.getOrElse(null))).generate()
                }
                List()
            }
            case TemplateDecl(name, block, modifier, parent) => {
                if (!firstPass){
                    val parentTemplate = parent match
                        case None => null
                        case Some(p) => context.getTemplate(p)
                    
                    context.addTemplate(new Template(context, name, modifier, block, parentTemplate))
                }
                List()
            }
            case PredicateDecl(name, args, block, modifier) => {
                val pred = context.addPredicate(new Predicate(context, name, args, modifier, block))
                pred.generateArgument()(context)
                List()
            }
            case TypeDef(name, typ) => {
                if (!firstPass){
                    context.addTypeDef(name, typ)
                }
                List()
            }
            case EnumDecl(name, fields, values, modifier) => {
                if (!firstPass){
                    val enm = context.addEnum(new Enum(context, name, modifier, fields.map(x => EnumField(x.name, context.getType(x.typ)))))
                    enm.addValues(values)
                }
                List()
            }
            case ForGenerate(key, provider, instr) => {
                val cases = Utils.getForgenerateCases(key, provider)
                
                cases.map(lst => lst.sortBy(0 - _._1.length()).foldLeft(instr)((instr, elm) => Utils.subst(instr, elm._1, elm._2))).flatMap(Compiler.compile(_, firstPass)).toList
            }
            case ForEach(key, provider, instr) => {
                val cases = Utils.getForeachCases(provider)
                
                cases.map(elm => Utils.subst(instr, key.toString(), elm)).flatMap(Compiler.compile(_, firstPass)).toList
            }
            case VariableDecl(name, typ, modifier) => {
                val vari = new Variable(context, name, context.getType(typ), modifier)
                context.addVariable(vari)
                vari.generate()
                List()
            }
            case sl.JSONFile(name, json) => {
                context.addJsonFile(new objects.JSONFile(context, name, Modifier.newPrivate(), Utils.compileJson(json)))
                List()
            }
            case Import(lib, value, alias) => {
                val ret = if (context.importFile(lib)){
                    compile(Utils.getLib(lib).get, firstPass)(context.root)
                }
                else{
                    List()
                }
                if (value != null){
                    context.addObjectFrom(value, if alias == null then value else alias, context.root.push(lib))
                }
                else if (alias != null){
                    context.push(alias, context.getContext(lib))
                }
                ret
            }
            case TemplateUse(iden, name, block) => {
                if (iden.toString() == "property"){
                    val sub = context.push(name)

                    compile(block, true)(sub)

                    context.addProperty(Property(name, sub.getFunction("get"), sub.getFunction("set"), null))
                    List()
                }
                else{
                    val template = context.getTemplate(iden)
                    val sub = context.push(name)

                    sub.inherit(template.context)
                    compile(Utils.fix(template.block)(template.context), true)(sub) ::: compile(block, true)(sub)
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
                if (op == "="){
                    compile(FunctionCall(name.path()+"."+"set", index ::: List(value)))
                }
                else{
                    compile(FunctionCall(name.path()+"."+"set", index:::List(BinaryOperation("+", FunctionCallValue(VariableValue(name.path()+"."+"get"), index), value))))
                }
            }
            case Return(value) => {
                context.getCurrentFunction() match
                    case cf: ConcreteFunction => cf.returnVariable.assign("=", value)
                    case _ => throw new Exception(f"Unexpected return at ${instruction.pos}")
            }
            case CMD(value) => List(value.replaceAllLiterally("\\\"","\""))
            case Package(name, block) => {
                val sub = context.root.push(name)
                val content = compile(block, firstPass)(sub)
                if (content.length > 0){
                    val init = sub.getNamedBlock("__init__", content)
                    init.modifiers.isLoading = true
                    if (Settings.target == MCBedrock && init.modifiers.isLoading){
                        init.modifiers.tags.addOne("__loading__")
                        sub.addFunctionToTags(init)
                    }
                }
                List()
            }
            case InstructionList(block) => {
                block.flatMap(inst => compile(inst, firstPass))
            }
            case InstructionBlock(block) => {
                block.flatMap(inst => compile(inst, firstPass))
            }
            case FunctionCall(name, args) => {
                context.getFunction(name, args, VoidType).call()
            }
            case LinkedFunctionCall(name, args, ret) => {
                (name, args).call(ret)
            }
            case If(BinaryOperation("||", left, right), ifBlock, elseBlock) => {
                compile(If(left, ifBlock, ElseIf(right, ifBlock) :: elseBlock), firstPass)
            }
            case ifb: If => Execute.ifs(ifb)
            case swit: Switch => Execute.switch(swit)
            case whl: WhileLoop => Execute.whileLoop(whl)
            case whl: DoWhileLoop => Execute.doWhileLoop(whl)
            case at: At => Execute.atInstr(at)
            case wth: With => Execute.withInstr(wth)
            case ElseIf(cond, ifBlock) => throw new Exception("Unexpected Instruction")
        }
    }
}