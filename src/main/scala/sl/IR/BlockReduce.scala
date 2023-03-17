package sl.IR

import sl.Settings

class BlockReduce(var files: List[IRFile]){
    val map = files.map(f => f.getName() -> f).toMap

    def run(): List[IRFile] ={
        reduceEmpty()
        computeCallGraph()

        var changed = true
        while(changed){
            reduceBlockCall()
            reduceDupplicate()
            changed = reduceEmpty()
        }

        files.filter(f => !f.deleted)
    }
    def computeCallGraph() ={
        def apply(instr: IRTree)(implicit file: IRFile): Unit = {
            instr match {
                case BlockCall(function, fullName) => {
                    map.get(fullName) match {
                        case Some(file) => file.addCalledBy(function)
                        case None => ()
                    }
                }
                case e: IRExecute => apply(e.getStatements)
                case _ => ()
            }
        }
        files.filterNot(_.isJsonFile()).map(f => f.resetCallGraph())
        files.filterNot(_.isJsonFile()).map(f => {
            f.getContents().foreach(instr => {
                apply(instr)(f)
            })
        })
    }

    def reduceEmpty(): Boolean = {
        var changed = false
        def apply(instr: IRTree)(implicit file: IRFile): IRTree = {
            instr match {
                case e: IRExecute => {
                    apply(e.getStatements) match
                        case EmptyIR => {changed = true;EmptyIR}
                        case instr => e.withStatements(instr)
                }
                case _ => instr
            }
        }


        files.filterNot(_.isJsonFile()).map(f => {
            f.setContents(f.getContents().map(instr => {
                apply(instr)(f)
            }).filterNot(_ == EmptyIR))
        })

        changed
    }

    def reduceBlockCall() ={
        def apply(instr: IRTree)(implicit parent: IRFile): IRTree = {
            instr match {
                case BlockCall(function, fullName) => {
                    map.get(fullName) match {
                        case Some(file) => {
                            val size = file.getContents().length
                            if (file.callByCount() == 1 && size == 1 && !file.hasSelfCall()){
                                file.delete()
                                //println("delete " + file.getName() + " because it is only called once by "+ parent.getName())
                                apply(file.getContents().head)(file)
                            }
                            else if (size == 1 && !file.hasSelfCall()){
                                apply(file.getContents().head)(file)
                            }
                            else if (size == 0){
                                EmptyIR
                            }
                            else{
                                instr
                            }
                        }
                        case None => instr
                    }
                }
                case e: IRExecute => {
                    apply(e.getStatements) match
                        case EmptyIR => EmptyIR
                        case instr => e.withStatements(instr)
                }
                case _ => instr
            }
        }

        files.filterNot(_.isJsonFile()).map(f => {
            f.setContents(f.getContents().map(instr => {
                apply(instr)(f)
            }).filterNot(_ == EmptyIR))
        })

        files.filter(_.getContents().length == 0).map(f=>
            //println("delete " + f.getName() + " because it is empty")
            f.delete()
            )
    }

    def reduceDupplicate() = if (Settings.optimizeDeduplication){
        val irMap = files.filterNot(_.isJsonFile()).filterNot(_.deleted).map(f => f.getContents() -> f).distinct.toMap
        var changed = false
        def apply(instr: IRTree)(implicit parent: IRFile): IRTree = {
            instr match {
                case BlockCall(function, fullName) => {
                    map.get(fullName) match {
                        case Some(file) => {
                            irMap.get(file.getContents()) match {
                                case Some(f) if f != file => {
                                    changed = true
                                    file.delete()
                                    //println("replace " + file.getName() + " with " + f.getName() +" in "+parent.getName()+ " because they are the same:\n"+file.getContents()+"\n===\n"+f.getContents())
                                    BlockCall(Settings.target.getFunctionName(f.getName()), f.getName())
                                }
                                case _ => instr
                            }
                        }
                        case None => instr
                    }
                }
                case e: IRExecute => e.withStatements(apply(e.getStatements))
                case _ => instr
            }
        }

        files.filterNot(_.isJsonFile()).filterNot(_.deleted).map(f => {
            f.setContents(f.getContents().map(instr => {
                apply(instr)(f)
            }))
        })

        if (changed){
            computeCallGraph()
        }
    }
}