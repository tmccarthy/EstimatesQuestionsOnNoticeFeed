package au.id.tmm.estimatesqon.data.databasemodel

import java.sql.Date

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

private[data] case class AnswerRow(answerID: Long,
                                   estimatesID: Long,

                                   queryTimestamp: Long,
                                   updateType: String,
                                   qonNumber: String,

                                   division: Option[String],
                                   senator: Option[String],
                                   topic: Option[String],
                                   latestDateReceived: Option[Date],

                                   pdfLinksBundleID: Option[Long])

class AnswersTable(tag: Tag) extends Table[AnswerRow](tag, "Answers") {

  def answerID = column[Long]("answerID", O.PrimaryKey)
  def estimatesID = column[Long]("estimatesID")

  def queryTimestamp = column[Long]("queryTimestamp")
  def updateType = column[String]("updateType")
  def qonNumber = column[String]("qonNumber")

  def division = column[Option[String]]("division")
  def senator = column[Option[String]]("senator")
  def topic = column[Option[String]]("topic")
  def latestDateReceived = column[Option[Date]]("datesReceivedListID")

  def pdfLinksBundleID = column[Option[Long]]("pdfLinksBundleID")

  def joinedEstimates = foreignKey("ESTIMATES_FK", estimatesID, TableQuery[EstimatesTable])(_.estimatesID)

  def joinedPdfLinksBundle = foreignKey("PDFS_FK", pdfLinksBundleID, TableQuery[PDFLinkBundlesTable])(_.pdfBundleID.?)

  def * = (answerID,
    estimatesID,

    queryTimestamp,
    updateType,
    qonNumber,

    division,
    senator,
    topic,
    latestDateReceived,
    pdfLinksBundleID) <> (AnswerRow.tupled, AnswerRow.unapply)
}
