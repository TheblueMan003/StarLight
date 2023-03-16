package sl.IR

trait IRTree{
    def getString(): String
}
trait IRExecute{
    def getExecuteString(): String
}

object EmptyIR extends IRTree{
    def getString(): String = ""
}
case class JsonIR(json: String) extends IRTree{
    def getString(): String = json
}

case class SBLink(entity: String, objective: String) extends IRTree{
    def getString(): String = s"$entity $objective"
    override def toString(): String = getString()
}

case class CommandIR(statement: String) extends IRTree{
    def getString(): String = statement
}
case class IfScoreboard(left: SBLink, op: String, right: SBLink, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def condition={
        if invert then 
            "unless score "+left.getString() + " " + op + " " + right.getString()
        else
            "if score "+left.getString() + " " + op + " " + right.getString()
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}
case class IfScoreboardMatch(left: SBLink, min: Int, max: Int, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def getBound = {
        (min, max) match
            case (Int.MinValue, v) => ".." + v
            case (v, Int.MaxValue) => v + ".."
            case (v1, v2) if v1 == v2 => v1.toString
            case (v1, v2) => v1 + ".." + v2
    }
    def condition={
        if invert then 
            "unless score "+left.getString() + " matches " + getBound
        else
            "if score "+left.getString() + " matches " + getBound
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}
case class IfEntity(selector: String, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def condition={
        if invert then 
            "unless entity "+selector
        else
            "if entity "+selector
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}

case class IfBlock(value: String, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def condition={
        if invert then 
            "unless block "+value
        else
            "if block "+value
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}

case class IfBlocks(value: String, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def condition={
        if invert then 
            "unless blocks "+value
        else
            "if blocks "+value
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}

case class IfPredicate(value: String, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def condition={
        if invert then 
            "unless predicate "+value
        else
            "if predicate "+value
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}

case class IfLoaded(value: String, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def condition={
        if invert then 
            "unless loaded "+value
        else
            "if loaded "+value
    }
    def getString(): String = {
        "execute "+condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        condition + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}

class ExecuteIR(block: String, statement: IRTree) extends IRTree with IRExecute{
    def getString(): String = {
        "execute "+block + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
    def getExecuteString(): String = {
        block + (statement match
            case a: IRExecute => " "+a.getExecuteString()
            case _ => " run "+statement.getString()
        )
    }
}
case class OnIR(selector: String, statement: IRTree) extends ExecuteIR(f"on ${selector}", statement)
case class AsIR(selector: String, statement: IRTree) extends ExecuteIR(f"as ${selector}", statement)
case class AtIR(selector: String, statement: IRTree) extends ExecuteIR(f"at ${selector}", statement)
case class PositionedOverIR(pos: String, statement: IRTree) extends ExecuteIR(f"positioned over ${pos}", statement)
case class PositionedIR(pos: String, statement: IRTree) extends ExecuteIR(f"positioned ${pos}", statement)
case class RotatedIR(rot: String, statement: IRTree) extends ExecuteIR(f"rotated ${rot}", statement)
case class RotatedEntityIR(rot: String, statement: IRTree) extends ExecuteIR(f"rotated as ${rot}", statement)
case class FacingIR(value: String, statement: IRTree) extends ExecuteIR(f"facing ${value}", statement)
case class FacingEntityIR(ent: String, height: String, statement: IRTree) extends ExecuteIR(f"facing entity ${ent} $height", statement)
case class AlignIR(value: String, statement: IRTree) extends ExecuteIR(f"align ${value}", statement)

case class ScoreboardOperation(target: SBLink, operation: String, source: SBLink) extends IRTree{
    def getString(): String = s"scoreboard players operation $target $operation $source"
}
case class ScoreboardSet(target: SBLink, value: Int) extends IRTree{
    def getString(): String = s"scoreboard players set $target $value"
}
case class ScoreboardAdd(target: SBLink, value: Int) extends IRTree{
    def getString(): String = s"scoreboard players add $target $value"
}
case class ScoreboardRemove(target: SBLink, value: Int) extends IRTree{
    def getString(): String = s"scoreboard players remove $target $value"
}
case class ScoreboardReset(target: SBLink) extends IRTree{
    def getString(): String = s"scoreboard players reset $target"
}

case class BlockCall(block: String) extends IRTree{
    def getString(): String = s"function $block"
}