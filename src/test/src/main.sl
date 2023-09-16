package test

import standard::print

class A{
    B b
    void=>B get

    def test(){
        b = get()
    }
}
class B{

}

def test(){
    A a = new A()
}