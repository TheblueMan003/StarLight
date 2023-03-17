package sl.IR

class IRFile(path: String, name: String, contents: List[IRTree], isJson: Boolean = false) {
    var calledBy: List[String] = List()
    var calling = List[String]()


    var deleted: Boolean = false
    var _content = contents

    def getPath(): String = {
        path
    }
    def getName(): String = {
        name
    }
    def getContents(): List[IRTree] = {
        _content
    }
    def setContents(content: List[IRTree]): Unit = {
        _content = content
    }

    def isJsonFile(): Boolean = {
        isJson
    }

    def addCalledBy(name: String): Unit = {
        calledBy = calledBy :+ name
    }
    def callByCount(): Int = {
        calledBy.length
    }
    def addCalling(name: String): Unit = {
        calling = calling :+ name
    }
    def callingCount(): Int = {
        calling.length
    }

    def resetCallGraph(): Unit = {
        calledBy = List()
        calling = List()
    }


    def delete(): Unit = {
        deleted = true
    }

    def hasSelfCall(): Boolean = {
        calledBy.contains(name)
    }

    def isBlock(): Boolean = {
        name.contains(".zzz_sl_block.")
    }
}