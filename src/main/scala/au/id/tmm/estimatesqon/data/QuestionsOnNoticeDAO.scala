package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.model.{QuestionsOnNoticePage, Answer, AnswerUpdateBundle, Estimates}

import scala.concurrent.Future

trait QuestionsOnNoticeDAO {

  private[data] def initialise(): Future[Unit]

  def haveQueried(questionsOnNoticePage: QuestionsOnNoticePage): Future[Boolean]

  def retrieveAnswers(estimates: Estimates): Future[Set[Answer]]

  def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit]

}

object QuestionsOnNoticeDAO {
  def get: QuestionsOnNoticeDAO = ???
}