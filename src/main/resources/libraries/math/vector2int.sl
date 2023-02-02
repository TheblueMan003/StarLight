package math.Vector2Int

import math

struct Vector2Int{
    int x
    int y

    def lazy __init__(int x, int y){
        this.x = x
        this.y = y
    }

    def lazy __set__(int x, int y){
        this.x = x
        this.y = y
    }

    def __set__(Vector2Int other){
        this.x = other.x
        this.y = other.y
    }

    def lazy __set__(int other){
        this.x = other
        this.y = other
    }



    def lazy __add__(Vector2Int other){
        this.x += other.x
        this.y += other.y
    }

    def lazy __add__(int x, int y){
        this.x += x
        this.y += y
    }
    
    def lazy __sub__(Vector2Int other){
        this.x -= other.x
        this.y -= other.y
    }

    def lazy __sub__(int x, int y){
        this.x -= x
        this.y -= y
    }

    def lazy bool __eq__(Vector2Int other){
        return(this.x == other.x && this.y == other.y)
    }
    def lazy bool __ne__(Vector2Int other){
        return(this.x != other.x || this.y != other.y)
    }
    
    """
    Compute the scalar product with other
    """
    int scalarProd(Vector2Int other){
        int output = 0
        output += this.x * this.x
        output += this.y * this.y
        return(output)
    }
    
    def lazy __mult__(int a){
        this.x *= a
        this.y *= a
    }

    def lazy __mult__(Vector2Int other){
        this.x *= other.x
        this.y *= other.y
    }

    def lazy __mult__(int x, int y){
        this.x *= x
        this.y *= y
    }
    
    def lazy __div__(int a){
        this.x /= a
        this.y /= a
    }

    def lazy __div__(Vector2Int other){
        this.x /= other.x
        this.y /= other.y
    }

    def lazy __div__(int x, int y){
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
}
