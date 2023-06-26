package main

import mc.java.resourcespack.models as models
import mc.java.display.DisplayItem

if (Compiler.isJava()){
    models.make(minecraft:birch_boat, ("item/screen_background", 1))
}
def summon(){
    DisplayItem background = new DisplayItem()
    background.setItem("minecraft:birch_boat", {CustomModelData: 1})
    background.setScale(3,3,3)
}