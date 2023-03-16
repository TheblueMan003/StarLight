package game.Region

"""
Template for an area of chunk that need to be loaded
"""
template Region{
    lazy int sx, sy, sz, ex, ey, ez
    lazy string path = "$this"
    lazy int id = Compiler.hash(path)
    lazy string name = "sl.region."+id
    bool loaded
    
    def lazy init(int sx, int sy, int sz, int ex, int ey, int ez){
        if (sx > ex){
            this.sx, this.ex = ex, sx
        }
        else{
            this.sx, this.ex = sx, ex
        }

        if (sy > ey){
            this.sy, this.ey = ey, sy
        }
        else{
            this.sy, this.ey = sy, ey
        }

        if (sz > ez){
            this.sz, this.ez = ez, sz
        }
        else{
            this.sz, this.ez = sz, ez
        }
    }

    """
    Load the region
    """
    def load(){
        if (Compiler.isJava()){
            import cmd.java.forceload as fl
            fl.forceload(sx, sz, ex, ez)
        }
        if (Compiler.isBedrock()){
            import cmd.bedrock.tickingarea as ta
            ta.add(sx, sy, sz, ex, ey, ez, name)
            loaded = true
        }
    }

    """
    Unload the region
    """
    def unload(){
        if (Compiler.isJava()){
            import cmd.java.forceload as fl
            fl.forceunload(sx, sz, ex, ez)
        }
        if (Compiler.isBedrock()){
            import cmd.bedrock.tickingarea as ta
            ta.remove(name)
            loaded = false
        }
    }

    """
    Return if Loaded
    """
    [noReturnCheck=true] bool isLoaded(){
        if (Compiler.isBedrock()){
            return loaded
        }
        if (Compiler.isJava()){
            var ret = true
            foreach(x in sx..ex by 16){
                foreach(z in sz..ez by 16){
                    Compiler.insert($x, x){
                        Compiler.insert($z, z){
                            if (loaded($x ~ $z)){
                                ret = false
                            }
                        }
                    }
                }
            }
            return ret
        }
    }

}