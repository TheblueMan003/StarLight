def ticking __load__(){
    if (Compiler.isBedrock){
        int v
        lazy int id = Compiler.random()
        if (v != id){
            v = id
            @__loading__()
        }
    }
}

int __totalRefCount

class object{
    int __ref
    int __refCount

    def __addRef(){
        __refCount++
    }
    def __remRef(){
        __refCount--
        if (__refCount <= 0){
            /kill
        }
    }
}

lazy object __initInstance(mcobject clazz, mcobject $entity = marker){
    __totalRefCount++
    if (Compiler.isJava()){
        /summon $entity ~ ~ ~ {Tags:["__class__","cls_trg"]}
        with(@e[tag=cls_trg]){
            object.__ref = __totalRefCount
            object.__refCount = 1
            /tag @s remove cls_trg
            Compiler.addClassTags(clazz)
        }
    }
    if (Compiler.isBedrock()){
        /tag @e[tag=!object.__tagged] add object.__tagged
        /summon $entity
        with(@e[tag=!object.__tagged]){
            object.__ref = __totalRefCount
            object.__refCount = 1
            /tag @s add __class__
            Compiler.addClassTags(clazz)
        }
    }
    return __totalRefCount
}