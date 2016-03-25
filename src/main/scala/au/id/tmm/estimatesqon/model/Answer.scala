package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.LocalDate

case class Answer protected (val estimates: Estimates,
                        val qonIdentifier: String,

                        val divisionOrAgency: Option[String],
                        val senator: Option[String],
                        val topic: Option[String],
                        val pdfURLs: Seq[URL],
                        val latestDateReceived: Option[LocalDate]
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
}

object Answer {
  def create(estimates: Estimates,
             qonNumber: String,
             divisionOrAgency: Option[String],
             senator: Option[String],
             topic: Option[String],
             pdfURLs: Seq[URL],
             datesReceived: Set[LocalDate]): Answer = {
    new Answer(estimates, qonNumber, divisionOrAgency, senator, topic, pdfURLs, extractLatestFrom(datesReceived))
  }

  def create(estimates: Estimates,
             qonNumber: String,
             divisionOrAgency: Option[String],
             senator: Option[String],
             topic: Option[String],
             pdfURLs: Seq[URL],
             latestDateReceived: Option[LocalDate]): Answer = {
    new Answer(estimates, qonNumber, divisionOrAgency, senator, topic, pdfURLs, latestDateReceived)
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