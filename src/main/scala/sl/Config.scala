package sl

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
    def load(path: String)={
        Settings = SettingsContext()

        val meta_var = raw"meta\.(\w[a-zA-Z0-9]*)".r

        Utils.getFileLines(path).foreach(line => {
            val splitted = line.split("\\=", 2)
            if (splitted.length == 2){
                (splitted(0), splitted(1)) match{
                    case ("name", name)                => Settings.outputName = name
                    case ("target", "bedrock")         => Settings.target = MCBedrock
                    case ("target", "java")            => Settings.target = MCJava
                    case ("scoreboard.variable", name) => Settings.variableScoreboard = name
                    case ("scoreboard.value", name)    => Settings.valueScoreboard = name
                    case ("scoreboard.const", name)    => Settings.constScoreboard = name
                    case ("scoreboard.tmp", name)      => Settings.tmpScoreboard = name

                    case ("mc.java.datapack.path", value)              => Settings.java_datapack_output = value.split(";").toList
                    case ("mc.java.datapack.version", value)           => Settings.java_datapack_version.version = value.toInt
                    case ("mc.java.datapack.description", value)       => Settings.java_datapack_version.description = value
                    case ("mc.java.datapack.min_engine_version", value)=> Settings.java_datapack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList
                    
                    case ("mc.java.resourcepack.path", value)              => Settings.java_resourcepack_output = value.split(";").toList
                    case ("mc.java.resourcepack.version", value)           => Settings.java_resourcepack_version.version = value.toInt
                    case ("mc.java.resourcepack.description", value)       => Settings.java_resourcepack_version.description = value
                    case ("mc.java.resourcepack.min_engine_version", value)=> Settings.java_resourcepack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("mc.bedrock.behaviorpack.path", value)              => Settings.bedrock_behaviorpack_output = value.split(";").toList
                    case ("mc.bedrock.behaviorpack.version", value)           => Settings.bedrock_behaviorpack_version.version = value.toInt
                    case ("mc.bedrock.behaviorpack.description", value)       => Settings.bedrock_behaviorpack_version.description = value
                    case ("mc.bedrock.behaviorpack.min_engine_version", value)=> Settings.bedrock_behaviorpack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("mc.bedrock.resourcepack.path", value)              => Settings.bedrock_resourcepack_output = value.split(";").toList
                    case ("mc.bedrock.resourcepack.version", value)           => Settings.bedrock_resourcepack_version.version = value.toInt
                    case ("mc.bedrock.resourcepack.description", value)       => Settings.bedrock_resourcepack_version.description = value
                    case ("mc.bedrock.resourcepack.min_engine_version", value)=> Settings.bedrock_resourcepack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("folder.block", name)        => Settings.functionFolder = name
                    case ("folder.mux", name)          => Settings.multiplexFolder = name
                    case ("folder.tag", name)          => Settings.tagsFolder = name

                    case ("tree.size", size)           => Settings.treeSize = size.toInt
                    
                    case ("meta.debug", value)         => Settings.debug = value == "true"
                    case (meta_var(name), value)       => Settings.metaVariable ::= ("Compiler."+name, () => value == "true")
                }
            }
        })
    }

    def get(target: String, name: String):List[String] = {
        List(
            f"name=$name",
            f"target=$target",
            f"scoreboard.variable=${Settings.variableScoreboard}",
            f"scoreboard.value=${Settings.valueScoreboard}",
            f"scoreboard.const=${Settings.constScoreboard}",
            f"scoreboard.tmp=${Settings.tmpScoreboard}",

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
        Main.safeWriteFile(path+"project.slproject", content)
    }

    def engine(lst: List[Int]):String={
        f"${lst(0)}.${lst(1)}.${lst(2)}"
    }
    def toCSV(lst: List[String]):String={
        lst.reduce(_ +";"+_)
    }
}