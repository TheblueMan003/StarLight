package mc.resourcespack.sound

private lazy json sounds = {}

def lazy add(string file){
    lazy val name = Compiler.replace(file, "/",".")
    if (Compiler.isJava()){
        sounds[name] = {"sounds":[{"name": file,"volume":1,"pitch":1,"weight":1,"stream":false,"attenuation_distance":16,"preload":false}]}
    }
    if (Compiler.isBedrock()){
        lazy val bfile = "sounds/" + file
        sounds[name] = {"category":"hostile","sounds":[{"name": bfile,"volume":1,"pitch":1,"weight":1,"stream":false,"load_on_low_memory":true}]}
    }
}
def lazy music(string file){
    lazy val name = Compiler.replace(file, "/",".")
    if (Compiler.isJava()){
        sounds[name] = {"sounds":[{"name": file,"volume":1,"pitch":1,"weight":1,"stream":true,"attenuation_distance":100000,"preload":false}]}
    }
    if (Compiler.isBedrock()){
        lazy val bfile = "sounds/" + file
        sounds[name] = {"category":"music","sounds":[{"name": bfile,"volume":1,"pitch":1,"weight":1,"stream":true,"load_on_low_memory":true}]}
    }
}
def lazy music(string file, float volume){
    lazy val name = Compiler.replace(file, "/",".")
    if (Compiler.isJava()){
        sounds[name] = {"sounds":[{"name": file,"volume":volume,"pitch":1,"weight":1,"stream":true,"attenuation_distance":100000,"preload":false}]}
    }
    if (Compiler.isBedrock()){
        lazy val bfile = "sounds/" + file
        sounds[name] = {"category":"music","sounds":[{"name": bfile,"volume":volume,"pitch":1,"weight":1,"stream":true,"load_on_low_memory":true}]}
    }
}

def [Compile.order=999999] generate(){
    [java_rp=true] jsonfile sounds sounds
}
