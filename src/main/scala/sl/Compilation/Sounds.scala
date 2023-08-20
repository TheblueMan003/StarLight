package sl.Compilation

import sl.Utils

object Sounds{
    lazy val soundMap = loadSoundMap()

    def getSoundName(name: String)={
        soundMap.getOrElse(name, name)
    }
    def loadSoundMap()={
        val line = Utils.getConfig("soundmap.csv")
        line.map(_.split(";")).drop(1).filter(_.length > 2).map(f => (f(0), f(1))).toMap
    }

    def getJava(name: String) = {
        name
    }

    def getBedrock(name: String)={
        if (soundMap.contains(name)) 
        {
            soundMap(name)
        }
        else{
            name
        }
    }
}