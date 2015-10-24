package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.{LocalDate, Month}

class QuestionsOnNoticePage protected (val estimates: Estimates,
                                       val url: URL) {
  def readAnswers: Set[Answer] = ???
}

object QuestionsOnNoticePage {
  def fromList(): Seq[QuestionsOnNoticePage] = {
    val portfolio = Portfolio.withName("Attorney General's Portfolio")
    val estimatesDescription = "Budget Estimates"
    val hearingDates = Set(LocalDate.of(2015, Month.MAY, 27), LocalDate.of(2015, Month.MAY, 28))
    val estimates = Estimates.create(portfolio, estimatesDescription, hearingDates)
    val url: URL = new URL("http://www.aph.gov.au/Parliamentary_Business/Senate_Estimates/legconctte/estimates/bud1516/AGD/index")

    val aGQonPage: QuestionsOnNoticePage = new QuestionsOnNoticePage(estimates, url)

    Seq(aGQonPage)
  }
}