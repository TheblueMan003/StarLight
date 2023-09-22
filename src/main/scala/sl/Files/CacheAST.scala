package sl.files

import java.io._
import sl._

object CacheAST{
    val map = scala.collection.mutable.Map[String, CacheLine]()
    def contains(file2: String)={
        val file = normalize(file2)
        if (!map.contains(file)) false
        else if (File(file+".sl").exists() && File(file+".sl").lastModified() == map(file).time) true
        else if (File("src/"+file+".sl").exists() && File("src/"+file+".sl").lastModified() == map(file).time) true
        else if (file2 == "__init__") true
        else false
    }
    def get(file2: String)={
        val file = normalize(file2)
        map(file).file
    }
    def add(file2: String, instr: Instruction)={
        val file = normalize(file2)
        if (File(file+".sl").exists()){
            map(file) = CacheLine(File(file+".sl").lastModified(), instr)
        }
        else if (File("src/"+file+".sl").exists()){
            map(file) = CacheLine(File("src/"+file+".sl").lastModified(), instr)
        }
        else{
            map(file) = CacheLine(0, instr)
        }
    }
    def normalize(name: String):String={
        if name.endsWith(".sl") then normalize(name.substring(0, name.size - 3))
        else if name.startsWith("./") || name.startsWith(".\\") then normalize(name.substring(2))
        else name.replace(".", "/").replace("\\", "/")
    }
}
case class CacheLine(time: Long, file: Instruction)