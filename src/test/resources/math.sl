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