package animation.bossbar

import cmd.bossbar as bs

"""
Show text in the bossbar with animation according to `tick`. Return true if the animation is finished
"""
lazy bool show(bs.Bossbar bar, int tick, rawjson text){
    lazy var index = 0
    foreach(sub in Compiler.substring(text)){
        if (index == tick)bs.setName(bar.id, sub)
        index += 1
    }
    if(index >= tick){
        return true
    }
    else{
        return false
    }
}