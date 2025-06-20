package sl.files

import sl.files.FileUtils
import java.io.PrintWriter
import java.util.zip.ZipOutputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import scala.collection.parallel.CollectionConverters._
import scala.collection.mutable
import sl.Reporter
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.io.{ BufferedInputStream, FileInputStream, FileOutputStream }
import java.util.zip.{ ZipEntry, ZipOutputStream }
import java.io.File
import sl.IR.*
import sl.Settings

object DataPackBuilder{
    var previous = mutable.Map[String, Map[String, List[IRTree]]]()
    def clearCache() = previous = mutable.Map[String, Map[String, List[IRTree]]]()
    def build(build: String, source: List[String], dirs: List[String], output: List[IRFile]):Unit={
        if (!previous.contains(build)){
            Reporter.info("Clearing old Data Packs")
            dirs.foreach(FileUtils.deleteDirectory(_))
        }

        val newSet = dirs.flatMap(dir => output.map(file => (dir + file.getPath(), file.getContents()))).toMap
        previous.getOrElse(build, Map[String, List[IRTree]]()).filter(x => !newSet.contains(x._1)).foreach(x => {
            //Reporter.debug(f"Removing old file: ${x._1}")
            FileUtils.deleteFile(x._1)
        }
        )
        
        dirs.foreach(target => {
            if (Settings.consoleInfoExportPath){Reporter.ok(f"Building Data Pack: $target")}
            if (target.endsWith(".zip/")){
                makeDPZip(source, target.replaceAllLiterally(".zip/",".zip"), output)
            }
            else if (target.endsWith(".mcpack/")){
                makeDPZip(source, target.replaceAllLiterally(".mcpack/",".mcpack"), output)
            }
            else{
                makeDPFolder(build, source, target, output)
            }
            Reporter.ok(f"Resource Data build")
        })

        previous(build) = newSet
    }

    def exportOutputZip(out: String, files: List[IRFile]) = {
        val zip = new ZipOutputStream(new FileOutputStream(out))
        val writer = new PrintWriter(zip)
        files.foreach { file =>
            val name = file.getPath()
            val content = file.getFinalContents()
            zip.putNextEntry(new ZipEntry(if (name.startsWith("/")) name.drop(1) else name))
            content.foreach(x => writer.println(x.getString()))
            writer.flush()
            zip.closeEntry()
        }
        zip.close()
    }

    def makeDPFolder(build: String, sources: List[String], target: String, generated: List[IRFile])={
        sources.flatMap(source => getListOfDPFiles(source, "").map(f => (source, f)))
        .groupBy(_._2)
        .toList
        .map((k,v) => v.sortBy(_._1.length()).head)
        .map((source, file) =>{
            val copied = Paths.get(target+file);
            val originalPath = Paths.get(source+"/"+file)
            FileUtils.createFolderForFiles(File(target+file))
            Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
        })
        generated.map{case irfile => {
            val content = irfile.getFinalContents()
            val file = irfile.getPath()
            val filename = target+file
            if (!previous.getOrElse(build, Map[String, List[IRTree]]()).contains(filename) || previous.getOrElse(build, Map[String, List[IRTree]]())(filename) != content){
                FileUtils.safeWriteFile(target+file, content.map(_.getString()))
            }
        }}
    }
    def makeDPZip(sources: List[String], target: String, generated: List[IRFile])={
        val Buffer = 2 * 1024
        var data = new Array[Byte](Buffer)
        val zip = new ZipOutputStream(new FileOutputStream(target))
        val writer = new PrintWriter(zip)

        // Copy Files
        sources.flatMap(source => getListOfDPFiles(source, "").map(f => (source, f)))
        .groupBy(_._2)
        .filterNot(x => generated.exists(_.getPath() == x._1))
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

        // Add Generated Files
        generated.groupBy(_.getPath())
        .toList
        .map((k,v) => v.sortBy(_.getFinalContents().length).head)
        .foreach { file =>
            val content = file.getFinalContents()
            val name = file.getPath()
            zip.putNextEntry(new ZipEntry(if (name.startsWith("/")) name.drop(1) else name))
            content.foreach(x => writer.println(x.getString()))
            writer.flush()
            zip.closeEntry()
        }

        zip.close()
    }

    def getListOfDPFiles(dir: String, prefix: String):List[String] = {
        val d = new File(dir)
        if (d.exists && d.isDirectory) {
            d.listFiles.filter(_.isDirectory()).flatMap(f => getListOfDPFiles(f.getPath(), prefix+f.getName()+"/")).toList:::
            d.listFiles.filter(_.isFile).map(prefix+_.getName()).toList
        } else if (d.isFile()) {
            List[String](prefix+d.getName())
        }
        else{
            List()
        }
    }
}