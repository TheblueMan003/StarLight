package sl.IR

import scala.collection.mutable

class Interpreter(var files: List[IRFile]){
    val map = files.map(f => f.getName() -> f).toMap
    val scoreboards = mutable.Map[String, Int]()

    def printScoreboards(): Unit = {
        scoreboards.foreach(entry => {
            println(entry._1 + ": " + entry._2)
        })
    }

    def run(name: String, debug: Boolean):Unit = {
        map.get(name) match {
            case Some(file) => file.getContents().foreach(instr => run(instr)(IRContext((0,0,0), (0,0), null, "", debug)))
            case None => throw new Exception("File not found: " + name)
        }
    }
    def run(ir: IRTree)(implicit context: IRContext):Unit = {
        ir match{
            case ScoreboardAdd(target, value) => {
                val key = target.getKey()
                val current = scoreboards.getOrElse(key, 0)
                scoreboards.put(key, current + value)
                if (context.debug){
                    println(context.shift + "Scoreboard " + key + " = " + current + " + " + value + " = " + scoreboards.getOrElse(key, 0))
                }
            }
            case ScoreboardRemove(target, value) => {
                val key = target.getKey()
                val current = scoreboards.getOrElse(key, 0)
                scoreboards.put(key, current - value)
                if (context.debug){
                    println(context.shift + "Scoreboard " + key + " = " + current + " - " + value + " = " + scoreboards.getOrElse(key, 0))
                }
            }
            case ScoreboardSet(target, value) => {
                val key = target.getKey()
                scoreboards.put(key, value)
                if (context.debug){
                    println(context.shift + "Scoreboard " + key + " = " + value)
                }
            }
            case ScoreboardReset(target) => {
                val key = target.getKey()
                scoreboards.put(key, 0)
                if (context.debug){
                    println(context.shift + "Scoreboard " + key + " = 0")
                }
            }
            case ScoreboardOperation(target, operation, source) => {
                val key = target.getKey()
                val current = scoreboards.getOrElse(key, 0)
                val sourceValue = scoreboards.getOrElse(source.getKey(), 0)
                val newValue = operation match{
                    case "+=" => current + sourceValue
                    case "-=" => current - sourceValue
                    case "*=" => current * sourceValue
                    case "/=" => current / sourceValue
                    case "%=" => current % sourceValue
                    case "=" => sourceValue
                    case _ => throw new Exception("Unknown operation: " + operation)
                }
                scoreboards.put(key, newValue)
                if (context.debug){
                    println(context.shift + "Scoreboard " + key + " = " + current + " " + operation + " " + sourceValue + " = " + scoreboards.getOrElse(key, 0))
                }
            }
            case BlockCall(function, fullName) => {
                if (context.debug){
                    println(context.shift + "BlockCall " + fullName)
                }
                map.get(fullName) match {
                    case Some(file) => {
                        val newContext = context.withShift(context.shift + "  ")
                        file.getContents().foreach(instr => {
                            run(instr)(newContext)
                        })
                    }
                    case None => ()
                }
            }
            case IfScoreboard(left, op, right, statement, invert) => {
                val leftValue = scoreboards.getOrElse(left.getKey(), 0)
                val rightValue = scoreboards.getOrElse(right.getKey(), 0)

                if (context.debug){
                    println(context.shift + "IfScoreboard " + leftValue + " " + op + " " + rightValue)
                }

                val result = op match{
                    case "=" => leftValue == rightValue
                    case "!=" => leftValue != rightValue
                    case ">" => leftValue > rightValue
                    case "<" => leftValue < rightValue
                    case ">=" => leftValue >= rightValue
                    case "<=" => leftValue <= rightValue
                    case _ => throw new Exception("Unknown operation: " + op)
                }
                if((result && !invert) || (!result && invert)){
                    run(statement)(context.withShift(context.shift + " "))
                }
            }
            case IfScoreboardMatch(left, min, max, statement, invert) => {
                val leftValue = scoreboards.getOrElse(left.getKey(), 0)

                if (context.debug){
                    println(context.shift + "IfScoreboardMatch " + leftValue + " " + min + " " + max)
                }

                val result = leftValue >= min && leftValue <= max
                if((result && !invert) || (!result && invert)){
                    run(statement)(context.withShift(context.shift + " "))
                }
            }
            case CommandIR(statement) => {
                println(context.shift + "/" + statement)
            }
            case EmptyIR => ()
            case e: IRExecute if debug => run(e.getStatements)(context)
            case _ => throw new Exception("Unknown instruction: " + ir)
        }
    }
}

case class IRContext(position: (Int, Int, Int), rotation: (Int, Int), entity: String, shift: String, debug: Boolean = false){
    def withPosition(position: (Int, Int, Int)) = IRContext(position, rotation, entity, shift, debug)
    def withRotation(rotation: (Int, Int)) = IRContext(position, rotation, entity, shift, debug)
    def withEntity(entity: String) = IRContext(position, rotation, entity, shift, debug)
    def withShift(shift: String) = IRContext(position, rotation, entity, shift, debug)

    def shiftPosition(x: Int, y: Int, z: Int) = IRContext((position._1 + x, position._2 + y, position._3 + z), rotation, entity, shift, debug)
}