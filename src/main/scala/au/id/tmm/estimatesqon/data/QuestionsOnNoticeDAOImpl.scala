package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.data.databasemodel._
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, AnswerUpdateBundle, Estimates}
import slick.driver.SQLiteDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

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

  private def lookupRowFor(estimates: Estimates): Future[Option[EstimatesRow]] = {
    val query = TableQuery[EstimatesTable]
      .filter(_.pageURL === estimates.pageURL.toString)

    database.run(query.result).map(_.headOption)
  }

  override def writeUpdateBundle(updateBundle: AnswerUpdateBundle): Future[Unit] = {
    val estimates: Estimates = updateBundle.estimates

    lookupRowFor(estimates).flatMap(estimatesRow => {

      if (estimatesRow.isEmpty) {
        throw new UnregisteredEstimatesException(estimates)
      }

      val estimatesID: Long = estimatesRow.get.estimatesID
      val (answerRows, pdfLinkRows) = constructRowsToInsertFor(updateBundle, estimatesID)

      val answerRowsInsert = database.run(TableQuery[AnswersTable] ++= answerRows)
      val pdfLinkRowsInsert = database.run(TableQuery[PDFLinkBundlesTable] ++= pdfLinkRows)

      pdfLinkRowsInsert
        .flatMap(_ => answerRowsInsert)
        .map(_ => Unit)
    })
  }

  private def constructRowsToInsertFor(updateBundle: AnswerUpdateBundle,
                                       estimatesID: Long): (Set[AnswerRow], Set[PDFLinkBundleRow]) = {
    val rowsToInsert: Set[(AnswerRow, Seq[PDFLinkBundleRow])] = updateBundle
      .updates
      .zipWithIndex
      .map{
        case(answerUpdate, index) => constructRowsFor(answerUpdate, estimatesID, index)
      }

    val answerRows = rowsToInsert.map(_._1)
    val pdfLinkRows = rowsToInsert.flatMap(_._2)

    (answerRows, pdfLinkRows)
  }

  private def constructRowsFor(answerUpdate: AnswerUpdate, estimatesID: Long, answerID: Long): (AnswerRow, Seq[PDFLinkBundleRow]) = {
    // TODO better way to get bundle id

    val linkBundleID = Random.nextLong()

    val pdfs: Option[Seq[PDFLinkBundleRow]] = answerUpdate.newAnswer
      .map(RowModelConversions.pdfLinkBundleRowsFrom(_, linkBundleID))
      .filter(_.nonEmpty)

    val answerRow: AnswerRow = RowModelConversions.answerUpdateToDbRow(answerUpdate, estimatesID, Some(linkBundleID).filter(_ => pdfs.isDefined), answerID)

    (answerRow, pdfs.getOrElse(Seq.empty))
  }

  override def registerEstimates(estimates: Estimates): Future[Unit] = {
    val newRow: EstimatesRow = RowModelConversions.estimatesToDbRow(estimates)

    val query = TableQuery[EstimatesTable] += newRow

    database.run(query).map(_ => Unit)
  }

  override def listEstimates: Future[Set[Estimates]] = {
    val query = TableQuery[EstimatesTable]

    val rowsFuture: Future[Seq[EstimatesRow]] = database.run(query.result)

    rowsFuture.map(_.map(RowModelConversions.estimatesFromDbRow).toSet)
  }

  override def haveEverQueried(estimates: Estimates): Future[Boolean] = {
    val query = TableQuery[AnswersTable]
      .filter(_.joinedEstimates.filter(_.pageURL === estimates.pageURL.toString).exists)
      .exists

    database.run(query.result)
  }

  override def retrieveLatestAnswersFor(estimates: Estimates): Future[Set[Answer]] = {
    lookupRowFor(estimates).flatMap(estimatesRow => {

      if (estimatesRow.isEmpty) {
        Future{Set.empty}
      } else {

        val estimatesID = estimatesRow.get.estimatesID

        // Query inspired by http://stackoverflow.com/a/28090544/1951001
        val latestAnswersQuery = constructLatestAnswersQuery(estimatesID)

        val matchingPdfBundlesQuery = latestAnswersQuery.flatMap(_.joinedPdfLinksBundle)

        for {
          answerRows <- database.run(latestAnswersQuery.result)
          pdfBundleRows <- database.run(matchingPdfBundlesQuery.result)
        } yield RowModelConversions.composeAnswersFrom(estimates, answerRows, pdfBundleRows)
      }
    })
  }

  private def constructLatestAnswersQuery(estimatesID: Long): Query[AnswersTable, AnswerRow, Seq] = {
    TableQuery[AnswersTable].joinLeft(TableQuery[AnswersTable])
      .on((anAnswer, allMoreRecentAnswers) => anAnswer.qonNumber === allMoreRecentAnswers.qonNumber && anAnswer.queryTimestamp < allMoreRecentAnswers.queryTimestamp)
      .filter { case (anAnswer, allMoreRecentAnswers) => allMoreRecentAnswers.isEmpty }
      .map { case (anAnswer, allMoreRecentAnswers) => anAnswer }
      .filter(_.estimatesID === estimatesID)
  }
}

object QuestionsOnNoticeDAOImpl {
  def forConfigName(dbConfigName: String): QuestionsOnNoticeDAOImpl = new QuestionsOnNoticeDAOImpl(dbConfigName)
}