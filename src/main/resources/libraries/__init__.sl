if (Compiler.isBedrock){
    def __load__(){
        @__loading__()
    }
}
def [compile.order=999999] ticking __tick__(){
    @tick()
}
def @tick __playertick__(){
    if (@playertick){
        with(@a, true){
            @playertick()
        }
    }
}

def lazy aligned(void=>void fct){
    align("xyz")at(~0.5 ~ ~0.5)fct()
}

def lazy __at__(float x, float y, float z, void=>void fct){
    import mc.pointer as pointer
    import cmd.tp as tp

    entity p = pointer.newPointer(){
        tp.absolute(x, y, z)
    }
    at(p){
        fct()
    }
}

int __totalRefCount

class object{
    private int __ref
    private int __refCount

    def __addRef(){
        __refCount++
    }
    def __remRef(){
        __refCount--
        if (__refCount <= 0){
            /kill
        }
    }
    static lazy object __initInstance(mcobject clazz, mcobject entity = marker){
        __totalRefCount++
        if (Compiler.isJava()){
            def static lazy summon(mcobject $entity){
                /summon $entity ~ ~ ~ {Tags:["__class__","cls_trg"]}
            }
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
                summon(entity)
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
            def lazy summon(mcobject $entity){
                /summon $entity
            }
            summon(entity)
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