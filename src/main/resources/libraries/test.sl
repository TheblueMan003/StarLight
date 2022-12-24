package test

import standard
from utils.process import Process

template Test extends Process{
    int time
    def onStart(){
        time = 0
        run()
    }
    def main(){
        time ++
        if (time > getDuration()){
            stop()
        }
    }
    def onStop(){
        if (getResult()){
            standard.print(("[PASSED] ","green"),"$this")
            /scoreboard players add __pass__ tbms.var 1
        }
        else{
            standard.print(("[FAILLED] ","red"),"$this")
        }
    }


    def lazy int getDuration(){
        return 1
    }
    def run(){
    }
    def bool getResult(){
    }
}