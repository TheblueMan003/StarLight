package standard

def lazy print(rawjson $text){
    /tellraw @a $text
}

def lazy debug(rawjson text){
    if (Compiler.isDebug()){
        lazy rawjson prefix = (("[DEBUG]", purple),(" "))
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
    lazy int major = Compiler.getProjectVersionMajor()
    lazy int minor = Compiler.getProjectVersionMinor()
    lazy int patch = Compiler.getProjectVersionPatch()
    standard.print("============[",name,"]============")
    standard.print("Compiled with: Star Light v")
    standard.print("Project Version: ",major,".",minor,".",patch)
    standard.print("==================================")
}