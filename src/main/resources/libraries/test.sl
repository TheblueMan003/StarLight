package test

import standard

template Test{
    def loading run(){
        if (main()){
            standard.print(("[PASSED] ","green"),"$this")
            /scoreboard players add __pass__ tbms.var 1
        }
        else{
            standard.print(("[FAILLED] ","red"),"$this")
        }
    }

}