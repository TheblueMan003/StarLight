package sl.IR

trait IR{
    def getString(): String
}
trait IRExecute{
    def getExecuteString(): String
}
case class SBLink(entity: String, objective: String) extends IR{
    def getString(): String = s"$entity $objective"
}

case class CommandIR(statement: String) extends IR{
    def getString(): String = statement
}
case class IfScoreboard(left: SBLink, op: String, right: SBLink, statement: IR) extends IR with IRExecute{
    def condition={
        "if score "+left.getString() + " " + op + " " + right.getString()
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}
case class IfScoreboardMatch(left: SBLink, min: Int, max: Int, statement: IR) extends IR with IRExecute{
    def condition={
        "if score "+left.getString() + " matches " + min + ".." + max
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}
case class Execute(block: String, statement: IR) extends IR with IRExecute{
    def getString(): String = {
        block + (statement match
            case a: IRExecute => a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        block + (statement match
            case a: IRExecute => a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}
case class ScoreboardOperation(target: String, operation: String, source: String) extends IR{
    def getString(): String = s"scoreboard players operation $target $operation $source"
}
case class ScoreboardSet(target: String, value: String) extends IR{
    def getString(): String = s"scoreboard players set $target $value"
}
case class ScoreboardAdd(target: String, value: String) extends IR{
    def getString(): String = s"scoreboard players add $target $value"
}
case class ScoreboardRemove(target: String, value: String) extends IR{
    def getString(): String = s"scoreboard players remove $target $value"
}
case class ScoreboardReset(target: String) extends IR{
    def getString(): String = s"scoreboard players reset $target"
}

case class BlockCall(block: String) extends IR{
    def getString(): String = s"function $block"
}