package au.id.tmm.estimatesqon.model

import java.net.URL
import java.sql
import java.time._
import java.util.Date

import scala.concurrent.Future

case class Estimates protected (val portfolio: Portfolio,
                                val description: String,
                                val firstDay: LocalDate,
                                val lastDay: LocalDate,
                                val pageURL: URL) {
  if (firstDay.isAfter(lastDay)) {
    throw new IllegalArgumentException("The first day can't be after the last day")
  }

  def cloneWithUrl(newUrl: URL): Estimates = new Estimates(portfolio, description, firstDay, lastDay, newUrl)

  lazy val firstDayOldDateType: Date = Estimates.convertToOldDateType(firstDay)

  lazy val lastDayOldDateType: Date = Estimates.convertToOldDateType(lastDay)
}

object Estimates {
  private val estimatesTimeZone: ZoneId = ZoneId.of("Australia/Canberra")

  def create(portfolio: Portfolio, description: String, pageUrl: URL, firstDay: Date, lastDay: Date): Estimates =
    create(portfolio,
      description,
      pageUrl,
      convertFromOldDateType(firstDay),
      convertFromOldDateType(lastDay))

  // TODO refactor this. The method has too many parameters
  def create(portfolio: Portfolio, description: String, pageURL: URL, firstDay: LocalDate, lastDay: LocalDate): Estimates =
    new Estimates(portfolio,
      description,
      firstDay,
      lastDay,
      pageURL)

  private def convertToOldDateType(localDate: LocalDate): Date = {
    val startOfDay: Instant = localDate.atStartOfDay(estimatesTimeZone).toInstant

    Date.from(startOfDay)
  }

  private def convertFromOldDateType(oldDate: Date): LocalDate = {
    val instant: Instant = Instant.ofEpochMilli(oldDate.getTime)

    LocalDateTime.ofInstant(instant, estimatesTimeZone).toLocalDate
  }
}