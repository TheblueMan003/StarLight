## delay
- void=>void func
- int t
schedule `func` in `t` ticks
## asyncrepeat
- int count
- int=>void func
repeat `func` every tick for `count` times
## asyncrepeat
- int count
- int dt
- int=>void func
repeat `func` every `dt` tick for `count` times
## asyncwhile
- bool condition
- void=>void func
repeat `func` while `condition` is true
## asyncwhile
- int condition
- int dlt
- void=>void func
repeat `func` while `condition` is true with a delay of `dlt` ticks
## asyncwhile
- int condition
- entity sel
- int dlt
- void=>void func
repeat `func` while `condition` is true with a delay of `dlt` ticks at entity `sel`
## clear
- void=>void $func
remove `func` from the schedule
## remove
- void=>void $func
remove `func` from the schedule
## add
- void=>void $func
add `func` to the schedule
## add
- int $t
- void=>void $func
add `func` to the schedule int `t` tick
