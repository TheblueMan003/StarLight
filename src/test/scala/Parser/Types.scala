package test.parser

import lexer.Lexer
import parser.TypeParser
import objects.Context
import objects.types.IntType
import objects.types.FloatType
import objects.types.TuppleType
import objects.types.ArrayType
import objects.types.FuncType
import objects.types.VoidType

class TypesParsingTest extends munit.FunSuite {
  test("int") {
    val obtained = TypeParser.parse(Lexer.tokenize(" int "))(Context("root"))
    val expected = IntType
    assertEquals(obtained, expected)
  }
  test("float") {
    val obtained = TypeParser.parse(Lexer.tokenize("float"))(Context("root"))
    val expected = FloatType
    assertEquals(obtained, expected)
  }

  test("tupple") {
    val obtained = TypeParser.parse(Lexer.tokenize("(int,int)"))(Context("root"))
    val expected = TuppleType(List(IntType, IntType))
    assertEquals(obtained, expected)
  }

  test("array") {
    val obtained = TypeParser.parse(Lexer.tokenize("int [ 10 ]"))(Context("root"))
    val expected = ArrayType(IntType, 10)
    assertEquals(obtained, expected)
  }

  test("function int=>int") {
    val obtained = TypeParser.parse(Lexer.tokenize("int=>int"))(Context("root"))
    val expected = FuncType(List(IntType), IntType)
    assertEquals(obtained, expected)
  }

  test("function void=>int") {
    val obtained = TypeParser.parse(Lexer.tokenize("void=>void"))(Context("root"))
    val expected = FuncType(List(), VoidType)
    assertEquals(obtained, expected)
  }

  test("function (int,int)=>int") {
    val obtained = TypeParser.parse(Lexer.tokenize("(int,int)=>int"))(Context("root"))
    val expected = FuncType(List(IntType,IntType), IntType)
    assertEquals(obtained, expected)
  }

  test("function in tuple (int=>int, int)") {
    val obtained = TypeParser.parse(Lexer.tokenize("(int=>int, int)"))(Context("root"))
    val expected = TuppleType(List(FuncType(List(IntType), IntType), IntType))
    assertEquals(obtained, expected)
  }
  test("array of functions (int=>int)[10]") {
    val obtained = TypeParser.parse(Lexer.tokenize("(int=>int)[10]"))(Context("root"))
    val expected = ArrayType(FuncType(List(IntType), IntType), 10)
    assertEquals(obtained, expected)
  }
}
