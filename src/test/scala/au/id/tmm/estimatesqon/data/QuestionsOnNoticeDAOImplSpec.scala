package au.id.tmm.estimatesqon.data

import java.nio.file.{Files, Paths}

import au.id.tmm.estimatesqon.StandardProjectSpec
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.FileUtils
import slick.jdbc.meta.MTable

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class QuestionsOnNoticeDAOImplSpec extends StandardProjectSpec {

  private val config: Config = ConfigFactory.load()

  val workingDir = Paths.get(config.getString("testWorkingDir"))

  val dbPath = Paths.get(config.getString("testDB.path"))

  val dao = QuestionsOnNoticeDAOImpl.forConfigName("testDB")

  def initialiseNewDb(): Unit = {
    Given("the working directory is clean")
    cleanWorkingDir()

    When("the dao is initialised")
    Await.result(dao.initialiseIfNeeded(), 30.seconds)
  }

  def cleanWorkingDir() = {
    if (Files.exists(workingDir)) {
      FileUtils.cleanDirectory(workingDir.toFile)
    } else {
      Files.createDirectories(workingDir)
    }
  }

  "The DAO" should "create a database file if none exists" in {
    initialiseNewDb()

    Then("a new database file is created")
    assert(Files.isRegularFile(dbPath), dbPath)
  }

  it should "create the empty tables on DB initialisation" in {
    initialiseNewDb()

    Then("the tables are created")
    val listTables: Future[Vector[MTable]] = dao.database.run(MTable.getTables)
    val tableMetadata: Vector[MTable] = Await.result(listTables, 1.second)
    val actualTableNames: Set[String] = tableMetadata
      .map(_.name.name)
      .toSet
      .filterNot(_ == "sqlite_sequence")

    val expectedTableNames: Set[String] = Set("PageQueries", "Answers", "PDFLinkBundles", "Estimates")

    assert(actualTableNames == expectedTableNames)
  }

}
