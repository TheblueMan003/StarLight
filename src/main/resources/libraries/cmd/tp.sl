package cmd.tp

import mc.pointer as pointer
import mc.java.nbt as nbt

"""
Teleport `selector` to `pos`
"""
def lazy tp(entity $selector, mcposition $pos = ~ ~ ~){
    /tp $selector $pos
}

"""
Teleport the current entity to `pos`
"""
def lazy tp(mcposition $pos = ~ ~ ~){
    /tp @s $pos
}

"""
Teleport the `selector` to `x`, `y`, `z`
"""
def tp(entity selector, float x, float y, float z){
    with(selector){
        absolute(x, y, z)
    }
}

"""
Teleport the currently `x` blocks relativly to the current position on axis x
"""
def relativeX(float x){
    if (x > 0){
        tpAxisPlus(x, 0)
    }
    if (x < 0){
        tpAxisMinus(-x, 0)
    }
}

"""
Teleport the currently `x` blocks absolute position on axis x
"""
def absoluteX(float x){
    at(@s)./tp @s 0 ~ ~
    relativeX(x)
}


"""
Teleport the currently `y` blocks relativly to the current position on axis y
"""
def relativeY(float y){
    if (y > 0){
        tpAxisPlus(y, 1)
    }
    if (y < 0){
        tpAxisMinus(-y, 1)
    }
}
"""
Teleport the currently `y` blocks absolute position on axis y
"""
def absoluteY(float y){
    at(@s)./tp @s ~ 0 ~
    relativeY(y)
}


"""
Teleport the currently `z` blocks relativly to the current position on axis z
"""
def relativeZ(float z){
    if (z > 0){
        tpAxisPlus(z, 2)
    }
    if (z < 0){
        tpAxisMinus(-z, 2)
    }
}
"""
Teleport the currently `z` blocks absolute position on axis z
"""
def absoluteZ(float z){
    at(@s)./tp @s ~ ~ 0
    relativeZ(z)
}

"""
Teleport the current entity relativly from `x`, `y`, `z`
"""
def relative(float x, float y, float z){
    relativeX(x)
    relativeY(y)
    relativeZ(z)
}

"""
Teleport the current entity at `x`, `y`, `z`
"""
def absolute(float x, float y, float z){
    /tp @s 0 0 0
    relativeX(x)
    relativeY(y)
    relativeZ(z)
}

"""
Rotate the entity of `x` degree relativly to the current rotation on the x axis
"""
def rotateRelativeX(float x){
    if (x > 0){
        tpAxisPlus(x, 3)
    }
    if (x < 0){
        tpAxisMinus(-x, 3)
    }
}

"""
Rotate the entity of `y` degree relativly to the current rotation on the y axis
"""
def rotateRelativeY(float y){
    if (y > 0){
        tpAxisPlus(y, 4)
    }
    if (y < 0){
        tpAxisMinus(-y, 4)
    }
}

"""
Rotate the current entity of `x`, `y` relativly to the current rotation
"""
def rotateRelative(float x, float y, float z){
    rotateRelativeX(x)
    rotateRelativeY(y)
}

"""
Rotate the current entity of `x`, `y`
"""
def rotateAbsolute(float x, float y){
    rotated(0, 0){
        rotateRelativeX(x)
        rotateRelativeY(y)
    }
}


def private lazy tpAxis(float $d, int axis){
    if (axis == 0){
        at(@s)./tp @s ~$d ~ ~
    }
    if (axis == 1){
        at(@s)./tp @s ~ ~$d ~
    }
    if (axis == 2){
        at(@s)./tp @s ~ ~ ~$d
    }
    if (axis == 3){
        at(@s)./tp @s ~ ~ ~ ~$d ~
    }
    if (axis == 4){
        at(@s)./tp @s ~ ~ ~ ~ ~$d
    }
}

def private tpAxisPlus(float x, int axis){
    foreach(i in 0..31){
        lazy var pow2 = Compiler.pow(2, 31-i)
        lazy var pow = pow2/1000
        if(x >= pow){
            tpAxis(pow, axis)
            x-=pow
        }
    }
}
def private tpAxisMinus(float x, int axis){
    foreach(i in 0..31){
        lazy var pow2 = Compiler.pow(2, 31-i)
        lazy var pow = pow2/1000
        if(x >= pow){
            lazy int a = -pow
            tpAxis(a, axis)
            x-=pow
        }
    }
}


"""
Get the x position of the current entity
"""
float getX(){
    if(Compiler.isJava){
        float x = 0
        pointer.run(){
            x = nbt.x
        }
        return x
    }
    if (Compiler.isBedrock()){
        float x = 0
        pointer.run(){
            if (@s[x=0,dx=2147483647]){
                foreach(i in 0..31){
                    at(@s){
                        lazy var pow2 = Compiler.pow(2, 31-i)
                        lazy var pow = pow2/1000
                        if (@s[x=pow,dx=2147483647]){
                            x+=pow
                            lazy float a = -pow
                            tpAxis(a, 0)
                        }
                    }
                }
            }
            if (@s[x=-2147483648,dx=2147483647]){
                foreach(i in 0..31){
                    at(@s){
                        lazy var pow2 = Compiler.pow(2, 31-i)
                        lazy var pow = pow2/1000
                        lazy var j = -pow
                        if (@s[x=j,dx=2147483647]){
                            x-=pow
                            tpAxis(pow, 0)
                        }
                    }
                }
            }
        }
        return x
    }
}

"""
Get the y position of the current entity
"""
float getY(){
    if(Compiler.isJava){
        float y = 0
        pointer.run(){
            y = nbt.y
        }
        return y
    }
    if (Compiler.isBedrock()){
        float y = 0
        pointer.run(){
            if (@s[y=0,dy=2147483647]){
                foreach(i in 0..31){
                    at(@s){
                        lazy var pow2 = Compiler.pow(2, 31-i)
                        lazy var pow = pow2/1000
                        if (@s[y=pow,dy=2147483647]){
                            y+=pow
                            lazy float a = -pow
                            tpAxis(a, 1)
                        }
                    }
                }
            }
            if (@s[y=-2147483648,dy=2147483647]){
                foreach(i in 0..31){
                    at(@s){
                        lazy var pow2 = Compiler.pow(2, 31-i)
                        lazy var pow = pow2/1000
                        lazy var j = -pow
                        if (@s[y=j,dy=2147483647]){
                            y-=pow
                            tpAxis(pow, 1)
                        }
                    }
                }
            }
        }
        return y
    }
}

"""
Get the z position of the current entity
"""
float getZ(){
    if(Compiler.isJava){
        float z = 0
        pointer.run(){
            z = nbt.z
        }
        return z
    }
    if (Compiler.isBedrock()){
        float z = 0
        pointer.run(){
            if (@s[z=0,dz=2147483647]){
                foreach(i in 0..31){
                    at(@s){
                        lazy var pow2 = Compiler.pow(2, 31-i)
                        lazy var pow = pow2/1000
                        if (@s[z=pow,dz=2147483647]){
                            z+=pow
                            lazy float a = -pow
                            tpAxis(a, 2)
                        }
                    }
                }
            }
            if (@s[z=-2147483648,dz=2147483647]){
                foreach(i in 0..31){
                    at(@s){
                        lazy var pow2 = Compiler.pow(2, 31-i)
                        lazy var pow = pow2/1000
                        lazy var j = -pow
                        if (@s[z=j,dz=2147483647]){
                            z-=pow
                            tpAxis(pow, 2)
                        }
                    }
                }
            }
        }
        return z
    }
}