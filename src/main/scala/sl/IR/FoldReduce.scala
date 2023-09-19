package sl.IR

import scala.collection.mutable

class FoldReduce(var files: List[IRFile]){
    val map = files.map(f => f.getName() -> f).toMap
    var globalChanged = false
    var state: mutable.Map[SBLink, ScoreboardState] = null

    def run(): (List[IRFile], Boolean) ={
        files.foreach(f => f.setContents(f.getContents().map(map).foldLeft(List[IRTree]())(fold)))
        (files, globalChanged)
    }
    def map(op: IRTree): IRTree = {
        op match{
            case IfScoreboard(left, ">=" | ">", right, ScoreboardOperation(sb1, "=", sb3), false) if left == sb1 && right == sb3 => {
                ScoreboardOperation(sb1, "<", sb3)
            }
            case IfScoreboard(left, "<=" | "<", right, ScoreboardOperation(sb1, "=", sb3), false) if left == sb1 && right == sb3 => {
                ScoreboardOperation(sb1, ">", sb3)
            }
            case IfScoreboard(left, ">=" | ">", right, ScoreboardOperation(sb1, "=", sb3), false) if left == sb3 && right == sb1 => {
                ScoreboardOperation(sb1, ">", sb3)
            }
            case IfScoreboard(left, "<=" | "<", right, ScoreboardOperation(sb1, "=", sb3), false) if left == sb3 && right == sb1 => {
                ScoreboardOperation(sb1, "<", sb3)
            }
            case e @ ExecuteIR(block, statement) => {
                e.withStatements(map(e.getStatements))
            }
            case other => other
        }
    }
    def fold(acc: List[IRTree], right: IRTree): List[IRTree] = {
        if acc.isEmpty then return List(right)
        val left = acc.last
        acc.dropRight(1):::(
            (left, right) match {
                case (ScheduleCall(function, fullName, time), ScheduleClear(function2, fullName2)) => {
                    if (fullName == fullName2){
                        globalChanged = true
                        List[IRTree]()
                    } else {
                        List(left, right)
                    }
                }
                case (ScoreboardSet(sb1: SBLink, _), op2 @ ScoreboardSet(sb2: SBLink, _)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardAdd(sb1: SBLink, _), op2 @ ScoreboardSet(sb2: SBLink, _)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardRemove(sb1: SBLink, _), op2 @ ScoreboardSet(sb2: SBLink, _)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardReset(sb1: SBLink), op2 @ ScoreboardSet(sb2: SBLink, _)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardOperation(sb1: SBLink, _, sb3: SBLink), op2 @ ScoreboardSet(sb2: SBLink, _)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)

                case (ScoreboardSet(sb1: SBLink, _), op2 @ ScoreboardOperation(sb2: SBLink, "=", sb4: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardAdd(sb1: SBLink, _), op2 @ ScoreboardOperation(sb2: SBLink, "=", sb4: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardRemove(sb1: SBLink, _), op2 @ ScoreboardOperation(sb2: SBLink, "=", sb4: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardReset(sb1: SBLink), op2 @ ScoreboardOperation(sb2: SBLink, "=", sb4: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardOperation(sb1: SBLink, _, sb3: SBLink), op2 @ ScoreboardOperation(sb2: SBLink, "=", sb4: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)

                case (ScoreboardSet(sb1: SBLink, _), op2 @ ScoreboardReset(sb2: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardAdd(sb1: SBLink, _), op2 @ ScoreboardReset(sb2: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardRemove(sb1: SBLink, _), op2 @ ScoreboardReset(sb2: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardReset(sb1: SBLink), op2 @ ScoreboardReset(sb2: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)
                case (ScoreboardOperation(sb1: SBLink, _, sb3: SBLink), op2 @ ScoreboardReset(sb2: SBLink)) if sb1 == sb2 =>
                    globalChanged = true
                    List(op2)



                case (StorageSet(target1, value1), StorageSet(target2, value2)) if target1 == target2 => {
                    globalChanged = true
                    List(StorageSet(target1, value2))
                }
                case (StorageAppend(target1, value1), StorageSet(target2, value2)) if target1 == target2 => {
                    globalChanged = true
                    List(StorageSet(target1, value2))
                }
                case (StoragePrepend(target1, value1), StorageSet(target2, value2)) if target1 == target2 => {
                    globalChanged = true
                    List(StorageSet(target1, value2))
                }
                case (StorageRemove(target1, value1), StorageSet(target2, value2)) if target1 == target2 => {
                    globalChanged = true
                    List(StorageSet(target1, value2))
                }

                case (op1 @ ScoreboardSet(sb1: SBLink, value), IfScoreboardMatch(sb2: SBLink, min, max, stm, invert)) 
                    if sb1 == sb2 && ((min <= value && value <= max && !invert) || (!(min <= value && value <= max) && invert)) =>
                        globalChanged = true
                        List(op1, stm)

                case _ => List(left, right)
        })
    }
}