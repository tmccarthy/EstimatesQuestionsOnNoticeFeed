package au.id.tmm.estimatesqon.controller

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.controller.twitter.QonTwitterBot
import au.id.tmm.estimatesqon.data.MockableUpdates
import au.id.tmm.estimatesqon.model.{AnswerUpdatesForTesting, ExampleEstimates}
import org.scalamock.scalatest.MockFactory

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class DriverSpec extends StandardProjectSpec with MockFactory {

  behaviour of "a step"

  val updates = mock[MockableUpdates]
  val twitterBot = mock[QonTwitterBot]

  private val latestUpdates = Set(AnswerUpdatesForTesting.forDetailsAltered)
  private val latestUpdatesPerEstimates = Map(ExampleEstimates.COMMUNICATIONS_2015_BUDGET -> latestUpdates)

  it should "retrieve the latest updates, store them and tweet about them" in {
    (updates.retrieveLatestFromAllEstimates _).expects().returns(Future.successful(latestUpdatesPerEstimates))

    (updates.store _).expects(latestUpdates).returns(Future.successful(Unit))

    (twitterBot.tweetAboutEachOf _).expects(latestUpdates).returns(Future.successful(Unit))

    Await.result(Driver.doStep(updates, twitterBot), 30.seconds)
  }
}
