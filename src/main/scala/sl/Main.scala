package sl

import java.io.File
import java.io._

object Main{
  def main(args: Array[String]): Unit = {
    if (args.length == 0){
      build(Array("build","build.slconf"))
    }
    else{
      if (args(0) == "new"){
        newProject(args)
      }
      if (args(0) == "build"){
        println("building project")
        build(args)
      }
      if (args(0) == "compile"){
        println("compiling project")
        compile(args)
      }
    }
  }
  def newProject(args: Array[String]): Unit = {
    val p = getArg(args, "-p")
    val directory = if p == "default" then "." else p
    safeWriteFile(directory+"/build.slconf", List(f"compile -i ./src -o ./output -name ${getArg(args, "-name")}"))
    safeWriteFile(directory+"/src/main.sl", List("package main", "","def ticking main(){","","}"))
  }
  def build(args: Array[String]): Unit = {
    Utils.getFile(args(1)).split("\n").foreach(line => main(line.split(" ")))
  }
  def compile(args: Array[String]): Unit = {
    if (hasArg(args, "-bedrock")){
      Settings.target = MCBedrock
    }
    Settings.outputName = getArg(args, "-name")
    var files = getFiles(sourceFromArg(args, "-i"))
    var tokenized = files.map((f, c) => Parser.parse(f, c))
    if (tokenized.contains(None)) return;
    val context = ContextBuilder.build(getArg(args, "-name"), InstructionList(tokenized.map(_.get)))
    var output = Compiler.compile(context)
    exportOutput(getArg(args, "-o"), output)
  }

  /**
   * Export Output to directory
   */
  def exportOutput(dir: String, output: List[(String, List[String])]):Unit={
    output.foreach((path, content) =>{
      val filename = dir + path
      safeWriteFile(filename, content)
    })
  }

  def safeWriteFile(filename: String, content: List[String]):Unit = {
      val file = new File(filename)
      val directory = new File(file.getParent())

      // Create Directory
      if (!directory.exists()){
         directory.mkdirs()
      }

      // Write all files
      val str = content.foldRight("")(_ + "\n"+ _)
      val out = new PrintWriter(file, "UTF-8")
      out.print(str)
      out.close()
  }

  // Return CMD arg
  def hasArg(args: Array[String], param: String): Boolean ={
    args.contains(param)
  }

  // Return CMD arg
  def getArg(args: Array[String], param: String): String ={
    var started = false
    var ret = "default"
    for(i <- Range(0, args.length)){
        if (args(i) == param){
            started = true
        }
        else if (args(i).startsWith("-")){
            started = false
        }
        else if (started){
            ret = args(i)
        }
    }
    return ret
  }

  // Return List of CMD arg
  def sourceFromArg(args: Array[String], param: String): List[String] ={
    var lst :List[String] = List()
    var started = false
    for(i <- Range(0, args.length)){
        if (args(i) == param){
            started = true
        }
        else if (args(i).startsWith("-")){
            started = false
        }
        else if (started){
            lst = args(i) :: lst
        }
    }
    lst
  }
  def getFiles(paths: List[String]): List[(String, String)] = {
    paths.flatMap(path => getListOfFiles(path)).map(p => (p,Utils.getFile(p)))
  }
  def getListOfFiles(dir: String):List[String] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isFile).map(_.getPath()).toList
    } else if (d.isFile()) {
        List[String](d.getPath())
    }
    else{
        List()
    }
    }
}