package cmd.schedule

"""
schedule `func` in `t` ticks
"""
def lazy delay(void=>void func, int t){
    schedule.add(func, t)
}

"""
repeat `func` every tick for `count` times
"""
def lazy asyncrepeat(int count, int=>void func){
    int c = 0
    asyncwhile(c < count){
        func(c)
        c++
    }
}

"""
repeat `func` every `dt` tick for `count` times
"""
def lazy asyncrepeat(int count, int dt, int=>void func){
    int c = 0
    asyncwhile(c < count, dt){
        func(c)
        c++
    }
}

"""
repeat `func` while `condition` is true
"""
def lazy asyncwhile(bool condition, void=>void func){
    def __lambda__(){
        func()
        if (condition){
            schedule.add(__lambda__)
        }
    }
    if (condition){
        __lambda__()
    }
}

"""
repeat `func` while `condition` is true with a delay of `dlt` ticks
"""
def lazy asyncwhile(int condition, int dlt, void=>void func){
    def __lambda__(){
        func()
        if (condition){
            schedule.add(__lambda__, dlt)
        }
    }
    if (condition){
        __lambda__()
    }
}

"""
repeat `func` while `condition` is true with a delay of `dlt` ticks at entity `sel`
"""
def lazy asyncwhile(int condition, entity sel, int dlt, void=>void func){
    def __lambda__(){
        with(sel,true){
            func()
        }
        if (condition){
            schedule.add(__lambda__, dlt)
        }
    }
    if (condition){
        __lambda__()
    }
}

"""
remove `func` from the schedule
"""
def lazy clear(void=>void $func){
    /schedule clear $func
}

"""
remove `func` from the schedule
"""
def lazy remove(void=>void $func){
    /schedule clear $func
}

"""
add `func` to the schedule
"""
def lazy add(void=>void $func){
    /schedule function $func 1t append
}

"""
add `func` to the schedule int `t` tick
"""
def lazy add(int $t, void=>void $func){
    /schedule function $func $t append
}