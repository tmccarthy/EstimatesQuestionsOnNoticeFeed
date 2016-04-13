package au.id.tmm.estimatesqon.controller

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.controller.scraping.EstimatesScraper
import au.id.tmm.estimatesqon.data.MockableQonDao
import au.id.tmm.estimatesqon.model.{AnswerUpdate, AnswerUpdatesForTesting, ExampleEstimates}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class UpdatesReaderSpec extends StandardProjectSpec with MockFactory {

  private val dao = mock[MockableQonDao]

  private val scraper = mock[EstimatesScraper]

  private val updatesReader = UpdatesReader.using(scraper, dao)

  behaviour of "the UpdatesReader when reading updates for one estimates"

  it should "handle the case where there old answers" in {
    val estimates = ExampleEstimates.COMMUNICATIONS_2015_BUDGET

    val oldAnswer = AnswerUpdatesForTesting.defaultAnswer()
    val newAnswer = AnswerUpdatesForTesting.defaultAnswer(senator = Some("Senator"))

    (dao.haveEverQueried _).expects(estimates).returning(Future.successful(true))
    (dao.retrieveLatestAnswersFor _).expects(estimates).returning(Future.successful(Set(oldAnswer)))
    (scraper.scrapeFrom _).expects(estimates).returning(List(newAnswer))

    val actualUpdates = Await.result(updatesReader.latestUpdatesFrom(estimates), 30.seconds)
    val expectedUpdates = Set(AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer))

    assert(actualUpdates === expectedUpdates)
  }

  it should "handle the case where the estimates has never been queried before" in {
    val estimates = ExampleEstimates.COMMUNICATIONS_2015_BUDGET

    val newAnswer = AnswerUpdatesForTesting.defaultAnswer()

    (dao.haveEverQueried _).expects(estimates).returning(Future.successful(false))

    (scraper.scrapeFrom _).expects(estimates).returning(List(newAnswer))

    val actualUpdates = Await.result(updatesReader.latestUpdatesFrom(estimates), 30.seconds)
    val expectedUpdates = Set(AnswerUpdate.forExistingAnswer(newAnswer))

    assert(actualUpdates === expectedUpdates)
  }

  behaviour of "the UpdatesReader when reading updates for all Estimates"

  it should "read the updates for all the registered estimates" in {
    val estimates1 = ExampleEstimates.COMMUNICATIONS_2015_BUDGET
    val estimates2 = ExampleEstimates.TREASURY_2015_BUDGET

    val registeredEstimates = Set(estimates1, estimates2)

    (dao.listEstimates _).expects().returning(Future.successful(registeredEstimates))

    (dao.haveEverQueried _).expects(estimates1).returning(Future.successful(false))
    (dao.haveEverQueried _).expects(estimates2).returning(Future.successful(false))

    (scraper.scrapeFrom _).expects(estimates1).returning(List.empty)
    (scraper.scrapeFrom _).expects(estimates2).returning(List.empty)

    Await.result(updatesReader.latestUpdatesFromAllEstimates, 30.seconds)

    // The expectations act as assertions
  }
}
