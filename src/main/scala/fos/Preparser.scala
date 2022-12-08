package fos

object Preparser{
    def parse(text: String): String = {
        text.replaceAll("\n\\s*/([a-zA-Z0-9].+)","\n%%%$1%%%").replaceAll("\"\"\"([^\"]*)\"\"\"", "")
    }
}