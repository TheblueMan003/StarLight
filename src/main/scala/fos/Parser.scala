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

object Parser extends StandardTokenParsers{
  lexical.delimiters ++= List("(", ")", "\\", ".", ":", "=", "->", "{", "}", ",", "*", "[", "]", "/", "+", "-", "*", "/", "\\", "%", "&&", "||", "=>", ";",
                              "+=", "-=", "/=", "*=", "?=", ":=", "%", "@", "@e", "@a", "@s", "@r", "@p", "~", "^", "<=", "==", ">=", "<", ">", "!=", "%%%")
  lexical.reserved   ++= List("bool", "int", "float", "void", "string", "json", "true", "false", "if", "then", "else", "return", "switch", "for", "do", "while",
                              "as", "at", "with", 
                              "var", "val", "def", "package", "struct", "enum", "lazy", "jsonfile",
                              "public", "protected", "private", "entity", "scoreboard",
                              "ticking", "loading")


  def block: Parser[Instruction] = "{" ~> rep(instruction) <~ "}" ^^ (p => InstructionBlock(p))
  def assignmentOp: Parser[String] = ("=" | "+=" | "-=" | "*=" | "/=" | ":=")

  def ident2: Parser[String] = rep1sep(ident, ".") ^^ { p => p.reduce(_ + "." + _)}



  def floatValue: Parser[Double] = (numericLit <~ ".") ~ numericLit ^^ { p => (p._1 + "." + p._2).toDouble } 
  def jsonValueStr: Parser[String] = (floatValue ^^ { p => p.toString() } 
                                    | numericLit | stringLit ^^ (Utils.stringify(_)) | jsonStr 
                                    | ("[" ~> repsep(jsonValueStr, ",")) <~ "]" ^^ { p => "[" + p.reduce(_ + ","+ _) + "]" }
                                    )^^ { p => p.toString }
  def jsonKeypairStr: Parser[String] = ((stringLit ^^ (Utils.stringify(_)) | ident) <~ ":") ~ jsonValueStr ^^ { p => p._1 + ":" + p._2 }
  def jsonStr: Parser[String] = "{" ~> rep1sep(jsonKeypairStr, ",") <~ "}" ^^ { p => "{" + p.reduce(_ + ","+ _) + "}" }


  def jsonValue: Parser[JSONElement] = floatValue ^^ { p => JsonFloat(p) } 
                                    | numericLit ^^ { p => JsonInt(p.toInt) } 
                                    | stringLit ^^ { p => JsonString(p) }
                                    | ident2 ^^ { p => JsonIdentifier(p) }
                                    | json
                                    | ("[" ~> repsep(jsonValue, ",")) <~ "]" ^^ { p => JsonArray(p)}
                                    
  def jsonKeypair: Parser[(String, JSONElement)] = ((stringLit | ident) <~ ":") ~ jsonValue ^^ { p => (p._1, p._2) }
  def json: Parser[JSONElement] = "{" ~> rep1sep(jsonKeypair, ",") <~ "}" ^^ { p => JsonDictionary(p.toMap) }



  def argument: Parser[Argument] = types ~ ident ~ opt("=" ~> expr) ^^ { p => Argument(p._1._2, p._1._1, p._2) }

  def instruction: Parser[Instruction] = 
      ((((("def" ~> modifier) ~ ident) <~ "(") ~ repsep(argument, ",")) <~ ")") ~ instruction ^^ (p => FunctionDecl(p._1._1._2, p._2, VoidType, p._1._2, p._1._1._1))
      | ((((((opt("def") ~> modifier) ~ types) ~ ident) <~ "(") ~ repsep(argument, ",")) <~ ")") ~ instruction ^^ (p => FunctionDecl(p._1._1._2, p._2, p._1._1._1._2, p._1._2, p._1._1._1._1))
      | ((ident2 <~ "(") ~ repsep(exprNoTuple, ",")) <~ ")" ^^ (p => FunctionCall(p._1, p._2)) // Function Call
      | "package" ~> ident2 ~ program ^^ (p => Package(p._1, p._2)) // Package
      | (modifier <~ "struct") ~ ident ~ block ^^ (p => StructDecl(p._1._2, p._2, p._1._1)) // Struct Dec
      | varDeclaration
      | varAssignment
      | "%%%" ~> rep(ident2 | jsonStr | selectorStr | "~" | "^") <~ "%%%" ^^ { p => CMD(p.reduce(_ + " " + _)) }
      | ifs
      | "return" ~> expr ^^ (Return(_))
      | block
      | switch | whileLoop | doWhileLoop | forLoop | jsonFile
      | "as" ~> expr ~ instruction ^^ (p => With(p._1, BoolValue(false), BoolValue(true), p._2))
      | "at" ~> expr ~ instruction ^^ (p => At(p._1, p._2))
      | withInstr
      | enumInstr

      

  def jsonFile: Parser[Instruction] = "jsonfile" ~> ident2 ~ json ^^ (p => JSONFile(p._1, p._2))
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


  def enumInstr: Parser[EnumDecl] = (modifier ~ ("enum" ~> ident) ~ opt("("~>repsep(enumField,",")<~")") <~ "{") ~ repsep(enumValue, ",") <~ "}" ^^ 
                                    (p => EnumDecl(p._1._1._2, p._1._2.getOrElse(List()), p._2, p._1._1._1))
  def enumField: Parser[EnumField] = types ~ ident ^^ { p => EnumField(p._2, p._1) }
  def enumValue: Parser[EnumValue] = ident ~ opt("("~>repsep(exprNoTuple,",")<~")") ^^ (p => EnumValue(p._1, p._2.getOrElse(List())))

  def varAssignment: Parser[Instruction] = (rep1sep(ident2, ",") ~ assignmentOp ~ expr) ^^ (p => 
    {
      val identifiers = p._1._1.map(Identifier.fromString(_))
      VariableAssigment(identifiers, p._1._2, p._2)
    })

  def varDeclaration: Parser[Instruction] = (modifier ~ types ~ rep1sep(ident, ",") ~ opt("=" ~> expr)) ^^ (p => {
      val mod = p._1._1._1
      val decl = p._1._2.map(VariableDecl(_, p._1._1._2, mod))
      val identifiers = p._1._2.map(Identifier.fromString(_))
      if (!mod.isEntity && p._2.isEmpty){
        InstructionList(decl ::: List(VariableAssigment(identifiers, ":=", DefaultValue)))
      }
      else if (!mod.isEntity && !p._2.isEmpty){
        InstructionList(decl ::: List(VariableAssigment(identifiers, "=", p._2.get)))
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




  def sfRange: Parser[SelectorFilterValue] = (floatValue <~ "..") ~ floatValue ^^ (p => SelectorRange(p._1, p._2))
  def sfNumber: Parser[SelectorFilterValue] = floatValue ^^ (SelectorNumber(_))
  def sfNumber2: Parser[SelectorFilterValue] = numericLit ^^ (p => SelectorNumber(p.toInt))
  def sfString: Parser[SelectorFilterValue] = stringLit ^^ (SelectorString(_))
  def sfIdentifier: Parser[SelectorFilterValue] = ident2 ^^ (SelectorIdentifier(_))

  def selectorFilterValue = sfRange | sfNumber | sfString | sfIdentifier | sfNumber2
  def selectorFilter: Parser[(String, SelectorFilterValue)] = (ident <~ "=") ~ selectorFilterValue ^^ { p => (p._1, p._2) }
  def selector: Parser[Selector] = ("@a" | "@s" | "@e" | "@p" | "@r") ~ opt("[" ~> rep1sep(selectorFilter, ",") <~ "]") ^^ { p => Selector.parse(p._1, p._2.getOrElse(List())) }
  def selectorStr : Parser[String] = (selector ^^ (_.toString()))

  def exprBottom: Parser[Expression] = 
    floatValue ^^ (p => FloatValue(p))
    | numericLit ^^ (p => IntValue(p.toInt))
    | "-" ~> exprBottom ^^ (BinaryOperation("-", IntValue(0), _))
    | "true" ^^^ BoolValue(true)
    | "false" ^^^ BoolValue(false)
    | stringLit ^^ (StringValue(_))
    | ((ident2 <~ "(") ~ repsep(expr, ",")) <~ ")" ^^ (p => FunctionCallValue(p._1, p._2))
    | ident2 ^^ (VariableValue(_))
    | "(" ~> expr <~ ")"
    | json ^^ (JsonValue(_))
    | selector ^^ (SelectorValue(_))

  def comparator: Parser[String] = "<" | "<=" | ">=" | ">" | "==" | "!="

  def exprMod: Parser[Expression] = exprBottom ~ rep("%" ~> exprMod) ^^ {unpack("%", _)}
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


  
  def nonRecTypes: Parser[Type] = 
    "int" ^^^ IntType |
    "float" ^^^ FloatType |
    "bool" ^^^ BoolType |
    "void" ^^^ VoidType |
    "string" ^^^ StringType |
    "json" ^^^ JsonType |
    "entity" ^^^ EntityType |
    ident2 ^^ { IdentifierType(_) } |
    (("(" ~> types) ~ rep1("," ~> types)) <~ ")" ^^ (p => if p._2.length > 0 then TupleType(p._1 :: p._2) else p._1)

  def types: Parser[Type] = ("(" ~> repsep(nonRecTypes, ",") <~ ")"<~ "=>") ~ types ^^ (p => FuncType(p._1, p._2)) |
                            (nonRecTypes <~ "=>") ~ types ^^ (p => FuncType(List(p._1), p._2)) |
                            ((nonRecTypes <~ "[") ~ numericLit) <~ "]" ^^ (p => ArrayType(p._1, p._2)) |
                            nonRecTypes

  def modifierSub: Parser[String] = ("override" | "lazy" | "inline"| "scoreboard" | "ticking" | "loading")
  def modifier: Parser[Modifier] = 
    (opt("public" | "private" | "protected") ~ rep(modifierSub)) ^^ (p => {
      val mod = new Modifier()
      p._1 match{
        case Some("public") => mod.protection = Protection.Public
        case Some("private") => mod.protection = Protection.Private
        case Some("protected") => mod.protection = Protection.Protected
        case _ => {}
      }

      if p._2.contains("override") then {mod.isOverride = true}
      if p._2.contains("lazy")     then {mod.isLazy = true}
      if p._2.contains("inline")   then {mod.isInline = true}
      if p._2.contains("scoreboard")   then {mod.isEntity = true}
      if p._2.contains("ticking")   then {mod.isTicking = true}
      if p._2.contains("loading")   then {mod.isLoading = true}
      
      mod
    })

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
