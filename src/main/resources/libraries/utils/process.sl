package utils.process

if (Compiler.isJava){
    import cmd.schedule as schedule
}
import standard

int t_running, t_total

def @test.after show(){
    standard.print(("===[ Running Processes ]===","green"))
    int running = 0
    int off = 0
    int unknown = 0
    int total = 0
    
    forgenerate($i, @process.count){
        t_running = 0
        t_total = 0
        $i()
        if (t_running == 1){
            standard.print((" [ON] $i","green"))
            running++
        }
        else if (t_running == 0){
            standard.print((" [OFF] $i","red"))
            off++
        }
        else{
            standard.print((" [??] $i","yellow"))
            unknown++
        }
        total++
    }
    standard.print(("Stats: ","white"),(running,"green"),("/"),(total,"green"),(" Running","green"),(" - "),
                    (off,"red"),("/"),(total,"red"),(" Off","red"),(" - "),
                    (unknown,"yellow"),("/"),(total,"yellow"),(" Unknown","yellow"),(" - "))
}


template Process{
    int enabled
    int crashCount
    void=>void callback
    
    def loading reload(){
        if (Compiler.isJava){
            run()
        }
    }
    def crash(){
        //exception.exception("Stack Overlow detect in Process. Try to increase the maxCommandChainLength")
        crashCount++
        if (crashCount > 10){
            //exception.exception("Max Number of Stack Overflow reach. Process Killed.")
            enabled = 0
        }
        if (Compiler.isJava){
            run()
        }
    }
    def start(){
        enabled:=false
        if (!enabled){
            enabled = true
            onStart()
            if (Compiler.isJava){
                run()
            }
        }
    }
    if (Compiler.isJava){
        def @process.main run(){
            schedule.asyncwhile(enabled){
                schedule.add(crash)
                if (enabled){
                    main()
                }
                schedule.remove(crash)
                crashCount = 0
            }
        }
    }
    if (Compiler.isBedrock){
        bool crashDetect
        def ticking mainLoop(){
            if (enabled){
                if (crashDetect){
                    crash()
                }
                crashDetect = true
                main()
                crashDetect = false
            }
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