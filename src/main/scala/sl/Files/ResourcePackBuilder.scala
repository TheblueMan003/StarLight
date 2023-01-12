package sl.files

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.io.{ BufferedInputStream, FileInputStream, FileOutputStream }
import java.util.zip.{ ZipEntry, ZipOutputStream }
import java.io.PrintWriter
import java.io.File
import sl.Reporter

object ResourcePackBuilder{
    def build(source: List[String], target: String, jsonFiles: List[objects.JSONFile])={
        Reporter.ok(f"Building Resource Pack: $target")
        if (target.endsWith(".zip/")){
            makeRPZip(source, target.replaceAllLiterally(".zip/",".zip"), jsonFiles)
        }
        else if (target.endsWith(".mcpack/")){
            makeRPZip(source, target.replaceAllLiterally(".mcpack/",".mcpack"), jsonFiles)
        }
        else{
            makeRPFolder(source, target, jsonFiles)
        }
        Reporter.ok(f"Resource Pack build")
    }
    def makeRPFolder(sources: List[String], target: String, jsonFiles: List[objects.JSONFile])={
        FileUtils.deleteDirectory(target)
        sources.flatMap(source => getListOfRPFiles(source, "").map(f => (source, f)))
        .groupBy(_._2)
        .toList
        .map((k,v) => v.sortBy(_._1.length()).head)
        .map((source, file) =>{
            val copied = Paths.get(target+file);
            val originalPath = Paths.get(source+"/"+file)
            FileUtils.createFolderForFiles(File(target+file))
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        })
        jsonFiles.map(file => {
            FileUtils.safeWriteFile(target+file.getName(), file.getContent())
        })
    }
    def makeRPZip(sources: List[String], target: String, jsonFiles: List[objects.JSONFile])={
        val Buffer = 2 * 1024
        var data = new Array[Byte](Buffer)
        val zip = new ZipOutputStream(new FileOutputStream(target))
        val writer = new PrintWriter(zip)

        // Copy Files
        sources.flatMap(source => getListOfRPFiles(source, "").map(f => (source, f)))
        .groupBy(_._2)
        .toList
        .map((k,v) => v.sortBy(_._1.length()).head)
        .map((source, name) =>{
            zip.putNextEntry(new ZipEntry(name))
            val in = new BufferedInputStream(new FileInputStream(source+"/"+name), Buffer)
            var b = in.read(data, 0, Buffer)
            while (b != -1) {
                zip.write(data, 0, b)
                b = in.read(data, 0, Buffer)
            }
            in.close()
            zip.closeEntry()
        })

        // Add JSON FILES
        jsonFiles.map(f => (f.getName(), f.getContent())).foreach { (name, content) =>
            zip.putNextEntry(new ZipEntry(if name.startsWith("/") then name.drop(1) else name))
            content.foreach(x => writer.println(x))
            writer.flush()
            zip.closeEntry()
        }

        zip.close()
    }
    def getListOfRPFiles(dir: String, prefix: String):List[String] = {
        val d = new File(dir)
        if (d.exists && d.isDirectory) {
            d.listFiles.filter(_.isDirectory()).flatMap(f => getListOfRPFiles(f.getPath(), prefix+f.getName()+"/")).toList:::
            d.listFiles.filter(_.isFile).map(prefix+_.getName()).toList
        } else if (d.isFile()) {
            List[String](prefix+d.getName())
        }
        else{
            List()
        }
    }
}