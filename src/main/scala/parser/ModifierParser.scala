package parser

import objects.Context
import utils.TokenBufferedIterator
import lexer.tokens.*

import scala.collection.mutable.ArrayBuffer
import objects.Modifier
import objects.Protection

object ModifierParser{
    def parse(_acc: Modifier = null)(implicit text: TokenBufferedIterator, context: Context): Modifier = {
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
                parse(acc)
            }
            case IdentifierToken("protected") => {
                text.takeNoSpace()
                use()
                acc.protection = Protection.Protected
                parse(acc)
            }
            case IdentifierToken("private") => {
                text.takeNoSpace()
                use()
                acc.protection = Protection.Private
                parse(acc)
            }

            case IdentifierToken("override") => {
                text.takeNoSpace()
                use()
                acc.isOverride = true
                parse(acc)
            }
            case IdentifierToken("lazy") => {
                text.takeNoSpace()
                use()
                acc.isLazy = true
                parse(acc)
            }
            case IdentifierToken("inline") => {
                text.takeNoSpace()
                use()
                acc.isInline = true
                parse(acc)
            }
            case IdentifierToken("entity") => {
                text.takeNoSpace()
                use()
                acc.isEntity = true
                parse(acc)
            }
            case IdentifierToken(name) if name.startsWith("@") => {
                text.takeNoSpace()
                use()
                acc.tags.addOne(name)
                parse(acc)
            }
            case _ => acc
        }
    }
}