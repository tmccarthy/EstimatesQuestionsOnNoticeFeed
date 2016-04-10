package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.{LocalDate, Month}
import java.util.Date

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.controller.TestResources

class EstimatesSpec extends StandardProjectSpec {

  val portfolio: Portfolio = Portfolio.withName("Communications")
  val description: String = "Budget Estimates"
  val pageUrl: URL = TestResources.communications20152016BudgetEstimates

  val firstDay: LocalDate = LocalDate.of(2015, Month.MAY, 27)
  val lastDay: LocalDate = LocalDate.of(2015, Month.MAY, 28)

  val estimatesUnderTest: Estimates = Estimates.create(
    portfolio,
    description,
    pageUrl,
    firstDay, lastDay)

  "A constructed Estimates" should "have the correct portfolio" in {
    assert(portfolio === estimatesUnderTest.portfolio)
  }

  it should "have the correct description" in {
    assert(description === estimatesUnderTest.description)
  }

  it should "have the correct first day" in {
    assert(firstDay === estimatesUnderTest.firstDay)
  }

  it should "have the correct second day" in {
    assert(lastDay === estimatesUnderTest.lastDay)
  }

  it should "have the correct page URL" in {
    assert(pageUrl === estimatesUnderTest.pageURL)
  }

  it should "correctly convert the first and last dates to the old format" in {
    val expectedFirstDayOldFormat: Date = new Date(1432648800000l)
    val expectedLastDayOldFormat: Date = new Date(1432735200000l)

    assert(expectedFirstDayOldFormat === estimatesUnderTest.firstDayOldDateType)
    assert(expectedLastDayOldFormat === estimatesUnderTest.lastDayOldDateType)
  }

  it should "have a printable representation" in {
    val actualString = estimatesUnderTest.printableString

    val expectedString = "2015 Communications Budget Estimates"

    assert(expectedString === actualString)
  }

  "An estimates" can "be constructed using dates in the old type" in {
    val firstDayOldType: Date = new Date(1432648800000l)
    val lastDayOldType: Date = new Date(1432735200000l)

    val estimatesWithOldDateTypes = Estimates.create(portfolio, description, pageUrl, firstDayOldType, lastDayOldType)

    assert(estimatesUnderTest === estimatesWithOldDateTypes)
  }

  "An estimates" should "throw if its first day is after its last day" in {
    intercept[IllegalArgumentException] {
      Estimates.create(portfolio, description, pageUrl, lastDay, firstDay)
    }
  }
}
