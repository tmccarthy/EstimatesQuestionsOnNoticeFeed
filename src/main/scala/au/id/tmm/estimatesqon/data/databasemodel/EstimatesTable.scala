package au.id.tmm.estimatesqon.data.databasemodel

import java.sql.Date
import java.time.{ZoneId, LocalDate, Period}

import slick.lifted.Tag
import slick.driver.SQLiteDriver.api._

private[data] case class EstimatesRow(estimatesID: Long,
                        portfolioName: String,
                        description: String,
                        firstDay: Date,
                        lastDay: Date,
                        pageURL: String) {
  lazy val firstDayLocalDate: LocalDate = firstDay.toInstant.atZone(ZoneId.of("AET")).toLocalDate

  lazy val lastDayLocalDate: LocalDate = lastDay.toInstant.atZone(ZoneId.of("AET")).toLocalDate

  lazy val hearingDates: Period = Period.between(firstDayLocalDate, lastDayLocalDate)
}

class EstimatesTable(tag: Tag) extends Table[EstimatesRow](tag, "Estimates") {

  def estimatesID = column[Long]("estimatesID", O.PrimaryKey, O.AutoInc)
  def portfolioName = column[String]("portfolioID")
  def description = column[String]("description")
  def firstDay = column[Date]("firstDay")
  def lastDay = column[Date]("lastDay")
  def pageURL = column[String]("pageURL")

  def answerUpdates = foreignKey("ANSWER_UPDATES_FK", estimatesID, TableQuery[AnswersTable])(_.estimatesID)

  def pageQueries = foreignKey("PAGE_QUERIES_FK", estimatesID, TableQuery[PageQueriesTable])(_.pageQueryID)

  def * = (estimatesID, portfolioName, description, firstDay, lastDay, pageURL) <> (EstimatesRow.tupled, EstimatesRow.unapply)
}
