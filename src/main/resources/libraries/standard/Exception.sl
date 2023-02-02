package standard.Exception

public class Exception{
    void=>void printStackTrace
    void=>void printMessage
    static lazy string __className = Compiler.getClassName()

    def lazy __init__(string message){
        printMessage = () =>{
            import standard::print
            print((__className, "dark_red"),message)
        }
    }
}
public class InvalidInputException extends Exception{
}
public class InvalidArgumentException extends Exception{
}
public class IndexOutOfBoundsException extends Exception{
}

package _
standard.Exception.Exception __exceptionThrown