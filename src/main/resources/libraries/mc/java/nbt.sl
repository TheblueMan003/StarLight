package mc.java.nbt

if (Compiler.isJava()){
    def lazy getNBT(mcobject ret, mcobject field, float scale){
        lazy mcobject o = Compiler.getObjective(ret)
        lazy mcobject s = Compiler.getSelector(ret)
        getNBT(s, o, field, scale)
    }
    def lazy getNBT(mcobject $s, mcobject $o, mcobject $field, float $scale){
        /execute store result score $s $o run data get entity @s $field $scale
    }
    def lazy setNBT(mcobject value, mcobject field, mcobject type, float scale){
        lazy mcobject o = Compiler.getObjective(value)
        lazy mcobject s = Compiler.getSelector(value)
        setNBT(s, o, field, type, scale)
    }
    def lazy setNBT(mcobject $s, mcobject $o, mcobject $field, mcobject $type, float $scale){
        /execute store result entity @s $field $type $scale run scoreboard players get $s $o
    }

    property x{
        [noReturnCheck=true]  lazy float get() getNBT(_ret, "Pos[0]", 1000)
        lazy void set(float value) setNBT(value, "Pos[0]", "double", 0.001)
    }
    property y{
        [noReturnCheck=true] lazy float get() getNBT(_ret, "Pos[1]", 1000)
        lazy void set(float value) setNBT(value, "Pos[1]", "double", 0.001)
    }
    property z{
        [noReturnCheck=true] lazy float get() getNBT(_ret, "Pos[2]", 1000)
        lazy void set(float value) setNBT(value, "Pos[2]", "double", 0.001)
    }
}