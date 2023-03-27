package test

from test import Test
import utils.Process

Test run{
    int tick
    Process inner{
        def main(){
            tick++
        }
    }
    def asyncStart(){
        inner.start()
        tick = 0
    }
    int getDuration(){
        return 10
    }
    bool getResult(){
        inner.stop()
        if (tick > 5){
            return true
        } else {
            return false
        }
    }
}

Test stop{
    int tick
    Process inner{
        def main(){
        }
        def onStop(){
            tick = 10
        }
    }
    def asyncStart(){
        inner.start()
        tick = 0
    }
    int getDuration(){
        return 10
    }
    bool getResult(){
        inner.stop()
        if (tick > 5){
            return true
        } else {
            return false
        }
    }
}

Test start{
    int tick
    Process inner{
        def main(){
        }
        def onStart(){
            tick = 10
        }
    }
    def asyncStart(){
        inner.start()
        tick = 0
    }
    int getDuration(){
        return 10
    }
    bool getResult(){
        inner.stop()
        if (tick > 5){
            return true
        } else {
            return false
        }
    }
}