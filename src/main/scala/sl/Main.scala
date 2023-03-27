package sl

import java.io.File
import java.io._
import scala.collection.parallel.CollectionConverters._
import objects.Context
import objects.JSONFile
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import sl.files.*
import sl.files.CacheAST
import sl.IR.*

object Main{
  private var lastIR: List[IRFile] = null
  private var lastContxt: Context = null
  private var interpreter: Interpreter = null
  private var lastBuild: String = null

  def main(args: Array[String]): Unit = {
    if (args.length == 0){
      mainLoop()
    }
    else{
      if (args(0) == "compile"){
        println("compiling project")
        compile(args)
        FileUtils.deleteDirectory("./bin")
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
            if (args.length < 1 && lastBuild == null) then {
              Reporter.error(f"Expected 1 argument got: ${args.length-1}")
            }
            else if (args.length < 1 && lastBuild != null) then {
              build(lastBuild)
              Reporter.ok("Build Completed!")
            }
            else{
              lastBuild = args(1)+".slconf"
              build(args(1)+".slconf")
              Reporter.ok("Build Completed!")
            }
          }
          case "test" => {
            if (args.length < 1) then {
              Reporter.error(f"Expected 1 argument got: ${args.length-1}")
            }
            else{
              lastBuild = args(1)+".slconf"
              build(args(1)+".slconf")
              test()
              Reporter.ok("Test Completed!")
            }
          }
          case "testScala" => {
            if (args.length < 1) then {
              Reporter.error(f"Expected 1 argument got: ${args.length-1}")
            }
            else{
              compile(args.drop(1))
              test()
              Reporter.ok("Test Completed!")
            }
          }
          case "run" => {
            if (args.length < 1) then {
              Reporter.error(f"Expected 1 argument got: ${args.length-1}")
            }
            else{
              run(args(1))
              Reporter.ok("Run Completed!")
            }
          }
          case "debug" => {
            if (args.length < 1) then {
              Reporter.error(f"Expected 1 argument got: ${args.length-1}")
            }
            else{
              debug(args(1))
              Reporter.ok("Debug Completed!")
            }
          }
          case "new" => {
            newProject(Array())
            Reporter.ok("Project created!")
          }
          case "clearcache" => {
            FileUtils.deleteDirectory("./bin")
            DataPackBuilder.clearCache()
            ResourcePackBuilder.clearCache()
            Reporter.ok("Cache cleared!")
          }
          case "help" => {
            println("build <config_name>: Build the project with the config contains in the file config_name. The .slconf must be omited.")
            println("new: Make a new project")
            println("run <function>: Interpret the function. The .sl must be omited and the internal name must be used. Example: `main.ticking.main`. You must compile the project before.")
            println("debug <function>: Interpret the function in debug mode (print every operation). The .sl must be omited and the internal name must be used. Example: `main.ticking.main`. You must compile the project before.")
            println("help: Show this")
            println("exit: Close")
          }
          case "exit" => {
            FileUtils.deleteDirectory("./bin")
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
  def test(): Unit = {
    if (interpreter == null){
      interpreter = new Interpreter(lastIR, lastContxt)
    }
    interpreter.run(1000, "default.test.runAll")
  }
  def run(args: String): Unit = {
    if (interpreter == null){
      interpreter = new Interpreter(lastIR, lastContxt)
    }
    interpreter.run(args, false)
    interpreter.printScoreboards()
  }
  def debug(args: String): Unit = {
    if (interpreter == null){
      interpreter = new Interpreter(lastIR, lastContxt)
    }
    interpreter.run(args, true)
    interpreter.printScoreboards()
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

    FileUtils.copyFromResourcesToFolder("icon/64.png", directory+"/java_resourcepack/pack.png")
    FileUtils.copyFromResourcesToFolder("icon/256.png", directory+"/bedrock_resourcepack/pack_icon.png")

    ConfigLoader.saveProject(directory+"/")
  }
  def build(args: String): Unit = {
    ConfigLoader.load(args)
    ConfigLoader.loadProject()
    Settings.version=List(Settings.version(0), Settings.version(1), Settings.version(2)+1)

    val libs = FileUtils.getListOfFiles("./lib").map(l => "./lib/"+l+"/src")
    val datpack = if (Settings.target == MCJava)
                FileUtils.getListOfFiles("./lib").map(l => "./lib/"+l+"/java_datapack")
              else
                FileUtils.getListOfFiles("./lib").map(l => "./lib/"+l+"/bedrock_datapack")
    val respack = if (Settings.target == MCJava)
                FileUtils.getListOfFiles("./lib").map(l => "./lib/"+l+"/java_resourcepack")
              else
                FileUtils.getListOfFiles("./lib").map(l => "./lib/"+l+"/bedrock_resourcepack")

    if (Settings.target == MCJava){
      compile(args, "./src"::libs, "./java_datapack"::datpack, "./java_resourcepack"::respack, Settings.java_datapack_output)
    }
    if (Settings.target == MCBedrock){
      compile(args, "./src"::libs, "./bedrock_datapack"::datpack, "./bedrock_resourcepack"::respack, Settings.bedrock_behaviorpack_output)
    }
    ConfigLoader.saveProject()
  }
  def compile(args: Array[String]): Unit = {
    if (hasArg(args, "-bedrock")) Settings.target= MCBedrock
    compile("_", sourceFromArg(args, "-i"), List(), List(), List(getArg(args, "-o")))
  }
  def compile(script: String, inputs: List[String], dataInput: List[String],resourceInput: List[String], outputs: List[String]): Context = {
    val start = LocalDateTime.now()
    var files = FileUtils.getFiles(inputs)

    Reporter.phase(f"===========[Parsing]==========")
    var tokenized = files.par.map(f => Parser.parseFromFile(f, ()=>Utils.getFile(f))).toList

    Reporter.phase(f"===========[Compiling]==========")
    if (tokenized.contains(None)) throw new Exception("Failled to Parse")
    val context = ContextBuilder.build(Settings.name, InstructionList(tokenized))
    var output = Compiler.compile(context)

    val time = ChronoUnit.MILLIS.between(start, LocalDateTime.now())
    Reporter.info(f"Number of files: ${output.size}")
    Reporter.info(f"Number of mcfunctions: ${output.filter(_.getPath().endsWith(".mcfunction")).size}")
    Reporter.info(f"Number of commands: ${output.filter(_.getPath().endsWith(".mcfunction")).map(_.getContents().size).foldRight(0)(_ + _)}")
    Reporter.info(f"Total compile Time: ${time}ms")

    val exportStart = LocalDateTime.now()
    val outputPath = outputs.map(path => 
      if (!path.endsWith("/") && !path.endsWith("\\"))then path + "/" else path
    ).toList

    if (Settings.optimize){
      Reporter.phase(f"===========[Optimizing]==========")
      val optstart = LocalDateTime.now()
      var count = 0
      var changed = true
      while(count < 20 && changed){
        changed = false
        Reporter.info(f"Iteration ${count}")
        if (Settings.optimizeVariableValue){
          Reporter.info(f">> Optimizing variable")
          val (a, b) = sl.IR.ScoreboardReduce(output, context.getScoreboardUsedForce()).run()
          output = a
          changed |= b
        }
        if (Settings.optimizeInlining){
          Reporter.info(f">> Optimizing calls")
          val (a, b) = sl.IR.BlockReduce(output).run()
          output = a
          changed |= b
        }
        count += 1
      }

      val opttime = ChronoUnit.MILLIS.between(optstart, LocalDateTime.now())
      Reporter.info(f"Number of files: ${output.size}")
      Reporter.info(f"Number of mcfunctions: ${output.filter(_.getPath().endsWith(".mcfunction")).size}")
      Reporter.info(f"Number of commands: ${output.filter(_.getPath().endsWith(".mcfunction")).map(_.getContents().size).foldRight(0)(_ + _)}")
      Reporter.info(f"Total optimization Time: ${opttime}ms")
    }

    Reporter.phase(f"===========[Exporting]==========")

    lastIR = output
    lastContxt = context
    interpreter = null
    DataPackBuilder.build(script, dataInput, outputPath, output)
    if (Settings.target == MCJava){
        Settings.java_resourcepack_output.map(path => 
          if (!path.endsWith("/") && !path.endsWith("\\"))then path + "/" else path
        ).foreach(f => ResourcePackBuilder.build(resourceInput, f, Settings.target.getResourcesExtraFiles(context) :::context.getAllJsonFiles().filter(_.isJavaRP()).map(f => (f.getIRFile()))))
    }
    if (Settings.target == MCBedrock){
        Settings.bedrock_resourcepack_output.map(path => 
          if (!path.endsWith("/") && !path.endsWith("\\"))then path + "/" else path
        ).foreach(f => ResourcePackBuilder.build(resourceInput, f, Settings.target.getResourcesExtraFiles(context):::context.getAllJsonFiles().filter(_.isBedrockRP()).map(f => (f.getIRFile()))))
    }

    val time2 = ChronoUnit.MILLIS.between(exportStart, LocalDateTime.now())
    Reporter.info(f"Total export Time: ${time2}ms")

    val time3 = ChronoUnit.MILLIS.between(start, LocalDateTime.now())
    Reporter.info(f"Total Time: ${time3}ms")

    //FileUtils.safeWriteFile("vscode/token", Utils.getConfig("blocks.txt"):::Utils.getConfig("sounds/java.csv").map("minecraft:"+_):::Utils.getConfig("particles.txt"))

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

    var tokenized = files.par.map(f => Parser.parse(f, Utils.getFile(f))).toList

    if (tokenized.contains(None)) throw new Exception("Failled to Parse")

    FileUtils.safeWriteFile(f"./docs/libraries/${name}.md", List(DocMaker.make2(InstructionList(tokenized.map(_.get)))))
  }
}