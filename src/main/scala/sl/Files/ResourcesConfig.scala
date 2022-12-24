package sl.files

import java.nio.file.FileSystems
import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.LinkOption
import java.nio.file.Paths
import scala.collection.JavaConverters._

class ConfigFiles (val basePath: String) {
  lazy val jarFileSystem: FileSystem = FileSystems.newFileSystem(getClass.getResource(basePath).toURI, Map[String, String]().asJava);

  def listPathsFromResource(folder: String): List[Path] = {
    Files.list(getPathForResource(folder))
      .filter(p ⇒ Files.isRegularFile(p, Array[LinkOption](): _*))
      .sorted.toList().asScala.toList // from Stream to java List to Scala Buffer to scala List
  }

  private def getPathForResource(filename: String) = {
    val url = classOf[ConfigFiles].getResource(basePath + "/" + filename)
    if ("file" == url.getProtocol) Paths.get(url.toURI)
    else jarFileSystem.getPath(basePath, filename)
  }
}