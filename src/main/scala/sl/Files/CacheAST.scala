package sl.files

import java.io._
import sl._

object CacheAST{
    val map = scala.collection.mutable.Map[String, CacheLine]()
    def contains(file: String)={
        if (new File(file).exists()){
            map.contains(file) && new File(map(file).file).exists && new File(file).lastModified() <= map(file).lastWriteTime
        }
        else{
            map.contains(file) && new File(map(file).file).exists
        }
    }
    def get(file: String)={
        val filename = map(file).file
        val ois = new ObjectInputStream(new FileInputStream(filename))
        val obj = ois.readObject.asInstanceOf[Instruction]
        ois.close
        obj
    }
    def add(file: String, instr: Instruction)={
        val filename = "./bin/" + file.replace(".sl", "")+".slbin"
        FileUtils.createFolderForFiles(new File(filename))
        val oos = new ObjectOutputStream(new FileOutputStream(filename))
        oos.writeObject(instr)
        oos.close
        map(file) = CacheLine(new File(file).lastModified(), filename)
    }
}
case class CacheLine(lastWriteTime: Long, file: String)