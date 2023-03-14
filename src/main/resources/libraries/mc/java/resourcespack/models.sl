package mc.java.resourcespack.models

lazy var overrides = {}
"""
Add custom `model` to `item` with with CustomModelData = `index`
"""
def lazy add(mcobject item, string model, int index){
    lazy val name = Compiler.getNamespaceName(item)
    overrides[name] += [{"predicate": {"custom_model_data": index},"model": model}]
}
"""
Generate `item` model
"""
def lazy generate(mcobject item){
    lazy val name = Compiler.getNamespaceName(item)
    lazy val data = overrides[name]
    Compiler.insert($item, name){
        [java_rp=true] jsonfile models.item.$item{
            "parent": "item/generated",
            "textures": {
                "layer0": "item/$item"
            },
            "overrides": data
        }
    }
}

"""
Generate `item` model with custom models
Take a list of pairs (model, index)
"""
def lazy make(mcobject item, params models){
    foreach(values in models){
        lazy int model, index = values
        add(item, model, index)
    }
    generate(item)
}

"""
Generate a flat item model with `sprite` as texture
"""
def lazy string flat(string name, string sprite){
    Compiler.insert($name, name){
        [java_rp=true] jsonfile models.item.$name{
            "parent": "item/generated",
            "textures": {
                "layer0": sprite
            }
        }
    }
    return "item/" + name
}