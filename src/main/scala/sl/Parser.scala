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

object Parser extends StandardTokenParsers{
  lexical.delimiters ++= List("(", ")", "\\", ".", "..", ":", "=", "->", "{", "}", ",", "*", "[", "]", "/", "+", "-", "*", "/", "\\", "%", "&&", "||", "=>", ";",
                              "+=", "-=", "/=", "*=", "%=", "?=", ":=", "%", "@", "@e", "@a", "@s", "@r", "@p", "~", "^", "<=", "==", ">=", "<", ">", "!=", "%%%", "???", "$",
                              "!", "!=", "#", "<<", ">>", "&", "<<=", ">>=", "&=", "|=", "::")
  lexical.reserved   ++= List("true", "false", "if", "then", "else", "return", "switch", "for", "do", "while",
                              "as", "at", "with", "to", "import", "doc", "template", "null", "typedef", "foreach", "in",
                              "def", "package", "struct", "enum", "class", "lazy", "jsonfile", "blocktag",
                              "public", "protected", "private", "scoreboard", "forgenerate", "from", "rotated", "facing", "align",
                              "ticking", "loading", "predicate", "extends", "new", "const", "static", "virtual", "abstract", "override")


  def block: Parser[Instruction] = "{" ~> repsep(instruction, opt(";")) <~ "}" ^^ (p => InstructionBlock(p))
  def assignmentOp: Parser[String] = ("=" | "+=" | "-=" | "*=" | "/=" | ":=" | "%=" | "^=" | "|=" | "&=" | "<<=" | ">>=")

  def ident2: Parser[String] = rep1sep(ident, ".") ^^ { p => p.reduce(_ + "." + _) }
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
  def jsonKeypairStr: Parser[String] = ((stringLit2 ^^ (Utils.stringify(_)) | identLazy) <~ ":") ~ jsonValueStr ^^ { p => p._1 + ":" + p._2 }
  def jsonStr: Parser[String] = "{" ~> rep1sep(jsonKeypairStr, ",") <~ "}" ^^ { p => "{" + p.reduce(_ + ","+ _) + "}" }


  def jsonValue: Parser[JSONElement] = floatValue ^^ { p => JsonFloat(p) } 
                                    | numericLit ^^ { p => JsonInt(p.toInt) } 
                                    | "-" ~> floatValue ^^ { p => JsonFloat(-p) } 
                                    | "-" ~> numericLit ^^ { p => JsonInt(f"-$p".toInt) } 
                                    | stringLit2 ^^ { p => JsonString(p) }
                                    | jsonCall
                                    | identLazy2 ^^ { p => JsonIdentifier(p) }
                                    | "true" ^^^ (JsonBoolean(true))
                                    | "false" ^^^ (JsonBoolean(false))
                                    | json

  def jsonCall: Parser[JSONElement] = identLazy2 ~ typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ {case f ~ t ~ a => JsonCall(f, a, t)}
  def jsonArray: Parser[JSONElement] = ("[" ~> repsep(jsonValue, ",")) <~ "]" ^^ { p => JsonArray(p)}
  def jsonKeypair: Parser[(String, JSONElement)] = ((stringLit2 | identLazy) <~ ":") ~ jsonValue ^^ { p => (p._1, p._2) }
  def jsonDic: Parser[JSONElement] = "{" ~> repsep(jsonKeypair, ",") <~ "}" ^^ { p => JsonDictionary(p.toMap) }
  def json: Parser[JSONElement] = jsonDic | jsonArray



  def argument: Parser[Argument] = types ~ identLazy ~ opt("=" ~> exprNoTuple) ^^ { p => Argument(p._1._2, p._1._1, p._2) }
  def arguments: Parser[List[Argument]] = "(" ~> repsep(argument, ",") <~ ")"

  def instruction: Parser[Instruction] = 
      ((((doc ~ ("def" ~> modifier)) ~ identLazy ~ typeArgument)) ~ arguments) ~ instruction ^^ { case doc ~ mod ~ n ~ at ~ a ~i  => FunctionDecl(n, i, VoidType, a, at, mod.withDoc(doc)) }
      | ((((doc ~ (opt("def") ~> modifier)) ~ types) ~ identLazy ~ typeArgument) ~ arguments) ~ instruction ^^ { case doc ~ mod ~ t ~ n ~ at ~ a ~i  => FunctionDecl(n, i, t, a, at, mod.withDoc(doc)) }
      | (((identFunction ~ typeVariables <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")") ~ block ^^ {case f ~ t ~ e ~ b => FunctionCall(f, e ::: List(LambdaValue(List(), b)), t)} // Function Call
      | ((identFunction ~ typeVariables <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")" ^^ {case f ~ t ~ e => FunctionCall(f, e, t)} // Function Call
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
      | "as" ~"("~> exprNoTuple ~")"~ instruction ^^ {case e ~ _ ~ i => With(e, BoolValue(false), BoolValue(true), i)}
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

  def anyKeyword: Parser[String] = lexical.reserved.map(f => f ^^ (p => p)).reduce(_ | _)

  def predicate:Parser[Instruction] = doc ~ (modifier <~ "predicate") ~ identLazy ~ arguments ~ json ^^ {case doc ~ mod ~ name ~ args ~ json => PredicateDecl(name, args, json, mod.withDoc(doc))}
  def arrayAssign:Parser[Instruction] = (identLazy2 <~ "[") ~ (rep1sep(exprNoTuple, ",") <~ "]") ~ assignmentOp ~ expr ^^ { case a ~ i ~ o ~ e => ArrayAssigment(Left(a), i, o, e) }
  def foreach: Parser[Instruction] = (("foreach" ~ opt("(") ~> ident <~ "in") ~ exprNoTuple <~ opt(")")) ~ instruction ^^ { case v ~ e ~ i => ForEach(v, e, i) }
  def packageInstr: Parser[Instruction] = "package" ~> identLazy2 ~ program ^^ (p => Package(p._1, p._2))
  def classDecl: Parser[Instruction] = doc ~ (modifier <~ "class") ~ identLazy ~ typeArgument ~ opt("extends" ~> ident2) ~ rep("with" ~> namespacedName ~ "for" ~ ident) ~ block ^^ 
  { case doc ~ mod ~ iden ~ typeargs ~ par ~ entity ~ block => ClassDecl(iden, typeargs, block, mod.withDoc(doc), par, entity.map{ case e ~ _ ~ n => (n, e)}.toMap) }
  def structDecl: Parser[Instruction] = doc ~ (modifier <~ "struct") ~ identLazy ~ typeArgument ~ opt("extends" ~> ident2) ~ block ^^ 
  { case doc ~ mod ~ iden ~ typeargs ~ par ~ block => StructDecl(iden, typeargs, block, mod.withDoc(doc), par) }
  def typedef: Parser[Instruction] = "typedef" ~> types ~ identLazy ^^ { case _1 ~ _2 => TypeDef(_2, _1) }
  def templateUse: Parser[Instruction] = ident2 ~ ident ~ block ^^ {case iden ~ name ~ instr => TemplateUse(iden, name, instr)}
  def templateDesc: Parser[Instruction] = doc ~ (modifier <~ "template") ~ identLazy ~ opt("extends" ~> ident2) ~ instruction ^^ {case doc ~ mod ~ name ~ parent ~ instr => TemplateDecl(name, instr, mod.withDoc(doc), parent)}
  def importShortInst: Parser[Instruction] = "import"~>ident2 ~ "::" ~ ident2 ~ opt("as" ~> ident2) ^^ {case file ~ _ ~ res ~ alias => Import(file, res, alias.getOrElse(null))}
  def importInst: Parser[Instruction] = "import"~>ident2 ~ opt("as" ~> ident2) ^^ {case file ~ alias => Import(file, null, alias.getOrElse(null))}
  def fromImportInst: Parser[Instruction] = "from"~>ident2 ~ ("import" ~> ident2) ~ opt("as" ~> ident2) ^^ {case file ~ res ~ alias => Import(file, res, alias.getOrElse(null))}
  def forgenerate: Parser[Instruction] = (("forgenerate" ~> "(" ~> identLazy <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (p => ForGenerate(p._1._1, p._1._2, p._2))
  def jsonFile: Parser[Instruction] = doc ~ modifier ~ "jsonfile" ~ identLazy2 ~ json ^^ {case d ~ m ~_ ~n~json => JSONFile(n, json, m.withDoc(d))}
  def doWhileLoop: Parser[Instruction] = ("do" ~> instruction <~ "while") ~ ("(" ~> exprNoTuple <~ ")") ^^ (p => DoWhileLoop(p._2, p._1))
  def whileLoop: Parser[Instruction] = ("while" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ^^ (p => WhileLoop(p._1, p._2))
  def forLoop: Parser[Instruction] = ((("for" ~> "(" ~> instruction <~ ";") ~ exprNoTuple <~ ";") ~ instruction <~ ")") ~ instruction ^^ 
    (p => InstructionList(List(p._1._1._1, WhileLoop(p._1._1._2, InstructionList(List(p._2, p._1._2))))))
  def withInstr: Parser[Instruction] = 
    ("with" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ^^ (p => With(p._1, BoolValue(false), BoolValue(true), p._2))
      | (("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (p => With(p._1._1, p._1._2, BoolValue(true), p._2))
      | ((("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (p => With(p._1._1._1, p._1._1._2, p._1._2, p._2))
  def switch: Parser[Switch] = ("switch" ~> exprNoTuple <~ "{") ~ rep(switchCase) <~ "}" ^^ (p => Switch(p._1, p._2))
  def switchCase: Parser[SwitchCase] = (exprNoTuple <~ "->") ~ instruction ^^ (p => SwitchCase(p._1, p._2))
  def rotated1: Parser[Instruction] = "rotated" ~"("~ exprNoTuple ~","~ exprNoTuple~")" ~ instruction ^^ { case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ i => Execute(RotatedType, List(e1, e2), i) }
  def rotated2: Parser[Instruction] = "rotated" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i => Execute(RotatedType, List(e), i) }
  def facing1: Parser[Instruction] = "facing" ~"("~ exprNoTuple ~","~ exprNoTuple~")" ~ instruction ^^ { case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ i => Execute(FacingType, List(e1, e2), i) }
  def facing2: Parser[Instruction] = "facing" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i => Execute(FacingType, List(e), i) }
  def align: Parser[Instruction] = "align" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i => Execute(AlignType, List(e), i) }

  def blocktag: Parser[Instruction] = doc ~ modifier ~ "blocktag" ~ identLazy2 ~ "{" ~ repsep(tagentry, ",") ~ "}" ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ => BlocktagDecl(n, c, m.withDoc(d))}
  def tagentry: Parser[Expression] = namespacedName | (identLazy2 ^^ (VariableValue(_)))


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

  def sfField: Parser[(String, SelectorFilterValue)] = ident ~ "=" ~ selectorFilterInnerValue ^^ {case n ~ _ ~ v => (n, v)}
  def sfCompound: Parser[SelectorFilterValue] = "{" ~> rep1sep(sfField, ",") <~ "}" ^^ {case a => SelectorComposed(a.toMap)}
  def sfNumber: Parser[SelectorFilterValue] = floatValue ^^ (SelectorNumber(_))
  def sfNumber2: Parser[SelectorFilterValue] = numericLit ^^ (p => SelectorNumber(p.toInt))
  def sfNumber3: Parser[SelectorFilterValue] = "-" ~> floatValue ^^ (p=>SelectorNumber(-p))
  def sfNumber4: Parser[SelectorFilterValue] =  "-" ~> numericLit ^^ (p => SelectorNumber(f"-$p".toInt))
  def sfString: Parser[SelectorFilterValue] = stringLit2 ^^ (SelectorString(_))
  def sfIdentifier: Parser[SelectorFilterValue] = identLazy2 ^^ (SelectorIdentifier(_))
  def sfNamespacedName: Parser[SelectorFilterValue] = namespacedName ^^ (p => SelectorIdentifier(p.toString()))
  def sfNBT: Parser[SelectorFilterValue] = json ^^ (SelectorNbt(_))
  def selectorFilterInnerValue2 = sfNumber | sfNumber3 | sfString | sfNamespacedName | sfIdentifier | sfNumber2 | sfNumber4 | sfNBT | sfCompound
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
  def blockDataField = anyWord ~ "=" ~ expr ^^ {case n ~ _ ~ v => n +"="+v }
  def blockData = "[" ~> rep1sep(blockDataField, ",") <~ "]" ^^ {case fields => fields.mkString("[", ",", "]")}
  def namespacedName = ident ~ ":" ~ ident2 ~ opt(blockData) ^^ { case a ~ b ~ c ~ d => NamespacedName(a+b+c+d.getOrElse("")) }
  def namespacedName2 = opt(identLazy <~ ":") ~ identLazy2 ^^ { case a ~ c => if a.isEmpty then NamespacedName(c) else NamespacedName(a.get+":"+c)}

  def validCordNumber1: Parser[String] = floatValue^^{_.toString()} | numericLit | identLazyForce
  def validCordNumber2: Parser[String] = ("-" ~> validCordNumber1 ^^ {"-"+_}) | validCordNumber1
  def relCoordinate1: Parser[String] = "~"~>validCordNumber2 ^^ {"~"+_}
  def relCoordinate2: Parser[String] = "~" ^^^ "~"
  def relCoordinate: Parser[String] = relCoordinate1 | relCoordinate2 | validCordNumber2
  def frontCoordinateNumber: Parser[String] = "^"~>validCordNumber2 ^^ {"^"+_}
  def frontCoordinateHere: Parser[String] = "^" ^^^ "^"
  def frontCoordinate: Parser[String] = frontCoordinateNumber | frontCoordinateHere

  def relPositionCase1: Parser[String] = relCoordinate2 ~ validCordNumber1 ~ relCoordinate ^^ {case x ~ y ~ z => f"$x $y $z"}
  def relPositionCase2: Parser[String] = relCoordinate ~ relCoordinate2 ~ validCordNumber1 ^^ {case x ~ y ~ z => f"$x $y $z"}

  def frontPosition: Parser[String] = frontCoordinate ~ frontCoordinate ~ frontCoordinate ^^ {case x ~ y ~ z => f"$x $y $z"}
  def relPosition: Parser[String] = relCoordinate ~ relCoordinate ~ relCoordinate ^^ {case x ~ y ~ z => f"$x $y $z"}
  def position: Parser[PositionValue] = (frontPosition | relPosition | relPositionCase1 | relPositionCase2) ^^ {case a => PositionValue(a)}

  def exprBottom: Parser[Expression] = 
    floatValue ^^ (p => FloatValue(p))
    | numericLit ^^ (p => IntValue(p.toInt))
    | "-" ~> floatValue ^^ (p => FloatValue(-p))
    | "-" ~> numericLit ^^ (p => IntValue(f"-$p".toInt))
    | "-" ~> exprBottom ^^ (BinaryOperation("-", IntValue(0), _))
    | "!" ~> exprBottom ^^ (UnaryOperation("!", _))
    | "true" ^^^ BoolValue(true)
    | "false" ^^^ BoolValue(false)
    | "null" ^^^ NullValue
    | "#" ~> ident2 ^^ (TagValue(_))
    | namespacedName
    | stringLit2 ^^ (StringValue(_))
    | "new" ~> identLazy2 ~ typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ t ~ a ~ b => ConstructorCall(f, a ::: List(LambdaValue(List(), b)), t) }
    | "new" ~> identLazy2 ~ typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ { case f ~ t ~ a => ConstructorCall(f, a, t) }
    | identLazy2 ~ typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ t ~ a ~ b => FunctionCallValue(VariableValue(f), a ::: List(LambdaValue(List(), b)), t) }
    | identLazy2 ~ rep1((typeVariables <~"(") ~ repsep(exprNoTuple, ",") <~ ")") ^^ { case f ~ a => a.foldLeft[Expression](VariableValue(f))((b, a) => FunctionCallValue(b, a._2, a._1)) }
    | identLazy2 ^^ (VariableValue(_))
    | selector ~ "." ~ identLazy2 ^^ { case s ~ _ ~ n => VariableValue(n, s) }
    | identTag ^^ (VariableValue(_))
    | "(" ~> expr <~ ")"
    | json ^^ (JsonValue(_))
    | selector ^^ (SelectorValue(_))

  def comparator: Parser[String] = "<" | "<=" | ">=" | ">" | "==" | "!="

  def typeVariablesForce = "<" ~ repsep(types,",") ~ ">" ^^ {case _ ~ a ~ _ => a}
  def typeVariables = opt(typeVariablesForce) ^^ {case Some(a) => a;case None => List()}
  def typeArgument = opt("<" ~ repsep(ident,",") ~ ">") ^^ {case Some(_ ~ a ~ _) => a;case None => List()}

  def exprRange: Parser[Expression] = exprBottom ~ opt(".."~>exprBottom) ^^ { case e ~ None => e; case e1 ~ Some(e2) => RangeValue(e1, e2)}
  def exprArray: Parser[Expression] = exprRange ~ rep("[" ~> rep1sep(expr, ",") <~ "]") ^^ {case e ~ g => g.foldLeft(e)((e, i) => ArrayGetValue(e, i))}
  def exprPow: Parser[Expression] = exprArray ~ rep("^" ~> exprPow) ^^ {unpack("^", _)}
  def exprMod: Parser[Expression] = exprPow ~ rep("%" ~> exprMod) ^^ {unpack("%", _)}
  def exprDiv: Parser[Expression] = exprMod ~ rep(("/" | "\\") ~> exprDiv) ^^ {unpack("/", _)}
  def exprMult: Parser[Expression] = exprDiv ~ rep("*" ~> exprMult) ^^ {unpack("*", _)}
  def exprSub: Parser[Expression] = exprMult ~ rep("-" ~> exprSub) ^^ {unpack("-", _)}
  def exprAdd: Parser[Expression] = exprSub ~ rep("+" ~> exprAdd) ^^ {unpack("+", _)}
  def exprComp: Parser[Expression] = exprAdd ~ rep(comparator ~ exprComp) ^^ {unpack(_)}
  def exprIn: Parser[Expression] = exprComp ~ rep("in" ~> exprIn) ^^ {unpack("in", _)}
  def exprShiftRight: Parser[Expression] = exprIn ~ rep(">>" ~> exprShiftRight) ^^ {unpack(">>", _)}
  def exprShiftLeft: Parser[Expression] = exprShiftRight ~ rep("<<" ~> exprShiftLeft) ^^ {unpack("<<", _)}
  def exprBitwiseAnd: Parser[Expression] = exprShiftLeft ~ rep("&" ~> exprBitwiseAnd) ^^ {unpack("&", _)}
  def exprBitwiseOr: Parser[Expression] = exprBitwiseAnd ~ rep("|" ~> exprBitwiseOr) ^^ {unpack("|", _)}
  def exprAnd: Parser[Expression] = exprBitwiseOr ~ rep("&&" ~> exprAnd) ^^ {unpack("&&", _)}
  def exprOr: Parser[Expression] = exprAnd ~ rep("||" ~> exprOr) ^^ {unpack("||", _)}  
  def exprNoTuple = position | exprOr | lambda
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
        case other => IdentifierType(other, List())
    }
  }
  
  def nonRecTypes: Parser[Type] = 
    ident2 ~ typeVariablesForce ^^ { case n ~ t => IdentifierType(n, t) } |
    ident2 ^^ { identifierType(_) } |
    (("(" ~> types) ~ rep1("," ~> types)) <~ ")" ^^ (p => if p._2.length > 0 then TupleType(p._1 :: p._2) else p._1)

  def types: Parser[Type] = ("(" ~> repsep(nonRecTypes, ",") <~ ")"<~ "=>") ~ types ^^ (p => FuncType(p._1, p._2)) |
                            (nonRecTypes <~ "=>") ~ types ^^ (p => FuncType(List(p._1), p._2)) |
                            ((nonRecTypes <~ "[") ~ expr) <~ "]" ^^ (p => ArrayType(p._1, p._2)) |
                            nonRecTypes

  def modifierSub: Parser[String] = ("override" | "virtual" |"lazy" | "scoreboard" | "ticking" | "loading" | "helper" | "static" | "const")
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
        Some(trees)
      case e =>
        println(f"Error in file '${file}'': ${e}")
        None
    }
  }
  def parse(string: String): Instruction = {
    val tokens = new lexical.Scanner(Preparser.parse("",string))
    phrase(program)(tokens) match {
      case Success(trees, _) =>
        trees
      case e =>
        println(f"Error in file '${string}'': ${e}")
        null
    }
  }
  def parseJson(file: String): JSONElement = {
    val tokens = new lexical.Scanner(Preparser.parse(file, ""))
    phrase(json)(tokens) match {
      case Success(trees, _) =>
        Reporter.ok(f"Parsed: $file")
        trees
      case e =>
        println(f"Error in file '${file}'': ${e}")
        null
    }
  }
  def parseExpression(file: String): Expression = {
    val tokens = new lexical.Scanner(Preparser.parse(file, ""))
    phrase(expr)(tokens) match {
      case Success(trees, _) =>
        Reporter.ok(f"Parsed: $file")
        trees
      case e =>
        println(f"Error in file '${file}'': ${e}")
        null
    }
  }
}
