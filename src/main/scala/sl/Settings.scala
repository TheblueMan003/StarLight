package sl

import objects.Context
import java.util.Random

case class PackInfo(var version: Int, var description: String, var min_engine_version: List[Int])
class SettingsContext(){
    var name = "default"
    var version = List(1,0,0)
    var variableScoreboard = "tbms.var"
    var valueScoreboard = "tbms.value"
    var constScoreboard = "tbms.const"
    var tmpScoreboard = "tbms.tmp"

    var functionFolder = "zzz_sl_block"
    var multiplexFolder = "zzz_sl_mux"
    var tagsFolder = "zzz_sl_tags"
    var outputName = "default"

    var java_datapack_version = PackInfo(10, "Made With StarLight", List(1,19,3))
    var java_resourcepack_version = PackInfo(10, "Made With StarLight", List(1,19,3))

    var bedrock_behaviorpack_version = PackInfo(2, "Made With StarLight", List(1,19,3))
    var bedrock_resourcepack_version = PackInfo(2, "Made With StarLight", List(1,19,3))

    var java_datapack_output = List("./output/java_datapack")
    var java_resourcepack_output = List("./output/java_resourcepack")
    var bedrock_behaviorpack_output = List("./output/bedrock_datapack")
    var bedrock_resourcepack_output = List("./output/bedrock_resourcepack")

    var floatPrec = 1000
    var treeSize = 20
    var target: Target = MCJava
    var debug = false
    var allFunction = true

    var metaVariable = List(
        ("Compiler.isJava", () => target == MCJava),
        ("Compiler.isBedrock", () => target == MCBedrock),
        ("Compiler.isDebug", () => debug)
    )
}
var Settings = SettingsContext()

trait Target{
    def getFunctionPath(path: String): String
    def getPredicatePath(path: String): String
    def getFunctionName(path: String): String
    def getJsonPath(path: String): String
    def getExtraFiles(context: Context): List[(String, List[String])]
}
case object MCJava extends Target{
    def getFunctionPath(path: String): String = {
        "/data/" + path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", "/functions/")+ ".mcfunction"
    }
    def getPredicatePath(path: String): String = {
        "/data/" + path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", "/predicates/")+ ".mcfunction"
    }
    def getFunctionName(path: String): String = {
        path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", ":")
    }
    def getJsonPath(path: String): String = {
        "/data/" + path.replaceAllLiterally(".","/")+ ".json"
    }
    def getExtraFiles(context: Context): List[(String, List[String])] = {
        val ticks = context.getAllFunction()
                        .filter(_.modifiers.isTicking)
                        .map(f => getFunctionName(f.fullName))
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val loads = context.getAllFunction()
                        .filter(_.modifiers.isLoading)
                        .map(f => getFunctionName(f.fullName))
                        .appended(f"${context.root.getPath()}:__init__")
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val dfScore = List(f"scoreboard objectives add ${Settings.tmpScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.valueScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.constScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.variableScoreboard} dummy")::: 
                            context.getAllConstant().map(v => f"scoreboard players set c$v ${Settings.constScoreboard} $v"):::
                            context.getAllVariable().filter(_.modifiers.isEntity).map(v => f"scoreboard objectives add ${v.scoreboard} ${v.criterion}")

        

        List(("pack.mcmeta", List(getPackMeta())),
            (f"data/${context.root.getPath()}/functions/__init__.mcfunction", dfScore),
            ("data/minecraft/tags/functions/tick.json", List("{", f"\t\"values\":[$ticks]", "}")),
            ("data/minecraft/tags/functions/load.json", List("{", f"\t\"values\":[$loads]", "}")))
    }

    def getPackMeta()=
        """
        {
            "pack": {
                "pack_format": 10,
                "description": "Made with Project Star Light"
            }
        }
        """
}
case object MCBedrock extends Target{
    def getFunctionPath(path: String): String = {
        "/functions/" + path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/") + ".mcfunction"
    }
    def getPredicatePath(path: String): String = {
        throw new Exception("Predicate not supported on bedrock!")
    }
    def getFunctionName(path: String): String = {
        path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/")
    }
    def getJsonPath(path: String): String = {
        "/" + path.replaceAllLiterally(".","/") + ".json"
    }
    def getExtraFiles(context: Context): List[(String, List[String])] = {
        val ticks = context.getAllFunction()
                        .filter(_.modifiers.isTicking)
                        .map(f => getFunctionName(f.fullName))
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val dfScore = List(f"scoreboard objectives add ${Settings.tmpScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.valueScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.constScoreboard} dummy",
                            f"scoreboard objectives add ${Settings.variableScoreboard} dummy")::: 
                            context.getAllConstant().map(v => f"scoreboard players set c$v ${Settings.constScoreboard} $v"):::
                            context.getAllVariable().filter(_.modifiers.isEntity).map(v => f"scoreboard objectives add ${v.scoreboard} dummy"):::
                            context.getAllVariable().filter(v => !v.modifiers.isEntity && !v.modifiers.isLazy).map(v => f"scoreboard players set ${v.getSelector()} 0"):::
                            List(f"function ${context.root.getPath()}/__load__")

        List((f"manifest.json", List(getManifestContent())),
            (f"functions/__init__.mcfunction", dfScore),
            ("functions/tick.json", List("{", f"\t\"values\":[$ticks]", "}")))
    }

    def getManifestContent(): String = {
        f"""
{
    "format_version": ${Settings.bedrock_behaviorpack_version.version},
    "header": {
        "description": "${Settings.bedrock_behaviorpack_version.description}",
        "name": "${Settings.outputName}",
        "uuid": "${getUUID(Settings.outputName)}",
        "version": [${Settings.version(0)}, ${Settings.version(1)}, ${Settings.version(2)}],
        "min_engine_version": [1, 19, 50]
    },
    "modules": [
        {
            "description": "${Settings.bedrock_behaviorpack_version.description}",
            "type": "data",
            "uuid": "${getUUID(Settings.outputName+"_")}",
            "version": [${Settings.version(0)}, ${Settings.version(1)}, ${Settings.version(2)}]
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