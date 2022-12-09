package fos

import scala.util.parsing.combinator.syntactical.StandardTokenParsers
import scala.util.parsing.input._
import objects.types.*
import objects.{Modifier, Protection}
import objects.types.VoidType
import scala.util.parsing.combinator._
import objects.Identifier
import fos.Compilation.Selector.*
import objects.EnumField
import objects.EnumValue
import javax.swing.text.DefaultEditorKit.PasteAction

object Parser extends StandardTokenParsers{
  lexical.delimiters ++= List("(", ")", "\\", ".", "..", ":", "=", "->", "{", "}", ",", "*", "[", "]", "/", "+", "-", "*", "/", "\\", "%", "&&", "||", "=>", ";",
                              "+=", "-=", "/=", "*=", "?=", ":=", "%", "@", "@e", "@a", "@s", "@r", "@p", "~", "^", "<=", "==", ">=", "<", ">", "!=", "%%%", "$",
                              "!", "!=")
  lexical.reserved   ++= List("true", "false", "if", "then", "else", "return", "switch", "for", "do", "while",
                              "as", "at", "with", "to", "import", "doc", "template", "null", "typedef", "helper", "foreach", "in",
                              "var", "val", "def", "package", "struct", "enum", "class", "lazy", "jsonfile",
                              "public", "protected", "private", "scoreboard", "forgenerate",
                              "ticking", "loading", "predicate", "extends", "new", "static")


  def block: Parser[Instruction] = "{" ~> rep(instruction) <~ "}" ^^ (p => InstructionBlock(p))
  def assignmentOp: Parser[String] = ("=" | "+=" | "-=" | "*=" | "/=" | ":=")

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
                                    | identLazy2 ^^ { p => JsonIdentifier(p) }
                                    | "true" ^^^ (JsonBoolean(true))
                                    | "false" ^^^ (JsonBoolean(false))
                                    | json
                                    | ("[" ~> repsep(jsonValue, ",")) <~ "]" ^^ { p => JsonArray(p)}
                                    
  def jsonKeypair: Parser[(String, JSONElement)] = ((stringLit2 | identLazy) <~ ":") ~ jsonValue ^^ { p => (p._1, p._2) }
  def json: Parser[JSONElement] = "{" ~> repsep(jsonKeypair, ",") <~ "}" ^^ { p => JsonDictionary(p.toMap) }



  def argument: Parser[Argument] = types ~ identLazy ~ opt("=" ~> exprNoTuple) ^^ { p => Argument(p._1._2, p._1._1, p._2) }
  def arguments: Parser[List[Argument]] = "(" ~> repsep(argument, ",") <~ ")"

  def functionDoc: Parser[String] = ("doc" ~ "(") ~> stringLit2 <~ ")"
  def instruction: Parser[Instruction] = 
      ((((opt(functionDoc) ~> "def" ~> modifier) ~ identLazy)) ~ arguments) ~ instruction ^^ (p => FunctionDecl(p._1._1._2, p._2, VoidType, p._1._2, p._1._1._1))
      | ((((opt(functionDoc) ~> opt("def") ~> modifier) ~ types) ~ identLazy) ~ arguments) ~ instruction ^^ (p => FunctionDecl(p._1._1._2, p._2, p._1._1._1._2, p._1._2, p._1._1._1._1))
      | (((identFunction <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")") ~ block ^^ (p => FunctionCall(p._1._1, p._1._2 ::: List(LambdaValue(List(), p._2))))
      | ((identFunction <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")" ^^ (p => FunctionCall(p._1, p._2)) // Function Call
      | packageInstr
      | structDecl
      | classDecl
      | identLazy2 <~ ("+" ~ "+") ^^ (p => VariableAssigment(List(Left(p)), "+=", IntValue(1)))
      | identLazy2 <~ ("-" ~ "-") ^^ (p => VariableAssigment(List(Left(p)), "-=", IntValue(1)))
      | templateUse
      | varDeclaration
      | varAssignment
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
      | templateDesc
      | typedef
      | foreach
      | predicate

  def anyKeyword: Parser[String] = lexical.reserved.map(f => f ^^ (p => p)).reduce(_ | _)

  def predicate:Parser[Instruction] = (modifier <~ "predicate") ~ identLazy ~ arguments ~ json ^^ {case mod ~ name ~ args ~ json => PredicateDecl(name, args, json, mod)}
  def arrayAssign:Parser[Instruction] = (identLazy2 <~ "[") ~ (expr <~ "]") ~ assignmentOp ~ expr ^^ { case a ~ i ~ o ~ e => ArrayAssigment(Left(a), i, o, e) }
  def foreach: Parser[Instruction] = ("foreach" ~> ident <~ "in") ~ expr ~ program ^^ { case v ~ e ~ i => ForEach(v, e, i) }
  def packageInstr: Parser[Instruction] = "package" ~> identLazy2 ~ program ^^ (p => Package(p._1, p._2))
  def classDecl: Parser[Instruction] = (modifier <~ "class") ~ identLazy ~ opt("extends" ~> ident2) ~ block ^^ { case mod ~ iden ~ par ~ block => ClassDecl(iden, block, mod, par) }
  def structDecl: Parser[Instruction] = (modifier <~ "struct") ~ identLazy ~ opt("extends" ~> ident2) ~ block ^^ { case mod ~ iden ~ par ~ block => StructDecl(iden, block, mod, par) }
  def typedef: Parser[Instruction] = "typedef" ~> types ~ identLazy ^^ { case _1 ~ _2 => TypeDef(_2, _1) }
  def templateUse: Parser[Instruction] = ident2 ~ ident ~ block ^^ {case iden ~ name ~ instr => TemplateUse(iden, name, instr)}
  def templateDesc: Parser[Instruction] = (modifier <~ "template") ~ identLazy ~ opt("extends" ~> ident2) ~ instruction ^^ {case mod ~ name ~ parent ~ instr => TemplateDecl(name, instr, mod, parent)}
  def importInst: Parser[Instruction] = "import"~>ident2 ~ opt("as" ~> ident2) ^^ {case file ~ alias => Import(file, alias.getOrElse(null))}
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


  def enumInstr: Parser[EnumDecl] = (modifier ~ ("enum" ~> identLazy) ~ opt("("~>repsep(enumField,",")<~")") <~ "{") ~ repsep(enumValue, ",") <~ "}" ^^ 
                                    (p => EnumDecl(p._1._1._2, p._1._2.getOrElse(List()), p._2, p._1._1._1))
  def enumField: Parser[EnumField] = types ~ identLazy ^^ { p => EnumField(p._2, p._1) }
  def enumValue: Parser[EnumValue] = identLazy ~ opt("("~>repsep(exprNoTuple,",")<~")") ^^ (p => EnumValue(p._1, p._2.getOrElse(List())))

  def varAssignment: Parser[Instruction] = (rep1sep(identLazy2, ",") ~ assignmentOp ~ expr) ^^ (p => 
    {
      val identifiers = p._1._1.map(Identifier.fromString(_))
      VariableAssigment(identifiers.map(Left(_)), p._1._2, p._2)
    })

  def varDeclaration: Parser[Instruction] = (modifier ~ types ~ rep1sep(identLazy, ",") ~ opt("=" ~> expr)) ^^ (p => {
      val mod = p._1._1._1
      val decl = p._1._2.map(VariableDecl(_, p._1._1._2, mod))
      val identifiers = p._1._2.map(Identifier.fromString(_))
      if (!mod.isEntity && p._2.isEmpty){
        InstructionList(decl ::: List(VariableAssigment(identifiers.map(Left(_)), ":=", DefaultValue)))
      }
      else if (!mod.isEntity && !p._2.isEmpty){
        InstructionList(decl ::: List(VariableAssigment(identifiers.map(Left(_)), "=", p._2.get)))
      }
      else{
        InstructionList(decl)
      }
    }) // Variable Dec
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

  def selectorFilterValue = sfRange | sfNumber | sfString | sfNamespacedName | sfIdentifier | sfNumber2 | sfNBT
  def selectorFilter: Parser[(String, SelectorFilterValue)] = (identLazy <~ "=") ~ selectorFilterValue ^^ { p => (p._1, p._2) }
  def selector: Parser[Selector] = ("@a" | "@s" | "@e" | "@p" | "@r") ~ opt("[" ~> rep1sep(selectorFilter, ",") <~ "]") ^^ { p => Selector.parse(p._1, p._2.getOrElse(List())) }
  def selectorStr : Parser[String] = (selector ^^ (_.toString()))

  def stringLit2: Parser[String] = stringLit ^^ {p => p.replaceAllLiterally("◘", "\\\"")}
  def namespacedName = ident ~ ":" ~ ident2 ^^ { case a ~ b ~ c => NamespacedName(a+b+c) }
  def exprBottom: Parser[Expression] = 
    (numericLit <~ "..") ~ numericLit ^^ (p => RangeValue(IntValue(p._1.toInt), IntValue(p._2.toInt)))
    | floatValue ^^ (p => FloatValue(p))
    | numericLit ^^ (p => IntValue(p.toInt))
    | "-" ~> numericLit ^^ (p => IntValue(f"-$p".toInt))
    | "-" ~> exprBottom ^^ (BinaryOperation("-", IntValue(0), _))
    | "!" ~> exprBottom ^^ (UnaryOperation("!", _))
    | "true" ^^^ BoolValue(true)
    | "false" ^^^ BoolValue(false)
    | "null" ^^^ NullValue
    | namespacedName
    | stringLit2 ^^ (StringValue(_))
    | lambda
    | "new" ~> identLazy2 ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ a ~ b => ConstructorCall(f, a ::: List(LambdaValue(List(), b))) }
    | "new" ~> identLazy2 ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ { case f ~ a => ConstructorCall(f, a) }
    | identLazy2 ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ~ block ^^ { case f ~ a ~ b => FunctionCallValue(VariableValue(f), a ::: List(LambdaValue(List(), b))) }
    | identLazy2 ~ rep1("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ (p => p._2.foldLeft[Expression](VariableValue(p._1))((b, a) => FunctionCallValue(b, a)))
    | identLazy2 ^^ (VariableValue(_))
    | identTag ^^ (VariableValue(_))
    | "(" ~> expr <~ ")"
    | json ^^ (JsonValue(_))
    | selector ^^ (SelectorValue(_))

  def comparator: Parser[String] = "<" | "<=" | ">=" | ">" | "==" | "!="

  def exprArray: Parser[Expression] = exprBottom ~ rep("[" ~> expr <~ "]") ^^ {case e ~ g => g.foldLeft(e)((e, i) => ArrayGetValue(e, i))}
  def exprMod: Parser[Expression] = exprArray ~ rep("%" ~> exprMod) ^^ {unpack("%", _)}
  def exprDiv: Parser[Expression] = exprMod ~ rep(("/" | "\\") ~> exprDiv) ^^ {unpack("/", _)}
  def exprMult: Parser[Expression] = exprDiv ~ rep("*" ~> exprMult) ^^ {unpack("*", _)}
  def exprSub: Parser[Expression] = exprMult ~ rep("-" ~> exprSub) ^^ {unpack("-", _)}
  def exprAdd: Parser[Expression] = exprSub ~ rep("+" ~> exprAdd) ^^ {unpack("+", _)}
  def exprComp: Parser[Expression] = exprAdd ~ rep(comparator ~ exprComp) ^^ {unpack(_)}
  def exprAnd: Parser[Expression] = exprComp ~ rep("&&" ~> exprAnd) ^^ {unpack("&&", _)}
  def exprOr: Parser[Expression] = exprAnd ~ rep("||" ~> exprOr) ^^ {unpack("||", _)}
  def exprNoTuple = exprOr
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

  def modifierSub: Parser[String] = ("override" | "lazy" | "inline"| "scoreboard" | "ticking" | "loading" | "helper" | "static")
  def modifier: Parser[Modifier] = 
    (opt(stringLit2 ~ stringLit2 ~ stringLit2)~opt("public" | "private" | "protected") ~ rep(modifierSub) ~ rep(identTag)) ^^ 
    { case doc ~ protection ~ subs ~ tags => {
      val mod = new Modifier()
      mod.tags.addAll(tags)
      protection match{
        case Some("public") => mod.protection = Protection.Public
        case Some("private") => mod.protection = Protection.Private
        case Some("protected") => mod.protection = Protection.Protected
        case _ => {}
      }

      if subs.contains("override") then {mod.isOverride = true}
      if subs.contains("lazy")     then {mod.isLazy = true}
      if subs.contains("inline")   then {mod.isInline = true}
      if subs.contains("scoreboard")   then {mod.isEntity = true}
      if subs.contains("ticking")   then {mod.isTicking = true}
      if subs.contains("loading")   then {mod.isLoading = true}
      if subs.contains("helper")   then {mod.isHelper = true}
      if subs.contains("static")   then {mod.isStatic = true}
      
      mod
    }
  }

  def program: Parser[Instruction] = rep(instruction) ^^ (InstructionList(_))

  /** Print an error message, together with the position where it occured. */
  case class TypeError(t: Instruction, msg: String) extends Exception(msg) {
    override def toString =
      msg + "\n" + t
  }

  def parse(file: String, args: String): Option[Instruction] = {
    val tokens = new lexical.Scanner(Preparser.parse(args))
    phrase(program)(tokens) match {
      case Success(trees, _) =>
        Some(trees)
      case e =>
        println(f"Error in file '${file}'': ${e}")
        None
    }
  }
}
