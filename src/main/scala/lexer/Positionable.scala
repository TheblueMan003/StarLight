package lexer

trait Positionable{
    var index: Int = 0
    var line: Int = 0
    var file: String = ""

    def setPosition(index: Int, line: Int, file: String):Unit = { 
        this.index = index;
        this.line = line;
        this.file = file;
    }

    def setPosition(other: Positionable):Unit = { 
        this.index = other.index;
        this.line = other.line;
        this.file = other.file;
    }

    def positionString(): String = f"in file: $file at line: $line at index: $index"
}