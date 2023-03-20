package game.Room

import standard

int t_running, t_total

if (Compiler.isJava){
    import mc.java.nbt as nbt

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

"""
Room detection init
"""
def [tag.order=-100] @tick room_detection_init(){
    @game.room.init()
}

"""
Room detection end
"""
def [tag.order=100] @tick room_detection_end(){
    @game.room.end()
}

"""
Show the list of active room
"""
public @test.after void show(){
    standard.print(("===[ Running Room ]===","green"))
    int running = 0
    int off = 0
    int unknown = 0
    int total = 0
    
    forgenerate($i, @room.count){
        t_running = 0
        t_total = 0
        $i()
        if (t_running == 1){
            standard.print((" [ON] $i","green"))
            running++
        }
        else if (t_running == 0){
            standard.print((" [OFF] $i","red"))
            off++
        }
        else{
            standard.print((" [??] $i","yellow"))
            unknown++
        }
        total++
    }
    standard.print(("Stats: ","white"),(running,"green"),("/"),(total,"green"),(" Running","green"),(" - "),
                    (off,"red"),("/"),(total,"red"),(" Off","red"),(" - "),
                    (unknown,"yellow"),("/"),(total,"yellow"),(" Unknown","yellow"),(" - "))
}

"""
Template for a room that need to execute a function when a player enter it, stay in it, exit it, or when the room is activated or desactivated.
"""
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
    """
    Set the color to display in creative
    """
    def setColor(Color c){
        color = c
    }
    """
    Call back when a player enter the room.
    """
    def onEnter(){
    }
    """
    Call back when a player stays in the room.
    """
    def onStay(){
    }
    """
    Call back when the room get activated. A player enter the room while nobody is in it.
    """
    def onActivate(){
    }
    """
    Call back when the room get desactivated. All the players left the room.
    """
    def onDesactivate(){
    }
    """
    Call back when the room contains at least one player.
    """
    def main(){
    }
    """
    Call back when a player exit the room.
    """
    def onExit(){
    }
    def private ticking __main__(){
        if (nbPlayer > 0){
            main()
        }
    }
    
    def private particule(){
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
    [noReturnCheck=true] private lazy bool check(){
        if (Compiler.isJava){
            return x >= sx && x < ex && y >= sy && y < ey && z >= sz && z < ez
        }
        if (Compiler.isBedrock){
            return @s[x=sx,dx=ex,y=sy,dy=ey,z=sz,dz=ez]
        }
    }
    def [Compile.order=100] @game.room.tick __main_player__(){
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
                    onDesactivate()
                }
                if (nbPlayer < 0){
                    nbPlayer = 0
                }
            }
        }
    }

    """
    Count the number of active room
    """
    def @room.count __count__(){
        t_total += 1
        if (nbPlayer > 0){
            t_running += 1
        }
    }
}