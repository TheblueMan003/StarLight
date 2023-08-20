package sl.Compilation

import sl.Utils

object BlockConverter{
    lazy val blockMap = loadBlockMap()
    def getBlockName(name: String)={
        blockMap.getOrElse(name, (name, "-1"))._1
    }
    def getBlockID(name: String)={
        blockMap.getOrElse(name, (name, "-1"))._2
    }
    def getItemID(name: String)={
        blockMap.getOrElse(name, (name, "0"))._2
    }
    def loadBlockMap()={
        val line = Utils.getConfig("blockmap.csv")
        line.map(_.split(";")).drop(1).filter(_.length > 2).map(f => (f(0), (f(1), f(2)))).toMap
    }
}