package game


def lazy initPlayer(void=>void function){
    def ticking init(){
        entity inited
        with(@a not in inited,true){
            function()
            inited += @s
        }
    }
}