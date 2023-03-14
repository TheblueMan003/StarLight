package mc.java.shader

import cmd.schedule as schedule
import cmd.title as title
import cmd.entity as entity
import cmd.gamemode as gamemode
import cmd.tp as tp
import math.Vector3 as Vector3

scoreboard int Shader
scoreboard gamemode.Gamemode Gamemode
scoreboard Vector3 Pos
[criterion="minecraft.custom:minecraft.sneak_time"] scoreboard int SSneak
entity failled
entity shader
private void save(){
    Pos = math.Vector3.getPosition()
}
private void reload(){
    at(Pos){
        /tp @s ~ ~ ~ ~ ~
    }
}

private void killPlayer(){
    int hasKeepInventory
    Compiler.cmdstore(hasKeepInventory){
        /gamerule keepInventory
    }
    int hasImmediateRespawn
    Compiler.cmdstore(hasImmediateRespawn){
        /gamerule doImmediateRespawn
    }
    /gamerule keepInventory true
    /gamerule doImmediateRespawn true
    /kill @s
    if (!hasKeepInventory)./gamerule keepInventory false
    if (!hasImmediateRespawn)./gamerule doImmediateRespawn false
}
    
"""
Add the creeper shader to the player
"""
def creeper(){
    if (SSneak){
        title.show(("warning","red"))
        title.showSubtitle(("Stop Sneaking","red"))
        failled += @s
        schedule.add(1){
            with(failled,true){
                failled -= @s
                creeper()
            }
        }
        SSneak = 0
    }
    else{
        SSneak = 0
        save()
        title.show(("","red"))
        title.showSubtitle(("","red"))
        /spawnpoint @s ~ ~ ~ ~
        entity mob
        with(mob,true){
            entity.despawn()
        }
        mob = entity.summon(minecraft:creeper, {NoAI:true,Invulnerable:true})
        Gamemode = gamemode.get()
        with(mob){
            /tp @s ~ ~ ~ ~ ~
        }
        gamemode.spectator()
        entity.spectate(mob)
        Shader = 1
        shader += @s
        schedule.add(5){
            with(shader){
                killPlayer()
            }
        }
        schedule.add(10){
            with(shader,true){
                reload()
                gamemode.set(Gamemode)
                shader -= @s
                if (SSneak){
                    creeper()
                }
            }
            with(mob,true){
                entity.despawn()
            }
        }
    }
}

"""
Clear the shader
"""
def clear(){
    if (SSneak){
        title.show(("warning","red"))
        title.showSubtitle(("Stop Sneaking","red"))
        failled += @s
        schedule.add(1){
            with(failled,true){
                failled -= @s
                clear()
            }
        }
        SSneak = 0
    }
    else{
        SSneak = 0
        save()
        title.show(("","red"))
        title.showSubtitle(("","red"))
        /spawnpoint @s ~ ~ ~ ~
        entity mob
        with(mob,true){
            entity.despawn()
        }
        mob = entity.summon(minecraft:armor_stand, {NoAI:true,Invulnerable:true})
        Gamemode = gamemode.get()
        with(mob){
            /tp @s ~ ~ ~ ~ ~
        }
        gamemode.spectator()
        entity.spectate(mob)
        Shader = 1
        shader += @s
        schedule.add(5){
            with(shader){
                killPlayer()
            }
        }
        schedule.add(10){
            with(shader,true){
                reload()
                gamemode.set(Gamemode)
                shader -= @s
                if (SSneak){
                    clear()
                }
            }
            with(mob,true){
                entity.despawn()
            }
        }
    }
}

"""
Add the spider shader to the player
"""
def spider(){
    if (SSneak){
        title.show(("warning","red"))
        title.showSubtitle(("Stop Sneaking","red"))
        failled += @s
        schedule.add(1){
            with(failled,true){
                failled -= @s
                spider()
            }
        }
        SSneak = 0
    }
    else{
        SSneak = 0
        save()
        title.show(("","red"))
        title.showSubtitle(("","red"))
        /spawnpoint @s ~ ~ ~ ~
        entity mob
        with(mob,true){
            entity.despawn()
        }
        mob = entity.summon(minecraft:spider, {NoAI:true,Invulnerable:true})
        Gamemode = gamemode.get()
        with(mob){
            /tp @s ~ ~ ~ ~ ~
        }
        gamemode.spectator()
        entity.spectate(mob)
        Shader = 1
        shader += @s
        schedule.add(5){
            with(shader){
                killPlayer()
            }
        }
        schedule.add(10){
            with(shader,true){
                reload()
                gamemode.set(Gamemode)
                shader -= @s
                if (SSneak){
                    spider()
                }
            }
            with(mob,true){
                entity.despawn()
            }
        }
    }
}


"""
Add the enderman shader to the player
"""
def enderman(){
    if (SSneak){
        title.show(("warning","red"))
        title.showSubtitle(("Stop Sneaking","red"))
        failled += @s
        schedule.add(1){
            with(failled,true){
                failled -= @s
                enderman()
            }
        }
        SSneak = 0
    }
    else{
        SSneak = 0
        save()
        title.show(("","red"))
        title.showSubtitle(("","red"))
        /spawnpoint @s ~ ~ ~ ~
        entity mob
        with(mob,true){
            entity.despawn()
        }
        mob = entity.summon(minecraft:enderman, {NoAI:true,Invulnerable:true})
        Gamemode = gamemode.get()
        with(mob){
            /tp @s ~ ~ ~ ~ ~
        }
        gamemode.spectator()
        entity.spectate(mob)
        Shader = 1
        shader += @s
        schedule.add(5){
            with(shader){
                killPlayer()
            }
        }
        schedule.add(10){
            with(shader,true){
                reload()
                gamemode.set(Gamemode)
                shader -= @s
                if (SSneak){
                    enderman()
                }
            }
            with(mob,true){
                entity.despawn()
            }
        }
    }
}