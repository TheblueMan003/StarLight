package mc.java.font

"""
Add a new font to the game
"""
template Font{
    lazy json data = []
    lazy string name = "default"

    """
    Set the name of the font
    """
    def lazy setName(string name){
        this.name = name
    }
    
    """
    Add a new char to the font
    `file`: The file to load the char from
    `ascent`: The ascent of the char
    `height`: The height of the char
    `char`: The char to replace
    `shiftx`: The x shift of the char
    `shifty`: The y shift of the char
    """
    def lazy customAddChar(string file, int ascent, int height, int char, int shiftx, int shifty){
        data += {"type": "bitmap","file": file,"ascent": ascent, "height": height,"chars": [char],"shift": [shiftx, shifty]}
    }
        
    """
    Add a new char to the font
    `file`: The file to load the char from	
    `ascent`: The ascent of the char
    `height`: The height of the char
    `char`: The char to replace
    """
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