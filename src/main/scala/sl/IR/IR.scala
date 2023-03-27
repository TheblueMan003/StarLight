package sl.IR

trait IRTree{
    def getString(): String
}
trait IRExecute extends IRTree{
    def getExecuteString(): String
    def getStatements: IRTree
    def withStatements(statements: IRTree): IRExecute
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

    def getKey()(implicit context: IRContext) = {
        if entity.startsWith("@") then ???
        else entity+" "+objective
    }
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = IfScoreboard(left, op, right, nstatement, invert)
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = IfScoreboardMatch(left, min, max, nstatement, invert)
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = IfEntity(selector, nstatement, invert)
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = IfBlock(value, nstatement, invert)
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = IfBlocks(value, nstatement, invert)
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = IfPredicate(value, nstatement, invert)
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = IfLoaded(value, nstatement, invert)
}

case class ExecuteIR(block: String, statement: IRTree) extends IRTree with IRExecute{
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
    def getStatements = statement
    def withStatements(nstatement: IRTree): IRExecute = new ExecuteIR(block, nstatement)
}
def OnIR(selector: String, statement: IRTree) = ExecuteIR(f"on ${selector}", statement)
def AsIR(selector: String, statement: IRTree) = ExecuteIR(f"as ${selector}", statement)
def AtIR(selector: String, statement: IRTree) = ExecuteIR(f"at ${selector}", statement)
def PositionedOverIR(pos: String, statement: IRTree) = ExecuteIR(f"positioned over ${pos}", statement)
def PositionedIR(pos: String, statement: IRTree) = ExecuteIR(f"positioned ${pos}", statement)
def RotatedIR(rot: String, statement: IRTree) = ExecuteIR(f"rotated ${rot}", statement)
def RotatedEntityIR(rot: String, statement: IRTree) = ExecuteIR(f"rotated as ${rot}", statement)
def FacingIR(value: String, statement: IRTree) = ExecuteIR(f"facing ${value}", statement)
def FacingEntityIR(ent: String, height: String, statement: IRTree) = ExecuteIR(f"facing entity ${ent} $height", statement)
def AlignIR(value: String, statement: IRTree) = ExecuteIR(f"align ${value}", statement)

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

case class BlockCall(function: String, fullName: String) extends IRTree{
    def getString(): String = s"function $function"
}
case class ScheduleCall(function: String, fullName: String, time: Int) extends IRTree{
    def getString(): String = s"schedule function $function $time append"
}
case class ScheduleClear(function: String, fullName: String) extends IRTree{
    def getString(): String = s"schedule clear $function"
}
case class InterpreterException(message: String) extends IRTree{
    def getString(): String = s""
}