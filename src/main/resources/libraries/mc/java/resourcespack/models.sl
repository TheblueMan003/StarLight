package mc.java.resourcespack.models

lazy var overrides = {}
def lazy add(mcobject item, string model, int index){
    lazy val name = Compiler.getNamespaceName(item)
    overrides[name] += [{"predicate": {"custom_model_data": index},"model": model}]
}
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
def lazy make(int item, params models){
    foreach(values in models){
        lazy int model, index = values
        add(item, model, index)
    }
    generate(item)
}

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