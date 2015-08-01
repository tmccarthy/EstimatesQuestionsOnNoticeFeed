package au.id.tmm.estimatesqon.model

import java.time.{Month, LocalDate}

import org.scalatest.FreeSpec

class EstimatesSpec extends FreeSpec {

  "an estimates object created for the communications portfolio and the 2015-2016 Budget Estimates" - {
    val portfolio: Portfolio = Portfolio.withName("Communications")
    val hearingDates: Set[LocalDate] = Set(LocalDate.of(2015, Month.MAY, 27), LocalDate.of(2015, Month.MAY, 28))

    val estimates: Estimates = Estimates.create(portfolio, "Budget Estimates", hearingDates)

    "should be for the communications porfolio" in {
      assert(portfolio === estimates.portfolio)
    }

    "should have the description 'Budget Estimates'" in {
      assert("Budget Estimates" === estimates.description)
    }
    
    "should have the hearing dates of the 27th and 28th of May 2015" in {
      assert(hearingDates === estimates.hearingDates)
    }
  }
}
