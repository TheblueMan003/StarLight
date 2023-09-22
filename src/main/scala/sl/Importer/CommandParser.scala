package sl.Importer

import scala.collection.mutable
import objects.types.*

class CommandParser{
    val scoreboardDelc: Regex = """scoreboard objectives add (\w+) (\w+)""".r
    val scoreboardSet: Regex = """scoreboard players set (\w+) (\w+) (\d+)""".r
    val scoreboardAdd: Regex = """scoreboard players add (\w+) (\w+) (\d+)""".r
    val scoreboardRemove: Regex = """scoreboard players remove (\w+) (\w+) (\d+)""".r
    val scoreboardReset: Regex = """scoreboard players reset (\w+) (\w+)""".r

    val functionCall = """function ([\w\\/\:]+)""".r

    val tagAdd = """tag (\w+) add (\w+)""".r
    val tagRemove = """tag (\w+) remove (\w+)""".r


    def line(text: List[String])(implicit shift: Int, context: PContext): List[String] = {
        text match{
            case scoreboardDelc(name, typ) :: rest => {
                f"[criterion=\"$typ\"] scoreboard int $name"
            }
            case scoreboardSet(name, target, value) :: rest => {
                if (name.start("@")){
                    if (name == "@s"){
                        f"$target = $value"
                    }
                    else{
                        f"$name.$target = $value"
                    }
                }
                else{
                    if (target == Settings.variableScoreboard){
                        context.requestVariable(name, IntType)
                        f"$name = $value"
                    }
                    else{
                        context.requestVariable(name, IntType)
                        f"$name_$target = $value"
                    }
                }
            }
            case scoreboardAdd(name, target, value) :: rest => {
                if (name.start("@")){
                    if (name == "@s"){
                        f"$target += $value"
                    }
                    else{
                        f"$name.$target += $value"
                    }
                }
                else{
                    if (target == Settings.variableScoreboard){
                        context.requestVariable(name, IntType)
                        f"$name += $value"
                    }
                    else{
                        context.requestVariable(f"$name_$target", IntType)
                        f"$name_$target += $value"
                    }
                }
            }
            case scoreboardRemove(name, target, value) :: rest => {
                if (name.start("@")){
                    if (name == "@s"){
                        f"$target -= $value"
                    }
                    else{
                        f"$name.$target -= $value"
                    }
                }
                else{
                    if (target == Settings.variableScoreboard){
                        context.requestVariable(name, IntType)
                        f"$name -= $value"
                    }
                    else{
                        context.requestVariable(f"$name_$target", IntType)
                        f"$name_$target -= $value"
                    }
                }
            }
            case scoreboardReset(name, target) :: rest => {
                if (name.start("@")){
                    if (name == "@s"){
                        f"$target = null"
                    }
                    else{
                        f"$name.$target = null"
                    }
                }
                else{
                    if (target == Settings.variableScoreboard){
                        context.requestVariable(name, IntType)
                        f"$name = null"
                    }
                    else{
                        context.requestVariable(f"$name_$target", IntType)
                        f"$name_$target = null"
                    }
                }
            }
            case functionCall(name) :: rest => {
                f"${name.replace(":",".").replace("/",".")}()"
            }
            case tagAdd(name, tag) :: rest => {
                context.requestVariable(tag, EntityType)
                f"$tag += $name"
            }
            case tagRemove(name, tag) :: rest => {
                context.requestVariable(tag, EntityType)
                f"$tag -= $name"
            }
            case _ => List()
        }
    }
}
class PContext{
    val variables: mutable.Map[String, Type] = mutable.Map()

    def requestVariable(name: String, typ: Type): Unit = {
        variables(name) = typ
    }
}