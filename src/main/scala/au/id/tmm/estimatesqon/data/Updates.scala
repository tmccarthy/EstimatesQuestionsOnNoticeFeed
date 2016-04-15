package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.controller.scraping.EstimatesScraper
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, Estimates}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Updates protected(private val scraper: EstimatesScraper, private val dao: QonDao) {

  // TODO this should really be done with one big db query to get all the most up-to-date answers.
  def retrieveLatestFromAllEstimates: Future[Map[Estimates, Set[AnswerUpdate]]] = {
    dao.listEstimates.flatMap(allEstimates => {
      val estimatesWithAnswers: Set[Future[(Estimates, Set[AnswerUpdate])]] =
        allEstimates.map(retrieveLatestPairedWithEstimates)

      val sequencedEstimatesWithAnswers: Future[Set[(Estimates, Set[AnswerUpdate])]] =
        Future.sequence(estimatesWithAnswers)

      val returnedFuture: Future[Map[Estimates, Set[AnswerUpdate]]] = sequencedEstimatesWithAnswers.map(_.toMap)

      returnedFuture
    })
  }

  private def retrieveLatestPairedWithEstimates(singleEstimates: Estimates): Future[(Estimates, Set[AnswerUpdate])] = {
    val latestUpdates = retrieveLatestFor(singleEstimates)

    latestUpdates.map((singleEstimates, _))
  }

  def retrieveLatestFor(estimates: Estimates): Future[Set[AnswerUpdate]] = {
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

  private def scrapedAnswersFrom(estimates: Estimates): Future[Set[Answer]] = Future {
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

  def store(answerUpdates: Set[AnswerUpdate]): Future[Unit] = dao.writeUpdates(answerUpdates)
}

object Updates {
  private [data] def using(scraper: EstimatesScraper, dao: QonDao) = new Updates(scraper, dao)

  def apply() = using(EstimatesScraper(), QonDao.forConfigName("prodDB"))
}