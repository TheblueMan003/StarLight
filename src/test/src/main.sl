package test

class a{
    int a
    lazy int get(){
        return a
    }
    def set(int b){
        a = b
    }
}
class b extends a{
    def test2(){
        /setblock2
    }
}

def lazy int bro(){
    return 1
}

def test(){
    b blah
    int pp = blah.get()
    blah.set(4)
    blah.test2()
}
test()