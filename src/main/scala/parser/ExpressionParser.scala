package parser

import lexer.tokens.*
import utils.TokenBufferedIterator
import objects.Context
import utils.UnexpectedTokenException

object ExpressionParser {
    private var operatorsPriority = List(List("^"), List("*", "/", "%"), List("+", "-"), List("<", ">", "<=", ">="), List("==", "!="), List("&&","||")).reverse

    /**
     * Parse a function call or a variable 
     */
    private def parseSimple()(implicit text: TokenBufferedIterator, context: Context): Expression = {
        val token = text.takeNoSpace()
        val tree = token match{
            case IdentifierToken(iden) => {
               ???
            }

            case DelimiterToken("(") => {
                val expr = parse()
                text.requierToken(DelimiterToken(")"))
                expr
            }

            // Literals
            case IntToken(value)    => IntValue(value)
            case BooleanToken(value)=> BooleanValue(value)
            case StringToken(value) => StringValue(value)
            case FloatToken(value)  => FloatValue(value)
            
            // Unary operators
            case OperatorToken("-") => {
                val expr = parse()
                BinarayExpr("-", IntValue(0), expr)
            }
            // Unary operators
            case OperatorToken("!") => {
                Not(parse())
            }

            case other => throw new UnexpectedTokenException(other, "Any Expression Token")
        }
        tree.setPosition(token)
        tree
    }
    
    /**
     * Parse an expression
     */ 
    def parse(opIndex: Int = 0)(implicit text: TokenBufferedIterator, context: Context): Expression = {
        def rec(): Expression = {
            if (opIndex + 1 == operatorsPriority.size){
                parseSimple()
            }
            else{
                parse(opIndex + 1)
            }
        }

        // Get Left Part of potential binary expression
        val token = text.peekNoSpace()
        var left = rec()
        left.setPosition(token)

        // Get Operator according to priority
        val ops = operatorsPriority(opIndex)
        
        var op = text.getOperator(ops)
        while(op.nonEmpty){
            left = BinarayExpr(op.get, left, rec())
            left.setPosition(token)
            op = text.getOperator(ops)
        }
        left
    }
}
