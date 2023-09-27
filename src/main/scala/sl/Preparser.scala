package sl

import sl.Reporter
import sl.Parser.namespacedName

object Preparser {
  val paternsToReplace = List(
    ("@s\n", "@s\n;"),
    ("@e\n", "@e\n;"),
    ("@a\n", "@a\n;"),
    ("@p\n", "@p\n;"),
    ("@r\n", "@r\n;")
  )
  def parse(name: String, text: String, silent: Boolean = false): String = {
    var text2 = text
    val cmd = "\n\\s*/([a-zA-Z0-9].+)".r
    val cmd2 = "\\./([a-zA-Z0-9].+)".r
    val doc = "\"\"\"([^\"]+)\"\"\"".r
    val namespacedName =
      "([a-zA-Z][a-zA-Z0-9$\\._]*):([a-zA-Z][a-zA-Z0-9$\\._]*)".r

    def unformatNamespacedName(name: String): String = {
      val namespacedName =
        "§§§\"([a-zA-Z][a-zA-Z0-9$\\._]*)@([a-zA-Z][a-zA-Z0-9$\\._]*)\"§§§".r
      var ended = false
      var text2 = name
      while (!ended) {
        namespacedName.findFirstMatchIn(text2) match
          case None => ended = true
          case Some(value) => {
            value.matched
            text2 =
              value.before.toString() + value.group(1) + ":" + value.group(
                2
              ) + value.after.toString()
          }
      }

      text2
    }

    var ended = false
    /*while(!ended){
            namespacedName.findFirstMatchIn(text2)match
                case None => ended = true
                case Some(value) => {
                    value.matched
                    text2 = value.before.toString()+
                    "§§§"+ Utils.stringify(value.group(1)+"@"+value.group(2))+ "§§§" +
                    value.after.toString()
                }
        }*/

    ended = false
    while (!ended) {
      cmd.findFirstMatchIn(text2) match
        case None => ended = true
        case Some(value) => {
          value.matched
          text2 = value.before.toString() +
            "\n%%%" + Utils.stringify(
              unformatNamespacedName(value.group(1))
            ) + "%%%" +
            value.after.toString()
        }
    }
    ended = false
    while (!ended) {
      cmd2.findFirstMatchIn(text2) match
        case None => ended = true
        case Some(value) => {
          value.matched
          text2 = value.before.toString() +
            "%%%" + Utils.stringify(
              unformatNamespacedName(value.group(1))
            ) + "%%%" +
            value.after.toString()
        }
    }

    ended = false
    while (!ended) {
      doc.findFirstMatchIn(text2) match
        case None => ended = true
        case Some(value) => {
          value.matched
          text2 = value.before.toString() +
            "???" + Utils.stringify(
              unformatNamespacedName(
                value.group(1).replaceAllLiterally("\n", "")
              )
            ) + "???" +
            value.after.toString()
        }
    }

    paternsToReplace.foreach(p => text2 = text2.replaceAllLiterally(p._1, p._2))
    if (name != "" && !silent) {
      Reporter.ok(f"Preparsed: $name")
    }

    text2
      .replaceAll("\"\"\"([^\"]*)\"\"\"", "")
      .replaceAllLiterally("\\\"", "◘")
  }
}
