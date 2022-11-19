package fos

object Settings{
    var variableScoreboard = "tbms.var"
    var valueScoreboard = "tbms.value"
    var constScoreboard = "tbms.const"
    var tmpScoreboard = "tbms.tmp"
    var functionFolder = "zzz_sl_block"
    var multiplexFolder = "zzz_sl_mux"
    var floatPrec = 1000
    var treeSize = 20
    var target:Target = MCJava
}

trait Target{
    def getFunctionPath(path: String): String
    def getJsonPath(path: String): String
}
case object MCJava extends Target{
    def getFunctionPath(path: String): String = {
        "/data/" + path.replaceAllLiterally(".","/").replaceFirst("/", "/functions/")+ ".mcfunction"
    }
    def getJsonPath(path: String): String = {
        "/data/" + path.replaceAllLiterally(".","/")+ ".json"
    }
}
case object MCBedrock extends Target{
    def getFunctionPath(path: String): String = {
        "/data/functions" + path.replaceAllLiterally(".","/") + ".mcfunction"
    }
    def getJsonPath(path: String): String = {
        "/data/" + path.replaceAllLiterally(".","/") + ".json"
    }
}