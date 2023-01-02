package sl

import java.io.File
import java.io._
import scala.collection.parallel.CollectionConverters._
import objects.Context
import objects.JSONFile
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import sl.files.*

object Main{
  def main(args: Array[String]): Unit = {
    if (args.length == 0){
      mainLoop()
    }
    else{
      if (args(0) == "compile"){
        println("compiling project")
        compile(args)
      }
      else{
        mainLoop(args)
      }
    }
  }
  def mainLoop(argsOri: Array[String] = null)={
    var ended = false
    while(!ended){
      Reporter.input()
      val args = if argsOri != null then argsOri else scala.io.StdIn.readLine().split(" ")
      try{
        args(0) match
          case "doc" => {
            val libraries: List[String] = FileUtils.getListOfFiles("./src/main/resources/libraries").filterNot(_.contains("__init__.sl"))
            libraries.foreach(f => makeDocumentation(f.dropRight(3).replaceAllLiterally("\\","/").replaceAllLiterally("./src/main/resources/libraries/",""), List(f)))
            val names = libraries.map(f => f.dropRight(3).replaceAllLiterally("\\","/").replaceAllLiterally("./src/main/resources/libraries/",""))
            FileUtils.safeWriteFile("docs/index.md", List(DocMaker.makeIndex(names)))
            Reporter.ok("Documentation Completed!")
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
      if (argsOri != null){
        ended = true
      }
    }
  }
  def newProject(args: Array[String]): Unit = {
    println("Project Name: ")
    val name = scala.io.StdIn.readLine()
    val p = getArg(args, "-p")
    val directory = if p == "default" then "." else p
    
    ConfigLoader.newProjectPath.map(name => FileUtils.createDirectory(directory+"/"+ name))
    
    FileUtils.safeWriteFile(directory+"/java.slconf", ConfigLoader.get("java", name))
    FileUtils.safeWriteFile(directory+"/bedrock.slconf", ConfigLoader.get("bedrock", name))
    FileUtils.safeWriteFile(directory+"/src/main.sl", List("package main", "","def ticking main(){","","}"))
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
  def compile(args: Array[String]): Unit = {
    if (hasArg(args, "-bedrock")) Settings.target= MCBedrock
    compile(sourceFromArg(args, "-i"), List(getArg(args, "-o")))
  }
  def compile(inputs: List[String], outputs: List[String]): Context = {
    val start = LocalDateTime.now()
    var files = FileUtils.getFiles(inputs)

    var tokenized = files.par.map((f, c) => Parser.parse(f, c)).toList

    if (tokenized.contains(None)) throw new Exception("Failled to Parse")
    val context = ContextBuilder.build(Settings.name, InstructionList(tokenized.map(_.get)))
    var output = Compiler.compile(context)

    val time = ChronoUnit.MILLIS.between(start, LocalDateTime.now())
    Reporter.info(f"Number of files: ${output.size}")
    Reporter.info(f"Number of mcfunctions: ${output.filter(_._1.endsWith(".mcfunction")).size}")
    Reporter.info(f"Number of commands: ${output.filter(_._1.endsWith(".mcfunction")).map(_._2.size).foldRight(0)(_ + _)}")
    Reporter.info(f"Total compile Time: ${time}ms")

    val exportStart = LocalDateTime.now()
    val outputPath = outputs.map(path => 
      if (!path.endsWith("/") && !path.endsWith("\\"))then path + "/" else path
    ).toList

    DataPackBuilder.build(outputPath, output)
    if (Settings.target == MCJava){
        Settings.java_resourcepack_output.map(path => 
          if (!path.endsWith("/") && !path.endsWith("\\"))then path + "/" else path
        ).foreach(f => ResourcePackBuilder.build("java_resourcepack", f, context.getAllJsonFiles().filter(_.isJavaRP())))
    }
    if (Settings.target == MCBedrock){
        Settings.bedrock_resourcepack_output.map(path => 
          if (!path.endsWith("/") && !path.endsWith("\\"))then path + "/" else path
        ).foreach(f => ResourcePackBuilder.build("bedrock_resourcepack", f, context.getAllJsonFiles().filter(_.isBedrockRP())))
    }

    val time2 = ChronoUnit.MILLIS.between(exportStart, LocalDateTime.now())
    Reporter.info(f"Total export Time: ${time2}ms")

    val time3 = ChronoUnit.MILLIS.between(start, LocalDateTime.now())
    Reporter.info(f"Total Time: ${time3}ms")

    context
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

  def makeDocumentation(name: String, inputs: List[String])={
    var files = FileUtils.getFiles(inputs)

    var tokenized = files.par.map((f, c) => Parser.parse(f, c)).toList

    if (tokenized.contains(None)) throw new Exception("Failled to Parse")

    FileUtils.safeWriteFile(f"./docs/libraries/${name}.md", List(DocMaker.make2(InstructionList(tokenized.map(_.get)))))
  }
}