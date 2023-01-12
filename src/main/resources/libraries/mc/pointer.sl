package mc.pointer

import cmd.entity as entity

if (Compiler.isBedrock()){
	jsonfile entities.marker{
		"format_version": "1.8.0",
		"minecraft:entity": {
			"description": {
				"identifier": "sl:marker",
				"is_spawnable": true,
				"is_experimental": false,
				"is_summonable": true
			},
			"components": {
				"minecraft:collision_box": {
					"width": 0.0,
					"height": 0.0
				},
				"minecraft:pushable": {
					"is_pushable": false,
					"is_pushable_by_piston": false
				},
				"minecraft:damage_sensor": [
					{
						"cause": "all",
						"deals_damage": false
					}
				],
				"minecraft:has_gravity": {
					"value": false
				},
				"minecraft:push_through": {
					"value": 1
				},
				"minecraft:knockback_resistance": {
					"value": 1
				}
			},
			"component_groups": {
				"death": {
					"minecraft:despawn": {
					}
				}
			},
			"events": {
				"to_death": {
					"add": {
						"component_groups": [
							"death"
						]
					}
				}
			}
		}
	}
}


"""
Creates a new pointer
"""
lazy entity newPointer(){
    if (Compiler.isJava()){
        return entity.summon(minecraft:marker)
    }
    if (Compiler.isBedrock()){
        return entity.summon(sl:marker)
    }
}

"""
Creates a new pointer
"""
lazy entity newPointer(void=>void func){
    entity a = newPointer()
	with(a,true){
		func()
	}
	return a
}


"""
Runs a function in a with the pointer
"""
lazy void run(void=>void func){
	entity a = newPointer()
	with(a,true){
		func()
		/kill
	}
}