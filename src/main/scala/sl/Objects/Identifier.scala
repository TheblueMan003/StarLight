package objects

object Identifier:
    given fromString: Conversion[String, Identifier] = (s => Identifier(s.split("\\.").toList))

case class Identifier(values: List[String]) {
    def child(name: String): Identifier={
        Identifier(values:::List(name))
    }
    def getTagFunctionName()={
        toString().replaceAll("[\\.]", "_").replaceAll("@", "")
    }
    def parent(): Identifier = {
        Identifier(values.dropRight(1))
    }
    def drop(): Identifier = {
        Identifier(values.tail)
    }
    def drop(n: Int): Identifier = {
        Identifier(values.drop(n))
    }
    def size(): Int = {
        values.size
    }
    def isSingleton() = {
        values.size == 1
    }
    def head(): String = {
        if (values.size == 0){
            "~"
        }
        else{
            values.head
        }
    }
    def isPrefixBy(other: Identifier): Boolean ={
        def rec(l1: List[String], l2: List[String]): Boolean = {
            l1 match
                case head :: next => {
                    l2 match
                        case h1 :: n2 => h1 == head && rec(next, n2)
                        case Nil => true
                }
                case Nil => {
                    l2 match
                        case h1 :: n2 => false
                        case Nil => true
                }
            
        }
        rec(values, other.values)
    }
    def getSufixOf(other: Identifier): List[String] ={
        def rec(l1: List[String], l2: List[String]): List[String] = {
            l1 match
                case head :: next => {
                    l2 match
                        case h1 :: n2 => {
                            if (h1 == head) {
                                rec(next, n2)
                            }
                            else{
                                head :: next
                            }
                        }
                        case Nil => Nil
                }
                case Nil => {
                    l2 match
                        case h1 :: n2 => l2
                        case Nil => Nil
                }
            
        }
        rec(values, other.values)
    }
    def replaceAllLiterally(other: String, to: String): Identifier ={
        Identifier.fromString(toString().replaceAll(other, to))
    }
    def replaceAllLiterally(other: Identifier, to: Identifier): Identifier ={
        if (isPrefixBy(other)){
            Identifier(to.values ::: getSufixOf(other))
        }
        else{
            this
        }
    }

    override def toString(): String = {
        values.reduce(_ + "." + _)
    }
}
