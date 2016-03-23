package au.id.tmm.estimatesqon.controller

import java.time.Instant

import au.id.tmm.estimatesqon.data.QuestionsOnNoticeDAO
import au.id.tmm.estimatesqon.data.databasemodel.{AnswerRow, EstimatesRow}
import au.id.tmm.estimatesqon.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class QuestionsOnNoticeUpdater protected (val dao: QuestionsOnNoticeDAO,
                                          val estimates: Seq[Estimates]) {
  def doUpdate() = {
    estimates.foreach(updateFromPage)
  }

  private def updateFromPage(estimates: Estimates): Future[Unit] = {

    val timestamp = Instant.now

    readAnswerFromPageFor(estimates)
      .flatMap(answersFromPage => answerUpdatesFrom(estimates, answersFromPage))
      .flatMap(answerUpdates => {
        val newAnswerUpdateBundle: AnswerUpdateBundle = AnswerUpdateBundle.fromUpdates(answerUpdates, estimates, timestamp)

        dao.writeUpdateBundle(newAnswerUpdateBundle)
      })
  }

  private def readAnswerFromPageFor(estimates: Estimates): Future[Set[Answer]] = ???

  private def answerUpdatesFrom(questionsOnNoticePage: Estimates,
                                answersFromPage: Set[Answer]): Future[Set[AnswerUpdate]] = {
    dao.haveEverQueried(questionsOnNoticePage).flatMap(haveQueriedThisPageBefore => {
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

  private def updatesFromPreviousAnswersAndNewAnswers(estimates: Estimates, answersFromPage: Set[Answer]): Future[Set[AnswerUpdate]] = {
    dao.retrieveAnswers(estimates)
      .map(answersFromDatabase => AnswerUpdate.fromListsOfOldAndNewAnswers(answersFromDatabase, answersFromPage))
  }
}
