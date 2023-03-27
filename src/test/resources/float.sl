package float

import test::Test
import standard::print

Test addition_variable{
    def bool getResult(){
        float a = 1
        float b = 2
        float c = a + b
        if(c == 3){
            return true
        }
        else{
            return false
        }
    }
}
Test multi_variable{
    def bool getResult(){
        float a = 1
        float b = 2
        float c = a * b
        if(c == 2.0){
            return true
        }
        else{
            showError(c, 2.0)
            return false
        }
    }
}
Test addition_float{
    def bool getResult(){
        float a = 1.0
        float c = a + 2.0
        if(c == 3.0){
            return true
        }
        else{
            showError(c, 3.0)
            return false
        }
    }
}
Test addition_int{
    def bool getResult(){
        float a = 1.0
        float c = a + 2
        if(c == 3.0){
            return true
        }
        else{
            showError(c, 3.0)
            return false
        }
    }
}
Test mult_int{
    def bool getResult(){
        float a = 1.0
        float c = a * 2
        if(c == 2.0){
            return true
        }
        else{
            showError(c, 2.0)
            return false
        }
    }
}
Test mult_float{
    def bool getResult(){
        float a = 1.0
        float c = a * 2.5
        if(c == 2.5){
            return true
        }
        else{
            showError(c, 2.5)
            return false
        }
    }
}

Test div_float{
    def bool getResult(){
        float a = 5.0
        float c = a / 2.5
        if(c == 2.0){
            return true
        }
        else{
            showError(c, 2.0)
            return false
        }
    }
}
Test div_int{
    def bool getResult(){
        float a = 5.0
        float c = a / 2
        if(c == 2.5){
            return true
        }
        else{
            showError(c, 2.5)
            return false
        }
    }
}


Test le_int{
    def bool getResult(){
        float a = 1
        if(a <= 2){
            return true
        }
        else{
            return false
        }
    }
}

Test le_float{
    def bool getResult(){
        float a = 1.0
        if(a <= 2.0){
            return true
        }
        else{
            return false
        }
    }
}

Test lt_int{
    def bool getResult(){
        float a = 1
        if(a < 2){
            return true
        }
        else{
            return false
        }
    }
}

Test lt_float{
    def bool getResult(){
        float a = 1.0
        if(a < 2.0){
            return true
        }
        else{
            return false
        }
    }
}

Test ge_int{
    def bool getResult(){
        float a = 3
        if(a >= 2){
            return true
        }
        else{
            return false
        }
    }
}

Test ge_float{
    def bool getResult(){
        float a = 3.0
        if(a >= 2.0){
            return true
        }
        else{
            return false
        }
    }
}

Test gt_int{
    def bool getResult(){
        float a = 3
        if(a > 2){
            return true
        }
        else{
            return false
        }
    }
}

Test gt_float{
    def bool getResult(){
        float a = 3.0
        if(a > 2.0){
            return true
        }
        else{
            return false
        }
    }
}

Test eq_int{
    def bool getResult(){
        float a = 1
        if(a == 1){
            return true
        }
        else{
            return false
        }
    }
}

Test eq_float{
    def bool getResult(){
        float a = 1.0
        if(a == 1.0){
            return true
        }
        else{
            return false
        }
    }
}

Test ne_int{
    def bool getResult(){
        float a = 1
        if(a != 2){
            return true
        }
        else{
            return false
        }
    }
}

