if (Compiler.isBedrock){
    def __load__(){
        @__loading__()
    }
}
def [compile.order=999999] ticking __tick_tag__(){
    @tick()
}
def [compile.order=999999,tag.order=999999] loading __load_tag__(){
    @load()
}
def @tick __playertick__(){
    if (@playertick){
        with(@a, true){
            @playertick()
        }
    }
}
int __totalRefCount
def lazy __addBindEntity__(int variable, entity e){
    if (variable == null){
        __totalRefCount++
        variable = __totalRefCount
    }
    int id = variable
    with(e){
        variable = id
    }
}
def lazy __clearBindEntity__(int variable){
    if (variable != null){
        __totalRefCount++
        variable = __totalRefCount
    }
}
def lazy __withBindEntity__(int variable, void=>void action){
    int id = variable
    with(@e, false, variable == id){
        action()
    }
}
def lazy entity __getBindEntity__(int variable){
    int id = variable
    entity ne = null
    with(@e, false, variable == id){
        ne += @s
    }
    return ne
}

def lazy aligned(void=>void fct){
    align("xyz")at(~0.5 ~ ~0.5)fct()
}

def lazy __at__(float x, float y, float z, void=>void fct){
    import mc.pointer as pointer
    import cmd.tp as tp
    int px = x
    int py = y
    int pz = z
    entity p = pointer.newPointer(){
        tp.absolute(px, py, pz)
    }
    at(p){
        fct()
    }
    with(p){
        /kill @s
    }
}

class object{
    private int __ref
    private int __refCount

    def __addRef(){
        __refCount++
    }
    def __destroy__(){
    }
    def __remRef(){
        __refCount--
        if (__refCount <= 0){
            __destroy__()
            /kill
        }
    }
    static lazy object __initInstance(mcobject clazz, mcobject entity = minecraft:marker){
        __totalRefCount++
        if (Compiler.isJava()){
            lazy string namespaceName = Compiler.getNamespace(entity)
            if (namespaceName == "blockbench"){
                /tag @e[tag=!object.__tagged] add object.__tagged
                Compiler.blockbenchSummon(entity)
                with(@e[tag=!object.__tagged,type=marker]){
                    object.__ref = __totalRefCount
                    object.__refCount = 1
                    /tag @s add __class__
                    Compiler.addClassTags(clazz)
                }
            }
            else{
                Compiler.insert($entity, entity){
                    /summon $entity ~ ~ ~ {Tags:["__class__","cls_trg"]}
                }
                with(@e[tag=cls_trg]){
                    object.__ref = __totalRefCount
                    object.__refCount = 1
                    /tag @s remove cls_trg
                    Compiler.addClassTags(clazz)
                }
            }
        }
        if (Compiler.isBedrock()){
            /tag @e[tag=!object.__tagged] add object.__tagged
            def lazy summon_(mcobject $entity){
                /summon $entity
            }
            summon_(entity)
            with(@e[tag=!object.__tagged]){
                object.__ref = __totalRefCount
                object.__refCount = 1
                /tag @s add __class__
                Compiler.addClassTags(clazz)
            }
        }
        return __totalRefCount
    }
}