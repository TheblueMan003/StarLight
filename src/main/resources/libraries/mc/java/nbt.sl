package mc.java.nbt

if (Compiler.isJava()){
    """
    Get the nbt and store it in ret
    """
    def lazy getNBT(mcobject ret, mcobject field){
        if (ret is float){
            getNBT(ret, field, 1000)
        }
        else{
            getNBT(ret, field, 1)
        }
    }
    """
    Get the nbt and store it in ret
    """
    def lazy getNBT(mcobject ret, mcobject field, float scale){
        lazy mcobject o = Compiler.getObjective(ret)
        lazy mcobject s = Compiler.getSelector(ret)
        getNBTInner(s, o, field, scale)
    }
    """
    Get the nbt and store it in s.o
    """
    private lazy void getNBTInner(mcobject $s, mcobject $o, mcobject $field, float $scale){
        /execute store result score $s $o run data get entity @s $field $scale
    }


    """
    Set the nbt from a scoreboard
    """
    def lazy setNBT(mcobject value, mcobject field, mcobject type, float scale){
        lazy mcobject o = Compiler.getObjective(value)
        lazy mcobject s = Compiler.getSelector(value)
        if (value is float){
            setNBT(s, o, field, type, 0.001)
        }
        else{
            setNBT(s, o, field, type, 1)
        }
    }
    """
    Set the nbt from a scoreboard
    """
    def lazy setNBT(mcobject value, mcobject field, mcobject type, float scale){
        lazy mcobject o = Compiler.getObjective(value)
        lazy mcobject s = Compiler.getSelector(value)
        setNBTInner(s, o, field, type, scale)
    }

    """
    Set the nbt from a scoreboard
    """
    private lazy void setNBTInner(mcobject $s, mcobject $o, mcobject $field, mcobject $type, float $scale){
        /execute store result entity @s $field $type $scale run scoreboard players get $s $o
    }

    property x{
        [noReturnCheck=true] lazy float get() getNBT(_ret, "Pos[0]")
        lazy void set(float value) setNBT(value, "Pos[0]", "double")
    }
    property y{
        [noReturnCheck=true] lazy float get() getNBT(_ret, "Pos[1]")
        lazy void set(float value) setNBT(value, "Pos[1]", "double")
    }
    property z{
        [noReturnCheck=true] lazy float get() getNBT(_ret, "Pos[2]")
        lazy void set(float value) setNBT(value, "Pos[2]", "double")
    }
}