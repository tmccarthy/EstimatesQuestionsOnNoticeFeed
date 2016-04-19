package au.id.tmm.estimatesqon.controller

import au.id.tmm.estimatesqon.controller.twitter.QonTwitterBot
import au.id.tmm.estimatesqon.data.Updates

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Driver {
  def doStep(updates: Updates, twitterBot: QonTwitterBot): Future[Unit] =
    updates.retrieveLatestFromAllEstimates
      .map(_.values.flatten.toSet)
      .flatMap(latestUpdates => updates.store(latestUpdates).map(_ => latestUpdates))
      .map(twitterBot.tweetAboutEachOf)
}
