package au.id.tmm.estimatesqon.controller

import java.time.Instant

import au.id.tmm.estimatesqon.data.QuestionsOnNoticeDAO
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, AnswerUpdateBundle, QuestionsOnNoticePage}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QuestionsOnNoticeUpdater protected (val dao: QuestionsOnNoticeDAO,
                                          val pages: Seq[QuestionsOnNoticePage]) {
  def doUpdate() = {
    pages.foreach(updateFromPage)
  }

  private def updateFromPage(questionsOnNoticePage: QuestionsOnNoticePage): Future[Unit] = {
    val answersFromPage = questionsOnNoticePage.readAnswers
    val timestamp = Instant.now

    answerUpdatesFrom(questionsOnNoticePage, answersFromPage)
      .flatMap(answerUpdates => {
        val newAnswerUpdateBundle: AnswerUpdateBundle = AnswerUpdateBundle.fromUpdates(answerUpdates, questionsOnNoticePage, timestamp)

        dao.writeUpdateBundle(newAnswerUpdateBundle)
      })
  }

  private def answerUpdatesFrom(questionsOnNoticePage: QuestionsOnNoticePage,
                                answersFromPage: Set[Answer]): Future[Set[AnswerUpdate]] = {
    dao.haveQueried(questionsOnNoticePage).flatMap(haveQueriedThisPageBefore => {
      if (haveQueriedThisPageBefore) {
        updatesFromPreviousAnswersAndNewAnswers(questionsOnNoticePage, answersFromPage)
      } else {
        updatesForFirstQuery(answersFromPage)
      }
    })
  }

  private def updatesForFirstQuery(answersFromPage: Set[Answer]): Future[Set[AnswerUpdate]] = {
    Future(answersFromPage.map(AnswerUpdate.forExistingAnswer))
  }

  private def updatesFromPreviousAnswersAndNewAnswers(questionsOnNoticePage: QuestionsOnNoticePage, answersFromPage: Set[Answer]): Future[Set[AnswerUpdate]] = {
    dao.retrieveAnswers(questionsOnNoticePage.estimates)
      .map(answersFromDatabase => AnswerUpdate.fromListsOfOldAndNewAnswers(answersFromDatabase, answersFromPage))
  }
}
