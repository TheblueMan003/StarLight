package parser

import objects.Context
import utils.TokenBufferedIterator
import objects.types.*
import lexer.tokens.*

import scala.collection.mutable.ArrayBuffer
import utils.UnexpectedTokenException
import objects.types.FuncType

object TypeParser{
    /**
     * Try to parse a type. If succed return Some[Type] otherwise None
     */
    def tryParse(text: TokenBufferedIterator)(implicit context: Context):Option[Type] = {
        val state = text.saveState()
        try{
            Some(parse(text))
        }
        catch{
            case e: Exception => {
                text.backtrack(state)
                None
            }
        }
    }

    /**
     * Parse a type and return a type value
     */
    def parse(text: TokenBufferedIterator)(implicit context: Context): Type = {
        /** 
         * Parse a basic Type
         */ 
        def singleType(token: Token): Type = {
            token match{
                case IdentifierToken("int") => IntType
                case IdentifierToken("float") => FloatType
                case IdentifierToken("bool") => BoolType
                case IdentifierToken("void") => VoidType
            }
        }

        /**
         * Parse a list of Type
         */ 
        def listType(): List[Type] = {
            val lst = ArrayBuffer[Type]()
            lst.addOne(parse(text))
            while(text.peekNoSpace() == DelimiterToken(",")){
                text.takeNoSpace()
                lst.addOne(parse(text))
            }
            text.requierTokenNoSpace(DelimiterToken(")"))
            lst.toList
        }

        /**
         * Parse Function & Array
         */
        def composed(typ: Type):Type = {
            if (text.peekNoSpace() == DelimiterToken("[")){
                text.takeNoSpace()

                // Get Size
                val size = text.takeNoSpace()
                val sizeNb = size match{
                    case IntToken(nb) => nb
                    case _ => throw new UnexpectedTokenException(size, "IntToken")
                }

                text.requierTokenNoSpace(DelimiterToken("]"))

                composed(ArrayType(typ, sizeNb))
            }
            else if (text.peekNoSpace() == OperatorToken("=>")){
                text.takeNoSpace()
                typ match{
                    case TuppleType(sub) => composed(FuncType(sub, parse(text)))
                    case VoidType => composed(FuncType(List(), parse(text)))
                    case _ => FuncType(List(typ), parse(text))
                }
            }
            else{
                typ
            }
        }

        /**
         * Pops singleton
         */
        def simplify(typ: Type):Type={
            typ match{
                case TuppleType(List(a)) => a
                case a => a
            }
        }


        val token = text.takeNoSpace()
        var ret = token match{
            case DelimiterToken("(") => TuppleType(listType())
            case _ => singleType(token)
        }

        ret = simplify(composed(simplify(ret)))

        ret
    }
}