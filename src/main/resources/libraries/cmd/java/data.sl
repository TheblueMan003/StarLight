package cmd.java.data

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
Append the data of current entity to data
"""
def lazy append(string $key, json data){
    lazy val nbt = Compiler.toNBT(data)
    Compiler.insert($nbt, nbt){
        /data modify entity @s $key append value $nbt
    }
}

"""
Set the data of target to data
"""
def lazy set(entity target, json data){
    with(target){
        set(data)
    }
}

"""
Append the data of target to data
"""
def lazy append(entity target, string key, json data){
    with(target){
        append(key, data)
    }
}

"""

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