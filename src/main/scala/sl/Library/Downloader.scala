package sl.Library

import sl.files.FileUtils
import sl.Parser
import sl.Reporter
import java.io.File
import sl.Utils
import scala.util.Random
import java.io._
import java.net.URL
import java.nio.file.{Files, Paths}
import sl.JSONElement
import sl.JsonDictionary

object Downloader{
    val urlbases = List(("https://raw.githubusercontent.com/TheblueMan003/StarLightLibraries/main/published/", "github.com"), ("https://theblueman003.com/StarLightLibraries/published", "theblueman.com"))
    var index = 0
    var hasDownloadedIndex = false

    def downloadFile(url: String, destination: String): Unit = {
        val connection = new URL(url).openConnection()
        val inputStream = connection.getInputStream
        val outputStream = new FileOutputStream(destination)

        try {
        val buffer = new Array[Byte](4096)
        var bytesRead = 0

        while (bytesRead != -1) {
            bytesRead = inputStream.read(buffer)
            if (bytesRead != -1)
            outputStream.write(buffer, 0, bytesRead)
        }
        } finally {
        inputStream.close()
        outputStream.close()
        }
    }
    def download(fileToDownload: String, location: String) = {
        if (fileToDownload.endsWith(".zip")){
            val tmp = f"tmp_${Random.between(0,1000000)}}.zip"
            downloadFile(fileToDownload, tmp)
            FileUtils.unzip(tmp, location)
            FileUtils.deleteFile(tmp)
        }
        else{
            val src = scala.io.Source.fromURL(fileToDownload)
            FileUtils.safeWriteFile(location, src.getLines.toList)
        }
    }
    def clearCache()={
        hasDownloadedIndex = false
    }
    private def fetchLibrary(urlbase: String, servername: String, json: JsonDictionary, name: String)={
        val version = json(name).getArray(-1)
        val url = version.getDictionary("url").getStringValue
        val filename = version.getDictionary("filename").getStringValue
        val versionNumber = version.getDictionary("version").getStringValue
        
        Reporter.warning("Downloading "+name+" "+versionNumber+" from "+servername)
        download(urlbase+url, "libraries/"+filename)

        if (version.getDictionary.contains("resourcespack")){
            version.getDictionary("resourcespack").getArray.content.foreach(resource => {
                val url = resource.getDictionary("url").getStringValue
                val filename = resource.getDictionary("filename").getStringValue
                val versionNumber = resource.getDictionary("version").getStringValue
                val game = resource.getDictionary("game").getStringValue
                
                Reporter.warning("Downloading resources pack"+name+" "+versionNumber+" from "+servername)
                if (game == "java"){
                    download(urlbase+url, "lib/"+filename+"/java_resourcepack")
                }
                else if (game == "bedrock"){
                    download(urlbase+url, "lib/"+filename+"/bedrock_resourcepack")
                }
                else{
                    Reporter.error("Unknown game "+game)
                }
            })
        }
    }
    def fetchLibrary(name: String):Unit={
        urlbases.find{case (urlbase, servername) => {
            if (!hasDownloadedIndex){
                download(urlbase+"index.json", "libraries/index.json")
                hasDownloadedIndex = true
            }
            val json = Parser.parseJson(Utils.getFile("libraries/index.json").trim()).getDictionary("libraries").getDictionary
            if (json.contains(name)){
                fetchLibrary(urlbase, servername, json, name)
                true
            }
            else if (name.endsWith("._")){
                val pref = name.substring(0, name.size-1)
                var found = false
                json.map.filter(x => x._1.startsWith(pref)).foreach{case (name, version) => {
                    fetchLibrary(urlbase, servername, json, name)
                    found = true
                }}
                found
            }
            else{
                false
            }
        }}
    }
    def installLib(name2: String, libversion: String):Unit={
        val name = name2.toLowerCase()
        urlbases.find{case (urlbase, servername) => {
            download(urlbase+"index.json", "libraries/index.json")
            val json = Parser.parseJson(Utils.getFile("libraries/index.json").trim()).getDictionary("libraries").getDictionary
            if (json.contains(name)){
                val version = json(name).getArray.content.find(_.getDictionary("version").getStringValue == libversion).get
                val url = version.getDictionary("url").getStringValue
                val filename = version.getDictionary("filename").getStringValue
                val versionNumber = version.getDictionary("version").getStringValue
                
                Reporter.warning("Downloading "+name+" "+versionNumber+" from "+servername)
                download(urlbase+url, "libraries/"+filename)
                true
            }
            else{
                false
            }
        }}
    }
    def updateLib(name2: String)={
        val name = name2.toLowerCase()
        fetchLibrary(name)
    }
    def getLibrary(name2: String)={
        val name = name2.toLowerCase()
        if (new File("libraries/"+name.replace(".","/")+".sl").exists()){
            Utils.getFile("libraries/"+name.replace(".","/")+".sl")
        }
        else{
            fetchLibrary(name)
            if (name.endsWith("._")){
                File("libraries/"+name.substring(0, name.size - 1).replace(".","/")).listFiles().map(file => {
                    if (file.getName.endsWith(".sl")){
                        Utils.getFile(file.getAbsoluteFile().toString())
                    }
                }).mkString("\n")
            }
            else{
                Utils.getFile("libraries/"+name.replace(".","/")+".sl")
            }
        }
    }
}