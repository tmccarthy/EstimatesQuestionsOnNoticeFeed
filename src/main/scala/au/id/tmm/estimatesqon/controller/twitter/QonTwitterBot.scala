package au.id.tmm.estimatesqon.controller.twitter

import au.id.tmm.estimatesqon.model.AnswerUpdate

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait QonTwitterBot {

  def tweetAboutEachOf(updates: Set[AnswerUpdate]): Future[Unit] = Future.sequence(updates.map(tweetAbout)).map(_ => Unit)

  def tweetAbout(answerUpdate: AnswerUpdate): Future[Unit]

}
