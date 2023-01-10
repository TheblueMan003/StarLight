## ` lazy void delay(void=>void func, int t)`
schedule `func` in `t` ticks

## ` lazy void asyncrepeat(int count, int=>void func)`
repeat `func` every tick for `count` times

## ` lazy void asyncrepeat(int count, int dt, int=>void func)`
repeat `func` every `dt` tick for `count` times

## ` lazy void asyncwhile(bool condition, void=>void func)`
repeat `func` while `condition` is true

## ` lazy void asyncwhile(int condition, int dlt, void=>void func)`
repeat `func` while `condition` is true with a delay of `dlt` ticks

## ` lazy void asyncwhile(int condition, entity sel, int dlt, void=>void func)`
repeat `func` while `condition` is true with a delay of `dlt` ticks at entity `sel`

## ` lazy void clear(void=>void $func)`
remove `func` from the schedule

## ` lazy void remove(void=>void $func)`
remove `func` from the schedule

## ` lazy void add(void=>void $func)`
add `func` to the schedule

## ` lazy void add(int $t, void=>void $func)`
add `func` to the schedule int `t` tick


