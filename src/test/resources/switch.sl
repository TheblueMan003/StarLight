package switch_

import test::Test
import standard::print

def test(){
    int a,b
    switch(a){
        b..b -> ./say hi
        b..1 -> ./say hi
        b -> ./say hi
        0..1 -> ./say hi
        0 -> ./say hi
    }
}