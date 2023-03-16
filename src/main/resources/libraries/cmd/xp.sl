package cmd.xp


"""
Add the given amount of xp to the player.
"""
def lazy add(int $value){
    if (Compiler.isJava()){
        /xp add @s $value
    }
    if (Compiler.isBedrock()){
        /xp $value
    }
}

"""
Adds the given amount of levels to the player.
"""
def lazy addLevel(int $value){
    if (Compiler.isJava()){
        /xp add @s $value levels
    }
    if (Compiler.isBedrock()){
        /xp $valueL
    }
}

"""
Sets the xp of the player to the given value.
"""
def lazy set(int $value){
    if (Compiler.isJava()){
        /xp set @s $value
    }
    if (Compiler.isBedrock()){
        /xp -100000L
        /xp -100000
        /xp $value
    }
}

"""
Sets the level of the player to the given value.
"""
def lazy setLevel(int $value){
    if (Compiler.isJava()){
        /xp set @s $value levels
    }
    if (Compiler.isBedrock()){
        /xp -100000L
        /xp -100000
        /xp $valueL
    }
}