package array

import standard

from test import Test
import math.Vector3

Test initer{
    bool getResult(){
        Vector3 vec = new Vector3(10, 20, 0)
        if (vec.x == 10 && vec.y == 20){
            return true
        }
        else{
            return false
        }
    }
}

Test addition{
    bool getResult(){
        Vector3 vec1 = new Vector3(10, 20, 0)
        Vector3 vec2 = new Vector3(10, 20, 0)
        Vector3 vec3 = vec1 + vec2
        if (vec3.x == 20 && vec3.y == 40){
            return true
        }
        else{
            return false
        }
    }
}

Test sugar{
    bool getResult(){
        Vector3 vec1 = new(10, 20, 0)
        Vector3 vec2 = new(10, 20, 0)
        Vector3 vec3 = vec1 + vec2
        if (vec3.x == 20 && vec3.y == 40){
            return true
        }
        else{
            return false
        }
    }
}