package mc.java.font

template Font{
    lazy json data = []
    lazy string name = "default"

    def lazy setName(string name){
        this.name = name
    }
    def lazy customAddChar(string file, int ascent, int height, int char, int shiftx, int shifty){
        data += {"type": "bitmap","file": file,"ascent": ascent, "height": height,"chars": [char],"shift": [shiftx, shifty]}
    }
    def lazy customaddChar(int file, int ascent, int height, int char){
        data += {"type": "bitmap","file": file,"ascent": ascent, "height": height,"chars": [char],"shift": [0, 0]}
    }
    [compile.order=999999] private void generate(){
        def private lazy make(string $name){
            [java_rp=true] jsonfile font.$name{
                "providers": data
            }
        }
        make(name)
    }
}