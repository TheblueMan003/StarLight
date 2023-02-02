package standard.collections.LinkedList

import standard.Exception::IndexOutOfBoundsException

"""
Simple linked list implementation
"""
public class LinkedList<T>{
    private LinkedList<T> next
    private T value
    private bool hasValue

    public void __init__(T value){
        this.value = value
        hasValue = true
    }
    public void __init__(){
        hasValue = false
    }


    """
    Get the value at the given index
    """
    public void set(int index, T value){
        if (index > 0 && next == null){
            print("Index out of bounds")
        }
        else if (index > 0){
            next[index-1] = value
        }
        else{
            this.value = value
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
    Get the value at the given index
    """
    public T __get__(int index){
        if (!hasValue){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 0 && next == null){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 0){
            return next[index-1]
        }
        else{
            return value
        }
    }

    """
    Add the value at the end of the list
    """
    public void add(T value){
        if (!hasValue){
            this.value = value
            hasValue = true
        }
        else if (next == null){
            next = new LinkedList<T>(value)
        }
        else{
            next.add(value)
        }
    }

    """
    Add the value at the given index
    """
    public void add(int index, T value){
        if (!hasValue){
            print("Index out of bounds")
        }
        else if (index > 0 && next == null){
            print("Index out of bounds")
        }
        else if (index > 0){
            next.add(index-1, value)
        }
        else{
            next = new LinkedList<T>(this.value)
            this.value = value
        }
    }

    """
    Remove all values
    """
    public void clear(){
        if (next != null){
            next.clear()
            next = null
        }
        hasValue = false
    }

    """
    Check if the list contains the given value
    """
    public bool contains(T value){
        if (!hasValue){
            return false
        }
        else if (this.value == value){
            return true
        }
        else if (next != null){
            return next.contains(value)
        }
        else{
            return false
        }
    }

    """
    Get the number of values in the list
    """
    public int count(){
        if (!hasValue){
            return 0
        }
        else if (next != null){
            return 1 + next.count()
        }
        else{
            return 1
        }
    }

    """
    Get the index of the given value. Returns -1 if the value is not in the list
    """
    public int indexOf(T value){
        if (!hasValue){
            return -1
        }
        else if (this.value == value){
            return 0
        }
        else if (next != null){
            int index = next.indexOf(value)
            if (index == -1){
                return -1
            }
            else{
                return 1 + index
            }
        }
        else{
            return -1
        }
    }

    """
    Insert the value at the given index
    """
    public void insert(int index, T value){
        if (!hasValue){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 0 && next == null){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 0){
            next.insert(index-1, value)
        }
        else{
            next = new LinkedList<T>(this.value)
            this.value = value
        }
    }
    
    """
    Check if the list is empty
    """
    public bool isEmpty(){
        return !hasValue
    }

    """
    Remove the first occurence of the given value and return true if the value was found
    """
    public bool remove(T value){
        if (!hasValue){
            return false
        }
        else if (this.value == value){
            if (next != null){
                this.value = next.value
                next = next.next
                return true
            }
            else{
                hasValue = false
                return true
            }
        }
        else if (next != null){
            return next.remove(value)
        }
        else{
            return false
        }
    }

    """
    Remove the value at the given index
    """
    public void removeAt(int index){
        if (!hasValue){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 0 && next == null){
            throw new IndexOutOfBoundsException("Index out of bounds in LinkedList")
        }
        else if (index > 0){
            next.remove(index-1)
        }
        else{
            if (next != null){
                this.value = next.value
                next = next.next
            }
            else{
                hasValue = false
            }
        }
    }
    
    """
    Sort the list in ascending order
    """
    public void sort(){
        if (next != null){
            next.sort()
            if (value > next.value){
                T temp = value
                value = next.value
                next.value = temp
            }
        }
    }
    """
    Sort the list in descending order if reverse is true
    """
    public void sort(bool reverse){
        if (!reverse){
            sort()
        }
        else if (next != null){
            next.sort(reverse)
            if (value < next.value){
                T temp = value
                value = next.value
                next.value = temp
            }
        }
    }

    """
    Get the list as an array
    """
    public T[] toArray(){
        T[] array = new T[count()]
        toArray(array, 0)
        return array
    }

    """
    Fill the array with the values of the list
    """
    public void toArray(T[] array, int index){
        if (hasValue){
            array[index] = value
            if (next != null){
                next.toArray(array, index+1)
            }
        }
    }

    """
    Print the list
    """
    public void print(){
        if (hasValue){
            print(value)
            if (next != null){
                next.print()
            }
        }
    }
}