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

object Parser extends StandardTokenParsers {
  lexical.delimiters ++= List(
    "(",
    ")",
    "\\",
    ".",
    "..",
    ":",
    "=",
    "{",
    "}",
    ",",
    "*",
    "[",
    "]",
    "/",
    "+",
    "-",
    "*",
    "/",
    "\\",
    "%",
    "&&",
    "||",
    "=>",
    ";",
    "+=",
    "-=",
    "/=",
    "*=",
    "%=",
    "?=",
    ":=",
    "^=",
    "%",
    "@",
    "@e",
    "@a",
    "@s",
    "@r",
    "@p",
    "~",
    "^",
    "<=",
    "==",
    ">=",
    "<",
    ">",
    "!=",
    "%%%",
    "???",
    "§§§",
    "$",
    "!",
    "!=",
    "#",
    "&",
    "<<=",
    ">>=",
    "&=",
    "|=",
    "::",
    ":>",
    ">:",
    "<:",
    "-:",
    "??",
    "?",
    "::=",
    ">:=",
    "<:=",
    "-:="
  )
  lexical.reserved ++= List(
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
    "assert"
  )

  /** Parses a block of instructions enclosed in curly braces.
    * @return
    *   A parser for an instruction block.
    */
  def block: Parser[Instruction] = positioned(
    "{" ~> rep(instruction <~ opt(";")) <~ "}" ^^ (p => InstructionBlock(p))
  )

  /** Parses a block for extension of instructions enclosed in curly braces.
    * @return
    *   A parser for an instruction block.
    */
  def blockExtension: Parser[Instruction] = positioned(
    "{" ~> rep((functionDecl | optFunctionDecl) <~ opt(";")) <~ "}" ^^ (p => InstructionBlock(p))
  )


  /** Parses an assignment operator.
    * @return
    *   A parser for an assignment operator.
    */
  def assignmentOp: Parser[String] =
    ("=" | "+=" | "-=" | "*=" | "/=" | ":=" | "%=" | "^=" | "|=" | "&=" | "<<=" | ">>=" | "::=" | ">:=" | "<:=" | "-:=")

  def subident: Parser[String] = opt("$") ~ ident ^^ { case _1 ~ _2 =>
    _1.getOrElse("") + _2
  }
  def ident2: Parser[String] = rep1sep(subident, ".") ^^ { p =>
    p.reduce(_ + "." + _)
  }
  def subident2: Parser[String] = "$" ~ ident ^^ { case _1 ~ _2 => _1 + _2 }
  def identLazy: Parser[String] = subident ~ rep(subident2) ^^ { p =>
    (p._1 :: p._2).reduce(_ + _)
  }
  def identLazyForce: Parser[String] = "$" ~> ident ^^ { "$" + _ }
  def identLazy2: Parser[String] = rep1sep(identLazy, ".") ^^ { p =>
    p.reduce(_ + "." + _)
  }


  def keyword_or_subident: Parser[String] = opt("$") ~ (ident | anyKeyword) ^^ { case _1 ~ _2 =>
    _1.getOrElse("") + _2
  }
  def keyword_or_ident2: Parser[String] = rep1sep(keyword_or_subident, ".") ^^ { p =>
    p.reduce(_ + "." + _)
  }
  def keyword_or_subident2: Parser[String] = "$" ~ (ident | anyKeyword) ^^ { case _1 ~ _2 => _1 + _2 }
  def keyword_or_identLazy: Parser[String] = keyword_or_subident ~ rep(keyword_or_subident2) ^^ { p =>
    (p._1 :: p._2).reduce(_ + _)
  }
  def keyword_or_identLazyForce: Parser[String] = "$" ~> (ident | anyKeyword) ^^ { "$" + _ }
  def keyword_or_identLazy2: Parser[String] = rep1sep(keyword_or_identLazy, ".") ^^ { p =>
    p.reduce(_ + "." + _)
  }


  /** Parses a lazy identifier for a command.
    * @return
    *   A parser for a lazy identifier for a command.
    */
  def identLazyCMD: Parser[String] = rep1sep(subident, ".") ^^ { p =>
    p.reduce(_ + "." + _)
  }

  /** Parses an identifier tag.
    * @return
    *   A parser for an identifier tag.
    */
  def identTag: Parser[String] =
    ("@" | "@e" | "@a" | "@s" | "@r" | "@p") ~ identLazy2 ^^ { case a ~ b =>
      f"$a$b"
    }

  /** Parses an identifier for a function.
    * @return
    *   A parser for an identifier for a function.
    */
  def identFunction: Parser[String] = identTag | identLazy2

  /** Parses a floating point value.
    * @return
    *   A parser for a floating point value.
    */
  def floatValue: Parser[Double] = (numericLit <~ ".") ~ numericLit ^^ { p =>
    (p._1 + "." + p._2).toDouble
  }

  /** Parses a JSON string value.
    * @return
    *   A parser for a JSON string value.
    */
  def jsonValueStr: Parser[String] = (floatValue ^^ { p => p.toString() }
    | numericLit | stringLit2 ^^ (Utils.stringify(_)) | jsonStr
    | ("[" ~> repsep(jsonValueStr, ",")) <~ "]" ^^ { p =>
      "[" + p.reduce(_ + "," + _) + "]"
    }) ^^ { p => p.toString }

  /** Parses a JSON key-value pair as a string.
    * @return
    *   A parser for a JSON key-value pair as a string.
    */
  def jsonKeypairStr: Parser[String] = ((stringLit2 ^^ (Utils.stringify(_)) 
  	| identLazy) <~ ":") ~ jsonValueStr ^^ { p => p._1 + ":" + p._2 } |
    ("§§§" ~> stringLit <~ "§§§") ^^ { p => p.replaceAll("@", ":") }

  /** Parses a JSON string.
    * @return
    *   A parser for a JSON string.
    */
  def jsonStr: Parser[String] = "{" ~> rep1sep(jsonKeypairStr, ",") <~ "}" ^^ {
    p => "{" + p.reduce(_ + "," + _) + "}"
  }

  /** Parses a JSON value.
    * @return
    *   A parser for a JSON value.
    */
  def jsonValue: Parser[JSONElement] = exprNoTuple ~ opt(ident) ^^ {
    case p ~ t =>
      Utils.simplifyJsonExpression(JsonExpression(p, t.getOrElse(null)))
  }
    | json

  /** Parses a JSON array.
    * @return
    *   A parser for a JSON array.
    */
  def jsonArray: Parser[JSONElement] =
    ("[" ~> repsep(jsonValue, ",")) <~ "]" ^^ { p => JsonArray(p) }

  /** Parses a JSON key-value pair.
    * @return
    *   A parser for a JSON key-value pair.
    */
  def jsonKeypair: Parser[(String, JSONElement)] =
    ((stringLit2 | identLazy) <~ ":") ~ jsonValue ^^ { p => (p._1, p._2) } |
      ("§§§" ~> stringLit <~ "§§§") ^^ { p =>
        {
          val q = p.replaceAll("@", ":").split(":");
          (q(0), JsonExpression(parseExpression(q(1), true), null))
        }
      }

  /** Parses a JSON dictionary.
    * @return
    *   A parser for a JSON dictionary.
    */
  def jsonDic: Parser[JSONElement] = "{" ~> repsep(jsonKeypair, ",") <~ "}" ^^ {
    p => JsonDictionary(p.toMap)
  }

  /** Parses a JSON value or expression.
    * @return
    *   A parser for a JSON value or expression.
    */
  def json: Parser[JSONElement] = jsonDic | jsonArray

  /** Parses a function argument.
    * @return
    *   A parser for a function argument.
    */
  def argument: Parser[Argument] =
    types ~ identLazy ~ opt("=" ~> exprNoTuple) ^^ { case typ ~ name ~ value  =>
      Argument(name, typ, value)
    }

  /** Parses a list of function arguments.
    * @return
    *   A parser for a list of function arguments.
    */
  def arguments: Parser[List[Argument]] = "(" ~> repsep(argument, ",") <~ ")"

  /** Parses a function argument.
    * @return
    *   A parser for a function argument.
    */
  def templateArgument: Parser[TemplateArgument] =
    identLazy ~ opt("=" ~> exprBottom) ^^ { case n ~ v =>
      TemplateArgument(n, v)
    }

  /** Parses a list of function arguments.
    * @return
    *   A parser for a list of function arguments.
    */
  def templateArguments: Parser[List[TemplateArgument]] = opt("<" ~> repsep(templateArgument, ",") <~ ">") ^^ {case None =>List();case Some(value)=>value}

  def instruction: Parser[Instruction] = positioned(
    functionDecl
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
      | "%%%" ~> stringLit2 <~ "%%%" ^^ { p => CMD(p) }
      | ifs
      | "return" ~> opt(expr) ^^ { case e => Return(e.getOrElse(NullValue)) }
      | block
      | switch | whileLoop | doWhileLoop | forLoop | repeatLoop | jsonFile
      | "as" ~ "(" ~> exprNoTuple ~ ")" ~ instruction ^^ { case e ~ _ ~ i =>
        With(e, BoolValue(false), BoolValue(true), i, null)
      }
      | "at" ~ "(" ~> repsep(exprNoTuple, ",") ~ ")" ~ instruction ^^ {
        case e ~ _ ~ i => Execute(AtType, e, i)
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
      | constructorCall ^^ { case c => FreeConstructorCall(c) }
  )

  /** Parses a function declaration and returns a FunctionDecl object.
    *
    * @return
    *   A Parser object that parses a function declaration and returns a
    *   FunctionDecl object.
    */
  def functionDecl: Parser[FunctionDecl] =
    ((((doc ~ ("def" ~> modifier(
      "function_short"
    ))))) ~ arguments) ~ instruction ^^ { case doc ~ mod ~ a ~ i =>
      FunctionDecl("~", i, VoidType, a, List(), mod.withDoc(doc))
    }
      | ((((doc ~ ("def" ~> modifier(
        "abstract_function"
      ))) ~ identLazy ~ typeArgument)) ~ arguments) ^^ {
        case doc ~ mod ~ n ~ at ~ a =>
          FunctionDecl(
            n,
            InstructionList(List()),
            VoidType,
            a,
            at,
            mod.withDoc(doc)
          )
      }
      | ((((doc ~ (opt("def") ~> modifier(
        "abstract_function"
      ))) ~ types) ~ identLazy ~ typeArgument) ~ arguments) ^^ {
        case doc ~ mod ~ t ~ n ~ at ~ a =>
          FunctionDecl(n, InstructionList(List()), t, a, at, mod.withDoc(doc))
      }
      | ((((doc ~ ("def" ~> modifier(
        "function"
      ))) ~ identLazy ~ typeArgument)) ~ arguments) ~ functionInstruction ^^ {
        case doc ~ mod ~ n ~ at ~ a ~ i =>
          FunctionDecl(n, i, VoidType, a, at, mod.withDoc(doc))
      }
      | ((((doc ~ (opt("def") ~> modifier(
        "function"
      ))) ~ types) ~ identLazy ~ typeArgument) ~ arguments) ~ functionInstruction ^^ {
        case doc ~ mod ~ t ~ n ~ at ~ a ~ i =>
          FunctionDecl(n, i, t, a, at, mod.withDoc(doc))
      }

  /** Parses a function declaration and returns a FunctionDecl object.
    *
    * @return
    *   A Parser object that parses a function declaration and returns a
    *   FunctionDecl object.
    */
  def optFunctionDecl: Parser[OptionalFunctionDecl] =
    ((((doc ~ ("def" ~> modifier(
        "function"
      ))) ~ identLazy2 ~ typeArgument)) ~ arguments ~ "from" ~ identLazy2 ~ "as" ~ identLazy2) ^^ {
        case doc ~ mod ~ n ~ at ~ a ~ _ ~ lib ~ _ ~ alias=>
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
      | ((((doc ~ (opt("def") ~> modifier(
        "function"
      ))) ~ types) ~ identLazy2 ~ typeArgument) ~ arguments ~ "from" ~ identLazy2 ~ "as" ~ identLazy2) ^^ {
        case doc ~ mod ~ t ~ n ~ at ~ a ~ _ ~ lib ~ _ ~ alias =>
          OptionalFunctionDecl(alias,n,lib, t, a, at, mod.withDoc(doc))
      }

  /** Parses a function instruction and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses a function instruction and returns an
    *   Instruction object.
    */
  def functionInstruction: Parser[Instruction] =
    instruction | ("=" ~> exprNoTuple) ^^ (p =>
      InstructionList(List(Return(p)))
    )

  /** Parses a function call and returns a FunctionCall object.
    *
    * @return
    *   A Parser object that parses a function call and returns a FunctionCall
    *   object.
    */
  def functionCall: Parser[FunctionCall] = positioned(
    (((identFunction ~ typeVariables <~ "(") ~ repsep(
      exprNoTuple,
      ","
    )) <~ ")") ~ block ^^ { case f ~ t ~ e ~ b =>
      FunctionCall(f, e ::: List(LambdaValue(List(), b, null)), t)
    } // Function Call
      | (((identFunction ~ typeVariables <~ "(") ~ repsep(
        exprNoTuple,
        ","
      )) <~ ")") ^^ { case f ~ t ~ e => FunctionCall(f, e, t) } // Function Call
      | (identFunction ~ typeVariables) ~ block ^^ { case f ~ t ~ b =>
        FunctionCall(f, List(LambdaValue(List(), b, null)), t)
      }
  )

  def dotFunctionCall: Parser[Instruction] = {
    exprBottom ~ "." ~ functionCall ^^ { case e ~ _ ~ f =>
      DotCall(e, f)
    }
  }

  /** Parses a selector function call and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses a selector function call and returns an
    *   Instruction object.
    */
  def selectorFunctionCall: Parser[Instruction] = positioned(
    selector ~ "." ~ functionCall ^^ { case s ~ _ ~ f =>
      DotCall(SelectorValue(s), f)
    }
  )

  /** Parses a continuation and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses a continuation and returns an Instruction
    *   object.
    */
  def continuation: Parser[Instruction] = positioned(
    opt(";") ~> rep(instruction <~ opt(";")) ^^ (p => InstructionList(p))
  )

  /** Parses a sleep instruction and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses a sleep instruction and returns an
    *   Instruction object.
    */
  def sleepInstr: Parser[Instruction] = positioned(
    ("sleep" ~> exprNoTuple) ~ continuation ^^ { case e ~ instr =>
      Sleep(e, instr)
    }
  )

  /** Parses an await instruction and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses an await instruction and returns an
    *   Instruction object.
    */
  def awaitInstr: Parser[Instruction] = positioned(
    ("await" ~> functionCall) ~ continuation ^^ { case e ~ instr =>
      Await(e, instr)
    }
  )

  /** Parses an assert instruction and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses an assert instruction and returns an
    *   Instruction object.
    */
  def assertInstr: Parser[Instruction] = positioned(
    ("assert" ~> exprNoTuple) ~ continuation ^^ { case e ~ instr =>
      Assert(e, instr)
    }
  )

  /** Parses any reserved keyword and returns a String object.
    *
    * @return
    *   A Parser object that parses any reserved keyword and returns a String
    *   object.
    */
  def anyKeyword: Parser[String] =
    lexical.reserved.map(f => f ^^ (p => p)).reduce(_ | _)

  /** Parses a throw instruction and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses a throw instruction and returns an
    *   Instruction object.
    */
  def throwError: Parser[Instruction] = positioned("throw" ~ exprNoTuple ^^ {
    case _ ~ e => Throw(e)
  })

  /** Parses a try-catch-finally block and returns an Instruction object.
    *
    * @return
    *   A Parser object that parses a try-catch-finally block and returns an
    *   Instruction object.
    */
  def tryCatchFinalyBlock: Parser[Instruction] = positioned(
    "try" ~> block ~ opt("catch" ~> block) ~ opt("finally" ~> block) ^^ {
      case b ~ c ~ f =>
        Try(
          b,
          c.getOrElse(InstructionList(List())),
          f.getOrElse(InstructionList(List()))
        )
    }
  )

  /** Parses a predicate declaration instruction.
    *
    * A predicate declaration defines a function that takes arguments and
    * returns a boolean value.
    *
    * @return
    *   a `Parser` that matches a predicate declaration instruction and returns
    *   a `PredicateDecl` object.
    */
  def predicate: Parser[Instruction] = positioned(
    doc ~ (modifier(
      "predicate"
    ) <~ "predicate") ~ identLazy ~ arguments ~ json ^^ {
      case doc ~ mod ~ name ~ args ~ json =>
        PredicateDecl(name, args, json, mod.withDoc(doc))
    }
  )

  /** Parses an array assignment instruction.
    *
    * An array assignment assigns a value to an element of an array.
    *
    * @return
    *   a `Parser` that matches an array assignment instruction and returns an
    *   `ArrayAssigment` object.
    */
  def arrayAssign: Parser[Instruction] = positioned(
    (identLazy2 <~ "[") ~ (rep1sep(
      exprNoTuple,
      ","
    ) <~ "]") ~ assignmentOp ~ expr ^^ { case a ~ i ~ o ~ e =>
      ArrayAssigment(Left(a), i, o, e)
    }
  )

  /** Parses a `foreach` loop instruction.
    *
    * A `foreach` loop iterates over the elements of a collection and executes a
    * block of code for each element.
    *
    * @return
    *   a `Parser` that matches a `foreach` loop instruction and returns a
    *   `ForEach` object.
    */
  def foreach: Parser[Instruction] = positioned(
    (("foreach" ~ opt("(") ~> ident <~ "in") ~ exprNoTuple <~ opt(
      ")"
    )) ~ instruction ^^ { case v ~ e ~ i => ForEach(v, e, i) }
  )

  /** Parses a package declaration instruction.
    *
    * A package declaration defines a package that contains classes and other
    * packages.
    *
    * @return
    *   a `Parser` that matches a package declaration instruction and returns a
    *   `Package` object.
    */
  def packageInstr: Parser[Instruction] = positioned(
    "package" ~> identLazy2 ~ program ^^ (p => Package(p._1, p._2))
  )

  /** Parses a list of interfaces.
    *
    * An interface defines a set of methods that a class must implement.
    *
    * @return
    *   a `Parser` that matches a list of interfaces and returns a list of
    *   tuples containing the interface name and its type variables.
    */
  def interfaces: Parser[List[(String, List[Type])]] =
    opt("implements" ~> rep1sep(ident2 ~ typeVariables, ",")) ^^ { p =>
      p.getOrElse(List()).map { case i ~ t => (i, t) }
    }

  /** Parses a class declaration instruction.
    *
    * A class declaration defines a class that contains fields, methods, and
    * other classes.
    *
    * @return
    *   a `Parser` that matches a class declaration instruction and returns a
    *   `ClassDecl` object.
    */
  def classDecl: Parser[Instruction] = positioned(
    doc ~ (modifier("class") <~ "class") ~ identLazy ~ typeArgument ~ opt(
      "extends" ~> ident2 ~ typeVariables
    ) ~ interfaces ~ rep("with" ~> namespacedName ~ "for" ~ ident) ~ block ^^ {
      case doc ~ mod ~ iden ~ typeargs ~ par ~ interface ~ entity ~ block =>
        ClassDecl(
          iden,
          typeargs,
          block,
          mod.withDoc(doc),
          if par.isDefined then Some(par.get._1) else None,
          if par.isDefined then (par.get._2) else List(),
          interface,
          entity.map { case e ~ _ ~ n => (n, e) }.toMap
        )
    }
  )

  /** Parses an interface declaration.
    *
    * @return
    *   A parser for an interface declaration.
    */
  def interfaceDecl: Parser[Instruction] = positioned(
    doc ~ (modifier(
      "interface"
    ) <~ "interface") ~ identLazy ~ typeArgument ~ opt(
      "extends" ~> ident2 ~ typeVariables
    ) ~ interfaces ~ block ^^ {
      case doc ~ mod ~ iden ~ typeargs ~ par ~ interface ~ block =>
        ClassDecl(
          iden,
          typeargs,
          block,
          mod.withDoc(doc),
          if par.isDefined then Some(par.get._1) else None,
          if par.isDefined then (par.get._2) else List(),
          interface,
          Map()
        )
    }
  )

  /** Parses a case class declaration.
    *
    * @return
    *   A parser for a case class declaration.
    */
  def caseClassDecl: Parser[Instruction] = positioned(
    doc ~ (modifier("class") <~ opt(
      "case"
    ) ~ "class") ~ identLazy ~ typeArgument ~ arguments ~ opt(
      "extends" ~> ident2 ~ typeVariables
    ) ~ interfaces ~ rep("with" ~> namespacedName ~ "for" ~ ident) ~ block ^^ {
      case doc ~ mod ~ iden ~ typeargs ~ arguments ~ par ~ interface ~ entity ~ block =>
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
          if par.isDefined then Some(par.get._1) else None,
          if par.isDefined then (par.get._2) else List(),
          interface,
          entity.map { case e ~ _ ~ n => (n, e) }.toMap
        )
    }
  )

  /** Returns a case class instruction.
    *
    * @param args
    *   The arguments for the case class.
    * @param rest
    *   The rest of the case class.
    * @return
    *   A case class instruction.
    */
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

  /** Parses a struct declaration with a case modifier.
    * @return
    *   A StructDecl instruction.
    */
  def caseStruct: Parser[Instruction] = positioned(
    doc ~ (modifier("struct") <~ opt(
      "case"
    ) ~ "struct") ~ identLazy ~ typeArgument ~ arguments ~ opt(
      "extends" ~> ident2
    ) ~ opt(block) ^^ { case doc ~ mod ~ iden ~ typeargs ~ args ~ par ~ block =>
      StructDecl(
        iden,
        typeargs,
        getCaseClassIntruction(args, block),
        mod.withDoc(doc),
        par
      )
    }
  )

  /** Parses a regular struct declaration.
    * @return
    *   A StructDecl instruction.
    */
  def structDecl: Parser[Instruction] = positioned(
    doc ~ (modifier("struct") <~ "struct") ~ identLazy ~ typeArgument ~ opt(
      "extends" ~> ident2
    ) ~ block ^^ { case doc ~ mod ~ iden ~ typeargs ~ par ~ block =>
      StructDecl(iden, typeargs, block, mod.withDoc(doc), par)
    }
  )

  /** Parses a regular extension declaration.
    * @return
    *   A ExtensionDecl instruction.
    */
  def extensionDecl: Parser[Instruction] = positioned(
    doc ~ (modifier("extension") <~ "extension") ~ types ~ blockExtension ^^ { 
      case doc ~ mod ~ typ ~ block => ExtensionDecl(typ, block, mod.withDoc(doc))
    }
  )

  /** Parses a type definition inner block.
    * @return
    *   A tuple containing the name, type, and optional alias of the type
    *   definition.
    */
  def typedefInner: Parser[(String, Type, String)] =
    types ~ identLazy ~ opt("for" ~> identLazy) ^^ { case typ ~ str1 ~ str2 =>
      (str1, typ, str2.getOrElse(""))
    }

  /** Parses a type definition.
    * @return
    *   A TypeDef instruction.
    */
  def typedef: Parser[Instruction] = positioned(
    "typedef" ~> rep1sep(typedefInner, ",") ^^ { case t => TypeDef(t) }
  )

  /** Parses a template use block.
    * @return
    *   A TemplateUse instruction.
    */
  def templateUse: Parser[Instruction] = positioned(
    ident2 ~ typeArgumentExpression ~ identLazy ~ block ^^ {
      case iden ~ values ~ name ~ instr =>
        TemplateUse(iden, name, instr, values)
    }
  )

  /** Parses a template declaration block.
    * @return
    *   A TemplateDecl instruction.
    */
  def templateDesc: Parser[Instruction] = positioned(
    doc ~ (modifier("template") <~ "template") ~ identLazy ~ templateArguments ~ opt(
      "extends" ~> (ident2 ~ typeArgumentExpression)
    ) ~ instruction ^^ {
      case doc ~ mod ~ name ~ generics ~ Some(
            parent ~ genericsParent
          ) ~ instr =>
        TemplateDecl(
          name,
          instr,
          mod.withDoc(doc),
          Some(parent),
          generics,
          genericsParent
        );
      case doc ~ mod ~ name ~ generics ~ None ~ instr =>
        TemplateDecl(name, instr, mod.withDoc(doc), None, generics, List())
    }
  )

  /** Parses a short import statement.
    * @return
    *   An Import instruction.
    */
  def importShortInst: Parser[Instruction] = positioned(
    "import" ~> ident2 ~ "::" ~ ident2 ~ opt("as" ~> ident2) ^^ {
      case file ~ _ ~ res ~ alias => Import(file, res, alias.getOrElse(null))
    }
  )

  /** Parses a regular import statement.
    * @return
    *   An Import instruction.
    */
  def importInst: Parser[Instruction] = positioned(
    "import" ~> ident2 ~ opt("as" ~> ident2) ^^ { case file ~ alias =>
      Import(file, null, alias.getOrElse(null))
    }
  )

  /** Parses a from-import statement.
    * @return
    *   An Import instruction.
    */
  def fromImportInst: Parser[Instruction] = positioned(
    "from" ~> ident2 ~ ("import" ~> ident2) ~ opt("as" ~> ident2) ^^ {
      case file ~ res ~ alias => Import(file, res, alias.getOrElse(null))
    }
  )

  /** Parses a for-generate loop.
    * @return
    *   An Instruction.
    */
  def forgenerate: Parser[Instruction] = positioned(
    (("forgenerate" ~> "(" ~> identLazy <~ ",") ~ exprNoTuple <~ ")") ~ instruction ^^ (
      p => ForGenerate(p._1._1, p._1._2, p._2)
    )
  )

  /** Parses a JSON file block.
    * @return
    *   A JSONFile instruction.
    */
  def jsonFile: Parser[Instruction] = positioned(
    doc ~ modifier("jsonfile") ~ "jsonfile" ~ identLazy2 ~ expr ^^ {
      case d ~ m ~ _ ~ n ~ json => JSONFile(n, json, m.withDoc(d))
    }
  )

  /** Parses a do-while loop.
    * @return
    *   A DoWhileLoop instruction.
    */
  def doWhileLoop: Parser[Instruction] = positioned(
    ("do" ~> instruction <~ "while") ~ ("(" ~> exprNoTuple <~ ")") ^^ (p =>
      DoWhileLoop(p._2, p._1)
    )
  )

  /** Parses a while loop.
    * @return
    *   A WhileLoop instruction.
    */
  def whileLoop: Parser[Instruction] = positioned(
    ("while" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ^^ (p =>
      WhileLoop(p._1, p._2)
    )
  )

  /** Parses a for loop.
    * @return
    *   An InstructionBlock containing a ForLoop and a WhileLoop.
    */
  def forLoop: Parser[Instruction] = positioned(
    ((("for" ~> "(" ~> instruction <~ ";") ~ exprNoTuple <~ ";") ~ instruction <~ ")") ~ instruction ^^
      (p =>
        InstructionBlock(
          List(
            p._1._1._1,
            WhileLoop(p._1._1._2, InstructionList(List(p._2, p._1._2)))
          )
        )
      )
  )

  /** Parses a repeat loop.
    * @return
    *   A FunctionCall instruction.
    */
  def repeatLoop: Parser[Instruction] = positioned(
    ("repeat" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ^^ {
      case value ~ intr =>
        FunctionCall(
          Identifier.fromString("__repeat__"),
          List(value, LambdaValue(List(), intr, null)),
          List()
        )
    }
  )

  /** Parses a with statement.
    * @return
    *   A With instruction.
    */
  def withInstr: Parser[Instruction] = positioned(
    ("with" ~> "(" ~> exprNoTuple <~ ")") ~ instruction ~ opt(
      "else" ~> instruction
    ) ^^ { case sel ~ intr ~ elze =>
      With(sel, BoolValue(false), BoolValue(true), intr, elze.getOrElse(null))
    }
      | (("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ~ opt(
        "else" ~> instruction
      ) ^^ { case sel ~ isat ~ intr ~ elze =>
        With(sel, isat, BoolValue(true), intr, elze.getOrElse(null))
      }
      | ((("with" ~> "(" ~> exprNoTuple <~ ",") ~ exprNoTuple <~ ",") ~ exprNoTuple <~ ")") ~ instruction ~ opt(
        "else" ~> instruction
      ) ^^ { case sel ~ isat ~ cond ~ intr ~ elze =>
        With(sel, isat, cond, intr, elze.getOrElse(null))
      }
  )

  /** Parses a switch statement.
    * @return
    *   A Switch instruction.
    */
  def switch: Parser[Switch] = positioned(
    ("switch" ~> exprNoTuple <~ "{") ~ rep(switchCase) <~ "}" ^^ (p =>
      Switch(p._1, p._2)
    )
  )

  /** Parses an optional "if" expression and returns a `BoolValue(true)` if it
    * is not present.
    *
    * @return
    *   A `Parser` that returns an `Expression`.
    */
  def switchIf: Parser[Expression] =
    opt("if" ~> exprNoTuple) ^^ (p => p.getOrElse(BoolValue(true)))

  /** Parses a `SwitchCase` element.
    *
    * @return
    *   A `Parser` that returns a `SwitchElement`.
    */
  def switchCaseBase: Parser[SwitchCase] =
    (exprNoTuple ~ switchIf <~ (("-" ~ ">") | "=>")) ~ instruction ^^ {
      case e ~ c ~ i => SwitchCase(e, i, c)
    } |
      ("case" ~> exprNoTuple ~ switchIf <~ ":") ~ instruction ^^ {
        case e ~ c ~ i => SwitchCase(e, i, c)
      } |
      ((("default" | "else") ~> switchIf <~ ":")) ~ instruction ^^ {
        case c ~ i => SwitchCase(DefaultValue, i, c)
      } |
      ((("default" | "else") ~> switchIf <~ (("-" ~ ">") | "=>"))) ~ instruction ^^ {
        case c ~ i => SwitchCase(DefaultValue, i, c)
      }

  /** Parses a `SwitchElement` element.
    *
    * @return
    *   A `Parser` that returns a `SwitchElement`.
    */
  def switchCase: Parser[SwitchElement] =
    switchCaseBase
      | positioned(
        (("foreach" ~ opt("(") ~> ident <~ "in") ~ exprNoTuple <~ opt(
          ")"
        )) ~ (opt("{") ~> switchCaseBase <~ opt("}")) ^^ { case v ~ e ~ i =>
          SwitchForEach(v, e, i)
        }
      )
      | positioned(
        (("forgenerate" ~> "(" ~> identLazy <~ ",") ~ exprNoTuple <~ ")") ~ (opt(
          "{"
        ) ~> switchCaseBase <~ opt("}")) ^^ (p =>
          SwitchForGenerate(p._1._1, p._1._2, p._2)
        )
      )

  /** Parses an `Execute` instruction with two arguments.
    *
    * @return
    *   A `Parser` that returns an `Instruction`.
    */
  def rotated1: Parser[Instruction] = positioned(
    "rotated" ~ "(" ~ exprNoTuple ~ "," ~ exprNoTuple ~ ")" ~ instruction ^^ {
      case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ i => Execute(RotatedType, List(e1, e2), i)
    }
  )

  /** Parses an `Execute` instruction with one argument.
    *
    * @return
    *   A `Parser` that returns an `Instruction`.
    */
  def rotated2: Parser[Instruction] = positioned(
    "rotated" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i =>
      Execute(RotatedType, List(e), i)
    }
  )

  /** Parses an `Execute` instruction with two arguments.
    *
    * @return
    *   A `Parser` that returns an `Instruction`.
    */
  def facing1: Parser[Instruction] = positioned(
    "facing" ~ "(" ~ exprNoTuple ~ "," ~ exprNoTuple ~ ")" ~ instruction ^^ {
      case _ ~ _ ~ e1 ~ _ ~ e2 ~ _ ~ i => Execute(FacingType, List(e1, e2), i)
    }
  )

  /** Parses an `Execute` instruction with one argument.
    *
    * @return
    *   A `Parser` that returns an `Instruction`.
    */
  def facing2: Parser[Instruction] = positioned(
    "facing" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i =>
      Execute(FacingType, List(e), i)
    }
  )

  /** Parses an `Execute` instruction with one argument.
    *
    * @return
    *   A `Parser` that returns an `Instruction`.
    */
  def align: Parser[Instruction] = positioned(
    "align" ~ exprNoTuple ~ instruction ^^ { case _ ~ e ~ i =>
      Execute(AlignType, List(e), i)
    }
  )

  /** Parses a `TagDecl` instruction for a block tag, entity tag, or item tag.
    *
    * @return
    *   A `Parser` that returns an `Instruction`.
    */
  def blocktag: Parser[Instruction] =
    positioned(
      doc ~ modifier("blocktag") ~ "blocktag" ~ identLazy2 ~ "{" ~ repsep(
        tagentry,
        ","
      )  ~ (opt(",") ~> "}") ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ =>
        TagDecl(n, c, m.withDoc(d), objects.BlockTag)
      }
    ) |
      positioned(
        doc ~ modifier("entitytag") ~ "entitytag" ~ identLazy2 ~ "{" ~ repsep(
          tagentry,
          ","
        ) ~ (opt(",") ~> "}") ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ =>
          TagDecl(n, c, m.withDoc(d), objects.EntityTag)
        }
      ) |
      positioned(
        doc ~ modifier("itemtag") ~ "itemtag" ~ identLazy2 ~ "{" ~ repsep(
          tagentry,
          ","
        ) ~ (opt(",") ~> "}") ^^ { case d ~ m ~ _ ~ n ~ _ ~ c ~ _ =>
          TagDecl(n, c, m.withDoc(d), objects.ItemTag)
        }
      )

  /** Parses a `tagentry` element.
    *
    * @return
    *   A `Parser` that returns an `Expression`.
    */
  def tagentry: Parser[Expression] = positioned(
    namespacedName | (identLazy2 ^^ (VariableValue(_))) | tagValue
  )

  /** Parser for Enum declaration
    * @return
    *   EnumDecl
    */
  def enumInstr: Parser[EnumDecl] = positioned(
    (doc ~ modifier("enum") ~ ("enum" ~> identLazy) ~ opt(
      "(" ~> repsep(enumField, ",") <~ ")"
    ) <~ "{") ~ repsep(enumValue, ",") <~ opt(",") <~ "}" ^^ { case doc ~ mod ~ n ~ f ~ v =>
      EnumDecl(n, f.getOrElse(List()), v, mod.withDoc(doc))
    }
  )

  /** Parser for Enum field
    * @return
    *   EnumField
    */
  def enumField: Parser[EnumField] = types ~ identLazy ^^ { p =>
    EnumField(p._2, p._1)
  }

  /** Parser for Enum value
    * @return
    *   EnumValue
    */
  def enumValue: Parser[EnumValue] =
    identLazy ~ opt("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ (p =>
      EnumValue(p._1, p._2.getOrElse(List()))
    )

  /** Parser for variable assignment
    * @return
    *   Instruction
    */
  def varAssignment: Parser[Instruction] = positioned(
    (rep1sep(opt(selector <~ ".") ~ identLazy2, ",") ~ assignmentOp ~ expr) ^^ {
      case id ~ op ~ expr => {
        val identifiers = id.map(p =>
          (Identifier.fromString(p._2), p._1.getOrElse(Selector.self))
        )
        VariableAssigment(identifiers.map((i, s) => (Left(i), s)), op, expr)
      }
    } |
      (rep1sep(opt(selector <~ ".") ~ identLazy2, ",") <~ ("+" ~ "+")) ^^ {
        case id => {
          val identifiers = id.map(p =>
            (Identifier.fromString(p._2), p._1.getOrElse(Selector.self))
          )
          VariableAssigment(
            identifiers.map((i, s) => (Left(i), s)),
            "+=",
            IntValue(1)
          )
        }
      } |
      (rep1sep(opt(selector <~ ".") ~ identLazy2, ",") <~ ("-" ~ "-")) ^^ {
        case id => {
          val identifiers = id.map(p =>
            (Identifier.fromString(p._2), p._1.getOrElse(Selector.self))
          )
          VariableAssigment(
            identifiers.map((i, s) => (Left(i), s)),
            "-=",
            IntValue(1)
          )
        }
      }
  )

  /** Parser for single variable assignment
    * @return
    *   VariableAssigment
    */
  def singleVarAssignment: Parser[VariableAssigment] = positioned(
    (opt(selector <~ ".") ~ identLazy2 ~ assignmentOp ~ expr) ^^ {
      case s ~ i ~ op ~ e =>
        VariableAssigment(List((Left(i), s.getOrElse(Selector.self))), op, e)
    } |
      ("+" ~ "+") ~> opt(selector <~ ".") ~ identLazy2 ^^ { case s ~ i =>
        VariableAssigment(
          List((Left(i), s.getOrElse(Selector.self))),
          "+=",
          IntValue(1)
        )
      } |
      ("-" ~ "-") ~> opt(selector <~ ".") ~ identLazy2 ^^ { case s ~ i =>
        VariableAssigment(
          List((Left(i), s.getOrElse(Selector.self))),
          "-=",
          IntValue(1)
        )
      }
  )

  /** Parser for single variable assignment
    * @return
    *   VariableAssigment
    */
  def singleVarAssignmentPos: Parser[VariableAssigment] = positioned(
    (
      opt(selector <~ ".") ~ identLazy2 <~ ("+" ~ "+") ^^ { case s ~ i =>
        VariableAssigment(
          List((Left(i), s.getOrElse(Selector.self))),
          "+=",
          IntValue(1)
        )
      } |
      opt(selector <~ ".") ~ identLazy2 <~ ("-" ~ "-") ^^ { case s ~ i =>
        VariableAssigment(
          List((Left(i), s.getOrElse(Selector.self))),
          "-=",
          IntValue(1)
        )
      }
    )
  )

  /** Parser for variable declaration
    * @return
    *   VariableDecl
    */
  def varDeclaration: Parser[VariableDecl] = positioned(
    (doc ~ modifier("variable") ~ types ~ rep1sep(identLazy, ",") ~ opt(
      assignmentOp ~ expr
    )) ^^ {
      case doc ~ mod1 ~ typ ~ names ~ expr => {
        val mod = mod1.withDoc(doc)
        val identifiers = names.map(Identifier.fromString(_))
        if (!mod.isEntity && expr.isEmpty) {
          VariableDecl(names, typ, mod, ":=", DefaultValue)
        } else if (!expr.isEmpty) {
          VariableDecl(names, typ, mod, expr.get._1, expr.get._2)
        } else {
          VariableDecl(names, typ, mod, null, null)
        }
      }
    }
  )

  /** Parser for property declarations.
    * @return
    *   A parser for property declarations.
    */
  def propertyDeclaration: Parser[Instruction] = positioned(
    varDeclaration ~ "{" ~ rep1sep(
      ("private" | "protected" | "public") ~ ident,
      (";" | ",")
    ) ~ opt(";" | ",") ~ "}" ^^ {
      case VariableDecl(v, typ, mod, op, expr) ~ _ ~ m ~ _ ~ _ =>
        var hasGet = m.exists { case _ ~ "get" => true; case _ => false }
        var hasSet = m.exists { case _ ~ "set" => true; case _ => false }

        val pp = v.map(x => {
          TemplateUse(
            Identifier.fromString("property"),
            x,
            InstructionBlock(
              List(
                if (hasGet)
                  FunctionDecl(
                    "get",
                    Return(VariableValue(Identifier.fromString("--" + x))),
                    typ,
                    List(),
                    List(),
                    mod
                  )
                else EmptyInstruction,
                if (hasSet)
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
                else EmptyInstruction
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

  /** Parser for if statements.
    * @return
    *   A parser for if statements.
    */
  def ifs: Parser[If] = positioned(
    ("if" ~> "(" ~> expr <~ ")") ~ instruction ~
      rep(("else" ~> "if" ~> "(" ~> expr <~ ")") ~ instruction) ~
      opt("else" ~> instruction) ^^ { p =>
        {
          val elze =
            p._1._2.map(k => ElseIf(k._1, k._2)) ::: (if p._2.isEmpty then Nil
                                                      else
                                                        List(
                                                          ElseIf(
                                                            BoolValue(true),
                                                            p._2.get
                                                          )
                                                        )
            )
          If(p._1._1._1, p._1._1._2, elze)
        }
      }
  )

  /** Parses a lambda expression with one parameter
    * @return
    *   a Parser for a lambda expression with one parameter
    */
  def lambda1: Parser[Expression] = positioned(
    (identLazy2 <~ "=>") ~ instruction ^^ (p =>
      LambdaValue(List(p._1), p._2, null)
    )
  )

  /** Parses a lambda expression with multiple parameters
    * @return
    *   a Parser for a lambda expression with multiple parameters
    */
  def lambda2: Parser[Expression] = positioned(
    ("(" ~> repsep(identLazy2, ",") <~ ")" <~ "=>") ~ instruction ^^ (p =>
      LambdaValue(p._1, p._2, null)
    )
  )

  /** Parses a lambda expression
    * @return
    *   a Parser for a lambda expression
    */
  def lambda = lambda1 | lambda2

  /** Parses a selector filter field
    * @return
    *   a Parser for a selector filter field
    */
  def sfField: Parser[(String, SelectorFilterValue)] =
    ident ~ "=" ~ selectorFilterInnerValue ^^ { case n ~ _ ~ v => (n, v) }

  /** Parses a selector filter composed of multiple fields
    * @return
    *   a Parser for a selector filter composed of multiple fields
    */
  def sfCompound: Parser[SelectorFilterValue] =
    "{" ~> rep1sep(sfField, ",") <~ "}" ^^ { case a =>
      SelectorComposed(a.toMap)
    }

  /** Parses a selector filter number value
    * @return
    *   a Parser for a selector filter number value
    */
  def sfNumber: Parser[SelectorFilterValue] = floatValue ^^ (SelectorNumber(_))

  /** Parses a selector filter number value
    * @return
    *   a Parser for a selector filter number value
    */
  def sfNumber2: Parser[SelectorFilterValue] =
    numericLit ^^ (p => SelectorNumber(p.toInt))

  /** Parses a selector filter negative number value
    * @return
    *   a Parser for a selector filter negative number value
    */
  def sfNumber3: Parser[SelectorFilterValue] =
    "-" ~> floatValue ^^ (p => SelectorNumber(-p))

  /** Parses a selector filter negative number value
    * @return
    *   a Parser for a selector filter negative number value
    */
  def sfNumber4: Parser[SelectorFilterValue] =
    "-" ~> numericLit ^^ (p => SelectorNumber(f"-$p".toInt))

  /** Parses a selector filter string value
    * @return
    *   a Parser for a selector filter string value
    */
  def sfString: Parser[SelectorFilterValue] = stringLit2 ^^ (SelectorString(_))

  /** Parses a selector filter identifier value
    * @return
    *   a Parser for a selector filter identifier value
    */
  def sfIdentifier: Parser[SelectorFilterValue] =
    keyword_or_identLazy2 ^^ (SelectorIdentifier(_))

  /** Parses a selector filter tag value
    * @return
    *   a Parser for a selector filter tag value
    */
  def sfTag: Parser[SelectorFilterValue] = tagValue ^^ { case TagValue(v) =>
    SelectorTag(v)
  }

  /** Parses a selector filter namespaced name value
    * @return
    *   a Parser for a selector filter namespaced name value
    */
  def sfNamespacedName: Parser[SelectorFilterValue] =
    namespacedName ^^ (p => SelectorIdentifier(p.toString()))

  /** Parses a selector filter NBT value
    * @return
    *   a Parser for a selector filter NBT value
    */
  def sfNBT: Parser[SelectorFilterValue] = json ^^ (SelectorNbt(_))

  /** Parses a selector filter inner value
    * @return
    *   a Parser for a selector filter inner value
    */
  def selectorFilterInnerValue2 =
    sfNumber | sfNumber3 | sfString | sfNamespacedName | sfIdentifier | sfNumber2 | sfNumber4 | sfNBT | sfCompound | sfTag

  /** Parses a selector filter inner value with optional range
    * @return
    *   a Parser for a selector filter inner value with optional range
    */
  def selectorFilterInnerValue = opt(selectorFilterInnerValue2) ~ opt(
    ".." ~ opt(selectorFilterInnerValue2)
  ) ^^ {
    case Some(a) ~ Some(b, Some(c)) => SelectorRange(a, c)
    case Some(a) ~ Some(b, None)    => SelectorGreaterRange(a)
    case Some(a) ~ None             => a
    case None ~ Some(b, Some(c))    => SelectorLowerRange(c)
    case other => throw new Exception(f"Invalid Selector filter: $other")
  }

  /** Parses a SelectorFilterValue.
    *
    * @return
    *   A Parser for SelectorFilterValue.
    */
  def selectorFilterValue: Parser[SelectorFilterValue] =
    ("!" ~> selectorFilterInnerValue ^^ (SelectorInvert(
      _
    ))) | selectorFilterInnerValue

  /** Parses a SelectorFilter.
    *
    * @return
    *   A Parser for SelectorFilter.
    */
  def selectorFilter: Parser[(String, SelectorFilterValue)] =
    (identLazy <~ "=") ~ selectorFilterValue ^^ { p => (p._1, p._2) }

  /** Parses a Selector.
    *
    * @return
    *   A Parser for Selector.
    */
  def selector: Parser[Selector] = ("@a" | "@s" | "@e" | "@p" | "@r") ~ opt(
    "[" ~> rep1sep(selectorFilter, ",") <~ "]"
  ) ^^ { p => Selector.parse(p._1, p._2.getOrElse(List())) }

  /** Parses a Selector as a String.
    *
    * @return
    *   A Parser for Selector as a String.
    */
  def selectorStr: Parser[String] = (selector ^^ (_.toString()))

  /** Parses a string literal and replaces ◘ with \".
    *
    * @return
    *   A Parser for a string literal.
    */
  def stringLit2: Parser[String] = stringLit ^^ { p =>
    p.replaceAllLiterally("◘", "\\\"")
  }

  /** Parses any word.
    *
    * @return
    *   A Parser for any word.
    */
  def anyWord = lexical.reserved.foldLeft(ident2) { (a, b) => a | b } | ident

  /** Parses a block data field.
    *
    * @return
    *   A Parser for a block data field.
    */
  def blockDataField = anyWord ~ "=" ~ exprNoTuple ^^ { case n ~ _ ~ v =>
    n + "=" + v
  }

  /** Parses a block data.
    *
    * @return
    *   A Parser for a block data.
    */
  def blockData = "[" ~> rep1sep(blockDataField, ",") <~ "]" ^^ { case fields =>
    fields.mkString("[", ",", "]")
  }

  /** Parses a namespaced name.
    *
    * @return
    *   A Parser for a namespaced name.
    */
  def namespacedName =
    keyword_or_identLazy ~ ":" ~ keyword_or_identLazy2 ~ opt(blockData) ~ opt(json) ^^ {
      case a ~ _ ~ b ~ d ~ j =>
        NamespacedName(
          a + ":" + b + d.getOrElse(""),
          JsonValue(j.getOrElse(JsonNull))
        )
    }

  /** Parses a valid coordinate number.
    *
    * @return
    *   A Parser for a valid coordinate number.
    */
  def validCordNumber1: Parser[Expression] = floatValue ^^ {
    FloatValue(_)
  } | (numericLit ^^ { i => IntValue(i.toInt) }) | (identLazyForce ^^ {
    VariableValue(_)
  })

  /** Parses a valid coordinate number.
    *
    * @return
    *   A Parser for a valid coordinate number.
    */
  def validCordNumber2: Parser[Expression] =
    floatValue ^^ (p => FloatValue(p))
      | numericLit ^^ (p => IntValue(p.toInt))
      | identLazyForce ^^ { VariableValue(_) }
      | "-" ~> floatValue ^^ (p => FloatValue(-p))
      | "-" ~> numericLit ^^ (p => IntValue(f"-$p".toInt))
      | "-" ~> identLazyForce ^^ { s =>
        BinaryOperation("+", StringValue("-"), VariableValue(s))
      }

  /** Parses a relative coordinate.
    *
    * @return
    *   A Parser for a relative coordinate.
    */
  def relCoordinate1: Parser[Expression] = "~" ~> validCordNumber2 ^^ {
    BinaryOperation("+", StringValue("~"), _)
  }

  /** Parses a relative coordinate.
    *
    * @return
    *   A Parser for a relative coordinate.
    */
  def relCoordinate2: Parser[StringValue] = "~" ^^^ StringValue("~")

  /** Parses a relative coordinate.
    *
    * @return
    *   A Parser for a relative coordinate.
    */
  def relCoordinate: Parser[Expression] =
    relCoordinate1 | relCoordinate2 | validCordNumber2

  /** Parses a front coordinate number.
    *
    * @return
    *   A Parser for a front coordinate number.
    */
  def frontCoordinateNumber: Parser[Expression] = "^" ~> validCordNumber2 ^^ {
    { BinaryOperation("+", StringValue("^"), _) }
  }

  /** Parses a front coordinate here.
    *
    * @return
    *   A Parser for a front coordinate here.
    */
  def frontCoordinateHere: Parser[StringValue] = "^" ^^^ StringValue("^")

  /** Parses a front coordinate.
    *
    * @return
    *   A Parser for a front coordinate.
    */
  def frontCoordinate: Parser[Expression] =
    frontCoordinateNumber | frontCoordinateHere

  /** Parses a relative position case 1.
    *
    * @return
    *   A Parser for a relative position case 1.
    */
  def relPositionCase1: Parser[PositionValue] =
    relCoordinate2 ~ validCordNumber2 ~ relCoordinate ^^ { case x ~ y ~ z =>
      PositionValue(x, y, z)
    }

  /** Parses a relative position case 2.
    *
    * @return
    *   A Parser for a relative position case 2.
    */
  def relPositionCase2: Parser[PositionValue] =
    relCoordinate ~ relCoordinate2 ~ validCordNumber2 ^^ { case x ~ y ~ z =>
      PositionValue(x, y, z)
    }

  /** Parses a front position.
    *
    * @return
    *   A Parser for a front position.
    */
  def frontPosition: Parser[PositionValue] =
    frontCoordinate ~ frontCoordinate ~ frontCoordinate ^^ { case x ~ y ~ z =>
      PositionValue(x, y, z)
    }

  /** Parses a relative position.
    *
    * @return
    *   A Parser for a relative position.
    */
  def relPosition: Parser[PositionValue] =
    relCoordinate ~ relCoordinate ~ relCoordinate ^^ { case x ~ y ~ z =>
      PositionValue(x, y, z)
    }

  /** Parses a position.
    *
    * @return
    *   A Parser for a position.
    */
  def position: Parser[PositionValue] =
    (frontPosition | relPosition | relPositionCase1 | relPositionCase2)

  /** Parses a relative coordinate as an expression.
    *
    * @return
    *   A Parser for a relative coordinate as an expression.
    */
  def relCoordinate1Expr: Parser[Expression] = "~" ~> exprAs ^^ {
    BinaryOperation("+", StringValue("~"), _)
  }

  /** Parses a relative coordinate as an expression.
    *
    * @return
    *   A Parser for a relative coordinate as an expression.
    */
  def relCoordinate2Expr: Parser[StringValue] = "~" ^^^ StringValue("~")

  /** Parses a relative coordinate as an expression.
    *
    * @return
    *   A Parser for a relative coordinate as an expression.
    */
  def relCoordinateExpr: Parser[Expression] =
    relCoordinate1Expr | relCoordinate2Expr | exprAs

  /** Parses a front coordinate number as an expression.
    *
    * @return
    *   A Parser for a front coordinate number as an expression.
    */
  def frontCoordinateNumberExpr: Parser[Expression] = "^" ~> exprAs ^^ {
    { BinaryOperation("+", StringValue("^"), _) }
  }

  /** Parses a front coordinate here as an expression.
    *
    * @return
    *   A Parser for a front coordinate here as an expression.
    */
  def frontCoordinateHereExpr: Parser[StringValue] = "^" ^^^ StringValue("^")

  /** Parses a front coordinate as an expression.
    *
    * @return
    *   A Parser for a front coordinate as an expression.
    */
  def frontCoordinateExpr: Parser[Expression] =
    frontCoordinateNumberExpr | frontCoordinateHereExpr

  /** Parses a relative position case 1 as an expression.
    *
    * @return
    *   A Parser for a relative position case 1 as an expression.
    */
  def relPositionCase1Expr: Parser[PositionValue] =
    relCoordinate2Expr ~ "," ~ exprAs ~ "," ~ relCoordinateExpr ^^ {
      case x ~ _ ~ y ~ _ ~ z => PositionValue(x, y, z)
    }

  /** Parses a relative position case 2 as an expression.
    *
    * @return
    *   A Parser for a relative position case 2 as an expression.
    */
  def relPositionCase2Expr: Parser[PositionValue] =
    relCoordinateExpr ~ "," ~ relCoordinate2Expr ~ "," ~ exprAs ^^ {
      case x ~ _ ~ y ~ _ ~ z => PositionValue(x, y, z)
    }

  /** Parses a front position as an expression.
    *
    * @return
    *   A Parser for a front position as an expression.
    */
  def frontPositionExpr: Parser[PositionValue] =
    frontCoordinateExpr ~ "," ~ frontCoordinateExpr ~ "," ~ frontCoordinateExpr ^^ {
      case x ~ _ ~ y ~ _ ~ z => PositionValue(x, y, z)
    }

  /** Parses a relative position as an expression.
    *
    * @return
    *   A Parser for a relative position as an expression.
    */
  def relPositionExpr: Parser[PositionValue] =
    relCoordinateExpr ~ "," ~ relCoordinateExpr ~ "," ~ relCoordinateExpr ^^ {
      case x ~ _ ~ y ~ _ ~ z => PositionValue(x, y, z)
    }

  /** Parses a position as an expression.
    *
    * @return
    *   A Parser for a position as an expression.
    */
  def positionExpr: Parser[PositionValue] =
    "{" ~> (frontPositionExpr | relPositionExpr | relPositionCase1Expr | relPositionCase2Expr) <~ "}"

  /** Parses a tag value starting with a '#' symbol, followed by an identifier
    * and an optional colon and identifier. Returns a TagValue object.
    *
    * @return
    *   A Parser that returns a TagValue object.
    */
  def tagValue: Parser[TagValue] = "#" ~> ident2 ~ opt(":" ~ ident2) ^^ {
    case a ~ Some(_ ~ b) => TagValue(a + ":" + b); case a ~ None => TagValue(a)
  }

  /** Parses a constructor call expression, which can take one of the following
    * forms:
    *   - "new" followed by type variables and a list of expressions in
    *     parentheses.
    *   - "new" followed by non-recursive types and a list of expressions in
    *     square brackets.
    *   - "new" followed by an identifier, type variables, a list of expressions
    *     in parentheses, and a block.
    *   - "new" followed by an identifier, type variables, and a list of
    *     expressions in parentheses. Returns a ConstructorCall object.
    *
    * @return
    *   A Parser that returns a ConstructorCall object.
    */
  def constructorCall: Parser[ConstructorCall] =
    "new" ~> typeVariables ~ ("(" ~> repsep(exprNoTuple, ",") <~ ")") ^^ {
      case t ~ a => ConstructorCall("@@@", a, t)
    }
      | "new" ~> nonRecTypes ~ ("[" ~> repsep(exprNoTuple, ",") <~ "]") ^^ {
        case t ~ a => ConstructorCall("standard.array.Array", a, List(t))
      }
      | "new" ~> identLazy2 ~ typeVariables ~ ("(" ~> repsep(
        exprNoTuple,
        ","
      ) <~ ")") ~ block ^^ { case f ~ t ~ a ~ b =>
        ConstructorCall(f, a ::: List(LambdaValue(List(), b, null)), t)
      }
      | "new" ~> identLazy2 ~ typeVariables ~ ("(" ~> repsep(
        exprNoTuple,
        ","
      ) <~ ")") ^^ { case f ~ t ~ a => ConstructorCall(f, a, t) }

  /** Parses a simple expression
    *
    * @return
    *   A Parser that returns an Expression.
    */
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
      | singleVarAssignment ^^ { p =>
        SequenceValue(p, VariableValue(p.name.head._1.left.get, p.name.head._2))
      }
      | singleVarAssignmentPos ^^ { p =>
        SequencePostValue(VariableValue(p.name.head._1.left.get, p.name.head._2), p)
      }
      | tagValue
      | namespacedName
      | "$" ~ stringLit2 ^^ { case _ ~ s => InterpolatedString.build(s) }
      | stringLit2 ^^ (StringValue(_))
      | identLazy2 ~ selector ^^ { case id ~ sel =>
        BinaryOperation("in", SelectorValue(sel), VariableValue(id))
      }
      | constructorCall
      | exprArray
      | identifierExpr
      | "(" ~> expr <~ ")"
      | json ^^ (JsonValue(_))
      | selector ^^ (SelectorValue(_))
  )

  /** A parser for identifier expressions.
    */
  def identifierExpr: Parser[Expression] =
    identLazy2 ~ typeVariables ~ ("(" ~> repsep(
      exprNoTuple,
      ","
    ) <~ ")") ~ block ^^ { case f ~ t ~ a ~ b =>
      FunctionCallValue(
        VariableValue(f),
        a ::: List(LambdaValue(List(), b, null)),
        t
      )
    }
    | identLazy2 ~ typeVariables ~ block ^^ { case f ~ t ~ b =>
      FunctionCallValue(
        VariableValue(f),
        List(LambdaValue(List(), b, null)),
        t
      )
    }
      | identLazy2 ~ rep1(
        (typeVariables <~ "(") ~ repsep(exprNoTuple, ",") <~ ")"
      ) ^^ { case f ~ a =>
        a.foldLeft[Expression](VariableValue(f))((b, a) =>
          FunctionCallValue(b, a._2, a._1)
        )
      }
      | identLazy2 ^^ (VariableValue(_))
      | selector ~ "." ~ identLazy2 ^^ { case s ~ _ ~ n => VariableValue(n, s) }
      | identTag ^^ (VariableValue(_))

  /** A parser for array expressions.
    */
  def exprArray: Parser[Expression] =
    positioned(
      (identifierExpr | "(" ~> expr <~ ")") ~ rep(
        "[" ~> rep1sep(expr, ",") <~ "]"
      ) ^^ { case e ~ g => g.foldLeft(e)((e, i) => ArrayGetValue(e, i)) }
    )

  /** A parser for comparators.
    */
  def comparator: Parser[String] = "<" | "<=" | ">=" | ">" | "==" | "!="

  /** A parser for type variables.
    */
  def typeVariablesForce = "<" ~ repsep(types, ",") ~ ">" ^^ { case _ ~ a ~ _ =>
    a
  }

  /** A parser for optional type variables.
    */
  def typeVariables = opt(typeVariablesForce) ^^ {
    case Some(a) => a; case None => List()
  }

  /** A parser for type arguments.
    */
  def typeArgument = opt("<" ~ repsep(ident, ",") ~ ">") ^^ {
    case Some(_ ~ a ~ _) => a; case None => List()
  }

  /** A parser for type argument expressions.
    */
  def typeArgumentExpression = opt("<" ~ repsep(exprBottom, ",") ~ ">") ^^ {
    case Some(_ ~ a ~ _) => a; case None => List()
  }

  /** A parser for dot expressions.
    */
  def exprDot: Parser[Expression] =
    positioned(rep1sep(exprBottom, ".") ^^ {
      case e if e.size == 1 => e.head;
      case e                => e.tail.foldLeft(e.head)((p, n) => DotValue(p, n))
    })

  /** A parser for ternary operators.
    */
  def ternaryOperator: Parser[Expression] =
    positioned(
      exprDot ~! opt(("?") ~>! exprNoTuple ~! ":" ~! exprNoTuple) ^^ {
        case e1 ~ Some(e2 ~ _ ~ e3) => TernaryOperation(e1, e2, e3);
        case e1 ~ None              => e1
      } |||
        exprDot ~ opt(("?") ~> identifierExpr ~ ":" ~ exprNoTuple) ^^ {
          case e1 ~ Some(e2 ~ _ ~ e3) => TernaryOperation(e1, e2, e3);
          case e1 ~ None              => e1
        } |
        exprDot ~ opt(("if") ~> exprNoTuple ~ ("else") ~ exprNoTuple) ^^ {
          case e1 ~ Some(e2 ~ _ ~ e3) => TernaryOperation(e2, e1, e3);
          case e1 ~ None              => e1
        }
    )

  /** Parses an expression that may contain a range operator (..) with optional
    * step (by).
    * @return
    *   The parsed Expression.
    */
  def exprLeftRange: Parser[Expression] = positioned(
    (".." ~> ternaryOperator ~ opt("by" ~> ternaryOperator)) ^^ {
      case e ~ None                 => RangeValue(IntValue(Int.MinValue), e, IntValue(1));
      case e1 ~ Some(e2)     => RangeValue(IntValue(Int.MinValue), e1, e2);
    }
    | ternaryOperator
  )

  /** Parses an expression that may contain a range operator (..) with optional
    * step (by).
    * @return
    *   The parsed Expression.
    */
  def exprRange: Parser[Expression] = positioned(
    exprLeftRange ~ opt(
      ".." ~> opt(ternaryOperator) ~ opt("by" ~> ternaryOperator)
    ) ^^ {
      case e ~ None                 => e;
      case e1 ~ Some(Some(e2) ~ None)     => RangeValue(e1, e2, IntValue(1));
      case e1 ~ Some(None ~ None)     => RangeValue(e1, IntValue(Int.MaxValue), IntValue(1));
      case e1 ~ Some(Some(e2) ~ Some(e3)) => RangeValue(e1, e2, e3)
      case e1 ~ Some(None ~ Some(e3)) => RangeValue(e1, IntValue(Int.MaxValue), e3)
    }
  )

  /** Parses an expression that may contain exponentiation operator (^).
    * @return
    *   The parsed Expression.
    */
  def exprPow: Parser[Expression] = positioned(
    exprRange ~ rep("^" ~> exprPow) ^^ { unpack("^", _) }
  )

  /** Parses an expression that may contain modulo operator (%).
    * @return
    *   The parsed Expression.
    */
  def exprMod: Parser[Expression] = positioned(
    exprPow ~ rep("%" ~> exprMod) ^^ { unpack("%", _) }
  )

  /** Parses an expression that may contain division (/) or integer division
    * (\).
    * @return
    *   The parsed Expression.
    */
  def exprDiv: Parser[Expression] = positioned(
    exprMod ~ rep(("/" | "\\") ~> exprDiv) ^^ { unpack("/", _) }
  )

  /** Parses an expression that may contain multiplication (*).
    * @return
    *   The parsed Expression.
    */
  def exprMult: Parser[Expression] = positioned(
    exprDiv ~ rep("*" ~> exprMult) ^^ { unpack("*", _) }
  )

  /** Parses an expression that may contain subtraction (-).
    * @return
    *   The parsed Expression.
    */
  def exprSub: Parser[Expression] = positioned(
    exprMult ~ rep("-" ~> exprSub) ^^ { unpack("-", _) }
  )

  /** Parses an expression that may contain addition (+).
    * @return
    *   The parsed Expression.
    */
  def exprAdd: Parser[Expression] = positioned(
    exprSub ~ rep("+" ~> exprAdd) ^^ { unpack("+", _) }
  )

  /** Parses an expression that may contain comparison operators (==, !=, <, <=,
    * >, >=).
    * @return
    *   The parsed Expression.
    */
  def exprComp: Parser[Expression] = positioned(
    exprAdd ~ rep(comparator ~ exprComp) ^^ { unpack(_) }
  )

  /** Parses an expression that may contain list concatenation operator (>:).
    * @return
    *   The parsed Expression.
    */
  def exprAppend: Parser[Expression] = positioned(
    exprComp ~ rep(">:" ~> exprAppend) ^^ { unpack(">:", _) }
  )

  /** Parses an expression that may contain list prepending operator (<:).
    * @return
    *   The parsed Expression.
    */
  def exprPrepend: Parser[Expression] = positioned(
    exprAppend ~ rep("<:" ~> exprPrepend) ^^ { unpack("<:", _) }
  )

  /** Parses an expression that may contain list concatenation operator (::).
    * @return
    *   The parsed Expression.
    */
  def exprMerge: Parser[Expression] = positioned(
    exprPrepend ~ rep("::" ~> exprMerge) ^^ { unpack("::", _) }
  )

  /** Parses an expression that may contain "not in" operator.
    * @return
    *   The parsed Expression.
    */
  def exprNotIn: Parser[Expression] = positioned(
    exprMerge ~ rep(("not" | "!") ~> "in" ~> exprNotIn) ^^ {
      unpack("not in", _)
    }
  )

  /** Parses an expression that may contain "in" operator.
    * @return
    *   The parsed Expression.
    */
  def exprIn: Parser[Expression] = positioned(
    exprNotIn ~ rep("in" ~> exprIn) ^^ { unpack("in", _) }
  )

  /** Parses an expression that may contain "is" operator.
    * @return
    *   The parsed Expression.
    */
  def exprIs: Parser[Expression] = positioned((exprIn ~ opt("is" ~ types)) ^^ {
    case e ~ Some(_ ~ t) => IsType(e, t); case e ~ None => e
  })

  /** Parses an expression that may contain "is not" operator.
    * @return
    *   The parsed Expression.
    */
  def exprIsNot: Parser[Expression] = positioned(
    (exprIs ~ opt("is" ~ "not" ~ types)) ^^ {
      case e ~ Some(_ ~ t) => UnaryOperation("!", IsType(e, t));
      case e ~ None        => e
    }
  )

  /** Parses an expression that may contain right shift operator (>>).
    * @return
    *   The parsed Expression.
    */
  def exprShiftRight: Parser[Expression] = positioned(
    exprIsNot ~ rep(">" ~> ">" ~> exprShiftRight) ^^ { unpack(">>", _) }
  )

  /** Parses an expression that may contain left shift operator (<<).
    * @return
    *   The parsed Expression.
    */
  def exprShiftLeft: Parser[Expression] = positioned(
    exprShiftRight ~ rep("<" ~> "<" ~> exprShiftLeft) ^^ { unpack("<<", _) }
  )

  /** Parses an expression that may contain bitwise and operator (&).
    * @return
    *   The parsed Expression.
    */
  def exprBitwiseAnd: Parser[Expression] = positioned(
    exprShiftLeft ~ rep("&" ~> exprBitwiseAnd) ^^ { unpack("&", _) }
  )

  /** Parses an expression that may contain bitwise or operator (|).
    * @return
    *   The parsed Expression.
    */
  def exprBitwiseOr: Parser[Expression] = positioned(
    exprBitwiseAnd ~ rep("|" ~> exprBitwiseOr) ^^ { unpack("|", _) }
  )

  /** Parses an expression that may contain logical and operator (&&).
    * @return
    *   The parsed Expression.
    */
  def exprAnd: Parser[Expression] = positioned(
    exprBitwiseOr ~ rep("&&" ~> exprAnd) ^^ { unpack("&&", _) }
  )

  /** Parses an expression that may contain logical or operator (||).
    * @return
    *   The parsed Expression.
    */
  def exprOr: Parser[Expression] = positioned(exprAnd ~ rep("||" ~> exprOr) ^^ {
    unpack("||", _)
  })

  /** Parses an expression that may contain null coalescing operator (??).
    * @return
    *   The parsed Expression.
    */
  def exprNullCoalesing: Parser[Expression] = positioned(
    exprOr ~ rep("??" ~> exprNullCoalesing) ^^ { unpack("??", _) }
  )

  /** Parses an expression that may contain type casting operator (as).
    * @return
    *   The parsed Expression.
    */
  def exprAs: Parser[Expression] = positioned(
    exprNullCoalesing ~ rep(":>" ~> types) ^^ { unpackCast(_) }
  )

  /** Parses an expression that may contain a for loop with a select statement.
    * @return
    *   The parsed Expression.
    */
  def exprForSelect: Parser[Expression] = positioned(
    exprAs ~ rep("for" ~> (ident2 | stringLit) ~ "in" ~ exprNoTuple) ^^ {
      unpackForSelect(_)
    }
  )

  /** Parses an expression that may not contain a tuple.
    * @return
    *   The parsed Expression.
    */
  def exprNoTuple = lambda | position | exprForSelect | positionExpr

  /** Parses an expression.
    * @return
    *   The parsed Expression.
    */
  def expr: Parser[Expression] = positioned(
    rep1sep(exprNoTuple, ",") ^^ (p =>
      if p.length == 1 then p.head else TupleValue(p)
    )
  )

  def unpackCast(p: (Expression ~ List[Type])): Expression = {
    if p._2.isEmpty then p._1 else CastValue(p._1, p._2.last)
  }

  def unpackForSelect(
      p: (Expression ~ List[String ~ String ~ Expression])
  ): Expression = {
    if p._2.isEmpty then p._1
    else p._2.foldLeft(p._1)((e, p) => ForSelect(e, p._1._1, p._2))
  }

  def unpack(op: String, p: (Expression ~ List[Expression])): Expression = {
    if p._2.isEmpty then p._1
    else p._2.foldLeft(p._1)(BinaryOperation(op, _, _))
  }

  def unpack(p: (Expression ~ List[String ~ Expression])): Expression = {
    if p._2.isEmpty then p._1
    else p._2.foldLeft(p._1)((e, p) => BinaryOperation(p._1, e, p._2))
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

  def nonRecTypes: Parser[Type] =
    ident2 ~ typeVariablesForce ^^ { case n ~ t => IdentifierType(n, t) } |
      ident2 ^^ { identifierType(_) } |
      (("(" ~> types) ~ rep("," ~> types)) <~ ")" ^^ (p =>
        if p._2.length > 0 then TupleType(p._1 :: p._2) else p._1
      )

  def types: Parser[Type] =
    ("(" ~> repsep(nonRecTypes, ",") <~ ")" <~ "=>") ~ types ^^ (p =>
      FuncType(p._1, p._2)
    ) |
      (nonRecTypes <~ "=>") ~ types ^^ (p => FuncType(List(p._1), p._2)) |
      ((nonRecTypes <~ "[") ~ expr) <~ "]" ^^ (p => ArrayType(p._1, p._2)) |
      ((nonRecTypes <~ "[" ~ "]")) ^^ (p => ArrayType(p, null)) |
      nonRecTypes

  def modifierSub(sel: String): Parser[String] = {
    sel match {
      case "all" => (
        "abstract" | "override" | "virtual" | "lazy" | "macro" | "scoreboard" | "ticking" | "loading" | "helper" | "static" | "const" | "async"
      )
      case "function" => (
        "abstract" | "override" | "virtual" | "lazy" | "macro" | "ticking" | "loading" | "helper" | "static" | "const" | "async"
      )
      case "function_short" => (
        "lazy" | "macro" | "ticking" | "loading" | "helper" | "static" | "const" | "async"
      )
      case "variable" => ("lazy" | "macro" | "scoreboard" | "static" | "const")
      case "class"    => ("abstract" | "static")
      case "struct"   => ("abstract" | "static")
      case "enum"     => ("abstract" | "static")
      case _          => ("static")
    }
  }

  def modifierAttribute: Parser[(String, Expression)] =
    ident2 ~ "=" ~ exprNoTuple ^^ { case i ~ _ ~ e => (i, e) }
  def modifierAttributes: Parser[Map[String, Expression]] =
    opt("[" ~> rep1sep(modifierAttribute, ",") <~ "]") ^^ {
      (_.getOrElse(List()).toMap)
    }
  def modifier(sel: String): Parser[Modifier] =
    (sel match {
      case "abstract_function" => (
        opt(stringLit2 ~ stringLit2 ~ stringLit2) ~ modifierAttributes ~ opt(
          "public" | "private" | "protected"
        ) ~ ("abstract" ~> rep(modifierSub("function"))) ~ rep(identTag)
      )
      case other => (
        opt(stringLit2 ~ stringLit2 ~ stringLit2) ~ modifierAttributes ~ opt(
          "public" | "private" | "protected"
        ) ~ rep(modifierSub(sel)) ~ rep(identTag)
      )
    }) ^^ {
      case doc ~ attributes ~ protection ~ subs ~ tags => {
        val mod = new Modifier()
        mod.tags.addAll(tags)
        mod.attributes = attributes
        protection match {
          case Some("public")    => mod.protection = Protection.Public
          case Some("private")   => mod.protection = Protection.Private
          case Some("protected") => mod.protection = Protection.Protected
          case _                 => {}
        }

        if subs.contains("virtual") || sel == "abstract_function" then {
          mod.isVirtual = true
        }
        if subs.contains("abstract") || sel == "abstract_function" then {
          mod.isAbstract = true
        }
        if subs.contains("override") then { mod.isOverride = true }
        if subs.contains("lazy") then { mod.isLazy = true }
        if subs.contains("macro") then { mod.isMacro = true }
        if subs.contains("scoreboard") then { mod.isEntity = true }
        if subs.contains("ticking") then { mod.isTicking = true }
        if subs.contains("loading") then { mod.isLoading = true }
        if subs.contains("helper") then { mod.isHelper = true }
        if subs.contains("static") then { mod.isStatic = true }
        if subs.contains("const") then { mod.isConst = true }
        if subs.contains("async") then { mod.isAsync = true }

        mod
      }
    }

  def doc: Parser[Option[String]] = opt("???" ~> stringLit <~ "???")

  def program: Parser[Instruction] = positioned(
    rep(instruction <~ opt(";")) ^^ (InstructionList(_))
  )

  /** Print an error message, together with the position where it occured. */
  case class TypeError(t: Instruction, msg: String) extends Exception(msg) {
    override def toString =
      msg + "\n" + t
  }

  def parseFromFile(f: String, get: () => String): Instruction = {
    if (CacheAST.contains(f)) {
      CacheAST.get(f)
    } else {
      val ast = parse(f, get())
      CacheAST.add(f, ast.get)
      ast.get
    }
  }
  def parse(file: String, args: String): Option[Instruction] = {
    val tokens = new lexical.Scanner(Preparser.parse(file, args))
    phrase(program)(tokens) match {
      case Success(trees, _) =>
        if (Settings.consoleInfoParsed){Reporter.ok(f"Parsed: $file")}
        Some(StaticAnalyser.handleSleep(StaticAnalyser.check(trees)))
      case e =>
        Reporter.error(f"Error in file '${file}'': ${e}")
        None
    }
  }
  def parse(string: String): Instruction = {
    val tokens = new lexical.Scanner(Preparser.parse("", string))
    phrase(program)(tokens) match {
      case Success(trees, _) =>
        StaticAnalyser.check(trees)
      case e =>
        Reporter.error(f"Error in file '${string}'': ${e}")
        null
    }
  }
  def parseJson(file: String): JSONElement = {
    val preparsed = Preparser.parse("", file)
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
        if (!silent && Settings.consoleInfoParsed) {
          Reporter.ok(f"Parsed: $file")
        }
        trees
      case e =>
        println(f"Error in file '${file}'': ${e}")
        null
    }
  }
}
