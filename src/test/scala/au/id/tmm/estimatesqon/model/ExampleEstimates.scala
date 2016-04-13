package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.{LocalDate, Month}

/**
  * Created by timothy on 13/04/2016.
  */
object ExampleEstimates {

  val AG_2015_BUDGET: Estimates = Estimates.create(
    Portfolios.ATTORNEY_GENERAL,
    "Budget Estimates",
    new URL("http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/legconctte/estimates/bud1516/AGD/index"),
    LocalDate.of(2015, Month.MAY, 27), LocalDate.of(2015, Month.MAY, 28))

  val SOCIAL_SERVICES_2015_BUDGET: Estimates = Estimates.create(Portfolios.SOCIAL_SERVICES,
    "Budget Estimates",
    new URL("http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/clacctte/estimates/bud1516/Social%20Services/index"),
    LocalDate.of(2015, Month.JUNE, 4), LocalDate.of(2015, Month.JUNE, 5))

  val COMMUNICATIONS_2015_BUDGET: Estimates = Estimates.create(
    Portfolios.COMMUNICATIONS,
    "Budget Estimates",
    new URL("http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/ecctte/estimates/bud1516/communications/index"),
    LocalDate.of(2015, Month.MAY, 27), LocalDate.of(2015, Month.MAY, 28))

  val TREASURY_2015_BUDGET: Estimates = Estimates.create(
    Portfolios.TREASURY,
    "Budget Estimates",
    new URL("http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/economicsctte/estimates/bud1516/Treasury/index"),
    LocalDate.of(2015, Month.JUNE, 1), LocalDate.of(2015, Month.JUNE, 3))

}
