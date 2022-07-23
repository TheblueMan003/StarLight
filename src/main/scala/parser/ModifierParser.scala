package parser

import objects.Context
import utils.TokenBufferedIterator
import lexer.tokens.*

import scala.collection.mutable.ArrayBuffer
import objects.Modifier
import objects.Protection

object ModifierParser{
    def parse(text: TokenBufferedIterator, _acc: Modifier = null)(implicit context: Context): Modifier = {
        var acc = _acc
        def use()={
            if (acc == null){
                acc = Modifier()
            }
        }        
        text.peekNoSpace() match{
            case IdentifierToken("public") => {
                text.takeNoSpace()
                use()
                acc.protection = Protection.Public
                parse(text, acc)
            }
            case IdentifierToken("protected") => {
                text.takeNoSpace()
                use()
                acc.protection = Protection.Protected
                parse(text, acc)
            }
            case IdentifierToken("private") => {
                text.takeNoSpace()
                use()
                acc.protection = Protection.Private
                parse(text, acc)
            }

            case IdentifierToken("override") => {
                text.takeNoSpace()
                use()
                acc.isOverride = true
                parse(text, acc)
            }
            case IdentifierToken("lazy") => {
                text.takeNoSpace()
                use()
                acc.isLazy = true
                parse(text, acc)
            }
            case IdentifierToken("inline") => {
                text.takeNoSpace()
                use()
                acc.isInline = true
                parse(text, acc)
            }
            case IdentifierToken("entity") => {
                text.takeNoSpace()
                use()
                acc.isEntity = true
                parse(text, acc)
            }
            case IdentifierToken(name) if name.startsWith("@") => {
                text.takeNoSpace()
                use()
                acc.tags.addOne(name)
                parse(text, acc)
            }
            case _ => acc
        }
    }
}