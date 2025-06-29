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
def @tick __itemtick__(){
    if (@itemtick){
        with(@e[type=item], true){
            @itemtick()
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
def lazy __repeat__(int variable, void=>void action){
    for (int i = 0; i < variable; i++){
        action()
    }
}
def lazy __sleep__(int time, void=>void action){
    import cmd.schedule as schedule
    schedule.add(time, action)
}
def lazy __async_repeat__(int time, void=>void action, void=>void next){
    var counter = 0
    def loop(){
        counter++
        if (counter < time){
            action()
            __sleep__(1, loop)
        }
        else{
            next()
        }
    }
    loop()
}
def lazy __until__(bool cond, int step, void=>void action, void=>void next){
    import cmd.schedule as schedule
    def loop(){
        action()
        if (cond){
            next()
        }
        else{
            schedule.add(step, loop)
        }
    }
    loop()
}
def lazy __while__(bool cond, int step, void=>void action, void=>void next){
    import cmd.schedule as schedule
    def loop(){
        action()
        if (cond){
            schedule.add(step, loop)
        }
        else{
            next()
        }
    }
    loop()
}
def lazy __wait_for__(bool cond, void=>void action){
    import cmd.schedule as schedule
    def loop(){
        if (cond){
            action()
        }
        else{
            schedule.add(1, loop)
        }
    }
    loop()
}

def lazy aligned(void=>void fct){
    align("xyz")at(~0.5 ~ ~0.5)fct()
}

def lazy __at__(float x, float y, float z, void=>void fct){
    if (Compiler.isBedrock()){
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
    if (Compiler.isJava()){
        def macro inner(float x, float y, float z){
            at("$(x) $(y) $(z)"){
                fct()
            }
        }
        inner(x, y, z)
    }
}

def lazy __at__(string mx, float x, string my, float y, string mz, float z, void=>void fct){
    def macro inner(string mx, float x, string my, float y, string mz, float z){
        at("$(mx)$(x) $(my)$(y) $(mz)$(z)"){
            fct()
        }
    }
    inner(mx, x, my, y, mz, z)
}

template Template{
}

class object{
    private int __ref
    private int __refCount

    def __init__(){
    }
    def __addRef(){
        if (__refCount >= 0)__refCount++
    }
    def virtual __destroy__(){
    }
    def __delete__(){
        __destroy__()
        /kill
    }
    def __remRef(){
        if (__refCount > 0) __refCount--
        if (__refCount == 0){
            __delete__()
        }
    }

    static lazy object __initInstance(mcobject clazz, mcobject entity = minecraft:marker){
        __totalRefCount++
        if (Compiler.isJava()){
            lazy string namespaceName = Compiler.getNamespace(entity)
            if (namespaceName == "blockbench"){
                /tag @e[tag=!object.__tagged] add object.__tagged
                lazy var e = Compiler.blockbenchSummon(entity)
                with(@e[tag=!object.__tagged] in e){
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
            if (entity == minecraft:marker){
                entity = sl:marker
            }
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

    static lazy object __initUnbounded(mcobject clazz, mcobject entity = minecraft:marker){
        __totalRefCount++
        if (Compiler.isJava()){
            lazy string namespaceName = Compiler.getNamespace(entity)
            if (namespaceName == "blockbench"){
                /tag @e[tag=!object.__tagged] add object.__tagged
                lazy var e = Compiler.blockbenchSummon(entity)
                with(@e[tag=!object.__tagged] in e){
                    object.__ref = __totalRefCount
                    object.__refCount = -1
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
                    object.__refCount = -1
                    /tag @s remove cls_trg
                    Compiler.addClassTags(clazz)
                }
            }
        }
        if (Compiler.isBedrock()){
            if (entity == minecraft:marker){
                entity = sl:marker
            }
            /tag @e[tag=!object.__tagged] add object.__tagged
            def lazy summon_(mcobject $entity){
                /summon $entity
            }
            summon_(entity)
            with(@e[tag=!object.__tagged]){
                object.__ref = __totalRefCount
                object.__refCount = -1
                /tag @s add __class__
                Compiler.addClassTags(clazz)
            }
        }
        return __totalRefCount
    }
}

def lazy __assert__(bool cond, void=>void f){
    if (cond){
        f()
    }
    else{
        import standard
        standard.print("Assert Failled")
    }
}