package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.LocalDate

import scala.concurrent.Future

class Estimates protected (val portfolio: Portfolio,
                           val description: String,
                           val hearingDates: Set[LocalDate],
                           val pageURL: URL) {
  def readAnswers: Future[Set[Answer]] = ???

  def cloneWithUrl(newUrl: URL): Estimates = new Estimates(portfolio, description, hearingDates, newUrl)
}

object Estimates {
  def create(portfolio: Portfolio, description: String, pageURL: URL, hearingDates: LocalDate*): Estimates =
    create(portfolio, description, pageURL, hearingDates.toSet)

  // TODO refactor this. The method has too many parameters
  def create(portfolio: Portfolio, description: String, pageURL: URL, hearingDates: Set[LocalDate]): Estimates =
    new Estimates(portfolio, description, hearingDates, pageURL)
}