package cmd.title

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
Show text in the title
"""
def lazy show(rawjson $text){
    if (Compiler.isJava()){
        /title @s title $text
    }
    else{
        /titleraw @s title $text
    }
}

"""
Show text in the subtitle
"""
def lazy showSubtitle(rawjson $text){
    if (Compiler.isJava()){
        /title @s subtitle $text
    }
    else{
        /titleraw @s subtitle $text
    }
}

"""
Show text in the title with priority `priority` and time `time`
"""
def lazy show(int priority, int time, rawjson text){
    if (currentPriority <= priority){
        show(text)
        time(0, time, 0)
        currentTime = time
        currentPriority = priority
    }
}

"""
Show text in the title with priority `priority` and time `time`
"""
def lazy show(int priority, int start, int time, int end, rawjson text){
    if (currentPriority <= priority){
        show(text)
        time(start, time, end)
        currentTime = time
        currentPriority = priority
    }
}

"""
Set the priority for the title to `priority` and time to `time`
"""
def lazy use(int priority, int time){
    if (currentPriority <= priority){
        currentTime = time
    }
    if (currentPriority <= priority){
        currentPriority = priority
    }
}

def lazy time(int $start, int $middle, int $end){
    /title @s times $start $middle $end
}