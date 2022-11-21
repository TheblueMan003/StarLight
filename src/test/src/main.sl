package test

struct test{
    int bruh
    int sup(){
        bruh += 1
        return bruh
    }
}
struct timer{
    int h, m, s, t

    def update(){
        t += 1
    }
    def lazy __set__(int time){
        h = time
        m = time
        s = time
        t = time
    }
    def lazy __set__(timer other){
        h = other.h
        m = other.m
        s = other.s
        t = other.t
    }
    def lazy __set__(test other){
        update()
        h = other.sup()
    }
    def lazy __add__(timer other){
        h += other.h
        m += other.m
        s += other.s
        t += other.t
    }
}
timer a
timer b
test c
def ticking main(){
    a = c
    a.update()
}

