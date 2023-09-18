package test

import standard::print

class A{
    int a
}
interface B{
    int b
}
class C extends A implements B{
    def this(){
        this.a = 1
        this.b = 2
    }
}
C c = new C()