package au.id.tmm.estimatesqon.data

import java.nio.file.{Files, Paths}

import org.apache.commons.io.FileUtils
import org.scalatest.FreeSpec
import slick.jdbc.meta.MTable

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

      "a call to initialise will create a database with the correct tables" in {
        Await.result(daoImpl.initialise(), 1.seconds)

        val expectedDbLocation = workingDir.resolve("questionsOnNotice.db")
        assert(Files.isRegularFile(expectedDbLocation))

        val listTables: Future[Vector[MTable]] = daoImpl.database.run(MTable.getTables)
        val tableMetadata: Vector[MTable] = Await.result(listTables, 1.second)
        val tableNames: Set[String] = tableMetadata
          .map(_.name.name)
          .toSet
          .filterNot(_ == "sqlite_sequence")

        assert(tableNames == Set("AnswerUpdates", "DateSets", "Estimates", "PageQueries", "PDFLinkBundles", "Portfolios"))
      }
    }
  }
}
