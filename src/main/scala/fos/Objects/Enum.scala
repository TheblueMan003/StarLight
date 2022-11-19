package objects

import fos.*
import objects.types.Type

class Enum(context: Context, name: String, _modifier: Modifier, val fields: List[EnumField]) extends CObject(context, name, _modifier){
    var values = List[EnumValue]()

    def addValues(v: List[EnumValue])(implicit context: Context) = {
        // Remove Duplicate
        val filtered = v.filter(x => !values.exists( y => x.name == x.name))

        // Check fields
        filtered.foreach(x => {
            if (x.fields.length != fields.length){
                throw new Exception(f"Wrong number of fields in enum value ${x.name}. Expected: ${fields.length} got ${x.fields.length}")
            }
            x.fields.map(Utils.typeof(_)).zip(fields).foreach((a, b) => {
                if (a.getDistance(b.typ) > 1000){
                    throw new Exception(f"Unexpected type in enum value ${x.name}. Expected: ${b.typ} got ${a}")
                }
            })
        })

        // Append
        values = values ::: filtered
    }
}

case class EnumValue(val name: String, val fields: List[Expression]){
}
case class EnumField(val name: String, val typ: Type){
}