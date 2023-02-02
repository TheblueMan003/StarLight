package mc.Item

import mc.player as player
import mc.bedrock.textures as textures
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
    player.addAnimation("controller.animation.$name")
    jsonfile animation_controllers.$name{
            "format_version": "1.10.0",
            "animation_controllers": {
                "controller.animation.$name": {
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
                                    "clickrelease": "(!query.is_using_item) || (query.get_equipped_item_name != '$name')"
                                },
                                {
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
    def lazy setNamespace(string name){
        _namespace = name
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
    def lazy setUseDuration(int value){
        _component += {"minecraft:use_duration": value}
    }
    def lazy setIcon(string icon){
        _component += {"minecraft:icon": {"texture": icon}}
        textures.add(icon)
    }
    def lazy addComponent(json component){
        _component += component
    }

    def lazy onClick(void=>void fct){
        _onClick = fct
        setFood(0)
    }
    def lazy whileClick(void=>void fct){
        _whileClick = fct
        setFood(0)
    }
    def lazy onRelease(void=>void fct){
        _onRelease = fct
        setFood(0)
    }

    def [Compile.order=1000] build(){
        lazy val namespacedName = _namespace + ":" + _name
        createItem(_name, namespacedName, _category, _component)
        createAnimationController(_name, _onClick, _whileClick, _onRelease)
    }
}