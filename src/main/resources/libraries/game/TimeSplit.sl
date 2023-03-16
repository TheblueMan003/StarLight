package game.TimeSplit

import standard.int as int
import standard::tell
import math

private lazy int level_number = 100

"""
Set the number of levels in the game
"""
def setLevelNumber(int nb){
    level_number = nb
}

"""
A template to manage time split in a game
"""
template TimeSplit{
    scoreboard int[level_number] best_times
    scoreboard int[level_number] current_times
    scoreboard int best_time, current_lvl
    scoreboard bool HasTime
    
    """
    Init the current player
    """
    def start(){
        current_lvl = -1
    }
        
    """
    Reset everything
    """
    def @reset reset(){
        with(@a){
            best_times = null
            current_times = null
            best_time = null
            current_lvl = null
            HasTime = null
        }
    }

    """
    Show the time difference between the best time and the current time
    """
    def show_time(int time, int ptime){
        int delta = ptime-time
        int dp = math.abs(delta)
        int t = (dp % 20)*5
        int s = (dp/20)%60
        int m = (dp/(20*60))%60
        int h = (dp/(20*60*60))
        if (delta >= 0){
            if (m >= 10 && s >= 10 && t >= 10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":" , "green"),(s, "green"),(".",  "green"),(t, "green"))}
            if (m >= 10 && s >= 10 && t <  10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":" , "green"),(s, "green"),(".0", "green"),(t, "green"))}
            if (m >= 10 && s <  10 && t >= 10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":0", "green"),(s, "green"),(".",  "green"),(t, "green"))}
            if (m >= 10 && s <  10 && t <  10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":0", "green"),(s, "green"),(".0", "green"),(t, "green"))}
            if (m <  10 && s >= 10 && t >= 10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":" , "green"),(s, "green"),(".",  "green"),(t, "green"))}
            if (m <  10 && s >= 10 && t <  10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":" , "green"),(s, "green"),(".0", "green"),(t, "green"))}
            if (m <  10 && s <  10 && t >= 10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":0", "green"),(s, "green"),(".",  "green"),(t, "green"))}
            if (m <  10 && s <  10 && t <  10){tell(("-","green"),(h, "green"),(":", "green"),(m, "green"),(":0", "green"),(s, "green"),(".0", "green"),(t, "green"))}
        }
        if (delta < 0){
            if (m >= 10 && s >= 10 && t >= 10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":" , "red"),(s, "red"),(".",  "red"),(t, "red"))}
            if (m >= 10 && s >= 10 && t <  10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":" , "red"),(s, "red"),(".0", "red"),(t, "red"))}
            if (m >= 10 && s <  10 && t >= 10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":0", "red"),(s, "red"),(".",  "red"),(t, "red"))}
            if (m >= 10 && s <  10 && t <  10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":0", "red"),(s, "red"),(".0", "red"),(t, "red"))}
            if (m <  10 && s >= 10 && t >= 10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":" , "red"),(s, "red"),(".",  "red"),(t, "red"))}
            if (m <  10 && s >= 10 && t <  10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":" , "red"),(s, "red"),(".0", "red"),(t, "red"))}
            if (m <  10 && s <  10 && t >= 10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":0", "red"),(s, "red"),(".",  "red"),(t, "red"))}
            if (m <  10 && s <  10 && t <  10){tell(("-","red"),(h, "red"),(":", "red"),(m, "red"),(":0", "red"),(s, "red"),(".0", "red"),(t, "red"))}
        }
    }

    """
    Start a new level
    """
    def step(int level, int time){
        HasTime:=false
        if (current_lvl < level){
            current_lvl = level
            current_times[level] = time
            if (HasTime){
                int v = best_times[level]
                show_time(time, v)
            }
        }
    }

    """
    Stop the current run and save the current time as the best time if it is better
    """
    def stop(int level, int time){
        step(level, time)
        best_time := int.maxValue
        if (best_time > time){
            best_times = current_times
            best_time = time
            HasTime = true
        }
    }
}