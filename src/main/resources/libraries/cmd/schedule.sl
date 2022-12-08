def lazy delay(void=>void func, int t){
    schedule.add(func, t)
}
def lazy asyncrepeat(int count, int=>void func){
    int c = 0
    asyncwhile(c < count){
        func(c)
        c++
    }
}
def lazy asyncrepeat(int count, int dt, int=>void func){
    int c = 0
    asyncwhile(c < count, dt){
        func(c)
        c++
    }
}


def lazy asyncwhile(int condition, void=>void func){
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

package schedule

def lazy clear(void=>void $func){
    /schedule clear $func
}
def lazy remove(void=>void $func){
    /schedule clear $func
}
def lazy add(void=>void $func){
    /schedule function $func 1t append
}
def lazy add(int $t, void=>void $func){
    /schedule function $func $t append
}