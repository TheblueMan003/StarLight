package int

import test::Test
import standard::print

Test addition_variable{
    def bool getResult(){
        int a = 1
        int b = 2
        int c = a + b
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
        int a = 1
        int b = 2
        int c = a * b
        if(c == 2){
            return true
        }
        else{
            return false
        }
    }
}
Test addition_value{
    def bool getResult(){
        int a = 1
        int c = a + 2
        if(c == 3){
            return true
        }
        else{
            return false
        }
    }
}
Test sub_value{
    def bool getResult(){
        int a = 1
        int b = 2
        int c = a - 2
        if(c == -1){
            return true
        }
        else{
            return false
        }
    }
}

Test lt_int{
    def bool getResult(){
        int a = 1
        if(a < 2){
            return true
        }
        else{
            return false
        }
    }
}
Test gt_int{
    def bool getResult(){
        int a = 1
        if(a > 0){
            return true
        }
        else{
            return false
        }
    }
}
Test eq_int{
    def bool getResult(){
        int a = 1
        if(a == 1){
            return true
        }
        else{
            return false
        }
    }
}
Test neq_int{
    def bool getResult(){
        int a = 1
        if(a != 2){
            return true
        }
        else{
            return false
        }
    }
}
Test lte_int{
    def bool getResult(){
        int a = 1
        if(a <= 1){
            return true
        }
        else{
            return false
        }
    }
}
Test gte_int{
    def bool getResult(){
        int a = 1
        if(a >= 1){
            return true
        }
        else{
            return false
        }
    }
}

Test lt_float{
    def bool getResult(){
        int a = 1
        if(a < 2.0){
            return true
        }
        else{
            return false
        }
    }
}
Test gt_float{
    def bool getResult(){
        int a = 1
        if(a > 0.0){
            return true
        }
        else{
            return false
        }
    }
}
Test eq_float{
    def bool getResult(){
        int a = 1
        if(a == 1.0){
            return true
        }
        else{
            return false
        }
    }
}
Test neq_float{
    def bool getResult(){
        int a = 1
        if(a != 2.0){
            return true
        }
        else{
            return false
        }
    }
}
Test lte_float{
    def bool getResult(){
        int a = 1
        if(a <= 1.0){
            return true
        }
        else{
            return false
        }
    }
}
Test gte_float{
    def bool getResult(){
        int a = 1
        if(a >= 1.0){
            return true
        }
        else{
            return false
        }
    }
}

Test neq_float_delta{
    def bool getResult(){
        int a = 1
        if(a != 1.5){
            return true
        }
        else{
            return false
        }
    }
}
Test neq_float_fail{
    def bool getResult(){
        int a = 1
        if(a != 1.0){
            return false
        }
        else{
            return true
        }
    }
}

Test neq_int_fail{
    def bool getResult(){
        int a = 1
        if(a != 1){
            return false
        }
        else{
            return true
        }
    }
}









Test not_addition_variable{
    def bool getResult(){
        int a = 1
        int b = 2
        int c = a + b
        if(!(c == 3)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_multi_variable{
    def bool getResult(){
        int a = 1
        int b = 2
        int c = a * b
        if(!(c == 2)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_addition_value{
    def bool getResult(){
        int a = 1
        int c = a + 2
        if(!(c == 3)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_sub_value{
    def bool getResult(){
        int a = 1
        int b = 2
        int c = a - 2
        if(!(c == -1)){
            return false
        }
        else{
            return true
        }
    }
}

Test not_lt_int{
    def bool getResult(){
        int a = 1
        if(!(a < 2)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_gt_int{
    def bool getResult(){
        int a = 1
        if(!(a > 0)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_eq_int{
    def bool getResult(){
        int a = 1
        if(!(a == 1)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_neq_int{
    def bool getResult(){
        int a = 1
        if(!(a != 2)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_lte_int{
    def bool getResult(){
        int a = 1
        if(!(a <= 1)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_gte_int{
    def bool getResult(){
        int a = 1
        if(!(a >= 1)){
            return false
        }
        else{
            return true
        }
    }
}

Test not_lt_float{
    def bool getResult(){
        int a = 1
        if(!(a < 2.0)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_gt_float{
    def bool getResult(){
        int a = 1
        if(!(a > 0.0)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_eq_float{
    def bool getResult(){
        int a = 1
        if(!(a == 1.0)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_neq_float{
    def bool getResult(){
        int a = 1
        if(!(a != 2.0)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_lte_float{
    def bool getResult(){
        int a = 1
        if(!(a <= 1.0)){
            return false
        }
        else{
            return true
        }
    }
}
Test not_gte_float{
    def bool getResult(){
        int a = 1
        if(!(a >= 1.0)){
            return false
        }
        else{
            return true
        }
    }
}