package fos

object Preparser{
    def parse(text: String): String = {
        text.replaceAll("\n\\s*/(.+)","\n%%%$1%%%")
    }
}