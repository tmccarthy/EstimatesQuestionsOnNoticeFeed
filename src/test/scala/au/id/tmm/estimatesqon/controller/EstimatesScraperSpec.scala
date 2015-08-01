package au.id.tmm.estimatesqon.controller

import java.time.{Month, LocalDate}

import au.id.tmm.estimatesqon.model.{Answer, Estimates, Portfolio}
import org.scalatest.FreeSpec

import scala.collection.immutable.SortedSet
import scala.io.Source

class EstimatesScraperSpec extends FreeSpec {

  "for the 2015-2016 communications Budget Estimates" - {
    val portfolio: Portfolio = Portfolio.withName("Communications")
    val hearingDates: Set[LocalDate] = Set(LocalDate.of(2015, Month.MAY, 27), LocalDate.of(2015, Month.MAY, 28))
    val estimates: Estimates = Estimates.create(portfolio, "Budget Estimates", hearingDates)

    val pageSource: Source = Source.fromURL(getClass.getResource("communications20152016BudgetEstimates.html"))

    "in the extracted set of answers" - {
      val scraper: EstimatesScraper = EstimatesScraper.forEstimates(estimates)

      val answers: SortedSet[Answer] = scraper.extractAnswers(pageSource)

      "there should be 138 elements" in {
        assert(answers.size === 138)
      }

      "the first answer" - {
        val firstAnswer: Answer = answers.head

        answerAssertions(answer = firstAnswer,
            qonNumber = 1,
            senator = "Carr",
            topic = "Cooperative intelligent transport systems",
            pdfURL = "http://www.aph.gov.au/~/media/Committees/ec_ctte/estimates/bud_1516/communications/q1.pdf",
            dateReceived = LocalDate.of(2015, 7, 9))
      }

      "the last answer" - {
        val lastAnswer: Answer = answers.last

        answerAssertions(answer = lastAnswer,
          qonNumber = 138,
          senator = "Ludwig",
          topic = "Departmental Dispute Resolution",
          pdfURL = "http://www.aph.gov.au/~/media/Committees/ec_ctte/estimates/bud_1516/communications/q138_ACMA.pdf",
          dateReceived = LocalDate.of(2015, 7, 9))
      }
    }
  }

  def answerAssertions(answer: Answer, qonNumber: Int, senator: String, topic: String, pdfURL: String, dateReceived: LocalDate): Unit = {
    s"the question on notice number should be $qonNumber" in {
      assert(answer.qonNumber === qonNumber)
    }

    s"the Senator should be '$senator'" in {
      assert(answer.senator === senator)
    }

    s"the topic should be '$topic'" in {
      assert(answer.topic === topic)
    }

    s"the first pdf URL should be <$pdfURL>" in {
      assert(answer.pdfURLs.head.toString === pdfURL)
    }

    s"the first date received should be $dateReceived" in {
      assert(answer.datesReceived.head === dateReceived)
    }
  }
}
