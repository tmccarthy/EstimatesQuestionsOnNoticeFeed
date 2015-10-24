package au.id.tmm.estimatesqon.controller

import java.time.Instant

import au.id.tmm.estimatesqon.data.QuestionsOnNoticeDAO
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, AnswerUpdateBundle, QuestionsOnNoticePage}

class QuestionsOnNoticeUpdater protected (val dao: QuestionsOnNoticeDAO,
                                          val pages: Seq[QuestionsOnNoticePage]) {

  def doUpdate() = {
    pages.foreach(updateFromPage)
  }

  private def updateFromPage(questionsOnNoticePage: QuestionsOnNoticePage) = {
    val answersFromPage = questionsOnNoticePage.readAnswers
    val timestamp = Instant.now

    val answerUpdates = answerUpdatesFrom(questionsOnNoticePage, answersFromPage)

    val answerUpdateBundle = AnswerUpdateBundle.fromUpdates(answerUpdates, questionsOnNoticePage, timestamp)

    dao.writeUpdateBundle(answerUpdateBundle)
  }

  private def answerUpdatesFrom(questionsOnNoticePage: QuestionsOnNoticePage,
                                answersFromPage: Set[Answer]): Set[AnswerUpdate] = {
    val haveQueriedThisPageBefore = dao.haveQueried(questionsOnNoticePage)

    if (haveQueriedThisPageBefore) {
      updatesFromPreviousAnswersAndNewAnswers(questionsOnNoticePage, answersFromPage)
    } else {
      updatesForFirstQuery(answersFromPage)
    }
  }

  private def updatesForFirstQuery(answersFromPage: Set[Answer]): Set[AnswerUpdate] = {
    answersFromPage.map(AnswerUpdate.forExistingAnswer)
  }

  private def updatesFromPreviousAnswersAndNewAnswers(questionsOnNoticePage: QuestionsOnNoticePage, answersFromPage: Set[Answer]): Set[AnswerUpdate] = {
    val answersFromDatabase = dao.retrieveAnswers(questionsOnNoticePage.estimates)

    val answerUpdates = AnswerUpdate.fromListsOfOldAndNewAnswers(answersFromDatabase, answersFromPage)

    answerUpdates
  }
}
