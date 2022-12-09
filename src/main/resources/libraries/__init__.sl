def ticking __load__(){
    if (Compiler.isBedrock){
        int v
        if (v != 1){
            v = 1
            @__loading__()
        }
    }
}

class object{
    int this
    int __refCount

    def __addRef(){
        __refCount++
    }
    def __remRef(){
        __refCount--
    }
}