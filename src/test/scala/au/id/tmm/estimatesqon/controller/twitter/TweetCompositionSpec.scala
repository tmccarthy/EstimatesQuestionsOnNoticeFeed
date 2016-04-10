package au.id.tmm.estimatesqon.controller.twitter

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.model.{AnswerUpdate, AnswerUpdatesForTesting}

class TweetCompositionSpec extends StandardProjectSpec {

  val estimatesURL = AnswerUpdatesForTesting.estimates.pageURL

  behaviour of "the answer update tweet computation"

  def assertTweetFor(answerUpdate: AnswerUpdate, expectedTweetContent: String) = {
    val actualTweet = TweetComposition.forUpdate(answerUpdate)

    val expectedTweet = Tweet(expectedTweetContent)

    assert(actualTweet === expectedTweet)
    assert(actualTweet.isValid)
  }

  it should "construct a valid tweet for an unchanged answer" in {
    assertTweetFor(AnswerUpdatesForTesting.forNoChange,
      s"""Question "1" for the 2015 Communications Budget Estimates has not been updated at $estimatesURL""")
  }

  it should "construct a valid tweet for an existing answer" in {
    assertTweetFor(AnswerUpdatesForTesting.forExistingUnansweredAnswer, s"""Question "1" has been """ +
      s"""registered for the for the 2015 Communications Budget Estimates at $estimatesURL""")
  }

  it should "construct a valid tweet for a newly added question " in {
    assertTweetFor(AnswerUpdatesForTesting.forNew, s"""Question "1" has been added to the page """ +
      s"for the 2015 Communications Budget Estimates at $estimatesURL")
  }

  it should "construct a valid tweet for a removed question" in {
    assertTweetFor(AnswerUpdatesForTesting.forRemoved, s"""Question "1" has been removed from the page """ +
      s"for the 2015 Communications Budget Estimates at $estimatesURL")
  }

  it should "construct a valid tweet for an answered question" in {
    assertTweetFor(AnswerUpdatesForTesting.forAnswered, s"""Question "1" has been answered on the page for the """ +
      s"2015 Communications Budget Estimates at $estimatesURL")
  }

  it should "construct a valid tweet for a question that has been marked as answered" in {
    assertTweetFor(AnswerUpdatesForTesting.forMarkedAsAnswered, """Question "1" has been marked as answered on the """ +
      s"page for the 2015 Communications Budget Estimates at $estimatesURL")
  }

  it should "construct a valid tweet for a question that has had its details changed" in {
    assertTweetFor(AnswerUpdatesForTesting.forDetailsAltered, """Question "1" has had its details altered on the """ +
      s"page for the 2015 Communications Budget Estimates at $estimatesURL")
  }
}
