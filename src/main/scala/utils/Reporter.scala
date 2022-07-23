package utils

import Console._

object Reporter{
    var debugEnabled = false

    def ok(value: String) = {
        println(f"[${Console.GREEN}success${Console.WHITE}] ${value}")
    }
    def debug(value: Object) = {
        if (debugEnabled){
            println(f"[${Console.MAGENTA}debug${Console.WHITE}] ${value}")
        }
    }
    def info(value: String) = {
        println(f"[${Console.CYAN}info${Console.WHITE}] ${value}")
    }
    def warning(value: String) = {
        println(f"[${Console.YELLOW}warning${Console.WHITE}] ${value}")
    }
    def error(value: String) = {
        println(f"[${Console.RED}error${Console.WHITE}] ${value}")
    }
}