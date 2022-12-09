package cmd.particle

"""
Spawn a single particle of type `part`
"""
def lazy point(mcobject $part){
    /particle $part ~ ~ ~ 0 0 0 0 1
}

"""
Spawn a single dust particle with color (`r`, `g`, `b`) and size `a`
"""
def lazy dust(float $r, float $g, float $b, float $a){
    /particle minecraft:dust $r $g $b $a ~ ~ ~ 0 0 0 0 1
}

"""
Spawn a single dust particle with color (`r`, `g`, `b`) and size 1
"""
def lazy dust(float $r, float $g, float $b){
    /particle minecraft:dust $r $g $b 1 ~ ~ ~ 0 0 0 0 1
}

"""
Spawn a sphere of `radius` of particle of type `part` with `speed` containing `amount` of particles
"""
def lazy sphere(mcobject $part, float $radius, float $speed, float $amount){
    /particle $part ~ ~ ~ $radius $radius $radius $speed $amount
}

"""
Spawn a disk of `radius` of particle of type `part` with `speed` containing `amount` of particles
"""
def lazy disk(mcobject $part, float $radius, float $speed, float $amount){
    /particle $part ~ ~ ~ $radius 0 $radius $speed $amount
}

"""
Spawn an explosion particle
"""
def explosion(){
    particle.sphere(minecraft:explosion_emitter,1,1,1)
}