package au.id.tmm.estimatesqon.controller.twitter

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.model.AnswerUpdatesForTesting
import org.scalamock.scalatest.MockFactory
import twitter4j.api.TweetsResources

class QonTwitterBotSpec extends StandardProjectSpec with MockFactory {

  private val tweeter = stub[TweetsResources]

  private val botUnderTest = QonTwitterBot.using(tweeter)

  behaviour of "the QON Twitter bot"

  it should "tweet about an answer update" in {
    botUnderTest.tweetAbout(AnswerUpdatesForTesting.forDetailsAltered)

    (tweeter.updateStatus(_: String)).verify("Question \"1\" has had its details altered on the page for the 2015 " +
      "Communications Budget Estimates at http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/ecctte/estimates/bud1516/communications/index")
  }

  it should "tweet about each update when these are provided in bulk" in {
    botUnderTest.tweetAboutEachOf(Set(AnswerUpdatesForTesting.forDetailsAltered, AnswerUpdatesForTesting.forNew))

    (tweeter.updateStatus(_: String)).verify("Question \"1\" has had its details altered on the page for the 2015 " +
      "Communications Budget Estimates at http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/ecctte/estimates/bud1516/communications/index")

    (tweeter.updateStatus(_: String)).verify("Question \"1\" has been added to the page for the 2015 Communications " +
      "Budget Estimates at http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/ecctte/estimates/bud1516/communications/index")
  }

  it should "not tweet about exiting answers" in {
    botUnderTest.tweetAbout(AnswerUpdatesForTesting.forExistingAnsweredAnswer)

    (tweeter.updateStatus(_: String)).verify(*).never()
  }
}