package au.id.tmm.estimatesqon.controller.twitter

import au.id.tmm.estimatesqon.model.{AnswerUpdate, AnswerUpdateType}
import twitter4j.api.TweetsResources

class QonTwitterBot protected (private val tweeter: TweetsResources) {

  def tweetAboutEachOf(updates: Set[AnswerUpdate]): Unit = updates.foreach(tweetAbout)

  def tweetAbout(update: AnswerUpdate): Unit = {
    if (!QonTwitterBot.IGNORED_TYPES.contains(update.updateType)) {
      val tweetToBroadcast = TweetComposition.forUpdate(update)

      tweet(tweetToBroadcast)
    }
  }

  def tweet(tweet: Tweet): Unit = tweeter.updateStatus(tweet.content)
}

object QonTwitterBot {
  def using(tweeter: TweetsResources) = new QonTwitterBot(tweeter)

  private val IGNORED_TYPES = Set(AnswerUpdateType.EXISTING, AnswerUpdateType.NO_CHANGE)
}