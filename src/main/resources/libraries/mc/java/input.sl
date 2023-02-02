package mc.java.input

import cmd.schedule as schedule
import mc.inventory as inventory

private enum eventList(string criterion){
    Click("minecraft.used:minecraft.carrot_on_a_stick"),
    ClickWarpedFungus("minecraft.used:minecraft.warped_fungus_on_a_stick"),
    Jump("minecraft.custom:minecraft.jump"),
    Drop("minecraft.custom:minecraft.drop"),
    Die("minecraft.custom:minecraft.deaths"),
    Hit("minecraft.custom:minecraft.damage_dealt"),
    Relog("minecraft.custom:minecraft.leave_game"),
    Damage("minecraft.custom:minecraft.damage_taken"),
    BowShoot("minecraft.used:minecraft.bow"),
    CrossbowShoot("minecraft.used:minecraft.crossbow"),
    KillPlayer("minecraft.custom:minecraft.player_kills"),
    Kill("minecraft.custom:minecraft.totalKillCount"),
    Bred("minecraft.custom:minecraft.animals_bred"),
    CleanArmor("minecraft.custom:minecraft.clean_armor"),
    CleanBanner("minecraft.custom:minecraft.clean_banner"),
    OpenBarrel("minecraft.custom:minecraft.open_barrel"),
    BellRing("minecraft.custom:minecraft.bell_ring"),
    EatCakeSlice("minecraft.custom:minecraft.eat_cake_slice"),
    FillCauldron("minecraft.custom:minecraft.fill_cauldron"),
    OpenChest("minecraft.custom:minecraft.open_chest"),
    DamageAbsorbed("minecraft.custom:minecraft.damage_absorbed"),
    DamageBlockByShield("minecraft.custom:minecraft.damage_blocked_by_shield"),
    OpenEnderChest("minecraft.custom:minecraft.open_enderchest"),
    InteractFurnace("minecraft.custom:minecraft.interact_with_furnace")
}

private enum eventParamBlockList(string criterion){
    Mined("minecraft.mined:minecraft."),
    Break("minecraft.broken:minecraft.")
}
private enum eventParamItemList(string criterion){
    Craft("minecraft.crafted:minecraft."),
    Use("minecraft.used:minecraft."),
    PickUp("minecraft.picked_up:minecraft."),
    Drop("minecraft.dropped:minecraft.")
}
private enum eventParamEntityList(string criterion){
    Kill("minecraft.killed:minecraft."),
    Killed("minecraft.killed_by:minecraft.")
}

private enum continuousEventList(string criterion){
    Sneak("minecraft.custom:minecraft.sneak_time"),
    Walk("minecraft.custom:minecraft.walk_one_cm"),
    WalkOnWater("minecraft.custom:minecraft.walk_on_water_one_cm"),
    WalkUnderWater("minecraft.custom:minecraft:walk_under_water_one_cm"),
    Boat("minecraft.custom:minecraft.boat_one_cm"),
    Horse("minecraft.custom:minecraft.horse_one_cm"),
    Minecart("minecraft.custom:minecraft:minecart_one_cm"),
    Pig("minecraft.custom:minecraft:pig_one_cm"),
    Strider("minecraft.custom:minecraft:strider_one_cm"),
    Swim("minecraft.custom:minecraft.swim_one_cm"),
    Sprint("minecraft.custom:minecraft.sprint_one_cm"),
    Fly("minecraft.custom:minecraft.fly_one_cm"),
    Fall("minecraft.custom:minecraft.fall_one_cm"),
    Climb("minecraft.custom:minecraft.climb_one_cm"),
    Elytra("minecraft.custom:minecraft.aviate_one_cm")
}
forgenerate($e,eventList){
    def lazy on$e(int=>void fct){
        lazy val criterion = "$e.criterion"
        [criterion=criterion] scoreboard int count
        def @input.init @input.reset init$e(){
            count = 0
        }
        
        if (count > 0){
            fct(count)
            entity tag 
            tag += @s;
            schedule.add(1){
                with(tag, true){
                    tag -= @s
                    @input.reset()
                }
            }
        }
    }
}
forgenerate($e,eventParamBlockList){
    def lazy on$e(mcobject block, int=>void fct){
        lazy val name = Compiler.getNamespaceName(block)
        lazy val criterion = "$e.criterion" + name
        [criterion=criterion] scoreboard int count
        def @input.init @input.reset init$e(){
            count = 0
        }
        
        if (count > 0){
            fct(count)
            entity tag 
            tag += @s;
            schedule.add(1){
                with(tag, true){
                    tag -= @s
                    @input.reset()
                }
            }
        }
    }
}


forgenerate($e,eventParamItemList){
    def lazy on$e(mcobject block, int=>void fct){
        lazy val name = Compiler.getNamespaceName(block)
        lazy val criterion = "$e.criterion" + name
        [criterion=criterion] scoreboard int count
        def @input.init @input.reset init$e(){
            count = 0
        }
        
        if (count > 0){
            fct(count)
            entity tag 
            tag += @s;
            schedule.add(1){
                with(tag, true){
                    tag -= @s
                    @input.reset()
                }
            }
        }
    }
}

forgenerate($e,eventParamEntityList){
    def lazy on$e(mcobject block, int=>void fct){
        lazy val name = Compiler.getNamespaceName(block)
        lazy val criterion = "$e.criterion" + name
        [criterion=criterion] scoreboard int count
        def @input.init @input.reset init$e(){
            count = 0
        }
        
        if (count > 0){
            fct(count)
            entity tag 
            tag += @s;
            schedule.add(1){
                with(tag, true){
                    tag -= @s;
                    @input.reset()
                }
            }
        }
    }
}

forgenerate($e,continuousEventList){
    def lazy on$e(int=>void fct){
        lazy val criterion = "$e.criterion"
        [criterion=criterion] scoreboard int count
        scoreboard bool active
        def @input.init @input.reset init$e(){
            count = 0
        }
        
        if (count > 0 && !active){
            fct(count)
            active = true
            entity tag 
            tag += @s;
            schedule.add(1){
                with(tag, true){
                    tag -= @s
                    @input.reset()
                }
            }
        }
        else if(count > 0 && active){
        }
        else{
            active = false
        }
    }
    def lazy during$e(int=>void fct){
        lazy val criterion = "$e.criterion"
        [criterion=criterion] scoreboard int count
        def @input.init @input.reset init$e(){
            count = 0
        }
        
        if (count > 0){
            fct(count)
            entity tag
            tag += @s;
            schedule.add(1){
                with(tag, true){
                    tag -= @s
                    @input.reset()
                }
            }
        }
    }
}