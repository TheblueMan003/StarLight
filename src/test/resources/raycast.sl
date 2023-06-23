package raycast

import math.raycast as raycast
import test::Test
import standard::print

def test(){
    raycast.shoot(10,0.5,block(minecraft:stone)){
        print("hi")
    }
}