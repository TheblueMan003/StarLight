

package math.vector3int

import math

struct Vector3Int{
    int x
    int y
    int z

    def lazy __init__(int x, int y, int z){
        this.x = x
        this.y = y
        this.z = z
    }

    def lazy __set__(int x, int y, int z){
        this.x = x
        this.y = y
        this.z = z  
    }

    def __set__(Vector3Int other){
        this.x = other.x
        this.y = other.y
        this.z = other.z
    }

    def lazy __set__(int other){
        this.x = other
        this.y = other
        this.z = other
    }

    def lazy __add__(Vector3Int other){
        this.x += other.x
        this.y += other.y
        this.z += other.z
    }

    def lazy __add__(int x, int y, int z){
        this.x += x
        this.y += y
        this.z += z
    }
    
    def lazy __sub__(Vector3Int other){
        this.x -= other.x
        this.y -= other.y
        this.z -= other.z
    }

    def lazy __sub__(int x, int y, int z){
        this.x -= x
        this.y -= y
        this.z -= z
    }
    
    """
    Compute the scalar product with other
    """
    def int scalarProd(Vector3Int other){
        int output = 0
        output += this.x * other.x
        output += this.y * other.y
        output += this.z * other.z
        return(output)
    }
    
    def lazy __mult__(int a){
        this.x *= a
        this.y *= a
        this.z *= a
    }

    def lazy __mult__(Vector3Int other){
        this.x *= other.x
        this.y *= other.y
        this.z *= other.z
    }

    def lazy __mult__(int x, int y, int z){
        this.x *= x
        this.y *= y
        this.z *= z
    }
    
    def lazy __div__(int a){
        this.x /= a
        this.y /= a
        this.z /= a
    }

    def lazy __div__(Vector3Int other){
        this.x /= other.x
        this.y /= other.y
        this.z /= other.z
    }

    def lazy __div__(int x, int y, int z){
        this.x /= x
        this.y /= y
        this.z /= z
    }

    """
    Return the magnitude of the vector
    """
    int magnitude(){
        float tmp1 = math.pow(this.x, 2)
        float tmp2 = math.pow(this.y, 2)
        float tmp3 = math.pow(this.z, 2)
        tmp1 = tmp1 + tmp2 + tmp3
        tmp1 = math.sqrt(tmp1)
        return(tmp1)
    }
}
Vector3Int getPosition(){
    import cmd.tp as tp
    return(new Vector3Int(tp.getX(), tp.getY(), tp.getZ()))
}

package _

def lazy __at__(math.vector3int.Vector3Int v, void=>void fct){
    __at__(v.x, v.y, v.z, fct)
}