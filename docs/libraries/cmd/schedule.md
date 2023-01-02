## lazy void cmd.schedule.delay(void=>void func, int t)
- void=>void func
- int t

schedule `func` in `t` ticks

## lazy void cmd.schedule.asyncrepeat(int count, int=>void func)
- int count
- int=>void func

repeat `func` every tick for `count` times

## lazy void cmd.schedule.asyncrepeat(int count, int dt, int=>void func)
- int count
- int dt
- int=>void func

repeat `func` every `dt` tick for `count` times

## lazy void cmd.schedule.asyncwhile(bool condition, void=>void func)
- bool condition
- void=>void func

repeat `func` while `condition` is true

## lazy void cmd.schedule.asyncwhile(int condition, int dlt, void=>void func)
- int condition
- int dlt
- void=>void func

repeat `func` while `condition` is true with a delay of `dlt` ticks

## lazy void cmd.schedule.asyncwhile(int condition, entity sel, int dlt, void=>void func)
- int condition
- entity sel
- int dlt
- void=>void func

repeat `func` while `condition` is true with a delay of `dlt` ticks at entity `sel`

## lazy void cmd.schedule.clear(void=>void $func)
- void=>void $func

remove `func` from the schedule

## lazy void cmd.schedule.remove(void=>void $func)
- void=>void $func

remove `func` from the schedule

## lazy void cmd.schedule.add(void=>void $func)
- void=>void $func

add `func` to the schedule

## lazy void cmd.schedule.add(int $t, void=>void $func)
- int $t
- void=>void $func

add `func` to the schedule int `t` tick


