package sl

import java.io.File
import java.io._
import scala.collection.parallel.CollectionConverters._
import objects.Context
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
object Main{
  def main(args: Array[String]): Unit = {
    if (args.length == 0){
      mainLoop()
    }
    else{
      if (args(0) == "new"){
        newProject(args)
      }
      if (args(0) == "build"){
        println("building project")
        build(args(1))
      }
      if (args(0) == "compile"){
        println("compiling project")
        compile(args)
      }
    }
  }
  def mainLoop()={
    var ended = false
    while(!ended){
      Reporter.input()
      val args = scala.io.StdIn.readLine().split(" ")
      try{
        args(0) match
          case "doc" => {
            if (args.length < 2) then {
              Reporter.error(f"Expected 2 argument got: ${args.length-1}")
            }
            else{
              makedoc(args(1)+".slconf", args(2))
              Reporter.ok("Documentation Completed!")
            }
          }
          case "build" => {
            if (args.length < 1) then {
              Reporter.error(f"Expected 1 argument got: ${args.length-1}")
            }
            else{
              build(args(1)+".slconf")
              Reporter.ok("Build Completed!")
            }
          }
          case "new" => {
            newProject(Array())
            Reporter.ok("Project created!")
          }
          case "help" => {
            println("build <config_name>: Build the project with the config contains in the file config_name. The .slconf must be omited.")
            println("new: Make a new project")
            println("help: Show this")
            println("exit: Close")
          }
          case "exit" => {
            ended = true
          }
      }
      catch{
        case e => Reporter.error(e.getMessage())
      }
    }
  }
  def newProject(args: Array[String]): Unit = {
    println("Project Name: ")
    val name = scala.io.StdIn.readLine()
    val p = getArg(args, "-p")
    val directory = if p == "default" then "." else p
    
    ConfigLoader.newProjectPath.map(name => createDirectory(directory+"/"+ name))
    
    safeWriteFile(directory+"/java.slconf", ConfigLoader.get("java", name))
    safeWriteFile(directory+"/bedrock.slconf", ConfigLoader.get("bedrock", name))
    safeWriteFile(directory+"/src/main.sl", List("package main", "","def ticking main(){","","}"))
    ConfigLoader.saveProject(directory+"/")
  }
  def build(args: String): Unit = {
    ConfigLoader.load(args)
    ConfigLoader.loadProject()
    Settings.version=List(Settings.version(0), Settings.version(1), Settings.version(2)+1)
    if (Settings.target == MCJava){
      compile(List("./src"), Settings.java_datapack_output)
    }
    if (Settings.target == MCBedrock){
      compile(List("./src"), Settings.bedrock_behaviorpack_output)
    }
    ConfigLoader.saveProject()
  }
  def makedoc(args: String, prefix: String): Unit = {
    ConfigLoader.load(args)
    ConfigLoader.loadProject()
    Settings.version=List(Settings.version(0), Settings.version(1), Settings.version(2)+1)
    val context = if (Settings.target == MCJava){
      compile(List("./src"), List())
    }
    else if (Settings.target == MCBedrock){
      compile(List("./src"), List())
    }else null
    ConfigLoader.saveProject()
    safeWriteFile(prefix+".html", List(DocMaker.make(context, prefix)))
  }
  def compile(args: Array[String]): Unit = {
    if (hasArg(args, "-bedrock")) Settings.target= MCBedrock
    compile(sourceFromArg(args, "-i"), List(getArg(args, "-o")))
  }
  def compile(inputs: List[String], outputs: List[String]): Context = {
    val start = LocalDateTime.now()
    var files = getFiles(inputs)
    var tokenized = files.par.map((f, c) => Parser.parse(f, c)).toList
    if (tokenized.contains(None)) return null;
    val context = ContextBuilder.build(Settings.name, InstructionList(tokenized.map(_.get)))
    var output = Compiler.compile(context)

    val outputPath = outputs.map(path => 
      if (!path.endsWith("/") && !path.endsWith("\\"))then path + "/" else path
    )

    exportOutput(outputPath, output)

    val end = LocalDateTime.now()
    val time = ChronoUnit.MILLIS.between(start, end)
    Reporter.info(f"Number of files: ${output.size}")
    Reporter.info(f"Number of mcfunctions: ${output.filter(_._1.endsWith(".mcfunction")).size}")
    Reporter.info(f"Number of commands: ${output.filter(_._1.endsWith(".mcfunction")).map(_._2.size).foldRight(0)(_ + _)}")
    Reporter.info(f"Total compile Time: ${time}ms")

    context
  }

  /**
   * Export Output to directory
   */
  def exportOutput(dirs: List[String], output: List[(String, List[String])]):Unit={
    dirs.foreach(deleteDirectory(_))
    output.foreach((path, content) =>{
      dirs.foreach(dir => {
        val filename = dir + path
        safeWriteFile(filename, content)
      })
    })
  }

  def safeWriteFile(filename: String, content: List[String]):Unit = {
    val file = new File(filename)
    try{
      val directory = new File(file.getParent())

      // Create Directory
      if (!directory.exists()){
          directory.mkdirs()
      }
    }catch{
      case _ => {}
    }

    // Write all files
    val str = content.foldRight("")(_ + "\n"+ _)
    val out = new PrintWriter(file, "UTF-8")
    out.print(str)
    out.close()
  }

  def createDirectory(filename: String):Unit = {
    val directory = new File(filename)

    // Create Directory
    if (!directory.exists()){
        directory.mkdirs()
    }
  }

  def deleteDirectory(dir: String): Boolean = deleteDirectory(new File(dir))
  def deleteDirectory(directoryToBeDeleted: File):Boolean= {
        val allContents = directoryToBeDeleted.listFiles()
        if (allContents != null) {
            allContents.foreach(f => deleteDirectory(f))
        }
        directoryToBeDeleted.delete()
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