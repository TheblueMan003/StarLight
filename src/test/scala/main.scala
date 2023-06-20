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
import sl.Settings

class ListFunSuite extends AnyFlatSpec with should.Matchers with BeforeAndAfterAll {
    val testFiles: List[String] = FileUtils.getListOfFiles("./src/test/resources/").filterNot(_.contains("__init__.sl"))
    val compileTests: List[String] = FileUtils.getListOfFiles("./src/main/resources/libraries").filterNot(_.contains("__init__.sl")) ::: FileUtils.getListOfFiles("./src/test/resources/").filterNot(_.contains("__init__.sl"))


    def deleteDirectory(directoryToBeDeleted: File):Boolean= {
        val allContents = directoryToBeDeleted.listFiles()
        if (allContents != null) {
            allContents.foreach(f => deleteDirectory(f))
        }
        directoryToBeDeleted.delete()
    }


    compileTests.foreach(f => {
        deleteDirectory(new File("./test_env/world/datapacks/test"))
        f should "compile" in {
            FileUtils.deleteDirectory("./bin")
            sl.Main.main(Array("compile", "-i", f, "-o", "./test_env/world/datapacks/test/"))
        }
    })

    testFiles.foreach(f => {
        f should "pass without optimization" in {
            Settings.optimize = false
            FileUtils.deleteDirectory("./bin")
            sl.Reporter.errorThrow = true
            sl.Main.main(Array("testScala", "-i", f, "-o", "./test_env/world/datapacks/test/"))
        }
    })
    testFiles.foreach(f => {
        f should "pass with optimization" in {
            Settings.optimize = true
            Settings.optimizeInlining = true
            Settings.optimizeDeduplication = true
            Settings.optimizeVariableValue = true
            Settings.optimizeVariableGlobal = true
            Settings.optimizeVariableLocal = true
            FileUtils.deleteDirectory("./bin")
            sl.Reporter.errorThrow = true
            sl.Main.main(Array("testScala", "-i", f, "-o", "./test_env/world/datapacks/test/"))
        }
    })
}