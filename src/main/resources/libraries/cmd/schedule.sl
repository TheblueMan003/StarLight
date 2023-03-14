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
add `func` to the schedule
"""
def lazy add(void=>void func){
    add(1, func)
}


if (Compiler.isJava()){
    """
    remove `func` from the schedule
    """
    def lazy remove(void=>void func){
        clear(func)
    }

    """
    remove `func` from the schedule
    """
    def lazy clear(void=>void $func){
        /schedule clear $func
    }

    """
    add `func` to the schedule int `t` tick
    """
    def lazy add(int $t, void=>void $func){
        /schedule function $func $t append
    }
}
if (Compiler.isBedrock()){
    import mc.pointer as pointer
    lazy int id = 0
    lazy json data = {
        "format_version": "1.8.0",
        "minecraft:entity": {
            "description": {
                "identifier": "sl:scheduler",
                "is_spawnable": true,
                "is_experimental": false,
                "is_summonable": true
            },
            "components": {
            },
            "component_groups": {
            },
            "events": {
            }
        }
    }

    [Compile.order=99999] private void build(){
	    jsonfile entities.sl_scheduler data
    }

    """
    remove `func` from the schedule
    """
    def lazy clear(void=>void $func){
        /kill @e[name=$func, type=sl:scheduler]
    }

    """
    add `func` to the schedule int `t` tick
    """
    def lazy add(int t, void=>void func){
        lazy val time = t / 20.0
        lazy var call = Compiler.callToArray(func)
        call += "kill @s"
        id++
        Compiler.insert($name, id){
            data += {"minecraft:entity": {
                "component_groups": {
                    "$name" :{
                        "minecraft:timer": {
                            "looping": false,
                            "time": [
                                time,
                                time
                            ],
                            "time_down_event": {
                                "event": "$name_trigger"
                            }
                        }
                    }
                },
                "events": {
                    "$name_trigger": {
                        "run_command": {
                            "command": call
                        }
                    },
                    "$name_schedule": {
                        "add": {
                            "component_groups": [
                                "$name"
                                ]
                            }
                        }
                    }
                }
            }
            /summon sl:scheduler 0 0 0 $name_schedule $name
        }
    }
}