package au.id.tmm.estimatesqon.controller.scraping

import java.time.Instant

import au.id.tmm.estimatesqon.model.{Answer, Estimates}
import au.id.tmm.estimatesqon.utils.StringUtils.InstanceStringUtils
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.io.Source

object EstimatesScraperImpl {

  def scrapeFrom(estimates: Estimates): List[Answer] = {
    val timestamp = Instant.now()

    val document: Document = retrieveEstimatesPage(estimates)

    val questionsOnNoticeTable: Option[Element] = findQuestionsOnNoticeTable(document)

    val tableRows: Option[Elements] = questionsOnNoticeTable.map(extractRowsFrom)

    val answers: Option[List[Answer]] = tableRows.map(answersFromTableRows(_, timestamp, estimates))

    answers.getOrElse(List.empty)
  }

  private def retrieveEstimatesPage(estimates: Estimates): Document = {
    val pageSource: Source = Source.fromURL(estimates.pageURL)
    val htmlAsString: String = pageSource.getLines().fold("")((left, right) => left + "\n" + right)

    val browser: Browser = new Browser
    val document: Document = browser.parseString(htmlAsString)
    document
  }

  private def findQuestionsOnNoticeTable(document: Document): Option[Element] = {
    val h3Elements: Elements = document >> elements("h3")
    val questionsOnNoticeHeading = h3Elements.find(_.text.containsAnyIgnoreCase("Answers to Questions on Notice",
      "Questions on notice"))

    val allElements: Elements = document.getAllElements

    val qONHeadingElementIndex = questionsOnNoticeHeading.map(allElements.indexOf(_))

    val elementsAfterQONHeading: Option[Stream[Element]] = qONHeadingElementIndex.map(allElements.drop(_).toStream)

    val questionsOnNoticeTable: Option[Element] = elementsAfterQONHeading
      .flatMap(elements => elements.find(_.tagName() == "table"))

    questionsOnNoticeTable
  }

  private def extractRowsFrom(questionsOnNoticeTable: Element): Elements = questionsOnNoticeTable.children.first.children

  private def answersFromTableRows(tableRows: Elements, timestamp: Instant, estimates: Estimates): List[Answer] = {
    val headerRow: Element = tableRows.head
    val answerColumnInfo: AnswerColumnInfo = AnswerColumnInfo.determineFromHeaderRow(headerRow)

    val contentRows = tableRows.drop(1)

    val answers: Stream[Answer] = contentRows
      .map(answerFromContentRow(answerColumnInfo, _, timestamp, estimates))
      .toStream
      .flatten

    answers.toList
  }

  private def answerFromContentRow(answerColumnInfo: AnswerColumnInfo,
                                   questionsOnNoticeTableRow: Element,
                                   timestamp: Instant,
                                   estimates: Estimates): Option[Answer] = {

    val qonNumber        = answerColumnInfo.extractQONNumber(questionsOnNoticeTableRow)
    val divisionOrAgency = answerColumnInfo.extractDivisionOrAgency(questionsOnNoticeTableRow)
    val senator          = answerColumnInfo.extractSenator(questionsOnNoticeTableRow)
    val topic            = answerColumnInfo.extractTopic(questionsOnNoticeTableRow)
    val pdfs             = answerColumnInfo.extractPDFs(questionsOnNoticeTableRow).getOrElse(List.empty)

    val datesReceived = answerColumnInfo
      .extractDates(questionsOnNoticeTableRow)
      .getOrElse(Set.empty)

    if (qonNumber.isDefined) {
      val answer = Answer.create(
        estimates,
        qonNumber.get,
        timestamp,
        divisionOrAgency,
        senator,
        topic,
        pdfs,
        datesReceived)

      Some(answer)
    } else {
      None
    }
  }
}
