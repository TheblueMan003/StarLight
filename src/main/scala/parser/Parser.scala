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
    def parse(text: TokenBufferedIterator)(implicit context: Context):Instruction = {
        if (text.peekNoSpace() == IdentifierToken("namespace")){
            text.takeNoSpace()
            text.takeNoSpace() match{
                case IdentifierToken(name) => parse(text)(context.root.push(name))
                case other => throw UnexpectedTokenException(other, "IdentifierToken")
            }
        }
        ???
    }

    def parseIntruction(text: TokenBufferedIterator)(implicit context: Context):Instruction = {
        val modifier = ModifierParser.parse(text)
        val styp = TypeParser.tryParse(text)
        styp match{
            case Some(typ) => parseObject(text, typ, modifier)
            case other => parseIdentifierInstruction(text)
        }
    }

    def parseObject(text: TokenBufferedIterator, typ: Type, modifier: Modifier)(implicit context: Context):Instruction = {
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