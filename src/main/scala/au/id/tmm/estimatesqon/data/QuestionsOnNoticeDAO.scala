package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdateBundle, Estimates}

import scala.concurrent.Future

trait QuestionsOnNoticeDAO {

  private[data] def initialiseIfNeeded(): Future[Unit]

  def recordEstimates(estimates: Estimates): Future[Unit]

  def haveEverQueried(estimates: Estimates): Future[Boolean]

  def retrieveAnswers(estimates: Estimates): Future[Set[Answer]]

  def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit]

}

object QuestionsOnNoticeDAO {
}