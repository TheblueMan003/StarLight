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



Test float_addition {
    def bool getResult(){
        float a = 3.456
        float b = 2.345
        float result = a + b
        return (result == 5.801)
    }
}

Test float_subtraction {
    def bool getResult(){
        float a = 8.721
        float b = 1.234
        float result = a - b
        return (result == 7.487)
    }
}

Test float_multiplication {
    def bool getResult(){
        float a = 2.345
        float b = 3.456
        float result = a * b
        return (result == 8.103)
    }
}

Test float_division {
    def bool getResult(){
        float a = 9.876
        float b = 3.456
        float result = a / b
        return (result == 2.857)
    }
}

Test float_mixed_operations {
    def bool getResult(){
        float a = 4.321
        float b = 1.234
        float c = 5.678
        float result = ((a + b) * c) / b
        return (result == 23.145)
    }
}