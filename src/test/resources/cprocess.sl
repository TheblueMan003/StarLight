package test

from test import Test
import utils.CProcess

Test run{
    int tick
    CProcess inner{
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
    CProcess inner{
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
    CProcess inner{
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


Test multiple{
    int tick
    CProcess inner{
        def main(){
        }
        def onStop(){
            tick = 10
        }
    }
    def asyncStart(){
        inner.start()
        inner.start()
        tick = 0
    }
    int getDuration(){
        return 10
    }
    bool getResult(){
        inner.stop()
        inner.stop()
        if (tick > 5){
            return true
        } else {
            return false
        }
    }
}


Test multiple2{
    int tick
    CProcess inner{
        def main(){
        }
        def onStop(){
            tick = 10
        }
    }
    def asyncStart(){
        inner.start()
        inner.start()
        tick = 0
    }
    int getDuration(){
        return 10
    }
    bool getResult(){
        inner.stop()
        
        if (tick == 0){
            return true
        } else {
            return false
        }
    }
}