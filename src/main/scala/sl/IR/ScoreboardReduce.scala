package sl.IR

import scala.collection.mutable
import sl.Settings

class ScoreboardReduce(files: List[IRFile], access: mutable.Set[SBLink]){
    val map = files.map(f => f.getName() -> f).toMap
    var state = mutable.Map[SBLink, ScoreboardState]()
    var changed = false
    var globalChanged = false

    def run():(List[IRFile], Boolean) = {
        changed = true
        var iteration = 0
        while(changed){
            changed = false
            resetState()
            files.filterNot(f => f.isJsonFile() || f.deleted).map(f => {
                f.getContents().foreach(instr => {
                    computeState0(instr)(f)
                })
            })
            computeState1()
            if (Settings.optimizeVariableLocal && false){
                reduceState()
            }
            if (Settings.optimizeVariableGlobal){
                files.filterNot(f => f.isJsonFile() || f.deleted).map(f => {
                    f.setContents(f.getContents().map(instr => {
                        val ret = reduce(instr)(f)
                        if (ret != instr) {
                            changed = true
                        }
                        ret
                    }).filterNot(_ == EmptyIR))
                })
            }
            iteration += 1
            if (iteration > 10){
                changed = false
            }
            if (changed){
                globalChanged = true
            }
        }
        (files, globalChanged)
    }
    def resetState()={
        state = mutable.Map[SBLink, ScoreboardState]()
        files.foreach(f => f.resetScoreboard())
    }
    def getOrAdd(name: SBLink) = {
        if (name.entity.startsWith("@")) {
            new ScoreboardState(name, true)
        }
        else{
            state.getOrElseUpdate(name, new ScoreboardState(name, access.contains(name)))
        }
    }
    def computeState0(tree: IRTree)(implicit file: IRFile): Unit = {
        tree match {
            case ScoreboardAdd(target, value) => {
                val state = getOrAdd(target)
                state.addModified(file)
                state.incrementValue()
            }
            case ScoreboardSet(target, value) => {
                val state = getOrAdd(target)
                state.addModified(file)
                state.setValue(value)
            }
            case ScoreboardRemove(target, value) => {
                val state = getOrAdd(target)
                state.addModified(file)
                state.decrementValue()
            }
            case ScoreboardReset(target) => {
                val state = getOrAdd(target)
                state.addModified(file)
                state.setNull()
            }
            case ScoreboardOperation(target, operation, source) => {
                val state = getOrAdd(target)
                val sourceState = getOrAdd(source)
                state.addModified(file)
                sourceState.addAccessed(file)

                if (!source.entity.startsWith("@")) {
                    state.addOperation(SBOperation(operation, source))
                }
                else{
                    state.possibleValue = AnyValue
                }
            }
            case IfScoreboardMatch(left, min, max, statement, invert) => {
                getOrAdd(left).addAccessed(file)
                computeState0(statement)
            }
            case IfScoreboard(left, op, right, statement, invert) => {
                getOrAdd(left).addAccessed(file)
                getOrAdd(right).addAccessed(file)
                computeState0(statement)
            }
            case e: IRExecute => {
                computeState0(e.getStatements)
            }
            case BlockCall(function, fullName) => ()
            case _ => ()
        }
    }
    def computeState1(): Unit = {
        def setOrDirect(v1: Int, v2: Int) = {
            if (v1 == v2) DirectValue(v1)
            else SetValue(Set(v1, v2))
        }

        var lchange = true
        while(lchange){
            lchange = false
            state.foreach((name, state) => {
                state.operation.foreach(op => {
                    val sourceState = getOrAdd(op.source)
                    val prev = state.possibleValue
                    try{
                        (state.possibleValue, op.operation, sourceState.possibleValue) match{
                            case (DirectValue(v1), "+=", DirectValue(v2)) => state.possibleValue = setOrDirect(v1, v1 + v2)
                            case (DirectValue(v1), "-=", DirectValue(v2)) => state.possibleValue = setOrDirect(v1, v1 - v2)
                            case (DirectValue(v1), "*=", DirectValue(v2)) => state.possibleValue = setOrDirect(v1, v1 * v2)
                            case (DirectValue(v1), "/=", DirectValue(v2)) => state.possibleValue = setOrDirect(v1, v1 / v2)
                            case (DirectValue(v1), "%=", DirectValue(v2)) => state.possibleValue = setOrDirect(v1, v1 % v2)
                            case (DirectValue(v1), "=", DirectValue(v2)) => state.possibleValue = setOrDirect(v1, v2)

                            case (SetValue(v1), "=", SetValue(v2)) => state.possibleValue = SetValue(v1 ++ v2)
                            case (SetValue(v1), "=", DirectValue(v2)) => state.possibleValue = SetValue(v1 + v2)
                            case (DirectValue(v1), "=", SetValue(v2)) => state.possibleValue = SetValue(v2 + v1)

                            case (_, _, _) => state.possibleValue = AnyValue
                        }
                    }
                    catch{
                        case e: ArithmeticException => state.possibleValue = AnyValue
                    }
                    if (prev != state.possibleValue) lchange = true
                })
            })
        }
    }
    def reduce(tree: IRTree)(implicit file: IRFile): IRTree = {
        val ret = tree match {
            case ScoreboardAdd(target, value) => {
                val state = getOrAdd(target)
                if (state.accessCount == 0) EmptyIR else tree
            }
            case ScoreboardSet(target, value) => {
                val state = getOrAdd(target)
                if (state.accessCount == 0) EmptyIR else tree
            }
            case ScoreboardRemove(target, value) => {
                val state = getOrAdd(target)
                if (state.accessCount == 0) EmptyIR else tree
            }
            case ScoreboardReset(target) => {
                val state = getOrAdd(target)
                if (state.accessCount == 0) EmptyIR else tree
            }
            case ScoreboardOperation(target, operation, source) => {
                val state = getOrAdd(target)
                if (state.accessCount == 0) EmptyIR else tree
            }
            case IfScoreboardMatch(left, min, max, statement, invert) => {
                val state = getOrAdd(left)

                lazy val ntree = {
                    val s = reduce(statement)
                    if (s == EmptyIR) EmptyIR
                    else IfScoreboardMatch(left, min, max, s, invert)
                }
                state.possibleValue match
                    case DirectValue(value) => {
                        if ((value >= min && value <= max && !invert)) reduce(statement)
                        else if ((value < min || value > max) && invert) reduce(statement)
                        else EmptyIR
                    }
                    case SetValue(values) => {
                        val newValues = values.filter(v => v >= min && v <= max)
                        if (newValues.isEmpty && !invert) EmptyIR
                        else if (newValues.size == values.size && !invert) reduce(statement)
                        else if (newValues.isEmpty && invert) reduce(statement)
                        else if (newValues.size == values.size && invert) EmptyIR
                        else ntree
                    }
                    case _ => ntree
            }
            case IfScoreboard(left, op, right, statement, invert) => {
                if (left == right) {
                    tree
                }
                else if (!invert){
                    val lstate = getOrAdd(left)
                    val rstate = getOrAdd(right)

                    lazy val ntree = {
                        val s = reduce(statement)
                        if (s == EmptyIR) EmptyIR
                        else IfScoreboard(left, op, right, s, invert)
                    }

                    (lstate.possibleValue, op, rstate.possibleValue) match{
                        case (DirectValue(v1), "=", DirectValue(v2)) => {
                            if (v1 == v2) reduce(statement)
                            else EmptyIR
                        }
                        case (DirectValue(v1), "!=", DirectValue(v2)) => {
                            if (v1 != v2) reduce(statement)
                            else EmptyIR
                        }
                        case (DirectValue(v1), "<", DirectValue(v2)) => {
                            if (v1 < v2) reduce(statement)
                            else EmptyIR
                        }
                        case (DirectValue(v1), ">", DirectValue(v2)) => {
                            if (v1 > v2) reduce(statement)
                            else EmptyIR
                        }
                        case (DirectValue(v1), "<=", DirectValue(v2)) => {
                            if (v1 <= v2) reduce(statement)
                            else EmptyIR
                        }
                        case (DirectValue(v1), ">=", DirectValue(v2)) => {
                            if (v1 >= v2) reduce(statement)
                            else EmptyIR
                        }
                        case (SetValue(v1), "<", DirectValue(v2)) => {
                            if (v1.forall(_ < v2)) reduce(statement)
                            else ntree
                        }
                        case (SetValue(v1), ">", DirectValue(v2)) => {
                            if (v1.forall(_ > v2)) reduce(statement)
                            else ntree
                        }
                        case (SetValue(v1), "<=", DirectValue(v2)) => {
                            if (v1.forall(_ <= v2)) reduce(statement)
                            else ntree
                        }
                        case (SetValue(v1), ">=", DirectValue(v2)) => {
                            if (v1.forall(_ >= v2)) reduce(statement)
                            else ntree
                        }
                        case (DirectValue(v1), "<", SetValue(v2)) => {
                            if (v2.forall(_ > v1)) reduce(statement)
                            else ntree
                        }
                        case (DirectValue(v1), ">", SetValue(v2)) => {
                            if (v2.forall(_ < v1)) reduce(statement)
                            else ntree
                        }
                        case (DirectValue(v1), "<=", SetValue(v2)) => {
                            if (v2.forall(_ >= v1)) reduce(statement)
                            else ntree
                        }
                        case (DirectValue(v1), ">=", SetValue(v2)) => {
                            if (v2.forall(_ <= v1)) reduce(statement)
                            else ntree
                        }
                        case other => ntree
                    }
                }
                else tree
            }
            case e: IRExecute => {
                reduce(e.getStatements) match {
                    case EmptyIR => EmptyIR
                    case other => e.withStatements(other)
                }
            }
            case other => other
        }
        if (!ret.equals(tree)) {
            changed = true
        }
        ret
    }
    def reduceState(): Unit = {
        files.filterNot(f => f.isJsonFile() || f.deleted).foreach(file => {
            var localState = mutable.Map[SBLink, ScoreboardState]()
            file.setContents(reduceStateForBlock(file.getContents())(localState))
        })
    }
    def reduceStateForBlock(lst: List[IRTree])(implicit localState: mutable.Map[SBLink, ScoreboardState]): List[IRTree]={
        def lgetOrAdd(name: SBLink) = {
            if (name.entity.startsWith("@")) {
                new ScoreboardState(name, true)
            }
            else{
                localState.getOrElseUpdate(name, new ScoreboardState(name, access.contains(name)))
            }
        }
        def reduceOne(tree: IRTree)(implicit inIf: Boolean): IRTree = {
            val ret = tree match{
                case ScoreboardSet(target, value) => {
                    val state = lgetOrAdd(target)
                    state.possibleValue match
                        case DirectValue(v) => state.possibleValue = if(inIf){SetValue(Set(v, value))}else{DirectValue(value)}
                        case SetValue(v) => state.possibleValue = if(inIf){SetValue(v + value)}else{DirectValue(value)}
                        case _ => state.possibleValue = if(inIf){AnyValue}else{DirectValue(value)}
                    tree
                }
                case ScoreboardAdd(target, value) => {
                    val state = lgetOrAdd(target)
                    state.possibleValue match
                        case DirectValue(v) => state.possibleValue = if(inIf){SetValue(Set(v, v+value))}else{DirectValue(v + value)}
                        case SetValue(v) => state.possibleValue = if(inIf){SetValue(v.map(_ + value) ++ v)}else{SetValue(v.map(_ + value))}
                        case _ => state.possibleValue = AnyValue
                    
                    (state.possibleValue) match
                        case DirectValue(v)=> ScoreboardSet(target, v)
                        case other => tree
                }
                case ScoreboardRemove(target, value) => {
                    val state = lgetOrAdd(target)
                    state.possibleValue match
                        case DirectValue(v) => state.possibleValue = if(inIf){SetValue(Set(v, v-value))}else{DirectValue(v - value)}
                        case SetValue(v) => state.possibleValue = if(inIf){SetValue(v.map(_ - value) ++ v)}else{SetValue(v.map(_ - value))}
                        case _ => state.possibleValue = AnyValue
                    
                    (state.possibleValue) match
                        case DirectValue(v)=> ScoreboardSet(target, v)
                        case other => tree
                }
                case IfScoreboardMatch(left, min, max, statement, invert) => {
                    val state = lgetOrAdd(left)
                    state.possibleValue match
                        case DirectValue(v) => {
                            if (v >= min && v <= max && !invert) reduceOne(statement)
                            else if ((v < min || v > max) && !invert) EmptyIR
                            else if ((v >= min && v <= max) && invert) EmptyIR
                            else reduceOne(statement)
                        }
                        case SetValue(v) => {
                            val newValues = v.filter(v => v >= min && v <= max)
                            if (newValues.isEmpty && !invert) EmptyIR
                            else if (newValues.size == v.size && !invert) reduceOne(statement)
                            else if (newValues.isEmpty && invert) reduceOne(statement)
                            else if (newValues.size == v.size && invert) EmptyIR
                            else{
                                state.possibleValue = SetValue(newValues)
                                IfScoreboardMatch(left, min, max, reduceOne(statement)(true), invert)
                            }
                        }
                        case _ => {
                            IfScoreboardMatch(left, min, max, reduceOne(statement)(true), invert)
                        }
                }
                case IfScoreboard(left, op, right, statement, invert) => {
                    if (left == right){
                        IfScoreboard(left, op, right, reduceOne(statement)(true), invert)
                    }
                    else if (!invert){
                        val lstate = lgetOrAdd(left)
                        val rstate = lgetOrAdd(right)
                        
                        (lstate.possibleValue, op, rstate.possibleValue) match{
                            case (DirectValue(v1), "=", DirectValue(v2)) => {
                                if (v1 == v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), "!=", DirectValue(v2)) => {
                                if (v1 != v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), "<", DirectValue(v2)) => {
                                if (v1 < v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), ">", DirectValue(v2)) => {
                                if (v1 > v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), "<=", DirectValue(v2)) => {
                                if (v1 <= v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), ">=", DirectValue(v2)) => {
                                if (v1 >= v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (SetValue(v1), "<", DirectValue(v2)) => {
                                if (v1.forall(_ < v2)) reduceOne(statement)
                                else if (v1.forall(_ >= v2)) EmptyIR
                                else IfScoreboard(left, "<", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">", DirectValue(v2)) => {
                                if (v1.forall(_ > v2)) reduceOne(statement)
                                else if (v1.forall(_ <= v2)) EmptyIR
                                else IfScoreboard(left, ">", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), "<=", DirectValue(v2)) => {
                                if (v1.forall(_ <= v2)) reduceOne(statement)
                                else if (v1.forall(_ > v2)) EmptyIR
                                else IfScoreboard(left, "<=", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">=", DirectValue(v2)) => {
                                if (v1.forall(_ >= v2)) reduceOne(statement)
                                else if (v1.forall(_ < v2)) EmptyIR
                                else IfScoreboard(left, ">=", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), "<", SetValue(v2)) => {
                                if (v2.forall(_ > v1)) reduceOne(statement)
                                else if (v2.forall(_ <= v1)) EmptyIR
                                else IfScoreboard(left, "<", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), ">", SetValue(v2)) => {
                                if (v2.forall(_ < v1)) reduceOne(statement)
                                else if (v2.forall(_ >= v1)) EmptyIR
                                else IfScoreboard(left, ">", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), "<=", SetValue(v2)) => {
                                if (v2.forall(_ >= v1)) reduceOne(statement)
                                else if (v2.forall(_ < v1)) EmptyIR
                                else IfScoreboard(left, "<=", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), ">=", SetValue(v2)) => {
                                if (v2.forall(_ <= v1)) reduceOne(statement)
                                else if (v2.forall(_ > v1)) EmptyIR
                                else IfScoreboard(left, ">=", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), "<", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 < v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 >= v2))) EmptyIR
                                else IfScoreboard(left, "<", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 > v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 <= v2))) EmptyIR
                                else IfScoreboard(left, ">", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), "<=", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 <= v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 > v2))) EmptyIR
                                else IfScoreboard(left, "<=", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">=", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 >= v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 < v2))) EmptyIR
                                else IfScoreboard(left, ">=", right, reduceOne(statement)(true), false)
                            }
                            case _ => {
                                IfScoreboard(left, op, right, reduceOne(statement)(true), invert)
                            }
                        }
                    }
                    else{
                        val lstate = lgetOrAdd(left)
                        val rstate = lgetOrAdd(right)
                        
                        (lstate.possibleValue, op, rstate.possibleValue) match{
                            case (DirectValue(v1), "=", DirectValue(v2)) => {
                                if (v1 != v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), "!=", DirectValue(v2)) => {
                                if (v1 == v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), "<", DirectValue(v2)) => {
                                if (v1 >= v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), ">", DirectValue(v2)) => {
                                if (v1 <= v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), "<=", DirectValue(v2)) => {
                                if (v1 > v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (DirectValue(v1), ">=", DirectValue(v2)) => {
                                if (v1 < v2) reduceOne(statement)
                                else EmptyIR
                            }
                            case (SetValue(v1), "<", DirectValue(v2)) => {
                                if (v1.forall(_ >= v2)) reduceOne(statement)
                                else if (v1.forall(_ < v2)) EmptyIR
                                else IfScoreboard(left, "<", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">", DirectValue(v2)) => {
                                if (v1.forall(_ <= v2)) reduceOne(statement)
                                else if (v1.forall(_ > v2)) EmptyIR
                                else IfScoreboard(left, ">", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), "<=", DirectValue(v2)) => {
                                if (v1.forall(_ > v2)) reduceOne(statement)
                                else if (v1.forall(_ <= v2)) EmptyIR
                                else IfScoreboard(left, "<=", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">=", DirectValue(v2)) => {
                                if (v1.forall(_ < v2)) reduceOne(statement)
                                else if (v1.forall(_ >= v2)) EmptyIR
                                else IfScoreboard(left, ">=", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), "<", SetValue(v2)) => {
                                if (v2.forall(_ <= v1)) reduceOne(statement)
                                else if (v2.forall(_ > v1)) EmptyIR
                                else IfScoreboard(left, "<", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), ">", SetValue(v2)) => {
                                if (v2.forall(_ >= v1)) reduceOne(statement)
                                else if (v2.forall(_ < v1)) EmptyIR
                                else IfScoreboard(left, ">", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), "<=", SetValue(v2)) => {
                                if (v2.forall(_ < v1)) reduceOne(statement)
                                else if (v2.forall(_ >= v1)) EmptyIR
                                else IfScoreboard(left, "<=", right, reduceOne(statement)(true), false)
                            }
                            case (DirectValue(v1), ">=", SetValue(v2)) => {
                                if (v2.forall(_ > v1)) reduceOne(statement)
                                else if (v2.forall(_ <= v1)) EmptyIR
                                else IfScoreboard(left, ">=", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), "<", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 >= v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 < v2))) EmptyIR
                                else IfScoreboard(left, "<", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 <= v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 > v2))) EmptyIR
                                else IfScoreboard(left, ">", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), "<=", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 > v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 <= v2))) EmptyIR
                                else IfScoreboard(left, "<=", right, reduceOne(statement)(true), false)
                            }
                            case (SetValue(v1), ">=", SetValue(v2)) => {
                                if (v1.forall(v1 => v2.forall(v2 => v1 < v2))) reduceOne(statement)
                                else if (v1.forall(v1 => v2.forall(v2 => v1 >= v2))) EmptyIR
                                else IfScoreboard(left, ">=", right, reduceOne(statement)(true), false)
                            }
                            case _ => {
                                IfScoreboard(left, op, right, reduceOne(statement)(true), invert)
                            }
                        }
                    }
                }
                case ScoreboardOperation(target, operation, source) => {
                    val lstate = lgetOrAdd(target)
                    val rstate = lgetOrAdd(source)
                    try{
                        (lstate.possibleValue, operation, rstate.possibleValue) match{
                            case (DirectValue(v1), "+=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(Set(v1, v1 + v2))}else{DirectValue(v1 + v2)}
                            case (DirectValue(v1), "-=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(Set(v1, v1 - v2))}else{DirectValue(v1 - v2)}
                            case (DirectValue(v1), "*=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(Set(v1, v1 * v2))}else{DirectValue(v1 * v2)}
                            case (DirectValue(v1), "/=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(Set(v1, v1 / v2))}else{DirectValue(v1 / v2)}
                            case (DirectValue(v1), "%=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(Set(v1, v1 % v2))}else{DirectValue(v1 % v2)}
                            case (_, "=", DirectValue(v2)) => lstate.possibleValue = if (inIf){AnyValue}else{DirectValue(v2)}
                            case (_, "=", SetValue(v2)) => lstate.possibleValue = if (inIf){AnyValue}else{SetValue(v2)}
                            case (DirectValue(v1), "+=", SetValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v2.map(_ + v1)+v1)}else{SetValue(v2.map(_ + v1))}
                            case (DirectValue(v1), "-=", SetValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v2.map(_ - v1)+v1)}else{SetValue(v2.map(_ - v1))}
                            case (DirectValue(v1), "*=", SetValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v2.map(_ * v1)+v1)}else{SetValue(v2.map(_ * v1))}
                            case (DirectValue(v1), "/=", SetValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v2.map(_ / v1)+v1)}else{SetValue(v2.map(_ / v1))}
                            case (DirectValue(v1), "%=", SetValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v2.map(_ % v1)+v1)}else{SetValue(v2.map(_ % v1))}
                            case (SetValue(v1), "+=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v1.map(_ + v2)+v2)}else{SetValue(v1.map(_ + v2))}
                            case (SetValue(v1), "-=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v1.map(_ - v2)+v2)}else{SetValue(v1.map(_ - v2))}
                            case (SetValue(v1), "*=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v1.map(_ * v2)+v2)}else{SetValue(v1.map(_ * v2))}
                            case (SetValue(v1), "/=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v1.map(_ / v2)+v2)}else{SetValue(v1.map(_ / v2))}
                            case (SetValue(v1), "%=", DirectValue(v2)) => lstate.possibleValue = if (inIf){SetValue(v1.map(_ % v2)+v2)}else{SetValue(v1.map(_ % v2))}
                            
                            case _ => lstate.possibleValue = AnyValue
                        }
                    }
                    catch{
                        case e: ArithmeticException => lstate.possibleValue = AnyValue
                    }
                    (lstate.possibleValue, operation, rstate.possibleValue) match
                        case (DirectValue(v), "=", _)=> ScoreboardSet(target, v)
                        case (_, "+=", DirectValue(v))=> ScoreboardAdd(target, v)
                        case (_, "-=", DirectValue(v))=> ScoreboardRemove(target, v)
                        case other => tree
                }
                case BlockCall(function, fullName) => {
                    map.get(fullName) match
                        case Some(block) => {
                            block.scoreboardModified.foreach(name => {
                                val state = lgetOrAdd(name)
                                state.possibleValue = AnyValue
                            })
                            access.foreach(name => {
                                val state = lgetOrAdd(name)
                                state.possibleValue = AnyValue
                            })
                            tree
                        }
                        case None => tree
                }
                case e: ExecuteIR => {
                    e.withStatements(reduceOne(e.getStatements)(true))
                }
                case other => {
                    access.foreach(name => {
                        val state = lgetOrAdd(name)
                        state.possibleValue = AnyValue
                    })
                    other
                }
            }
            if (ret != tree){
                changed = true
            }
            ret
        }

        if (lst.isEmpty) return lst
        reduceOne(lst.head)(false) :: reduceStateForBlock(lst.tail)
    }
}

class ScoreboardState(sblink: SBLink,forced: Boolean = false){
    var accessed = mutable.Set[IRFile]()
    var modified = mutable.Set[IRFile]()
    var possibleValue: ScoreboardValue = if forced then AnyValue else UnknownValue
    var accessedValue = mutable.Set[ScoreboardValue]()
    var operation = mutable.Set[SBOperation]()

    def addAccessed(file: IRFile): Unit ={
        accessed += file
        file.addScoreboardAccess(sblink)
        accessedValue.add(possibleValue)
    }
    def addModified(file: IRFile): Unit ={
        modified += file
        file.addScoreboardModified(sblink)
    }

    def incrementValue(): Unit ={
        possibleValue = AnyValue
    }
    def decrementValue(): Unit ={
        possibleValue = AnyValue
    }
    def setNull(): Unit ={
        possibleValue = AnyValue
    }
    def setValue(value: Int): Unit ={
        possibleValue = (
            possibleValue match
                case UnknownValue => DirectValue(value)
                case DirectValue(v) if v == value => DirectValue(v)
                case DirectValue(v) => SetValue(Set(v, value))
                case SetValue(set) => SetValue(set + value)
                case _ => AnyValue
        )
    }
    def addOperation(operation: SBOperation): Unit ={
        this.operation += operation
    }
    def getPossibleValue(): ScoreboardValue ={
        possibleValue
    }

    def accessCount = if forced then 1 else accessed.size
    def modifyCount = if forced then 1 else modified.size

    override def toString(): String = {
        s"ScoreboardState($accessCount, $modifyCount, $possibleValue, $operation)"
    }
}

trait ScoreboardValue{
}
case class DirectValue(value: Int) extends ScoreboardValue
case class SetValue(value: Set[Int]) extends ScoreboardValue
case object AnyValue extends ScoreboardValue
case object UnknownValue extends ScoreboardValue

case class SBOperation(operation: String, source: SBLink) extends ScoreboardValue