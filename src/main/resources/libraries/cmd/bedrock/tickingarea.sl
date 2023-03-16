package cmd.bedrock.tickingarea

"""
Add a ticking area from x1 y1 z1 to x2 y2 z2 with the name name and preload it if preload is true
"""
def lazy add(int $x1, int $y1, int $z1, int $x2, int $y2, int $z2, string $name, bool $preload = true){
    /tickingarea add $x1 $y1 $z1 $x2 $y2 $z2 $name $preload
}

"""
Remove a ticking area
"""
def lazy remove(string $name){
    /tickingarea remove $name
}

"""
Add Circle Ticking Area
"""
def lazy addCircle(int $x, int $y, int $z, int $radius, string $name, bool $preload = true){
    /tickingarea add circle $x $y $z $radius $name $preload
}

"""
Remove all ticking areas
"""
def lazy removeAll(){
    /tickingarea remove_all
}