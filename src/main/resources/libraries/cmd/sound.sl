package cmd.sound

"""
Play the sound `sound` to `selector` with `volume` and `pitch` on channel `loc`
"""
def lazy play(entity $selector mcobject $sound, int $volume, int $pitch, mcobject $loc){
    require $pitch <= 2 "pitch must be in [0,2]"
    require $pitch >= 0 "pitch must be in [0,2]"
    require $volume >= 0 "volume must be in [0,+inf["
    if (Compiler.isJava()){
        /playsound $sound $loc $selector ~ ~ ~ $volume pitch
    }
    else{
        /playsound $sound $selector ~ ~ ~ $volume $pitch
    }
}

"""
Play the sound `sound` with `volume` and `pitch`
"""
def lazy play(mcobject sound, float volume, float pitch){
    play(@s, sound, volume, pitch, master)
}

"""
Play the sound `sound` with volume = 1 and `pitch`
"""
def lazy play(mcobject sound, int pitch){
    play(@s, sound, 1, pitch, master)
}

"""
Play the sound `sound` with volume = pitch = 1
"""
def lazy play(mcobject sound){
    play(@s, sound, 1, 1, master)
}

"""
Play the sound `sound` with `volume` and `pitch` at the player 
"""
def lazy playAt(mcobject sound, float volume, float pitch){
    at(@s)play(@s, sound, volume, pitch, master)
}

"""
Play the sound `sound` with volume = 1 and `pitch` at the player 
"""
def lazy playAt(mcobject sound, int pitch){
    at(@s)play(@s, sound, 1, pitch, master)
}

"""
Play the sound `sound` with volume = pitch = 1 at the player 
"""
def lazy playAt(mcobject sound){
    at(@s)play(@s, sound, 1, 1, master)
}