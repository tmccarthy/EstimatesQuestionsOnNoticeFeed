package au.id.tmm.estimatesqon.utils

import java.time.format.DateTimeFormatter
import java.time.{Month, LocalDate}

import org.scalatest.FreeSpec

class DateUtilsSpec extends FreeSpec {

  "the parseDate method" - {

    "should be able to parse 3 July 2015 with the format d MMMM yyyy" in {
      assert(DateUtils.parseDate("3 July 2015", DateTimeFormatter.ofPattern("d MMMM yyyy"))
        contains LocalDate.of(2015, Month.JULY, 3))
    }

    "should be able to parse 3 July 2015 with the formats dd/MM/yyyy and d MMMM yyyy" in {
      assert(DateUtils.parseDate("3 July 2015",
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d MMMM yyyy"))
        contains LocalDate.of(2015, Month.JULY, 3))
    }
  }
}
