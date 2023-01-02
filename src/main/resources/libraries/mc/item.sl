package mc.item

import mc.player

import standard

def private lazy createItem(int $name, json _name, json _category, json _component){
    jsonfile items.$name{
        "format_version": "1.16.100",
        "minecraft:item": {
            "description": {
                "identifier": _name,
                "category": _category
            },
            "components":_component
        }
    }
}
def private lazy createAnimationController(int $name, void=>void onClick, void=>void whileClick, void=>void onRelease){
    jsonfile animation_controllers.$name{
            "format_version": "1.10.0",
            "animation_controllers": {
                "controller.animation.$this": {
                    "initial_state": "default",
                    "states": {
                        "default": {
                            "transitions": [
                                {
                                    "clickstart": "(query.is_using_item) && (query.get_equipped_item_name == '$name')"
                                }
                            ]
                        },
                        "clickstart": {
                            "on_entry": onClick(),
                            "transitions": [
                                {
                                    "clickhold": "true"
                                }
                            ]
                        },
                        "clickhold": {
                            "on_entry": whileClick(),
                            "transitions": [
                                {
                                    "clickrelease": "(!query.is_using_item) || (query.get_equipped_item_name != '$name')",
                                    "clickhold": "true"
                                }
                            ]
                        },
                        "clickrelease": {
                            "on_entry": onRelease(),
                            "transitions": [
                                {
                                    "default": "true"
                                }
                            ]
                        }
                    }
                }
            }
        }
}

template Item{
    private lazy string _namespace = "sl"
    private lazy string _name = Compiler.getContextName()
    private lazy string _category = "Items"
    private lazy json _component = {}
    private lazy void=>void _onClick = null
    private lazy void=>void _whileClick = null
    private lazy void=>void _onRelease = null
    
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

    def lazy onClick(void=>void fct){
        _onClick = fct
    }
    def lazy whileClick(void=>void fct){
        _whileClick = fct
    }
    def lazy onRelease(void=>void fct){
        _onRelease = fct
    }

    def [Compile.order=1000] build(){
        createItem(_name, _name, _category, _component)
        createAnimationController(_name, _onClick, _whileClick, _onRelease)
    }
}