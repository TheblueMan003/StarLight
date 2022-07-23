package program

import utils.CharBufferIterator
import lexer.Lexer
import utils.FileLoader

class Build(files: List[String]) {
  def build():Unit = {
    files.map(FileLoader.load(_))
         .map(Lexer.tokenize(_))
         .map(println(_))
  }
}
