package sl

import sl.Reporter

object Preparser{
    def parse(name: String, text: String): String = {
        var text2 = text
        val cmd = "\n\\s*/([a-zA-Z0-9].+)".r
        val doc = "\"\"\"([^\"]+)\"\"\"".r

        var ended = false
        while(!ended){
            cmd.findFirstMatchIn(text2)match
                case None => ended = true
                case Some(value) => {
                    value.matched
                    text2 = value.before.toString()+
                    "%%%"+ Utils.stringify(value.group(1))+ "%%%" +
                    value.after.toString()
                }
        }

        ended = false
        while(!ended){
            doc.findFirstMatchIn(text2)match
                case None => ended = true
                case Some(value) => {
                    value.matched
                    text2 = value.before.toString()+
                    "???"+ Utils.stringify(value.group(1).replaceAllLiterally("\n",""))+ "???" +
                    value.after.toString()
                }
        }

        Reporter.ok(f"Preparsed: $name")
        
        text2.replaceAll("\"\"\"([^\"]*)\"\"\"", "").replaceAllLiterally("\\\"","◘")
    }
}