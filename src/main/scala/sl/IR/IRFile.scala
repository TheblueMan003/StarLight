package sl.IR

class IRFile(val path: String, val name: String, val contents: List[IRTree], val comments: List[IRTree], val isJson: Boolean = false, val canDelete: Boolean = true, val isMacro: Boolean = false) {
    var calledBy: List[String] = List()
    var calling = List[String]()

    var scoreboardAccess: List[SBLink] = List()
    var scoreboardModified: List[SBLink] = List()


    var deleted: Boolean = false
    var _content = contents


    def addScoreboardAccess(link: SBLink): Unit = {
        scoreboardAccess = scoreboardAccess :+ link
    }
    def addScoreboardModified(link: SBLink): Unit = {
        scoreboardModified = scoreboardModified :+ link
    }
    def resetScoreboard(): Unit = {
        scoreboardAccess = List()
        scoreboardModified = List()
    }

    def getPath(): String = {
        path
    }
    def getName(): String = {
        name
    }
    def getContents(): List[IRTree] = {
        _content
    }
    def makeMacro(tree: IRTree): IRTree = {
        val str = tree.getString()
        if (str.contains("$")){
            CommandIR("$"+str)
        }
        else{
            CommandIR(str)
        }
    }
    def getFinalContents(): List[IRTree] = {
        if (isMacro){
            if (comments.length > 0){
                comments ::: List(EmptyLineIR) ::: _content.map(makeMacro(_))
            }
            else{
                _content.map(makeMacro(_))
            }
        }
        else{
            if (comments.length > 0){
                comments ::: List(EmptyLineIR) ::: _content
            }
            else{
                _content
            }
        }
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
        if (canDelete){
            deleted = true
        }
    }
    def canBeDeleted(): Boolean = {
        canDelete || contents.forall(x => x == EmptyIR || x.isInstanceOf[CommentsIR])
    }

    def hasSelfCall(): Boolean = {
        calledBy.contains(name)
    }

    def isBlock(): Boolean = {
        name.contains(".zzz_sl_block.")
    }

    override def toString(): String = name

    def printName()={
        println("File: " + name)
    }
    def print()={
        println("File: " + name)
        println(getContents().map("\t" + _.getString()).mkString("\n"))
    }
}