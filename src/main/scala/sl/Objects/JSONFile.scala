package objects

import objects.*
import sl.JSONElement
import sl.Settings
import sl.IR.*

class JSONFile(context: Context, _name: String, _modifier: Modifier, _json: JSONElement) extends CObject(context, _name, _modifier){
    var json: JSONElement = _json

    def exists(): Boolean = true
    def getContent(): List[JsonIR] = List(JsonIR(json.getString()(Context.getNew(""))))
    def getName(): String = if isDatapack() then Settings.target.getJsonPath(name) else Settings.target.getRPJsonPath(name)

    def isDatapack(): Boolean = !modifiers.hasAttributes("bedrock_rp")(context) && !modifiers.hasAttributes("java_rp")(context)
    def isBedrockRP(): Boolean = modifiers.hasAttributes("bedrock_rp")(context)
    def isJavaRP(): Boolean = modifiers.hasAttributes("java_rp")(context)

    def getIRFile(): IRFile = {
        IRFile(getName(), name, getContent(), List(), true)
    }
}