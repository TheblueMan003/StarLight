package random

if (Compiler.isBedrock){
    """
    Return a random number between `x` (included) and `y` (excluded)
    """
    lazy int range(int x, int y){
        Compiler.random(_ret, x, y-1)
    }
}
if (Compiler.isJava){
    import mc.java.nbt as nbt
    
    """
    Return a random number between `x` (included) and `y` (excluded)
    """
    lazy int range(int x, int y){
        /summon marker ~ ~ ~ {Tags:["random.trg"]}
        with(@e[tag=random.trg,limit=1]){
            nbt.getNBT(_ret, "UUID[0]", 1)
            /kill
        }
        _ret %= y-x
        _ret += x
    }
}

"""
Return a random number between 0 and `x` (excluded)
"""
lazy int range(int x){
    return range(0, x)
}

"""
Return a random number between `x` (included) and `y` (excluded)
"""
lazy int next(){
    import standard.int as int
    return range(int.minValue, int.maxValue)
}