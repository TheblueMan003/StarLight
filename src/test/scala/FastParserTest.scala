import munit.FunSuite

class FastParserTest extends munit.FunSuite {
    test("Variable Declaration") {
        val input = "var x = 10"
        val expected = "var? x = 10"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Function Declaration") {
        val input = "def f(){var x = 10}"
        val expected = "def f() {\nvar? x = 10\n}"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Import Declaration") {
        val input = "import test"
        val expected = "from test import null as null"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Package Declaration") {
        val input = "package test import test"
        sl.FastParser.parse(input) match {
            case sl.InstructionList(List(sl.Package(p, b))) =>{
                assertEquals(p, "test")
                assertEquals(b.toString, "from test import null as null")
            }
            case other => fail("Expected Package declaration" + other.getClass())
        }
    }

    test("CMD Declaration") {
        val input = "./say hi"
        val expected = "/say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Assigment Declaration") {
        val input = "a = b - 5"

        val expected = "a = (b - 5)"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("If Declaration") {
        val input = "if (b == -5){}else if (c <= 5){}else{}"

        val expected = "if ((b == -5)){\n\n}\nelse if ((c <= 5)){\n\n}\nelse if (true){\n\n}"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Return Declaration") {
        val input = "return 5"

        val expected = "return(5)"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Ternary Ops") {
        val input = "a = (a < b) ? ((b < c) ? c : b) : a"

        val expected = "a = ((a < b) ? ((b < c) ? c : b) : a)"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Tuple declaration") {
        val input = "((int, int), (float, float)) tuple = ((1, 2), (3.14, 2.718))"

        val expected = "((int,int),(float,float)) tuple = ((1, 2), (3.14, 2.718))"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("Multiline") {
        val input = """int a = b
                       int b
                    """

        val expected = "int a = b\nint b := default"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("comment") {
        val input = "//test"

        val expected = ""
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("at") {
        val input = "at(2 2 2)./say hi"

        val expected = "at(List(2 2 2)) /say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("as") {
        val input = "as(@p)./say hi"

        val expected = "with(@p, false, true) /say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("facing") {
        val input = "facing(@p)./say hi"

        val expected = "facing(List(@p)) /say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("rotated") {
        val input = "rotated(@p)./say hi"

        val expected = "rotated(List(@p)) /say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("rotated") {
        val input = "rotated(0, 0)./say hi"

        val expected = "rotated(List(0, 0)) /say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("align") {
        val input = "align(\"xyz\")./say hi"

        val expected = "align(List(xyz)) /say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }

    test("facing") {
        val input = "facing(@p)./say hi"

        val expected = "facing(List(@p)) /say hi"
        assertEquals(sl.FastParser.parse(input).toString, expected)
    }
}