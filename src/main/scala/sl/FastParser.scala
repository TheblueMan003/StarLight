package sl

import fastparse._, ScalaWhitespace._
import objects.types.*
import objects.{Modifier, Protection, Identifier, Variable, EnumValue, EnumField}
import sl.Compilation.Selector.*
import objects.{Modifier, Protection}
import sl.files.CacheAST

object FastParser {
    val reserved = Set(
        "true",
        "false",
        "if",
        "then",
        "else",
        "return",
        "switch",
        "for",
        "do",
        "while",
        "by",
        "is",
        "as",
        "at",
        "with",
        "to",
        "import",
        "template",
        "null",
        "typedef",
        "foreach",
        "in",
        "not",
        "def",
        "extension",
        "package",
        "struct",
        "enum",
        "class",
        "interface",
        "lazy",
        "macro",
        "jsonfile",
        "blocktag",
        "itemtag",
        "entitytag",
        "throw",
        "try",
        "catch",
        "finally",
        "public",
        "protected",
        "private",
        "scoreboard",
        "forgenerate",
        "from",
        "rotated",
        "facing",
        "align",
        "case",
        "default",
        "ticking",
        "loading",
        "predicate",
        "extends",
        "implements",
        "new",
        "const",
        "static",
        "virtual",
        "abstract",
        "override",
        "repeat",
        "sleep",
        "async",
        "await",
        "assert",
        "operator",
        "break",
        "delete",
        "timeline"
    )

    def keywordD[$: P](name: String): P[Unit] = P(CharIn("a-z", "A-Z", "_") ~~ CharIn("a-z", "A-Z", "0-9", "_").repX ~~ !CharIn("a-z", "A-Z", "0-9", "_")).!.filter(name == _).map(_ => ())
    // BASE PARSER
    def ident[$: P]: P[String] = 
        P(CharIn("a-z", "A-Z", "_").! ~~ CharIn("a-z", "A-Z", "0-9", "_").repX).!.map(_.toString).filter(!reserved.contains(_))
    
    def numericLit[$: P]: P[String] =
        P(CharIn("0-9").repX(1)).!.map(_.toString)
    
    // Custom parsers
    def assignmentOp[$: P]: P[String] = 
        P(("=" | "+=" | "-=" | "*=" | "/=" | ":=" | "%=" | "^=" | "|=" | "&=" | "<<=" | ">>=" | "::=" | ">:=" | "<:=" | "-:=").!).map(_.toString)

    def nonEscapedChars[$: P]: P[String] = P(CharsWhile(c => c != '\\' && c != '"').map(_.toString))

    def hexDigits[$: P]: P[String] = P(CharsWhileIn("0-9a-fA-F").map(_.toString))

    def escapeSequences[$: P]: P[String] = P(
        "\\" ~ (
        "n".map(_ => "\n") |
        "t".map(_ => "\t") |
        "r".map(_ => "\r") |
        "b".map(_ => "\b") |
        "f".map(_ => "\f") |
        "\"".map(_ => "\"") |
        "\\".map(_ => "\\") |
        "x" ~ (hexDigits.map(s => Integer.parseInt(s.toString, 16).toString()))
        )
    )

    def stringChar[$: P]: P[String] = P(nonEscapedChars | escapeSequences)

    def stringLit[$: P]: P[String] = {
        P("\"" ~~ stringChar.rep.map(_.mkString).! ~~ "\"").map(lit => lit)
    }
    // Identifiers

    def subident[$: P]: P[String] = ("$".? ~~ ident).!
    def ident2[$: P]: P[String] = (subident.repX(1, sep = ".")).map(_.mkString("."))
    def subident2[$: P]: P[String] = ("$" ~~ ident2).!
    def identLazy[$: P]: P[String] = (subident ~~ subident2.rep).map {
        case (first, rest) => (first :: rest.toList).mkString(".")
    }
    def identLazyForce[$: P]: P[String] = P("$" ~~ ident)
    def identLazy2[$: P]: P[String] = P(identLazy.rep(1, sep = ".").map(_.mkString(".")))

    def identLazyCMD[$: P]: P[String] = P(subident.rep(1, sep = ".").map(_.mkString(".")))
    def identTag[$: P]: P[String] = P("@".! ~~ identLazy2).filter(_._2.size >= 2).map {
        case (tag, ident) => tag + ident
    }

    def identFunction[$: P]: P[String] = identTag | identLazy2

    def floatValue[$: P]: P[Double] = P(CharIn("0-9").rep(1) ~ "." ~ CharIn("0-9").rep(1)).!.map(_.toDouble)
    
    def block[$: P]: P[Instruction] = P(Index ~ "{" ~ instruction.rep ~ "}").map {
        case (index, instructions) => InstructionBlock(instructions.toList).setPos(index)
    }
    def classBlock[$: P]: P[Instruction] = P(Index ~ "{" ~ classInstruction.rep ~ "}").map {
        case (index, instructions) => InstructionBlock(instructions.toList).setPos(index)
    }
    def blockExtension[$: P]: P[Instruction] = P(Index ~ "{" ~ (functionDecl | optFunctionDecl).rep ~ "}").map {
        case (index, instructions) => InstructionBlock(instructions.toList).setPos(index)
    }
    def keyword_or_subident[$: P]: P[String] = P(
        ("$".!.? ~ (ident | anyKeyword)).map {
            case (sub, keyword) => sub.getOrElse("") + keyword
        }
    )
    def keyword_or_ident2[$: P]: P[String] = P(
        keyword_or_subident.rep(1, sep = ".").map(_.mkString("."))
    )
    def keyword_or_subident2[$: P]: P[String] = P(
        ("$".! ~ (ident | anyKeyword)).map {
            case (sub, keyword) => sub + keyword
        }
    )
    def keyword_or_identLazy[$: P]: P[String] = P(
        (keyword_or_subident ~ keyword_or_subident2.rep).map{(a,b) => (a::b.toList).mkString("")}
    )
    def keyword_or_identLazyForce[$: P]: P[String] = P(
        ("$" ~ (ident | anyKeyword)).map { case sub => sub }
    )
    def keyword_or_identLazy2[$: P]: P[String] = P(
        keyword_or_identLazy.rep(1, sep = ".").map(_.mkString("."))
    )
    def jsonValueStr[$: P]: P[String] = P(
        floatValue.map(_.toString) |
        numericLit |
        stringLit2.map(Utils.stringify) |
        jsonStr |
        ("[" ~ jsonValueStr.rep(sep = ",") ~ "]").map(_.mkString("[", ",", "]"))
    )
    def jsonKeypairStr[$: P]: P[String] = P(
        ((stringLit2.map(Utils.stringify) | identLazy) ~ ":" ~ jsonValueStr).map {
            case (key, value) => s"""$key:$value"""
        } |
        ("§§§" ~ stringLit ~ "§§§").map{ p => p.replaceAll("@", ":") }
    )
    def jsonStr[$: P]: P[String] = P(("{" ~ jsonKeypairStr.rep(sep = ",").! ~ "}").map(s => s.mkString("{", ",", "}")))
    def jsonValue[$: P]: P[JSONElement] = P((exprNoTuple ~ ident.?).map{
        (p, t) => Utils.simplifyJsonExpression(JsonExpression(p, t.getOrElse(null)))
    })
    def jsonArray[$: P]: P[JSONElement] = P(
        ("[" ~ jsonValue.rep(sep = ",") ~ "]").map { values =>
            JsonArray(values.toList)
        }
    )
    def jsonKeypair[$: P]: P[(String, JSONElement)] = {
        ((stringLit2 | identLazy) ~ ":" ~ jsonValue).map { p => (p._1, p._2) } |
        ("§§§" ~ stringLit ~ "§§§").map { p =>
            {
                val q = p.replaceAll("@", ":").split(":");
                (q(0), JsonExpression(parseExpression(q(1), true), null))
            }
        }
    }
    def jsonDic[$: P]: P[JSONElement] = ("{" ~ jsonKeypair.rep(sep= ",") ~ "}").map {
        p => JsonDictionary(p.toMap)
    }
    def json[$: P]: P[JSONElement] = jsonDic | jsonArray
    def argument[$: P]: P[Argument] = {
        (types ~ identLazy ~ ("=" ~ exprNoTuple).?).map { case (typ, name, value)  => Argument(name, typ, value) } |
        (identLazy ~ ":" ~ types ~ ("=" ~ exprNoTuple).?).map { case (name, typ, value)  => Argument(name, typ, value) }
    }
    def arguments[$: P]: P[List[Argument]] = ("(" ~ argument.rep(sep = ",") ~ ")").map(_.toList)
    def templateArgument[$: P]: P[TemplateArgument] = (identLazy ~ ("=" ~ exprBottom).?).map{ case (n, v) =>
      TemplateArgument(n, v)
    }
    def templateArguments[$: P]: P[List[TemplateArgument]] = (("<" ~ templateArgument.rep(sep=",") ~ ">").?).map {
        case None           =>  List()
        case Some(value)    =>  value.toList
    }
    def instruction2[$: P]: P[Instruction] = P(positioned
     (timeline
      | functionDecl
      | functionCall
      | dotFunctionCall
      | selectorFunctionCall
      | sleepInstr
      | awaitInstr
      | assertInstr
      | packageInstr
      | structDecl
      | extensionDecl
      | caseStruct
      | classDecl
      | interfaceDecl
      | caseClassDecl
      | templateUse
      | propertyDeclaration
      | varDeclaration
      | varAssignment
      | arrayAssign
      | ("%%%" ~ stringLit2 ~ "%%%").map { p => CMD(p) }
      | ifs
      | (keywordD("return") ~ expr.?).map { case e => Return(e.getOrElse(NullValue)) }
      | keywordD("break").!.map(_ => Break)
      | block
      | switch | whileLoop | doWhileLoop | forLoop | forEachLoop | repeatLoop | jsonFile
      | (keywordD("as") ~ "(" ~ exprNoTuple ~ ")" ~ instruction).map { case (e, i) =>
        With(e, BoolValue(false), BoolValue(true), i, null)
      }
      | (keywordD("at") ~ "(" ~ exprNoTuple.rep(sep = ",") ~ ")" ~ instruction).map {
        case (e, i) => Execute(AtType, e.toList, i)
      }
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
      | constructorCall.map { case c => FreeConstructorCall(c) }
      | destructorCall)
    )
    def instruction[$: P]: P[Instruction] = instruction2 ~ ";".?

    def classInstruction[$: P]: P[Instruction] = P(positioned((functionDecl 
        | constructorDecl
        | operatorFunctionDecl
        | structDecl
        | extensionDecl
        | caseStruct
        | classDecl
        | interfaceDecl
        | caseClassDecl
        | templateUse
        | propertyDeclaration
        | varDeclaration
        | ifs
        | jsonFile
        | enumInstr
        | forgenerate
        | importShortInst
        | importInst
        | fromImportInst
        | templateDesc
        | typedef
        | foreach
        | predicate
        | blocktag)
        )
    )

    def timeline_time[$: P]: P[Expression] = positioned(
        ((exprNoTuple ~ (keywordD("hours") | keywordD("h"))).? ~
        (exprNoTuple ~ (keywordD("minutes") | keywordD("m"))).? ~ 
        (exprNoTuple ~ (keywordD("seconds") | keywordD("s"))).? ~
        (exprNoTuple ~ (keywordD("ticks") | keywordD("t")).?).?).map{
            case (hours, minutes, seconds, ticks) => {
                val time = List(
                    hours  .map(BinaryOperation("*", _, IntValue(20*60*60))), 
                    minutes.map(BinaryOperation("*", _, IntValue(20*60))), 
                    seconds.map(BinaryOperation("*", _, IntValue(20))), 
                    ticks).flatten

                if (time.isEmpty) IntValue(0)
                else time.reduceLeft((a, b) => BinaryOperation("+", a, b))
            }
        }
    )
    def timeline_event[$: P]: P[TimelineElement] = positioned(
        (keywordD("event") ~ "(" ~ expr ~ ")" ~ instruction).map {
            case (expr, instr) => EventTimelineElement(expr, instr)
        }
    )
    def timeline_for[$: P]: P[TimelineElement] = positioned(
        (keywordD("for") ~ "(" ~ timeline_time ~ ")" ~ instruction).map {
            case (expr, instr) => ForLengthTimelineElement(expr, instr)
        }
    )
    def timeline_after[$: P]: P[TimelineElement] = positioned(
        (keywordD("after") ~ "(" ~ timeline_time ~ ")" ~ instruction).map {
            case (expr, instr) => DelayTimelineElement(expr, instr)
        }
    )
    def timeline_until[$: P]: P[TimelineElement] = positioned(
        (keywordD("until") ~ "(" ~ expr ~ ")" ~ instruction).map {
            case (expr, instr) => UntilTimelineElement(expr, instr)
        }
    )
    def timeline_while[$: P]: P[TimelineElement] = positioned(
        (keywordD("while") ~ "(" ~ expr ~ ")" ~ instruction).map {
            case (expr, instr) => WhileTimelineElement(expr, instr)
        }
    )
    def timeline_direct[$: P]: P[TimelineElement] = positioned(
        (instruction).map {
            case (instr) => DirectTimelineElement(instr)
        }
    )
    def timeline_elements[$: P]: P[TimelineElement] = positioned(
        (timeline_event | timeline_for | timeline_after | timeline_until | timeline_while | timeline_direct)
    )
    def timeline[$: P]: P[Instruction] = positioned(
        (keywordD("timeline") ~ identLazy ~ "{" ~ timeline_elements.rep ~ "}").map {
            case (name, elements) => Timeline(name, elements.toList)
        }
    )
    def functionDecl[$: P]: P[FunctionDecl] = {
        (doc ~ keywordD("def") ~ modifier("function_short") ~ arguments ~ instruction).map { 
            case (doc, mod, a, i) => {
                FunctionDecl("~", i, VoidType, a, List(), mod.withDoc(doc))
            }
        }
        | (doc ~ keywordD("def") ~ modifier("abstract_function") ~ identLazy ~ typeArgument ~ arguments).map {
            case (doc, mod, n, at, a) => {
                FunctionDecl(
                    n,
                    InstructionList(List()),
                    VoidType,
                    a,
                    at,
                    mod.withDoc(doc)
                )
            }
        }
        | (doc ~ keywordD("def").? ~ modifier("abstract_function") ~ types ~ identLazy ~ typeArgument ~ arguments).map {
            case (doc, mod, t, n, at, a) =>{
                FunctionDecl(n, InstructionList(List()), t, a, at, mod.withDoc(doc))
            }
        }
        | (doc ~ keywordD("def").? ~ modifier("function") ~ identLazy ~ typeArgument ~ arguments ~ functionInstruction).map {
            case (doc, mod, n, at, a, i) => {
                FunctionDecl(n, i, VoidType, a, at, mod.withDoc(doc))
            }
        }
        | (doc ~ keywordD("def").? ~ modifier("function") ~ types ~ identLazy ~ typeArgument ~ arguments ~ functionInstruction).map {
            case (doc, mod, t, n, at, a, i) => {
                FunctionDecl(n, i, t, a, at, mod.withDoc(doc))
            }
        }
    }
    def constructorDecl[$: P]: P[FunctionDecl] = {
        (doc ~ modifier("function") ~ identLazy ~ typeArgument ~ arguments ~ functionInstruction).map {
            case (doc, mod, n, at, a, i) => {
                FunctionDecl("__init__", i, VoidType, a, at, mod.withDoc(doc))
            }
      }
    }
    def operator[$: P]: P[String] = assignmentOp | ("==" | "!=" | "<" | "<=" | ">" | ">=" | keywordD("delete") | "()").!
    def operatorFunctionDecl[$: P]: P[FunctionDecl] = {
         (doc ~ keywordD("def") ~ modifier("function") ~ keywordD("operator") ~ operator ~ typeArgument ~ arguments ~ functionInstruction).map {
            case (doc, mod, op, at, a, i) => {
                FunctionDecl(Utils.getOpFunctionName(op), i, VoidType, a, at, mod.withDoc(doc))
            }
        }
        | (doc ~ keywordD("def").? ~ modifier("function") ~ types ~ keywordD("operator") ~ operator ~ typeArgument ~ arguments ~ functionInstruction).map {
            case (doc, mod, t, op, at, a, i) => {
                FunctionDecl(Utils.getOpFunctionName(op), i, t, a, at, mod.withDoc(doc))
            }
        }
    }
    def optFunctionDecl[$: P]: P[OptionalFunctionDecl] = {
        (doc ~ keywordD("def") ~ modifier("function") ~ identLazy2 ~ typeArgument ~ arguments ~ keywordD("from") ~ identLazy2 ~ keywordD("as") ~ identLazy2).map {
            case (doc, mod, n, at, a, lib, alias)=>{
                OptionalFunctionDecl(
                    alias,
                    n,
                    lib,
                    VoidType,
                    a,
                    at,
                    mod.withDoc(doc)
                )
            }
      }
      | (doc ~ keywordD("def").? ~ modifier("function") ~ types ~ identLazy2 ~ typeArgument ~ arguments ~ keywordD("from") ~ identLazy2 ~ keywordD("as") ~ identLazy2).map {
            case (doc, mod, t, n, at, a, lib, alias) =>{
                OptionalFunctionDecl(alias,n,lib, t, a, at, mod.withDoc(doc))
            }
        }
    }
    def functionInstruction[$: P]: P[Instruction] = (instruction | ("=" ~ exprNoTuple).map (p =>
      InstructionList(List(Return(p)))
    ))
    def functionCall[$: P]: P[FunctionCall] = positioned(
        (identFunction ~ typeVariables ~ "(" ~ exprNoTuple.rep(sep=",") ~ ")" ~ block).map { 
            case (f, t, e, b) => {
                FunctionCall(f, e.toList ::: List(LambdaValue(List(), b, null)), t)
            }
        } // Function Call
        | (identFunction ~ typeVariables ~ "(" ~ exprNoTuple.rep(sep=",") ~ ")").map{ 
            case (f, t, e) => FunctionCall(f, e.toList, t) 
        } // Function Call
        | (identFunction ~ typeVariables ~ block).map { 
            case (f, t, b) => {
                FunctionCall(f, List(LambdaValue(List(), b, null)), t)
            }
        }
    )
    def dotFunctionCall[$: P]: P[Instruction] = {
        P(Index ~ exprBottom ~ "." ~ functionCall).map {
            case (index, e, f) => DotCall(e, f).setPos(index)
        }
    }
    def selectorFunctionCall[$: P]: P[Instruction] = {
        P(Index ~ selector ~ "." ~ functionCall).map {
            case (index, selector, f) => DotCall(SelectorValue(selector), f).setPos(index)
        }
    }
    def continuation[$: P]: P[Instruction] = P(
        (";".? ~ instruction.rep).map {
            case instrs => InstructionList(instrs.toList)
        }
    )
    def sleepInstr[$: P]: P[Instruction] = P(
        (Index ~ keywordD("sleep") ~ exprNoTuple ~ continuation).map {
            case (index, expr, block) => Sleep(expr, block).setPos(index)
        }
    )
    def awaitInstr[$: P]: P[Instruction] = P(
        (Index ~ keywordD("await") ~ functionCall ~ continuation).map {
            case (index, expr, block) => Await(expr, block).setPos(index)
        }
    )
    def assertInstr[$: P]: P[Instruction] = P(
        (Index ~ keywordD("assert") ~ exprNoTuple ~ continuation).map {
            case (index, expr, block) => Assert(expr, block).setPos(index)
        }
    )
    def anyKeyword[$: P]: P[String] =   P(("true" |
                                        "false" |
                                        "if" |
                                        "then" |
                                        "else" |
                                        "return" |
                                        "switch" |
                                        "for" |
                                        "do" |
                                        "while" |
                                        "by" |
                                        "is" |
                                        "as" |
                                        "at" |
                                        "with" |
                                        "to" |
                                        "import" |
                                        "template" |
                                        "null" |
                                        "typedef" |
                                        "foreach" |
                                        "in" |
                                        "not" |
                                        "def" |
                                        "extension" |
                                        "package" |
                                        "struct" |
                                        "enum" |
                                        "class" |
                                        "interface" |
                                        "lazy" |
                                        "macro" |
                                        "jsonfile" |
                                        "blocktag" |
                                        "itemtag" |
                                        "entitytag" |
                                        "throw" |
                                        "try" |
                                        "catch" |
                                        "finally" |
                                        "public" |
                                        "protected" |
                                        "private" |
                                        "scoreboard" |
                                        "forgenerate" |
                                        "from" |
                                        "rotated" |
                                        "facing" |
                                        "align" |
                                        "case" |
                                        "default" |
                                        "ticking" |
                                        "loading" |
                                        "predicate" |
                                        "extends" |
                                        "implements" |
                                        "new" |
                                        "const" |
                                        "static" |
                                        "virtual" |
                                        "abstract" |
                                        "override" |
                                        "repeat" |
                                        "sleep" |
                                        "async" |
                                        "await" |
                                        "assert" |
                                        "operator" |
                                        "break" |
                                        "delete").!.map(_.toString))
    def throwError[$: P]: P[Instruction] = positioned((keywordD("throw") ~ exprNoTuple).map {
        case e => Throw(e)
    })
    def tryCatchFinalyBlock[$: P]: P[Instruction] = positioned(
        (keywordD("try") ~ block ~ (keywordD("catch") ~ block).? ~ (keywordD("finally") ~ block).?).map {
        case (b, c, f) =>{
            Try(
            b,
            c.getOrElse(InstructionList(List())),
            f.getOrElse(InstructionList(List()))
            )
        }
        }
    )
    def predicate[$: P]: P[Instruction] = positioned(
        (doc ~ (modifier("predicate") ~ keywordD("predicate")) ~ identLazy ~ arguments ~ json).map {
            case (doc, mod, name, args, json) => {
                PredicateDecl(name, args, json, mod.withDoc(doc))
            }
        }
    )
    def arrayAssign[$: P]: P[Instruction] = positioned(
        (identLazy2 ~ "[" ~ exprNoTuple.rep(1, sep=",") ~ "]" ~ assignmentOp ~ expr).map { case (a, i, o, e) =>
            ArrayAssigment(Left(a), i.toList, o, e)
        }
    )
    def foreach[$: P]: P[Instruction] = positioned(
    (keywordD("foreach") ~ "(".? ~ ident ~ keywordD("in") ~ exprNoTuple ~ ")".? ~ instruction).map(
        (v, e, instruction) => ForEach(v, e, instruction)
    ))
    def packageInstr[$: P]: P[Instruction] = positioned(
        (keywordD("package") ~ identLazy2 ~ program).map (p => Package(p._1, p._2))
    )
    def interfaces[$: P]: P[List[(String, List[Type])]] ={
        (keywordD("implements") ~ (ident2 ~ typeVariables).rep(1, sep=",")).?.map { p =>
            p.getOrElse(List()).map { case (i, t) => (i, t) }.toList
        }
    }
    def classDecl[$: P]: P[Instruction] = positioned(
        (doc ~ modifier("class") ~ keywordD("class") ~ identLazy ~ typeArgument ~ (
            keywordD("extends") ~ ident2 ~ typeVariables
        ).? ~ interfaces ~ (keywordD("with") ~ namespacedName ~ keywordD("for") ~ ident).rep ~ classBlock).map {
        case (doc, mod, iden, typeargs, par, interface, entity, block) =>
            ClassDecl(
                iden,
                typeargs,
                block,
                mod.withDoc(doc),
                if (par.isDefined) Some(par.get._1) else None,
                if (par.isDefined) (par.get._2) else List(),
                interface,
                entity.map { case (e, n) => (n, e) }.toMap
            )
        }
    )
    def interfaceDecl[$: P]: P[Instruction] = positioned(
        (doc ~ (modifier(
            "interface"
        ) ~ keywordD("interface")) ~ identLazy ~ typeArgument ~ (
        keywordD("extends") ~ ident2 ~ typeVariables
        ).? ~ interfaces ~ classBlock).map {
        case (doc, mod, iden, typeargs, par, interface, block) =>
            ClassDecl(
                iden,
                typeargs,
                block,
                mod.withDoc(doc),
                if (par.isDefined) Some(par.get._1) else None,
                if (par.isDefined) (par.get._2) else List(),
                interface,
                Map()
            )
        }
    )
    def caseClassDecl[$: P]: P[Instruction] = positioned(
        (doc ~ (modifier("class") ~ keywordD("case").? ~ keywordD("class")) ~ identLazy ~ typeArgument ~ arguments ~ (
        keywordD("extends") ~ ident2 ~ typeVariables
        ).? ~ interfaces ~ (keywordD("with") ~ namespacedName ~ keywordD("for") ~ ident).rep ~ classBlock).map {
        case (doc, mod, iden, typeargs, arguments, par, interface, entity, block) =>
            val fieldsDecl = arguments.map { case Argument(n, t, _) =>
                VariableDecl(List(n), t, Modifier.newPublic(), null, null)
            }
            val fields = arguments.map { case Argument(n, t, _) =>
                (
                    Left[Identifier, Variable](Identifier.fromString(f"this.$n")),
                    Selector.self
                )
            }
            val argu = TupleValue(arguments.map {
                case Argument(n, t, _) => (VariableValue(n))
            })
            var mod = Modifier.newPublic()
            mod.isLazy = true

            val constructor = FunctionDecl(
                "__init__",
                VariableAssigment(fields, "=", argu),
                VoidType,
                arguments,
                List(),
                mod
            )

            ClassDecl(
                iden,
                typeargs,
                InstructionList(fieldsDecl ::: List(constructor, block)),
                mod.withDoc(doc),
                if (par.isDefined) Some(par.get._1) else None,
                if (par.isDefined) (par.get._2) else List(),
                interface,
                entity.map { case (e, n) => (n, e) }.toMap
            )
        }
    )

    def getCaseClassIntruction(
        args: List[Argument],
        rest: Option[Instruction]
    ): Instruction = {
        var mod = Modifier.newPublic()
        mod.isLazy = true
        InstructionBlock(
        args.map(a =>
            VariableDecl(List(a.name), a.typ, Modifier.newPublic(), null, null)
        ) :::
            List(
            FunctionDecl(
                "__init__",
                InstructionList(
                args.map(a =>
                    VariableAssigment(
                    List(
                        (
                        Left(Identifier.fromString(f"this.${a.name}")),
                        Selector.self
                        )
                    ),
                    "=",
                    VariableValue(a.name)
                    )
                )
                ),
                VoidType,
                args,
                List(),
                mod
            ),
            rest.getOrElse(InstructionList(List())).unBlockify()
            )
        )
    }
    def caseStruct[$: P]: P[Instruction] = positioned(
        (doc ~ (modifier("struct") ~ keywordD("case").? ~ keywordD("struct")) ~ identLazy ~ typeArgument ~ arguments ~ (
        keywordD("extends") ~ ident2
        ).? ~ (block).?).map { case (doc, mod, iden, typeargs, args, par, block) =>
            StructDecl(
                iden,
                typeargs,
                getCaseClassIntruction(args, block),
                mod.withDoc(doc),
                par
            )
        }
    )
    def structDecl[$: P]: P[Instruction] = positioned(
        (doc ~ (modifier("struct") ~ keywordD("struct")) ~ identLazy ~ typeArgument ~ (keywordD("extends") ~ ident2).? ~ classBlock).map { 
            case (doc, mod, iden, typeargs, par, block) => {
                StructDecl(iden, typeargs, block, mod.withDoc(doc), par)
            }
        }
    )
    def extensionDecl[$: P]: P[Instruction] = positioned(
        (doc ~ modifier("extension") ~ keywordD("extension") ~ types ~ blockExtension).map { 
            case (doc, mod, typ, block) => ExtensionDecl(typ, block, mod.withDoc(doc))
        }
    )
    def typedefInner[$: P]: P[(String, Type, String)] = {
        (types ~ identLazy ~ (keywordD("for") ~ identLazy).?).map { case (typ, str1, str2) =>
            (str1, typ, str2.getOrElse(""))
        }
    }
    def typedef[$: P]: P[Instruction] = positioned(
        (keywordD("typedef") ~ typedefInner.rep(1, sep= ",")).map { case t => TypeDef(t.toList) }
    )
    def templateUse[$: P]: P[Instruction] = positioned(
        (ident2 ~ typeArgumentExpression ~ identLazy ~ block).map {
        case (iden, values, name, instr) =>
            TemplateUse(iden, name, instr, values)
        }
    )
    def templateDesc[$: P]: P[Instruction] = positioned(
        (doc ~ modifier("template") ~ keywordD("template") ~ identLazy ~ templateArguments ~ (
        keywordD("extends") ~ (ident2 ~ typeArgumentExpression)
        ).? ~ instruction).map {
        case (doc, mod, name, generics, Some(parent, genericsParent), instr) =>
                TemplateDecl(
                name,
                instr,
                mod.withDoc(doc),
                Some(parent),
                generics,
                genericsParent
            );
        case (doc, mod, name, generics, None, instr) =>
            TemplateDecl(name, instr, mod.withDoc(doc), None, generics, List())
        }
    )
    def importShortInst[$: P]: P[Instruction] = positioned(
        (keywordD("import") ~ ident2 ~ "::" ~ ident2 ~ (keywordD("as") ~ ident2).?).map {
        case (file, res, alias) => Import(file, res, alias.getOrElse(null))
        }
    )
    def importInst[$: P]: P[Instruction] = positioned(
        (keywordD("import") ~ ident2 ~ (keywordD("as") ~ ident2).?).map { case (file, alias) =>
            Import(file, null, alias.getOrElse(null))
        }
    )
    def fromImportInst[$: P]: P[Instruction] = positioned(
        (keywordD("from") ~ ident2 ~ (keywordD("import") ~ ident2) ~ (keywordD("as") ~ ident2).?).map {
            case (file, res, alias) => Import(file, res, alias.getOrElse(null))
        }
    )
    def forgenerate[$: P]: P[Instruction] = positioned(
        (((keywordD("forgenerate") ~ "(" ~ identLazy ~ ",") ~ exprNoTuple ~ ")") ~ instruction).map (
            p => ForGenerate(p._1, p._2, p._3)
        )
    )
    def jsonFile[$: P]: P[Instruction] = positioned(
        (doc ~ modifier("jsonfile") ~ keywordD("jsonfile") ~ identLazy2 ~ expr).map {
            case (d, m, n, json) => JSONFile(n, json, m.withDoc(d))
        }
    )
    def doWhileLoop[$: P]: P[Instruction] = positioned(
        (keywordD("do") ~ instruction ~ keywordD("while") ~ "(" ~ exprNoTuple ~ ")").map(p =>
            DoWhileLoop(p._2, p._1)
        )
    )
    def whileLoop[$: P]: P[Instruction] = positioned(
        (keywordD("while") ~ "(" ~ exprNoTuple ~ ")" ~ instruction).map (p =>
            WhileLoop(p._1, p._2)
        )
    )
    def forLoop[$: P]: P[Instruction] = positioned(
        (keywordD("for") ~ "(" ~ instruction2 ~ ";" ~ exprNoTuple ~ ";" ~ instruction2 ~ ")" ~ instruction).map
        (p =>
            InstructionBlock(
            List(
                p._1,
                WhileLoop(p._2, InstructionList(List(p._4, p._3)))
            )
            )
        )
    )
    def forEachLoop[$: P]: P[Instruction] = positioned(
        (keywordD("for") ~ "(" ~ types ~ ident2 ~ keywordD("in") ~ exprNoTuple ~ ")" ~ instruction).map{ 
            case (t, n, e, i) => For(t, n, e, i) 
        }
    )
    def repeatLoop[$: P]: P[Instruction] = positioned(
        (keywordD("repeat") ~ "(" ~ exprNoTuple ~ ")" ~ instruction).map {
        case (value, intr) =>
            FunctionCall(
            Identifier.fromString("__repeat__"),
            List(value, LambdaValue(List(), intr, null)),
            List()
            )
        }
    )
    def withInstr[$: P]: P[Instruction] = positioned(
        (keywordD("with") ~ "(" ~ exprNoTuple ~ ")" ~ instruction ~ (
        keywordD("else") ~ instruction
        ).? ).map { case (sel, intr, elze) =>
            With(sel, BoolValue(false), BoolValue(true), intr, elze.getOrElse(null))
        }
        | ((keywordD("with") ~ "(" ~ exprNoTuple ~ "," ~ exprNoTuple ~ ")") ~ instruction ~ (
            keywordD("else") ~ instruction
        ).?).map { case (sel, isat, intr, elze) =>
            With(sel, isat, BoolValue(true), intr, elze.getOrElse(null))
        }
        | ((keywordD("with") ~ "(" ~ exprNoTuple ~ "," ~ exprNoTuple ~ "," ~ exprNoTuple ~ ")") ~ instruction ~ (
            keywordD("else") ~ instruction
        ).?).map { case (sel, isat, cond, intr, elze) =>
            With(sel, isat, cond, intr, elze.getOrElse(null))
        }
    )
    def switch[$: P]: P[Switch] = positioned(
        (keywordD("switch") ~ exprNoTuple ~ "{" ~ switchCase.rep ~ "}").map (p =>
            Switch(p._1, p._2.toList)
        )
    )
    def switchIf[$: P]: P[Expression] = ((keywordD("if") ~ exprNoTuple).?).map (p => p.getOrElse(BoolValue(true)))
    def switchCaseBase[$: P]: P[SwitchCase] = P(
        (exprNoTuple ~ switchIf ~ ("->" | "=>") ~ instruction).map 
        (
            (e, c, i) => SwitchCase(e, i, c)
        ) | ((keywordD("case") ~ exprNoTuple ~ switchIf ~ ":") ~ instruction).map 
        (
            (e, c, i) => SwitchCase(e, i, c)
        ) | ((((keywordD("default") | keywordD("else")) ~ switchIf ~ ":")) ~ instruction).map 
        (
            (c, i) => SwitchCase(DefaultValue, i, c)
        ) | ((((keywordD("default") | keywordD("else")) ~ switchIf ~ (("->") | "=>"))) ~ instruction).map 
        (
            (c, i) => SwitchCase(DefaultValue, i, c)
        )
    )
    def switchCase[$: P]: P[SwitchElement] = P(
        (Index ~ (
            switchCaseBase
            | (
                (keywordD("foreach") ~ "(".? ~ ident ~ keywordD("in") ~ exprNoTuple ~ ")".? ~ "{".? ~ switchCaseBase ~ "}".?).map { 
                    case (v, e, i) => {
                        SwitchForEach(v, e, i)
                    }
                }
            )
            | (
                (keywordD("forgenerate") ~ "(" ~ identLazy ~ "," ~ exprNoTuple ~ ")" ~ "{".?) ~ switchCaseBase ~ "}".?).map (p =>
                    SwitchForGenerate(p._1, p._2, p._3)
                )
        )).map { case (index, sc) =>
            sc.setPos(index)
        }
    )
    def rotated1[$: P]: P[Instruction] = positioned(
        (keywordD("rotated") ~ "(" ~ exprNoTuple ~ "," ~ exprNoTuple ~ ")" ~ instruction).map{
            case (e1, e2, i) => Execute(RotatedType, List(e1, e2), i)
        }
    )
    def rotated2[$: P]: P[Instruction] = positioned(
        (keywordD("rotated") ~ exprNoTuple ~ instruction).map { 
            case (e, i) => Execute(RotatedType, List(e), i)
        }
    )
    def facing1[$: P]: P[Instruction] = positioned(
        (keywordD("facing") ~ "(" ~ exprNoTuple ~ "," ~ exprNoTuple ~ ")" ~ instruction).map {
            case (e1, e2, i) => Execute(FacingType, List(e1, e2), i)
        }
    )
    def facing2[$: P]: P[Instruction] = positioned(
        (keywordD("facing") ~ exprNoTuple ~ instruction).map { 
            case (e, i) => Execute(FacingType, List(e), i)
        }
    )
    def align[$: P]: P[Instruction] = positioned(
        (keywordD("align") ~ exprNoTuple ~ instruction).map { 
            case (e, i) => Execute(AlignType, List(e), i)
        }
    )
    def blocktag[$: P]: P[Instruction] = {
        positioned(
        (doc ~ modifier("blocktag") ~ keywordD("blocktag") ~ identLazy2 ~ "{" ~ tagentry.rep(sep = ",")  ~ ",".? ~ "}") .map { 
            case (d, m, n, c) => {
                TagDecl(n, c.toList, m.withDoc(d), objects.BlockTag)
            }
        }
        ) |
        positioned(
            (doc ~ modifier("entitytag") ~ keywordD("entitytag") ~ identLazy2 ~ "{" ~ tagentry.rep(sep = ",") ~ ",".? ~ "}").map { 
                case (d, m, n, c) => {
                    TagDecl(n, c.toList, m.withDoc(d), objects.EntityTag)
                }
            }
        ) |
        positioned(
            (doc ~ modifier("itemtag") ~ keywordD("itemtag") ~ identLazy2 ~ "{" ~ tagentry.rep(sep = ",") ~ ",".? ~ "}") .map { 
                case (d, m, n, c) => {
                    TagDecl(n, c.toList, m.withDoc(d), objects.ItemTag)
                }
            }
        )
    }
    def tagentry[$: P]: P[Expression] = positioned(
        namespacedName | (identLazy2.map(VariableValue(_))) | tagValue
    )
    def enumInstr[$: P]: P[EnumDecl] = positioned(
        (doc ~ modifier("enum") ~ keywordD("enum") ~ identLazy ~ (
        "(" ~ enumField.rep(sep=",") ~ ")"
        ).? ~ "{" ~ enumValue.rep(sep=",") ~ ",".? ~ "}").map { 
            case (doc, mod, n, f, v) =>{
                EnumDecl(n, f.getOrElse(Seq()).toList, v.toList, mod.withDoc(doc))
            }
        }
    )
    
    def enumField[$: P]: P[EnumField] = (types ~ identLazy).map { p =>
        EnumField(p._2, p._1)
    }
    def enumValue[$: P]: P[EnumValue] = (identLazy ~ ("(" ~ exprNoTuple.rep(sep=",")~ ")").?).map (p =>
      EnumValue(p._1, p._2.getOrElse(Seq()).toList)
    )
    def varAssignment[$: P]: P[Instruction] = positioned(
        (((selector ~ ".").? ~ identLazy2).rep(1, sep=",") ~ assignmentOp ~ expr).map {
        case (id, op, expr) => {
            val identifiers = id.map(p => {
                (Identifier.fromString(p._2), p._1.getOrElse(Selector.self))
            })
            VariableAssigment(identifiers.map((i, s) => (Left(i), s)).toList, op, expr)
        }
        } |
        (((selector ~ ".").? ~ identLazy2).rep(1, sep = ",") ~ ("++")).map {
            case id => {
            val identifiers = id.map(p =>
                (Identifier.fromString(p._2), p._1.getOrElse(Selector.self))
            )
            VariableAssigment(
                identifiers.map((i, s) => (Left(i), s)).toList,
                "+=",
                IntValue(1)
            )
            }
        } |
        (((selector ~ ".").? ~ identLazy2).rep(1, sep = ",") ~ ("--")).map {
            case id => {
            val identifiers = id.map(p =>
                (Identifier.fromString(p._2), p._1.getOrElse(Selector.self))
            )
            VariableAssigment(
                identifiers.map((i, s) => (Left(i), s)).toList,
                "-=",
                IntValue(1)
            )
            }
        }
    )
    def singleVarAssignment[$: P]: P[VariableAssigment] = positioned(
        ((selector ~ ".").? ~ identLazy2 ~ assignmentOp ~ expr).map {
        case (s, i, op, e) =>
            VariableAssigment(List((Left(i), s.getOrElse(Selector.self))), op, e)
        } |
        (("++") ~ (selector ~ ".").? ~ identLazy2).map { case (s, i) =>
            VariableAssigment(
            List((Left(i), s.getOrElse(Selector.self))),
            "+=",
            IntValue(1)
            )
        } |
        (("--") ~ (selector ~ ".").? ~ identLazy2).map { case (s, i) =>
            VariableAssigment(
            List((Left(i), s.getOrElse(Selector.self))),
            "-=",
            IntValue(1)
            )
        }
    )
    def singleVarAssignmentPos[$: P]: P[VariableAssigment] = positioned(
        (
        ((selector ~ ".").? ~ identLazy2 ~ ("++")).map { case (s, i) =>
            VariableAssigment(
            List((Left(i), s.getOrElse(Selector.self))),
            "+=",
            IntValue(1)
            )
        } |
        ((selector ~ ".").? ~ identLazy2 ~ ("--")).map { case (s, i) =>
            VariableAssigment(
            List((Left(i), s.getOrElse(Selector.self))),
            "-=",
            IntValue(1)
            )
        }
        )
    )

    def varDeclaration[$: P]: P[VariableDecl] = positioned(
        (doc ~ modifier("variable") ~ types ~ identLazy.rep(1, sep= ",") ~ (
        assignmentOp ~ expr
        ).?).map {
        case (doc, mod1, typ, names, expr) => {
            val mod = mod1.withDoc(doc)
            val identifiers = names.map(Identifier.fromString(_))
            if (!mod.isEntity && expr.isEmpty) {
                VariableDecl(names.toList, typ, mod, ":=", DefaultValue)
            } else if (!expr.isEmpty) {
                VariableDecl(names.toList, typ, mod, expr.get._1, expr.get._2)
            } else {
                VariableDecl(names.toList, typ, mod, null, null)
            }
        }
        }
    )

    def propertyDeclaration[$: P]: P[Instruction] = positioned(
        (varDeclaration ~ "{" ~ (
        (keywordD("private") | keywordD("protected") | keywordD("public")) ~ ident
        ).rep(1, sep=(";" | ",")) ~ (";" | ",").? ~ "}").map {
        case (VariableDecl(v, typ, mod, op, expr), m) =>
            var hasGet = m.exists { case "get" => true; case _ => false }
            var hasSet = m.exists { case "set" => true; case _ => false }

            val pp = v.map(x => {
            TemplateUse(
                Identifier.fromString("property"),
                x,
                InstructionBlock(
                List(
                    if (hasGet){
                        FunctionDecl(
                            "get",
                            Return(VariableValue(Identifier.fromString("--" + x))),
                            typ,
                            List(),
                            List(),
                            mod
                        )
                    }
                    else {
                        EmptyInstruction
                    },
                    if (hasSet){
                        FunctionDecl(
                            "set",
                            VariableAssigment(
                            List(
                                (Left(Identifier.fromString("--" + x)), Selector.self)
                            ),
                            "=",
                            VariableValue("value")
                            ),
                            VoidType,
                            List(Argument("value", typ, None)),
                            List(),
                            mod
                        )
                    }
                    else {
                        EmptyInstruction
                    }
                )
                ),
                List()
            )
            })
            InstructionList(
                List(
                    VariableDecl(v.map(s => "--" + s), typ, mod, op, expr),
                    InstructionList(pp)
                )
            )
        }
    )
    def ifs[$: P]: P[If] = positioned(
        ((keywordD("if") ~ "(" ~ expr ~ ")") ~ instruction ~
        ((keywordD("else") ~ keywordD("if") ~ "(" ~ expr ~ ")") ~ instruction).rep ~
        (keywordD("else") ~ instruction).?).map { case (cond, instr, elif, els) =>
            {
            val elze = elif.toList.map(k => ElseIf(k._1, k._2)) ::: 
                        (if (els.isEmpty) Nil
                        else {
                            List(
                                ElseIf(
                                    BoolValue(true),
                                    els.get
                                )
                            )
                        }
                    )
            If(cond, instr, elze)
            }
        }
    )

    def lambda[$: P] = lambda1 | lambda2
    def lambda1[$: P]: P[Expression] = P((Index ~ 
        identLazy2 ~ "=>" ~ instruction).map((index,a,i) =>
            LambdaValue(List(a), i, null).setPos(index)
    ))
    def lambda2[$: P]: P[Expression] = P((Index ~ 
        "(" ~ identLazy2.rep(sep=",") ~")" ~ "=>" ~ instruction).map((index,a,i) =>
            LambdaValue(a.toList, i, null).setPos(index)
    ))
    def sfField[$: P]: P[(String, SelectorFilterValue)] = P((ident ~ "=" ~ selectorFilterInnerValue).map(x => (x._1, x._2)))
    def sfCompound[$: P]: P[SelectorFilterValue] = ("{" ~ sfField.rep(1, sep = ",") ~ "}") .map { case a =>
      SelectorComposed(a.toMap)
    }
    def sfNumber[$: P]: P[SelectorFilterValue] = (floatValue).map(SelectorNumber(_))
    def sfNumber2[$: P]: P[SelectorFilterValue] = (numericLit).map(p => SelectorNumber(p.toInt))
    def sfNumber3[$: P]: P[SelectorFilterValue] = ("-" ~ floatValue).map(p => SelectorNumber(-p))
    def sfNumber4[$: P]: P[SelectorFilterValue] =  ("-" ~ numericLit).map(p => SelectorNumber(f"-$p".toInt))
    def sfString[$: P]: P[SelectorFilterValue] = (stringLit2).map(SelectorString(_))
    def sfIdentifier[$: P]: P[SelectorFilterValue] =  (keyword_or_identLazy2).map(SelectorIdentifier(_))
    def sfTag[$: P]: P[SelectorFilterValue] = tagValue.map{ case TagValue(v) => SelectorTag(v) }
    def sfNamespacedName[$: P]: P[SelectorFilterValue] = namespacedName.map(p => SelectorIdentifier(p.toString()))
    def sfNBT[$: P]: P[SelectorFilterValue] = json.map(SelectorNbt(_))
    def selectorFilterInnerValue2[$: P]: P[SelectorFilterValue] =
        sfNumber | sfNumber3 | sfString | sfNamespacedName | sfIdentifier | sfNumber2 | sfNumber4 | sfNBT | sfCompound | sfTag
    def selectorFilterInnerValue[$: P]: P[SelectorFilterValue] = 
        (selectorFilterInnerValue2.? ~ "..".!.? ~ selectorFilterInnerValue2.?).map{
            case (Some(left), Some(_), Some(right)) => SelectorRange(left, right)
            case (Some(left), Some(_), None)        => SelectorGreaterRange(left)
            case (None,       Some(_), Some(right))       => SelectorLowerRange(right)
            case (Some(left), None, None) => left
            case (None, None, Some(right)) => right
            case other => throw new Exception(f"Invalid Selector filter: $other")
        }
    def selectorFilterValue[$: P]: P[SelectorFilterValue] = 
        (("!" ~ selectorFilterInnerValue).map(SelectorInvert(_)))
         | selectorFilterInnerValue
    def selectorFilter[$: P]: P[(String, SelectorFilterValue)] =
        (identLazy ~ "=" ~ selectorFilterValue).map { p => (p._1, p._2) }
    def selector[$: P]: P[Selector] = ("@".! ~ (keywordD("a") | keywordD("s") | keywordD("e") | keywordD("p") | keywordD("r")).! ~ 
    ("[" ~ selectorFilter.rep(sep=",") ~ "]").?)
        .map { p => Selector.parse(p._1 + p._2, p._3.getOrElse(Seq()).toList) }
    def selectorStr[$: P]: P[String] = (selector.map(_.toString()))
    def stringLit2[$: P]: P[String] = stringLit.map { p => p.replaceAllLiterally("◘", "\\\"") }
    def anyWord[$: P]: P[String] = ident
    def blockDataField[$: P]: P[String] = (anyWord ~ "=" ~ exprNoTuple).map { case (n, v) =>
        n + "=" + v
    }
    def blockData[$: P]: P[String] = ("[" ~ blockDataField.rep(1, sep=",") ~ "]").map { case fields =>
        fields.mkString("[", ",", "]")
    }
    def namespacedName[$: P]: P[NamespacedName] =
    (keyword_or_identLazy ~ ":" ~ keyword_or_identLazy2 ~ blockData.? ~ json.?).map {
      case (a, b, d, j) =>
        NamespacedName(
          a + ":" + b + d.getOrElse(""),
          JsonValue(j.getOrElse(JsonNull))
        )
    }
    def validCordNumber1[$: P]: P[Expression] = P(
        floatValue.map{FloatValue(_)} 
        | (numericLit.map { i => IntValue(i.toInt) }) 
        | (identLazyForce.map{ VariableValue(_)})
    )
    def validCordNumber2[$: P]: P[Expression] = P(
        floatValue.map(p => FloatValue(p))
      | numericLit.map(p => IntValue(p.toInt))
      | identLazyForce.map { VariableValue(_) }
      | "-" ~ floatValue.map (p => FloatValue(-p))
      | "-" ~ numericLit.map(p => IntValue(f"-$p".toInt))
      | "-" ~ identLazyForce .map { s =>
        BinaryOperation("+", StringValue("-"), VariableValue(s))
      }
    )
    def relCoordinate[$: P]: P[Expression] = P("~" ~~ validCordNumber2.?).map {
        case Some(num) => BinaryOperation("+", StringValue("~"), num)
        case None => StringValue("~")
    } | validCordNumber2
    def frontCoordinate[$: P]: P[Expression] = ("^" ~~ validCordNumber2.?).map {
        case Some(v) => { BinaryOperation("+", StringValue("^"), v) }
        case None => StringValue("^")
    }
    def relPositionCase1[$: P]: P[PositionValue] = (relCoordinate ~~ " " ~~ validCordNumber2 ~~ " " ~~ relCoordinate).map{ case (x, y, z) =>
      PositionValue(x, y, z)
    }
    def relPositionCase2[$: P]: P[PositionValue] = (relCoordinate ~~ " " ~~ relCoordinate~~ " " ~~ validCordNumber2).map{ case (x, y, z) =>
      PositionValue(x, y, z)
    }
    def frontPosition[$: P]: P[PositionValue] = (frontCoordinate ~~ " " ~~ frontCoordinate ~~ " " ~~ frontCoordinate).map { case (x, y, z) =>
      PositionValue(x, y, z)
    }
    def relPosition[$: P]: P[PositionValue] = (relCoordinate ~~ " " ~~ relCoordinate ~~ " " ~~ relCoordinate).map{ case (x, y, z) =>
      PositionValue(x, y, z)
    }
    def position[$: P]: P[PositionValue] =  (frontPosition | relPosition | relPositionCase1 | relPositionCase2)
    def relCoordinate1Expr[$: P]: P[Expression] = ("~" ~~ exprAs).map{
        BinaryOperation("+", StringValue("~"), _)
    }
    def relCoordinate2Expr[$: P]: P[StringValue] = "~".map(_ => StringValue("~"))
    def relCoordinateExpr[$: P]: P[Expression] = relCoordinate1Expr | relCoordinate2Expr | exprAs
    def frontCoordinateNumberExpr[$: P]: P[Expression] = ("^" ~~ exprAs).map {
        { BinaryOperation("+", StringValue("^"), _) }
    }
    def frontCoordinateHereExpr[$: P]: P[StringValue] = "^".map(_ => StringValue("^"))
    def frontCoordinateExpr[$: P]: P[Expression] = frontCoordinateNumberExpr | frontCoordinateHereExpr
    def relPositionCase1Expr[$: P]: P[PositionValue] = (relCoordinate2Expr ~ "," ~ exprAs ~ "," ~ relCoordinateExpr).map{
      case (x, y, z) => PositionValue(x, y, z)
    }
    def relPositionCase2Expr[$: P]: P[PositionValue] = (relCoordinateExpr ~ "," ~ relCoordinate2Expr ~ "," ~ exprAs).map{
      case (x, y, z) => PositionValue(x, y, z)
    }
    def frontPositionExpr[$: P]: P[PositionValue] = (frontCoordinateExpr ~ "," ~ frontCoordinateExpr ~ "," ~ frontCoordinateExpr).map {
      case (x, y, z) => PositionValue(x, y, z)
    }
    def relPositionExpr[$: P]: P[PositionValue] = (relCoordinateExpr ~ "," ~ relCoordinateExpr ~ "," ~ relCoordinateExpr).map {
      case (x, y, z) => PositionValue(x, y, z)
    }
    def positionExpr[$: P]: P[PositionValue] = "{" ~ (frontPositionExpr | relPositionExpr | relPositionCase1Expr | relPositionCase2Expr) ~ "}"
    def tagValue[$: P]: P[TagValue] = ("#" ~ ident2 ~ (":" ~ ident2).?).map {
        case (a, Some(b)) => TagValue(a + ":" + b); 
        case (a, None)    => TagValue(a)
    }
    def constructorCall[$: P]: P[ConstructorCall] = positioned(keywordD("new") ~ (
        (typeVariables ~ "(" ~ exprNoTuple.rep(sep=",") ~ ")").map {
            case (t, a) => ConstructorCall("@@@", a.toList, t)
        }
        | (nonRecTypes ~ ("[" ~ exprNoTuple.rep(sep=",") ~ "]")).map {
            case (t, a) => ConstructorCall("standard.array.Array", a.toList, List(t))
        }
        | (identLazy2 ~ typeVariables ~ "(" ~ exprNoTuple.rep(sep=",") ~ ")" ~ block.?).map { 
            case (f, t, a, Some(b)) => ConstructorCall(f, a.toList ::: List(LambdaValue(List(), b, null)), t)
            case (f, t, a, None) => ConstructorCall(f, a.toList, t) 
        }
    ))
    def destructorCall[$: P]: P[DestructorCall] = positioned((keywordD("delete") ~ expr).map { case e => DestructorCall(e) })
    def exprBottom[$: P]: P[Expression] = positioned(
            floatValue.map (p => FloatValue(p))
        | numericLit.map (p => IntValue(p.toInt))
        | "-" ~ floatValue.map (p => FloatValue(-p))
        | "-" ~ numericLit.map (p => IntValue(f"-$p".toInt))
        | "-" ~ exprBottom.map (BinaryOperation("-", IntValue(0), _))
        | "!" ~ exprBottom.map (UnaryOperation("!", _))
        | keywordD("true")  .map(_ => BoolValue(true))
        | keywordD("false") .map(_ => BoolValue(false))
        | keywordD("null")  .map(_ => NullValue)
        | selector.map(SelectorValue(_))
        | singleVarAssignment .map { p =>
            SequenceValue(p, VariableValue(p.name.head._1.left.get, p.name.head._2))
        }
        | singleVarAssignmentPos .map { p =>
            SequencePostValue(VariableValue(p.name.head._1.left.get, p.name.head._2), p)
        }
        | tagValue
        | namespacedName
        | "$" ~ stringLit2 .map { case s => InterpolatedString.build(s) }
        | stringLit2 .map (StringValue(_))
        | (identLazy2 ~ selector).map { case (id, sel) =>
            BinaryOperation("in", SelectorValue(sel), VariableValue(id))
        }
        | constructorCall
        | exprArray
        | identifierExpr
        | "(" ~ expr ~ ")"
        | json.map (JsonValue(_))
    )
    def identifierExpr[$: P]: P[Expression] = {
        (identLazy2 ~ typeVariables ~ "(" ~ exprNoTuple.rep(sep =",") ~ ")"~ block).map { 
            case (f, t, a, b) => {
                FunctionCallValue(
                    VariableValue(f),
                    a.toList ::: List(LambdaValue(List(), b, null)),
                    t
                )
            }
        }
        | (identLazy2 ~ typeVariables ~ block).map { 
            case (f, t, b) => {
                FunctionCallValue(
                    VariableValue(f),
                    List(LambdaValue(List(), b, null)),
                    t
                )
            }
        }
        | (identLazy2 ~ (typeVariables ~ "("~ exprNoTuple.rep(sep=",") ~ ")").rep(1)).map { 
            case (f, a)=> {
                a.toList.foldLeft[Expression](VariableValue(f))((b, a) =>
                    FunctionCallValue(b, a._2.toList, a._1)
                )
            }
        }
        | identLazy2.map(VariableValue(_))
        | (selector ~ "." ~ identLazy2).map { case (s, n) => VariableValue(n, s) }
        | identTag.map (VariableValue(_))
    }
    def exprArray[$: P]: P[Expression] = positioned(
      ((identifierExpr | "(" ~ expr ~ ")") ~ (
        "[" ~ expr.rep(1, sep=",") ~ "]"
      ).rep).map { case (e, g) => g.foldLeft(e)((e, i) => ArrayGetValue(e, i.toList)) }
    )
    def comparator[$: P]: P[String] = ("<=" | ">=" | "<" | ">" | "==" | "!=").!
    def typeVariablesForce[$: P]: P[List[Type]] = ("<" ~ types.rep(sep=",") ~ ">").map { case a =>
        a.toList
    }
    def typeVariables[$: P]: P[List[Type]] = typeVariablesForce.?.map {
        case Some(a) => a; 
        case None => List()
    }
    def typeArgument[$: P]: P[List[String]] = ("<" ~ ident.rep(sep= ",") ~ ">").?.map {
        case Some(a) => a.toList; 
        case None => List()
    }
    def typeArgumentExpression[$: P]: P[List[Expression]] = ("<" ~ exprBottom.rep(sep = ",") ~ ">").?.map {
        case Some(a) => a.toList; 
        case None => List()
    }
    def exprDot[$: P]: P[Expression] = positioned(exprBottom.rep(1, sep=".").map {
      case e if e.size == 1 => e.head;
      case e                => e.tail.foldLeft(e.head)((p, n) => DotValue(p, n))
    })
    def ternaryOperator[$: P]: P[Expression] = positioned(
        (exprDot ~ ("?" ~ (identifierExpr | exprNoTuple) ~ ":" ~ exprNoTuple).?).map {
          case (e1, Some(e2, e3)) => TernaryOperation(e1, e2, e3);
          case (e1, None)         => e1
        }
    )
    def exprLeftRange[$: P]: P[Expression] = positioned(
        (".." ~ ternaryOperator ~ (keywordD("by") ~ ternaryOperator).?).map {
        case (e,  None)     => RangeValue(IntValue(Int.MinValue), e, IntValue(1))
        case (e1, Some(e2)) => RangeValue(IntValue(Int.MinValue), e1, e2)
        }
        | ternaryOperator
    )
    def exprRange[$: P]: P[Expression] = positioned(
        (exprLeftRange ~ (
        ".." ~ ternaryOperator.? ~ (keywordD("by") ~ ternaryOperator).?
        ).?).map {
        case (e, None)                      => e;
        case (e1, Some(Some(e2), None))     => RangeValue(e1, e2, IntValue(1));
        case (e1, Some(None, None))         => RangeValue(e1, IntValue(Int.MaxValue), IntValue(1));
        case (e1, Some(Some(e2), Some(e3))) => RangeValue(e1, e2, e3)
        case (e1, Some(None, Some(e3)))     => RangeValue(e1, IntValue(Int.MaxValue), e3)
        }
    )
    def exprPow[$: P]: P[Expression] = positioned((exprRange ~ ("^" ~ exprRange).rep).map {
        case (left, right) => unpack("^", (left, right.toList))
    })
    def exprMod[$: P]: P[Expression] = positioned((exprPow ~ ("%" ~ exprPow).rep).map {
        case (left, right) => unpack("%", (left, right.toList))
    })
    def exprDiv[$: P]: P[Expression] = positioned((exprMod ~ ("/" ~ exprMod).rep).map {
        case (left, right) => unpack("/", (left, right.toList))
    })
    def exprMult[$: P]: P[Expression] = positioned((exprDiv ~ ("*" ~ exprDiv).rep).map {
        case (left, right) => unpack("*", (left, right.toList))
    })
    def exprSub[$: P]: P[Expression] = positioned((exprMult ~ ("-" ~ exprMult).rep).map {
        case (left, right) => unpack("-", (left, right.toList))
    })
    def exprAdd[$: P]: P[Expression] = positioned((exprSub ~ ("+" ~ exprSub).rep).map {
        case (left, right) => unpack("+", (left, right.toList))
    })
    def exprComp[$: P]: P[Expression] = positioned((exprAdd ~ (comparator ~ exprAdd).rep).map {
        case (left, right) => unpack((left, right.toList))
    })
    def exprAppend[$: P]: P[Expression] = positioned((exprComp ~ (">:" ~ exprComp).rep).map {
        case (left, right) => unpack(">:", (left, right.toList))
    })
    def exprPrepend[$: P]: P[Expression] = positioned((exprAppend ~ ("<:" ~ exprAppend).rep).map {
        case (left, right) => unpack("<:", (left, right.toList))
    })
    def exprMerge[$: P]: P[Expression] = positioned((exprPrepend ~ ("::" ~ exprPrepend).rep).map {
        case (left, right) => unpack("::", (left, right.toList))
    })
    def exprNotIn[$: P]: P[Expression] = positioned((exprMerge ~ (keywordD("not")~keywordD("in") ~ exprMerge).rep).map{
        case (left, right) => unpack("not in", (left, right.toList))
    })
    def exprIn[$: P]: P[Expression] = positioned((exprNotIn ~ (keywordD("in") ~ exprNotIn).rep).map{
        case (left, right) => unpack("in", (left, right.toList))
    })
    def exprIs[$: P]: P[Expression] = positioned((exprIn ~ (keywordD("is") ~ types).?).map {
        case (e, Some(t)) => IsType(e, t)
        case (e, None)    => e
    })
    def exprIsNot[$: P]: P[Expression] = positioned(
        (exprIs ~ (keywordD("is") ~ keywordD("not") ~ types).?).map {
        case (e, Some(t)) => UnaryOperation("!", IsType(e, t));
        case (e, None)    => e
        }
    )
    def exprShiftRight[$: P]: P[Expression] = positioned((exprIsNot ~ (">>" ~ exprIsNot).rep).map{
        case (left, right) => unpack(">>", (left, right.toList))
    })
    def exprShiftLeft[$: P]: P[Expression] = positioned((exprShiftRight ~ ("<<" ~ exprShiftRight).rep).map{
        case (left, right) => unpack("<<", (left, right.toList))
    })
    def exprBitwiseAnd[$: P]: P[Expression] = positioned((exprShiftLeft ~ ("&" ~ exprShiftLeft).rep).map{
        case (left, right) => unpack("&", (left, right.toList))
    })
    def exprBitwiseOr[$: P]: P[Expression] = positioned((exprBitwiseAnd ~ ("|" ~ exprBitwiseAnd).rep).map{
        case (left, right) => unpack("|", (left, right.toList))
    })
    def exprAnd[$: P]: P[Expression] = positioned((exprBitwiseOr ~ ("&&" ~ exprBitwiseOr).rep).map{
        case (left, right) => unpack("&&", (left, right.toList))
    })
    def exprOr[$: P]: P[Expression] = positioned((exprAnd ~ ("||" ~ exprAnd).rep).map{
        case (left, right) => unpack("||", (left, right.toList))
    })
    def exprNullCoalesing[$: P]: P[Expression] = positioned(
        (exprOr ~ ("??" ~ exprNullCoalesing).rep).map{ (a,b) => unpack("??", (a, b.toList)) }
    )
    def exprAs[$: P]: P[Expression] = positioned((exprNullCoalesing ~ (":>" ~ types).rep).map{ unpackCast(_) })
    def exprForSelect[$: P]: P[Expression] = positioned((exprAs ~ (keywordD("for") ~ (ident2 | stringLit) ~ keywordD("in") ~ exprNoTuple).rep).map {
      unpackForSelect(_)
    })
    def exprNoTuple[$: P]: P[Expression] = lambda | position | exprForSelect | positionExpr
    def expr[$: P]: P[Expression] = (Index ~ exprNoTuple.rep(1, sep = ",")).map {
        case (index, exprs) => 
            (if (exprs.length == 1) exprs.head else TupleValue(exprs.toList)).setPos(index)
    }
    def nonRecTypes[$: P]: P[Type] = {
        (ident2 ~ typeVariablesForce).map { case (n, t) => IdentifierType(n, t) } |
        ident2.map { identifierType(_) } |
        ("(" ~ types.rep(1, sep = ",") ~ ")").map(p =>
            if (p.length > 1) TupleType(p.toList) else p.head
        )
    }
    def positioned[T <: CPositionable, $: P](p: => P[T]): P[T] = {
        (Index ~ p).map {
            case (index, expr) => expr.setPos(index)
        }
    }
    def identifierType(string: String) = {
        string match {
            case "bool"       => BoolType
            case "int"        => IntType
            case "float"      => FloatType
            case "void"       => VoidType
            case "string"     => StringType
            case "json"       => JsonType
            case "entity"     => EntityType
            case "mcobject"   => MCObjectType
            case "mcposition" => MCPositionType
            case "params"     => ParamsType
            case "rawjson"    => RawJsonType
            case other        => IdentifierType(other, List())
        }
    }
    def types[$: P]: P[Type] = {
        ("(" ~ nonRecTypes.rep(sep = ",") ~ ")" ~ "=>" ~ types).map(p =>
            FuncType(p._1.toList, p._2)
        ) |
        (nonRecTypes ~ "=>" ~ types).map (p => FuncType(List(p._1), p._2)) |
        ((nonRecTypes ~ "[") ~ expr ~ "]").map(p => ArrayType(p._1, p._2)) |
        ((nonRecTypes ~ "[" ~ "]")).map(p => ArrayType(p, null)) |
        nonRecTypes
    }
    def modifierAttribute[$: P]: P[(String, Expression)] =
        (ident2 ~ "=" ~ exprNoTuple).map { case (i, e) => (i, e) }
    def modifierAttributes[$: P]: P[Map[String, Expression]] =
        (("[" ~ modifierAttribute.rep(1, sep = ",") ~ "]").?).map {
            (_.getOrElse(List()).toMap)
        }
    def modifier[$: P](sel: String): P[Modifier] =
        (sel match {
        case "abstract_function" => (
            (stringLit2 ~ stringLit2 ~ stringLit2).? ~ modifierAttributes ~ (
            (keywordD("public") | keywordD("private") | keywordD("protected")).!
            ).? ~ (keywordD("abstract") ~ (modifierSub("function")).rep) ~ (identTag).rep
        )
        case other => (
            (stringLit2 ~ stringLit2 ~ stringLit2).? ~ modifierAttributes ~ (
            (keywordD("public") | keywordD("private") | keywordD("protected")).!
            ).? ~ (modifierSub(sel)).rep ~ (identTag).rep
        )
        }).map {
        case (doc, attributes, protection, subs, tags) => {
            val mod = new Modifier()
            mod.tags.addAll(tags)
            mod.attributes = attributes
            protection match {
            case Some("public")    => mod.protection = Protection.Public
            case Some("private")   => mod.protection = Protection.Private
            case Some("protected") => mod.protection = Protection.Protected
            case _                 => {}
            }

            if (subs.contains("virtual") || sel == "abstract_function") {
            mod.isVirtual = true
            }
            if (subs.contains("abstract") || sel == "abstract_function") {
            mod.isAbstract = true
            }
            if (subs.contains("override")) { mod.isOverride = true }
            if (subs.contains("lazy")) { mod.isLazy = true }
            if (subs.contains("macro")) { mod.isMacro = true }
            if (subs.contains("scoreboard")) { mod.isEntity = true }
            if (subs.contains("ticking")) { mod.isTicking = true }
            if (subs.contains("loading")) { mod.isLoading = true }
            if (subs.contains("helper")) { mod.isHelper = true }
            if (subs.contains("static")) { mod.isStatic = true }
            if (subs.contains("const")) { mod.isConst = true }
            if (subs.contains("async")) { mod.isAsync = true }

            mod
        }
    }
    def modifierSub[$: P](sel: String): P[String] = {
        sel match {
        case "all" => (
            keywordD("abstract") | keywordD("override") | keywordD("virtual") | keywordD("lazy") | keywordD("macro") | keywordD("scoreboard") | keywordD("ticking") | keywordD("loading") | keywordD("helper") | keywordD("static") | keywordD("const") | keywordD("async")
        ).!
        case "function" => (
            keywordD("abstract") | keywordD("override") | keywordD("virtual") | keywordD("lazy") | keywordD("macro") | keywordD("ticking") | keywordD("loading") | keywordD("helper") | keywordD("static") | keywordD("const") | keywordD("async")
        ).!
        case "function_short" => (
            keywordD("lazy") | keywordD("macro") | keywordD("ticking") | keywordD("loading") | keywordD("helper") | keywordD("static") | keywordD("const") | keywordD("async")
        ).!
        case "variable" => (keywordD("lazy") | keywordD("macro") | keywordD("scoreboard") | keywordD("static") | keywordD("const")).!
        case "class"    => (keywordD("abstract") | keywordD("static")).!
        case "struct"   => (keywordD("abstract") | keywordD("static")).!
        case "enum"     => (keywordD("abstract") | keywordD("static")).!
        case _          => (keywordD("static")).!
        }
    }
    def doc[$: P]: P[Option[String]] = ("???" ~ stringLit ~ "???").?
    def program[$: P]: P[Instruction] = positioned(
        instruction.rep.map(i => InstructionList(i.toList))
    )
  

    def unpackCast(p: (Expression, Seq[Type])): Expression = {
        if (p._2.isEmpty) p._1 else CastValue(p._1, p._2.last)
    }

    def unpackForSelect(
        p: (Expression, Seq[(String, Expression)])
    ): Expression = {
        if (p._2.isEmpty) p._1
        else p._2.foldLeft(p._1)((e, p) => ForSelect(e, p._1, p._2))
    }

    def unpack(op: String, p: (Expression, List[Expression])): Expression = {
        if (p._2.isEmpty) p._1
        else p._2.foldLeft(p._1)(BinaryOperation(op, _, _))
    }

    def unpack(p: (Expression, List[(String, Expression)])): Expression = {
        if (p._2.isEmpty) p._1
        else p._2.foldLeft(p._1)((e, p) => BinaryOperation(p._1, e, p._2))
    }

    def globalParse[T, $: P](parser: => P[T]): P[T] = {
        P(Start ~ parser ~ End).map { case (instr) => instr }
    }

    def globalExprParse[$: P]: P[Expression] = {
        globalParse(expr)
    }
    def globalJsonParse[$: P]: P[JSONElement] = {
        globalParse(json)
    }
    def globalProgramParse[$: P]: P[Instruction] = {
        globalParse(program)
    }

    def string_color_formated[$: P]: P[TupleValue] = {
        ("§" ~ AnyChar.! ~ CharsWhile(c => c != '§').rep(0).!).map { case (c, s) => TupleValue(List(StringValue(c), StringValue(s))) }
    }
    def multi_string_color_formated[$: P]: P[List[TupleValue]] = {
        (Start ~ CharsWhile(c => c != '§', min = 0).! ~ string_color_formated.rep() ~ End).map{ case (h, t) =>
            (if (h.size==0) Nil else List(TupleValue(List(StringValue("reset"), StringValue(h))))) ::: t.toList
        }
    }

    def parse_string_color_formated(file: String): List[TupleValue] = {
        val result = fastparse.parse(file, multi_string_color_formated(using _), true)
        result match {
            case Parsed.Success(value, _) => value
            case f :Parsed.Failure => {
                Reporter.error(
                    s"Error parsing string color format: $file" + " "+ 
                    f.trace().longAggregateMsg,
                )
                Nil
            }
        }
    }

    def parseFromFile(f: String, get: () => String): Instruction = {
        if (CacheAST.contains(f)) {
            CacheAST.get(f)
        } 
        else {
            val ast = parse(f, get())
            CacheAST.add(f, ast.get)
            ast.get
        }
    }
    def parse(file: String, args: String): Option[Instruction] = {
        val result = fastparse.parse(Preparser.parse(file, args, true), globalProgramParse(using _), true)
        result match {
            case Parsed.Success(value, _) => Some(value)
            case f :Parsed.Failure => {
                Reporter.error(
                    s"Error parsing file:"+ file + " "+
                    f.trace().longAggregateMsg,
                )
                None
            }
        }
    }
    def parse(string: String): Instruction = {
        val result = fastparse.parse(Preparser.parse(string, string, true), globalProgramParse(using _), true)
        result match {
            case Parsed.Success(value, _) => value
            case f :Parsed.Failure => {
                Reporter.error(
                    s"Error parsing proram:"+ 
                    f.trace().longAggregateMsg,
                )
                null
            }
        }
    }
    def parseJson(file: String): JSONElement = {
        val result = fastparse.parse(file, globalJsonParse(using _), true)
        result match {
            case Parsed.Success(value, _) => value
            case f :Parsed.Failure => {
                Reporter.error(
                    s"Error parsing json: $file" + " "+ 
                    f.trace().longAggregateMsg,
                )
                null
            }
        }
    }
    def parseExpression(file: String, silent: Boolean = false): Expression = {
        val result = fastparse.parse(file, globalExprParse(using _), !silent)
        result match {
            case Parsed.Success(value, _) => value
            case f :Parsed.Failure =>{
                if (!silent) {
                    Reporter.error(
                        s"Error parsing expression: $file" + " "+ 
                        f.trace().longAggregateMsg,
                    )
                }
                null
            }
        }
    }
}
