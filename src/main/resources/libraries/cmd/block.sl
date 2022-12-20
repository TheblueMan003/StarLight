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
}


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
def lazy set(mcobject block){
    set(~ ~ ~, block)
}

def lazy fill(mcposition start, mcposition end, mcobject block){
    if (Compiler.isBedrock){
        lazy mcobject bblock = Compiler.getBedrockBlockName(block)
        lazy int bid = Compiler.getBedrockBlockID(block)
        _set(start, end, bblock, bid)
    }
    if (Compiler.isJava){
        _set(start, end, block)
    }
}

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

def lazy replaceNear(int $radius, mcobject from_, mcobject to_){
    fill(~-$radius ~-$radius ~-$radius, ~$radius ~$radius ~$radius, to_, from_)
}