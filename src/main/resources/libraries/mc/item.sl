package mc.item

import mc.player

template Item{
    private lazy string _name = "custom:item"
    private lazy string _category = "Items"
    private lazy json _component = {}
    
    def lazy setName(string name){
        _name = name
    }
    def lazy setCategory(string name){
        _category = name
    }
    def lazy setMaxItemStack(int size){
        _component += {"minecraft:max_stack_size": size}
    }
    def lazy setBlock(string block){
        _component += {"minecraft:block": block}
    } 
    def lazy setGlow(bool value){
        _component += {"minecraft:foil": value}
    }
    def lazy setFood(int value, string saturation_modifier = "low"){
        _component += {"minecraft:food": {
            "nutrition": value,
            "saturation_modifier": saturation_modifier
        }}
    }
    def lazy setFood(int value, string saturation_modifier = "low"){
        _component += {"minecraft:food": {
            "nutrition": value,
            "saturation_modifier": saturation_modifier
        }}
    }
    def lazy setMaxDamage(int value){
        _component += {"minecraft:max_damage": value}
    }

    def [Compile.order=1000] build(){
        jsonfile items.this{
            "format_version": "1.16.100",
            "minecraft:item": {
                "description": {
                    "identifier": _name,
                    "category": _category
                },
                "components":_component
            }
        }
        json animation_controllers.this{
            "format_version": "1.10.0",
            "animation_controllers": {
                "controller.animation.$this": {
                    "initial_state": "default",
                    "states": {
                        "default": {
                            "transitions": [
                                {
                                    "clickitem": "(query.is_using_item) && (query.get_equipped_item_name == 'cloudbuster')"
                                }
                            ]
                        },
                        "clickitem": {
                            "on_entry": [
                                "/function item/$this/shoot"
                            ],
                            "transitions": [
                                {
                                    "default": "(!query.is_using_item) || (query.get_equipped_item_name != 'cloudbuster')"
                                }
                            ]
                        }
                    }
                }
            }
        }
    }
}