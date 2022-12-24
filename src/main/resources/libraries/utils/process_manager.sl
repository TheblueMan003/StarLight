package utils.process_manager

import standard

int t_running, t_total

"""
Show the list of processes
"""
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

"""
Stop all the active processes
"""
def stopAll(){
    @process.stop()
}

"""
Return the number of currently active processes
"""
int count(){
    int total = 0
    
    forgenerate($i, @process.count){
        t_running = 0
        t_total = 0
        $i()
        total += t_running
    }

    return total
}