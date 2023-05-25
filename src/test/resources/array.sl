package array

import standard

from test import Test
import math

Test set_and_get{
    bool getResult(){
        int[10] array
        array[0] = 5
        if (array[0] == 5){
            return true
        }
        else{
            return false
        }
    }
}

Test print{
    bool getResult(){
        int[10] array
        standard.print(array[0])
        return true
    }
}

Test add{
    bool getResult(){
        int[10] array
        array[0] = 5
        array[1] = 6
        if (array[0] + array[1] == 11){
            return true
        }
        else{
            return false
        }
    }
}

Test subtract{
    bool getResult(){
        int[10] array
        array[0] = 5
        array[1] = 6
        int value = array[0] - array[1]
        if (value == -1){
            return true
        }
        else{
            return false
        }
    }
}

Test multi{
    bool getResult(){
        int[10,10] array
        int total = 0
        int check = 0
        for(int i = 0;i < 10;i++){
            for(int j = 0;j < 10;j++){
                total += i * j
                array[i,j] = i * j
            }
        }
        for(int i = 0;i < 10;i++){
            for(int j = 0;j < 10;j++){
                check += array[i,j]
            }
        }
        if (check == total){
            return true
        }
        else{
            return false
        }
    }
}

Test function_single{
    bool getResult(){
        int[10] array
        array[0] = math.abs(5)
        if (array[0] == 5){
            return true
        }
        else{
            return false
        }
    }
}

Test function_multicast{
    bool getResult(){
        int[10] array
        array = math.abs(5)
        if (array[0] == 5 && array[1] == 5){
            return true
        }
        else{
            return false
        }
    }
}