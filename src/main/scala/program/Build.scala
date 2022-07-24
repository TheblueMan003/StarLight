package program

import utils.CharBufferIterator
import lexer.Lexer
import utils.FileLoader
import parser.Parser
import objects.Context

class Build(files: List[String]) {
  def build():Unit = {
    val context = Context.getNew("main")
    files.map(FileLoader.load(_))
         .map(Lexer.tokenize(_))
         .map(Parser.parse()(_, context))
         .map(println(_))
  }
}
