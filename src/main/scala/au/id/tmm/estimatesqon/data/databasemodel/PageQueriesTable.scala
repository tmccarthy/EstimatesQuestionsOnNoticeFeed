package au.id.tmm.estimatesqon.data.databasemodel

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

private[data] case class PageQueryRow(pageQueryID: Long, url: String, timestamp: Long)

class PageQueriesTable(tag: Tag) extends Table[PageQueryRow](tag, "PageQueries") {
  def pageQueryID = column[Long]("pageQueryID", O.PrimaryKey)
  def url = column[String]("url")
  def timestamp = column[Long]("timestamp")

  def * = (pageQueryID, url, timestamp) <> (PageQueryRow.tupled, PageQueryRow.unapply)
}
