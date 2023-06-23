package lg

import mc.inventory as inventory
import random
import cmd.sound as sound
import cmd.particles as particles
import mc.java.event as events

scoreboard int percent
scoreboard int WarningCount
scoreboard int Slot
int tick

lazy json table = {"chainmail_chestplate":40,
    "chainmail_helmet":25,
    "chainmail_boots":20,
    "chainmail_leggings":35,

    "iron_chestplate":80,
    "iron_helmet":50,
    "iron_boots":40,
    "iron_leggings":70,
    "iron_block":90,
    "iron_sword":20,
    "iron_pickaxe":30,
    "iron_axe":30,
    "iron_hoe":20,
    "iron_shovel":10,
    "iron_ingot":10,
    "iron_nugget":1,
    "iron_bars":10,
    "iron_door":20,
    "iron_trapdoor":30,

    "golden_chestplate":160,
    "golden_helmet":100,
    "golden_boots":80,
    "golden_leggings":140,
    "gold_block":180,
    "golden_sword":40,
    "golden_pickaxe":60,
    "golden_axe":60,
    "golden_hoe":40,
    "golden_shovel":20,
    "gold_ingot":20,
    "gold_nugget":2,

    "netherite_chestplate":5,
    "netherite_helmet":5,
    "netherite_boots":5,
    "netherite_leggings":5,
    "netherite_block":5,
    "netherite_sword":5,
    "netherite_pickaxe":5,
    "netherite_axe":5,
    "netherite_hoe":5,
    "netherite_shovel":5,
    "netherite_ingot":5,

    "copper_ingot":15,
    "copper_block":135,
    "cut_copper":135,
    "cut_copper_stairs":120,

    "exposed_copper":120,
    "exposed_cut_copper":120,
    "exposed_cut_copper_stairs":105,

    "weathered_copper":90,
    "weathered_cut_copper":90,
    "weathered_cut_copper_stairs":90,

    "oxidized_copper":75,
    "oxidized_cut_copper":75,
    "oxidized_cut_copper_stairs":75,

    "lightning_rod":200,

    "leather_helmet":-200,
    "turtle_helmet":-200,

    "clock":10,
    "compass":10,
    "shears":10,
    "flint_and_steel":10,

    "bucket":30,
    "rail":10,
    "detector_rail":10,
    "powered_rail":10,
    "activator_rail":10,
    "minecart":10
}

def update(){
    percent = 0
    int v
    foreach(item in table){
        Compiler.insert($item, item){
            if(inventory.isHoldingItem(minecraft:$item)){
                v = table[item]
                percent += v
            }
            if(inventory.isHoldingItemHead(minecraft:$item)){
                v = table[item]
                percent += v
            }
            if(inventory.isHoldingItemChest(minecraft:$item)){
                v = table[item]
                percent += v
            }
            if(inventory.isHoldingItemLegs(minecraft:$item)){
                v = table[item]
                percent += v
            }
            if(inventory.isHoldingItemFeet(minecraft:$item)){
                v = table[item]
                percent += v
            }
            if(inventory.isHoldingItemOffhand(minecraft:$item)){
                v = table[item]
                percent += v
            }
        }
    }
}
blocktag air{
    air,
    cave_air,
    light,
    #minecraft:flowers,
    grass,
    tall_grass
}
bool valid(int y){
    if (y >= 350){
        return true
    }
    else{
        if (!block(~ ~ ~, #air)){
            return false
        }
        else{
            at(~ ~1 ~){
                return valid(y+1)
            }
        }
    }
}
events.onInventoryChanged(){
    update()
}
def ticking main(){
    if (world.isthundering()){
        tick++
        tick %= 60
        if (tick == 0){
            with(@a,true){
                int total_percent
                WarningCount := 0
                int y = @s.y
                bool v = valid(y)
                if (v){
                    if (percent == null){
                        update()
                    }
                    if (@s[type=player]){
                        int slot = @s.slot
                        if (Slot != slot){
                            Slot = slot
                            update()
                        }
                    }
                    total_percent = 0
                    total_percent += percent
                    if (percent > 0){
                        total_percent += y
                    }
                    else{
                        WarningCount = 0
                    }
                    if (WarningCount > 2 && random.range(0,1000) < total_percent){
                        /summon minecraft:lightning_bolt
                    }
                    else if (total_percent > 0){
                        WarningCount++
                        sound.play(minecraft:block.amethyst_block.hit,1,2)
                        switch(total_percent){
                            000..099 -> particles.sphere(minecraft:electric_spark,1.5,0.10,10)
                            100..199 -> particles.sphere(minecraft:electric_spark,1.5,0.15,20)
                            200..299 -> particles.sphere(minecraft:electric_spark,1.5,0.20,30)
                            300..399 -> particles.sphere(minecraft:electric_spark,1.5,0.25,40)
                            400..499 -> particles.sphere(minecraft:electric_spark,1.5,0.30,50)
                            500..599 -> particles.sphere(minecraft:electric_spark,1.5,0.35,60)
                            600..699 -> particles.sphere(minecraft:electric_spark,1.5,0.40,70)
                            700..799 -> particles.sphere(minecraft:electric_spark,1.5,0.45,80)
                            800..899 -> particles.sphere(minecraft:electric_spark,1.5,0.50,90)
                            900..999 -> particles.sphere(minecraft:electric_spark,1.5,0.55,100)
                        }
                    }
                }
            }
        }
    }
}