package math.Vector3

import math

struct Vector3{
    float x
    float y
    float z
    
    def lazy __init__(float x, float y,float z){
        this.x = x
        this.y = y
        this.z = z
    }

    def lazy __set__(float x, float y, float z){
        this.x = x
        this.y = y
        this.z = z  
    }

    def lazy __set__(float other){
        this.x = other
        this.y = other
        this.z = other
    }
    
    def lazy __add__(Vector3 other){
        this.x += other.x
        this.y += other.y
        this.z += other.z
    }

    def lazy __add__(float x, float y, float z){
        this.x += x
        this.y += y
        this.z += z
    }
        
    def lazy __sub__(Vector3 other){
        this.x -= other.x
        this.y -= other.y
        this.z -= other.z
    }

    def lazy __sub__(float x, float y, float z){
        this.x -= x
        this.y -= y
        this.z -= z
    }

    """
    Return the magnitude of the vector
    """
    float magnitude(){
        float tmp1 = math.pow(this.x, 2)
        float tmp2 = math.pow(this.y, 2)
        float tmp3 = math.pow(this.z, 2)
        tmp1 = tmp1+ tmp2 + tmp3
        tmp1 = math.sqrt(tmp1)
        return(tmp1)
    }

    """
    Normalize the vector
    """
    def normalize(){
        float mag = magnitude()
        x /= mag
        y /= mag
        z /= mag
    }
    
    """
    Compute the scalar product with other
    """
    float scalarProd(Vector3 other){
        float output = 0
        output += this.x * other.x
        output += this.y * other.y
        output += this.z * other.z
        return(output)
    }
    
    def lazy __mult__(float a){
        this.x *= a
        this.y *= a
        this.z *= a
    }

    def lazy __mult__(Vector3 other){
        this.x *= other.x
        this.y *= other.y
        this.z *= other.z
    }

    def lazy __mult__(float x, float y, float z){
        this.x *= x
        this.y *= y
        this.z *= z
    }
    
    def lazy __div__(float a){
        this.x /= a
        this.y /= a
        this.z /= a
    }

    def lazy __div__(Vector3 other){
        this.x /= other.x
        this.y /= other.y
        this.z /= other.z
    }

    def lazy __div__(float x, float y, float z){
        this.x /= x
        this.y /= y
        this.z /= z
    }
}