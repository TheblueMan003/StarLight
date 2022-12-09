package fos

object Preparser{
    def parse(text: String): String = {
        var text2 = text
        val cmd = "\n\\s*/([a-zA-Z0-9].+)".r

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
        
        text2.replaceAll("\"\"\"([^\"]*)\"\"\"", "").replaceAllLiterally("\\\"","◘")
    }
}