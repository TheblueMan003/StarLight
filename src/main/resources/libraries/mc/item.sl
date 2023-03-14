package mc.Item

import mc.player as player
import mc.bedrock.resourcespack.textures as textures
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
    
    """
    Set the name of the item
    """
    def lazy setName(string name){
        _name = name
    }

    """
    Set the namespace of the item
    """
    def lazy setNamespace(string name){
        _namespace = name
    }

    """
    Set the category of the item
    """
    def lazy setCategory(string name){
        _category = name
    }

    """
    Set the max stack of the item
    """
    def lazy setMaxItemStack(int size){
        _component += {"minecraft:max_stack_size": size}
    }

    """
    Set the item as a block
    """
    def lazy setBlock(string block){
        _component += {"minecraft:block": block}
    } 
    
    """
    Set if the item glows
    """
    def lazy setGlow(bool value){
        _component += {"minecraft:foil": value}
    }
    
    """
    Set the food value of the item
    """
    def lazy setFood(int value, string saturation_modifier = "low"){
        _component += {"minecraft:food": {
            "nutrition": value,
            "saturation_modifier": saturation_modifier
        }}
    }

    """
    Set the use duration of the item
    """
    def lazy setUseDuration(int value){
        _component += {"minecraft:use_duration": value}
    }
    
    """
    Set the icon of the item
    """
    def lazy setIcon(string icon){
        _component += {"minecraft:icon": {"texture": icon}}
        textures.addItem(icon)
    }

    """
    Add a component to the item
    """
    def lazy addComponent(json component){
        _component += component
    }

    """
    Set the on click function
    """
    def lazy onClick(void=>void fct){
        _onClick = fct
        setFood(0)
    }
    """
    Set the on while click function
    """
    def lazy whileClick(void=>void fct){
        _whileClick = fct
        setFood(0)
    }
    """
    Set the on release function
    """
    def lazy onRelease(void=>void fct){
        _onRelease = fct
        setFood(0)
    }

    [Compile.order=1000] private void build(){
        lazy val namespacedName = _namespace + ":" + _name
        createItem(_name, namespacedName, _category, _component)
        createAnimationController(_name, _onClick, _whileClick, _onRelease)
    }
}