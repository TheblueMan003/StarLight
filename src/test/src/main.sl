package AnimatedEntity

import math

class A{
    def a(){
        /say a
    }
}
class B{
    def b(){
        /say b
    }
}
class C extends A implements B{
    def c(){
        /say c
    }
}

C c = new C()
c.a()
c.b()
c.c()

