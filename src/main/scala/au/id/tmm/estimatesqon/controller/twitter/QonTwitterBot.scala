package au.id.tmm.estimatesqon.controller.twitter

import au.id.tmm.estimatesqon.model.AnswerUpdate

import scala.concurrent.Future

trait QonTwitterBot {

  def tweetAbout(answerUpdate: AnswerUpdate): Future[Unit]

}
