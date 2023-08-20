package sl.files

import java.io.File
import java.io.PrintWriter
import sl.Utils
import javax.sound.sampled.AudioSystem
import java.io.{FileInputStream, FileOutputStream}
import java.util.zip.ZipInputStream
import scala.io.BufferedSource

object FileUtils{
    def deleteDirectory(dir: String): Boolean = deleteDirectory(new File(dir))
    def deleteDirectory(directoryToBeDeleted: File):Boolean= {
        val allContents = directoryToBeDeleted.listFiles()
        if (allContents != null) {
            allContents.foreach(f => deleteDirectory(f))
        }
        directoryToBeDeleted.delete()
    }
    def deleteFile(file: String): Boolean = deleteFile(new File(file))
    def deleteFile(file: File):Boolean= {
        file.delete()
    }
    def getListOfTopDirectories(dir: String):List[String] = {
        val d = new File(dir)
        if (d.exists && d.isDirectory) {
            d.listFiles.filter(_.isDirectory()).map(_.getName()).toList
        } else {
            List[String]()
        }
    }
    def listSubdir(dir: String):List[String] = listSubdir(File(dir))
    def listSubdir(dir: File):List[String] = {
        if (!dir.exists || !dir.isDirectory) return List()
        dir.listFiles().filter(_.isDirectory()).map(_.getName()).toList
    }
    def getFiles(paths: List[String]): List[String] = {
        paths.flatMap(path => getListOfFiles(path))
    }
    def getListOfFiles(dir: String):List[String] = {
        val d = new File(dir)
        if (d.exists && d.isDirectory) {
            d.listFiles.filter(_.isDirectory()).flatMap(f => getListOfFiles(f.getPath())).toList:::
            d.listFiles.filter(_.isFile).map(_.getPath()).toList
        } else if (d.exists && d.isFile()) {
            List[String](d.getPath())
        }
        else{
            List()
        }
    }
    def createFolderForFiles(file: File)={
        try{
            val directory = new File(file.getParent())

            // Create Directory
            if (!directory.exists()){
                directory.mkdirs()
            }
        }catch{
            case e: Exception => {}
        }
    }
    def createDirectory(filename: String):Unit = {
        val directory = new File(filename)

        // Create Directory
        if (!directory.exists()){
            directory.mkdirs()
        }
    }

    def safeWriteFile(filename: String, content: List[String]):Unit = {
        val file = new File(filename)
        createFolderForFiles(file)

        // Write all files
        val out = new PrintWriter(file, "UTF-8")
        content.foreach(out.println(_))
        out.close()
    }

    /*
    * Return the duration of the audio file in seconds
    */
    def getAudioFileDuration(file: File):Double={
        val audioInputStream = AudioSystem.getAudioInputStream(file)
        val format = audioInputStream.getFormat()
        val frames = audioInputStream.getFrameLength()
        return (frames+0.0) / format.getFrameRate()
    }

    def copyFromResourcesToFolder(resource: String, target: String):Unit={
        val file = new File(target)
        if (!file.exists()){
            val directory = new File(file.getParent())

            // Create Directory
            if (!directory.exists()){
                directory.mkdirs()
            }
            
            val in = getClass().getResourceAsStream("/" + resource)
            val out = new java.io.FileOutputStream(file)
            val buffer = new Array[Byte](1024)
            var read = in.read(buffer)
            while (read != -1) {
                out.write(buffer, 0, read)
                read = in.read(buffer)
            }
            in.close()
            out.close()
        }
    }
    def unzip(zipFile: String, destination: String): Unit = {
        val buffer = new Array[Byte](1024)
        val zipInputStream = new ZipInputStream(new FileInputStream(zipFile))
        var zipEntry = zipInputStream.getNextEntry

        while (zipEntry != null) {
        val newFile = new File(destination, zipEntry.getName)
        if (zipEntry.isDirectory)
            newFile.mkdirs()
        else {
            createFolderForFiles(newFile)
            val outputStream = new FileOutputStream(newFile)

            try {
            var len = zipInputStream.read(buffer)
            while (len > 0) {
                outputStream.write(buffer, 0, len)
                len = zipInputStream.read(buffer)
            }
            } finally {
            outputStream.close()
            }
        }
        zipEntry = zipInputStream.getNextEntry
        }
        zipInputStream.closeEntry()
        zipInputStream.close()
    }
}