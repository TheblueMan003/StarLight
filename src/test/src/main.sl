package test


import standard::print

import random

int[52] array

def lazy shuffle(int array, int length){
    def swap(){
        int a = random.range(0, length)
        int b = random.range(0, length)
        int t = array[a]
        array[a] = array[b]
        array[b] = t
    }
    for(int i = 0; i < length; i++){
        array[i] = i
    }
    for(int i = 0; i < 200; i++){
        swap()
    }
}
def test(){
    shuffle(array, 52)
}