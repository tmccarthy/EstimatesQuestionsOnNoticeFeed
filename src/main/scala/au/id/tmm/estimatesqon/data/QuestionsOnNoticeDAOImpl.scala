package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.data.databasemodel._
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdateBundle, Estimates, QuestionsOnNoticePage}
import slick.driver.SQLiteDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.{ExecutionContext, Future}

class QuestionsOnNoticeDAOImpl extends QuestionsOnNoticeDAO {
  import ExecutionContext.Implicits.global

  val database = Database.forConfig("sqlite")

  override private[data] def initialise(): Future[Unit] = {
    tablesExist().flatMap(tablesExist => if (!tablesExist) createTables() else Future())
  }

  protected def tablesExist(): Future[Boolean] = {
    database.run(MTable.getTables).map(tables => tables.nonEmpty)
  }

  protected def createTables(): Future[Unit] = {
    val answerUpdates = TableQuery[AnswerUpdates]
    val dateSets = TableQuery[DateSets]
    val estimates = TableQuery[databasemodel.Estimates]
    val pageQueries = TableQuery[PageQueries]
    val pdfLinkBundles = TableQuery[PDFLinkBundles]
    val portfolios = TableQuery[Portfolios]

    val createTablesAction = (dateSets.schema ++
      portfolios.schema ++
      estimates.schema ++
      pageQueries.schema ++
      pdfLinkBundles.schema ++
      answerUpdates.schema).create

    database.run(createTablesAction)
  }

  override def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit] = ???

  override def retrieveAnswers(estimates: Estimates): Future[Set[Answer]] = ???

  override def haveQueried(questionsOnNoticePage: QuestionsOnNoticePage): Future[Boolean] = ???
}
