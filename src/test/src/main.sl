package test

import game.score

import math.raycast as r
enum B{
    a,b,c
}
class A{
    def lazy __init__(int a){

    }
    def virtual B test(){

    }
}

A a = new A(1)
a.__init__(1)
blocktag floor{
    minecraft:stone,
    minecraft:grass
}
def test(){
    r.shoot(10,1,block(minecraft:stone)){
        /say hi
    }
}

package c

def test(void=>void a){
    a()
}

def test2(){
    test(){
        /say lol
    }
}

package a

def test(void=>void a){
    a()
}

def test2(){
    test(){
        /say lol
    }
}

package b

def test(void=>void a){
    a()
}

def test2(){
    test(){
        /say lol
    }
}

package d

import standard

int lol(int a){
    if (a > 0){
        return 0
    }
    if (a <= 0){
        return 1
    }
}

public void test(){
    int b = lol(1)
    standard.print(b)

    int c = lol(0)
    standard.print(c)
}