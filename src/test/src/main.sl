package a

from game.room import Room
import standard

Room test{
    init(0,256,0,10,0,10)
    def onEnter(){
        ./say enter
    }
    def onActivate(){
        ./say activate
    }
    def onExit(){
        ./say exit
    }
    def onDisactivate(){
        ./say desactivate
    }
}