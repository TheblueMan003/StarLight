package game.countdown

import cmd.actionbar as actionbar

enum TimerState{
    None, Play, Pause, Ended
}

struct Countdown{
    int h,m,s,t
    int dh,dm,ds,dt
    int kill, level,maxlevel, death
    TimerState stat
    bool display
    bool sKill, sDeath, sLevels
    bool global
    void=>void eventEnd
    bool hasEventEnd
    
    def __init__(int h, int m, int s, int t = 20){
        dh, dm, ds, dt = h, m, s, t
        this.h, this.m, this.s, this.t = h, m, s, t
        stat = TimerState.None
        global = false
        sKill, sDeath, sLevels = false
        display = false
        kill, level,maxlevel, death = 0
        hasEventEnd = false
    }
    
    def onEnd(void=>void event){
        eventEnd = event
        hasEventEnd = true
    }
    
    def start(){
        if (stat == TimerState.None){
            stat = TimerState.Play
        }
    }
    
    def pause(){
        if (stat == TimerState.Play){
            stat = TimerState.Pause
        }
    }
    
    def continue(){
        if (stat == TimerState.Pause){
            stat = TimerState.Play
        }
    }
    
    def stop(){
        if (stat == TimerState.Pause || stat == TimerState.Play){
            stat = TimerState.Ended
        }
    }
    
    int getMilisec(){
        return(t*5)
    }
    
    def setLevel(int l){
        level = l
    }
    def setMaxLevel(int l){
        maxlevel = l
    }
    def addDeath(){
        death ++
    }
    def addKill(){
        kill++
    }
    def reset(){
        h,m,s,t = dh, dm, ds, dt
        stat = TimerState.None
        kill, level, death = 0
    }
    def showDeath(){
        sDeath = true
    }
    def showKill(){
        sKill = true
    }
    def showLevel(){
        sLevels = true
    }
    def setGlobal(bool g = true){
        global = g
    }
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
                    as(@a) actionbar.force(h,":",m,":",s,".",ms)
                }
                if (!global){
                    actionbar.force(h,":",m,":",s,".",ms)
                }
            }
            if (!sKill && !sDeath && sLevels){
                actionbar.force("Time: ",h,":",m,":",s,".",ms," - Level: ",level,"/",maxlevel)
            }
            if (!sKill && sDeath && sLevels){
                actionbar.force("Death: ",death," - Time: ",h,":",m,":",s,".",ms," - Level: ",level,"/",maxlevel)
            }
            if (!sKill && sDeath && !sLevels){
                actionbar.force("Death: ",death," - Time: ",h,":",m,":",s,".",ms)
            }
            if (sKill && !sDeath && !sLevels){
                actionbar.force("Kill: ",kill," - Time: ",h,":",m,":",s,".",ms)
            }
            if (sKill && !sDeath && sLevels){
                actionbar.force("Kill: ",kill," - Time: ",h,":",m,":",s,".",ms," - Level: ",level,"/",maxlevel)
            }
            if (sKill && sDeath && !sLevels){
                actionbar.force("Kill: ",kill," - Death: ",death,"- Time: ",h,":",m,":",s,".",ms)
            }
        }
    }
    
    def tick(){
        __display__()

        if (stat == TimerState.Play){
            t -= 1
            if (t < 0){
                t = 19
                s-=1
            }
            if (s < 0){
                s = 59
                m-=1
            }
            if (m < 0){
                m = 59
                h -= 1
            }
            if (t <= 0 && s <= 0 && m <= 0 && h <= 0){
                stat = TimerState.Ended
                if (hasEventEnd){
                    eventEnd()
                }
            }
        }           
    }
}