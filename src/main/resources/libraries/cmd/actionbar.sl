package cmd.actionbar

import standard.int as int

scoreboard int currentPriority
scoreboard int currentTime

def private @playertick main(){
    currentTime:=0
    if (currentTime > 0){
        currentTime--
    }
    if (currentTime <= 0){
        currentPriority = int.minValue
    }
}

"""
Reset the priorities for the actionbar
"""
def reset(){
    currentPriority = int.minValue
    currentTime = 0
}

"""
Show text in the actionbar
"""
def lazy show(rawjson $text){
    if (Compiler.isJava()){
        /title @s actionbar $text
    }
    else{
        /titleraw @s actionbar $text
    }
}

"""
Show text in the actionbar with priority `priority` and time `time`
"""
def lazy show(int priority, int time, rawjson text){
    if (currentPriority <= priority){
        show(text)
        currentTime = time
        currentPriority = priority
    }
}

"""
Set the priority for the actionbar to `priority` and time to `time`
"""
def lazy use(int priority, int time){
    if (currentPriority <= priority){
        currentTime = time
    }
    if (currentPriority <= priority){
        currentPriority = priority
    }
}