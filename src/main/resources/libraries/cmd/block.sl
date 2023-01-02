package cmd.block

if (Compiler.isBedrock){
    private lazy void _set(mcposition $position, mcobject $block, int $id){
        /setblock $position $block $id
    }
    private lazy void _fill(mcposition $start, mcposition $end, mcobject $block, int $id){
        /fill $start $end $block $id
    }
    private lazy void _fill(mcposition $start, mcposition $end, mcobject $block, int $id, mcobject $fro, int $id2){
        /fill $start $end $block $id replace $fro $id2
    }
    private lazy void _fill(mcposition $start, mcposition $end, mcobject $block, int $id, string $mod){
        /fill $start $end $block $id $mod
    }
    private lazy void _clone(mcposition $start, mcposition $end, mcposition $target, mcobject $block, int $id, string $mod2){
        /clone $start $end $target filtered $mod2 $block $id $mod2
    }
}
if (Compiler.isJava){
    private lazy void _set(mcposition $position, mcobject $block){
        /setblock $position $block
    }
    private lazy void _fill(mcposition $start, mcposition $end, mcobject $block){
        /fill $start $end $block
    }
    private lazy void _fill(mcposition $start, mcposition $end, mcobject $block, mcobject $fro){
        /fill $start $end $block replace $fro
    }
    private lazy void _fill(mcposition $start, mcposition $end, mcobject $block, string $mod){
        /fill $start $end $block $mod
    }
    private lazy void _clone(mcposition $start, mcposition $end, mcposition $target, mcobject $block, string $mod2){
        /clone $start $end $target filtered $mod2 $block $mod2
    }
}
private lazy void _clone(mcposition $start, mcposition $end, mcposition $target, string $mod1, string $mod2){
    /clone $start $end $target $mod1 $mod2
}


"""
Set a `block` at `position`
"""
def lazy set(mcposition position, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _set(position, bblock, bid)
    }
    if (Compiler.isJava){
        _set(position, block)
    }
}
"""
Set a `block` here
"""
def lazy set(mcobject block){
    set(~ ~ ~, block)
}

"""
Fill from `start` to `end` with `block`
"""
def lazy fill(mcposition start, mcposition end, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _fill(start, end, bblock, bid)
    }
    if (Compiler.isJava){
        _fill(start, end, block)
    }
}

"""
Replace `fro` to `block` from `start` to `end`
"""
def lazy fill(mcposition start, mcposition end, mcobject block, mcobject fro){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        lazy mcobject fbblock = Compiler.getBedrockBlockName(fro)
        lazy int fbid = Compiler.getBedrockBlockID(fro)
        _fill(start, end, bblock, bid, fbblock, fbid)
    }
    if (Compiler.isJava){
        _fill(start, end, block, fro)
    }
}

"""
Replace `from_` to `to_` in a radius.
"""
def lazy replaceNear(int $radius, mcobject from_, mcobject to_){
    fill(~-$radius ~-$radius ~-$radius, ~$radius ~$radius ~$radius, to_, from_)
}

"""
Fill and Destroy from `start` to `end` with `block`
"""
def lazy fillDestroy(mcposition start, mcposition end, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _fill(start, end, bblock, bid, "destroy")
    }
    if (Compiler.isJava){
        _fill(start, end, block, "destroy")
    }
}

"""
Hollow Fill from `start` to `end` with `block`
"""
def lazy fillHollow(mcposition start, mcposition end, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _fill(start, end, bblock, bid, "hollow")
    }
    if (Compiler.isJava){
        _fill(start, end, block, "hollow")
    }
}

"""
Fill Keep from `start` to `end` with `block`
"""
def lazy fillKeep(mcposition start, mcposition end, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _fill(start, end, bblock, bid, "keep")
    }
    if (Compiler.isJava){
        _fill(start, end, block, "keep")
    }
}

"""
Fill Outline from `start` to `end` with `block`
"""
def lazy fillOutline(mcposition start, mcposition end, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _fill(start, end, bblock, bid, "outline")
    }
    if (Compiler.isJava){
        _fill(start, end, block, "outline")
    }
}

"""
Clone Area from `start` to `end`, to new area `target`
"""
def lazy clone(mcposition start, mcposition end, mcposition target){
    _clone(start, end, target, "replace", "normal")
}
"""
Mask Clone Area from `start` to `end`, to new area `target`
"""
def lazy cloneMask(mcposition start, mcposition end, mcposition target){
    _clone(start, end, target, "masked", "normal")
}
"""
Clone Filtered by `block` in Area from `start` to `end`, to new area `target`
"""
def lazy clone(mcposition start, mcposition end, mcposition target, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _clone(start, end, target, bblock, bid, "normal")
    }
    if (Compiler.isJava){
        _clone(start, end, target, block, "normal")
    }
}


"""
Move Area from `start` to `end`, to new area `target`
"""
def lazy move(mcposition start, mcposition end, mcposition target){
    _clone(start, end, target, "replace", "move")
}
"""
Mask Move Area from `start` to `end`, to new area `target`
"""
def lazy moveMask(mcposition start, mcposition end, mcposition target){
    _clone(start, end, target, "masked", "move")
}
"""
Move Filtered by `block` in Area from `start` to `end`, to new area `target`
"""
def lazy move(mcposition start, mcposition end, mcposition target, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _clone(start, end, target, bblock, bid, "move")
    }
    if (Compiler.isJava){
        _clone(start, end, target, block, "move")
    }
}