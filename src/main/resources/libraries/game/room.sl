package game.room

if (Compiler.isJava){
    import mc.entity.nbt as nbt

    int x, y, z

    def @playertick player(){
        @game.room.tick()
    }
}
if (Compiler.isBedrock){
    def @playertick player(){
        @game.room.tick()
    }
}

enum Color{
    white, red, orange, yellow, green, aqua, blue, purple
}


def [tag.order=-100] @tick room_detection_init(){
    @game.room.init()
}
def [tag.order=100] @tick room_detection_end(){
    @game.room.end()
}

template Room{
    lazy int sx, sy, sz, ex, ey, ez
    Color color
    scoreboard bool IN
    bool display, hasDisplay, counting
    int nbPlayer
    
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

        if (Compiler.isBedrock()){
            this.ex -= this.sx
            this.ey -= this.sy
            this.ez -= this.sz
        
        }
        if (Compiler.isJava()){
            this.ex += 1
            this.ey += 1
            this.ez += 1
        }
        
        counting = true
        display = true
        color = yellow
    }
    def setColor(Color c){
        color = c
    }
    def onEnter(){
    }
    def onStay(){
    }
    def onActivate(){
    }
    def onDisactivate(){
    }
    def main(){
    }
    def onExit(){
    }
    def private ticking __main__(){
        if (nbPlayer > 0){
            main()
        }
    }
    
    def particule(){
        if (Compiler.isJava){
            switch(color){
                Color.red -> ./particle minecraft:dust 1 0 0 1 ~ ~ ~ 0 0 0 0 1
                Color.orange -> ./particle minecraft:dust 1 0.5 0 1 ~ ~ ~ 0 0 0 0 1
                Color.yellow -> ./particle minecraft:dust 1 1 0 1 ~ ~ ~ 0 0 0 0 1
                Color.green -> ./particle minecraft:dust 0 1 0 1 ~ ~ ~ 0 0 0 0 1
                Color.aqua -> ./particle minecraft:dust 0 1 1 1 ~ ~ ~ 0 0 0 0 1
                Color.blue -> ./particle minecraft:dust 0 0 1 1 ~ ~ ~ 0 0 0 0 1
                Color.purple -> ./particle minecraft:dust 1 0 1 1 ~ ~ ~ 0 0 0 0 1
                Color.white -> ./particle minecraft:dust 1 1 1 1 ~ ~ ~ 0 0 0 0 1
            }
        }
    }
    
    def @game.room.show show(){
        if (Compiler.isJava && Compiler.isDebug()){
            /summon marker ~ 0 ~ {Invisible:1,Tags:["trg_show"]}
            with(@e[tag=trg_show]){
                for(int x = sx;x <= ex;x+=5){
                    @s.x = x
                    @s.y = sy
                    @s.z = sz
                    at(@s){
                        particule()
                    }
                    @s.y = ey
                    at(@s){
                        particule()
                    }
                    @s.y = sy
                    @s.z = ez
                    at(@s){
                        particule()
                    }
                    @s.y = ey
                    at(@s){
                        particule()
                    }
                }
                for(int y = sy;y <= ey;y+=5){
                    @s.y = y
                    @s.x = sx
                    @s.z = sz
                    at(@s){
                        particule()
                    }
                    @s.x = ex
                    at(@s){
                        particule()
                    }
                    @s.x = sx
                    @s.z = ez
                    at(@s){
                        particule()
                    }
                    @s.x = ex
                    at(@s){
                        particule()
                    }
                }
                for(int z = sz;z <= ez;z+=5){
                    @s.z = z
                    @s.y = sy
                    @s.x = sx
                    at(@s){
                        particule()
                    }
                    @s.y = ey
                    at(@s){
                        particule()
                    }
                    @s.y = sy
                    @s.x = ex
                    at(@s){
                        particule()
                    }
                    @s.y = ey
                    at(@s){
                        particule()
                    }
                }
                kill(@s)
            }
        }
    }
    
    def @game.room.init main_init(){
        hasDisplay = false
        if (counting){
            nbPlayer = 0
        }
    }
    def @game.room.end main_end(){
        if (counting){
            counting = false
        }
    }
    def lazy bool check(){
        if (Compiler.isJava){
            return x >= sx && x < ex && y >= sy && y < ey && z >= sz && z < ez
        }
        if (Compiler.isBedrock){
            return @s[x=sx,dx=ex,y=sy,dy=ey,z=sz,dz=ez]
        }
    }
    def [Compile.order=100] @game.room.tick main(){
        if (check()){
            if (counting){
                nbPlayer++
            }
            if (IN){
                onStay()
                if (Compiler.isJava()){
                    if (display && !hasDisplay && @s[gamemode=creative]){
                        show()
                        hasDisplay = true
                    }
                }
            }
            else{
                onEnter()
                if (nbPlayer == 0){
                    onActivate()
                }
                if(!counting){
                    nbPlayer++
                }
                IN = true
            }
        }
        else if (IN){
            IN = false
            onExit()
            
            if (!counting){
                nbPlayer--
                if (nbPlayer == 0){
                    onDisactivate()
                }
                if (nbPlayer < 0){
                    nbPlayer = 0
                }
            }
        }
    }
}