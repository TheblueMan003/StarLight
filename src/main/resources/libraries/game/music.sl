package game.music

import mc.resourcespack.sound as sounds

lazy var trackCount = 0
lazy var musicDic = {}
scoreboard int currTrack
scoreboard int MusicTick
scoreboard bool Intro
scoreboard bool Enabled
scoreboard bool WasStopped
bool enabled := true

"""
Play the music with the given name
"""
def lazy play(string name){
    lazy val trackID = musicDic[name]
    WasStopped:=false
    if ((currTrack != trackID) || WasStopped){
        currTrack = trackID
        MusicTick = 10000000
        Enabled := true
        WasStopped = false
    }
}

"""
Add a new music track with the given name, file, and length
file must be inside the music folder
"""
def lazy add(string name, string file, int min, int sec){
    lazy val trackID = trackCount
    trackCount++
    musicDic[name] = trackID
    
    def @game.music.track loop(){
        if (currTrack == trackID && MusicTick >= (min*60+sec)*20){
            /stopsound @s record
            if (enabled && Enabled){
                Compiler.insert($file, file){
                    /playsound minecraft:music.$file record @s ~ ~ ~ 1
                }
            }
            MusicTick = 0
        }
    }
    sounds.music("music/"+file)
}

def private @tick main(){
    enabled := true
    if (enabled){
        with(@a,true){
            Enabled := true
            currTrack := -1
            if (Enabled && currTrack >= 0){
                MusicTick++
                @game.music.track()
                if (Compiler.isJava()){
                    import mc.java.input as input
                    input.onRelog(){
                        MusicTick = 10000000
                    }
                }
            }
        }
    }
}

"""
Stop the music
"""
public void stop(){
    MusicTick = -10000000
    /stopsound @s record
    WasStopped = true
    currTrack = -1
}

"""
Stop the music for all players
"""
public void stopForAll(){
    with(@a){
        stop()
    }
}

"""
Pause the music
"""
public void pause(){
    MusicTick = -10000000
    /stopsound @s record
    WasStopped = true
}

"""
Pause the music for all players
"""
public void pauseForAll(){
    with(@a){
        pause()
    }
}

"""
Continue the music
"""
public void continue(){
    MusicTick = 10000000
    WasStopped = false
}

"""
Continue the music for all players
"""
public void continueForAll(){
    with(@a){
        MusicTick = 10000000
        WasStopped = false
    }
}

"""
Disable the music for the player
"""
public void disablePlayer(){
    Enabled = false
    /stopsound @s record
}

"""
Enable the music for the player
"""
public void enablePlayer(){
    Enabled = true
    if (!WasStopped){
        MusicTick = 10000000
    }
}

"""
Toggle the music for the player
"""
public void togglePlayer(){
    if (Enabled){
        disablePlayer()
    }
    else{
        enablePlayer()
    }
}
    
"""
Disable the music for all players
"""
public void disableGlobal(){
    enabled = false
    /stopsound @a record
}

"""
Enable the music for all players
"""
public void enableGlobal(){
    enabled = true
}

"""
Toggle the music for all players
"""
public void toggleGlobal(){
    if (enabled){
        disableGlobal()
    }
    else{
        enableGlobal()
    }
}