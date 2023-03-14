package mc.bedrock.resourcespack.textures

lazy json items = {}
lazy json blocks = {}
def [Compiler.order=999999] make(){
    [bedrock_rp=true] jsonfile textures.item_texture{
        "resource_pack_name": "sl",
        "texture_name": "atlas.items",
        "texture_data": items
    }
    [bedrock_rp=true] jsonfile textures.terrain_texture{
        "resource_pack_name": "sl:terrain", 
        "texture_name": "atlas.terrain", 
        "padding": 8, 
        "num_mip_levels" : 4,
        "texture_data": blocks
    }
}

def lazy addItem(string path){
    Compiler.insert($name, path){
        lazy val texture = "textures/"+path
        items += {"$name": {
                "textures": texture
            }
        }
    }
}

def lazy addBlock(string path){
    Compiler.insert($name, path){
        lazy val texture = "textures/"+path
        blocks += {"$name": {
                "textures": texture
            }
        }
    }
}