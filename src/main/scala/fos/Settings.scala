package fos

import objects.Context
import java.util.Random

object Settings{
    var variableScoreboard = "tbms.var"
    var valueScoreboard = "tbms.value"
    var constScoreboard = "tbms.const"
    var tmpScoreboard = "tbms.tmp"

    var functionFolder = "zzz_sl_block"
    var multiplexFolder = "zzz_sl_mux"
    var tagsFolder = "zzz_sl_tags"
    var outputName = "default"
    var floatPrec = 1000
    var treeSize = 20
    var target:Target = MCJava
    var debug = false

    val metaVariable = List(
        ("Compiler.isJava", () => target == MCJava),
        ("Compiler.isBedrock", () => target == MCBedrock),
        ("Compiler.isDebug", () => debug)
    )
}

trait Target{
    def getFunctionPath(path: String): String
    def getPredicatePath(path: String): String
    def getFunctionName(path: String): String
    def getJsonPath(path: String): String
    def getExtraFiles(context: Context): List[(String, List[String])]
}
case object MCJava extends Target{
    def getFunctionPath(path: String): String = {
        "/data/" + path.replaceAll("([A-Z])","\\$$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", "/functions/")+ ".mcfunction"
    }
    def getPredicatePath(path: String): String = {
        "/data/" + path.replaceAll("([A-Z])","\\$$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", "/predicates/")+ ".mcfunction"
    }
    def getFunctionName(path: String): String = {
        path.replaceAll("([A-Z])","\\$$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", ":")
    }
    def getJsonPath(path: String): String = {
        "/data/" + path.replaceAllLiterally(".","/")+ ".json"
    }
    def getExtraFiles(context: Context): List[(String, List[String])] = {
        val ticks = context.getAllFunction()
                        .filter(_.modifiers.isTicking)
                        .map(_.fullName.replaceAllLiterally(".","/").replaceFirst("/", ":"))
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val loads = context.getAllFunction()
                        .filter(_.modifiers.isLoading)
                        .map(_.fullName.replaceAllLiterally(".","/").replaceFirst("/", ":"))
                        .appended(f"${context.root.getPath()}:__init__")
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val dfScore = List(f"scoreboard objectives add ${Settings.tmpScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.valueScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.constScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.variableScoreboard} dummy")::: 
                            context.getAllConstant().map(v => f"scoreboard players set $v ${Settings.constScoreboard} $v"):::
                            context.getAllVariable().filter(_.modifiers.isEntity).map(v => f"scoreboard objectives add ${v.scoreboard} dummy")

        List((f"data/${context.root.getPath()}/functions/__init__.mcfunction", dfScore),
            ("data/minecraft/tags/functions/tick.json", List("{", f"\t\"values\":[$ticks]", "}")),
            ("data/minecraft/tags/functions/load.json", List("{", f"\t\"values\":[$loads]", "}")))
    }
}
case object MCBedrock extends Target{
    def getFunctionPath(path: String): String = {
        "/functions/" + path.replaceAll("([A-Z])","\\$$1").toLowerCase().replaceAllLiterally(".","/") + ".mcfunction"
    }
    def getPredicatePath(path: String): String = {
        throw new Exception("Predicate not supported on bedrock!")
    }
    def getFunctionName(path: String): String = {
        path.replaceAll("([A-Z])","\\$$1").toLowerCase().replaceAllLiterally(".","/")
    }
    def getJsonPath(path: String): String = {
        "/" + path.replaceAllLiterally(".","/") + ".json"
    }
    def getExtraFiles(context: Context): List[(String, List[String])] = {
        val ticks = context.getAllFunction()
                        .filter(_.modifiers.isTicking)
                        .map(_.fullName.replaceAllLiterally(".","/"))
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val dfScore = List(f"scoreboard objectives add ${Settings.tmpScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.valueScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.constScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.variableScoreboard} dummy")::: 
                            context.getAllConstant().map(v => f"scoreboard players set $v ${Settings.constScoreboard} $v"):::
                            context.getAllVariable().filter(_.modifiers.isEntity).map(v => f"scoreboard objectives add ${v.scoreboard} dummy")

        List((f"manifest.json", List(getManifestContent())),
            (f"functions/__init__.mcfunction", dfScore),
            ("functions/tick.json", List("{", f"\t\"values\":[$ticks]", "}")))
    }

    def getManifestContent(): String = {
        f"""
{
    "format_version": 1,
    "header": {
        "description": "Made with Project Star Light",
        "name": "${Settings.outputName}",
        "uuid": "${getUUID(Settings.outputName)}",
        "version": [0, 0, 1],
        "min_engine_version": [1, 2, 6]
    },
    "modules": [
        {
            "description": "Made with Project Star Light",
            "type": "data",
            "uuid": "${getUUID(Settings.outputName+"_")}",
            "version": [0, 0, 1]
        }
    ]
}
        """
    }

    def getUUID(string: String): String = {
        val alphabet = "0123456789abcdef"
        val rng = Random()
        rng.setSeed(scala.util.hashing.MurmurHash3.stringHash(string).toLong)
        Range(0,8).map(_=>alphabet(rng.nextInt(16)).toString()).reduce(_ + _)+"-"+
        Range(0,4).map(_=>alphabet(rng.nextInt(16)).toString()).reduce(_ + _)+"-"+
        Range(0,4).map(_=>alphabet(rng.nextInt(16)).toString()).reduce(_ + _)+"-"+
        Range(0,4).map(_=>alphabet(rng.nextInt(16)).toString()).reduce(_ + _)+"-"+
        Range(0,12).map(_=>alphabet(rng.nextInt(16)).toString()).reduce(_ + _)
    }
}