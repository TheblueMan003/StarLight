package sl

import Console._

object Reporter{
    var debugEnabled = false

    def ok(value: String) = {
        println(f"[${Console.GREEN}success${Console.WHITE}] ${value}")
    }
    def input() = {
        print(f"StarLight${Console.CYAN}>${Console.WHITE} ")
    }
    def debug(value: Object) = {
        println(f"[${Console.MAGENTA}debug${Console.WHITE}] ${value}")
    }
    def info(value: String) = {
        println(f"[${Console.CYAN}info${Console.WHITE}] ${value}")
    }
    def warning(value: String) = {
        println(f"[${Console.YELLOW}warning${Console.WHITE}] ${value}")
    }
    def phase(value: String) = {
        println(f"${Console.YELLOW}========${value}========${Console.WHITE}")
    }
    def error(value: String) = {
        println(f"[${Console.RED}error${Console.WHITE}] ${value}")
    }
}