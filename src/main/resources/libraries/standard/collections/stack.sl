package standard.collections.Stack

import standard.Exception::IndexOutOfBoundsException

"""
A stack node is a node in a stack. It contains a value and a reference to the next node in the stack.
"""
private class StackNode<T>{
    private StackNode<T> next
    private T value

    def __init__(T value){
        this.value = value
    }
    def __init__(T value, StackNode<T> next){
        this.value = value
        this.next = next
    }
    def __destroy__(){
        next = null
    }
}

"""
A stack is a data structure that stores a list of elements, with two principal operations:
push, which adds an element to the top of the stack, and pop, which removes the element from the top of the stack.
"""
public class Stack<T>{
    private StackNode<T> list

    def __init__(){
    }
    def __destroy__(){
        list = null
    }

    """
    Pushes a value onto the stack.
    """
    void push(T value){
        list = new StackNode<T>(value, list)
    }

    """
    Pops a value off the stack. Returns the value at the top of the stack. Removes the value from the stack.
    """
    T pop(){
        if(list == null){
            throw new IndexOutOfBoundsException("Index out of bounds in Stack")
        }
        else{
            T value = list.value
            list = list.next
            return value
        }
    }

    """
    Peeks at the top of the stack. Returns the value at the top of the stack. Does not remove the value.
    """
    T peek(){
        if(list == null){
            throw new IndexOutOfBoundsException("Index out of bounds in Stack")
        }
        else{
            return list.value
        }
    }

    """
    Returns true if the stack is empty.
    """
    bool isEmpty(){
        return list == null
    }

    """
    Returns the number of elements in the stack.
    """
    int size(){
        int size = 0
        StackNode<T> node = list
        while(node != null){
            size++
            node = node.next
        }
        return size
    }

    """
    Clear the stack.
    """
    void clear(){
        list = null
    }
}