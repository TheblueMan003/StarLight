package mc.bedrock.textures

lazy json rp = {}
def [Compiler.order=999999] make(){
    [bedrock_rp=true] jsonfile textures.item_texture{
        "resource_pack_name": "sl",
        "texture_name": "atlas.items",
        "texture_data": rp
    }
}

def lazy add(string path){
    Compiler.insert($name, path){
        lazy val texture = "textures/"+path
        rp += {"$name": {
                "textures": texture
            }
        }
    }
}