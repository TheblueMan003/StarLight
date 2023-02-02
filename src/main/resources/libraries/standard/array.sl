package standard.array

import standard.Exception::IndexOutOfBoundsException

class Array<T>{
    private Array<T> next
    private T[20] values
    private int size

    public void __init__(int size){
        if (size > 20){
            next = new Array<T>(size-20)
        }
        this.size = size
    }

    public void set(int index, T value){
        if (index > size || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds in Array")
        }
        else if (index > 20){
            next[index-20] = value
        }
        else{
            values[index] = value
        }
    }
    public override void __destroy__(){
        if (next != null){
            next = null
        }
    }
    public T __get__(int index){
        if (index > size || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds in Array")
        }
        else if (index > 20){
            return next[index-20]
        }
        else{
            return values[index]
        }
    }
}