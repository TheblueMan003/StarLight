package cmd.team

def lazy create(mcobject $name, rawjson $text){
    /team add $name $text
}

def lazy setPrefix(mcobject $name, rawjson $text){
    /team modify $name prefix $text
}

def lazy setColor(mcobject $name, mcobject $color){
    /team modify $name color $color
}

def lazy seeFriendlyInvisibles(mcobject $name, bool $value = true){
    /team modify $name seeFriendlyInvisibles $value
}

def lazy hideFriendlyInvisibles(mcobject $name){
    /team modify $name seeFriendlyInvisibles false
}

def lazy disableCollision(mcobject $name){
    /team modify $name collisionRule never
}

def lazy collisionOther(mcobject $name){
    /team modify $name collisionRule pushOtherTeams
}

def lazy collisionOwn(mcobject $name){
    /team modify $name collisionRule pushOwnTeam
}

def lazy enableCollision(mcobject $name){
    /team modify $name collisionRule always
}

def lazy setName(mcobject $name, rawjson $text){
    /team modify $name displayName $text
}

def lazy friendlyFire(mcobject $name, bool $value = true){
    /team modify $name friendlyFire $value
}

def lazy join(mcobject $name, entity $sel){
    /team join $name $sel
}

def lazy join(mcobject $name){
    /team join $name @s
}

def lazy leave(entity $sel = @s){
    /team leave $sel
}

def lazy empty(mcobject $name){
    /team empty $name
}

def lazy delete(mcobject $name){
    /team remove $name
}

package team

scoreboard int teamID


struct Team{
    lazy string name = "$this"
    lazy int id = name.hash()
    
    def lazy __init__(rawjson text = ((""),(""))){
        create(id, text)
    }

    def lazy setPrefix(rawjson text){
        setPrefix(id, text)
    }
    
    def lazy setSuffix(rawjson text){
        setSuffix(id, text)
    }
    
    def lazy setColor(mcobject color){
        setColor(id, color)
    }
    
    def lazy seeFriendlyInvisibles(bool value = true){
        seeFriendlyInvisibles(id, value)
    }
    
    def lazy hideFriendlyInvisibles(){
        hideFriendlyInvisibles(id)
    }
    
    def lazy disableCollision(){
        disableCollision(id)
    }
    
    def lazy collisionOther(){
        collisionOther(id)
    }
    
    def lazy collisionOwn(){
        collisionOwn(id)
    }
    
    def lazy enableCollision(){
        enableCollision(id)
    }
    
    def lazy setName(json text){
        setName(id, text)
    }
    
    def lazy friendlyFire(bool value){
        friendlyFire(id, value)
    }
    
    def lazy join(entity sel){
        with(sel){
            if (teamID != id){
                join(id)
            }
        }
    }
    
    def lazy join(){
        if (teamID != id){
            join(id)
        }
    }
    def lazy forcejoin(){
        join(id)
    }
    
    def lazy __add__(entity sel){
        with(sel){
            if (teamID != id){
                join(id)
            }
        }
    }
    
    def lazy leave(entity sel){
        with(sel){
            team.leave()
            teamID = 0
        }
    }
    
    def lazy leave(){
        team.leave()
        teamID = 0
    }
    
    def lazy __sub__(entity sel){
        with(sel){
            team.leave()
            teamID = 0
        }
    }
    
    def lazy empty(){
        empty(id)
    }
    
    def lazy delete(){
        delete(id)
    }
}