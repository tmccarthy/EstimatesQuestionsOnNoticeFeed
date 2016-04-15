package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.data.databasemodel._
import au.id.tmm.estimatesqon.model.{Answer, AnswerUpdate, Estimates}
import slick.driver.SQLiteDriver.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.collection.immutable.Iterable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

private [data] class QonDao protected(dbConfigName: String) {

  lazy val database = Database.forConfig(dbConfigName)

  private[data] def initialiseIfNeeded(): Future[Unit] = {
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

  def writeUpdates(updates: Set[AnswerUpdate]): Future[Unit] = {
    val writesPerEstimates: Iterable[Future[Unit]] = updates
      .groupBy(_.estimates)
      .map {
        case (estimates, updatesForEstimates) => writeUpdatesForSingleEstimates(estimates, updatesForEstimates)
      }

    Future.sequence(writesPerEstimates).map(_ => Unit)
  }

  private def writeUpdatesForSingleEstimates(estimates: Estimates, updates: Set[AnswerUpdate]): Future[Unit] = {
    lookupRowFor(estimates).flatMap(estimatesRow => {

      if (estimatesRow.isEmpty) {
        throw new UnregisteredEstimatesException(estimates)
      }

      val estimatesID: Long = estimatesRow.get.estimatesID
      val (answerRows, pdfLinkRows) = constructRowsToInsertFor(updates, estimatesID)

      val answerRowsInsert = database.run(TableQuery[AnswersTable] ++= answerRows)
      val pdfLinkRowsInsert = database.run(TableQuery[PDFLinkBundlesTable] ++= pdfLinkRows)

      pdfLinkRowsInsert
        .flatMap(_ => answerRowsInsert)
        .map(_ => Unit)
    })
  }

  private def constructRowsToInsertFor(updates: Set[AnswerUpdate],
                                       estimatesID: Long): (Set[AnswerRow], Set[PDFLinkBundleRow]) = {
    val rowsToInsert: Set[(AnswerRow, List[PDFLinkBundleRow])] = updates
      .zipWithIndex
      .map{
        case(answerUpdate, index) => constructRowsFor(answerUpdate, estimatesID, index)
      }

    val answerRows = rowsToInsert.map(_._1)
    val pdfLinkRows = rowsToInsert.flatMap(_._2)

    (answerRows, pdfLinkRows)
  }

  private def constructRowsFor(answerUpdate: AnswerUpdate, estimatesID: Long, answerID: Long): (AnswerRow, List[PDFLinkBundleRow]) = {
    // TODO better way to get bundle id

    val linkBundleID = Random.nextLong()

    val pdfs: Option[List[PDFLinkBundleRow]] = answerUpdate.newAnswer
      .map(RowModelConversions.pdfLinkBundleRowsFrom(_, linkBundleID))
      .filter(_.nonEmpty)

    val answerRow: AnswerRow = RowModelConversions.answerUpdateToDbRow(answerUpdate, estimatesID, Some(linkBundleID).filter(_ => pdfs.isDefined), answerID)

    (answerRow, pdfs.getOrElse(List.empty))
  }

  def registerEstimates(estimates: Estimates): Future[Unit] = {
    val newRow: EstimatesRow = RowModelConversions.estimatesToDbRow(estimates)

    val query = TableQuery[EstimatesTable] += newRow

    database.run(query).map(_ => Unit)
  }

  def listEstimates: Future[Set[Estimates]] = {
    val query = TableQuery[EstimatesTable]

    val rowsFuture: Future[Seq[EstimatesRow]] = database.run(query.result)

    rowsFuture.map(_.map(RowModelConversions.estimatesFromDbRow).toSet)
  }

  def haveEverQueried(estimates: Estimates): Future[Boolean] = {
    val query = TableQuery[AnswersTable]
      .filter(_.joinedEstimates.filter(_.pageURL === estimates.pageURL.toString).exists)
      .exists

    database.run(query.result)
  }

  def retrieveLatestAnswersFor(estimates: Estimates): Future[Set[Answer]] = {
    lookupRowFor(estimates).flatMap(estimatesRow => {

      if (estimatesRow.isEmpty) {
        Future{Set.empty}
      } else {

        val estimatesID = estimatesRow.get.estimatesID

        val latestAnswersQuery = constructLatestAnswersQuery(estimatesID)

        val matchingPdfBundlesQuery = latestAnswersQuery.flatMap(_.joinedPdfLinksBundle)

        for {
          answerRows <- database.run(latestAnswersQuery.result).map(_.toList)
          pdfBundleRows <- database.run(matchingPdfBundlesQuery.result).map(_.toList)
        } yield RowModelConversions.composeAnswersFrom(estimates, answerRows, pdfBundleRows)
      }
    })
  }

  // Query inspired by http://stackoverflow.com/a/28090544/1951001
  private def constructLatestAnswersQuery(estimatesID: Long): Query[AnswersTable, AnswerRow, Seq] = {
    TableQuery[AnswersTable].joinLeft(TableQuery[AnswersTable])
      .on((anAnswer, allMoreRecentAnswers) => anAnswer.qonNumber === allMoreRecentAnswers.qonNumber && anAnswer.queryTimestamp < allMoreRecentAnswers.queryTimestamp)
      .filter { case (anAnswer, allMoreRecentAnswers) => allMoreRecentAnswers.isEmpty }
      .map { case (anAnswer, allMoreRecentAnswers) => anAnswer }
      .filter(_.estimatesID === estimatesID)
  }
}

object QonDao {
  def forConfigName(dbConfigName: String): QonDao = new QonDao(dbConfigName)
}