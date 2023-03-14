package mc.java.display.DisplayBlock

import mc.java.display.DisplayEntity
import mc.java.data as data

"""
Class representing an block display entity.
"""
public class DisplayBlock extends DisplayEntity with minecraft:block_display for mcjava {
    def lazy __init__(mcobject block){
        setBlock(block)
    }

    """
    Sets the block to be displayed
    """
    def lazy setBlock(mcobject block){
        data.set({"block_state":{"Name":block}})
    }
    """
    Sets the block to be displayed
    """
    def lazy setBlockProperties(mcobject block, json properties){
        data.set({"block_state":{"Name":block, "Properties":properties}})
    }
    """
    Sets the block to be displayed
    """
    def lazy setProperties(json properties){
        data.set({"block_state":{"Properties":properties}})
    }
}