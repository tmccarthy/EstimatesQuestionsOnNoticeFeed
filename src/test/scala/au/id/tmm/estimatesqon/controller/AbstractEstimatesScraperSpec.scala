package au.id.tmm.estimatesqon.controller

import java.net.URL
import java.time.{Duration, Instant}

import au.id.tmm.estimatesqon.model.{Answer, Estimates}
import org.scalatest.{FlatSpec, GivenWhenThen}

private[controller] abstract class AbstractEstimatesScraperSpec protected (val estimatesToTest: Estimates,
                                                                           val resourceURL: URL,
                                                                           val expectedNumAnswers: Int,
                                                                           val answerAssertions: Iterable[AnswerAssertionInfo]
                                                                          ) extends FlatSpec with GivenWhenThen {

  val estimates = estimatesToTest.cloneWithUrl(resourceURL)

  val estimatesTestDescription = s"${estimates.portfolio.name} ${estimates.description} estimates " +
    s"held on ${estimates.firstDay}"

  val scraper: EstimatesScraper = EstimatesScraper.forEstimates(estimates)
  val scrapedAnswers: List[Answer] = scraper.extractAnswers.toList

  s"The set of answers extracted from the $estimatesTestDescription" should s"have $expectedNumAnswers answers" in {
    assert(scrapedAnswers.size === expectedNumAnswers)
  }

  answerAssertions.foreach(answerAssertionInfo => {
    val answer = answerAssertionInfo.findAnswerIn(scrapedAnswers)

    s"The ${answerAssertionInfo.answerDescription} answer" should "exist" in {
      assert(answer.isDefined)
    }

    it should s"have ${answerAssertionInfo.qonNumber} as the qon number" in {
      assert(answer.get.qonIdentifier === answerAssertionInfo.qonNumber)
    }

    it should s"have ${answerAssertionInfo.divisionOrAgency} as the division" in {
      assert(answer.get.divisionOrAgency === Some(answerAssertionInfo.divisionOrAgency))
    }

    it should s"have ${answerAssertionInfo.senator} as the senator" in {
      assert(answer.get.senator === Some(answerAssertionInfo.senator))
    }

    it should s"have ${answerAssertionInfo.topic} as the topic" in {
      assert(answer.get.topic === Some(answerAssertionInfo.topic))
    }

    val expectedPDFURLDisplay = if (answerAssertionInfo.pdfURL.isDefined) s"<$answerAssertionInfo.pdfURL>" else "absent"

    it should s"have $expectedPDFURLDisplay as the first PDF" in {
      if (answerAssertionInfo.pdfURL.isDefined) {
        assert(answer.get.pdfURLs.headOption.map(_.toString) === answerAssertionInfo.pdfURL)
      } else {
        assert(answer.get.pdfURLs.isEmpty)
      }
    }

    val expectedDateReceivedDisplay = if (answerAssertionInfo.dateReceived.isDefined) {
      answerAssertionInfo.dateReceived.get.toString
    } else {
      "absent"
    }

    it should s"have a latest date received of $expectedDateReceivedDisplay" in {
      if (answerAssertionInfo.dateReceived.isDefined) {
        assert(answer.get.latestDateReceived === answerAssertionInfo.dateReceived)
      } else {
        assert(answer.get.latestDateReceived.isEmpty)
      }
    }

    it should s"have a timestamp that is almost exactly ${Instant.now()}" in {
      val expectedTimestamp = Instant.now()
      val actualTimestamp = answer.get.scrapedTimestamp

      val difference = Duration.between(expectedTimestamp, actualTimestamp).abs()

      assert(difference.toMillis < Duration.ofSeconds(1).toMillis)
    }
  })
}
