package cmd.bossbar

"""
Create a bossbar with `name` and text `display`
"""
def lazy create(mcobject $name, rawjson $display){
    /bossbar add $name $display
}

"""
Delete the bossbar with `name`
"""
def lazy delete(mcobject $name){
    /bossbar remove $name
}

"""
Set the color of bossbar `name` to `color`
"""
def lazy setColor(mcobject $name, mcobject $color){
    /bossbar set $name color $color
}

"""
Set the max of bossbar `name` to `value`
"""
def lazy setMax(mcobject $name, int $value){
    /bossbar set $name max $value
}

"""
Set the value of bossbar `name` to `value`
"""
def lazy setValue(mcobject $name, int $value){
    /bossbar set $name value $value
}

"""
Set the value of bossbar `name` to the variable `value`
"""
def lazy setValueVar(mcobject $name, int $value){
    /execute store result bossbar $name value run scoreboard players get $value.scoreboard
}

"""
Set the max of bossbar `name` to variable `value`
"""
def lazy setMaxVar(mcobject $name, int $value){
    /execute store result bossbar $name max run scoreboard players get $value.scoreboard
}

"""
Set the text of bossbar `name` to `value`
"""
def lazy setName(mcobject $name, rawjson $value){
    /bossbar set $name name $value
}

"""
Set the visibility of bossbar `name` to `value`
"""
def lazy setVisible(mcobject $name, int $value){
    /bossbar set $name visible $value
}

"""
Set the visibility of bossbar `name` to true
"""
def lazy show(mcobject $name){
    /bossbar set $name visible true
}

"""
Show the bossbar `name` to every player
"""
def lazy showEveryone(mcobject $name){
    /bossbar set $name visible true
    /bossbar set $name players @a
}

"""
Show the bossbar `name` to `player`
"""
def lazy showPlayer(mcobject $name, entity $player){
    /bossbar set $name visible true
    /bossbar set $name players $player
}

"""
Hide the bossbar `name`
"""
def lazy hide(mcobject $name){
    /bossbar set $name visible false
}

"""
Set the bossbar `name` player to `player`
"""
def lazy setPlayer(mcobject $name, entity $player){
    /bossbar set $name players $player
}

"""
Set the bossbar `name` style to be notched_10
"""
def lazy setNotched10(mcobject $name){
    /bossbar set $name style notched_10
}

"""
Set the bossbar `name` style to be notched_12
"""
def lazy setNotched12(mcobject $name){
    /bossbar set $name style notched_12
}

"""
Set the bossbar `name` style to be notched_20
"""
def lazy setNotched20(mcobject $name){
    /bossbar set $name style notched_20
}

"""
Set the bossbar `name` style to be notched_6
"""
def lazy setNotched6(mcobject $name){
    /bossbar set $name style notched_6
}

"""
Set the bossbar `name` style to be notched_0
"""
def lazy setNotched0(mcobject $name){
    /bossbar set $name style progress
}

struct Bossbar{
    bool visible
    lazy string name = "$this"
    lazy int id = name.hash()

    """
    Create a bossbar with text `display`
    """
    def lazy __init__(rawjson value = ((""),(""))){
        create(id, value)
    }
    
    """
    Delete the bossbar
    """
    def lazy delete(){
        delete(id)
    }
    
    """
    Set the color of bossbar `color`
    """
    def lazy setColor(int value){
        setColor(id, value)
    }
    
    """
    Set the max of bossbar to `value`
    """
    def lazy setMax(int value){
        if (Compiler.isVariable(value)){
            setMaxVar(id, value)
        }
        else
        {
            setMax(id, value)
        }
    }

    """
    Set the value of bossbar to `value`
    """
    def lazy setValue(int $value){
        if (Compiler.isVariable(value)){
            setValueVar(id, value)
        }
        else
        {
            setValue(id, value)
        }
    }

    """
    Set the value & max of bossbar to `value` & `max`
    """
    def lazy setValue(int value, int max){
        setValue(value)
        setMax(max)
    }
    
    """
    Set the text of bossbar `name` to `value`
    """
    def lazy setName(rawjson value){
        setName(id, value)
    }
    
    """
    Set the visibility of bossbar to `value`
    """
    def lazy setVisible(bool value){
        setVisible(id, value)
    }
    
   """
    Set the visibility of bossbar to true
    """
    def lazy show(){
        show(id)
    }
    
    """
    Show the bossbar to every player
    """
    def lazy showEveryone(){
        showEveryone(id)
    }
    
    """
    Show the bossbar `player`
    """
    def lazy showPlayer(entity player){
        showPlayer(id, player)
    }
    
    """
    Hide the bossbar
    """
    def lazy hide(){
        hide(id)
    }
    
    """
    Set the bossbar player to `player`
    """
    def lazy setPlayer(entity player){
        setPlayer(id, player)
    }
    
    """
    Set the bossbar style to be notched_10
    """
    def lazy setNotched10(){
        setNotched10(id)
    }

    """
    Set the bossbar style to be notched_12
    """
    def lazy setNotched12(){
        setNotched12(id)
    }
    
    """
    Set the bossbar style to be notched_20
    """
    def lazy setNotched20(){
        setNotched20(id)
    }

    """
    Set the bossbar style to be notched_6
    """
    def lazy setNotched6(){
        setNotched6(id)
    }
    
    """
    Set the bossbar style to be notched_0
    """
    def lazy setNotched0(){
        setNotched0(id)
    }
    
    """
    Hide all the bossbar
    """
    def @bossbar.shadow shadow(){
        hide()
    }

    """
    Unhide all the bossbar
    """
    def @bossbar.unshadow unshadow(){
        if (visible){
            show()
        }
    }
}