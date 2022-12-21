package utils.process

if (Compiler.isJava){
    import cmd.schedule as schedule
}
import standard
import utils.process_manager as pr

"""
Represent a task that run along side the main tick function.
Function to Override:
- main: Main function that is repeated as long as the process is running
- onStart: callback when the process is star. Cannot be called if the process was is running
- onStop: callback when the process is stop. Cannot be called if the process was not running
"""
template Process{
    int enabled
    int crashCount
    void=>void callback
    
    """
    Restart the process on load. (JAVA Only)
    """
    def loading reload(){
        if (Compiler.isJava){
            run()
        }
    }

    """
    Detect maxCommandChainLength extended, and stop process if more than 10 in a row
    """
    def crash(){
        //exception.exception("Stack Overlow detect in Process. Try to increase the maxCommandChainLength")
        crashCount++
        if (crashCount > 10){
            //exception.exception("Max Number of Stack Overflow reach. Process Killed.")
            enabled = 0
        }
        else if (Compiler.isJava){
            run()
        }
    }

    """
    Start the process
    """
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
        """
        Main loop for the process (JAVA Only)
        """
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
        """
        Main loop for the process (Bedrock Only)
        """
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

    """
    Stop the process
    """
    def stop(){
        if (enabled){
            onStop()
            enabled = false
            callback()
            callback = null
        }
    }

    """
    Add a callback for when the process stop
    """
    def waitFor(void=>void fct){
        callback = fct
    }

    """
    Count the number of active process
    """
    def @process.count __count__(){
        pr.t_total += 1
        pr.t_running += enabled
    }

    """
    Stop the process
    """
    def @process.stop stopall(){
        stop()
    }

    def onStop(){
    }
    def onStart(){
    }
}