package sl.IR

import sl.Settings

private var debug = false

class BlockReduce(var files: List[IRFile]){
    val map = files.map(f => f.getName() -> f).toMap
    var globalChanged = false

    def run(): (List[IRFile], Boolean) ={
        reduceEmpty()
        computeCallGraph()

        var changed = true
        while(changed){
            reduceNoCall()
            reduceBlockCall()
            reduceDupplicate()
            changed = reduceEmpty()
            if (changed){
                globalChanged = true
            }
        }

        val filtered = files.filter(f => !f.deleted)

        (filtered, globalChanged || filtered.length != files.length)
    }
    def reduceNoCall()={
        files.filter(f => f.calledBy.size == 0 && f.canBeDeleted() && !f.isJsonFile()).foreach(f => {
            if (debug){println("delete " + f.getName() + " because it is not called by anyone")}
            f.delete()
        })
    }
    def computeCallGraph() ={
        def apply(instr: IRTree)(implicit parent: IRFile): Unit = {
            instr match {
                case BlockCall(function, fullName, args) => {
                    map.get(fullName) match {
                        case Some(file) => file.addCalledBy(parent.getName())
                        case None => (println("error: " + fullName + " not found in " + parent.getName()))
                    }
                }
                case ScheduleCall(function, fullName, time) => {
                    map.get(fullName) match {
                        case Some(file) => file.addCalledBy(parent.getName())
                        case None => (println("error: " + fullName + " not found in " + parent.getName()))
                    }
                }
                case ScheduleClear(function, fullName) => {
                    map.get(fullName) match {
                        case Some(file) => file.addCalledBy(parent.getName())
                        case None => (println("error: " + fullName + " not found in " + parent.getName()))
                    }
                }
                case e: IRExecute => apply(e.getStatements)
                case _ => ()
            }
        }
        files.filterNot(_.isJsonFile()).map(f => f.resetCallGraph())
        files.filterNot(f => f.isJsonFile() || f.deleted).map(f => {
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
        def applyTop(instr: IRTree)(implicit parent: IRFile, inliningSet: Set[String] = Set()): List[IRTree] = {
            instr match {
                case BlockCall(function, fullName, args) if args == null || args == "" => {
                    map.get(fullName) match {
                        case Some(file) => {
                            val size = file.getContents().length
                            if (file.callByCount() == 1 && !file.hasSelfCall() && !parent.deleted && !inliningSet.contains(fullName)){
                                //file.delete()
                                //if (debug){println("delete " + file.getName() + " because it is only called once by "+ file.calledBy)}
                                file.getContents().flatMap(c => applyTop(c)(file, inliningSet + fullName))
                            }
                            else if (size == 0){
                                List()
                            }
                            else{
                                List(instr)
                            }
                        }
                        case None => List(instr)
                    }
                }
                case other => List(other)
            }
        }
        def apply(instr: IRTree)(implicit parent: IRFile, inliningSet: Set[String] = Set()): IRTree = {
            instr match {
                case BlockCall(function, fullName, args) if args == null || args == "" => {
                    map.get(fullName) match {
                        case Some(file) => {
                            val size = file.getContents().length
                            if (file.callByCount() == 1 && size == 1 && !file.hasSelfCall() && !parent.deleted && !inliningSet.contains(fullName)){
                                //file.delete()
                                //if (debug){println("delete " + file.getName() + " because it is only 1 line and called once by "+ file.calledBy+" in "+parent.getName())}
                                apply(file.getContents().head)(file, inliningSet + fullName)
                            }
                            else if (size == 1 && !file.hasSelfCall() && !inliningSet.contains(fullName)){
                                apply(file.getContents().head)(file, inliningSet + fullName)
                            }
                            else if (size == 0 || file.deleted){
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

        files.filterNot(f => f.isJsonFile() && !f.deleted).map(f => {
            f.setContents(f.getContents().flatMap(instr=> applyTop(instr)(f)).map(instr => {
                apply(instr)(f)
            }).filterNot(_ == EmptyIR))
        })
        
        files.filter(_.getContents().length == 0).map(f=>
            if (debug){println("delete " + f.getName() + " because it is empty")}
            f.delete()
            )
    }

    def reduceDupplicate() = if (Settings.optimizeDeduplication){
        val irMap = files.filterNot(_.isJsonFile()).filterNot(_.deleted).map(f => f.getContents() -> f).distinct.toMap
        var changed = false
        def apply(instr: IRTree)(implicit parent: IRFile): IRTree = {
            instr match {
                case BlockCall(function, fullName, args) if args == null || args == "" => {
                    map.get(fullName) match {
                        case Some(file) => {
                            irMap.get(file.getContents()) match {
                                case Some(f) if f != file && !parent.deleted=> {
                                    changed = true
                                    //file.delete()
                                    //if(debug){println("replace " + file.getName() + " with " + f.getName() +" in "+parent.getName()+ " because they are the same:\n"+file.getContents()+"\n===\n"+f.getContents())}
                                    BlockCall(Settings.target.getFunctionName(f.getName()), f.getName(), args)
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