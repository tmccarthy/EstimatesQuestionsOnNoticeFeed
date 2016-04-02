package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time._
import java.util.Date

import au.id.tmm.estimatesqon.utils.DateUtils.{ImprovedLocalDate, ImprovedOldDate}

case class Estimates protected (portfolio: Portfolio,
                                description: String,
                                firstDay: LocalDate,
                                lastDay: LocalDate,
                                pageURL: URL) {
  if (firstDay.isAfter(lastDay)) {
    throw new IllegalArgumentException("The first day can't be after the last day")
  }

  def cloneWithUrl(newUrl: URL): Estimates = new Estimates(portfolio, description, firstDay, lastDay, newUrl)

  lazy val firstDayOldDateType: Date = firstDay.toOldDateAtZone(Estimates.estimatesTimeZone)

  lazy val lastDayOldDateType: Date = lastDay.toOldDateAtZone(Estimates.estimatesTimeZone)
}

object Estimates {
  val estimatesTimeZone: ZoneId = ZoneId.of("Australia/Canberra")

  def create(portfolio: Portfolio, description: String, pageUrl: URL, firstDay: Date, lastDay: Date): Estimates =
    create(portfolio,
      description,
      pageUrl,
      firstDay.toLocalDateAtZone(estimatesTimeZone),
      lastDay.toLocalDateAtZone(estimatesTimeZone))

  // TODO refactor this. The method has too many parameters
  def create(portfolio: Portfolio, description: String, pageURL: URL, firstDay: LocalDate, lastDay: LocalDate): Estimates =
    new Estimates(portfolio,
      description,
      firstDay,
      lastDay,
      pageURL)
}