package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.LocalDate

case class Answer protected (val estimates: Estimates,
                        val qonIdentifier: String,

                        val divisionOrAgency: Option[String],
                        val senator: Option[String],
                        val topic: Option[String],
                        val pdfURLs: Seq[URL],
                        val datesReceived: Seq[LocalDate]
                         ) {

  def hasDifferentQONIdentifierTo(that: Answer): Boolean = {
    qonIdentifier != that.qonIdentifier
  }

  def hasDifferentAnswerDetailsTo(that: Answer): Boolean = {
    divisionOrAgency != that.divisionOrAgency ||
      senator != that.senator ||
      topic != that.topic ||
      pdfURLs != that.pdfURLs ||
      datesReceived != that.datesReceived
  }
}

object Answer {
  def create(estimates: Estimates,
             qonNumber: String,
             divisionOrAgency: Option[String],
             senator: Option[String],
             topic: Option[String],
             pdfURLs: Seq[URL],
             dateReceived: Seq[LocalDate]): Answer = {
    new Answer(estimates, qonNumber, divisionOrAgency, senator, topic, pdfURLs, dateReceived)
  }
}