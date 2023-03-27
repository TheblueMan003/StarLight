package float_int

import test::Test

Test lt_float_int{
    def bool getResult(){
        float a = 1
        int b = 2
        if(a < b){
            return true
        }
        else{
            return false
        }
    }
}
Test le_float_int{
    def bool getResult(){
        float a = 1
        int b = 2
        if(a <= b){
            return true
        }
        else{
            return false
        }
    }
}
Test gt_float_int{
    def bool getResult(){
        float a = 3
        int b = 2
        if(a > b){
            return true
        }
        else{
            return false
        }
    }
}
Test ge_float_int{
    def bool getResult(){
        float a = 3
        int b = 2
        if(a >= b){
            return true
        }
        else{
            return false
        }
    }
}
Test eq_float_int{
    def bool getResult(){
        float a = 2
        int b = 2
        if(a == b){
            return true
        }
        else{
            return false
        }
    }
}
Test ne_float_int{
    def bool getResult(){
        float a = 1
        int b = 2
        if(a != b){
            return true
        }
        else{
            return false
        }
    }
}

Test lt_int_float{
    def bool getResult(){
        int a = 1
        float b = 2
        if(a < b){
            return true
        }
        else{
            return false
        }
    }
}
Test le_int_float{
    def bool getResult(){
        int a = 1
        float b = 2
        if(a <= b){
            return true
        }
        else{
            return false
        }
    }
}
Test gt_int_float{
    def bool getResult(){
        int a = 3
        float b = 2
        if(a > b){
            return true
        }
        else{
            return false
        }
    }
}
Test ge_int_float{
    def bool getResult(){
        int a = 3
        float b = 2
        if(a >= b){
            return true
        }
        else{
            return false
        }
    }
}
Test eq_int_float{
    def bool getResult(){
        int a = 2
        float b = 2
        if(a == b){
            return true
        }
        else{
            return false
        }
    }
}
Test ne_int_float{
    def bool getResult(){
        int a = 1
        float b = 2
        if(a != b){
            return true
        }
        else{
            return false
        }
    }
}
