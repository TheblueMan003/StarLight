package cmd.entity

if (Compiler.isJava()){
    private lazy void _summon(mcobject $name, string $meta){
        /summon $name ~ ~ ~ $meta
    }
    private lazy void _summon(mcobject $name){
        /summon $name
    }
    lazy entity summon(mcobject name, json data, void=>void fct){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            lazy json ndata = {Tags:[tag]}
            ndata += data
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
            with(_ret, true){
                fct()
            }
        }
        else{
            entity tmp
            lazy string tag = Compiler.getVariableTag(tmp)
            lazy json ndata = {Tags:[tag]}
            ndata += data
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
            with(tmp, true){
                fct()
            }
        }
    }
    lazy entity summon(mcobject name, json data = {}){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            lazy json ndata = {Tags:[tag]}
            ndata += data
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
        }
        else{
            lazy string nbt = Compiler.toNBT(data)
            _summon(name, nbt)
        }
    }
    lazy entity summon(mcobject name, void=>void fct){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            lazy json ndata = {Tags:[tag]}
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
            with(_ret, true){
                fct()
            }
        }
        else{
            entity tmp
            lazy string tag = Compiler.getVariableTag(tmp)
            lazy json ndata = {Tags:[tag]}
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
            with(tmp, true){
                fct()
            }
        }
    }
}
if (Compiler.isBedrock()){
    private lazy void _summon(mcobject $name, string $tag, string $skin, void=>void fct){
        /tag @e[tag=!object.__tagged] add object.__tagged
        /summon $name ~ ~ ~ $skin
        with(@e[tag=!object.__tagged]){
            /tag @s add $tag
            /tag @s add object.__tagged
            fct()
        }
    }
    private lazy void _summon(mcobject $name, string $tag, void=>void fct){
        /tag @e[tag=!object.__tagged] add object.__tagged
        /summon $name
        with(@e[tag=!object.__tagged]){
            /tag @s add $tag
            /tag @s add object.__tagged
            fct()
        }
    }
    private lazy void _summon(mcobject $name){
        /summon $name
    }
    private lazy void _summon(mcobject $name, string $skin){
        /summon $name ~ ~ ~ $skin
    }
    lazy entity summon(mcobject name, string skin, void=>void fct){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            _summon(name, tag, skin, fct)
        }
        else{
            entity tmp
            lazy string tag = Compiler.getVariableTag(tmp)
            _summon(name, tag, skin, fct)
        }
    }
    lazy entity summon(mcobject name, void=>void fct){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            _summon(name, tag, fct)
        }
        else{
            entity tmp
            lazy string tag = Compiler.getVariableTag(tmp)
            _summon(name, tag, fct)
        }
    }
    lazy entity summon(mcobject name, string skin){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            _summon(name, tag, skin, null)
        }
        else{
            _summon(name, skin)
        }
    }
    lazy entity summon(mcobject name){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            _summon(name, tag, null)
        }
        else{
            _summon(name)
        }
    }
}

def lazy kill(entity $selector = @s){
	/kill $selector
}

def lazy despawn(entity $selector = @s){
	if(Compiler.isBedrock()){
		/event entity $selector to_death
	}
	if(Compiler.isJava()){
		with($selector, true){
		/tp @s ~ -200 ~
		}
	}
}