package AnimatedEntity

import standard::print

import mc.AnimatedEntity

class A extends AnimatedEntity with minecraft:cow for mcjava{
    def test(){
        playAnimation("cow")
    }
}

def test(){
    new A()
}