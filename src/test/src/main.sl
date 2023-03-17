package test

import math.raycast as raycast

def main(){
    raycast.shoot(10,0.1,!block(minecraft:air)){
        /setblock ~ ~ ~ minecraft:stone
    }
}

def test(){
    int a
    if (a > 0){
        /say a
        /say b
    }
    if (a > 0){
        /say a
        /say b
    }
}