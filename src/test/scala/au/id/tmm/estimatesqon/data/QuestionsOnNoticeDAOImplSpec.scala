package au.id.tmm.estimatesqon.data

import java.nio.file.{Files, Paths}
import java.sql.SQLException

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.controller.{EstimatesScraper, TestResources}
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, AnswerUpdateBundle, ExampleEstimates}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.FileUtils
import slick.jdbc.meta.MTable

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class QuestionsOnNoticeDAOImplSpec extends StandardProjectSpec {

  private val config: Config = ConfigFactory.load()

  val workingDir = Paths.get(config.getString("testWorkingDir"))

  val dbPath = Paths.get(config.getString("testDB.path"))

  val dao = QuestionsOnNoticeDAOImpl.forConfigName("testDB")

  def initialiseNewDb(): Unit = {
    cleanWorkingDir()

    Await.result(dao.initialiseIfNeeded(), 30.seconds)
  }

  def cleanWorkingDir() = {
    if (Files.exists(workingDir)) {
      FileUtils.cleanDirectory(workingDir.toFile)
    } else {
      Files.createDirectories(workingDir)
    }
  }

  "A newly initialised DAO" should "create a database file if none exists" in {
    Given("no database file exists")
    cleanWorkingDir()

    When("the DAO is initialised")
    initialiseNewDb()

    Then("a new database file is created")
    assert(Files.isRegularFile(dbPath), dbPath)
  }

  it should "create the empty tables" in {
    Given("no database exists")

    When("the DAO is initialised")
    initialiseNewDb()

    Then("the expected tables are created")
    val listTables: Future[Vector[MTable]] = dao.database.run(MTable.getTables)
    val tableMetadata: Vector[MTable] = Await.result(listTables, 1.second)
    val actualTableNames: Set[String] = tableMetadata
      .map(_.name.name)
      .toSet
      .filterNot(_ == "sqlite_sequence")

    val expectedTableNames: Set[String] = Set("Answers", "PDFLinkBundles", "Estimates")

    assert(actualTableNames == expectedTableNames)
  }

  it should "have not queried any Estimates" in {
    Given("a freshly initialised database")
    initialiseNewDb()

    Then("the DAO has recorded no page queries")

    val haveQueriedTheEstimates = Await.result(dao.haveEverQueried(ExampleEstimates.AG_2015_BUDGET), 30.seconds)
    assert(!haveQueriedTheEstimates)
  }

  it should "have no registered Estimates" in {
    Given("a freshly initialised database")
    initialiseNewDb()

    Then("the DAO has no registered Estimates")
    val estimates = Await.result(dao.listEstimates, 30.seconds)

    assert(estimates.isEmpty)
  }

  "The DAO" should "record an Estimates" in {
    Given("a freshly initialised database")
    initialiseNewDb()

    When("an estimates is recorded")
    Await.result(dao.registerEstimates(ExampleEstimates.COMMUNICATIONS_2015_BUDGET), 30.seconds)

    Then("that estimates should be listed by the DAO on request")
    val estimates = Await.result(dao.listEstimates, 30.seconds)

    assert(estimates == Set(ExampleEstimates.COMMUNICATIONS_2015_BUDGET))
  }

  it should "forbid the registration of duplicate estimates" in {
    Given("a freshly initialised database")
    initialiseNewDb()

    And("an estimates is added")
    Await.result(dao.registerEstimates(ExampleEstimates.COMMUNICATIONS_2015_BUDGET), 30.seconds)

    When("a duplicate estimates is added")
    Then("an exception occurs")
    intercept[SQLException] {
      Await.result(dao.registerEstimates(ExampleEstimates.COMMUNICATIONS_2015_BUDGET), 30.seconds)
    }
  }

  it should "correctly write an answer update bundle" in {
    Given("a freshly initialised database")
    initialiseNewDb()

    And("a registered Estimates")
    val estimates = ExampleEstimates.COMMUNICATIONS_2015_BUDGET
      .cloneWithUrl(TestResources.communications20152016BudgetEstimates)

    Await.result(dao.registerEstimates(estimates), 30.seconds)

    When("an answer update bundle is written")
    val answers: Set[Answer] = EstimatesScraper.forEstimates(estimates).extractAnswers.toSet
    val updates: Set[AnswerUpdate] = answers.map(AnswerUpdate.forExistingAnswer)
    val updateBundle: AnswerUpdateBundle = AnswerUpdateBundle.fromUpdates(updates, estimates)
    Await.result(dao.writeUpdateBundle(updateBundle), 30.seconds)

    Then("The written answers have the correct details")
    val storedAnswers: Set[Answer] = Await.result(dao.retrieveLatestAnswersFor(estimates), 30.seconds)

    assert(answers === storedAnswers)
  }
}
