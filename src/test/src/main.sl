package main

import utils.Process
import standard.collections.List

List<int>[100] lst

def start(){
    "test".toLower()
}

def test(){
    @p.effect.slowness()
    @s.tag.add("test")
    @s.tag.add("test2")
    @s.swap(@p)
}