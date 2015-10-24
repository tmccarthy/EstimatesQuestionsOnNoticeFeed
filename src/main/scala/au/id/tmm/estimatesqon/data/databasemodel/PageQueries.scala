package au.id.tmm.estimatesqon.data.databasemodel

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

case class PageQuery(pageQueryID: Long, url: String, timestamp: Long)

class PageQueries(tag: Tag) extends Table[PageQuery](tag, "PageQueries") {
  def pageQueryID = column[Long]("pageQueryID", O.PrimaryKey)
  def url = column[String]
  def timestamp = column[Long]

  def * = (pageQueryID, url, timestamp) <> (PageQuery.tupled, PageQuery.unapply)
}
