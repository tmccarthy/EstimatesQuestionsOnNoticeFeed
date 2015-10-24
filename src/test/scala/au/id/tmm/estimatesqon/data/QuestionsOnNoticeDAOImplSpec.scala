package au.id.tmm.estimatesqon.data

import java.nio.file.{Files, Paths}
import java.time.{Month, LocalDate}

import au.id.tmm.estimatesqon.controller.TestResources
import au.id.tmm.estimatesqon.data.databasemodel.Portfolios
import au.id.tmm.estimatesqon.model.{Portfolio, Estimates}
import org.apache.commons.io.FileUtils
import org.scalatest.FreeSpec
import slick.jdbc.meta.MTable
import slick.lifted.{Query, TableQuery}
import slick.driver.SQLiteDriver.api._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.DurationInt

class QuestionsOnNoticeDAOImplSpec extends FreeSpec {
  "A questions on notice DAO implementation" - {
    val daoImpl = new QuestionsOnNoticeDAOImpl

    "given there is no database in the working directory" - {
      val workingDir = Paths.get("working")

      if (Files.exists(workingDir)) {
        FileUtils.cleanDirectory(workingDir.toFile)
      } else {
        Files.createDirectories(workingDir)
      }

      "a call to initialise" - {
        Await.result(daoImpl.initialiseIfNeeded(), 5.seconds)

        "will create a databse file in the expected location" in {
          val expectedDbLocation = workingDir.resolve("questionsOnNotice.db")
          assert(Files.isRegularFile(expectedDbLocation))
        }

        "will create the expected tables" in {
          val listTables: Future[Vector[MTable]] = daoImpl.database.run(MTable.getTables)
          val tableMetadata: Vector[MTable] = Await.result(listTables, 1.second)
          val actualTableNames: Set[String] = tableMetadata
            .map(_.name.name)
            .toSet
            .filterNot(_ == "sqlite_sequence")

          val expectedTableNames: Set[String] = Set("AnswerUpdates", "DateSets", "Estimates",
            "PageQueries", "PDFLinkBundles", "Portfolios")

          assert(actualTableNames == expectedTableNames)
        }

        "will populate the Portfolios table" in {
          val query: Query[Rep[String], String, Seq] = TableQuery[Portfolios].map(_.name)
          val result: Seq[String] = Await.result(daoImpl.database.run(query.result), 5.seconds)

          assert (result == Seq(
            "Agriculture and Water Resources",
            "Attorney-General's",
            "Communications and the Arts",
            "Defence",
            "Education and Training",
            "Employment",
            "Environment ",
            "Finance",
            "Foreign Affairs and Trade",
            "Health",
            "Immigration and Border Protection",
            "Industry, Innovation and Science",
            "Infrastructure and Regional Development",
            "Parliament",
            "Prime Minister and Cabinet",
            "Social Services",
            "Treasury")
          )
        }
      }

      "once the database is initialised" - {
        Await.result(daoImpl.initialiseIfNeeded(), 5.seconds)

        "a given Estimates will not have been queried before" in {
          val portfolio: Portfolio = Portfolio.withName("Communications")
          val hearingDates: Set[LocalDate] = Set(LocalDate.of(2015, Month.MAY, 27), LocalDate.of(2015, Month.MAY, 28))
          Estimates.create(portfolio, "Budget Estimates", hearingDates, TestResources.communications20152016BudgetEstimates)
        }
      }
    }
  }
}
