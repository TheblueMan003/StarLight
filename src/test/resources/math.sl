package test

from test import Test
import math

Test abs{
    bool getResult(){
        if (math.abs(-5) == 5){
            return true
        }
        else{
            return false
        }
    }
}

Test max{
    bool getResult(){
        if (math.max(0, 5) == 5){
            return true
        }
        else{
            return false
        }
    }
}

Test min{
    bool getResult(){
        if (math.min(0, 5) == 0){
            return true
        }
        else{
            return false
        }
    }
}

Test max_float{
    bool getResult(){
        if (math.max(0.0, 5.0) == 5.0){
            return true
        }
        else{
            return false
        }
    }
}

Test max_mixed{
    bool getResult(){
        if (math.max(0, 5.0) == 5.0){
            return true
        }
        else{
            return false
        }
    }
}

Test sqrt1{
    bool getResult(){
        if (math.sqrt(4) == 2){
            return true
        }
        else{
            return false
        }
    }
}
Test sqrt2{
    bool getResult(){
        if (math.sqrt(16) == 4){
            return true
        }
        else{
            return false
        }
    }
}
Test sqrt3{
    bool getResult(){
        if (math.sqrt(256) == 16){
            return true
        }
        else{
            return false
        }
    }
}


Test bitwise_and{
    bool getResult(){
        int a = 13
        int b = 6
        b &= a
        if (b == 4){
            return true
        }
        else{
            return false
        }
    }
}


Test bitwise_or{
    bool getResult(){
        int a = 13
        int b = 6
        b |= a
        if (b == 15){
            return true
        }
        else{
            return false
        }
    }
}