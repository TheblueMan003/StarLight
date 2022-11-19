package fos

import java.io.File
import java.io._

object Main{
  def main(args: Array[String]): Unit = {
    var files = getFiles(sourceFromArg(args, "-i"))
    var tokenized = files.map((f, c) => Parser.parse(f, c))
    if (tokenized.contains(None)) return;
    val context = ContextBuilder.build(getArg(args, "-n"), InstructionList(tokenized.map(_.get)))
    var output = Compiler.compile(context)
    exportOutput(getArg(args, "-o"), output)
  }

  /**
   * Export Output to directory
   */
  def exportOutput(dir: String, output: List[(String, List[String])]):Unit={
    output.foreach((path, content) =>{
      val filename = dir + path
      val file = new File(filename)
      val directory = new File(file.getParent())

      // Create Directory
      if (!directory.exists()){
         directory.mkdirs()
      }

      // Write all files
      val bw = new BufferedWriter(new FileWriter(file))
      content.foreach(line => {
        bw.write(line);
        bw.write("\n")}
      )
      bw.close()
    })
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
    paths.flatMap(path => getListOfFiles(path)).map(p => (p,getFile(p)))
  }
  def getFile(path: String): String = {
    val source = scala.io.Source.fromFile(path)
    source.getLines mkString "\n"
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