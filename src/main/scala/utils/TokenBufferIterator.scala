package utils
import lexer.tokens.*


class TokenBufferedIterator(string: List[Token]){
    var itIndex = 0
    var start = -1

    /**
      * Returns the next token in the buffer and consumes it.
      *
      * @return Token
      */
    def take():Token = {
        val c = string(itIndex)
        itIndex += 1
        c
    }

    /**
      * Returns the next token in the buffer and consumes it. Ignore Spaces
      *
      * @return Token
      */
    def takeNoSpace():Token = {
        var c = string(itIndex)
        itIndex += 1
        c match{
            case SpaceToken => takeNoSpace()
            case CommentToken(_) => takeNoSpace()
            case _ => c
        }
    }

    /**
      * Consumes the next tokens in the buffer while the predicate is true.
      *
      * @param predicate: (Token) => Boolean The predicate to test the tokens against.
      */
    def takeWhile(predicate: Token=>Boolean) = {
        while (hasNext() && predicate(peek())){
            take()
        }
    }

    /**
      * @return the next token in the buffer without consuming it.
      */
    def peek():Token ={
        string(itIndex)
    }

    /**
      * @return the next token in the buffer without consuming it. Ignore Spaces
      */
    def peekNoSpace(acc: Int = 0):Token ={
        if (itIndex + acc < string.length){
            val c = string(itIndex + acc)
            c match{
                case SpaceToken => peekNoSpace(acc+1)
                case CommentToken(_) => peekNoSpace(acc+1)
                case _ => c
            }
        }
        else{
            EOFToken
        }
    }

    /**
      * Set the start of the cut
      */
    def setStart() = {
        start = itIndex
    }

    /**
      * @return list of tokens from the start of the cut to the current position
      */
    def cut(): List[Token] = {
        string.slice(start, itIndex)
    }

    /**
     * @return true if there are more tokens in the buffer
     *        false otherwise
     */
    def hasNext(): Boolean = {
        itIndex < string.length
    }

    /**
     * force the next token to be equal to token
     */
    def requierToken(token: Token) = {
        if (!hasNext()){
            throw new UnexpectedEOFException()
        }
        else if (peek() != token){
            throw new UnexpectedTokenException(peek(), token)
        }
        else{
            take()
        }
    }

    /**
     * force the next token to be equal to token
     */
    def requierTokenNoSpace(token: Token) = {
        if (!hasNext()){
            throw new UnexpectedEOFException()
        }
        else if (peekNoSpace() != token){
            throw new UnexpectedTokenException(peekNoSpace(), token)
        }
        else{
            takeNoSpace()
        }
    }

    /**
     * Save the state of the iterator. Used for backtracking
     */ 
    def saveState(): TBIState = {
        TBIState(start, itIndex)
    }

    /**
     * Go back to previous state
     */
    def backtrack(state: TBIState) = {
        itIndex = state.itIndex
        start = state.start
    }
}

case class TBIState(start: Int, itIndex: Int)
case class UnexpectedTokenException(val actual: Token, val expected: Object) extends Exception(f"found: ${actual.positionString()} ${actual} expected: ${expected}")
case class UnexpectedEOFException() extends Exception