package mc.java.display.DisplayItem

import mc.java.display.DisplayEntity
import cmd.java.data as data

"""
Class representing an item display entity.
"""
public class DisplayItem extends DisplayEntity with minecraft:item_display for mcjava {
    def __init__(){
    }
    def __init__(mcobject block){
        setBlock(block)
    }
    
    """
    Set the item to be displayed
    """
    def lazy setItem(mcobject block){
        data.set({"item":{"id": block, Count: 1}})
    }

    """
    Set the display mode to none
    """
    def lazy setDisplayModeNone(){
        data.set({"item_display":"none"})
    }

    """
    Set the display mode to third person left hand
    """
    def lazy setDisplayModeThirdPersonLeftHand(){
        data.set({"item_display":"thirdperson_lefthand"})
    }

    """
    Set the display mode to third person right hand
    """
    def lazy setDisplayModeThirdPersonRightHand(){
        data.set({"item_display":"thirdperson_righthand"})
    }

    """
    Set the display mode to first person left hand
    """
    def lazy setDisplayModeFirstPersonLeftHand(){
        data.set({"item_display":"firstperson_lefthand"})
    }

    """
    Set the display mode to first person right hand
    """
    def lazy setDisplayModeFirstPersonRightHand(){
        data.set({"item_display":"firstperson_righthand"})
    }

    """
    Set the display mode to head
    """
    def lazy setDisplayModeHead(){
        data.set({"item_display":"head"})
    }

    """
    Set the display mode to gui
    """
    def lazy setDisplayModeGui(){
        data.set({"item_display":"gui"})
    }

    """
    Set the display mode to ground
    """
    def lazy setDisplayModeGround(){
        data.set({"item_display":"ground"})
    }

    """
    Set the display mode to fixed
    """
    def lazy setDisplayModeFixed(){
        data.set({"item_display":"fixed"})
    }
}