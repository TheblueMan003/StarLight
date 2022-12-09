package objects

import objects.*
import sl.JSONElement
import sl.Settings

class JSONFile(context: Context, _name: String, _modifier: Modifier, _json: JSONElement) extends CObject(context, _name, _modifier){
    var json: JSONElement = _json

    def exists(): Boolean = true
    def getContent(): List[String] = List(json.getString()(Context.getNew("")))
    def getName(): String = Settings.target.getJsonPath(name)
}