package cmd.time

"""
Sets the time to the given amount of ticks.
"""
def lazy set(int $time){
    /time set $time
}
"""
Sets the time to the given amount of seconds.
"""
def lazy setSeconds(float $time){
    /time set $times
}
"""
Sets the time to the given amount of day.
"""
def lazy setDays(float $time){
    /time set $timed
}

"""
Adds the given amount of ticks to the current time.
"""
def lazy add(int $time){
    /time add $time
}
"""
Adds the given amount of seconds to the current time.
"""
def lazy addSeconds(float $time){
    /time add $times
}
"""
Adds the given amount of days to the current time.
"""
def lazy addDays(float $time){
    /time add $timed
}

if (Compiler.isJava()){
    """
    Gets the current time in ticks.
    """
    lazy int get(){
        Compiler.cmdstore(_ret){
            /time query gametime
        }
    }

    """
    Gets the current day.
    """
    lazy int getDay(){
        Compiler.cmdstore(_ret){
            /time query day
        }
    }

    """
    Gets the current day time.
    """
    lazy int getDayTime(){
        Compiler.cmdstore(_ret){
            /time query daytime
        }
    }
}