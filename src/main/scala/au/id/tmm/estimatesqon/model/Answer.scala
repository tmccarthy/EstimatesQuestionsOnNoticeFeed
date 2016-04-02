package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.{Instant, LocalDate}
import java.util.Date

import au.id.tmm.estimatesqon.utils.DateUtils.ImprovedLocalDate

case class Answer protected (estimates: Estimates,
                             qonIdentifier: String,
                             scrapedTimestamp: Instant,

                             divisionOrAgency: Option[String],
                             senator: Option[String],
                             topic: Option[String],
                             pdfURLs: Seq[URL],
                             latestDateReceived: Option[LocalDate]
                            ) {

  def hasDifferentQONIdentifierTo(that: Answer): Boolean = {
    qonIdentifier != that.qonIdentifier
  }

  def hasDifferentAnswerDetailsTo(that: Answer): Boolean = {
    divisionOrAgency != that.divisionOrAgency ||
      senator != that.senator ||
      topic != that.topic ||
      pdfURLs != that.pdfURLs ||
      latestDateReceived != that.latestDateReceived
  }

  val latestDateReceivedOldDateType: Option[Date] = latestDateReceived
    .map(_.toOldDateAtZone(Estimates.estimatesTimeZone))
}

object Answer {
  // TODO delete?
  def create(estimates: Estimates,
             qonNumber: String,
             scrapedTimestamp: Instant,
             divisionOrAgency: Option[String],
             senator: Option[String],
             topic: Option[String],
             pdfURLs: Seq[URL],
             datesReceived: Set[LocalDate]): Answer = {
    new Answer(estimates,
      qonNumber,
      scrapedTimestamp,
      divisionOrAgency,
      senator,
      topic,
      pdfURLs,
      extractLatestFrom(datesReceived))
  }

  def create(estimates: Estimates,
             qonNumber: String,
             scrapedTimestamp: Instant,
             divisionOrAgency: Option[String],
             senator: Option[String],
             topic: Option[String],
             pdfURLs: Seq[URL],
             latestDateReceived: Option[LocalDate]): Answer = {
    new Answer(estimates,
      qonNumber,
      scrapedTimestamp,
      divisionOrAgency,
      senator,
      topic,
      pdfURLs,
      latestDateReceived)
  }

  private def extractLatestFrom(dates: Set[LocalDate]): Option[LocalDate] = {
    if (dates.isEmpty) {
      None
    } else {
      Some(dates.max(localDateOrdering))
    }
  }

  private val localDateOrdering = new Ordering[LocalDate] {
    override def compare(left: LocalDate, right: LocalDate): Int = {
      if (left.isBefore(right)) {
        -1
      } else if (left.isAfter(right)) {
        1
      } else {
        0
      }
    }
  }
}