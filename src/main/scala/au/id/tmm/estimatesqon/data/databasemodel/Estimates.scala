package au.id.tmm.estimatesqon.data.databasemodel

import java.sql.Date

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

case class SingleEstimates(estimatesID: Long, portfolioID: Long, description: String, firstDay: Date, dateSetID: Long)

class Estimates(tag: Tag) extends Table[SingleEstimates](tag, "Estimates") {

  def estimatesID = column[Long]("estimatesID", O.PrimaryKey, O.AutoInc)
  def portfolioID = column[Long]("portfolioID")
  def description = column[String]("description")
  def firstDay = column[Date]("firstDay")
  def dateSetID = column[Long]("dateSetID")

  def * = (estimatesID, portfolioID, description, firstDay, dateSetID) <> (SingleEstimates.tupled, SingleEstimates.unapply)

}
