package cmd.worldborder

"""
Set World Border `size` in Block with `time`
"""
def lazy set(int $size, int $time = 0){
    /worldborder set $size $time
}

"""
Add World Border `size` in Block with `time`
"""
def lazy add(int $size, int $time = 0){
    /worldborder add $size $time
}

"""
Set World Border Center `x` `z`
"""
def lazy center(int $x, int $y, int $z){
    /worldborder center $x $z
}

"""
Set World Border Center to `x` `z`
"""
def lazy center(int $x, int $z){
    /worldborder center $x $z
}

"""
Set World Border Center
"""
def lazy center(){
    /worldborder center ~ ~
}

"""
Set World Border Buffer Zone to `size`
"""
def lazy buffer(int $size){
    /worldborder damage buffer $size
}

"""
Set World Border Damage Amount per block to `dmg`
"""
def lazy damage(int $dmg){
    /worldborder damage amount $dmg
}

"""
Set World Border Warning `time`
"""
def lazy warningtime(int $time){
    /worldborder warning time $time
}

"""
Set World Border Warning Distance to `dist`
"""
def lazy warningdistance(int $dist){
    /worldborder warning distance $dist
}