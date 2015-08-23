package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.{Month, LocalDate}

import org.scalatest.FreeSpec

class AnswerUpdateSpec extends FreeSpec {

  private val portfolio = Portfolio.withName("Communications")
  private val estimates = Estimates.create(portfolio, "Budget Estimates",
    Set(LocalDate.of(2015, Month.MAY, 27), LocalDate.of(2015, Month.MAY, 28)))

  def defaultAnswer(estimates: Estimates = estimates,
                    qonNumber: Option[String] = Some("1"),
                    divisionOrAgency: Option[String] = Some("All"),
                    senator: Option[String] = Some("Carr"),
                    topic: Option[String] = Some("General"),
                    pdfURLs: Seq[URL] = Seq.empty,
                    dateReceived: Seq[LocalDate] = Seq.empty): Answer = {

    Answer.create(estimates, qonNumber, divisionOrAgency, senator, topic, pdfURLs, dateReceived)
  }

  "an update including data for an existing Answer" - {

    val existingAnswer = defaultAnswer()

    val answerUpdate = AnswerUpdate.forExistingAnswer(existingAnswer)

    "should have an empty value for the old answer" in {
      assert(answerUpdate.oldAnswer.isEmpty)
    }

    "should have the existing answer as its new answer" in {
      assert(answerUpdate.newAnswer.contains(existingAnswer))
    }

    s"should have an Answer Update type of ${AnswerUpdateType.EXISTING}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.EXISTING)
    }
  }

  s"an update for answers to different questions should throw an IllegalArgumentException when constructed" in {
    val oldAnswer = defaultAnswer(qonNumber = Some("1"))
    val newAnswer = defaultAnswer(qonNumber = Some("2"))

    intercept[IllegalArgumentException] {
      AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)
    }
  }

  "an update from an empty answer to an answer" - {
    val newAnswer = defaultAnswer()

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(None, Some(newAnswer))

    s"should have an update type of ${AnswerUpdateType.NEW}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.NEW)
    }
  }

  "an update from an answer to an empty answer" - {
    val oldAnswer = defaultAnswer()

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(Some(oldAnswer), None)

    s"should have an update type of ${AnswerUpdateType.REMOVED}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.REMOVED)
    }
  }

  "an update between two identical answers" - {
    val oldAnswer = defaultAnswer()
    val newAnswer = defaultAnswer()

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    s"should have an update type of ${AnswerUpdateType.NO_CHANGE}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.NO_CHANGE)
    }
  }

  "an update from an answer without a pdf link to an answer with a pdf link" - {
    val oldAnswer = defaultAnswer(pdfURLs = Seq.empty)
    val newAnswer = defaultAnswer(pdfURLs = Seq(new URL("http://example.com/link.pdf")))

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    s"should have an update type of ${AnswerUpdateType.ANSWERED}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.ANSWERED)
    }
  }

  "an update from an answer without a pdf link or a date answered value to an answer with a date answered value but " +
    "no pdf link" - {
    val oldAnswer = defaultAnswer(pdfURLs = Seq.empty, dateReceived = Seq.empty)
    val newAnswer = defaultAnswer(pdfURLs = Seq.empty, dateReceived = Seq(LocalDate.of(2015, Month.AUGUST, 23)))

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    s"should have an update type of ${AnswerUpdateType.MARKED_AS_ANSWERED}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.MARKED_AS_ANSWERED)
    }
  }

  "an update from an unanswered answer to another unanswered answer with other details changed" - {
    val oldAnswer = defaultAnswer(senator = Some("Carr"))
    val newAnswer = defaultAnswer(senator = Some("Arbib"))

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    s"should have an answer type of ${AnswerUpdateType.DETAILS_ALTERED}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.DETAILS_ALTERED)
    }
  }
}