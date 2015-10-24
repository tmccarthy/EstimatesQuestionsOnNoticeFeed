package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.model.{QuestionsOnNoticePage, Answer, AnswerUpdateBundle, Estimates}

trait QuestionsOnNoticeDAO {

  protected def storeExists(): Boolean

  protected def createStore(): Unit

  def haveQueried(questionsOnNoticePage: QuestionsOnNoticePage): Boolean

  def retrieveAnswers(estimates: Estimates): Set[Answer]

  def writeUpdateBundle(updateBundle: AnswerUpdateBundle)

}

object QuestionsOnNoticeDAO {
  def get: QuestionsOnNoticeDAO = ???
}