package cmd.entity

if (Compiler.isJava()){
    private lazy void _summon(mcobject $name, string $meta){
        /summon $name ~ ~ ~ $meta
    }
    lazy entity summon(mcobject name, json data = {}){
        lazy string tag = Compiler.getVariableTag(_ret)
        lazy json ndata = {Tags:[tag]}
        ndata += data
        lazy string nbt = Compiler.toNBT(ndata)
        _summon(name, nbt)
    }
}
if (Compiler.isBedrock()){
    private lazy void _summon(mcobject $name, string $tag){
        /tag @e[tag=!object.__tagged] add object.__tagged
        /summon $name
        with(@e[tag=!object.__tagged]){
            /tag add @s $tag
        }
    }
    lazy entity summon(mcobject name){
        lazy string tag = Compiler.getVariableTag(_ret)
        _summon(name, tag)
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