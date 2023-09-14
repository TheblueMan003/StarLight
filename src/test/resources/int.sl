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

Test ternary_operator_integer_result{
    def bool getResult(){
        int a = 5
        int b = 10
        int result = (a < b) if a else b
        return (result == a)
    }
}

Test ternary_operator_float_result{
    def bool getResult(){
        float x = 3.5
        float y = 2.0
        float result = (x > y) ? x :: y
        return (result == x)
    }
}

Test ternary_operator_mixed_result{
    def bool getResult(){
        int a = 7
        float b = 7.5
        float result = (a >= 7) ? a :: b
        return (result == a)
    }
}

Test ternary_operator_nested{
    def bool getResult(){
        int a = 10
        int b = 20
        int c = 30
        int result = (a < b) ? ((b < c) ? c :: b) :: a
        return (result == a)
    }
}