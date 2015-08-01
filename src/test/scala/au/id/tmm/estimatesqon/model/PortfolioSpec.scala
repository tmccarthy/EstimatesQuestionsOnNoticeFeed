package au.id.tmm.estimatesqon.model

import org.scalatest.FreeSpec

class PortfolioSpec extends FreeSpec {

  "a portfolio created with the name 'communications'" - {
    val portfolio: Portfolio = Portfolio.withName("communications")

    "should have the name 'communications'" in {
      assert("communications" === portfolio.name)
    }
  }
}
