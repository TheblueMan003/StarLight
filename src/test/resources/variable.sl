package variable

import test::Test
import standard::print

Test default_assignment{
    def bool getResult(){
        int a := 3
        if(a == 3){
            return true
        }
        else{
            return false
        }
    }
}

Test lambda_test{
    int a
    def @test_function(){
        a = 1
    }
    def bool getResult(){
        @test_function()
        if(a == 1){
            return true
        }
        else{
            return false
        }
    }
}

Test isVariable{
    def bool getResult(){
        int a
        if(Compiler.isVariable(a)){
            return true
        }
        else{
            return false
        }
    }
}

Test isNotVariable{
    def bool getResult(){
        lazy int a = 5
        if(Compiler.isVariable(a)){
            return false
        }
        else{
            return true
        }
    }
}