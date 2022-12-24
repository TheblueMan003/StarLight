package math.vector2

import math

struct Vector2{
    float x
    float y

    def lazy __init__(float x, float y){
        this.x = x
        this.y = y
    }

    def lazy __set__(float x, float y){
        this.x = x
        this.y = y
    }

    def __set__(Vector2 other){
        this.x = other.x
        this.y = other.y
    }

    def lazy __set__(float other){
        this.x = other
        this.y = other
    }

    def lazy __add__(Vector2 other){
        this.x += other.x
        this.y += other.y
    }

    def lazy __add__(float x, float y){
        this.x += x
        this.y += y
    }
    
    def lazy __sub__(Vector2 other){
        this.x -= other.x
        this.y -= other.y
    }

    def lazy __sub__(float x, float y){
        this.x -= x
        this.y -= y
    }
    
    float scalarProd(Vector2 other){
        float output = 0
        output += this.x * this.x
        output += this.y * this.y
        return(output)
    }
    
    def lazy __mult__(float a){
        this.x *= a
        this.y *= a
    }

    def lazy __mult__(Vector2 other){
        this.x *= other.x
        this.y *= other.y
    }

    def lazy __mult__(float x, float y){
        this.x *= x
        this.y *= y
    }
    
    def lazy __div__(float a){
        this.x /= a
        this.y /= a
    }

    def lazy __div__(Vector2 other){
        this.x /= other.x
        this.y /= other.y
    }

    def lazy __div__(float x, float y){
        this.x /= x
        this.y /= y
    }

    """
    Return the magnitude of the vector
    """
    float magnitude(){
        float tmp1 = math.pow(this.x, 2)
        float tmp2 = math.pow(this.y, 2)
        tmp1 = tmp1+ tmp2
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
    }

    """
    Compute the scalar product with other
    """
    float scalarProd(Vector2 other){
        float output = 0
        output += this.x * other.x
        output += this.y * other.y
        return(output)
    }
}