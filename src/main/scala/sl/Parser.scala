package sl

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.input._
import objects.types.*
import objects.{Modifier, Protection}
import objects.types.VoidType
import scala.util.parsing.combinator._
import objects.Identifier
import sl.Compilation.Selector.*
import sl.Reporter
import objects.EnumField
import objects.EnumValue
import sl.files.CacheAST
import objects.Variable

object Parser extends StandardTokenParsers{
  lexical.delimiters ++= List("(", ")", "\\", ".", "..", ":", "=", "{", "}", ",", "*", "[", "]", "/", "+", "-", "*", "/", "\\", "%", "&&", "||", "=>", ";",
                              "+=", "-=", "/=", "*=", "%=", "?=", ":=", "%", "@", "@e", "@a", "@s", "@r", "@p", "~", "^", "<=", "==", ">=", "<", ">", "!=", "%%%", "???", "§§§", "$",
                              "!", "!=", "#", "&", "<<=", ">>=", "&=", "|=", "::", ":>", ">:", "<:", "-:", "??", "?", "::=" , ">:=" , "<:=" , "-:=")
  lexical.reserved   ++= List("true", "false", "if", "then", "else", "return", "switch", "for", "do", "while", "by", "is",
                              "as", "at", "with", "to", "import", "template", "null", "typedef", "foreach", "in", "not",
                              "def", "package", "struct", "enum", "class", "interface", "lazy", "macro", "jsonfile", "blocktag", "itemtag", "entitytag", "throw", "try", "catch", "finally",
                              "public", "protected", "private", "scoreboard", "forgenerate", "from", "rotated", "facing", "align", "case", "default",
                              "ticking", "loading", "predicate", "extends", "implements", "new", "const", "static", "virtual", "abstract", "override", "repeat",
                              "sleep", "async", "await", "assert")


  def block: Parser[Instruction] = positioned("{" ~> rep(instruction <~ opt(";")) <~ "}" ^^ (p => InstructionBlock(p)))
  def assignmentOp: Parser[String] = ("=" | "+=" | "-=" | "*=" | "/=" | ":=" | "%=" | "^=" | "|=" | "&=" | "<<=" | ">>=" | "::=" | ">:=" | "<:=" | "-:=")

  def ident2: Parser[String] = rep1sep(subident, ".") ^^ { p => p.reduce(_ + "." + _) }
  def subident: Parser[String] = opt("$") ~ ident ^^ { case _1 ~ _2 => _1.getOrElse("") + _2 }
  def subident2: Parser[String] = "$" ~ ident ^^ { case _1 ~ _2 => _1 + _2 }
  def identLazy: Parser[String] = subident ~ rep(subident2) ^^ { p => (p._1 :: p._2).reduce(_ + _) }
  def identLazyForce: Parser[String] = "$"~>ident^^ {"$"+_}
  def identLazy2: Parser[String] = rep1sep(identLazy, ".") ^^ { p => p.reduce(_ + "." + _) }
  def identLazyCMD: Parser[String] = rep1sep(subident, ".") ^^ { p => p.reduce(_ + "." + _) }
  def identTag: Parser[String] = ("@" | "@e" | "@a" | "@s" | "@r" | "@p") ~ identLazy2 ^^ { case a ~ b => f"$a$b" }
  def identFunction: Parser[String] = identTag | identLazy2


  def floatValue: Parser[Double] = (numericLit <~ ".") ~ numericLit ^^ { p => (p._1 + "." + p._2).toDouble } 
  def jsonValueStr: Parser[String] = (floatValue ^^ { p => p.toString() } 
                                    | numericLit | stringLit2 ^^ (Utils.stringify(_)) | jsonStr 
                                    | ("[" ~> repsep(jsonValueStr, ",")) <~ "]" ^^ { p => "[" + p.reduce(_ + ","+ _) + "]" }
                                    )^^ { p => p.toString }
  def jsonKeypairStr: Parser[String] = ((stringLit2 ^^ (Utils.stringify(_)) | identLazy) <~ ":") ~ jsonValueStr ^^ { p => p._1 + ":" + p._2 } |
                                        ("§§§" ~> stringLit <~ "§§§") ^^ { p => p.replaceAll("@",":") }
  def jsonStr: Parser[String] = "{" ~> rep1sep(jsonKeypairStr, ",") <~ "}" ^^ { p => "{" + p.reduce(_ + ","+ _) + "}" }


  def jsonValue: Parser[JSONElement] = exprNoTuple ~ opt(ident) ^^ { case p ~ t => Utils.simplifyJsonExpression(JsonExpression(p, t.getOrElse(null))) }
                                    | json

  def jsonArray: Parser[JSONElement] = ("[" ~> repsep(jsonValue, ",")) <~ "]" ^^ { p => JsonArray(p)}
  def jsonKeypair: Parser[(String, JSONElement)] = ((stringLit2 | identLazy) <~ ":") ~ jsonValue ^^ { p => (p._1, p._2) } |
                                                  ("§§§" ~> stringLit <~ "§§§") ^^ { p => {val q = p.replaceAll("@",":").split(":");(q(0), JsonExpression(parseExpression(q(1), true), null))} }
  def jsonDic: Parser[JSONElement] = "{" ~> repsep(jsonKeypair, ",") <~ "}" ^^ { p => JsonDictionary(p.toMap) }
  def json: Parser[JSONElement] = jsonDic | jsonArray



  def argument: Parser[Argument] = types ~ identLazy ~ opt("=" ~> exprNoTuple) ^^ { p => Argument(p._1._2, p._1._1, p._2) }
  def arguments: Parser[List[Argument]] = "(" ~> repsep(argument, ",") <~ ")"

  def instruction: Parser[Instruction] = positioned(
      functionDecl
      | functionCall
      | selectorFunctionCall
      | sleepInstr
      | awaitInstr
      | assertInstr
      | packageInstr
      | structDecl
      | caseStruct
      | classDecl
      | interfaceDecl
      | caseClassDecl
      | templateUse
      | propertyDeclaration
      | varDeclaration
      | varAssignment
      | arrayAssign
      | "%%%" ~> stringLit2 <~ "%%%" ^^ { p => CMD(p) }
      | ifs
      | "return" ~> opt(expr) ^^ {case e => Return(e.getOrElse(NullValue))}
      | block
      | switch | whileLoop | doWhileLoop | forLoop | repeatLoop | jsonFile
      | "as" ~"("~> exprNoTuple ~")"~ instruction ^^ {case e ~ _ ~ i => With(e, BoolValue(false), BoolValue(true), i, null)}
      | "at" ~"(" ~> repsep(exprNoTuple, ",") ~ ")"~ instruction ^^ {case e ~ _ ~ i => Execute(AtType, e, i)}
      | rotated1
      | rotated2
      | facing1
      | facing2
      | align
      | withInstr
      | enumInstr
      | forgenerate
      | importShortInst
      | importInst
      | fromImportInst
      | templateDesc
      | typedef
      | foreach
      | predicate
      | blocktag
      | throwError
      | tryCatchFinalyBlock
      | constructorCall ^^ {case c => FreeConstructorCall(c)}
  )

  def functionDecl: Parser[FunctionDecl] = 
    ((((doc ~ ("def" ~> modifier("function_short"))))) ~ arguments) ~ instruction ^^ { case doc ~ mod ~ a ~i  => FunctionDecl("~", i, VoidType, a, List(), mod.withDoc(doc)) }
      | ((((doc ~ ("def" ~> modifier("abstract_function"))) ~ identLazy ~ typeArgument)) ~ arguments) ^^ { case doc ~ mod ~ n ~ at ~ a => FunctionDecl(n, InstructionList(List()), VoidType, a, at, mod.withDoc(doc)) }
      | ((((doc ~ (opt("def") ~> modifier("abstract_function"))) ~ types) ~ identLazy ~ typeArgument) ~ arguments) ^^ { case doc ~ mod ~ t ~ n ~ at ~ a  => FunctionDecl(n, InstructionList(List()), t, a, at, mod.withDoc(doc)) }
      | ((((doc ~ ("def" ~> modifier("function"))) ~ identLazy ~ typeArgument)) ~ arguments) ~ functionInstruction ^^ { case doc ~ mod ~ n ~ at ~ a ~ i => FunctionDecl(n, i, VoidType, a, at, mod.withDoc(doc)) }
      | ((((doc ~ (opt("def") ~> modifier("function"))) ~ types) ~ identLazy ~ typeArgument) ~ arguments) ~ functionInstruction ^^ { case doc ~ mod ~ t ~ n ~ at ~ a ~i  => FunctionDecl(n, i, t, a, at, mod.withDoc(doc)) }
  def functionInstruction: Parser[Instruction] = instruction | ("=" ~> exprNoTuple) ^^ (p => InstructionList(List(Return(p))))
  def functionCall: Parser[FunctionCall] = (((identFunction ~ typeVariables <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")") ~ block ^^ {case f ~ t ~ e ~ b => FunctionCall(f, e ::: List(LambdaValue(List(), b, null)), t)} // Function Call
      | (((identFunction ~ typeVariables <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")") ^^ {case f ~ t ~ e => FunctionCall(f, e, t)} // Function Call
      | (identFunction ~ typeVariables) ~ block ^^ {case f ~ t ~ b  => FunctionCall(f, List(LambdaValue(List(), b, null)), t)} // Function Call

  def selectorFunctionCall: Parser[Instruction] = selector ~ "." ~ functionCall ^^  {case s ~ _ ~ f => With(SelectorValue(s), BoolValue(false), BoolValue(true), f, null)}

  def continuation: Parser[Instruction] = positioned(opt(";") ~> rep(instruction <~ opt(";")) ^^ (p => InstructionList(p)))

  def sleepInstr: Parser[Instruction] = positioned(("sleep" ~> exprNoTuple ) ~ continuation ^^ {case e ~ instr => Sleep(e, instr)})
  def awaitInstr: Parser[Instruction] = positioned(("await" ~> functionCall ) ~ continuation ^^ {case e ~ instr => Await(e, instr)})
  def assertInstr: Parser[Instruction] = positioned(("assert" ~> exprNoTuple ) ~ continuation ^^ {case e ~ instr => Assert(e, instr)})

  def anyKeyword: Parser[String] = lexical.reserved.map(f => f ^^ (p => p)).reduce(_ | _)
  def throwError: Parser[Instruction] = positioned("throw"~exprNoTuple ^^ {case _ ~ e => Throw(e)})
  def tryCatchFinalyBlock: Parser[Instruction] = positioned("try"~>block~opt("catch"~>block)~opt("finally"~>block) ^^ {case b ~ c ~ f => Try(b, c.getOrElse(InstructionList(List())), f.getOrElse(InstructionList(List())))})

  def predicate:Parser[Instruction] = positioned(doc ~ (modifier("predicate") <~ "predicate") ~ identLazy ~ arguments ~ json ^^ {case doc ~ mod ~ name ~ args ~ json => PredicateDecl(name, args, json, mod.withDoc(doc))})
  def arrayAssign:Parser[Instruction] = positioned((identLazy2 <~ "[") ~ (rep1sep(exprNoTuple, ",") <~ "]") ~ assignmentOp ~ expr ^^ { case a ~ i ~ o ~ e => ArrayAssigment(Left(a), i, o, e) })
  def foreach: Parser[Instruction] = positioned((("foreach" ~ opt("(") ~> ident <~ "in") ~ exprNoTuple <~ opt(")")) ~ instruction ^^ { case v ~ e ~ i => ForEach(v, e, i) })
  def packageInstr: Parser[Instruction] = positioned("package" ~> identLazy2 ~ program ^^ (p => Package(p._1, p._2)))
  def interfaces: Parser[List[(String, List[Type])]] = opt("implements" ~> rep1sep(ident2 ~ typeVariables, ",")) ^^ {p => p.getOrElse(List()).map{ case i ~ t => (i, t) }}
  def classDecl: Parser[Instruction] = positioned(doc ~ (modifier("class") <~ "class") ~ identLazy ~ typeArgument ~ opt("extends" ~> ident2 ~ typeVariables) ~ interfaces ~ rep("with" ~> namespacedName ~ "for" ~ ident) ~ block ^^ 
  { case doc ~ mod ~ iden ~ typeargs ~ par ~ interface ~ entity ~ block => 
    ClassDecl(iden, typeargs, block, mod.withDoc(doc), 
    if par.isDefined then Some(par.get._1) else None,
    if par.isDefined then (par.get._2) else List(),
    interface,
     entity.map{ case e ~ _ ~ n => (n, e)}.toMap) })
  def interfaceDecl: Parser[Instruction] = positioned(doc ~ (modifier("interface") <~ "interface") ~ identLazy ~ typeArgument ~ opt("extends" ~> ident2 ~ typeVariables) ~ interfaces ~ block ^^ 
  { case doc ~ mod ~ iden ~ typeargs ~ par ~ interface ~ block => 
    ClassDecl(iden, typeargs, block, mod.withDoc(doc), 
    if par.isDefined then Some(par.get._1) else None,
    if par.isDefined then (par.get._2) else List(),
    interface, Map()) })
  def caseClassDecl: Parser[Instruction] = positioned(doc ~ (modifier("class") <~ opt("case") ~ "class") ~ identLazy ~ typeArgument ~ arguments ~ opt("extends" ~> ident2 ~ typeVariables) ~ interfaces ~ rep("with" ~> namespacedName ~ "for" ~ ident) ~ block ^^ {
    case doc ~ mod ~ iden ~ typeargs ~ arguments ~ par ~ interface ~ entity ~ block => 
      val fieldsDecl = arguments.map{ case Argument(n, t, _) => VariableDecl(List(n), t, Modifier.newPublic(), null, null) }
      val fields = arguments.map{ case Argument(n, t, _) => (Left[Identifier, Variable](Identifier.fromString(f"this.$n")), Selector.self) }
      val argu = TupleValue(arguments.map{ case Argument(n, t, _) => (VariableValue(n)) })
      var mod = Modifier.newPublic()
      mod.isLazy = true

      val constructor = FunctionDecl("__init__", VariableAssigment(fields, "=", argu), VoidType, arguments, List(), mod)

      ClassDecl(iden, typeargs, InstructionList(fieldsDecl ::: List(constructor, block)), mod.withDoc(doc), 
        if par.isDefined then Some(par.get._1) else None,
        if par.isDefined then (par.get._2) else List(),
        interface, entity.map{ case e ~ _ ~ n => (n, e)}.toMap)
  })

  def getCaseClassIntruction(args: List[Argument], rest: Option[Instruction]): Instruction ={
    var mod = Modifier.newPublic()
    mod.isLazy = true
    InstructionBlock(
      args.map(a => VariableDecl(List(a.name), a.typ, Modifier.newPublic(), null, null)) ::: 
      List(
        FunctionDecl("__init__", InstructionList(
        args.map(a => VariableAssigment(List((Left(Identifier.fromString(f"this.${a.name}")), Selector.self)), "=", VariableValue(a.name)))
        ), VoidType, args, List(), mod),

        rest.getOrElse(InstructionList(List())).unBlockify()
      )
    )
  }

  def caseStruct: Parser[Instruction] = positioned(doc ~ (modifier("struct") <~ opt("case") ~ "struct") ~ identLazy ~ typeArgument ~ arguments ~ opt("extends" ~> ident2) ~ opt(block) ^^ 
  { case doc ~ mod ~ iden ~ typeargs ~ args ~ par ~ block => StructDecl(iden, typeargs,  getCaseClassIntruction(args, block), mod.withDoc(doc), par) })
  def structDecl: Parser[Instruction] = positioned(doc ~ (modifier("struct") <~ "struct") ~ identLazy ~ typeArgument ~ opt("extends" ~> ident2) ~ block ^^ 
  { case doc ~ mod ~ iden ~ typeargs ~ par ~ block => StructDecl(iden, typeargs, block, mod.withDoc(doc), par) })
  def typedefInner: Parser[(String, Type, String)] = types ~ identLazy ~ opt("for" ~> identLazy) ^^ { case typ ~ str1 ~ str2 => (str1, typ, str2.getOrElse("")) }
  def typedef: Parser[Instruction] = positioned("typedef" ~> rep1sep(typedefInner, ",") ^^ { case t => TypeDef(t) })
  def templateUse: Parser[Instruction] = positioned(ident2 ~ typeArgumentExpression ~ identLazy ~ block ^^ {case iden ~ values ~ name ~ instr => TemplateUse(iden, name, instr, values)})
  def templateDesc: Parser[Instruction] = positioned(doc ~ (modifier("template") <~ "template") ~ identLazy ~ typeArgument ~ opt("extends" ~> (ident2 ~ typeArgumentExpression)) ~ instruction ^^ 
    {case doc ~ mod ~ name ~ generics ~ Some(parent ~ genericsParent) ~ instr => TemplateDecl(name, instr, mod.withDoc(doc), Some(parent), generics, genericsParent);
     case doc ~ mod ~ name ~ generics ~ None ~ instr => TemplateDecl(name, instr, mod.withDoc(doc), None, generics, List())})
  def importShortInst: Parser[Instruction] = positioned("import"~>ident2 ~ "::" ~ ident2 ~ opt("as" ~> ident2) ^^ {case file ~ _ ~ res ~ alias => Import(file, res, alias.getOrElse(null))})
  def importInst: Parser[Instruction] = positioned("import"~>ident2 ~ opt("as" ~> ident2) ^^ {case file ~ alias => Import(file, null, alias.getOrElse(null))})
  def fromImportInst: Parser[Instruction] = positioned("from"~>ident2 ~ ("import" ~> ident2) ~ opt("as" ~> ident2) ^^ {case file ~ res ~ alias => Import(file, res, alias.getOrElse(null))})
  def forgenerate: Parser[Instruction] = positioned((("forgenerate" ~> "(" ~> identLazy <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (p => ForGenerate(p._1._1, p._1._2, p._2)))
  def jsonFile: Parser[Instruction] = positioned(doc ~ modifier("jsonfile") ~ "jsonfile" ~ identLazy2 ~ expr ^^ {case d ~ m ~_ ~n~json => JSONFile(n, json, m.withDoc(d))})
  def doWhileLoop: Parser[Instruction] = positioned(("do" ~> instruction <~ "while") ~ ("(" ~> exprNoTuple <~ ")") ^^ (p => DoWhileLoop(p._2, p._1)))
  def whileLoop: Parser[Instruction] = positioned(("while" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ^^ (p => WhileLoop(p._1, p._2)))
  def forLoop: Parser[Instruction] = positioned(((("for" ~> "(" ~> instruction <~ ";") ~ exprNoTuple <~ ";") ~ instruction <~ ")") ~ instruction ^^ 
    (p => InstructionBlock(List(p._1._1._1, WhileLoop(p._1._1._2, InstructionList(List(p._2, p._1._2)))))))
  def repeatLoop: Parser[Instruction] = positioned(("repeat" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ^^ 
    {case value ~ intr => FunctionCall(Identifier.fromString("__repeat__"), List(value, LambdaValue(List(), intr, null)), List())})
  def withInstr: Parser[Instruction] = positioned(
    ("with" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ~ opt("else" ~> instruction) ^^ {case sel ~ intr ~ elze => With(sel, BoolValue(false), BoolValue(true), intr, elze.getOrElse(null))}
      | (("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ~ opt("else" ~> instruction) ^^ {case sel ~ isat ~ intr ~ elze => With(sel, isat, BoolValue(true), intr, elze.getOrElse(null))}
      | ((("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ~ opt("else" ~> instruction) ^^ {case sel ~ isat ~ cond ~ intr ~ elze => With(sel, isat, cond, intr, elze.getOrElse(null))})
  def switch: Parser[Switch] = positioned(("switch" ~> exprNoTuple <~ "{") ~ rep(switchCase) <~ "}" ^^ (p => Switch(p._1, p._2)))
  
  def switchIf: Parser[Expression] = opt("if" ~> exprNoTuple) ^^ (p => p.getOrElse(BoolValue(true)))
  def switchCaseBase: Parser[SwitchCase] = 
    (exprNoTuple ~ switchIf <~ (("-"~">")|"=>")) ~ instruction ^^ {case e ~ c ~ i => SwitchCase(e, i, c)} |
    ("case" ~> exprNoTuple ~ switchIf <~ ":") ~ instruction ^^ {case e ~ c ~ i => SwitchCase(e, i, c)} |
    ((("default" | "else") ~> switchIf <~ ":")) ~ instruction ^^ {case c ~ i => SwitchCase(DefaultValue, i, c)} |
    ((("default" | "else") ~> switchIf <~ (("-"~">")|"=>"))) ~ instruction ^^ {case c ~ i => SwitchCase(DefaultValue, i, c)}
    
  def switchCase: Parser[SwitchElement] = 
    switchCaseBase
    | positioned((("foreach" ~ opt("(") ~> ident <~ "in") ~ exprNoTuple <~ opt(")")) ~ (opt("{") ~> switchCaseBase <~ opt("}")) ^^ { case v ~ e ~ i => SwitchForEach(v, e, i) })
    | positioned((("forgenerate" ~> "(" ~> identLazy <~ ",") ~ exprNoTuple <~ ")") ~ (opt("{") ~> switchCaseBase <~ opt("}")) ^^ (p => SwitchForGenerate(p._1._1, p._1._2, p._2)))
  def rotated1: Parser[Instruction] = positioned("rotated" ~"("~ exprNoTuple ~","~ exprNoTuple~")" ~ instruction ^^ { case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ i => Execute(RotatedType, List(e1, e2), i) })
  def rotated2: Parser[Instruction] = positioned("rotated" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i => Execute(RotatedType, List(e), i) })
  def facing1: Parser[Instruction] = positioned("facing" ~"("~ exprNoTuple ~","~ exprNoTuple~")" ~ instruction ^^ { case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ i => Execute(FacingType, List(e1, e2), i) })
  def facing2: Parser[Instruction] = positioned("facing" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i => Execute(FacingType, List(e), i) })
  def align: Parser[Instruction] = positioned("align" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i => Execute(AlignType, List(e), i) })

  def blocktag: Parser[Instruction] = 
    positioned(doc ~ modifier("blocktag") ~ "blocktag" ~ identLazy2 ~ "{" ~ repsep(tagentry, ",") ~ "}" ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ => TagDecl(n, c, m.withDoc(d), objects.BlockTag)}) |
    positioned(doc ~ modifier("entitytag") ~ "entitytag" ~ identLazy2 ~ "{" ~ repsep(tagentry, ",") ~ "}" ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ => TagDecl(n, c, m.withDoc(d), objects.EntityTag)}) |
    positioned(doc ~ modifier("itemtag") ~ "itemtag" ~ identLazy2 ~ "{" ~ repsep(tagentry, ",") ~ "}" ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ => TagDecl(n, c, m.withDoc(d), objects.ItemTag)})
  def tagentry: Parser[Expression] = positioned(namespacedName | (identLazy2 ^^ (VariableValue(_))) | tagValue )


  def enumInstr: Parser[EnumDecl] = positioned((doc ~ modifier("enum") ~ ("enum" ~> identLazy) ~ opt("("~>repsep(enumField,",")<~")") <~ "{") ~ repsep(enumValue, ",") <~ "}" ^^ 
                                    { case doc ~ mod ~ n ~ f ~ v => EnumDecl(n, f.getOrElse(List()), v, mod.withDoc(doc)) })
  def enumField: Parser[EnumField] = types ~ identLazy ^^ { p => EnumField(p._2, p._1) }
  def enumValue: Parser[EnumValue] = identLazy ~ opt("("~>repsep(exprNoTuple,",")<~")") ^^ (p => EnumValue(p._1, p._2.getOrElse(List())))

  def varAssignment: Parser[Instruction] = positioned((rep1sep(opt(selector <~ ".") ~ identLazy2, ",") ~ assignmentOp ~ expr) ^^ {case id ~ op ~ expr => 
    {
      val identifiers = id.map(p => (Identifier.fromString(p._2), p._1.getOrElse(Selector.self)))
      VariableAssigment(identifiers.map((i,s) => (Left(i), s)), op, expr)
    }} |
    (rep1sep(opt(selector <~ ".") ~ identLazy2, ",") <~ ("+" ~ "+")) ^^ {case id => 
    {
      val identifiers = id.map(p => (Identifier.fromString(p._2), p._1.getOrElse(Selector.self)))
      VariableAssigment(identifiers.map((i,s) => (Left(i), s)), "+=", IntValue(1))
    }} |
    (rep1sep(opt(selector <~ ".") ~ identLazy2, ",") <~ ("-" ~ "-")) ^^ {case id => 
    {
      val identifiers = id.map(p => (Identifier.fromString(p._2), p._1.getOrElse(Selector.self)))
      VariableAssigment(identifiers.map((i,s) => (Left(i), s)), "-=", IntValue(1))
    }}
    )

  def singleVarAssignment: Parser[VariableAssigment] = positioned((opt(selector <~ ".") ~ identLazy2 ~ assignmentOp ~ expr) ^^ {case s ~ i ~ op ~ e => VariableAssigment(List((Left(i), s.getOrElse(Selector.self))), op, e)} |
    opt(selector <~ ".") ~ identLazy2 <~ ("+" ~ "+") ^^ {case s ~ i => VariableAssigment(List((Left(i), s.getOrElse(Selector.self))), "+=", IntValue(1))} |
    opt(selector <~ ".") ~ identLazy2 <~ ("-" ~ "-") ^^ {case s ~ i => VariableAssigment(List((Left(i), s.getOrElse(Selector.self))), "-=", IntValue(1))}
    )

  def varDeclaration: Parser[VariableDecl] = positioned((doc ~ modifier("variable") ~ types ~ rep1sep(identLazy, ",") ~ opt(assignmentOp ~ expr)) ^^ {
    case doc ~ mod1 ~ typ ~ names ~ expr => {
      val mod = mod1.withDoc(doc)
      val identifiers = names.map(Identifier.fromString(_))
      if (!mod.isEntity && expr.isEmpty){
        VariableDecl(names, typ, mod, ":=", DefaultValue)
      }
      else if (!expr.isEmpty){
        VariableDecl(names, typ, mod, expr.get._1, expr.get._2)
      }
      else{
        VariableDecl(names, typ, mod, null, null)
      }
    }
  }) // Variable Dec

  def propertyDeclaration: Parser[Instruction] = positioned(varDeclaration ~ "{" ~ rep1sep(("private" | "protected" |"public") ~ ident, (";"|",")) ~ opt(";" | ",") ~ "}" ^^ {
    case VariableDecl(v, typ, mod, op, expr) ~ _ ~ m ~ _ ~ _ => 
      var hasGet = m.exists{case _ ~ "get" => true; case _ => false}
      var hasSet = m.exists{case _ ~ "set" => true; case _ => false}

      val pp = v.map(x => {
        TemplateUse(Identifier.fromString("property"), x, InstructionBlock(List(
          if (hasGet)FunctionDecl("get", Return(VariableValue(Identifier.fromString("--"+x))), typ, List(), List(), mod)else EmptyInstruction,
          if (hasSet)FunctionDecl("set", VariableAssigment(List((Left(Identifier.fromString("--"+x)), Selector.self)), "=", VariableValue("value")), VoidType, List(Argument("value", typ, None)), List(), mod)else EmptyInstruction
          )), List())
      })
      InstructionList(List(VariableDecl(v.map(s => "--"+s), typ, mod, op, expr), InstructionList(pp)))
  })

  def ifs: Parser[If] = positioned(("if" ~> "(" ~> expr <~ ")") ~ instruction ~ 
      rep(("else" ~> "if" ~> "(" ~> expr <~ ")") ~ instruction) ~
      opt("else" ~> instruction) ^^ {p => 
        {
          val elze = p._1._2.map(k => ElseIf(k._1, k._2)) ::: (if p._2.isEmpty then Nil else List(ElseIf(BoolValue(true), p._2.get)))
          If(p._1._1._1, p._1._1._2, elze)
        }
      })



  def lambda1: Parser[Expression] = positioned((identLazy2 <~ "=>") ~ instruction ^^ (p => LambdaValue(List(p._1), p._2, null)))
  def lambda2: Parser[Expression] = positioned(("(" ~> repsep(identLazy2, ",") <~ ")" <~ "=>") ~ instruction ^^ (p => LambdaValue(p._1, p._2, null)))
  def lambda = lambda1 | lambda2

  def sfField: Parser[(String, SelectorFilterValue)] = ident ~ "=" ~ selectorFilterInnerValue ^^ {case n ~ _ ~ v => (n, v)}
  def sfCompound: Parser[SelectorFilterValue] = "{" ~> rep1sep(sfField, ",") <~ "}" ^^ {case a => SelectorComposed(a.toMap)}
  def sfNumber: Parser[SelectorFilterValue] = floatValue ^^ (SelectorNumber(_))
  def sfNumber2: Parser[SelectorFilterValue] = numericLit ^^ (p => SelectorNumber(p.toInt))
  def sfNumber3: Parser[SelectorFilterValue] = "-" ~> floatValue ^^ (p=>SelectorNumber(-p))
  def sfNumber4: Parser[SelectorFilterValue] =  "-" ~> numericLit ^^ (p => SelectorNumber(f"-$p".toInt))
  def sfString: Parser[SelectorFilterValue] = stringLit2 ^^ (SelectorString(_))
  def sfIdentifier: Parser[SelectorFilterValue] = identLazy2 ^^ (SelectorIdentifier(_))
  def sfTag: Parser[SelectorFilterValue] = tagValue ^^ {case TagValue(v) => SelectorTag(v)}
  def sfNamespacedName: Parser[SelectorFilterValue] = namespacedName ^^ (p => SelectorIdentifier(p.toString()))
  def sfNBT: Parser[SelectorFilterValue] = json ^^ (SelectorNbt(_))
  def selectorFilterInnerValue2 = sfNumber | sfNumber3 | sfString | sfNamespacedName | sfIdentifier | sfNumber2 | sfNumber4 | sfNBT | sfCompound | sfTag
  def selectorFilterInnerValue = opt(selectorFilterInnerValue2) ~ opt(".." ~ opt(selectorFilterInnerValue2)) ^^ { 
    case Some(a) ~ Some(b, Some(c)) => SelectorRange(a, c)
    case Some(a) ~ Some(b, None) => SelectorGreaterRange(a)
    case Some(a) ~ None => a
    case None ~ Some(b, Some(c)) => SelectorLowerRange(c)
    case other => throw new Exception(f"Invalid Selector filter: $other")
    }

  def selectorFilterValue: Parser[SelectorFilterValue] = "!" ~> selectorFilterInnerValue ^^ (SelectorInvert(_)) |
                                              selectorFilterInnerValue

  def selectorFilter: Parser[(String, SelectorFilterValue)] = (identLazy <~ "=") ~ selectorFilterValue ^^ { p => (p._1, p._2) }
  def selector: Parser[Selector] = ("@a" | "@s" | "@e" | "@p" | "@r") ~ opt("[" ~> rep1sep(selectorFilter, ",") <~ "]") ^^ { p => Selector.parse(p._1, p._2.getOrElse(List())) }
  def selectorStr : Parser[String] = (selector ^^ (_.toString()))

  def stringLit2: Parser[String] = stringLit ^^ {p => p.replaceAllLiterally("◘", "\\\"")}
  def anyWord = lexical.reserved.foldLeft(ident2){(a, b) => a | b} | ident
  def blockDataField = anyWord ~ "=" ~ exprNoTuple ^^ {case n ~ _ ~ v => n +"="+v }
  def blockData = "[" ~> rep1sep(blockDataField, ",") <~ "]" ^^ {case fields => fields.mkString("[", ",", "]")}
  def namespacedName = identLazy ~":"~identLazy2 ~ opt(blockData) ~ opt(json) ^^ { case a ~ _ ~ b ~ d ~ j=> NamespacedName(a+":"+b+d.getOrElse(""), JsonValue(j.getOrElse(JsonNull))) }
  def namespacedName2 = opt(identLazy <~ ":") ~ identLazy2 ^^ { case a ~ c => if a.isEmpty then NamespacedName(c, JsonValue(JsonNull)) else NamespacedName(a.get+":"+c, JsonValue(JsonNull))}

  def validCordNumber1: Parser[Expression] = floatValue ^^ {FloatValue(_)} | (numericLit ^^ {i => IntValue(i.toInt)}) | (identLazyForce ^^ {VariableValue(_)})
  def validCordNumber2: Parser[Expression] = 
    floatValue ^^ (p => FloatValue(p))
    | numericLit ^^ (p => IntValue(p.toInt))
    | identLazyForce ^^ {VariableValue(_)}
    | "-" ~> floatValue ^^ (p => FloatValue(-p))
    | "-" ~> numericLit ^^ (p => IntValue(f"-$p".toInt))
    | "-" ~> identLazyForce ^^ {s => BinaryOperation("+", StringValue("-"), VariableValue(s))}
  def relCoordinate1: Parser[Expression] = "~"~>validCordNumber2 ^^ {BinaryOperation("+", StringValue("~"), _)}
  def relCoordinate2: Parser[StringValue] = "~" ^^^ StringValue("~")
  def relCoordinate: Parser[Expression] = relCoordinate1 | relCoordinate2 | validCordNumber2
  def frontCoordinateNumber: Parser[Expression] = "^"~>validCordNumber2 ^^ {{BinaryOperation("+", StringValue("^"), _)}}
  def frontCoordinateHere: Parser[StringValue] = "^" ^^^ StringValue("^")
  def frontCoordinate: Parser[Expression] = frontCoordinateNumber | frontCoordinateHere

  def relPositionCase1: Parser[PositionValue] = relCoordinate2 ~ validCordNumber2 ~ relCoordinate ^^ {case x ~ y ~ z => PositionValue(x, y, z)}
  def relPositionCase2: Parser[PositionValue] = relCoordinate ~ relCoordinate2 ~ validCordNumber2 ^^ {case x ~ y ~ z => PositionValue(x, y, z)}

  def frontPosition: Parser[PositionValue] = frontCoordinate ~ frontCoordinate ~ frontCoordinate ^^ {case x ~ y ~ z => PositionValue(x, y, z)}
  def relPosition: Parser[PositionValue] = relCoordinate ~ relCoordinate ~ relCoordinate ^^ {case x ~ y ~ z => PositionValue(x, y, z)}
  def position: Parser[PositionValue] = (frontPosition | relPosition | relPositionCase1 | relPositionCase2)



  def relCoordinate1Expr: Parser[Expression] = "~"~>exprAs ^^ {BinaryOperation("+", StringValue("~"), _)}
  def relCoordinate2Expr: Parser[StringValue] = "~" ^^^ StringValue("~")
  def relCoordinateExpr: Parser[Expression] = relCoordinate1Expr | relCoordinate2Expr | exprAs
  def frontCoordinateNumberExpr: Parser[Expression] = "^"~>exprAs ^^ {{BinaryOperation("+", StringValue("^"), _)}}
  def frontCoordinateHereExpr: Parser[StringValue] = "^" ^^^ StringValue("^")
  def frontCoordinateExpr: Parser[Expression] = frontCoordinateNumberExpr | frontCoordinateHereExpr

  def relPositionCase1Expr: Parser[PositionValue] = relCoordinate2Expr ~ "," ~ exprAs ~ "," ~ relCoordinateExpr ^^ {case x ~ _ ~ y ~ _ ~ z => PositionValue(x, y, z)}
  def relPositionCase2Expr: Parser[PositionValue] = relCoordinateExpr ~ "," ~ relCoordinate2Expr ~ "," ~ exprAs ^^ {case x ~ _ ~ y ~ _ ~ z => PositionValue(x, y, z)}

  def frontPositionExpr: Parser[PositionValue] = frontCoordinateExpr ~ "," ~ frontCoordinateExpr ~ "," ~ frontCoordinateExpr ^^ {case x ~  _ ~ y~ _ ~ z => PositionValue(x, y, z)}
  def relPositionExpr: Parser[PositionValue] = relCoordinateExpr ~ "," ~ relCoordinateExpr ~ "," ~ relCoordinateExpr ^^ {case x ~ _ ~ y ~ _ ~ z => PositionValue(x, y, z)}
  def positionExpr: Parser[PositionValue] = "{" ~> (frontPositionExpr | relPositionExpr | relPositionCase1Expr | relPositionCase2Expr) <~ "}"




  def tagValue: Parser[TagValue] = "#" ~> ident2 ~ opt(":" ~ ident2) ^^ {case a ~ Some(_ ~ b) => TagValue(a+":"+b); case a ~ None => TagValue(a)}

  def constructorCall: Parser[ConstructorCall] = 
    "new" ~> typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ { case t ~ a => ConstructorCall("@@@", a, t) }
    | "new" ~> nonRecTypes ~ ("[" ~> repsep(exprNoTuple, ",") <~ "]") ^^ { case t ~ a => ConstructorCall("standard.array.Array", a, List(t)) }
    | "new" ~> identLazy2 ~ typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ t ~ a ~ b => ConstructorCall(f, a ::: List(LambdaValue(List(), b, null)), t) }
    | "new" ~> identLazy2 ~ typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ { case f ~ t ~ a => ConstructorCall(f, a, t) }

  def exprBottom: Parser[Expression] = positioned(
    floatValue ^^ (p => FloatValue(p))
    | numericLit ^^ (p => IntValue(p.toInt))
    | "-" ~> floatValue ^^ (p => FloatValue(-p))
    | "-" ~> numericLit ^^ (p => IntValue(f"-$p".toInt))
    | "-" ~> exprBottom ^^ (BinaryOperation("-", IntValue(0), _))
    | "!" ~> exprBottom ^^ (UnaryOperation("!", _))
    | "true" ^^^ BoolValue(true)
    | "false" ^^^ BoolValue(false)
    | "null" ^^^ NullValue
    | singleVarAssignment ^^ {p => SequenceValue(p, VariableValue(p.name.head._1.left.get, p.name.head._2))}
    | tagValue
    | namespacedName
    | "$" ~ stringLit2 ^^ {case _ ~ s => InterpolatedString.build(s)}
    | stringLit2 ^^ (StringValue(_))
    | identLazy2 ~ selector ^^ { case id ~ sel => BinaryOperation("in", SelectorValue(sel), VariableValue(id)) }
    | constructorCall
    | exprArray
    | identifierExpr
    | "(" ~> expr <~ ")"
    | json ^^ (JsonValue(_))
    | selector ^^ (SelectorValue(_))
    
  )
  def identifierExpr: Parser[Expression] = identLazy2 ~ typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ t ~ a ~ b => FunctionCallValue(VariableValue(f), a ::: List(LambdaValue(List(), b, null)), t) }
    | identLazy2 ~ rep1((typeVariables <~"(") ~ repsep(exprNoTuple, ",") <~ ")") ^^ { case f ~ a => a.foldLeft[Expression](VariableValue(f))((b, a) => FunctionCallValue(b, a._2, a._1)) }
    | identLazy2 ^^ (VariableValue(_))
    | selector ~ "." ~ identLazy2 ^^ { case s ~ _ ~ n => VariableValue(n, s) }
    | identTag ^^ (VariableValue(_))
  def exprArray: Parser[Expression] = positioned((identifierExpr | "(" ~> expr <~ ")") ~ rep("[" ~> rep1sep(expr, ",") <~ "]") ^^ {case e ~ g => g.foldLeft(e)((e, i) => ArrayGetValue(e, i))})

  def comparator: Parser[String] = "<" | "<=" | ">=" | ">" | "==" | "!="

  def typeVariablesForce = "<" ~ repsep(types,",") ~ ">" ^^ {case _ ~ a ~ _ => a}
  def typeVariables = opt(typeVariablesForce) ^^ {case Some(a) => a;case None => List()}
  def typeArgument = opt("<" ~ repsep(ident,",") ~ ">") ^^ {case Some(_ ~ a ~ _) => a;case None => List()}
  def typeArgumentExpression= opt("<" ~ repsep(exprBottom,",") ~ ">") ^^ {case Some(_ ~ a ~ _) => a; case None => List()}

  def exprDot: Parser[Expression] = positioned(rep1sep(exprBottom, ".") ^^ { case e if e.size == 1 => e.head; case e => e.tail.foldLeft(e.head)((p, n) => DotValue(p, n))})
  def ternaryOperator: Parser[Expression] = 
    positioned(exprDot ~! opt(("?") ~>! exprNoTuple ~! ":" ~! exprNoTuple) ^^ {case e1 ~ Some(e2 ~ _ ~ e3) => TernaryOperation(e1, e2, e3); case e1 ~ None => e1} |||
              exprDot ~ opt(("?") ~> identifierExpr ~ ":" ~ exprNoTuple) ^^ {case e1 ~ Some(e2 ~ _ ~ e3) => TernaryOperation(e1, e2, e3); case e1 ~ None => e1} |
              exprDot ~ opt(("if") ~> exprNoTuple ~ ("else") ~ exprNoTuple) ^^ {case e1 ~ Some(e2 ~ _ ~ e3) => TernaryOperation(e2, e1, e3); case e1 ~ None => e1})
  def exprRange: Parser[Expression] = positioned(ternaryOperator ~ opt(".."~>ternaryOperator ~ opt("by"~>ternaryOperator)) ^^ { case e ~ None => e; case e1 ~ Some(e2 ~ None) => RangeValue(e1, e2, IntValue(1)); case e1 ~ Some(e2 ~ Some(e3)) => RangeValue(e1, e2, e3)})
  def exprPow: Parser[Expression] = positioned(exprRange ~ rep("^" ~> exprPow) ^^ {unpack("^", _)})
  def exprMod: Parser[Expression] = positioned(exprPow ~ rep("%" ~> exprMod) ^^ {unpack("%", _)})
  def exprDiv: Parser[Expression] = positioned(exprMod ~ rep(("/" | "\\") ~> exprDiv) ^^ {unpack("/", _)})
  def exprMult: Parser[Expression] = positioned(exprDiv ~ rep("*" ~> exprMult) ^^ {unpack("*", _)})
  def exprSub: Parser[Expression] = positioned(exprMult ~ rep("-" ~> exprSub) ^^ {unpack("-", _)})
  def exprAdd: Parser[Expression] = positioned(exprSub ~ rep("+" ~> exprAdd) ^^ {unpack("+", _)})
  def exprComp: Parser[Expression] = positioned(exprAdd ~ rep(comparator ~ exprComp) ^^ {unpack(_)})
  def exprAppend: Parser[Expression] = positioned(exprComp ~ rep(">:" ~> exprAppend) ^^ {unpack(">:", _)})
  def exprPrepend: Parser[Expression] = positioned(exprAppend ~ rep("<:" ~> exprPrepend) ^^ {unpack("<:", _)})
  def exprMerge: Parser[Expression] = positioned(exprPrepend ~ rep("::" ~> exprMerge) ^^ {unpack("::", _)})
  def exprNotIn: Parser[Expression] = positioned(exprMerge ~ rep(("not" | "!") ~> "in" ~> exprNotIn) ^^ {unpack("not in", _)})
  def exprIn: Parser[Expression] = positioned(exprNotIn ~ rep("in" ~> exprIn) ^^ {unpack("in", _)})
  def exprIs: Parser[Expression] = positioned((exprIn ~ opt("is" ~ types)) ^^ {case e ~ Some(_ ~ t) => IsType(e, t);case e ~ None => e})
  def exprIsNot: Parser[Expression] = positioned((exprIs ~ opt("is" ~ "not" ~ types)) ^^ {case e ~ Some(_ ~ t) => UnaryOperation("!", IsType(e, t));case e ~ None => e})
  def exprShiftRight: Parser[Expression] = positioned(exprIsNot ~ rep(">" ~> ">" ~> exprShiftRight) ^^ {unpack(">>", _)})
  def exprShiftLeft: Parser[Expression] = positioned(exprShiftRight ~ rep("<" ~> "<" ~> exprShiftLeft) ^^ {unpack("<<", _)})
  def exprBitwiseAnd: Parser[Expression] = positioned(exprShiftLeft ~ rep("&" ~> exprBitwiseAnd) ^^ {unpack("&", _)})
  def exprBitwiseOr: Parser[Expression] = positioned(exprBitwiseAnd ~ rep("|" ~> exprBitwiseOr) ^^ {unpack("|", _)})
  def exprAnd: Parser[Expression] = positioned(exprBitwiseOr ~ rep("&&" ~> exprAnd) ^^ {unpack("&&", _)})
  def exprOr: Parser[Expression] = positioned(exprAnd ~ rep("||" ~> exprOr) ^^ {unpack("||", _)})
  def exprNullCoalesing: Parser[Expression] = positioned(exprOr ~ rep("??" ~> exprNullCoalesing) ^^ {unpack("??", _)})
  def exprAs: Parser[Expression] = positioned(exprNullCoalesing ~ rep(":>" ~> types) ^^ {unpackCast(_)})
  def exprForSelect: Parser[Expression] = positioned(exprAs ~ rep("for" ~> (ident2 | stringLit) ~ "in" ~ exprNoTuple) ^^ {unpackForSelect(_)})
  def exprNoTuple = lambda | position | exprForSelect | positionExpr
  def expr: Parser[Expression] = positioned(rep1sep(exprNoTuple, ",") ^^ (p => if p.length == 1 then p.head else TupleValue(p)))


  def unpackCast(p: (Expression ~ List[Type])): Expression = {
    if p._2.isEmpty then p._1 else CastValue(p._1, p._2.last)
  }

  def unpackForSelect(p: (Expression ~ List[String ~ String ~ Expression])): Expression = {
    if p._2.isEmpty then p._1 else p._2.foldLeft(p._1)((e, p) => ForSelect(e, p._1._1, p._2))
  }

  def unpack(op: String, p: (Expression ~ List[Expression])): Expression = {
    if p._2.isEmpty then p._1 else p._2.foldLeft(p._1)(BinaryOperation(op, _, _))
  }

  def unpack(p: (Expression ~ List[String ~ Expression])): Expression = {
    if p._2.isEmpty then p._1 else p._2.foldLeft(p._1)((e, p) => BinaryOperation(p._1, e, p._2))
  }


  def identifierType(string: String) = {
    string match{
        case "bool" => BoolType
        case "int" => IntType
        case "float" => FloatType
        case "void" => VoidType
        case "string" => StringType
        case "json" => JsonType
        case "entity" => EntityType
        case "mcobject" => MCObjectType
        case "mcposition" => MCPositionType
        case "params" => ParamsType
        case "rawjson" => RawJsonType
        case other => IdentifierType(other, List())
    }
  }
  
  def nonRecTypes: Parser[Type] = 
    ident2 ~ typeVariablesForce ^^ { case n ~ t => IdentifierType(n, t) } |
    ident2 ^^ { identifierType(_) } |
    (("(" ~> types) ~ rep("," ~> types)) <~ ")" ^^ (p => if p._2.length > 0 then TupleType(p._1 :: p._2) else p._1)

  def types: Parser[Type] = ("(" ~> repsep(nonRecTypes, ",") <~ ")"<~ "=>") ~ types ^^ (p => FuncType(p._1, p._2)) |
                            (nonRecTypes <~ "=>") ~ types ^^ (p => FuncType(List(p._1), p._2)) |
                            ((nonRecTypes <~ "[") ~ expr) <~ "]" ^^ (p => ArrayType(p._1, p._2)) |
                            ((nonRecTypes <~ "[" ~ "]")) ^^ (p => ArrayType(p, null)) |
                            nonRecTypes

  def modifierSub(sel: String): Parser[String] = {
    sel match{
      case "all" => ("abstract" | "override" | "virtual" |"lazy" | "macro" | "scoreboard" | "ticking" | "loading" | "helper" | "static" | "const" | "async")
      case "function" => ("abstract" | "override" | "virtual" | "lazy" | "macro" | "ticking" | "loading" | "helper" | "static" | "const" | "async")
      case "function_short" => ("lazy" | "macro" | "ticking" | "loading" | "helper" | "static" | "const" | "async")
      case "variable" => ("lazy" | "macro" | "scoreboard" | "static" | "const")
      case "class" => ("abstract" | "static")
      case "struct" => ("abstract" | "static")
      case "enum" => ("abstract" | "static")
      case _ => ("static")
    }
  }

  def modifierAttribute: Parser[(String,Expression)] = ident2 ~ "=" ~ exprNoTuple ^^ {case i ~ _ ~ e => (i, e)}
  def modifierAttributes: Parser[Map[String,Expression]] = opt("[" ~> rep1sep(modifierAttribute,",") <~"]") ^^ {(_.getOrElse(List()).toMap)}
  def modifier(sel: String): Parser[Modifier] = 
    (sel match{
      case "abstract_function" => (opt(stringLit2 ~ stringLit2 ~ stringLit2) ~ modifierAttributes ~ opt("public" | "private" | "protected") ~ ("abstract" ~> rep(modifierSub("function"))) ~ rep(identTag))
      case other => (opt(stringLit2 ~ stringLit2 ~ stringLit2) ~ modifierAttributes ~ opt("public" | "private" | "protected") ~ rep(modifierSub(sel)) ~ rep(identTag))
    }) ^^ 
    { case doc ~ attributes ~ protection ~ subs ~ tags => {
      val mod = new Modifier()
      mod.tags.addAll(tags)
      mod.attributes = attributes
      protection match{
        case Some("public") => mod.protection = Protection.Public
        case Some("private") => mod.protection = Protection.Private
        case Some("protected") => mod.protection = Protection.Protected
        case _ => {}
      }

      if subs.contains("virtual")  || sel == "abstract_function" then {mod.isVirtual = true}
      if subs.contains("abstract") || sel == "abstract_function" then {mod.isAbstract = true}
      if subs.contains("override")  then {mod.isOverride = true}
      if subs.contains("lazy")      then {mod.isLazy = true}
      if subs.contains("macro")     then {mod.isMacro = true}
      if subs.contains("scoreboard")then {mod.isEntity = true}
      if subs.contains("ticking")   then {mod.isTicking = true}
      if subs.contains("loading")   then {mod.isLoading = true}
      if subs.contains("helper")    then {mod.isHelper = true}
      if subs.contains("static")    then {mod.isStatic = true}
      if subs.contains("const")     then {mod.isConst = true}
      if subs.contains("async")     then {mod.isAsync = true}

      mod
    }
  }

  def doc: Parser[Option[String]] = opt("???"~>stringLit<~"???")

  def program: Parser[Instruction] = positioned(rep(instruction <~ opt(";")) ^^ (InstructionList(_)))

  /** Print an error message, together with the position where it occured. */
  case class TypeError(t: Instruction, msg: String) extends Exception(msg) {
    override def toString =
      msg + "\n" + t
  }

  def parseFromFile(f: String, get: ()=>String) : Instruction ={
    if (CacheAST.contains(f)){
      CacheAST.get(f)
    }
    else{
      val ast = parse(f, get())
      CacheAST.add(f, ast.get)
      ast.get
    }
  }
  def parse(file: String, args: String): Option[Instruction] = {
    val tokens = new lexical.Scanner(Preparser.parse(file, args))
    phrase(program)(tokens) match {
      case Success(trees, _) =>
        Reporter.ok(f"Parsed: $file")
        Some(StaticAnalyser.handleSleep(StaticAnalyser.check(trees)))
      case e =>
        println(f"Error in file '${file}'': ${e}")
        None
    }
  }
  def parse(string: String): Instruction = {
    val tokens = new lexical.Scanner(Preparser.parse("",string))
    phrase(program)(tokens) match {
      case Success(trees, _) =>
        StaticAnalyser.check(trees)
      case e =>
        println(f"Error in file '${string}'': ${e}")
        null
    }
  }
  def parseJson(file: String): JSONElement = {
    val preparsed= Preparser.parse("", file)
    val tokens = new lexical.Scanner(preparsed)
    phrase(json)(tokens) match {
      case Success(trees, _) =>
        trees
      case e =>
        println(f"Error in file '${file}': ${e}")
        null
    }
  }
  def parseExpression(file: String, silent: Boolean = false): Expression = {
    val tokens = new lexical.Scanner(Preparser.parse(file, file, silent))
    phrase(expr)(tokens) match {
      case Success(trees, _) =>
        if (!silent){
          Reporter.ok(f"Parsed: $file")
        }
        trees
      case e =>
        println(f"Error in file '${file}'': ${e}")
        null
    }
  }
}
