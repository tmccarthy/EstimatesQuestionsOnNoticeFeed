package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.data.databasemodel._
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdateBundle, Estimates}
import slick.dbio
import slick.dbio.Effect.{All, Schema}
import slick.driver.SQLiteDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery
import slick.profile.FixedSqlAction

import scala.concurrent.{ExecutionContext, Future}
import ExecutionContext.Implicits.global

class QuestionsOnNoticeDAOImpl extends QuestionsOnNoticeDAO {

  val database = Database.forConfig("sqlite")

  override private[data] def initialiseIfNeeded(): Future[Unit] = {
    isInitialised.flatMap(alreadyInitialised => if (!alreadyInitialised) initialise() else Future())
  }

  protected def isInitialised: Future[Boolean] = {
    database.run(MTable.getTables).map(tables => tables.nonEmpty)
  }

  protected def initialise(): Future[Unit] = {

    val initialiseAction = createTablesAction() >> populatePortfoliosAction()

    val run: Future[Option[Int]] = database.run(initialiseAction)

    run.map(_ => Unit)
  }

  protected def createTablesAction(): DBIO[Unit] = {
    val answerUpdates = TableQuery[AnswerUpdates]
    val dateSets = TableQuery[DateSets]
    val estimates = TableQuery[databasemodel.Estimates]
    val pageQueries = TableQuery[PageQueries]
    val pdfLinkBundles = TableQuery[PDFLinkBundles]
    val portfolios = TableQuery[Portfolios]

    (dateSets.schema ++
      portfolios.schema ++
      estimates.schema ++
      pageQueries.schema ++
      pdfLinkBundles.schema ++
      answerUpdates.schema).create
  }

  protected def populatePortfoliosAction(): DBIO[Option[Int]] = {
    TableQuery[Portfolios] ++= QuestionsOnNoticeDAOImpl.portfolios
  }

  override def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit] = ???

  override def recordEstimates(estimates: Estimates): Future[Unit] = ???

  override def haveEverQueried(estimates: Estimates): Future[Boolean] = ???

  override def retrieveAnswers(estimates: Estimates): Future[Set[Answer]] = ???
}

object QuestionsOnNoticeDAOImpl {
  private val portfolios: Seq[Portfolio] = Seq(
    Portfolio(None, "Agriculture and Water Resources"),
    Portfolio(None, "Attorney-General's"),
    Portfolio(None, "Communications and the Arts"),
    Portfolio(None, "Defence"),
    Portfolio(None, "Education and Training"),
    Portfolio(None, "Employment"),
    Portfolio(None, "Environment "),
    Portfolio(None, "Finance"),
    Portfolio(None, "Foreign Affairs and Trade"),
    Portfolio(None, "Health"),
    Portfolio(None, "Immigration and Border Protection"),
    Portfolio(None, "Industry, Innovation and Science"),
    Portfolio(None, "Infrastructure and Regional Development"),
    Portfolio(None, "Parliament"),
    Portfolio(None, "Prime Minister and Cabinet"),
    Portfolio(None, "Social Services"),
    Portfolio(None, "Treasury")
  )
}