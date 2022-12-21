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
import javax.swing.text.DefaultEditorKit.PasteAction

object Parser extends StandardTokenParsers{
  lexical.delimiters ++= List("(", ")", "\\", ".", "..", ":", "=", "->", "{", "}", ",", "*", "[", "]", "/", "+", "-", "*", "/", "\\", "%", "&&", "||", "=>", ";",
                              "+=", "-=", "/=", "*=", "%=", "?=", ":=", "%", "@", "@e", "@a", "@s", "@r", "@p", "~", "^", "<=", "==", ">=", "<", ">", "!=", "%%%", "???", "$",
                              "!", "!=", "#")
  lexical.reserved   ++= List("true", "false", "if", "then", "else", "return", "switch", "for", "do", "while",
                              "as", "at", "with", "to", "import", "doc", "template", "null", "typedef", "foreach", "in",
                              "def", "package", "struct", "enum", "class", "lazy", "jsonfile", "blocktag",
                              "public", "protected", "private", "scoreboard", "forgenerate", "from",
                              "ticking", "loading", "predicate", "extends", "new", "const", "static", "virtual", "abstract", "override")


  def block: Parser[Instruction] = "{" ~> repsep(instruction, opt(";")) <~ "}" ^^ (p => InstructionBlock(p))
  def assignmentOp: Parser[String] = ("=" | "+=" | "-=" | "*=" | "/=" | ":=" | "%=")

  def ident2: Parser[String] = rep1sep(ident, ".") ^^ { p => p.reduce(_ + "." + _) }
  def subident: Parser[String] = opt("$") ~ ident ^^ { case _1 ~ _2 => _1.getOrElse("") + _2 }
  def subident2: Parser[String] = "$" ~ ident ^^ { case _1 ~ _2 => _1 + _2 }
  def identLazy: Parser[String] = subident ~ rep(subident2) ^^ { p => (p._1 :: p._2).reduce(_ + _) }
  def identLazy2: Parser[String] = rep1sep(identLazy, ".") ^^ { p => p.reduce(_ + "." + _) }
  def identLazyCMD: Parser[String] = rep1sep(subident, ".") ^^ { p => p.reduce(_ + "." + _) }
  def identTag: Parser[String] = ("@" | "@e" | "@a" | "@s" | "@r" | "@p") ~ identLazy2 ^^ { case a ~ b => f"$a$b" }
  def identFunction: Parser[String] = identTag | identLazy2


  def floatValue: Parser[Double] = (numericLit <~ ".") ~ numericLit ^^ { p => (p._1 + "." + p._2).toDouble } 
  def jsonValueStr: Parser[String] = (floatValue ^^ { p => p.toString() } 
                                    | numericLit | stringLit2 ^^ (Utils.stringify(_)) | jsonStr 
                                    | ("[" ~> repsep(jsonValueStr, ",")) <~ "]" ^^ { p => "[" + p.reduce(_ + ","+ _) + "]" }
                                    )^^ { p => p.toString }
  def jsonKeypairStr: Parser[String] = ((stringLit2 ^^ (Utils.stringify(_)) | identLazy) <~ ":") ~ jsonValueStr ^^ { p => p._1 + ":" + p._2 }
  def jsonStr: Parser[String] = "{" ~> rep1sep(jsonKeypairStr, ",") <~ "}" ^^ { p => "{" + p.reduce(_ + ","+ _) + "}" }


  def jsonValue: Parser[JSONElement] = floatValue ^^ { p => JsonFloat(p) } 
                                    | numericLit ^^ { p => JsonInt(p.toInt) } 
                                    | stringLit2 ^^ { p => JsonString(p) }
                                    | jsonCall
                                    | identLazy2 ^^ { p => JsonIdentifier(p) }
                                    | "true" ^^^ (JsonBoolean(true))
                                    | "false" ^^^ (JsonBoolean(false))
                                    | json

  def jsonCall: Parser[JSONElement] = identLazy2 ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ {case f ~ a => JsonCall(f, a)}
  def jsonArray: Parser[JSONElement] = ("[" ~> repsep(jsonValue, ",")) <~ "]" ^^ { p => JsonArray(p)}
  def jsonKeypair: Parser[(String, JSONElement)] = ((stringLit2 | identLazy) <~ ":") ~ jsonValue ^^ { p => (p._1, p._2) }
  def jsonDic: Parser[JSONElement] = "{" ~> repsep(jsonKeypair, ",") <~ "}" ^^ { p => JsonDictionary(p.toMap) }
  def json: Parser[JSONElement] = jsonDic | jsonArray



  def argument: Parser[Argument] = types ~ identLazy ~ opt("=" ~> exprNoTuple) ^^ { p => Argument(p._1._2, p._1._1, p._2) }
  def arguments: Parser[List[Argument]] = "(" ~> repsep(argument, ",") <~ ")"

  def instruction: Parser[Instruction] = 
      ((((doc ~ ("def" ~> modifier)) ~ identLazy)) ~ arguments) ~ instruction ^^ { case doc ~ mod ~ n ~ a ~i  => FunctionDecl(n, i, VoidType, a, mod.withDoc(doc)) }
      | ((((doc ~ (opt("def") ~> modifier)) ~ types) ~ identLazy) ~ arguments) ~ instruction ^^ { case doc ~ mod ~ t ~ n ~ a ~i  => FunctionDecl(n, i, t, a, mod.withDoc(doc)) }
      | (((identFunction <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")") ~ block ^^ (p => FunctionCall(p._1._1, p._1._2 ::: List(LambdaValue(List(), p._2))))
      | ((identFunction <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")" ^^ (p => FunctionCall(p._1, p._2)) // Function Call
      | packageInstr
      | structDecl
      | classDecl
      | identLazy2 <~ ("+" ~ "+") ^^ (p => VariableAssigment(List((Left(p), Selector.self)), "+=", IntValue(1)))
      | identLazy2 <~ ("-" ~ "-") ^^ (p => VariableAssigment(List((Left(p), Selector.self)), "-=", IntValue(1)))
      | templateUse
      | varDeclaration
      | varAssignment
      | arrayAssign
      | "%%%" ~> stringLit2 <~ "%%%" ^^ { p => CMD(p) }
      | ifs
      | "return" ~> expr ^^ (Return(_))
      | block
      | switch | whileLoop | doWhileLoop | forLoop | jsonFile
      | "as" ~> expr ~ instruction ^^ (p => With(p._1, BoolValue(false), BoolValue(true), p._2))
      | "at" ~> expr ~ instruction ^^ (p => At(p._1, p._2))
      | withInstr
      | enumInstr
      | forgenerate
      | importInst
      | fromImportInst
      | templateDesc
      | typedef
      | foreach
      | predicate

  def anyKeyword: Parser[String] = lexical.reserved.map(f => f ^^ (p => p)).reduce(_ | _)

  def predicate:Parser[Instruction] = doc ~ (modifier <~ "predicate") ~ identLazy ~ arguments ~ json ^^ {case doc ~ mod ~ name ~ args ~ json => PredicateDecl(name, args, json, mod.withDoc(doc))}
  def arrayAssign:Parser[Instruction] = (identLazy2 <~ "[") ~ (rep1sep(expr, ",") <~ "]") ~ assignmentOp ~ expr ^^ { case a ~ i ~ o ~ e => ArrayAssigment(Left(a), i, o, e) }
  def foreach: Parser[Instruction] = (("foreach" ~ opt("(") ~> ident <~ "in") ~ expr <~ opt(")")) ~ program ^^ { case v ~ e ~ i => ForEach(v, e, i) }
  def packageInstr: Parser[Instruction] = "package" ~> identLazy2 ~ program ^^ (p => Package(p._1, p._2))
  def classDecl: Parser[Instruction] = doc ~ (modifier <~ "class") ~ identLazy ~ opt("extends" ~> ident2) ~ opt("with" ~> namespacedName2) ~ block ^^ { case doc ~ mod ~ iden ~ par ~ entity ~ block => ClassDecl(iden, block, mod.withDoc(doc), par, entity) }
  def structDecl: Parser[Instruction] = doc ~ (modifier <~ "struct") ~ identLazy ~ opt("extends" ~> ident2) ~ block ^^ { case doc ~ mod ~ iden ~ par ~ block => StructDecl(iden, block, mod.withDoc(doc), par) }
  def typedef: Parser[Instruction] = "typedef" ~> types ~ identLazy ^^ { case _1 ~ _2 => TypeDef(_2, _1) }
  def templateUse: Parser[Instruction] = ident2 ~ ident ~ block ^^ {case iden ~ name ~ instr => TemplateUse(iden, name, instr)}
  def templateDesc: Parser[Instruction] = doc ~ (modifier <~ "template") ~ identLazy ~ opt("extends" ~> ident2) ~ instruction ^^ {case doc ~ mod ~ name ~ parent ~ instr => TemplateDecl(name, instr, mod.withDoc(doc), parent)}
  def importInst: Parser[Instruction] = "import"~>ident2 ~ opt("as" ~> ident2) ^^ {case file ~ alias => Import(file, null, alias.getOrElse(null))}
  def fromImportInst: Parser[Instruction] = "from"~>ident2 ~ ("import" ~> ident2) ~ opt("as" ~> ident2) ^^ {case file ~ res ~ alias => Import(file, res, alias.getOrElse(null))}
  def forgenerate: Parser[Instruction] = (("forgenerate" ~> "(" ~> identLazy <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (p => ForGenerate(p._1._1, p._1._2, p._2))
  def jsonFile: Parser[Instruction] = "jsonfile" ~> identLazy2 ~ json ^^ (p => JSONFile(p._1, p._2))
  def doWhileLoop: Parser[Instruction] = ("do" ~> instruction <~ "while") ~ ("(" ~> expr <~ ")") ^^ (p => DoWhileLoop(p._2, p._1))
  def whileLoop: Parser[Instruction] = ("while" ~> "(" ~> expr <~ ")") ~ instruction ^^ (p => WhileLoop(p._1, p._2))
  def forLoop: Parser[Instruction] = ((("for" ~> "(" ~> instruction <~ ";") ~ expr <~ ";") ~ instruction <~ ")") ~ instruction ^^ 
    (p => InstructionList(List(p._1._1._1, WhileLoop(p._1._1._2, InstructionList(List(p._2, p._1._2))))))
  def withInstr: Parser[Instruction] = 
    ("with" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ^^ (p => With(p._1, BoolValue(false), BoolValue(true), p._2))
      | (("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (p => With(p._1._1, p._1._2, BoolValue(true), p._2))
      | ((("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (p => With(p._1._1._1, p._1._1._2, p._1._2, p._2))
  def switch: Parser[Switch] = ("switch" ~> expr <~ "{") ~ rep(switchCase) <~ "}" ^^ (p => Switch(p._1, p._2))
  def switchCase: Parser[SwitchCase] = (expr <~ "->") ~ instruction ^^ (p => SwitchCase(p._1, p._2))

  def blocktag: Parser[Instruction] = doc ~ modifier ~"blocktag" ~> identLazy2 ~ "{" ~ repsep(tagentry, ",") ~"}" ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ => }
  def tagentry: Parser[String] = identLazy2 | (namespacedName ^^ {_.value})


  def enumInstr: Parser[EnumDecl] = (doc ~ modifier ~ ("enum" ~> identLazy) ~ opt("("~>repsep(enumField,",")<~")") <~ "{") ~ repsep(enumValue, ",") <~ "}" ^^ 
                                    { case doc ~ mod ~ n ~ f ~ v => EnumDecl(n, f.getOrElse(List()), v, mod.withDoc(doc)) }
  def enumField: Parser[EnumField] = types ~ identLazy ^^ { p => EnumField(p._2, p._1) }
  def enumValue: Parser[EnumValue] = identLazy ~ opt("("~>repsep(exprNoTuple,",")<~")") ^^ (p => EnumValue(p._1, p._2.getOrElse(List())))

  def varAssignment: Parser[Instruction] = (rep1sep(opt(selector <~ ".") ~ identLazy2, ",") ~ assignmentOp ~ expr) ^^ (p => 
    {
      val identifiers = p._1._1.map(p => (Identifier.fromString(p._2), p._1.getOrElse(Selector.self)))
      VariableAssigment(identifiers.map((i,s) => (Left(i), s)), p._1._2, p._2)
    })

  def varDeclaration: Parser[Instruction] = (doc ~ modifier ~ types ~ rep1sep(identLazy, ",") ~ opt("=" ~> expr)) ^^ {
    case doc ~ mod1 ~ typ ~ names ~ expr => {
      val mod = mod1.withDoc(doc)
      val identifiers = names.map(Identifier.fromString(_))
      if (!mod.isEntity && expr.isEmpty){
        VariableDecl(names, typ, mod, ":=", DefaultValue)
      }
      else if (!expr.isEmpty){
        VariableDecl(names, typ, mod, "=", expr.get)
      }
      else{
        VariableDecl(names, typ, mod, null, null)
      }
    }
  } // Variable Dec
  def ifs: Parser[If] = ("if" ~> "(" ~> expr <~ ")") ~ instruction ~ 
      rep(("else" ~> "if" ~> "(" ~> expr <~ ")") ~ instruction) ~
      opt("else" ~> instruction) ^^ {p => 
        {
          val elze = p._1._2.map(k => ElseIf(k._1, k._2)) ::: (if p._2.isEmpty then Nil else List(ElseIf(BoolValue(true), p._2.get)))
          If(p._1._1._1, p._1._1._2, elze)
        }
      }



  def lambda1: Parser[Expression] = (identLazy2 <~ "=>") ~ instruction ^^ (p => LambdaValue(List(p._1), p._2))
  def lambda2: Parser[Expression] = ("(" ~> repsep(identLazy2, ",") <~ ")" <~ "=>") ~ instruction ^^ (p => LambdaValue(p._1, p._2))
  def lambda = lambda1 | lambda2

  def sfRange: Parser[SelectorFilterValue] = (floatValue <~ "..") ~ floatValue ^^ (p => SelectorRange(p._1, p._2))
  def sfNumber: Parser[SelectorFilterValue] = floatValue ^^ (SelectorNumber(_))
  def sfNumber2: Parser[SelectorFilterValue] = numericLit ^^ (p => SelectorNumber(p.toInt))
  def sfString: Parser[SelectorFilterValue] = stringLit2 ^^ (SelectorString(_))
  def sfIdentifier: Parser[SelectorFilterValue] = identLazy2 ^^ (SelectorIdentifier(_))
  def sfNamespacedName: Parser[SelectorFilterValue] = namespacedName ^^ (p => SelectorIdentifier(p.toString()))
  def sfNBT: Parser[SelectorFilterValue] = json ^^ (SelectorNbt(_))
  def selectorFilterInnerValue = sfRange | sfNumber | sfString | sfNamespacedName | sfIdentifier | sfNumber2 | sfNBT

  def selectorFilterValue: Parser[SelectorFilterValue] = "!" ~> selectorFilterInnerValue ^^ (SelectorInvert(_)) |
                                              selectorFilterInnerValue

  def selectorFilter: Parser[(String, SelectorFilterValue)] = (identLazy <~ "=") ~ selectorFilterValue ^^ { p => (p._1, p._2) }
  def selector: Parser[Selector] = ("@a" | "@s" | "@e" | "@p" | "@r") ~ opt("[" ~> rep1sep(selectorFilter, ",") <~ "]") ^^ { p => Selector.parse(p._1, p._2.getOrElse(List())) }
  def selectorStr : Parser[String] = (selector ^^ (_.toString()))

  def stringLit2: Parser[String] = stringLit ^^ {p => p.replaceAllLiterally("◘", "\\\"")}
  def namespacedName = ident ~ ":" ~ ident2 ^^ { case a ~ b ~ c => NamespacedName(a+b+c) }
  def namespacedName2 = opt(identLazy <~ ":") ~ identLazy2 ^^ { case a ~ c => if a.isEmpty then NamespacedName(c) else NamespacedName(a.get+":"+c)}

  def validCordNumber1: Parser[String] = floatValue^^{_.toString()} | numericLit | identLazy
  def validCordNumber2: Parser[String] = ("-" ~> validCordNumber1 ^^ {"-"+_}) | validCordNumber1
  def relCoordinate1: Parser[String] = "~"~>validCordNumber2 ^^ {"~"+_}
  def relCoordinate2: Parser[String] = "~" ^^^ "~"
  def relCoordinate: Parser[String] = relCoordinate1 | relCoordinate2 | validCordNumber2
  def frontCoordinateNumber: Parser[String] = "^"~>validCordNumber2 ^^ {"^"+_}
  def frontCoordinateHere: Parser[String] = "^" ^^^ "^"
  def frontCoordinate: Parser[String] = frontCoordinateNumber | frontCoordinateHere

  def frontPosition: Parser[String] = frontCoordinate ~ frontCoordinate ~ frontCoordinate ^^ {case x ~ y ~ z => f"$x $y $z"}
  def relPosition: Parser[String] = relCoordinate ~ relCoordinate ~ relCoordinate ^^ {case x ~ y ~ z => f"$x $y $z"}
  def position: Parser[PositionValue] = (frontPosition | relPosition) ^^ {case a => PositionValue(a)}

  def exprBottom: Parser[Expression] = 
    floatValue ^^ (p => FloatValue(p))
    | numericLit ^^ (p => IntValue(p.toInt))
    | "-" ~> numericLit ^^ (p => IntValue(f"-$p".toInt))
    | "-" ~> exprBottom ^^ (BinaryOperation("-", IntValue(0), _))
    | "!" ~> exprBottom ^^ (UnaryOperation("!", _))
    | "true" ^^^ BoolValue(true)
    | "false" ^^^ BoolValue(false)
    | "null" ^^^ NullValue
    | namespacedName
    | stringLit2 ^^ (StringValue(_))
    | "new" ~> identLazy2 ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ a ~ b => ConstructorCall(f, a ::: List(LambdaValue(List(), b))) }
    | "new" ~> identLazy2 ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ { case f ~ a => ConstructorCall(f, a) }
    | identLazy2 ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ a ~ b => FunctionCallValue(VariableValue(f), a ::: List(LambdaValue(List(), b))) }
    | identLazy2 ~ rep1("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ (p => p._2.foldLeft[Expression](VariableValue(p._1))((b, a) => FunctionCallValue(b, a)))
    | identLazy2 ^^ (VariableValue(_))
    | selector ~ "." ~ identLazy2 ^^ { case s ~ _ ~ n => VariableValue(n, s) }
    | identTag ^^ (VariableValue(_))
    | "(" ~> expr <~ ")"
    | json ^^ (JsonValue(_))
    | selector ^^ (SelectorValue(_))

  def comparator: Parser[String] = "<" | "<=" | ">=" | ">" | "==" | "!="

  def exprRange: Parser[Expression] = exprBottom ~ opt(".."~>exprBottom) ^^ { case e ~ None => e; case e1 ~ Some(e2) => RangeValue(e1, e2)}
  def exprArray: Parser[Expression] = exprRange ~ rep("[" ~> rep1sep(expr, ",") <~ "]") ^^ {case e ~ g => g.foldLeft(e)((e, i) => ArrayGetValue(e, i))}
  def exprMod: Parser[Expression] = exprArray ~ rep("%" ~> exprMod) ^^ {unpack("%", _)}
  def exprDiv: Parser[Expression] = exprMod ~ rep(("/" | "\\") ~> exprDiv) ^^ {unpack("/", _)}
  def exprMult: Parser[Expression] = exprDiv ~ rep("*" ~> exprMult) ^^ {unpack("*", _)}
  def exprSub: Parser[Expression] = exprMult ~ rep("-" ~> exprSub) ^^ {unpack("-", _)}
  def exprAdd: Parser[Expression] = exprSub ~ rep("+" ~> exprAdd) ^^ {unpack("+", _)}
  def exprComp: Parser[Expression] = exprAdd ~ rep(comparator ~ exprComp) ^^ {unpack(_)}
  def exprIn: Parser[Expression] = exprComp ~ rep("in" ~> exprIn) ^^ {unpack("in", _)}
  def exprAnd: Parser[Expression] = exprIn ~ rep("&&" ~> exprAnd) ^^ {unpack("&&", _)}
  def exprOr: Parser[Expression] = exprAnd ~ rep("||" ~> exprOr) ^^ {unpack("||", _)}
  def exprNoTuple = exprOr | position | lambda
  def expr: Parser[Expression] = rep1sep(exprNoTuple, ",") ^^ (p => if p.length == 1 then p.head else TupleValue(p))

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
        case other => IdentifierType(other)
    }
  }
  
  def nonRecTypes: Parser[Type] = 
    ident2 ^^ { identifierType(_) } |
    (("(" ~> types) ~ rep1("," ~> types)) <~ ")" ^^ (p => if p._2.length > 0 then TupleType(p._1 :: p._2) else p._1)

  def types: Parser[Type] = ("(" ~> repsep(nonRecTypes, ",") <~ ")"<~ "=>") ~ types ^^ (p => FuncType(p._1, p._2)) |
                            (nonRecTypes <~ "=>") ~ types ^^ (p => FuncType(List(p._1), p._2)) |
                            ((nonRecTypes <~ "[") ~ expr) <~ "]" ^^ (p => ArrayType(p._1, p._2)) |
                            nonRecTypes

  def modifierSub: Parser[String] = ("override" | "lazy" | "scoreboard" | "ticking" | "loading" | "helper" | "static" | "const")
  def modifierAttribute: Parser[(String,Expression)] = ident2 ~ "=" ~ exprNoTuple ^^ {case i ~ _ ~ e => (i, e)}
  def modifierAttributes: Parser[Map[String,Expression]] = opt("[" ~> rep1sep(modifierAttribute,",") <~"]") ^^ {(_.getOrElse(List()).toMap)}
  def modifier: Parser[Modifier] = 
    (opt(stringLit2 ~ stringLit2 ~ stringLit2) ~ modifierAttributes ~ opt("public" | "private" | "protected") ~ rep(modifierSub) ~ rep(identTag)) ^^ 
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

      if subs.contains("virtual")   then {mod.isVirtual = true}
      if subs.contains("abstract")  then {mod.isAbstract = true}
      if subs.contains("override")  then {mod.isOverride = true}
      if subs.contains("lazy")      then {mod.isLazy = true}
      if subs.contains("scoreboard")then {mod.isEntity = true}
      if subs.contains("ticking")   then {mod.isTicking = true}
      if subs.contains("loading")   then {mod.isLoading = true}
      if subs.contains("helper")    then {mod.isHelper = true}
      if subs.contains("static")    then {mod.isStatic = true}
      if subs.contains("const")     then {mod.isConst = true}
      
      mod
    }
  }

  def doc: Parser[Option[String]] = opt("???"~>stringLit<~"???")

  def program: Parser[Instruction] = repsep(instruction, opt(";")) ^^ (InstructionList(_))

  /** Print an error message, together with the position where it occured. */
  case class TypeError(t: Instruction, msg: String) extends Exception(msg) {
    override def toString =
      msg + "\n" + t
  }

  def parse(file: String, args: String): Option[Instruction] = {
    val tokens = new lexical.Scanner(Preparser.parse(file, args))
    phrase(program)(tokens) match {
      case Success(trees, _) =>
        Reporter.ok(f"Parsed: $file")
        Some(trees)
      case e =>
        println(f"Error in file '${file}'': ${e}")
        None
    }
  }
}
