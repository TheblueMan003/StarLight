package main

import standard::print

class C{
    C(){
        print("C")
    }
    void test(){

    }
    bool operator>=(C c){
        return true
    }
}
def test(a: int){
    C c = new C()
    if (c >= c){
        print("true")
    }
    else{
        print("false")
    }
}