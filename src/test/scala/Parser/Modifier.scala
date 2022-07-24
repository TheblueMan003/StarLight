package test.parser

import lexer.Lexer
import parser.TypeParser
import objects.Context
import parser.ModifierParser
import objects.Protection

class ModifierParsingTest extends munit.FunSuite {
  test("public") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(" public "), Context("root"))
    assertEquals(obtained.protection, Protection.Public)
  }
  test("private") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(" private "), Context("root"))
    assertEquals(obtained.protection, Protection.Private)
  }
  test("none") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(""), Context("root"))
    assertEquals(obtained, null)
  }
  test("private override") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(" private override "), Context("root"))
    assertEquals(obtained.protection, Protection.Private)
    assertEquals(obtained.isOverride, true)
  }
  test("lazy") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(" lazy"), Context("root"))
    assertEquals(obtained.isLazy, true)
  }
  test("inline") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(" inline"), Context("root"))
    assertEquals(obtained.isInline, true)
  }
  test("entity") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(" entity"), Context("root"))
    assertEquals(obtained.isEntity, true)
  }
  test("tags") {
    val obtained = ModifierParser.parse()(Lexer.tokenize(" @test"), Context("root"))
    assertEquals(obtained.tags.head, "@test")
  }
}
