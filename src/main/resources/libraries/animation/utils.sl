package animation.utils

"""
Make the current entity look at `other`
"""
def lazy lookAt(entity other){
    facing(other)./tp @s ~ ~ ~ ~ 0
}

"""
Make the current entity look at nearest player
"""
def lookAtPlayer(){
    facing(@p){
        /tp @s ~ ~ ~ ~ 0
    }
}

"""
Make the current entity spin at `speed` degrees per tick
"""
def lazy spin(float $speed){
    /tp @s ~ ~ ~ ~$speed ~
}