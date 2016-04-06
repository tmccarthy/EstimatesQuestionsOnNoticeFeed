package au.id.tmm.estimatesqon.utils

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Month, ZoneId}
import java.util.Date

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.utils.DateUtils.{ImprovedLocalDate, ImprovedOldDate}

class DateUtilsSpec extends StandardProjectSpec {

  behaviour of "the parseDate method"

  it should "be able to parse 3 July 2015 with the format d MMMM yyyy" in {
    assert(DateUtils.parseDate("3 July 2015", DateTimeFormatter.ofPattern("d MMMM yyyy"))
      contains LocalDate.of(2015, Month.JULY, 3))
  }

  it should "be able to parse 3 July 2015 with the formats dd/MM/yyyy and d MMMM yyyy" in {
    assert(DateUtils.parseDate("3 July 2015",
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("d MMMM yyyy"))
      contains LocalDate.of(2015, Month.JULY, 3))
  }

  behaviour of "the toOldDateAtZone method"

  it should "correctly convert the date 3 July 2015 to an old Date" in {
    val originalDate = LocalDate.of(2015, Month.JULY, 3)
    val timezone = ZoneId.of("Australia/Melbourne")

    val convertedDate = originalDate.toOldDateAtZone(timezone)

    assert(convertedDate.getTime == 1435845600000l)
  }

  it should "correctly convert the date 3 July 2015 to an old Date in another timezone" in {
    val originalDate = LocalDate.of(2015, Month.JULY, 3)
    val timezone = ZoneId.of("Africa/Johannesburg")

    val convertedDate = originalDate.toOldDateAtZone(timezone)

    assert(convertedDate.getTime == 1435874400000l)
  }

  behaviour of "the toLocalDateAtZone method"

  it should "correctly convert the date 3 July 2015 to a LocalDate" in {
    val originalDate = new Date(1435845600000l)
    val timezone = ZoneId.of("Australia/Melbourne")

    val convertedDate = originalDate.toLocalDateAtZone(timezone)

    assert(convertedDate.getYear == 2015)
    assert(convertedDate.getMonth === Month.JULY)
    assert(convertedDate.getDayOfMonth === 3)
  }

  it should "correctly convert the date 3 July 2015 to a LocalDate in another timezone" in {
    val originalDate = new Date(1435845600000l)
    val timezone = ZoneId.of("Africa/Johannesburg")

    val convertedDate = originalDate.toLocalDateAtZone(timezone)

    assert(convertedDate.getYear == 2015)
    assert(convertedDate.getMonth === Month.JULY)
    assert(convertedDate.getDayOfMonth === 2)
  }

  behaviour of "the toSqlDate method"

  it should "correctly convert the date 3 July 2015 to an java.sql.Date" in {
    val originalDate = new Date(1435845600000l)

    val convertedDate = originalDate.toSqlDate

    assert(convertedDate.getTime === originalDate.getTime)
  }
}
