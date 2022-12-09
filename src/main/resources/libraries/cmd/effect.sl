package cmd.effect

forgenerate($name, (absorption, unluck, bad_omen, blindness, conduit_power, dolphins_grace, 
                fire_resistance, glowing, haste, health_boost, hero_of_the_village,
                hunger, instant_damage, instant_health, invisibility, jump_boost, levitation,
                luck, mining_fatigue, nausea, night_vision, poison, regeneration, resistance,
                saturation, slow_falling, slowness, speed, strength, water_breathing, weakness,
                wither, darkness)){
    """
    Give effect `$name` to `ent` for `duration` and `power`.
    Show particles if `particle`
    """
    def lazy $name(entity $ent, int $duration = 999999, int $power = 0, bool $particle = true){
        if (Compiler.isJava()){
            /effect give $ent $name $power $duration $particle
        }
        if (Compiler.isBedrock()){
            /effect $ent $name $power $duration $particle
        }
    }

    """
    Give effect `$name` to self for `duration` and `power`.
    Show particles if `particle`
    """
    def lazy $name(int $duration = 999999, int $power = 0, bool $particle = true){
        if (Compiler.isJava()){
            /effect give @s $name $power $duration $particle
        }
        if (Compiler.isBedrock()){
            /effect @s $name $power $duration $particle
        }
    }

    """
    Clear effect `$name` for `ent`
    """
    def lazy clear$name(entity $ent = @s){
        if (Compiler.isJava()){
            /effect clear $ent $name
        }
        if (Compiler.isBedrock()){
            /effect $ent $name 0 0
        }
    }
}