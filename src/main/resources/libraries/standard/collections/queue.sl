package standard.collections.Queue

import standard.collections.LinkedList::LinkedList
import standard.Exception::IndexOutOfBoundsException

"""
A queue is a first-in-first-out (FIFO) data structure. Items are added to the end of the queue and removed from the front.
"""
public class Queue<T>{
    private var list = LinkedList<T>()
    
    def __init__(){
    }

    public void __destroy__(){
        list = null
    }

    """
    Adds an item to the end of the queue
    """
    void enqueue(T item){
        list.add(item)
    }

    """
    Removes an item from the front of the queue
    """
    T dequeue(){
        if (list == null){
            throw new IndexOutOfBoundsException("Index out of bounds in Queue")
        }
        else{
            T item = list.get(0)
            list.remove(0)
            return item
        }
    }

    """
    Returns the item at the front of the queue. Does not remove it.
    """
    T peek(){
        return list.get(0)
    }

    """
    Returns the number of items in the queue
    """
    int size(){
        return list.size()
    }

    """
    Returns true if the queue is empty
    """
    bool isEmpty(){
        return list.isEmpty()
    }

    """
    Removes all items from the queue
    """
    void clear(){
        list.clear()
    }
}