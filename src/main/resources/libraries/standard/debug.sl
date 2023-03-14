package standard.debug

if (Compiler.isJava()){
    import cmd.bossbar as b

    lazy string name = Compiler.getProjectName()
    lazy string type = Compiler.getProjectVersionType()
    lazy int major = Compiler.getProjectVersionMajor()
    lazy int minor = Compiler.getProjectVersionMinor()
    lazy int patch = Compiler.getProjectVersionPatch()

    b.Bossbar bar = new b.Bossbar(name,": ", type," ", 1,".",minor,".",patch)

    if (major < 1){
        bar.setName(name,": ", type," ", 1,".",minor,".",patch)
        def ticking main(){
            with(@a,true){
                bar.showEveryone()
            }
        }
    }
    else{
        bar.delete()
    }
}
if (Compiler.isBedrock()){
    import cmd.actionbar as a

    lazy string name = Compiler.getProjectName()
    lazy string type = Compiler.getProjectVersionType()
    lazy int major = Compiler.getProjectVersionMajor()
    lazy int minor = Compiler.getProjectVersionMinor()
    lazy int patch = Compiler.getProjectVersionPatch()

    def ticking main(){
        with(@a,true){
            a.show(-1, 10, name,": ", type," ", 1,".",minor,".",patch)
        }
    }
}