package animation.actionbar

import cmd.actionbar as actionbar

"""
Show text in the actionbar with animation according to `tick`. Return true if the animation is finished
"""
lazy bool force(int tick, rawjson text){
    lazy var index = 0
    foreach(sub in Compiler.substring(text)){
        if (index == tick)actionbar.force(sub)
        index += 1
    }
    if(index >= tick){
        return true
    }
    else{
        return false
    }
}

"""
Show text in the actionbar with priority `priority` and time `time` with animation according to `tick`. Return true if the animation is finished
"""
lazy bool show(int priority, int time, int tick, rawjson text){
    lazy var index = 0
    actionbar.use(priority, time)
    if (actionbar.currentPriority <= priority){
        foreach(sub in Compiler.substring(text)){
            if (index == tick)actionbar.force(sub)
            index += 1
        }
    }
    if(index >= tick){
        return true
    }
    else{
        return false
    }
}