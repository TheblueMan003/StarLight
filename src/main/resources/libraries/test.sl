package test

import standard
import utils.Process

int __pass__ = 0
int __fail__ = 0
int __total__ = 0

template Test extends Process{
    int time
    def onStart(){
        time = 0
        asyncStart()
    }
    def main(){
        time ++
        if (time >= getDuration()){
            stop()
        }
    }
    def onStop(){
        lazy var name = Compiler.getTemplateName()
        Compiler.insert($name, name){
            if (getResult()){
                standard.print(("[PASSED] ","green"),"$name")
                __pass__ ++
            }
            else{
                standard.print(("[FAILLED] ","red"),"$name")
                __fail__ ++
            }
            __total__ ++
        }
        TestRunner.next()
    }


    def lazy int getDuration(){
        return 2
    }
    def asyncStart(){
    }
    def bool getResult(){
        return false
    }
    def @test.launch launch(){
        start()
    }
    def showError<T>(T actual, T expected){
        standard.print(" - Expected: ",expected)
        standard.print(" - Actual: ",actual)
    }
}

Process TestRunner{
    int index
    bool running
    def onStart(){
        standard.print("Test started")
        index = -1
        running = false
        next()
    }
    def main(){

    }
    def onStop(){
        standard.print(("=============[Test Completed]=============", "gold"))
        standard.print(">> Total: ",__total__)
        standard.print(">> Passed: ",__pass__)
        standard.print(">> Failled: ",__fail__)
    }
    def next(){
        index ++
        running = false

        lazy var i = 0
        foreach(test in @test.launch){
            if (!running && i == index){
                running = true
                test()
            }
            i ++
        }
        if (index == i){
            stop()
        }
    }
}

public void runAll(){
    __pass__ = 0
    __fail__ = 0
    __total__ = 0
    TestRunner.start()
}