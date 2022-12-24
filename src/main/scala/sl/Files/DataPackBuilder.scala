package sl.files

import sl.files.FileUtils
import java.io.PrintWriter
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry

object DataPackBuilder{
    def build(dirs: List[String], output: List[(String, List[String])]):Unit={
        dirs.foreach(FileUtils.deleteDirectory(_))
        dirs.foreach(dir => {
        if (dir.endsWith(".zip/")){
            exportOutputZip(dir.replaceAllLiterally(".zip/",".zip"), output.distinct)
        }
        else if (dir.endsWith(".mcpack/")){
            exportOutputZip(dir.replaceAllLiterally(".mcpack/",".mcpack"), output.distinct)
        }
        else{
            output.foreach((path, content) =>{
            val filename = dir + path
            FileUtils.safeWriteFile(filename, content)
            })
        }})
    }

    def exportOutputZip(out: String, files: List[(String, List[String])]) = {
        val zip = new ZipOutputStream(new FileOutputStream(out))
        val writer = new PrintWriter(zip)
        files.foreach { (name, content) =>
        zip.putNextEntry(new ZipEntry(if name.startsWith("/") then name.drop(1) else name))
        content.foreach(x => writer.println(x))
        writer.flush()
        zip.closeEntry()
        }
        zip.close()
    }
}