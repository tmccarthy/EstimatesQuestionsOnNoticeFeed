package au.id.tmm.estimatesqon.controller.scraping

import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import au.id.tmm.estimatesqon.utils.DateUtils
import au.id.tmm.estimatesqon.utils.ElementUtils.InstanceElementUtils
import au.id.tmm.estimatesqon.utils.StringUtils._
import net.ruippeixotog.scalascraper.dsl.DSL._
import org.jsoup.nodes.Element

private [scraping] class AnswerColumnInfo protected (val qonNumberCol: Option[Int],
                                                       val divisionOrAgencyCol: Option[Int],
                                                       val senatorCol: Option[Int],
                                                       val topicCol: Option[Int],
                                                       val pdfsCol: Option[Int],
                                                       val dateCol: Option[Int]
                                                      ) {

  private val primaryDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
  private val secondaryDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
  private val tertiaryDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")

  def extractQONNumber: Element => Option[String] = extractCellValueFromText(qonNumberCol)(string => string.trim.replaceAll("\u00a0", ""))

  def extractDivisionOrAgency: Element => Option[String] = extractCellValueFromText(divisionOrAgencyCol)(string => string.trim.replaceAll("\u00a0", ""))

  def extractSenator: Element => Option[String] = extractCellValueFromText(senatorCol)(string => string.trim.replaceAll("\u00a0", ""))

  def extractTopic: Element => Option[String] = extractCellValueFromText(topicCol)(string => string.trim.replaceAll("\u00a0", ""))

  def extractPDFs: Element => Option[List[URL]] = extractCellValue(pdfsCol)(cell => {
    val linkElements: List[Element] = cell.descendents.filter(_.tagName == "a").toList

    val pdfs: List[URL] = linkElements.flatMap(pdfLinkFromLinkElement)

    pdfs
  })

  private def pdfLinkFromLinkElement(linkElement: Element): Option[URL] = {
    Option.apply(linkElement.attr("href"))
      .filter(_.endsWith(".pdf"))
      .map(linkString => new URL("http://www.aph.gov.au" + linkString))
  }

  def extractDates: Element => Option[Set[LocalDate]] = extractCellValueFromText(dateCol)(string => {
    val dateText: String = string.replaceAll("\u00a0", "")

    if (dateText.isEmpty) {
      Set.empty[LocalDate]
    } else {

      val dateStrings: Stream[String] = dateText.split("&").toStream.map(_.trim)

      val dates: Stream[LocalDate] = dateStrings
        .flatMap(dateString => DateUtils.parseDate(dateString,
          primaryDateFormat, secondaryDateFormat, tertiaryDateFormat))

      Set(dates: _*)
    }
  })

  private def extractCellValue[T](column: Option[Int])(fromElement: Element => T)(tableRow: Element): Option[T] = {
    column
      .map(columnIndex => tableRow.child(columnIndex))
      .map(fromElement)
  }

  private def extractCellValueFromText[T](column: Option[Int])(fromString: String => T)(tableRow: Element): Option[T] =
    extractCellValue(column)(element => fromString(element.text.trim))(tableRow)
}

private[scraping] object AnswerColumnInfo {

  def determineFromHeaderRow(headerRowElement: Element): AnswerColumnInfo = {

    val columnHeadings: List[String] = headerRowElement.children().map(_.text.trim).toList

    var qonNumberCol: Option[Int] = determineQONNumberCol(columnHeadings)
    var divisionOrAgencyCol: Option[Int] = determineDivisionOrAgencyCol(columnHeadings)
    var senatorCol: Option[Int] = determineSenatorCol(columnHeadings)
    var topicCol: Option[Int] = determineTopicCol(columnHeadings)
    var pdfsCol: Option[Int] = determinePDFsCol(columnHeadings)
    var dateCol: Option[Int] = determineDateCol(columnHeadings)

    val undeterminedColumns: Set[Option[Int]] =
      Stream(qonNumberCol, divisionOrAgencyCol, senatorCol, topicCol, pdfsCol, dateCol)
        .collect { case columnOptional if columnOptional.isEmpty => columnOptional }
        .toSet

    val leftOverColumnIndices = columnHeadings.indices.filterNot(columnIndex => {
      qonNumberCol.contains(columnIndex) ||
        divisionOrAgencyCol.contains(columnIndex) ||
        senatorCol.contains(columnIndex) ||
        topicCol.contains(columnIndex) ||
        pdfsCol.contains(columnIndex) ||
        dateCol.contains(columnIndex)
    })

    if (leftOverColumnIndices.size == 1 && undeterminedColumns.size == 1) {
      val leftOverColumnIndex = leftOverColumnIndices.head

      if (qonNumberCol.isEmpty) qonNumberCol = Option.apply(leftOverColumnIndex)
      if (divisionOrAgencyCol.isEmpty) divisionOrAgencyCol = Option.apply(leftOverColumnIndex)
      if (senatorCol.isEmpty) senatorCol = Option.apply(leftOverColumnIndex)
      if (topicCol.isEmpty) topicCol = Option.apply(leftOverColumnIndex)
      if (pdfsCol.isEmpty) pdfsCol = Option.apply(leftOverColumnIndex)
      if (dateCol.isEmpty) dateCol = Option.apply(leftOverColumnIndex)
    }

    new AnswerColumnInfo(qonNumberCol, divisionOrAgencyCol, senatorCol, topicCol, pdfsCol, dateCol)
  }

  private def columnWithName(columnHeadings: List[String], possibleNames: String*): Option[Int] =
    columnMatching(columnHeadings, heading => heading.containsAnyWordIgnoreCase(possibleNames:_*))

  private def columnMatching(columnHeadings: List[String], predicate: String => Boolean): Option[Int] = {
    val matchingColumnHeading = columnHeadings
      .find(predicate)

    matchingColumnHeading
      .map(columnHeadings.indexOf(_))
      .filterNot(_ == -1)
  }

  private def determineQONNumberCol(columnHeadings: List[String]): Option[Int] = {
    columnWithName(columnHeadings, "QON No", "QON Number", "EQON Number")
  }

  private def determineDivisionOrAgencyCol(columnHeadings: List[String]): Option[Int] = {
    columnWithName(columnHeadings, "Division", "Group", "Agency")
  }

  private def determineSenatorCol(columnHeadings: List[String]): Option[Int] = {
    columnWithName(columnHeadings, "Senator")
  }

  private def determineTopicCol(columnHeadings: List[String]): Option[Int] = {
    columnWithName(columnHeadings, "Topic", "Subject")
  }

  private def determinePDFsCol(columnHeadings: List[String]): Option[Int] = {
    columnWithName(columnHeadings, "View", "Answer")
  }

  private def determineDateCol(columnHeadings: List[String]): Option[Int] = {
    columnWithName(columnHeadings, "Date Answer Received", "Received", "Date Received")
  }
}