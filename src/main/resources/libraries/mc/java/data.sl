package mc.java.data

"""
Set the data of current entity to data
"""
def lazy set(json data){
    lazy val nbt = Compiler.toNBT(data)
    Compiler.insert($nbt, nbt){
        /data merge entity @s $nbt
    }
}

"""
Set the data of current entity target to data
"""
def lazy set(entity target, json data){
    with(target){
        set(data)
    }
}

"""
Set the data of current block to data
"""
def lazy setBlock(json data){
    lazy val nbt = Compiler.toNBT(data)
    Compiler.insert($nbt, nbt){
        /data merge block ~ ~ ~ $nbt
    }
}

"""
Set the data of current block to data
"""
def lazy setBlock(int $x, int $y, int $z, json data){
    lazy val nbt = Compiler.toNBT(data)
    Compiler.insert($nbt, nbt){
        /data merge block $x $y $z $nbt
    }
}