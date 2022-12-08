package process

import cmd.schedule

int t_running, t_total

def @test.after show(){
    //print(("===[ Running Processes ]===",green))
    int running = 0
    int off = 0
    int unknown = 0
    int total = 0
    
    forgenerate($i,@process.count){
        t_running = 0
        t_total = 0
        $i()
        if (t_running == 1){
            //print((" [ON] $i",green))
            running++
        }
        else if (t_running == 0){
            //print((" [OFF] $i",red))
            off++
        }
        else{
            //print((" [??] $i",yellow))
            unknown++
        }
        total++
    }
    //print(("Stats: ",white),(f"{running}/{total} Running",green),(" - "), (f"{off}/{total} Off",red),(" - "),(f"{unknown}/{total} Unknown",yellow))
}


template process{
    int enabled
    int crashCount
    void=>void callback
    
    def loading reload(){
        run()
    }
    def crash(){
        //exception.exception("Stack Overlow detect in Process. Try to increase the maxCommandChainLength")
        crashCount++
        if (crashCount > 10){
            //exception.exception("Max Number of Stack Overflow reach. Process Killed.")
            enabled = 0
        }
        run()
    }
    def start(){
        enabled:=false
        if (!enabled){
            enabled = true
            onStart()
            run()
        }
    }
    def @process.main run(){
        asyncwhile(enabled){
            schedule.add(crash)
            if (enabled){
                main()
            }
            schedule.remove(crash)
            crashCount = 0
        }
    }
    def stop(){
        if (enabled){
            onStop()
            enabled = false
            callback()
            callback = null
        }
    }
    def waitFor(void=>void fct){
        callback = fct
    }
    def @process.count __count__(){
        t_total += 1
        t_running += enabled
    }
    def @process.stop stopall(){
        stop()
    }
}