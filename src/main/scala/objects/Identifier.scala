package objects

case class Identifier(values: List[String]) {
    def child(name: String): Identifier={
        Identifier(values:::List(name))
    }
    def parent(): Identifier = {
        Identifier(values.dropRight(1))
    }
    def drop(): Identifier = {
        Identifier(values.tail)
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
}
