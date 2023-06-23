package range

import test::Test
import standard::print

Test inc_value{
    def bool getResult(){
        var a = 0..5
        if(3 in a){
            return true
        }
        else{
            return false
        }
    }
}
Test inc_variable{
    def bool getResult(){
        var a = 0..5
        int b = 2
        if(b in a){
            return true
        }
        else{
            return false
        }
    }
}
Test addition_value{
    def bool getResult(){
        var a = 1..10
        a += 2
        if(!(1 in a)){
            return true
        }
        else{
            return false
        }
    }
}