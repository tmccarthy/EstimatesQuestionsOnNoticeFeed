package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.LocalDate

class Answer protected (val estimates: Estimates,
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

  def canEqual(other: Any): Boolean = other.isInstanceOf[Answer]

  override def equals(other: Any): Boolean = other match {
    case that: Answer =>
      (that canEqual this) &&
        estimates == that.estimates &&
        qonIdentifier == that.qonIdentifier &&
        divisionOrAgency == that.divisionOrAgency &&
        senator == that.senator &&
        topic == that.topic &&
        pdfURLs == that.pdfURLs &&
        datesReceived == that.datesReceived
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(estimates, qonIdentifier, divisionOrAgency, senator, topic, pdfURLs, datesReceived)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
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