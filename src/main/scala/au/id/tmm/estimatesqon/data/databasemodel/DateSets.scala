package au.id.tmm.estimatesqon.data.databasemodel

import java.sql.Date

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

case class DateSet(dateSetID: Long, dateID: Long, date: Date)

class DateSets(tag: Tag) extends Table[DateSet](tag, "DateSets") {
  def dateSetID = column[Long]("dateBundleID")
  def dateID = column[Long]("dateID")
  def date = column[Date]("date")

  def primaryKey = primaryKey("DateSets_primary_key", (dateSetID, dateID))

  def * = (dateSetID, dateID, date) <> (DateSet.tupled, DateSet.unapply)
}