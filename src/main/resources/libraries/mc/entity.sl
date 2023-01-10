package mc.entity

import standard.int as int

"""
Swap the position of the entity $a and $b
"""
def lazy swap(entity $a, entity $b){
	if (Compiler.isBedrock()){
		with($a, true){
			/summon sl:marker ~ ~ ~
			/tp @s $b
		}
		as($b){
			/tp @s @e[type=sl:marker,c=1]
		}
		/kill @e[type=sl:marker,c=1]
	}
	if (Compiler.isJava()){
		with($a, true){
			/summon marker ~ ~ ~ {Tags:["trg"]}
			/tp @s $b
		}
		as($b){
			/tp @s @e[type=marker,tag=trg,limit=1]
		}
		/kill @e[type=marker,tag=trg,limit=1]
	}
}

"""
Despawn the entity e without killing it
"""
def lazy despawn(entity e = @s){
	if(Compiler.isBedrock()){
        def lazy inner(entity $a){
            /event entity $a to_death
        }
		inner(e)
	}
	if(Compiler.isJava()){
		with(e, true){
		/tp @s ~ -200 ~
		}
	}
}

predicate onFire(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_on_fire": true
        }
    }
}

predicate isSneaking(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_sneaking": true
        }
    }
}

predicate isSprinting(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_sprinting": true
        }
    }
}

predicate isSwimming(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_swimming": true
        }
    }
}

predicate isBaby(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_baby": true
        }
    }
}

if (Compiler.isJava()){
    """
    Return true if the entity is on the ground
    """
	lazy bool onGround(){
		return @s[nbt={OnGround:true}]
	}
}
if (Compiler.isBedrock()){
    """
    Return true if the entity is on the ground
    """
	bool onGround(){
		if (!block(~ ~-0.1 ~, minecraft:air)){
			return true
		}
		else{
			return false
		}
	}
}

predicate overworld(){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": "minecraft:overworld"
    }
}

predicate the_end(){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": "minecraft:the_end"
    }
}

predicate nether(){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": "minecraft:the_nether"
    }
}

predicate dimension(string dimension){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": dimension
    }
}

"""
Anger the entity @s against e
"""
def lazy angerAngaist(entity $e){
    at(@s){
        /summon snowball ~ ~3 ~ {Tags:["trg"],HasBeenShot:1,LeftOwner:1}
        with(@e[tag=trg]){
            /data modify entity @s Owner set from entity $e UUID
            /tag @s remove trg
        }
    }
}

"""
Anger the entity e1 against e2
"""
def lazy angerAngaist(entity e1, entity e2){
    with(e1){
        angerAngaist(e2)
    }
}

"""
Count the number of entity in e
"""
lazy int count(entity e){
    int c = 0
    with(e){
        c++
    }
    return c
}

"""
Count the number of entity in e that match the predicate
"""
lazy int count(entity e, bool p){
    int c = 0
    with(e,true,p){
        c++
    }
    return c
}

"""
Count the number of entity in e that match the predicate
"""
lazy int count(entity e, void=>bool p){
    int c = 0
    with(e,true,p()){
        c++
    }
    return c
}