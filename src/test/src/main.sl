package test

import standard::print

[nbt="Pos[0]"] scoreboard json x
def macro bar(int a){
    a = 1
    /say $a
}
def lol(int a){
    bar(5)
}