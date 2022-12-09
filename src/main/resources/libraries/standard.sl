package standard

def lazy print(rawjson $text){
    /tellraw @a $text
}

def lazy debug(rawjson text){
    if (Compiler.isDebug()){
        lazy rawjson prefix = (("[DEBUG]"),(""))
        prefix += text
        standard.print(prefix)
    }
}