package au.id.tmm.estimatesqon.controller.twitter

import au.id.tmm.estimatesqon.StandardProjectSpec

class TweetSpec extends StandardProjectSpec {
  behaviour of "the length calculation"

  it should "be correct for a short tweet" in {
    val tweet = Tweet("Hello world!")

    assert(tweet.length === 12)
  }

  it should "be correct for a tweet with a link" in {
    val tweetWithLink = Tweet("This is a good site https://github.com/tmccarthy/EstimatesQuestionsOnNoticeFeed")

    assert(tweetWithLink.length === 43 )
  }

  behaviour of "the too long check"

  it should "be correct for a normal tweet" in {
    val tweet = Tweet("Hello world!")

    assert(!tweet.isTooLong)
  }

  it should "be correct for a tweet that is too long" in {
    val tooLongTweet = Tweet("To be or not to be, that is the question. Whether 'tis nobler in the mind to suffer the " +
      "slings and arrows of outrageous fortune. Or to take arms against a sea of troubles, and by opposing end them. " +
      "To die, to sleep...")

    assert(tooLongTweet.isTooLong)
  }

  behaviour of "the validity check"

  it should "correctly identify as valid a normal tweet" in {
    val tweet = Tweet("Hello World!")

    assert(tweet.isValid)
  }

  it should "correctly identify as invalid a tweet that is too long" in {
    val tooLongTweet = Tweet("Whenever Richard Cory went down town," +
      "We people on the pavement looked at him:" +
      "He was a gentleman from sole to crown," +
      "Clean favored, and imperially slim." +
      "" +
      "And he was always quietly arrayed," +
      "And he was always human when he talked;" +
      "But still he fluttered pulses when he said," +
      "\"Good-morning,\" and he glittered when he walked." +
      "" +
      "And he was rich—yes, richer than a king—" +
      "And admirably schooled in every grace:" +
      "In fine, we thought that he was everything" +
      "To make us wish that we were in his place." +
      "" +
      "So on we worked, and waited for the light," +
      "And went without the meat, and cursed the bread;" +
      "And Richard Cory, one calm summer night," +
      "Went home and put a bullet through his head.")

    assert(!tooLongTweet.isValid)
  }
}
