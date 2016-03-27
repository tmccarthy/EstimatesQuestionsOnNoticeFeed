package au.id.tmm.estimatesqon.data.databasemodel

import java.net.URL

import au.id.tmm.estimatesqon.model.{Estimates, Portfolio}

object RowToModelConversions {

  def estimatesFromDbRow(estimatesRow: EstimatesRow): Estimates = Estimates.create(
    Portfolio.withName(estimatesRow.portfolioName),
    estimatesRow.description,
    new URL(estimatesRow.pageURL),
    estimatesRow.firstDay,
    estimatesRow.lastDay
  )

}
