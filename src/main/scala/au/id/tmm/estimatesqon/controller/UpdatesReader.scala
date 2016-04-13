package au.id.tmm.estimatesqon.controller

import au.id.tmm.estimatesqon.controller.scraping.EstimatesScraper
import au.id.tmm.estimatesqon.data.QonDao
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, Estimates}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UpdatesReader protected (val scraper: EstimatesScraper, val dao: QonDao) {

  // TODO this should really be done with one big db query to get all the most up-to-date answers.
  def latestUpdatesFromAllEstimates: Future[Map[Estimates, Set[AnswerUpdate]]] = {
    dao.listEstimates.flatMap(allEstimates => {
      val estimatesWithAnswers: Set[Future[(Estimates, Set[AnswerUpdate])]] =
        allEstimates.map(latestUpdatesPairedWithEstimates)

      val sequencedEstimatesWithAnswers: Future[Set[(Estimates, Set[AnswerUpdate])]] =
        Future.sequence(estimatesWithAnswers)

      val returnedFuture: Future[Map[Estimates, Set[AnswerUpdate]]] = sequencedEstimatesWithAnswers.map(_.toMap)

      returnedFuture
    })
  }

  def latestUpdatesPairedWithEstimates(singleEstimates: Estimates): Future[(Estimates, Set[AnswerUpdate])] = {
    val latestUpdates = latestUpdatesFrom(singleEstimates)

    latestUpdates.map((singleEstimates, _))
  }

  def latestUpdatesFrom(estimates: Estimates): Future[Set[AnswerUpdate]] = {
    val previousAnswersFuture = previousAnswersFor(estimates)
    val scrapedAnswersFuture = scrapedAnswersFrom(estimates)

    for {
      previousAnswers <- previousAnswersFuture
      scrapedAnswers <- scrapedAnswersFuture
    } yield {
      previousAnswers
        .map(AnswerUpdate.fromSetsOfOldAndNewAnswers(_, scrapedAnswers))
        .getOrElse(scrapedAnswers.map(AnswerUpdate.forExistingAnswer))
    }
  }

  def scrapedAnswersFrom(estimates: Estimates): Future[Set[Answer]] = Future {
    scraper.scrapeFrom(estimates).toSet
  }

  // Option is empty if this estimates has never been queried before
  private def previousAnswersFor(estimates: Estimates): Future[Option[Set[Answer]]] = {
    dao.haveEverQueried(estimates).flatMap(haveBeenQueried => {
      if (haveBeenQueried) {
        dao.retrieveLatestAnswersFor(estimates).map(Some(_))
      } else {
        Future.successful(None)
      }
    })
  }
}

object UpdatesReader {
  def using(scraper: EstimatesScraper, dao: QonDao) = new UpdatesReader(scraper, dao)
}