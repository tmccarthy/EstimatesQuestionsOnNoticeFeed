package au.id.tmm.estimatesqon.data.databasemodel

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

case class AnswerUpdate(answerUpdateID: Long,
                        timestamp: Long,
                        estimatesID: Long,
                        qonNumber: String,
                        updateType: String,
                        oldDivision: String,
                        oldSenator: String,
                        oldTopic: String,
                        oldPDFLinksBundleID: Long,
                        oldDatesReceivedListID: Long,
                        newDivision: String,
                        newSenator: String,
                        newTopic: String,
                        newPDFLinksBundleID: Long,
                        newDatesReceivedListID: Long)

class AnswerUpdates(tag: Tag) extends Table[AnswerUpdate](tag, "AnswerUpdates") {

  def answerUpdateID = column[Long]("answerUpdateID", O.PrimaryKey)
  def timestamp = column[Long]("timestamp")
  def estimatesID = column[Long]("estimatesID")
  def qonNumber = column[String]("qonNumber")
  def updateType = column[String]("updateType")
  def oldDivision = column[String]("oldDivision")
  def oldSenator = column[String]("oldSenator")
  def oldTopic = column[String]("oldTopic")
  def oldPDFLinksBundleID = column[Long]("oldPDFLinksBundleID")
  def oldDatesReceivedListID = column[Long]("oldDatesReceivedListID")
  def newDivision = column[String]("newDivision")
  def newSenator = column[String]("newSenator")
  def newTopic = column[String]("newTopic")
  def newPDFLinksBundleID = column[Long]("newPDFLinksBundleID")
  def newDatesReceivedListID = column[Long]("newDatesReceivedListID")

  def * = (answerUpdateID,
    timestamp,
    estimatesID,
    qonNumber,
    updateType,
    oldDivision,
    oldSenator,
    oldTopic,
    oldPDFLinksBundleID,
    oldDatesReceivedListID,
    newDivision,
    newSenator,
    newTopic,
    newPDFLinksBundleID,
    newDatesReceivedListID) <> (AnswerUpdate.tupled, AnswerUpdate.unapply)
}
