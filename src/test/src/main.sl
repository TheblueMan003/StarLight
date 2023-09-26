package test

def macro test(int a){
    /say $(a)
}

def lol(){
    int c
    test(5)
    test(c)
}