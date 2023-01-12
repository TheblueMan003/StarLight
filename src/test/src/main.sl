package a

import utils.process::Process

class A with blockbench:dr_snake for mcjava{
    def __init__(){

    }
    def function1(){
        /say hi
    }
    def virtual function2(){
        /say hi2
    }
}

class B extends A{
    def override function2(){
        /say hi3
    }
}

A c = new A()
def lol(){
    c.function1()
    c.function2()
}

Process adas{
    def main(){

    }
}