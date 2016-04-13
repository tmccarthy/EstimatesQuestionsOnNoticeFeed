package au.id.tmm.estimatesqon.data.databasemodel

import java.net.URL
import java.time.Instant

import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, Estimates, Portfolio}
import au.id.tmm.estimatesqon.utils.DateUtils.ImprovedOldDate

object RowModelConversions {
  private val IGNORED_ID: Long = 0

  def estimatesFromDbRow(estimatesRow: EstimatesRow): Estimates = Estimates.create(
    portfolio   = Portfolio.withName(estimatesRow.portfolioName),
    description = estimatesRow.description,
    pageUrl     = new URL(estimatesRow.pageURL),
    firstDay    = estimatesRow.firstDay,
    lastDay     = estimatesRow.lastDay
  )

  def estimatesToDbRow(estimates: Estimates, estimatesID: Long = IGNORED_ID): EstimatesRow = EstimatesRow(
    estimatesID   = estimatesID,
    portfolioName = estimates.portfolio.name,
    description   = estimates.description,
    firstDay      = estimates.firstDayOldDateType.toSqlDate,
    lastDay       = estimates.lastDayOldDateType.toSqlDate,
    pageURL       = estimates.pageURL.toString)

  def answerUpdateToDbRow(answerUpdate: AnswerUpdate,
                          estimatesID: Long,
                          pdfLinkBundleId: Option[Long],
                          answerID: Long = IGNORED_ID): AnswerRow = {
    // If the new answer isn't present, this is a "removed" update, meaning the important part is the update type.
    // We just write the old details in that case.
    val answerDetailsToWrite = answerUpdate.newAnswer.orElse(answerUpdate.oldAnswer).get

    AnswerRow(
      answerID           = answerID,
      estimatesID        = estimatesID,
      queryTimestamp     = answerDetailsToWrite.scrapedTimestamp.toEpochMilli,
      updateType         = answerUpdate.updateType.toString,
      qonNumber          = answerDetailsToWrite.qonIdentifier,
      division           = answerDetailsToWrite.divisionOrAgency,
      senator            = answerDetailsToWrite.senator,
      topic              = answerDetailsToWrite.topic,
      latestDateReceived = answerDetailsToWrite.latestDateReceivedOldDateType.map(_.toSqlDate),
      pdfLinksBundleID   = pdfLinkBundleId)
  }

  def pdfLinkBundleRowsFrom(answer: Answer, bundleId: Long): List[PDFLinkBundleRow] =
    answer.pdfURLs.zipWithIndex
      .map { case (link, index) => PDFLinkBundleRow(bundleId, index, link.toString) }

  def composeAnswersFrom(estimates: Estimates,
                         answerRows: List[AnswerRow],
                         pdfBundleRows: List[PDFLinkBundleRow]): Set[Answer] = {
    val pdfsMap: Map[Long, List[URL]] = readIntoMap(pdfBundleRows)

    answerRows
      .map(row => constructAnswerFrom(estimates, row, pdfsMap))
      .toSet
  }

  private def readIntoMap(pdfBundleRows: List[PDFLinkBundleRow]): Map[Long, List[URL]] = {
    pdfBundleRows.groupBy(_.pdfBundleID)
      .map{
        case (bundleID, bundleRows) => {
          val pdfLinks = bundleRows
            .map(_.url)
            .map(new URL(_))

          (bundleID, pdfLinks)
        }
      }
  }

  private def constructAnswerFrom(estimates: Estimates,
                                  row: AnswerRow,
                                  pdfBundleLookup: Map[Long, List[URL]]): Answer = {
    Answer.create(
      estimates          = estimates,
      qonNumber          = row.qonNumber,
      scrapedTimestamp   = Instant.ofEpochMilli(row.queryTimestamp),
      divisionOrAgency   = row.division,
      senator            = row.senator,
      topic              = row.topic,
      pdfURLs            = row.pdfLinksBundleID.map(pdfBundleLookup(_)).getOrElse(List.empty),
      latestDateReceived = row.latestDateReceived.map(_.toLocalDateAtZone(Estimates.estimatesTimeZone)))
  }
}
