import org.scalatest.*
import matchers.*
import flatspec.*
import org.scalatest.BeforeAndAfterEach
import scala.sys.process._
import java.nio.file.Paths
import java.time.LocalDateTime
import java.io.File

import scala.collection.mutable
import java.io.BufferedInputStream
import java.time.temporal.ChronoUnit
import java.io.PrintStream
import sl.files.ConfigFiles
import sl.files.FileUtils

class ListFunSuite extends AnyFlatSpec with should.Matchers with BeforeAndAfterAll {
    var server: Process = null
    var pio: ProcessIO = null

    var chat: mutable.ListBuffer[String] = null

    var output: PrintStream = null

    val testFiles: List[(String, Int)] = List()
    val compileTests: List[String] = List()//FileUtils.getListOfFiles("./src/main/resources/libraries").filterNot(_.contains("__init__.sl")) ::: FileUtils.getListOfFiles("./src/test/resources/").filterNot(_.contains("__init__.sl"))


    override def beforeAll() = {
        if (testFiles.length > 0){
            chat = mutable.ListBuffer[String]()
            pio = new ProcessIO(stdin => (output = PrintStream(stdin, true)),
                            stdout => scala.io.Source.fromInputStream(stdout)
                            .getLines.foreach(line => {println(line);chat.synchronized{chat.append(line)}}),
                            stderr => scala.io.Source.fromInputStream(stderr)
                            .getLines.foreach(line => {println(line)}))
            server = Process("java -Xmx1024M -Xms1024M -jar server.jar nogui", new java.io.File("./test_env")).run(pio)
        }
    }
    override def afterAll() = {
        if (testFiles.length > 0){
            server.destroy()
        }
    }

    def checkForText(text: String):Boolean={
        chat.synchronized{
            chat.exists(s => s.contains(text))
        }
    }
    def clearChat()={
        chat.synchronized{
            chat.clear()
        }
    }
    def checkForCount(text: String):Int={
        chat.synchronized{
            chat.count(s => s.contains(text))
        }
    }

    def deleteDirectory(directoryToBeDeleted: File):Boolean= {
        val allContents = directoryToBeDeleted.listFiles()
        if (allContents != null) {
            allContents.foreach(f => deleteDirectory(f))
        }
        directoryToBeDeleted.delete()
    }

    if (testFiles.length > 0){
        "server" should "have started" in {
            assert(server.isAlive())
        }
    }

    if (testFiles.length > 0){
        "server" should "load world" in {
            val start =  LocalDateTime.now()
            var end =  LocalDateTime.now()
            while(!checkForText("Done") && ChronoUnit.SECONDS.between(start, end) < 100){
                end = LocalDateTime.now()
            }
            assert(checkForText("Done"))
            clearChat()
        }
    }

    compileTests.foreach(f => {
        deleteDirectory(new File("./test_env/world/datapacks/test"))
        f should "compile" in {
            FileUtils.deleteDirectory("./bin")
            sl.Main.main(Array("compile", "-i", f, "-o", "./test_env/world/datapacks/test/"))
        }
    })

    testFiles.foreach((f, n) => {
        deleteDirectory(new File("./test_env/world/datapacks/test"))
        f should "compile" in {
            FileUtils.deleteDirectory("./bin")
            sl.Main.main(Array("compile", "-i", "./src/test/resources/"+f, "-o", "./test_env/world/datapacks/test/"))
            output.println("/scoreboard players set __pass__ tbms.var 0\n")
            output.println("/reload\n")
        }
        f should "pass" in {
            var start =  LocalDateTime.now()
            var end =  LocalDateTime.now()
            while(ChronoUnit.SECONDS.between(start, end) < 2){
                end = LocalDateTime.now()
            }
            output.println("/scoreboard players get __pass__ tbms.var\n")
            start =  LocalDateTime.now()
            end =  LocalDateTime.now()
            while(ChronoUnit.SECONDS.between(start, end) < 1){
                end = LocalDateTime.now()
            }
            assert(checkForText(f"__pass__ has $n [tbms.var]"))
            clearChat()
        }
    })
}