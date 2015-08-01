package au.id.tmm.estimatesqon.controller

import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import au.id.tmm.estimatesqon.model.{Answer, Estimates}
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.immutable.SortedSet
import scala.io.Source

class EstimatesScraper protected (val estimates: Estimates) {

  val dateParsingPattern: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

  def extractAnswers(page: Source): SortedSet[Answer] = {
    val htmlAsString: String = page.getLines().fold("")((left, right) => left + "\n" + right)

    val browser: Browser = new Browser
    val document: Document = browser.parseString(htmlAsString)

    val h3Elements: Elements = document >> elements("h3")
    val questionsOnNoticeHeading: Element = h3Elements.filter(_.text.trim == "Answers to Questions on Notice").head

    val questionsOnNoticeHeadingIndex: Int = document.getAllElements.indexOf(questionsOnNoticeHeading)

    val questionsOnNoticeTable: Element = document.getAllElements.get(questionsOnNoticeHeadingIndex + 1)

    val rows: Elements = questionsOnNoticeTable.children.first.children

    val answers: Stream[Answer] = rows.flatMap(answerFromRow).toStream

    SortedSet(answers: _*)
  }

  def answerFromRow(questionsOnNoticeTableRow: Element): Option[Answer] = {

    if (isHeaderRow(questionsOnNoticeTableRow)) return None

    val qonNumber: Int = extractQONNumber(questionsOnNoticeTableRow)
    val divisionOrAgency: String = extractDivisionOrAgency(questionsOnNoticeTableRow)
    val senator: String = extractSenator(questionsOnNoticeTableRow)
    val topic: String = extractTopic(questionsOnNoticeTableRow)
    val pdfs: Seq[URL] = extractPDFs(questionsOnNoticeTableRow)
    val date: Seq[LocalDate] = extractDates(questionsOnNoticeTableRow)

    val returnedAnswer: Answer = Answer.create(
      estimates,
      qonNumber,
      divisionOrAgency,
      senator,
      topic,
      pdfs,
      date)

    Option.apply(returnedAnswer)
  }

  def isHeaderRow(questionsOnNoticeTableRow: Element): Boolean = {
    val firstColumnOnThisRow: String = questionsOnNoticeTableRow.children.first.tagName.toLowerCase.trim

    val isHeaderRow: Boolean = firstColumnOnThisRow == "th"
    isHeaderRow
  }

  def extractQONNumber(questionsOnNoticeTableRow: Element): Int = {
    val qonNumberRow: Element = questionsOnNoticeTableRow.child(0)
    val qonNumber: Int = qonNumberRow.text.toInt
    qonNumber
  }

  def extractDivisionOrAgency(questionsOnNoticeTableRow: Element): String = {
    val divisionOrAgencyRow: Element = questionsOnNoticeTableRow.child(1)
    val divisionOrAgency: String = divisionOrAgencyRow.text.trim
    divisionOrAgency
  }

  def extractSenator(questionsOnNoticeTableRow: Element): String = {
    val senatorRow: Element = questionsOnNoticeTableRow.child(2)
    val senator: String = senatorRow.text.trim
    senator
  }

  def extractTopic(questionsOnNoticeTableRow: Element): String = {
    val topicRow: Element = questionsOnNoticeTableRow.child(3)
    val topic: String = topicRow.text.trim
    topic
  }

  def extractPDFs(questionsOnNoticeTableRow: Element): Seq[URL] = {
    val pdfsRow: Element = questionsOnNoticeTableRow.child(5)

    val linkElements: List[Element] = pdfsRow.children().filter(_.tagName == "a").toList

    val pdfs: Seq[URL] = linkElements.flatMap(pdfLinkFromLinkElement).toSeq

    pdfs
  }

  def pdfLinkFromLinkElement(linkElement: Element): Option[URL] = {
    Option.apply(linkElement.attr("href"))
      .filter(_.endsWith(".pdf"))
      .map(linkString => new URL("http://www.aph.gov.au" + linkString))
  }

  def extractDates(questionsOnNoticeTableRow: Element): Seq[LocalDate] = {
    val dateRow: Element = questionsOnNoticeTableRow.child(6)
    val dateText: String = dateRow.text.trim.replaceAll("\u00a0", "")

    if (dateText.isEmpty) {
      Seq.empty
    } else {

      val dateStrings: Stream[String] = dateText.split("&").toStream

      val dates: Stream[LocalDate] = dateStrings.map(dateString => LocalDate.parse(dateString.trim, dateParsingPattern))

      dates.toSeq
    }
  }
}

object EstimatesScraper {
  def forEstimates(estimates: Estimates) = new EstimatesScraper(estimates)
}
