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

def private ticking main(){
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

def stop(){
    MusicTick = -10000000
    /stopsound @s record
    WasStopped = true
    currTrack = -1
}
def stopForAll(){
    with(@a){
        stop()
    }
}

def pause(){
    MusicTick = -10000000
    /stopsound @s record
    WasStopped = true
}
def pauseForAll(){
    with(@a){
        pause()
    }
}

def continue(){
    MusicTick = 10000000
    WasStopped = false
}
def continueForAll(){
    with(@a){
        MusicTick = 10000000
        WasStopped = false
    }
}


def disablePlayer(){
    Enabled = false
    /stopsound @s record
}
def enablePlayer(){
    Enabled = true
    if (!WasStopped){
        MusicTick = 10000000
    }
}

def togglePlayer(){
    if (Enabled){
        disablePlayer()
    }
    else{
        enablePlayer()
    }
}
def disableGlobal(){
    enabled = false
    /stopsound @a record
}
def enableGlobal(){
    enabled = true
}
def toggleGlobal(){
    if (enabled){
        disableGlobal()
    }
    else{
        enableGlobal()
    }
}