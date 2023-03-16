package cmd.spawnpoint


"""
Set the spawnpoint of the player to the current position.
"""
def lazy set(){
    /spawnpoint
}

"""
Set the spawnpoint of the player to the given position.
"""
def lazy set(mcposition $pos){
    /spawnpoint @s $pos
}

"""
Set the spawnpoint of the player to the given position and angle. (JAVA ONLY)
"""
def lazy set(mcposition $pos, float $angle){
    if (Compiler.isJava()){
        /spawnpoint @s $pos $angle
    }
    if (Compiler.isBedrock()){
        /spawnpoint @s $pos
    }
}


"""
Set the spawnpoint of the player to the current position.
"""
def lazy set(entity $player){
    /spawnpoint $player
}

"""
Set the spawnpoint of the player to the given position.
"""
def lazy set(entity $player, mcposition $pos){
    /spawnpoint $player $pos
}


"""
Set the spawnpoint of the player to the given position and angle. (JAVA ONLY)
"""
def lazy set(entity $player, mcposition $pos, float $angle){
    if (Compiler.isJava()){
        /spawnpoint $player $pos $angle
    }
    if (Compiler.isBedrock()){
        /spawnpoint $player $pos
    }
}


"""
Set the world spawnpoint to the current position.
"""
def lazy setWorld(){
    /setworldspawn
}

"""
Set the world spawnpoint to the given position.
"""
def lazy setWorld(mcposition $pos){
    /setworldspawn $pos
}

"""
Set the world spawnpoint to the given position and angle. (JAVA ONLY)
"""
def lazy setWorld(mcposition $pos, float $angle){
    if (Compiler.isJava()){
        /setworldspawn $pos $angle
    }
    if (Compiler.isBedrock()){
        /setworldspawn $pos
    }
}