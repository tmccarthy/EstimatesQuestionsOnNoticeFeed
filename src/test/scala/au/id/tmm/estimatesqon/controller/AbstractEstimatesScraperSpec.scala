package au.id.tmm.estimatesqon.controller

import java.time.LocalDate

import au.id.tmm.estimatesqon.model.{Answer, Estimates, Portfolio}
import org.scalatest.FreeSpec

import scala.io.Source

private[controller] abstract class AbstractEstimatesScraperSpec protected (val portfolioName: String,
                                                                           val hearingDates: Set[LocalDate],
                                                                           val estimatesDescription: String,
                                                                           val pageSource: Source,
                                                                           val expectedNumAnswers: Int,
                                                                           val answerAssertions: Iterable[AnswerAssertionInfo]
                                                                            ) extends FreeSpec {

  s"for the $portfolioName $estimatesDescription estimates held on $hearingDates" - {
    val portfolio: Portfolio = Portfolio.withName(portfolioName)
    val estimates: Estimates = Estimates.create(portfolio, estimatesDescription, hearingDates)

    "in the extracted set of answers" - {
      val scraper: EstimatesScraper = EstimatesScraper.forEstimates(estimates)

      val answers: List[Answer] = scraper.extractAnswers(pageSource).toList

      s"there should be $expectedNumAnswers elements" in {
        assert(answers.size === expectedNumAnswers)
      }

      answerAssertions.foreach(answerAssertionInfo => {
        s"for the ${answerAssertionInfo.answerDescription} answer" - {

          val answer = answerAssertionInfo.getAnswer(answers)

          answerAssertions(answer, answerAssertionInfo)
        }
      })
    }
  }

  def answerAssertions(answer: Option[Answer],
                       answerAssertionInfo: AnswerAssertionInfo): Unit = {

    assertExists(answer)

    if (answer.isDefined) {
      questionOnNoticeNumberAssertions(answer.get, answerAssertionInfo.qonNumber)
      agencyAssertions(answer.get, answerAssertionInfo.divisionOrAgency)
      senatorAssertions(answer.get, answerAssertionInfo.senator)
      topicAssertions(answer.get, answerAssertionInfo.topic)
      pdfURLAssertions(answer.get, answerAssertionInfo.pdfURL)
      dateReceivedAssertions(answer.get, answerAssertionInfo.dateReceived)
    }
  }

  def assertExists(answer: Option[Answer]): Unit = {
    "the answer exists" in {
      assert(answer.isDefined)
    }
  }

  def questionOnNoticeNumberAssertions(answer: Answer, qonNumber: String): Unit = {
    s"the question on notice number should be $qonNumber" in {
      assert(answer.qonIdentifier === Option.apply(qonNumber))
    }
  }

  def agencyAssertions(answer: Answer, divisionOrAgency: String): Unit = {
    s"the division or agency should be $divisionOrAgency" in {
      assert(answer.divisionOrAgency === Option.apply(divisionOrAgency))
    }
  }

  def senatorAssertions(answer: Answer, senator: String): Unit = {
    s"the Senator should be '$senator'" in {
      assert(answer.senator === Option.apply(senator))
    }
  }

  def topicAssertions(answer: Answer, topic: String): Unit = {
    s"the topic should be '$topic'" in {
      assert(answer.topic === Option.apply(topic))
    }
  }

  def pdfURLAssertions(answer: Answer, pdfURL: Option[String]): Unit = {
    val expectedPDFURLDisplay = if (pdfURL.isDefined) s"<$pdfURL>" else "absent"
    s"the first pdf URL should be $expectedPDFURLDisplay" in {

      if (pdfURL.isDefined) {
        assert(answer.pdfURLs.headOption.map(_.toString) === pdfURL)
      } else {
        assert(answer.pdfURLs.isEmpty)
      }
    }
  }

  def dateReceivedAssertions(answer: Answer, dateReceived: Option[LocalDate]): Unit = {
    val expectedDateReceivedDisplay = if (dateReceived.isDefined) dateReceived.toString else "absent"
    s"the first date received should be $expectedDateReceivedDisplay" in {
      if (dateReceived.isDefined) {
        assert(answer.datesReceived.headOption === dateReceived)
      } else {
        assert(answer.datesReceived.isEmpty)
      }
    }
  }
}
