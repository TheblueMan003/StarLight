package mc.java.forceload

"""
Force load chunk at x1, z1
"""
def lazy forceload(int $x1, int $z1){
    /forceload add $x1 $z1
}

"""
Force load chunks from x1, z1 to x2, z2
"""
def lazy forceload(int $x1, int $z1, int $x2, int $z2){
    /forceload add $x1 $z1 $x2 $z2
}

"""
Remove Force load chunk at x1, z1
"""
def lazy forceunload(int $x1, int $z1){
    /forceload remove $x1 $z1
}
    
"""
Remove Force load chunks from x1, z1 to x2, z2
"""
def lazy forceunload(int $x1, int $z1, int $x2, int $z2){
    /forceload remove $x1 $z1 $x2 $z2
}