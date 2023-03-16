package mc.java.display.DisplayText

import mc.java.display.DisplayEntity
import cmd.java.data as data

"""
Class representing an text display entity.
"""
public class DisplayText extends DisplayEntity with minecraft:text_display for mcjava {
    def __init__(){
    }
    def __init__(mcobject block){
        setBlock(block)
    }

    """
    Set the text to be displayed
    """
    def lazy setText(string text){
        data.set({"text":text})
    }

    """
    Set Line Width
    """
    def lazy setLineWidth(int width){
        data.set({"line_width":width})
    }

    """
    Set the text opcacity
    """
    def lazy setOpacity(int opacity){
        data.set({"text_opacity":opacity})
    }

    """
    Set the background color
    """
    def lazy setBackgroundColor(int color){
        data.set({"background":color})
    }

    """
    Set default background
    """
    def lazy setDefaultBackground(){
        data.set({"background":true})
    }

    """
    Set shadow
    """
    def lazy setShadow(){
        data.set({"shadow":true})
    }

    """
    Set See Through
    """
    def lazy setSeeThrough(){
        data.set({"see_through":true})
    }

    """
    Set alignment to center
    """
    def lazy setCenter(){
        data.set({"alignment":"center"})
    }

    """
    Set alignment to left
    """
    def lazy setLeft(){
        data.set({"alignment":"left"})
    }

    """
    Set alignment to right
    """
    def lazy setRight(){
        data.set({"alignment":"right"})
    }
}