package lexer

import utils.{Reporter, CharBufferIterator, SFile, TokenBufferedIterator}
import lexer.tokens.*
import scala.collection.mutable.ArrayBuffer

object Lexer{
    val delimiter = Set('[', ']', '(', ')', '{', '}', ',')
    val operators = Set('+', '-', '*', '/', '<', '>', '=', '?', ':', '!', '^', '#', '&', '|')
    val identifier = ".$_@".toCharArray
    
    def tokenize(string: String): TokenBufferedIterator = {
        tokenize(SFile("", string))
    }
    /**
     * Tokenize a file
     */ 
    def tokenize(file: SFile): TokenBufferedIterator = {
        val text = CharBufferIterator(file.content)
        val tokens = ArrayBuffer[Token]()

        while (text.hasNext()){
            val index = text.lineIndex
            val line = text.line
            
            var token = tokenizeOne(text)
            token.setPosition(index, line, file.name)

            tokens.addOne(token)
        }
        TokenBufferedIterator(tokens.toList)
    }
    
    private def tokenizeOne(text: CharBufferIterator):Token = {
        text.setStart()
        val c: Char = text.take()
            
        // Number
        if (c.isDigit){
            text.takeWhile(x => x.isDigit)
            if (text.hasNext() && text.peek() == '.'){
                text.take()
                text.takeWhile(x => x.isDigit)
                FloatToken(text.cut().toFloat)
            }
            else{
                IntToken(text.cut().toInt)
            }
        }
        // String
        else if (c == '"'){
            text.takeWhile(x => x != '"')
            text.take()
            val cut = text.cut()
            StringToken(cut.substring(1, cut.length()-1))
        }
        // Delimiter
        else if (delimiter.contains(c)){
            DelimiterToken(text.cut())
        }
        else if (operators.contains(c)){
            text.takeWhile(x => operators.contains(x))
            OperatorToken(text.cut())
        }
        // Identifier or Keyword
        else if (c.isLetter || identifier.contains(c)){
            text.takeWhile(x => x.isLetterOrDigit || identifier.contains(x))
            val cut = text.cut()
            if (cut == "true" || cut == "false"){
                BooleanToken(cut == "true")
            }
            else if (!cut.toIntOption.isEmpty){
                IntToken(cut.toInt)
            }
            else if (!cut.toFloatOption.isEmpty){
                FloatToken(cut.toFloat)
            }
            else{
                IdentifierToken(cut)
            }
        }
        // Spaces Chars
        else if (c.isSpaceChar || c == '\n'){
            text.takeWhile(x => x.isSpaceChar || x == '\n')
            val cut = text.cut()
            SpaceToken
        }
        else{
            val cut = text.cut()
            Reporter.error(f"Unknown Token ${cut}")
            ErrorToken(cut)
        }
    }
}