package sl

import sl.files.FileUtils

object ConfigLoader{
    val newProjectPath = List(
        "java_resourcepack/assets/minecraft/textures/block",
        "java_resourcepack/assets/minecraft/textures/item",
        "java_resourcepack/assets/minecraft/sounds",
        "java_resourcepack/assets/minecraft/models/block",
        "java_resourcepack/assets/minecraft/models/item",
        "java_resourcepack/assets/minecraft/blockstate",
        "java_resourcepack/assets/minecraft/font",
        "bedrock_resourcepack/textures/blocks",
        "bedrock_resourcepack/textures/items",
        "bedrock_resourcepack/textures/particle",
        "bedrock_resourcepack/animation_controllers",
        "bedrock_resourcepack/animations",
        "bedrock_resourcepack/attachables",
        "bedrock_resourcepack/entity",
        "bedrock_resourcepack/font",
        "bedrock_resourcepack/models",
        "bedrock_resourcepack/particles",
        "bedrock_resourcepack/render_controllers",
        "bedrock_resourcepack/sounds",
        "resources"
    )
    def loadVersion(value: String): PackVersion = {
        Parser.parseExpression(value, true) match
            case IntValue(value) => SinglePackVersion(value)
            case RangeValue(IntValue(min), IntValue(max), IntValue(1)) => RangedPackVersion(min, max)
            case JsonValue(JsonArray(lst)) => {
                ListPackVersion(lst.map{
                    _ match
                        case JsonInt(value, typ) => value
                        case JsonExpression(IntValue(value), typ) => value
                        case other => throw new Exception(f"Illegal Version format: $other")
                })
            }
            case other => throw new Exception(f"Illegal Version format: $other")
        
    }
    def load(path: String)={
        Settings = SettingsContext()

        val meta_var = raw"meta\.(\w[a-zA-Z0-9]*)".r

        Utils.getFileLines(path).foreach(line => {
            val splitted = line.split("\\=", 2)
            if (splitted.length == 2){
                (splitted(0), splitted(1)) match{
                    case ("name", name)                => Settings.outputName = name
                    case ("namespace", name)           => Settings.name = name
                    case ("author", name)              => Settings.author = name
                    case ("target", "bedrock")         => Settings.target = MCBedrock
                    case ("target", "java")            => Settings.target = MCJava
                    case ("scoreboard.variable", name) => Settings.variableScoreboard = name
                    case ("scoreboard.value", name)    => Settings.valueScoreboard = name
                    case ("scoreboard.const", name)    => Settings.constScoreboard = name
                    case ("scoreboard.tmp", name)      => Settings.tmpScoreboard = name
                    case ("scoreboard.do_hash", value) => Settings.hashedScoreboard = value == "true"

                    case ("mc.java.datapack.path", value)              => Settings.java_datapack_output = value.split(";").toList
                    case ("mc.java.datapack.version", value)           => Settings.java_datapack_version.version = loadVersion(value)
                    case ("mc.java.datapack.description", value)       => Settings.java_datapack_version.description = value
                    case ("mc.java.datapack.min_engine_version", value)=> Settings.java_datapack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList
                    
                    case ("mc.java.resourcepack.path", value)              => Settings.java_resourcepack_output = value.split(";").toList
                    case ("mc.java.resourcepack.version", value)           => Settings.java_resourcepack_version.version = loadVersion(value)
                    case ("mc.java.resourcepack.description", value)       => Settings.java_resourcepack_version.description = value
                    case ("mc.java.resourcepack.min_engine_version", value)=> Settings.java_resourcepack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("mc.bedrock.behaviorpack.path", value)              => Settings.bedrock_behaviorpack_output = value.split(";").toList
                    case ("mc.bedrock.behaviorpack.version", value)           => Settings.bedrock_behaviorpack_version.version = loadVersion(value)
                    case ("mc.bedrock.behaviorpack.description", value)       => Settings.bedrock_behaviorpack_version.description = value
                    case ("mc.bedrock.behaviorpack.min_engine_version", value)=> Settings.bedrock_behaviorpack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("mc.bedrock.resourcepack.path", value)              => Settings.bedrock_resourcepack_output = value.split(";").toList
                    case ("mc.bedrock.resourcepack.version", value)           => Settings.bedrock_resourcepack_version.version = loadVersion(value)
                    case ("mc.bedrock.resourcepack.description", value)       => Settings.bedrock_resourcepack_version.description = value
                    case ("mc.bedrock.resourcepack.min_engine_version", value)=> Settings.bedrock_resourcepack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("optimization", value) => Settings.optimize = value == "true"
                    case ("optimization.inlining", value) => Settings.optimizeInlining = value == "true"
                    case ("optimization.deduplication", value)  => Settings.optimizeDeduplication = value == "true"
                    case ("optimization.variable", value) => Settings.optimizeVariableValue = value == "true"
                    case ("optimization.variable.local", value) => Settings.optimizeVariableLocal = value == "true"
                    case ("optimization.variable.global", value) => Settings.optimizeVariableGlobal = value == "true"
                    case ("optimization.fold", value) => Settings.optimizeFold = value == "true"
                    case ("optimization.allowRemoveProtected", value) => Settings.optimizeAllowRemoveProtected = value == "true"

                    case ("lazyTypeChecking", value) => Settings.lazyTypeChecking = value == "true"
                    case ("macroConvertToLazy", value) => Settings.macroConvertToLazy = value == "true"

                    case ("experimental.multi_thread", value) => Settings.experimentalMultithread = value == "true"

                    case ("export.doc", value) => Settings.exportDoc = value == "true"
                    case ("export.source", value) => Settings.exportSource = value == "true"
                    case ("export.contextPath", value) => Settings.exportContextPath = value == "true"


                    case ("obfuscate", value)          => Settings.obfuscate = value == "true"

                    case ("folder.block", name)        => Settings.functionFolder = name
                    case ("folder.mux", name)          => Settings.multiplexFolder = name
                    case ("folder.tag", name)          => Settings.tagsFolder = name

                    case ("tree.size", size)           => Settings.treeSize = size.toInt
                    
                    case ("meta.debug", value)         => Settings.debug = value == "true"
                    case (meta_var(name), value)       => Settings.metaVariable ::= ("Compiler."+name, () => value == "true")
                    case (other, value)                => Reporter.warning(f"Unknown config: $other")
                }
            }
        })
    }

    def get(target: String, name: String, namespace: String, author: String):List[String] = {
        Settings.outputName = name
        Settings.name = namespace.toLowerCase().replaceAllLiterally("[^a-zA-Z0-9]", "_")
        Settings.author = author

        Settings.target = target match{
            case "bedrock" => MCBedrock
            case "java" => MCJava
        }

        get(target)
    }
    def get(target: String)={
        List(
            f"name=${Settings.outputName}",
            f"namespace=${Settings.name}",
            f"author=${Settings.author}",
            f"target=${target.replace(".slconfig", "")}",
            f"scoreboard.variable=${Settings.variableScoreboard}",
            f"scoreboard.value=${Settings.valueScoreboard}",
            f"scoreboard.const=${Settings.constScoreboard}",
            f"scoreboard.tmp=${Settings.tmpScoreboard}",
            f"scoreboard.do_hash=${Settings.hashedScoreboard}",

            f"mc.java.datapack.path=${toCSV(Settings.java_datapack_output)}",
            f"mc.java.datapack.version=${Settings.java_datapack_version.version}",
            f"mc.java.datapack.description=${Settings.java_datapack_version.description}",
            f"mc.java.datapack.min_engine_version=${engine(Settings.java_datapack_version.min_engine_version)}",

            f"mc.java.resourcepack.path=${toCSV(Settings.java_resourcepack_output)}",
            f"mc.java.resourcepack.version=${Settings.java_resourcepack_version.version}",
            f"mc.java.resourcepack.description=${Settings.java_resourcepack_version.description}",
            f"mc.java.resourcepack.min_engine_version=${engine(Settings.java_resourcepack_version.min_engine_version)}",

            f"mc.bedrock.behaviorpack.path=${toCSV(Settings.bedrock_behaviorpack_output)}",
            f"mc.bedrock.behaviorpack.version=${Settings.bedrock_behaviorpack_version.version}",
            f"mc.bedrock.behaviorpack.description=${Settings.bedrock_behaviorpack_version.description}",
            f"mc.bedrock.behaviorpack.min_engine_version=${engine(Settings.bedrock_behaviorpack_version.min_engine_version)}",

            f"mc.bedrock.resourcepack.path=${toCSV(Settings.bedrock_resourcepack_output)}",
            f"mc.bedrock.resourcepack.version=${Settings.bedrock_resourcepack_version.version}",
            f"mc.bedrock.resourcepack.description=${Settings.bedrock_resourcepack_version.description}",
            f"mc.bedrock.resourcepack.min_engine_version=${engine(Settings.bedrock_resourcepack_version.min_engine_version)}",

            f"folder.block=${Settings.functionFolder}",
            f"folder.mux=${Settings.multiplexFolder}",
            f"folder.tag=${Settings.tagsFolder}",

            f"tree.size=${Settings.treeSize}",

            f"obfuscate=${Settings.obfuscate}",

            f"optimization=${Settings.optimize}",
            f"optimization.inlining=${Settings.optimizeInlining}",
            f"optimization.deduplication=${Settings.optimizeDeduplication}",
            f"optimization.variable=${Settings.optimizeVariableValue}",
            f"optimization.variable.local=${Settings.optimizeVariableLocal}",
            f"optimization.variable.global=${Settings.optimizeVariableGlobal}",
            f"optimization.fold=${Settings.optimizeFold}",
            f"optimization.allowRemoveProtected=${Settings.optimizeAllowRemoveProtected}",

            f"lazyTypeChecking=${Settings.lazyTypeChecking}",
            f"macroConvertToLazy=${Settings.macroConvertToLazy}",

            f"export.doc=${Settings.exportDoc}",
            f"export.source=${Settings.exportSource}",
            f"export.contextPath=${Settings.exportContextPath}",

            f"experimental.multi_thread=${Settings.experimentalMultithread}",
            
            f"meta.debug=true",
        )
    }

    def loadProject()={
        Utils.getFileLines("project.slproject").foreach(line =>{
            val splitted = line.split("=", 2)
            if (splitted.length == 2){
                (splitted(0), splitted(1)) match{
                    case ("version", version) => Settings.version = version.split("\\.").map(_.toInt).toList
                }
            }
        })
    }
    def saveProject(path: String = "")={
        val content = List(
            f"version=${engine(Settings.version)}"
        )
        FileUtils.safeWriteFile(path+"project.slproject", content)
    }

    def engine(lst: List[Int]):String={
        f"${lst(0)}.${lst(1)}.${lst(2)}"
    }
    def toCSV(lst: List[String]):String={
        lst.reduce(_ +";"+_)
    }
}