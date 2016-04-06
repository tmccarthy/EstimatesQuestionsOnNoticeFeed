package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdateBundle, Estimates}

import scala.concurrent.Future

trait QuestionsOnNoticeDAO {

  private[data] def initialiseIfNeeded(): Future[Unit]

  def registerEstimates(estimates: Estimates): Future[Unit]

  def listEstimates: Future[Set[Estimates]]

  def haveEverQueried(estimates: Estimates): Future[Boolean]

  def retrieveLatestAnswersFor(estimates: Estimates): Future[Set[Answer]]

  def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit]

}

object QuestionsOnNoticeDAO {
}