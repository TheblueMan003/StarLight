package parser

import objects.Context
import utils.TokenBufferedIterator
import objects.types.*
import lexer.tokens.*

import scala.collection.mutable.ArrayBuffer
import utils.UnexpectedTokenException
import objects.Modifier
import objects.Variable

object Parser{
    def parse()(implicit text: TokenBufferedIterator, context: Context):Instruction = {
        if (text.peekNoSpace() == IdentifierToken("namespace")){
            text.takeNoSpace()
            text.takeNoSpace() match{
                case IdentifierToken(name) => parse()(text, context.root.push(name))
                case other => throw UnexpectedTokenException(other, "IdentifierToken")
            }
        }
        else{
            parseIntruction()
        }
    }

    def parseIntruction()(implicit text: TokenBufferedIterator, context: Context):Instruction = {
        val modifier = ModifierParser.parse()
        val styp = TypeParser.tryParse()
        styp match{
            case Some(typ) => parseObject(typ, modifier)
            case other => parseStandardInstruction()
        }
    }

    def parseObject(typ: Type, modifier: Modifier)(implicit text: TokenBufferedIterator, context: Context):Instruction = {
        text.takeNoSpace() match{
            case DelimiterToken("{") => ???
            case DelimiterToken("(") => ???
            case name: IdentifierToken => {
                text.peekNoSpace() match{
                    case DelimiterToken("(") => ???
                    case other => {
                        val names = ArrayBuffer[String]()
                        names.addOne(name.value)
                        // Get All Name
                        while(text.peekNoSpace() == DelimiterToken(",")){
                            text.takeNoSpace()
                            val token = text.takeNoSpace() match{
                                case IdentifierToken(name1) => names.addOne(name1)
                                case other => throw UnexpectedTokenException(other, "IdentifierToken")
                            }
                        }
                        // create variables
                        val varis = names.map(x => context.addVariable(Variable(context, x, typ, modifier)))
                        // manage instruction
                        text.peekNoSpace() match{
                            case OperatorToken(op) => {
                                text.takeNoSpace()
                                val expr = ExpressionParser.parse()
                                InstructionList(varis.map(VarOperationInstruction(op, _, expr)).toList)
                            }
                            case _ => EmptyInstruction
                        }
                    }
                }
            }
        }
    }

    def parseStandardInstruction()(implicit text: TokenBufferedIterator, context: Context):Instruction = {
        text.peekNoSpace() match{
            case OperatorToken("/") => parseVanillaCommand()
            case IdentifierToken(name) => parseIdentifierInstruction(name)
        }
    }

    def parseVanillaCommand()(implicit text: TokenBufferedIterator, context: Context):Instruction = {
        text.take()
        text.setStart()
        text.takeWhile(x => x != ReturnToken)
        VanillaCommand(text.cut())
    }
    def parseIdentifierInstruction(name: String)(implicit text: TokenBufferedIterator, context: Context):Instruction = {
        name match{
            case "jsonfile" => ???
            case "blocktag" => ???
            case "entitytag" => ???
            case "itemtag" => ???
        }
    }
    
}