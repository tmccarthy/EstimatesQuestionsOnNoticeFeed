package au.id.tmm.estimatesqon.model

import java.net.URL
import java.time.{ZoneId, _}

object AnswerUpdatesForTesting {

  private val SPECIAL_MOMENT: ZonedDateTime = ZonedDateTime.of(
    LocalDateTime.of(2003, Month.JANUARY, 3, 5, 0),
    ZoneId.of("Australia/Sydney"))

  val estimates = ExampleEstimates.COMMUNICATIONS_2015_BUDGET
  private val defaultQonNumber = "1"
  private val defaultDivision = Some("All")
  private val defaultSenator = Some("Dastyari")
  private val defaultTopic = Some("General")
  private val defaultPdfUrls = Seq(new URL("http://example.com/one.pdf"), new URL("http://example.com/two.pdf"))
  private val defaultScrapedTimestamp = SPECIAL_MOMENT.toInstant
  private val defaultDatesReceived = Set(LocalDate.of(2016, Month.JANUARY, 13), LocalDate.of(2015, Month.AUGUST, 18))

  def defaultAnswer(estimates: Estimates             = estimates,
                    qonNumber: String                = defaultQonNumber,
                    divisionOrAgency: Option[String] = defaultDivision,
                    senator: Option[String]          = defaultSenator,
                    topic: Option[String]            = defaultTopic,
                    pdfURLs: Seq[URL]                = defaultPdfUrls,
                    scrapedTimestamp: Instant        = defaultScrapedTimestamp,
                    datesReceived: Set[LocalDate]    = defaultDatesReceived): Answer = {

    Answer.create(estimates,
      qonNumber,
      divisionOrAgency = divisionOrAgency,
      senator = senator,
      topic = topic,
      pdfURLs = pdfURLs,
      scrapedTimestamp = scrapedTimestamp,
      datesReceived = datesReceived)
  }

  val answeredAnswer = defaultAnswer()
  val unansweredAnswer = defaultAnswer(pdfURLs = Seq.empty, datesReceived = Set.empty)
  val markedAnsweredAnswer = defaultAnswer(pdfURLs = Seq.empty)

  val forExistingAnsweredAnswer = AnswerUpdate.forExistingAnswer(answeredAnswer)

  val forExistingUnansweredAnswer = AnswerUpdate.forExistingAnswer(unansweredAnswer)

  val forNoChange = AnswerUpdate.withOldAndNewAnswers(answeredAnswer, answeredAnswer)

  val forNew = AnswerUpdate.withOldAndNewAnswers(None, Some(answeredAnswer))

  val forRemoved = AnswerUpdate.withOldAndNewAnswers(Some(answeredAnswer), None)

  val forAnswered = AnswerUpdate.withOldAndNewAnswers(unansweredAnswer, answeredAnswer)

  val forMarkedAsAnswered = AnswerUpdate.withOldAndNewAnswers(unansweredAnswer, markedAnsweredAnswer)

  val forDetailsAltered = AnswerUpdate.withOldAndNewAnswers(answeredAnswer, defaultAnswer(topic = Some("NBN")))

}
