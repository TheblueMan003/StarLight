package sl.Compilation

import sl.Utils

object Sounds{
    lazy val java = Utils.getConfig("sounds/java.csv")
    lazy val bedrock = Utils.getConfig("sounds/bedrock.csv")

    def getJava(name: String) = {
        if (java.contains(name)) 
        {
            name
        }
        else{
            val splited = name.split("\\.")
            java.map(v => (v, splited.map(v.contains(_)).count(_ == true))).maxBy(_._2)._1
        }
    }

    def getBedrock(name: String)={
        if (bedrock.contains(name)) 
        {
            name
        }
        else{
            val splited = name.split("\\.")
            bedrock.map(v => (v, splited.map(v.contains(_)).count(_ == true))).maxBy(_._2)._1
        }
    }
}