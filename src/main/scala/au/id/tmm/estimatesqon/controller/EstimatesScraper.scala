package au.id.tmm.estimatesqon.controller

import java.net.URL
import java.time.LocalDate

import au.id.tmm.estimatesqon.data.databasemodel.{AnswerRow, EstimatesRow}
import au.id.tmm.estimatesqon.model.{Answer, Estimates}
import au.id.tmm.estimatesqon.utils.StringUtils
import au.id.tmm.estimatesqon.utils.StringUtils.InstanceStringUtils
import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.collection.SortedSet
import scala.io.Source

private[controller] class EstimatesScraper protected (val estimates: Estimates) {

  def extractAnswers: Seq[Answer] = {
    val pageSource: Source = Source.fromURL(estimates.pageURL)

    val htmlAsString: String = pageSource.getLines().fold("")((left, right) => left + "\n" + right)

    val browser: Browser = new Browser
    val document: Document = browser.parseString(htmlAsString)

    val questionsOnNoticeTable: Option[Element] = findQuestionsOnNoticeTable(document)

    if (questionsOnNoticeTable.isEmpty) return Seq.empty

    val tableRows: Elements = questionsOnNoticeTable.get.children.first.children

    val answers: Seq[Answer] = answersFromTableRows(tableRows)

    answers
  }

  private def findQuestionsOnNoticeTable(document: Document): Option[Element] = {
    val h3Elements: Elements = document >> elements("h3")
    val questionsOnNoticeHeading = h3Elements.find(_.text.containsAnyIgnoreCase("Answers to Questions on Notice", "Questions on notice"))

    val allElements: Elements = document.getAllElements

    val qONHeadingElementIndex = questionsOnNoticeHeading.map(allElements.indexOf(_))

    val elementsAfterQONHeading: Option[Stream[Element]] = qONHeadingElementIndex.map(allElements.drop(_).toStream)

    val questionsOnNoticeTable: Option[Element] = elementsAfterQONHeading.flatMap(elements => elements.find(_.tagName() == "table"))
    questionsOnNoticeTable
  }

  private def answersFromTableRows(tableRows: Elements): Seq[Answer] = {
    val headerRow: Element = tableRows.head
    val answerColumnInfo: AnswerColumnInfo = AnswerColumnInfo.determineFromHeaderRow(headerRow)

    val contentRows = tableRows.drop(1)

    val answers: Stream[Answer] = contentRows.map(answerFromContentRow(answerColumnInfo, _)).toStream.flatten

    answers
  }

  private def answerFromContentRow(answerColumnInfo: AnswerColumnInfo, questionsOnNoticeTableRow: Element): Option[Answer] = {

    val qonNumber: Option[String] = answerColumnInfo.extractQONNumber(questionsOnNoticeTableRow)
    val divisionOrAgency: Option[String] = answerColumnInfo.extractDivisionOrAgency(questionsOnNoticeTableRow)
    val senator: Option[String] = answerColumnInfo.extractSenator(questionsOnNoticeTableRow)
    val topic: Option[String] = answerColumnInfo.extractTopic(questionsOnNoticeTableRow)
    val pdfs: Seq[URL] = answerColumnInfo.extractPDFs(questionsOnNoticeTableRow).getOrElse(Seq.empty)

    val datesReceived: Set[_ <: LocalDate] = answerColumnInfo
      .extractDates(questionsOnNoticeTableRow)
      .getOrElse(Set.empty)

    if (qonNumber.isDefined) {
      val answer: Answer = Answer.create(
        estimates,
        qonNumber.get,
        divisionOrAgency,
        senator,
        topic,
        pdfs,
        datesReceived.lastOption)

      Some(answer)
    } else {
      None
    }
  }
}

private[controller] object EstimatesScraper {
  def forEstimates(estimates: Estimates) = new EstimatesScraper(estimates)
}
