package cmd.entity

if (Compiler.isJava()){
    private lazy void _summon(mcobject $name, string $meta){
        /summon $name ~ ~ ~ $meta
    }
    private lazy void _summon(mcobject $name){
        /summon $name
    }
    private lazy void removetag(string $tag){
        /tag @e[tag=$tag] remove $tag
    }
    [noReturnCheck=true] lazy entity summon(mcobject name, json data, void=>void fct){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            lazy json ndata = {Tags:[tag]}
            removetag(tag)
            ndata += data
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
            _ret += 1
            with(_ret, true){
                fct()
            }
        }
        else{
            entity tmp
            lazy string tag = Compiler.getVariableTag(tmp)
            lazy json ndata = {Tags:[tag]}
            removetag(tag)
            ndata += data
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
            with(tmp, true){
                fct()
            }
        }
    }
    [noReturnCheck=true] lazy entity summon(mcobject name, json data = {}){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            lazy json ndata = {Tags:[tag]}
            removetag(tag)
            ndata += data
            lazy string nbt = Compiler.toNBT(ndata)
            _ret += 1
            _summon(name, nbt)
        }
        else{
            lazy string nbt = Compiler.toNBT(data)
            _summon(name, nbt)
        }
    }
    [noReturnCheck=true] lazy entity summon(mcobject name, void=>void fct){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            lazy json ndata = {Tags:[tag]}
            removetag(tag)
            lazy string nbt = Compiler.toNBT(ndata)
            _summon(name, nbt)
            _ret += 1
            with(_ret, true){
                fct()
            }
        }
        else{
            entity tmp
            lazy string tag = Compiler.getVariableTag(tmp)
            lazy json ndata = {Tags:[tag]}
            removetag(tag)
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
    [noReturnCheck=true] lazy entity summon(mcobject name, string skin, void=>void fct){
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
    [noReturnCheck=true] lazy entity summon(mcobject name, void=>void fct){
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
    [noReturnCheck=true] lazy entity summon(mcobject name, string skin){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            _summon(name, tag, skin, null)
        }
        else{
            _summon(name, skin)
        }
    }
    [noReturnCheck=true] lazy entity summon(mcobject name){
        if (Compiler.variableExist(_ret)){
            lazy string tag = Compiler.getVariableTag(_ret)
            _summon(name, tag, null)
        }
        else{
            _summon(name)
        }
    }
}

def lazy kill(entity selector = @s){
    with(selector){
	    /kill
    }
}

def lazy despawn(entity e = @s){
	if(Compiler.isBedrock()){
        def lazy inner(entity $a){
            /event entity $a to_death
        }
		inner(e)
	}
	if(Compiler.isJava()){
		with(e, true){
		/tp @s ~ -200 ~
		}
	}
}

"""
Swap the position of the entity $a and $b
"""
def lazy swap(entity $a, entity $b){
	if (Compiler.isBedrock()){
		with($a, true){
			/summon sl:marker ~ ~ ~
			/tp @s $b
		}
		as($b){
			/tp @s @e[type=sl:marker,c=1]
		}
		/kill @e[type=sl:marker,c=1]
	}
	if (Compiler.isJava()){
		with($a, true){
			/summon marker ~ ~ ~ {Tags:["trg"]}
			/tp @s $b
		}
		as($b){
			/tp @s @e[type=marker,tag=trg,limit=1]
		}
		/kill @e[type=marker,tag=trg,limit=1]
	}
}

"""
Despawn the entity e without killing it
"""
def lazy despawn(entity e = @s){
	if(Compiler.isBedrock()){
        def lazy inner(entity $a){
            /event entity $a to_death
        }
		inner(e)
	}
	if(Compiler.isJava()){
		with(e, true){
		/tp @s ~ -200 ~
		}
	}
}

predicate onFire(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_on_fire": true
        }
    }
}

predicate isSneaking(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_sneaking": true
        }
    }
}

predicate isSprinting(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_sprinting": true
        }
    }
}

predicate isSwimming(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_swimming": true
        }
    }
}

predicate isBaby(){
    "condition": "minecraft:entity_properties",
    "entity": "this",
    "predicate": {
        "flags": {
            "is_baby": true
        }
    }
}

if (Compiler.isJava()){
    """
    Return true if the entity is on the ground
    """
	lazy bool onGround(){
		return @s[nbt={OnGround:true}]
	}
}
if (Compiler.isBedrock()){
    """
    Return true if the entity is on the ground
    """
	bool onGround(){
		if (!block(~ ~-0.1 ~, minecraft:air)){
			return true
		}
		else{
			return false
		}
	}
}

predicate overworld(){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": "minecraft:overworld"
    }
}

predicate the_end(){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": "minecraft:the_end"
    }
}

predicate nether(){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": "minecraft:the_nether"
    }
}

predicate dimension(string dimension){
    "condition": "minecraft:location_check",
    "predicate": {
        "dimension": dimension
    }
}

"""
Anger the entity @s against e
"""
def lazy angerAngaist(entity $e){
    at(@s){
        /summon snowball ~ ~3 ~ {Tags:["trg"],HasBeenShot:1,LeftOwner:1}
        with(@e[tag=trg]){
            /data modify entity @s Owner set from entity $e UUID
            /tag @s remove trg
        }
    }
}

"""
Anger the entity e1 against e2
"""
def lazy angerAngaist(entity e1, entity e2){
    with(e1){
        angerAngaist(e2)
    }
}

"""
Count the number of entity in e
"""
[noReturnCheck=true] lazy int count(entity e){
    _ret = 0
    with(e)_ret++
}

"""
Count the number of entity in e that match the predicate
"""
[noReturnCheck=true] lazy int count(entity e, bool p){
    _ret = 0
    with(e,true,p)_ret++
}

"""
Count the number of entity in e that match the predicate
"""
[noReturnCheck=true] lazy int count(entity e, void=>bool p){
    _ret = 0
    with(e,true,p())_ret++
}

"""
Mount the current entity on the entity e
"""
def lazy ride(entity e){
    lazy var e2 = Compiler.makeUnique(e)
    if(Compiler.isJava()){
        Compiler.insert($e, e2){
            /ride @s mount $e
        }
    }
    if(Compiler.isBedrock()){
        Compiler.insert($e, e2){
            /ride @s start_riding $e
        }
    }
}
"""
Mount the entity e1 on the entity e2
"""
def lazy ride(entity e1, entity e2){
    with(e1){
        ride(e2)
    }
}
    
"""
Dismount the current entity
"""
def lazy dismount(){
    if(Compiler.isJava()){
        /ride @s dismount
    }
    if(Compiler.isBedrock()){
        /ride @s stop_riding
    }
}

"""
Dismount the entity e
"""
def lazy dismount(entity e){
    with(e){
        dismount()
    }
}

"""
Spectate the current entity on the entity e
"""
def lazy spectate(entity e){
    lazy var e2 = Compiler.makeUnique(e)
    if(Compiler.isJava()){
        Compiler.insert($e, e2){
            /spectate $e @s
        }
    }
    if(Compiler.isBedrock()){
        Compiler.insert($e, e2){
            /spectate $e @s
        }
    }
}
"""
Spectate the entity e1 on the entity e2
"""
def lazy spectate(entity e1, entity e2){
    with(e1){
        spectate(e2)
    }
}

"""
Return true if at least one entity in e has the scoreboard in score
"""
lazy bool exists(entity e, bool test){
    bool exist = false
    with(e,true,test){
        exist = true
    }
    return exist
}

"""
Return true if all entity in e has the scoreboard in score
"""
lazy bool forall(entity e, bool test){
    bool exist = true
    with(e,true,!test){
        exist = false
    }
    return exist
}