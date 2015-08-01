package au.id.tmm.estimatesqon.model

import java.time.LocalDate

class Estimates protected (val portfolio: Portfolio,
                           val description: String,
                           val hearingDates: Set[LocalDate]) {
}

object Estimates {
  def create(portfolio: Portfolio, description: String, hearingDates: Set[LocalDate]): Estimates =
    new Estimates(portfolio, description, hearingDates)
}