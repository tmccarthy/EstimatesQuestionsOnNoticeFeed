package au.id.tmm.estimatesqon.data.databasemodel

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

private[data] case class AnswerRow(answerID: Long,
                                   pageQueryID: Long,
                                   estimatesID: Long,

                                   updateType: String,
                                   qonNumber: String,

                                   division: Option[String],
                                   senator: Option[String],
                                   topic: Option[String],
                                   pdfLinksBundleID: Option[Long],
                                   datesReceivedListID: Option[Long])

class AnswersTable(tag: Tag) extends Table[AnswerRow](tag, "Answers") {

  def answerID = column[Long]("answerID", O.PrimaryKey)
  def pageQueryID = column[Long]("pageQueryID")
  def estimatesID = column[Long]("estimatesID")

  def updateType = column[String]("updateType")
  def qonNumber = column[String]("qonNumber")

  def division = column[Option[String]]("division")
  def senator = column[Option[String]]("senator")
  def topic = column[Option[String]]("topic")
  def pdfLinksBundleID = column[Option[Long]]("pdfLinksBundleID")
  def datesReceivedListID = column[Option[Long]]("datesReceivedListID") // TODO this concept needs rethinking

  def estimates = foreignKey("ESTIMATES_FK", estimatesID, TableQuery[EstimatesTable])(_.estimatesID)

  def pageQuery = foreignKey("PAGE_QUERY_FK", pageQueryID, TableQuery[PageQueriesTable])(_.pageQueryID)

  def pdfLinksBundle = foreignKey("PDFS_FK", pdfLinksBundleID, TableQuery[PDFLinkBundlesTable])(_.pdfBundleID.?)

  def * = (answerID,
    pageQueryID,
    estimatesID,

    updateType,
    qonNumber,

    division,
    senator,
    topic,
    pdfLinksBundleID,
    datesReceivedListID) <> (AnswerRow.tupled, AnswerRow.unapply)
}
