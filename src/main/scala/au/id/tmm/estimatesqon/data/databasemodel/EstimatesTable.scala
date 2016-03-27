package au.id.tmm.estimatesqon.data.databasemodel

import java.net.URL
import java.sql.Date

import au.id.tmm.estimatesqon.model.{Estimates, Portfolio}
import slick.driver.SQLiteDriver.api._
import slick.lifted.Tag

private[data] case class EstimatesRow(estimatesID: Long,
                        portfolioName: String,
                        description: String,
                        firstDay: Date,
                        lastDay: Date,
                        pageURL: String)

class EstimatesTable(tag: Tag) extends Table[EstimatesRow](tag, "Estimates") {

  def estimatesID = column[Long]("estimatesID", O.PrimaryKey, O.AutoInc)
  def portfolioName = column[String]("portfolioID")
  def description = column[String]("description")
  def firstDay = column[Date]("firstDay")
  def lastDay = column[Date]("lastDay")
  def pageURL = column[String]("pageURL")

  def joinedAnswers = foreignKey("ANSWERS_FK", estimatesID, TableQuery[AnswersTable])(_.estimatesID)

  def naturalIndex = index("IDX_NATURAL", (portfolioName, firstDay), unique = true)

  def * = (estimatesID, portfolioName, description, firstDay, lastDay, pageURL) <> (EstimatesRow.tupled, EstimatesRow.unapply)
}
