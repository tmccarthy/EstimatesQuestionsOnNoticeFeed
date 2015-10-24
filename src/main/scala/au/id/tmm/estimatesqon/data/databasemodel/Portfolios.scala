package au.id.tmm.estimatesqon.data.databasemodel

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

case class Portfolio(portfolioID: Long, name: String)

class Portfolios(tag: Tag) extends Table[Portfolio](tag, "Portfolios") {
  def portfolioID = column[Long]("portfolioID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")

  def * = (portfolioID, name) <> (Portfolio.tupled, Portfolio.unapply)
}
