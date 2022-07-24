package test.parser

import lexer.Lexer
import parser.TypeParser
import objects.Context
import objects.Protection
import parser.ExpressionParser
import parser.BinarayExpr

class ExpressionParsingTest extends munit.FunSuite {
    test("1") {
        val obtained = ExpressionParser.parse()(Lexer.tokenize("1"), Context("root"))
        assertEquals(obtained, parser.IntValue(1))
    }
    test("1 + 2") {
        val obtained = ExpressionParser.parse()(Lexer.tokenize("1 + 2"), Context("root"))
        assertEquals(obtained, BinarayExpr("+", parser.IntValue(1), parser.IntValue(2)))
    }
    test("1 + 2 * 3") {
        val obtained = ExpressionParser.parse()(Lexer.tokenize("1 + 2 * 3"), Context("root"))
        assertEquals(obtained, BinarayExpr("+", parser.IntValue(1), BinarayExpr("*", parser.IntValue(2), parser.IntValue(3))))
    }
    test("(1 + 2) * 3") {
        val obtained = ExpressionParser.parse()(Lexer.tokenize("(1 + 2) * 3"), Context("root"))
        assertEquals(obtained, BinarayExpr("*", BinarayExpr("+", parser.IntValue(1), parser.IntValue(2)), parser.IntValue(3)))
    }

    test("1.0") {
        val obtained = ExpressionParser.parse()(Lexer.tokenize("1.0"), Context("root"))
        assertEquals(obtained, parser.FloatValue(1))
    }

    test("false") {
        val obtained = ExpressionParser.parse()(Lexer.tokenize("false"), Context("root"))
        assertEquals(obtained, parser.BooleanValue(false))
    }

    test("text") {
        val obtained = ExpressionParser.parse()(Lexer.tokenize("\"hello\""), Context("root"))
        assertEquals(obtained, parser.StringValue("hello"))
    }
}