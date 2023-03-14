package standard

def lazy print(rawjson $text){
    /tellraw @a $text
}

def lazy tell(rawjson $text){
    /tellraw @s $text
}

def lazy tell(entity $selector,rawjson $text){
    /tellraw $selector $text
}

def lazy debug(rawjson text){
    if (Compiler.isDebug()){
        lazy rawjson prefix = (("[DEBUG]", "purple"),(" "))
        prefix += text
        standard.print(prefix)
    }
}

def (int,int,int) version(){
    return (
        Compiler.getProjectVersionMajor(), 
        Compiler.getProjectVersionMinor(), 
        Compiler.getProjectVersionPatch())
}
package _
def printVersion(){
    lazy string name = Compiler.getProjectName()
    lazy string type = Compiler.getProjectVersionType()
    lazy int major = Compiler.getProjectVersionMajor()
    lazy int minor = Compiler.getProjectVersionMinor()
    lazy int patch = Compiler.getProjectVersionPatch()
    if (major < 1){
        standard.print("============[",name,"]============")
        standard.print("Compiled with: Star Light v")
        standard.print("Project Version: ",type," ", 1,".",minor,".",patch)
        standard.print("==================================")
    }
    else{
        standard.print("============[",name,"]============")
        standard.print("Compiled with: Star Light v")
        standard.print("Project Version: ",type," ", major,".",minor,".",patch)
        standard.print("==================================")
    }
}