package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.data.databasemodel._
import au.id.tmm.estimatesqon.model.{Estimates, Answer, AnswerUpdateBundle}
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
    val pageQueries = TableQuery[PageQueriesTable]
    val pdfLinkBundles = TableQuery[PDFLinkBundlesTable]

    (estimates.schema ++
      pageQueries.schema ++
      pdfLinkBundles.schema ++
      answerUpdates.schema).create
  }

  override def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit] = ???

  override def recordEstimates(estimates: Estimates): Future[Unit] = ???

  override def haveEverQueried(estimates: Estimates): Future[Boolean] = ???

  override def retrieveAnswers(estimates: Estimates): Future[Set[Answer]] = ???
}

object QuestionsOnNoticeDAOImpl {
  def forConfigName(dbConfigName: String): QuestionsOnNoticeDAOImpl = new QuestionsOnNoticeDAOImpl(dbConfigName)
}