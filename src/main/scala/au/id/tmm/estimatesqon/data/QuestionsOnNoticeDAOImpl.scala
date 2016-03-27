package au.id.tmm.estimatesqon.data

import java.sql.Date
import java.time.Instant

import au.id.tmm.estimatesqon.data.databasemodel._
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdateBundle, Estimates}
import slick.driver.SQLiteDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class QuestionsOnNoticeDAOImpl protected (dbConfigName: String) extends QuestionsOnNoticeDAO {

  val database = Database.forConfig(dbConfigName)

  override private[data] def initialiseIfNeeded(): Future[Unit] = {
    isInitialised.flatMap(alreadyInitialised => if (!alreadyInitialised) initialise() else Future(Unit))
  }

  protected def isInitialised: Future[Boolean] = {
    database.run(MTable.getTables).map(tables => tables.nonEmpty)
  }

  protected def initialise(): Future[Unit] = {

    val initialiseAction = createTablesAction()

    val run: Future[Unit] = database.run(initialiseAction)

    run
  }

  protected def createTablesAction(): DBIO[Unit] = {
    val answerUpdates = TableQuery[AnswersTable]
    val estimates = TableQuery[databasemodel.EstimatesTable]
    val pdfLinkBundles = TableQuery[PDFLinkBundlesTable]

    (estimates.schema ++
      pdfLinkBundles.schema ++
      answerUpdates.schema).create
  }

  override def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit] = ???

  override def registerEstimates(estimates: Estimates): Future[Unit] = {
    val newRow: EstimatesRow = EstimatesRow(-1,
      estimates.portfolio.name,
      estimates.description,
      new Date(estimates.firstDayOldDateType.getTime),
      new Date(estimates.lastDayOldDateType.getTime),
      estimates.pageURL.toString)

    val query = TableQuery[EstimatesTable] += newRow

    database.run(query).map(_ => Unit)
  }

  override def listEstimates: Future[Set[Estimates]] = {
    val query = TableQuery[EstimatesTable]

    val rowsFuture: Future[Seq[EstimatesRow]] = database.run(query.result)

    rowsFuture.map(_.map(RowToModelConversions.estimatesFromDbRow).toSet)
  }

  override def haveEverQueried(estimates: Estimates): Future[Boolean] = {
    val query = TableQuery[AnswersTable]
      .filter(_.joinedEstimates.filter(_.pageURL === estimates.pageURL.toString).exists)
      .exists

    database.run(query.result)
  }

  override def retrieveLatestAnswersFor(estimates: Estimates): Future[Set[Answer]] = ???
}

object QuestionsOnNoticeDAOImpl {
  def forConfigName(dbConfigName: String): QuestionsOnNoticeDAOImpl = new QuestionsOnNoticeDAOImpl(dbConfigName)
}