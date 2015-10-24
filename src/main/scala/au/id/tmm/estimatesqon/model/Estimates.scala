package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.LocalDate

import scala.concurrent.Future

class Estimates protected (val portfolio: Portfolio,
                           val description: String,
                           val hearingDates: Set[LocalDate],
                           val pageURL: URL) {
  def readAnswers: Future[Set[Answer]] = ???
}

object Estimates {
  // TODO refactor this. The method has too many parameters
  def create(portfolio: Portfolio, description: String, hearingDates: Set[LocalDate], pageURL: URL): Estimates =
    new Estimates(portfolio, description, hearingDates, pageURL)
}