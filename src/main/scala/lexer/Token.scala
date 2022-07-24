package lexer.tokens

import lexer.Positionable

abstract class Token extends Positionable{

}

case object EOFToken extends Token{
    override def toString(): String = f"EOF"
}
case object ReturnToken extends Token{
    override def toString(): String = f"\\n"
}
case object SpaceToken extends Token{
    override def toString(): String = f"Space"
}
case class DelimiterToken(value: String) extends Token{
    override def toString(): String = f"Delimiter('$value')"
}
case class OperatorToken(value: String) extends Token{
    override def toString(): String = f"Operator('$value')"
}



case class IdentifierToken(value: String) extends Token{
    override def toString(): String = f"Identifier('$value')"
}
case class ErrorToken(value: String) extends Token{
    override def toString(): String = f"Error('$value')"
}
case class CommentToken(value: String) extends Token{
    override def toString(): String = f"Comment('$value')"
}

case class StringToken(value: String) extends Token{
    override def toString(): String = f"String('$value')"
}
case class IntToken(value: Int) extends Token{
    override def toString(): String = f"Int('$value')"
}
case class FloatToken(value: Float) extends Token{
    override def toString(): String = f"Float('$value')"
}
case class BooleanToken(value: Boolean) extends Token{
    override def toString(): String = f"Boolean('$value')"
}