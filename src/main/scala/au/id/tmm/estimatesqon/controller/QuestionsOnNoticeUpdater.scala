package au.id.tmm.estimatesqon.controller

import au.id.tmm.estimatesqon.data.QuestionsOnNoticeDAO
import au.id.tmm.estimatesqon.model._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// TODO write a test for this
object QuestionsOnNoticeUpdater {
  def updateDBFromEstimates(dao: QuestionsOnNoticeDAO, estimates: Iterable[Estimates]): Future[Unit] = {
    Future.sequence(estimates.map(updateDBFromEstimates(dao, _))).map(_ => Unit)
  }

  private def updateDBFromEstimates(dao: QuestionsOnNoticeDAO, estimates: Estimates): Future[Unit] = {
    val pastStoredAnswers = dao.retrieveLatestAnswersFor(estimates)

    val latestAnswers = scrapeLatestAnswersFrom(estimates)

    val combinedAnswers: Future[Set[AnswerUpdate]] = for {
      oldAnswers <- pastStoredAnswers
      newAnswers <- latestAnswers
    } yield AnswerUpdate.fromListsOfOldAndNewAnswers(oldAnswers, newAnswers)

    val updateBundle = combinedAnswers.map(AnswerUpdateBundle.fromUpdates(_, estimates))

    updateBundle.flatMap(dao.writeUpdateBundle)
  }

  private def scrapeLatestAnswersFrom(estimates: Estimates): Future[Set[Answer]] = Future {
    EstimatesScraper
      .forEstimates(estimates)
      .extractAnswers
      .toSet
  }
}