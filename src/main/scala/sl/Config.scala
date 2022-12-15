package sl

object ConfigLoader{
    def load(path: String)={
        Settings = SettingsContext()

        val meta_var = raw"meta\.(\w[a-zA-Z0-9]*)".r

        Utils.getFileLines(path).foreach(line => {
            val splitted = line.split("=", 1)
            if (splitted.length == 2){
                (splitted(0), splitted(1)) match{
                    case ("target", "bedrock") => Settings.target = MCBedrock
                    case ("target", "java") => Settings.target = MCJava
                    case ("scoreboard.variable", name) => Settings.variableScoreboard = name
                    case ("scoreboard.value", name)    => Settings.valueScoreboard = name
                    case ("scoreboard.const", name)    => Settings.constScoreboard = name
                    case ("scoreboard.tmp", name)      => Settings.tmpScoreboard = name

                    case ("mc.java.datapack.version", value)           => Settings.java_datapack_version.version = value.toInt
                    case ("mc.java.datapack.description", value)       => Settings.java_datapack_version.description = value
                    case ("mc.java.datapack.min_engine_version", value)=> Settings.java_datapack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("mc.java.resourcepack.version", value)           => Settings.java_resourcepack_version.version = value.toInt
                    case ("mc.java.resourcepack.description", value)       => Settings.java_resourcepack_version.description = value
                    case ("mc.java.resourcepack.min_engine_version", value)=> Settings.java_resourcepack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

                    case ("mc.bedrock.behaviorpack.version", value)           => Settings.bedrock_behaviorpack_version.version = value.toInt
                    case ("mc.bedrock.behaviorpack.description", value)       => Settings.bedrock_behaviorpack_version.description = value
                    case ("mc.bedrock.behaviorpack.min_engine_version", value)=> Settings.bedrock_behaviorpack_version.min_engine_version = value.split(raw"\.").map(_.toInt).toList

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
}