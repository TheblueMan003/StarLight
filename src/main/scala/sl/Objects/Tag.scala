package objects

import objects.*
import sl.Settings
import sl.Expression
import sl.Utils
import sl.MCJava

trait TagType{
    def path(fullName: String):String
}
case object BlockTag extends TagType{
    override def path(fullName: String): String = 
        fullName.replaceFirst("\\.", ".tags.blocks.")

}
class Tag(context: Context, _name: String, _modifier: Modifier, _content: List[Expression], typ: TagType) extends CObject(context, _name, _modifier){
    val content = _content
    def exists(): Boolean = Settings.target == MCJava
    def getContent(): List[String] = List("""{"values":[""" + content.map(v => Utils.stringify(v.getString()(Context.getNew("")))).reduceOption(_ + ", "+ _).getOrElse("") +"]}")
    def getName(): String = Settings.target.getJsonPath(typ.path(fullName))
    def getTag(): String = "#"+Settings.target.getFunctionName(fullName)
}