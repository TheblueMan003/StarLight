package test

import cmd.bossbar as bs
import animation.bossbar as ab

bs.Bossbar test = new bs.Bossbar("bar")

public void main(){
    int tick
    bool a = ab.show(test, tick, "Hello World!")
    tick ++
}