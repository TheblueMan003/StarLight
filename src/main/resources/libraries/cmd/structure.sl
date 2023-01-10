package cmd.structure

def lazy load(string $name){
    if (Compiler.isJava()){
        /place template $name
    }
    if (Compiler.isBedrock()){
        /structure load $name ~ ~ ~
    }
}

def lazy save(string $name, mcposition $pos1, mcposition $pos2){
    if (Compiler.isBedrock()){
        /structure save $name $pos1 $pos2
    }
}
