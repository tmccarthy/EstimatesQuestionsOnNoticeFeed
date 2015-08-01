package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.LocalDate

class Answer protected (val estimates: Estimates,

                        val qonNumber: Int,
                        val divisionOrAgency: String,
                        val senator: String,
                        val topic: String,
                        val pdfURLs: Seq[URL],
                        val datesReceived: Seq[LocalDate]

                         ) extends Ordered[Answer] {

  override def compare(that: Answer): Int = qonNumber.compare(that.qonNumber)
}

object Answer {
  def create(estimates: Estimates,
             qonNumber: Int,
             divisionOrAgency: String,
             senator: String,
             topic: String,
             pdfURLs: Seq[URL],
             dateReceived: Seq[LocalDate]): Answer = {
    new Answer(estimates, qonNumber, divisionOrAgency, senator, topic, pdfURLs, dateReceived)
  }
}