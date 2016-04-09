package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time._

import au.id.tmm.estimatesqon.StandardProjectSpec
import au.id.tmm.estimatesqon.controller.TestResources

class AnswerUpdateSpec extends StandardProjectSpec {

  private val SPECIAL_MOMENT: ZonedDateTime = ZonedDateTime.of(
    LocalDateTime.of(2003, Month.JANUARY, 3, 5, 0),
    ZoneId.of("Australia/Sydney"))

  private val portfolio = Portfolio.withName("Communications")
  private val estimates = ExampleEstimates
    .COMMUNICATIONS_2015_BUDGET
    .cloneWithUrl(TestResources.communications20152016BudgetEstimates)

  def defaultAnswer(estimates: Estimates = estimates,
                    qonNumber: String = "1",
                    divisionOrAgency: Option[String] = Some("All"),
                    senator: Option[String] = Some("Carr"),
                    topic: Option[String] = Some("General"),
                    pdfURLs: Seq[URL] = Seq.empty,
                    scrapedTimestamp: Instant = SPECIAL_MOMENT.toInstant,
                    datesReceived: Set[LocalDate] = Set.empty): Answer = {

    Answer.create(estimates,
      qonNumber,
      divisionOrAgency = divisionOrAgency,
      senator = senator,
      topic = topic,
      pdfURLs = pdfURLs,
      scrapedTimestamp = scrapedTimestamp,
      datesReceived = datesReceived)
  }

  behaviour of "an update including data for an existing Answer"

  {
    lazy val existingAnswer = defaultAnswer()
    lazy val answerUpdate = AnswerUpdate.forExistingAnswer(existingAnswer)

    it should "have an empty value for the old answer" in {
      assert(answerUpdate.oldAnswer.isEmpty)
    }

    it should "have the existing answer as its new answer" in {
      assert(answerUpdate.newAnswer.contains(existingAnswer))
    }

    it should s"have an Answer Update type of ${AnswerUpdateType.EXISTING}" in {
      assert(answerUpdate.updateType === AnswerUpdateType.EXISTING)
    }
  }

  behaviour of "an update for answers to different questions"

  it should "throw an IllegalArgumentException when constructed" in {
    val oldAnswer = defaultAnswer(qonNumber = "1")
    val newAnswer = defaultAnswer(qonNumber = "2")

    intercept[IllegalArgumentException] {
      AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)
    }
  }

  behaviour of "an update from an empty answer to an answer"

  it should s"have an update type of ${AnswerUpdateType.NEW}" in {
    val newAnswer = defaultAnswer()

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(None, Some(newAnswer))

    assert(answerUpdate.updateType === AnswerUpdateType.NEW)
  }

  behaviour of "an update from an answer to an empty answer"

  it should s"have an update type of ${AnswerUpdateType.REMOVED}" in {
    val oldAnswer = defaultAnswer()

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(Some(oldAnswer), None)

    assert(answerUpdate.updateType === AnswerUpdateType.REMOVED)
  }

  behaviour of "an update between two identical answers"

  it should s"have an update type of ${AnswerUpdateType.NO_CHANGE}" in {
    val oldAnswer = defaultAnswer()
    val newAnswer = defaultAnswer()

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    assert(answerUpdate.updateType === AnswerUpdateType.NO_CHANGE)
  }

  behaviour of "an update from an answer without a pdf link to an answer with a pdf link"

  it should s"have an update type of ${AnswerUpdateType.ANSWERED}" in {
    val oldAnswer = defaultAnswer(pdfURLs = Seq.empty)
    val newAnswer = defaultAnswer(pdfURLs = Seq(new URL("http://example.com/link.pdf")))

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    assert(answerUpdate.updateType === AnswerUpdateType.ANSWERED)
  }

  behaviour of "an update from an answer without a pdf link or a date answered value to an answer with a date " +
    "answered value but no pdf link"

  it should s"have an update type of ${AnswerUpdateType.MARKED_AS_ANSWERED}" in {
    val oldAnswer = defaultAnswer(pdfURLs = Seq.empty, datesReceived = Set.empty)
    val newAnswer = defaultAnswer(pdfURLs = Seq.empty, datesReceived = Set(LocalDate.of(2015, Month.AUGUST, 23)))

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    assert(answerUpdate.updateType === AnswerUpdateType.MARKED_AS_ANSWERED)
  }

  behaviour of "an update from an unanswered answer to another unanswered answer with other details changed"

  it should s"have an answer type of ${AnswerUpdateType.DETAILS_ALTERED}" in {
    val oldAnswer = defaultAnswer(senator = Some("Carr"))
    val newAnswer = defaultAnswer(senator = Some("Arbib"))

    val answerUpdate = AnswerUpdate.withOldAndNewAnswers(oldAnswer, newAnswer)

    assert(answerUpdate.updateType === AnswerUpdateType.DETAILS_ALTERED)
  }

  behaviour of "computation of AnswerUpdates from answer lists"

  it should "correctly compute the answer updates" in {
    val oldAnswer1 = defaultAnswer()
    val oldAnswer2 = defaultAnswer(qonNumber = "2")
    val oldAnswers = Set(oldAnswer1, oldAnswer2)

    val newAnswer1 = defaultAnswer(pdfURLs = Seq(new URL("https://example.com")), datesReceived = Set(LocalDate.of(2016, Month.APRIL, 9)))
    val newAnswers = Set(newAnswer1)

    val actualUpdates = AnswerUpdate.fromSetsOfOldAndNewAnswers(oldAnswers, newAnswers)

    val expectedUpdates = Set(
      AnswerUpdate(Some(oldAnswer1), Some(newAnswer1), AnswerUpdateType.ANSWERED),
      AnswerUpdate(Some(oldAnswer2), None, AnswerUpdateType.REMOVED)
    )
  }

  it should "fail if there are any duplicate QON numbers in the old answers" in {
    val oldAnswer1 = defaultAnswer()
    val oldAnswer2 = defaultAnswer(senator = Some("Dasha"))
    val oldAnswers = Set(oldAnswer1, oldAnswer2)

    val newAnswer1 = defaultAnswer()
    val newAnswers = Set(newAnswer1)

    intercept[IllegalArgumentException] {
      AnswerUpdate.fromSetsOfOldAndNewAnswers(oldAnswers, newAnswers)
    }
  }

  it should "fail if there are any duplicate QON numbers in the new answers" in {
    val oldAnswer1 = defaultAnswer()
    val oldAnswers = Set(oldAnswer1)

    val newAnswer1 = defaultAnswer()
    val newAnswer2 = defaultAnswer(senator = Some("Dasha"))
    val newAnswers = Set(newAnswer1, newAnswer2)

    intercept[IllegalArgumentException] {
      AnswerUpdate.fromSetsOfOldAndNewAnswers(oldAnswers, newAnswers)
    }
  }
}
