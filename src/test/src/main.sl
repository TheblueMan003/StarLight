package test

import cmd.title as title
import cmd.spawnpoint as spawnpoint
import cmd.gamemode as gm
import cmd.entity as entity
import cmd.tag as tag
import cmd.sound as sound
import cmd.particles as particles
import mc.java.nbt as nbt
import mc.pointer as pt
import standard

scoreboard int UUID
int uuid, guuid
entity markers

"""
Reset the spawnpoint
"""
void reset(){
    
}

private void mark(){
    guuid++
    UUID := guuid
    uuid = UUID
    with(markers,true,UUID==uuid){
        entity.despawn()
    }
    markers += pt.newPointer(){
        UUID = uuid
    }
}

"""
Teleport the player to last checkpoint
"""
void respawn(){
    entity.kill()
}

"""
Set the spawnpoint
"""
bool setSpawn(bool silent = false){
    lazy var sel = Compiler.mergeSelector(markers, @e[distance=..3])
    bool found = false
    uuid = UUID
    with(sel,true,UUID==uuid){
        found = true
    }
    if (!found){
        mark()
        spawnpoint.set()
        if (!silent){
            sound.play(minecraft:entity.player.levelup)
            particles.sphere(minecraft:totem_of_undying, 1, 1, 20)
        }
        return true
    }
    else{
        return false
    }
}

"""
Checkpoint for a block
"""
lazy void checkpoint(mcobject block, void=>void action){
    foreach(x in -1..1){
        foreach(z in -1..1){
            Compiler.insert($x,x){
                Compiler.insert($z,z){
                    at(~$x ~-1 ~$z){
                        if (block(~ ~-1 ~, block)){
                            if (setSpawn()){
                                action()
                            }
                        }
                    }
                }
            }
        }
    }
}

def lol(){
    checkpoint(minecraft:stone){
        /say hi
    }
}