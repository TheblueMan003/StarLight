package time

import math.Time
import test::Test

def test(){
    Time a
    lazy Time Second = new Time(0, 0, 1)
    if (a == new Time(1,2,3)) {
        ./say hi
    }
    switch(a){
        new Time(0, 20, 0) -> ./say 20
        new Time(0, 30, 0) -> ./say 30
        Second * 2 -> ./say 2
    }
}