package game.Timer

import cmd.actionbar as actionbar

"""
TimerState
"""
enum TimerState{
    None, Play, Pause, Ended
}


"""
Structure of Timer
"""
struct Timer{
    int h, m, s, t
    int kill, level, maxlevel, death
    TimerState stat
    bool display
    bool sKill, sDeath, sLevels, sCheat
    bool global
    lazy int colorLabel = "white"
    lazy int colorField = "gray"
    lazy int colorCheatLabel = "red"
    lazy int colorCheatField = "dark_red"
    
    def lazy __init__(){
        h,m,s,t = 0
        stat = TimerState.None
        global = false
        sKill, sDeath, sLevels, sCheat, display = false
        kill, level,maxlevel, death = 0
    }
    def lazy __init__(int d1, int d2){
        colorLabel = d1
        colorField = d2
        
        h, m, s, t = 0
        stat = None
        global = false
        sKill, sDeath, sLevels, sCheat, display = false
        kill, level,maxlevel, death = 0
    }
    
    def lazy __init__(int c1, int c2, int c3, int c4){
        colorLabel = c1
        colorField = c2
        colorCheatLabel = c3
        colorCheatField = c4
        h, m, s, t = 0
        stat = TimerState.None
        global = false
        sKill, sDeath, sLevels, sCheat, display = false
        kill, level,maxlevel, death = 0
    }
    """
    Reset the time of timer
    """
    def resetTime(){
        h,m,s,t = 0
    }
        
    """
    Start the timer
    """
    def start(){
        if (stat == TimerState.None){
            stat = TimerState.Play
        }
    }
    
    """
    Pause the timer
    """
    def pause(){
        if (stat == TimerState.Play){
            stat = TimerState.Pause
        }
    }
    
    """
    Unpause the timer
    """
    def continue(){
        if (stat == TimerState.Pause){
            stat = TimerState.Play
        }
    }
    
    """
    Stop the timer
    """
    def stop(){
        if (stat == TimerState.Pause || stat == TimerState.Play){
            stat = TimerState.Ended
        }
    }
        
    """
    Return the time in ticks
    """
    int getTime(){
        return (((h*60+m)*60+s)*20+t)
    }
        
    """
    Return the millisecond of the timer
    """
    int getMilisec(){
        return(t*5)
    }
    
    """
    Set the level of the timer
    """
    def setLevel(int l){
        if (l < maxlevel){
            level = l
        }
        else{
            level = maxlevel
        }
    }

    """
    Set the max level of the timer
    """
    def setMaxLevel(int l){
        maxlevel = l
    }

    """
    Add a death to the timer
    """
    def addDeath(){
        death ++
    }

    """
    Add a kill to the timer
    """
    def addKill(){
        kill++
    }

    """
    Reset the timer
    """
    def reset(){
        h,m,s,t = 0
        stat = TimerState.None
        kill, level, death = 0
    }

    """
    Set if the death should be shown
    """
    def showDeath(bool d = true){
        sDeath = d
    }

    """
    Set if the kill should be shown
    """
    def showKill(bool d = true){
        sKill = d
    }

    """
    Set if the cheat state should be shown
    """
    def showCheat(bool d = true){
        sCheat = d
    }

    """
    Set if the levels should be shown
    """
    def showLevel(bool d = true){
        sLevels = d
    }

    """
    Set if the timer is global
    """
    def setGlobal(bool g = true){
        global = g
    }

    """
    Set if the timer should be displayed
    """
    def setDisplay(bool d = true){
        display = d
    }
    
    def private __display__(){
        int ms = getMilisec()
        if (display){
            if (global) as(@s) actionbar.use(10,10)
            actionbar.use(10,10)
            if (!sKill && !sDeath && !sLevels){
                if (global){
                    if (m < 10 && s < 10 && ms < 10){
                        as(@a) actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        as(@a) actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        as(@a) actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        as(@a) actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        as(@a) actionbar.force((h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        as(@a) actionbar.force((h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        as(@a) actionbar.force((h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        as(@a) actionbar.force((h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                }
                if (!global){
                    if (m < 10 && s < 10 && ms < 10){
                        actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        actionbar.force((h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        actionbar.force((h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        actionbar.force((h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        actionbar.force((h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        actionbar.force((h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                }
            }
            if (!sKill && !sDeath && sLevels){
                if (sCheat){
                    if (m < 10 && s < 10 && ms < 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":0",colorCheatLabel),(s,colorCheatLabel),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":0",colorCheatLabel),(s,colorCheatLabel),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":",colorCheatLabel),(s,colorCheatLabel),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":",colorCheatLabel),(s,colorCheatLabel),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":0",colorCheatLabel),(s,colorCheatLabel),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":0",colorCheatLabel),(s,colorCheatLabel),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":",colorCheatLabel),(s,colorCheatLabel),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":",colorCheatLabel),(s,colorCheatLabel),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                }
                if (!sCheat){
                    if (m < 10 && s < 10 && ms < 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorLabel),(s,colorLabel),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorLabel),(s,colorLabel),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorLabel),(s,colorLabel),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorLabel),(s,colorLabel),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorLabel),(s,colorLabel),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorLabel),(s,colorLabel),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorLabel),(s,colorLabel),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorLabel),(s,colorLabel),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                }
            }
            if (!sKill && sDeath && sLevels){
                if (sCheat){
                    if (m < 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField),(" - Level: ",colorCheatLabel),(level,colorCheatField),("/",colorCheatField),(maxlevel,colorCheatField))
                    }
                }
                if (!sCheat){
                    if (m < 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                    }
                }
            }
            if (!sKill && sDeath && !sLevels){
                if (sCheat){
                    if (m < 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":0",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":0",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".0",colorCheatField),(ms,colorCheatField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorCheatLabel),(death,colorCheatField),(" - Time: ",colorCheatLabel),(h,colorCheatField),(":",colorCheatField),(m,colorCheatField),(":",colorCheatField),(s,colorCheatField),(".",colorCheatField),(ms,colorCheatField))
                    }
                }
                if (!sCheat){
                    if (m < 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m < 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m < 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m < 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s < 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s < 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms < 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                    }
                    if (m >= 10 && s >= 10 && ms >= 10){
                        actionbar.force(("Death: ",colorLabel),(death,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                    }
                }
            }
            if (sKill && !sDeath && !sLevels){
                if (m < 10 && s < 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m < 10 && s < 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
                if (m < 10 && s >= 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m < 10 && s >= 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
                if (m >= 10 && s < 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m >= 10 && s < 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
                if (m >= 10 && s >= 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m >= 10 && s >= 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
            }
            if (sKill && !sDeath && sLevels){
                if (m < 10 && s < 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
                if (m < 10 && s < 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
                if (m < 10 && s >= 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
                if (m < 10 && s >= 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
                if (m >= 10 && s < 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
                if (m >= 10 && s < 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
                if (m >= 10 && s >= 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
                if (m >= 10 && s >= 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField),(" - Level: ",colorLabel),(level,colorField),("/",colorField),(maxlevel,colorField))
                }
            }
            if (sKill && sDeath && !sLevels){
                if (m < 10 && s < 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m < 10 && s < 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
                if (m < 10 && s >= 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m < 10 && s >= 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":0",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
                if (m >= 10 && s < 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m >= 10 && s < 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":0",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
                if (m >= 10 && s >= 10 && ms < 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".0",colorField),(ms,colorField))
                }
                if (m >= 10 && s >= 10 && ms >= 10){
                    actionbar.force(("Kill: ",colorLabel),(kill,colorField),(" - Death: ",colorLabel),(death,colorField),("- Time: ",colorLabel),(h,colorField),(":",colorField),(m,colorField),(":",colorField),(s,colorField),(".",colorField),(ms,colorField))
                }
            }
        }
    }
    
    """
    Return the current time in ticks
    """
    int getTotalTick(){
        int tt = t + 20*(s + 60 * (m + (60 * h)))
        return(tt)
    }
    
    """
    Tick the timer
    """
    def tick(){
        __display__()
        
        if (stat == TimerState.Play){
            t ++
            if (t >= 20){
                t = 0
                s++
            }
            if (s >= 60){
                s = 0
                m++
            }
            if (m >= 60){
                m = 0
                h ++
            }
        }
    }
}