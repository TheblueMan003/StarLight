package standard.array

class Array<T>{
    private Array<T> next
    private T[20] values

    public void __init__(int size){
        if (size > 20){
            next = new Array<T>(size-20)
        }
    }

    public void set(int index, T value){
        if (index > 20){
            print("set next")
            next[index-20] = value
        }
        else{
            values[index] = value
        }
    }
    public T __get__(int index){
        if (index > 20){
            return next[index-20]
        }
        else{
            return values[index]
        }
    }
}