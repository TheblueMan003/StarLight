package animation.title

import cmd.title as title

"""
Clear the title and subtitle
"""
def lazy clear(){
    title.clear()
}

"""
Show text in the title with animation according to `tick`. Return true if the animation is finished
"""
lazy bool force(int tick, rawjson text){
    lazy var index = 0
    title.time(0, 10, 0)
    foreach(sub in Compiler.substring(text)){
        if (index == tick)title.force(sub)
        index += 1
    }
    if (tick >= index)title.force(text)
    return index >= tick
}

"""
Show text in the subtitle with animation according to `tick`. Return true if the animation is finished
"""
lazy bool showSubtitle(int tick, rawjson text){
    lazy var index = 0
    foreach(sub in Compiler.substring(text)){
        if (index == tick)title.showSubtitle(sub)
        index += 1
    }
    if (tick >= index)title.showSubtitle(text)
    if(index >= tick){
        return true
    }
    else{
        return false
    }
}

"""
Show text in the title with priority `priority` and time `time` with animation according to `tick`. Return true if the animation is finished
"""
lazy bool show(int priority, int time, int tick, rawjson text){
    lazy var index = 0
    title.time(0, 10, 0)
    title.use(priority, time)
    if (title.currentPriority <= priority){
        foreach(sub in Compiler.substring(text)){
            if (index == tick)title.force(sub)
            index += 1
        }
    }
    if (tick >= index)title.force(text)
    if(index >= tick){
        return true
    }
    else{
        return false
    }
}