package sl

import objects.Context
import java.util.Random
import objects.types.VoidType
import sl.IR.*

case class PackInfo(var version: Int, var description: String, var min_engine_version: List[Int])
class SettingsContext(){
    var name = "default"
    var author = "user"
    var version = List(1,0,0)
    var variableScoreboard = "tbms.var"
    var valueScoreboard = "tbms.value"
    var constScoreboard = "tbms.const"
    var tmpScoreboard = "tbms.tmp"

    var functionFolder = "zzz_sl_block"
    var multiplexFolder = "zzz_sl_mux"
    var tagsFolder = "zzz_sl_tags"
    var outputName = "default"
    var hashedScoreboard = false

    var java_datapack_version = PackInfo(10, "Made With StarLight", List(1,19,3))
    var java_resourcepack_version = PackInfo(10, "Made With StarLight", List(1,19,3))

    var bedrock_behaviorpack_version = PackInfo(2, "Made With StarLight", List(1,19,50))
    var bedrock_resourcepack_version = PackInfo(2, "Made With StarLight", List(1,19,50))

    var java_datapack_output = List("./output/java_datapack")
    var java_resourcepack_output = List("./output/java_resourcepack")
    var bedrock_behaviorpack_output = List("./output/bedrock_datapack")
    var bedrock_resourcepack_output = List("./output/bedrock_resourcepack")

    var floatPrec = 1000
    var treeSize = 20
    var target: Target = MCJava
    var obfuscate = false
    var debug = false
    var allFunction = true

    var optimize = false
    var optimizeInlining = true
    var optimizeDeduplication = true
    var optimizeVariableValue = true
    var optimizeVariableGlobal = true
    var optimizeVariableLocal = true
    var optimizeAllowRemoveProtected = false

    var optimizeMaxInlining = 10

    var globalImport: Instruction = InstructionList(List())

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
    def getRPJsonPath(path: String): String
    def getExtraFiles(context: Context): List[IRFile]
    def getResourcesExtraFiles(context: Context): List[IRFile]

    def hasFeature(feature: String): Boolean
}
case object MCJava extends Target{
    val features = List("execute on", "execute positioned over", "string")
    def hasFeature(feature: String): Boolean = features.exists(x => x == feature)

    def getFunctionPath(path: String): String = {
        "/data/" + path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", "/functions/")+ ".mcfunction"
    }
    def getPredicatePath(path: String): String = {
        "/data/" + path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", "/predicates/")+ ".json"
    }
    def getFunctionName(path: String): String = {
        path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/").replaceFirst("/", ":")
    }
    def getJsonPath(path: String): String = {
        "/data/" + path.replaceAll("([A-Z])","-$1").toLowerCase().replaceAllLiterally(".","/")+ ".json"
    }
    def getRPJsonPath(path: String): String = {
        "/assets/minecraft/" + path.replaceAllLiterally(".","/")+ ".json"
    }
    def getExtraFiles(context: Context): List[IRFile] = {
        val ticks = context.getAllFunction().map(_._2)
                        .filter(_.modifiers.isTicking)
                        .map(f => getFunctionName(f.fullName))
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val loads = context.getAllFunction().map(_._2)
                        .filter(_.modifiers.isLoading)
                        .sortBy(f => f.modifiers.getAttributesFloat("tag.order", ()=> 0)(f.context))
                        .map(f => getFunctionName(f.fullName))
                        .prepended(f"${context.root.getPath()}:__init__")
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val dfScore = List(CommandIR(f"scoreboard objectives add ${Settings.tmpScoreboard} dummy"),
                            CommandIR(f"scoreboard objectives add ${Settings.valueScoreboard} dummy"),
                            CommandIR(f"scoreboard objectives add ${Settings.constScoreboard} dummy"),
                            CommandIR(f"scoreboard objectives add ${Settings.variableScoreboard} dummy"))::: 
                            context.getAllConstant().map(v => ScoreboardSet(SBLink(f"c$v", Settings.constScoreboard), v)):::
                            context.getAllVariable().filter(_.modifiers.isEntity).map(v => CommandIR(f"scoreboard objectives add ${v.scoreboard} ${v.criterion}"))

        

        List(IRFile("pack.mcmeta", "pack.mcmeta", List(JsonIR(getPackMeta())), true),
            IRFile(f"data/${context.root.getPath()}/functions/__init__.mcfunction", "__init__", dfScore, false, false),
            IRFile("data/minecraft/tags/functions/tick.json", "data/minecraft/tags/functions/tick.json", List(JsonIR("{"+ f"\t\"values\":[$ticks]"+ "}")), true),
            IRFile("data/minecraft/tags/functions/load.json", "data/minecraft/tags/functions/load.json", List(JsonIR("{"+ f"\t\"values\":[$loads]"+ "}")), true))
    }

    def getPackMeta()=
        f"""
        {
            "pack": {
                "pack_format": ${Settings.java_datapack_version.version},
                "description": ${Utils.stringify(Settings.java_datapack_version.description)}
            }
        }
        """
    def getResourcesExtraFiles(context: Context):List[IRFile]= {
        List(IRFile(f"pack.mcmeta", f"pack.mcmeta", List(JsonIR(getResourcePackMeta())), true))
    }
    def getResourcePackMeta()=
        f"""
        {
            "pack": {
                "pack_format": ${Settings.java_resourcepack_version.version},
                "description": ${Utils.stringify(Settings.java_resourcepack_version.description)}
            }
        }
        """
}
case object MCBedrock extends Target{
    val features = List()
    def hasFeature(feature: String): Boolean = features.contains(feature)

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
    def getRPJsonPath(path: String): String = {
        "/" + path.replaceAllLiterally(".","/")+ ".json"
    }
    def getExtraFiles(context: Context): List[IRFile] = {
        val ticks = context.getAllFunction().map(_._2)
                        .filter(_.modifiers.isTicking)
                        .map(f => getFunctionName(f.fullName))
                        .map(Utils.stringify(_))
                        .reduceOption(_ +","+_)
                        .getOrElse("")

        val dfScore = List(CommandIR(f"scoreboard objectives add ${Settings.tmpScoreboard} dummy"),
                            CommandIR(f"scoreboard objectives add ${Settings.valueScoreboard} dummy"),
                            CommandIR(f"scoreboard objectives add ${Settings.constScoreboard} dummy"),
                            CommandIR(f"scoreboard objectives add ${Settings.variableScoreboard} dummy"))::: 
                            context.getAllConstant().map(v => ScoreboardSet(SBLink(f"c$v", Settings.constScoreboard), v)):::
                            context.getAllVariable().filter(_.modifiers.isEntity).map(v => CommandIR(f"scoreboard objectives add ${v.scoreboard} dummy")):::
                            context.getAllVariable().filter(v => !v.modifiers.isEntity && !v.modifiers.isLazy && v.getType() != VoidType).map(v => ScoreboardSet(v.getIRSelector(), 0)):::
                            List(CommandIR(f"function ${context.root.getPath()}/__load__"))

        List(IRFile(f"manifest.json", f"manifest.json", List(JsonIR(getManifestContent())), true),
            IRFile(f"functions/__init__.mcfunction", "__init__", dfScore, false, false),
            IRFile("functions/tick.json", "functions/tick.json", List(JsonIR("{"+ f"\t\"values\":[$ticks]"+ "}")), true))
    }

    def getManifestContent(): String = {
        f"""{
    "format_version": ${Settings.bedrock_behaviorpack_version.version},
    "header": {
        "description": "${Settings.bedrock_behaviorpack_version.description}",
        "name": "${Settings.outputName}",
        "uuid": "${getUUID(Settings.outputName)}",
        "version": [${Settings.version(0)}, ${Settings.version(1)}, ${Settings.version(2)}],
        "min_engine_version": [${Settings.bedrock_behaviorpack_version.min_engine_version(0)}, ${Settings.bedrock_behaviorpack_version.min_engine_version(1)}, ${Settings.bedrock_behaviorpack_version.min_engine_version(2)}]
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

    def getResourcesExtraFiles(context: Context):List[IRFile]= {
        List(IRFile(f"manifest.json", f"manifest.json", List(JsonIR(getResourcesManifestContent())), true))
    }

    def getResourcesManifestContent(): String = {
        f"""{
    "format_version": ${Settings.bedrock_resourcepack_version.version},
    "header": {
        "description": "${Settings.bedrock_resourcepack_version.description}",
        "name": "${Settings.outputName}",
        "uuid": "${getUUID(Settings.outputName+"rp")}",
        "version": [${Settings.version(0)}, ${Settings.version(1)}, ${Settings.version(2)}],
        "min_engine_version": [${Settings.bedrock_resourcepack_version.min_engine_version(0)}, ${Settings.bedrock_resourcepack_version.min_engine_version(1)}, ${Settings.bedrock_resourcepack_version.min_engine_version(2)}]
    },
    "modules": [
        {
            "description": "${Settings.bedrock_resourcepack_version.description}",
            "type": "resources",
            "uuid": "${getUUID(Settings.outputName+"rp_")}",
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