package game.room

if (Compiler.isJava){
    import mc.entity.nbt as nbt

    int x, y, z
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

def @playertick(){
    x = nbt.x
    y = nbt.y
    z = nbt.z
    @game.room.tick()
}

struct Room{
    lazy int sx, sy, sz, ex, ey, ez
    void=>void eventEnter, eventStay, eventExit, eventActivate, eventDisactivate
    bool hasEnterEvent, hasStayEvent, hasExitEvent, hasActivateEvent, hasDisactivateEvent
    Color color
    BOOL IN
    bool display, hasDisplay, count
    int nbPlayer
    
    def __init__(int sx, int sy, int sz, int ex, int ey, int ez, 
        void=>void onStay = null, void=>void onEnter = null,
        void=>void onExit = null, void=>void onActivate = null, void=>void onDisactivate = null){
        this.sx, this.ex = math.sorted(sx, ex)
        this.sy, this.ey = math.sorted(sy, ey)
        this.sz, this.ez = math.sorted(sz, ez)
        this.ex += 1
        this.ey += 1
        this.ez += 1
        
        this.onEnter(onEnter)
        this.onStay(onStay)
        this.onExit(onExit)
        this.onActivate(onActivate)
        this.onDisactivate(onDisactivate)
        
        count = true
        display = true
        color = yellow
    }
    def setColor(Color c){
        color = c
    }
    def onEnter(void=>void event){
        eventEnter = event
        if (event !=null){
            hasEnterEvent = true
        }
    }
    def onStay(void=>void event){
        eventStay = event
        if (event !=null){
            hasStayEvent = true
        }
    }
    def onActivate(void=>void event){
        eventActivate = event
        if (event !=null){
            hasActivateEvent = true
        }
    }
    def onDisactivate(void=>void event){
        eventDisactivate= event
        if (event !=null){
            hasDisactivateEvent = true
        }
    }
    def onExit(void=>void event){
        eventExit = event
        if (event !=null){
            hasExitEvent = true
        }
    }
    
    def particule(){
        if (Compiler.isJava){
            switch(color){
                Color.red -> /particle minecraft:dust 1 0 0 1 ~ ~ ~ 0 0 0 0 1
                Color.orange -> /particle minecraft:dust 1 0.5 0 1 ~ ~ ~ 0 0 0 0 1
                Color.yellow -> /particle minecraft:dust 1 1 0 1 ~ ~ ~ 0 0 0 0 1
                Color.green -> /particle minecraft:dust 0 1 0 1 ~ ~ ~ 0 0 0 0 1
                Color.aqua -> /particle minecraft:dust 0 1 1 1 ~ ~ ~ 0 0 0 0 1
                Color.blue -> /particle minecraft:dust 0 0 1 1 ~ ~ ~ 0 0 0 0 1
                Color.purple -> /particle minecraft:dust 1 0 1 1 ~ ~ ~ 0 0 0 0 1
                Color.white -> /particle minecraft:dust 1 1 1 1 ~ ~ ~ 0 0 0 0 1
            }
        }
    }
    
    def @game.room.show show(){
        if (Compiler.isJava && Compiler.isDebug){
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
        if (count){
            nbPlayer = 0
        }
    }
    def @game.room.end main_end(){
        if (count){
            count = false
        }
    }
    def lazy bool check(){
        if (Compiler.isJava){
            return x >= sx && x < ex && y >= sy && y < ey && z >= sz && z < ez
        }
        if (Compiler.isBedrock){
            return @s[x=]
        }
    }
    def @game.room.tick main(){
        if (x >= sx && x < ex && y >= sy && y < ey && z >= sz && z < ez){
            if (count){
                nbPlayer++
            }
            if (!IN){
                if (hasEnterEvent){
                    eventEnter()
                }
                if (nbPlayer == 0 && hasActivateEvent){
                    eventActivate()
                }
                if(!count){
                    nbPlayer++
                }
                IN = true
            }
            if (IN){
                if (hasStayEvent){
                    eventStay()
                }
                if (display && !hasDisplay && @s[gamemode=creative]){
                    show()
                    hasDisplay = true
                }
            }
        }
        else if (IN){
            IN = false
            if (hasExitEvent){
                eventExit()
            }
            if (!count){
                nbPlayer--
                if (nbPlayer == 0 && hasDisactivateEvent){
                    eventDisactivate()
                }
                if (nbPlayer < 0){
                    nbPlayer = 0
                }
            }
        }
    }
}