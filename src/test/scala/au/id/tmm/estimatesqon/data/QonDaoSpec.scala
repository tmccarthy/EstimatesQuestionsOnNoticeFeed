package au.id.tmm.estimatesqon.data

import java.nio.file.{Files, Paths}
import java.sql.SQLException

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.controller.TestResources
import au.id.tmm.estimatesqon.controller.scraping.EstimatesScraper
import au.id.tmm.estimatesqon.model.{AnswerUpdate, AnswerUpdateBundle, ExampleEstimates}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.io.FileUtils
import slick.jdbc.meta.MTable

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class QonDaoSpec extends StandardProjectSpec {

  private val config: Config = ConfigFactory.load()

  val workingDir = Paths.get(config.getString("testWorkingDir"))

  val dbPath = Paths.get(config.getString("testDB.path"))

  val dao = QonDao.forConfigName("testDB")

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
    val answers = EstimatesScraper().scrapeFrom(estimates).toSet
    val updates = answers.map(AnswerUpdate.forExistingAnswer)
    val updateBundle = AnswerUpdateBundle.fromUpdates(updates, estimates)
    Await.result(dao.writeUpdateBundle(updateBundle), 30.seconds)

    Then("The written answers have the correct details")
    val storedAnswers = Await.result(dao.retrieveLatestAnswersFor(estimates), 30.seconds)

    val missingAnswers = answers diff storedAnswers
    val extraAnswers = storedAnswers diff missingAnswers

    assert(answers === storedAnswers, s"The stored answers are missing $missingAnswers and have the following extra " +
      s"answers $extraAnswers")
  }

  it should "reject an answer update bundle for an unregistered Estimates" in {
    Given("a freshly initialised database")
    initialiseNewDb()

    And("an unregistered Estimates")
    val estimates = ExampleEstimates.COMMUNICATIONS_2015_BUDGET
      .cloneWithUrl(TestResources.communications20152016BudgetEstimates)

    When("an answer update bundle is written")
    val answers = EstimatesScraper().scrapeFrom(estimates).toSet
    val updates = answers.map(AnswerUpdate.forExistingAnswer)
    val updateBundle = AnswerUpdateBundle.fromUpdates(updates, estimates)

    Then("the write should throw")
    val interceptedException = intercept[UnregisteredEstimatesException] {
      Await.result(dao.writeUpdateBundle(updateBundle), 30.seconds)
    }

    assert(interceptedException.estimates === estimates)
  }

  it should "return an empty list if the most recent answers of an unregistered Estimates are requested" in {
    Given("a freshly initialised database")
    initialiseNewDb()

    And("an unregistered Estimates")
    val estimates = ExampleEstimates.COMMUNICATIONS_2015_BUDGET
      .cloneWithUrl(TestResources.communications20152016BudgetEstimates)

    When("the most recent answers for that estimates are requested")
    val answers = Await.result(dao.retrieveLatestAnswersFor(estimates), 30.seconds)

    Then("an empty set is returned")
    assert(answers.isEmpty)
  }
}
