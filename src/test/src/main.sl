package main

import standard::print

struct C(int a){
    string toString(){
        return "hi"
    }
}

class D{

}

def test(){
    var x = 1
    var y = 2
    var z = 3
    at({~x, ~y, ~z}){
        print(x)
        print(y)
        print(z)
    }
}   