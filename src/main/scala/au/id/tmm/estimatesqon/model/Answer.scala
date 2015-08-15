package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.LocalDate

class Answer protected (val estimates: Estimates,

                        val qonIdentifier: Option[String],
                        val divisionOrAgency: Option[String],
                        val senator: Option[String],
                        val topic: Option[String],
                        val pdfURLs: Seq[URL],
                        val datesReceived: Seq[LocalDate]
                         ) {
}

object Answer {
  def create(estimates: Estimates,
             qonNumber: Option[String],
             divisionOrAgency: Option[String],
             senator: Option[String],
             topic: Option[String],
             pdfURLs: Seq[URL],
             dateReceived: Seq[LocalDate]): Answer = {
    new Answer(estimates, qonNumber, divisionOrAgency, senator, topic, pdfURLs, dateReceived)
  }
}