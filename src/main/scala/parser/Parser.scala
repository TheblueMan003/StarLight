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
            case other => parseIdentifierInstruction(text)
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
                        context.addVariable(Variable(context, name.value, typ, modifier))
                        EmptyInstruction
                    }
                }
            }
        }
    }

    def parseIdentifierInstruction(text: TokenBufferedIterator)(implicit context: Context):Instruction = {
        ???
    }
    
}