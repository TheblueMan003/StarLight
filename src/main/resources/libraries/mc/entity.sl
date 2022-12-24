package mc.entity

import standard.int as int

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

def lazy despawn(entity $a = @s){
	if(Compiler.isBedrock()){
		/event entity $a to_death
	}
	if(Compiler.isJava()){
		with($a, true){
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
	lazy bool onGround(){
		return @s[nbt={OnGround:true}]
	}
}
if (Compiler.isBedrock()){
	bool onGround(){
		/*if (!block(~ ~-0.1 ~ air)){
			return true
		}
		else{
			return false
		}*/
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

def lazy angerAngaist(entity $e){
    at(@s){
        /summon snowball ~ ~3 ~ {Tags:["trg"],HasBeenShot:1,LeftOwner:1}
        with(@e[tag=trg]){
            /data modify entity @s Owner set from entity $e UUID
            /tag @s remove trg
        }
    }
}

def lazy angerAngaist(entity e1, entity e2){
    with(e1){
        angerAngaist(e2)
    }
}