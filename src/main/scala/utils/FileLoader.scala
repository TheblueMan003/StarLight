package utils

import scala.io.Source
import utils.CharBufferIterator

object FileLoader{
    /**
      * Loads a file from the given path in the resources folder.
      *
      * @param path: The path of the file to load.
      * @return StringBufferedIterator of the file.
      */
    def load(path: String): SFile = {
        val cpath = path.replace(".","/")
        val ipath = path.replace("/",".").replace("\\",".")
        SFile(ipath, Source.fromResource(cpath+".sl").getLines.reduce((x,y) => x + "\n" +y))
    }
}