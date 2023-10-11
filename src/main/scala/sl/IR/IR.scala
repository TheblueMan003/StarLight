package sl.IR

import objects.Variable

trait IRTree{
    var canBeDeleted = false
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
    def getStorage(): String = s"${objective.replaceFirst("\\.",":")} $entity"
    override def toString(): String = getString()

    def getKey()(implicit context: IRContext) = {
        if entity.startsWith("@") then ???
        else entity+" "+objective
    }

    def isEntity() = entity.startsWith("@")
}

case class CommandIR(statement: String) extends IRTree{
    def getString(): String = statement
}
case object EmptyLineIR extends IRTree{
    def getString(): String = ""
}
case class CommentsIR(statement: String) extends IRTree{
    def getString(): String = statement.split("\n").map(line => "# "+line).mkString("\n")
}
/* 
case class CommandInsertIR(link: SBLink, vari: String, statement: String) extends IRTree{
    def getString(): String = {
        Range(-10, 10).map(i => "execute if score "+link.getString()+" matches "+i+" run "+statement.replace(vari, i.toString())).mkString("\n")
    }
}*/
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
case class IfStorage(target: String, path: String, statement: IRTree, invert: Boolean = false) extends IRTree with IRExecute{
    def condition={
        if invert then 
            f"unless data storage $target $path"
        else
            f"if data storage $target $path"
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
    def withStatements(nstatement: IRTree): IRExecute = IfStorage(target, path, nstatement, invert)
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
    operation match
        case "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "<" | ">" | "><" | "><=" | ">=" | "=<" | "<=" => ()
        case _ => throw new Exception("Invalid operation: " + operation)
    
    def getString(): String = s"scoreboard players operation $target $operation $source"
}
case class ScoreboardSet(target: SBLink, value: Int) extends IRTree{
    def getString(): String = s"scoreboard players set $target $value"
}
case class ScoreboardAdd(target: SBLink, value: Int) extends IRTree{
    def getString(): String = if (value >= 0){s"scoreboard players add $target $value"}else{s"scoreboard players remove $target ${-value}"}
}
case class ScoreboardRemove(target: SBLink, value: Int) extends IRTree{
    def getString(): String = if (value >= 0){s"scoreboard players remove $target $value"}else{s"scoreboard players add $target ${-value}"}
}
case class ScoreboardReset(target: SBLink) extends IRTree{
    def getString(): String = s"scoreboard players reset $target"
}

trait StorageVariable
trait StorageValue
case class StorageStorage(target: String, key: String) extends StorageVariable with StorageValue{
    override def toString(): String = s"storage ${target} $key"
}
case class StorageEntity(target: String, key: String) extends StorageVariable with StorageValue{
    override def toString(): String = s"entity ${target} $key"
}
case class StorageBlock(target: String, key: String) extends StorageVariable with StorageValue{
    override def toString(): String = s"block ${target} $key"
}

case class StorageString(value: String) extends StorageValue{
    override def toString(): String = value
}
case class StorageScoreboard(key: SBLink, typ: String, scale: Double) extends StorageValue


case class StringSet(target: StorageVariable, value: String) extends IRTree{    
    def getString(): String = s"data modify $target set value $value"
}
case class StringCopy(target: StorageVariable, value: StorageVariable, start: Int = Int.MinValue, end: Int = Int.MaxValue) extends IRTree{
    def getString(): String = {
        if (start == Int.MinValue && end == Int.MaxValue){
            s"data modify $target set string $value"    
        }
        else if (end == Int.MaxValue){
            s"data modify $target set string $value $start"
        }
        else if (start == Int.MinValue){
            s"data modify $target set string $value 0 $end"
        }
        else{
            s"data modify $target set string $value $start $end"
        }
    }
}

case class StorageSet(target: StorageVariable, value: StorageValue) extends IRTree{
    def getString(): String = 
        value match{
            case StorageString(_) => s"data modify ${target} set value $value"
            case StorageStorage(_, _) => s"data modify ${target} set from $value"
            case StorageBlock(_, _) => s"data modify ${target} set from ${value.toString()}"
            case StorageEntity(_, _) => s"data modify ${target} set from ${value.toString()}"
            case StorageScoreboard(key2, typ, scale) => f"execute store result ${target} $typ $scale%.5f run scoreboard players get ${key2.toString()}"
        }
}
case class StorageAppend(target: StorageVariable, value: StorageValue) extends IRTree{
    def getString(): String = 
        value match{
            case StorageString(_) => s"data modify ${target} append value $value"
            case StorageStorage(_, _) => s"data modify ${target} append from $value"
            case StorageBlock(_, _) => s"data modify ${target} append from ${value.toString()}"
            case StorageEntity(_, _) => s"data modify ${target} append from ${value.toString()}"
            case StorageScoreboard(key, typ, scale) => throw new Exception("Cannot append scoreboard value")
        }
}
case class StoragePrepend(target: StorageVariable, value: StorageValue) extends IRTree{
    def getString(): String = 
        value match{
            case StorageString(_) => s"data modify ${target} prepend value $value"
            case StorageStorage(_, _) => s"data modify ${target} prepend from $value"
            case StorageBlock(_, _) => s"data modify ${target} prepend from ${value.toString()}"
            case StorageEntity(_, _) => s"data modify ${target} prepend from ${value.toString()}"
            case StorageScoreboard(key, typ, scale) => throw new Exception("Cannot prepend scoreboard value")
        }
}
case class StorageMerge(target: StorageVariable, value: StorageValue) extends IRTree{
    def getString(): String = 
        value match{
            case StorageString(_) => s"data modify ${target} merge value $value"
            case StorageStorage(_, _) => s"data modify ${target} merge from storage $value"
            case StorageBlock(_, _) => s"data modify ${target} merge from ${value.toString()}"
            case StorageEntity(_, _) => s"data modify ${target} merge from ${value.toString()}"
            case StorageScoreboard(key, typ, scale) => f"execute store result ${target} $typ $scale%.5f run scoreboard players get ${key.toString()}"
        }
}
case class StorageRemove(target: StorageVariable, value: StorageValue) extends IRTree{
    def getString(): String = 
        value match{
            case StorageString(_) => s"data modify ${target} remove value $value"
            case StorageStorage(_, _) => s"data modify ${target} remove from $value"
            case StorageBlock(_, _) => s"data modify ${target} remove from ${value.toString()}"
            case StorageEntity(_, _) => s"data modify ${target} remove from ${value.toString()}"
            case StorageScoreboard(key, typ, scale) => throw new Exception("Cannot remove scoreboard value")
        }
}
case class StorageRead(score: SBLink, target: StorageVariable, scale: Double = 1) extends IRTree{
    def getString(): String = 
        target match
            case StorageStorage(_, _) => f"execute store result score $score run data get $target $scale%.5f"
            case StorageBlock(_, _) => f"execute store result score $score run data get $target $scale%.5f"
            case StorageEntity(_, _) => f"execute store result score $score run data get $target $scale%.5f"
            case StorageScoreboard(key, typ, scale) => throw new Exception("Cannot read scoreboard value")
}


case class BlockCall(function: String, fullName: String, arg: String) extends IRTree{
    def getString(): String = 
        if arg == "" || arg == null then s"function $function"
        else s"function $function $arg"
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