package standard.collections.ArrayList

import standard.Exception::IndexOutOfBoundsException

"""
A generic ArrayList class
"""
class ArrayList<T>{
    private ArrayList<T> next
    private T[20] values
    private int size

    public void __init__(int size){
        if (size > 20){
            next = new ArrayList<T>(size-20)
        }
        this.size = size
    }

    """
    Sets the value at the given index to the given value
    """
    public void set(int index, T value){
        if (index >= size || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 20){
            next[index-20] = value
        }
        else{
            values[index] = value
        }
    }
    
    """
    Free Memory
    """
    public override void __destroy__(){
        if (next != null){
            next = null
        }
    }

    """
    Returns the value at the given index
    """
    public T __get__(int index){
        if (index >= size || index < 0){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 20){
            return next[index-20]
        }
        else{
            return values[index]
        }
    }

    """
    Adds the given value to the end of the list
    """
    public void add(T value){
        if (size > 20 && next == null){
            next = new ArrayList<T>(size-20)
            next.add(value)
        }
        else if (size > 20){
            next.add(value)
        }
        else{
            values[size] = value
        }
        size++
    }
    
    """
    Return true if the list contains the given value
    """
    public bool contains(T value){
        bool found = false
        for (int i = 0; i < size; i++){
            if (this[i] == value){
                found = true
            }
        }
        if (found){
            return true
        }
        else if (next == null){
            return false
        }
        else{
            return next.contains(value)
        }
    }

    """
    Returns the index of the given value
    """
    public int indexOf(T value){
        for (int i = 0; i < size; i++){
            if (this[i] == value){
                return i
            }
        }
        if (next == null){
            return -1
        }
        else{
            return next.indexOf(value)+20
        }
    }

    """
    Returns the size of the list
    """
    public int count(){
        if (next == null){
            return size
        }
        else{
            return size + next.count()
        }
    }
    private void reduceSizeTail(){
        if (next == null){
            size--
        }
        else{
            next.reduceSizeTail()
        }
    }

    """
    Removes the given value
    """
    public bool remove(T value){
        int index = indexOf(value)
        int size = count()
        if (index == -1){
            return false
        }
        else{
            for(int i = index; i < size-1; i++){
                this[i] = this[i+1]
            }
            reduceSizeTail()
            return true
        }
    }

    """
    Removes the value at the given index
    """
    public bool removeAt(int index){
        int size = count()
        if (index >= size || index < 0){
            return false
        }
        else{
            for(int i = index; i < size-1; i++){
                this[i] = this[i+1]
            }
            reduceSizeTail()
            return true
        }
    }


    """
    Clear the list
    """
    public void clear(){
        next = null
        size = 0
    }
}