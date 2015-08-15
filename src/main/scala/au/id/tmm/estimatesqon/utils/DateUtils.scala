package au.id.tmm.estimatesqon.utils

import java.time.LocalDate
import java.time.format.{DateTimeParseException, DateTimeFormatter}

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

}
