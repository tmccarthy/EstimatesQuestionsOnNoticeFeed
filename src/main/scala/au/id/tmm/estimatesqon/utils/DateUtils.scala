package au.id.tmm.estimatesqon.utils

import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.{Instant, LocalDate, LocalDateTime, ZoneId}
import java.util.Date

object DateUtils {

  def parseDate(string: String, formatToTry: DateTimeFormatter*): Option[LocalDate] = {
    formatToTry.collectFirst {case format if canParse(string, format) => LocalDate.parse(string, format) }
  }

  private def canParse(string: String, format:DateTimeFormatter): Boolean = {
    try {
      LocalDate.parse(string, format)
      true
    } catch {
      case ex: DateTimeParseException => false
    }
  }

  implicit class ImprovedLocalDate(localDate: LocalDate) {
    def toOldDateAtZone(zoneId: ZoneId): Date = {
      val startOfDay: Instant = localDate.atStartOfDay(zoneId).toInstant

      Date.from(startOfDay)
    }
  }

  implicit class ImprovedOldDate(date: Date) {
    def toLocalDateAtZone(zoneId: ZoneId): LocalDate = {
      val instant: Instant = Instant.ofEpochMilli(date.getTime)

      LocalDateTime.ofInstant(instant, zoneId).toLocalDate
    }

    def toSqlDate: java.sql.Date = new java.sql.Date(date.getTime)
  }
}
