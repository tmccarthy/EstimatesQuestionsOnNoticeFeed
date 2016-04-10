package au.id.tmm.estimatesqon.controller.twitter

import com.twitter.Validator

case class Tweet (content: String) {
  val length: Int = Tweet.tweetValidator.getTweetLength(content)

  val isTooLong: Boolean = length > Validator.MAX_TWEET_LENGTH

  val isValid: Boolean = Tweet.tweetValidator.isValidTweet(content)
}

private object Tweet {
  private val tweetValidator = new Validator()
}
