package au.id.tmm.estimatesqon.controller

import java.time.{Month, LocalDate}

import scala.io.Source

class SocialServicesEstimatesScraperSpec extends AbstractEstimatesScraperSpec(
  portfolioName = "Social Services",
  hearingDates = Set(LocalDate.of(2015, Month.JUNE, 4), LocalDate.of(2015, Month.JUNE, 5)),
  estimatesDescription = "Budget Estimates",
  pageURL = TestResources.socialServices20152016BudgetEstimates,
  expectedNumAnswers = 446,
  answerAssertions = Iterable(

    new AnswerAssertionInfo(
      answerDescription = "first",
      getAnswer = _.headOption,
      qonNumber = "SQ15-000340",
      divisionOrAgency = "3 - Ageing",
      senator = "Polley",
      topic = "ACAR",
      pdfURL = Option.apply("http://www.aph.gov.au/~/media/Committees/clac_ctte/estimates/bud_1516/Social Services/Answers/340.pdf"),
      dateReceived = Option.apply(LocalDate.of(2015, 7, 24))
    ),

    new AnswerAssertionInfo(
      answerDescription = "last",
      getAnswer = _.lastOption,
      qonNumber = "NDIA SQ15-000102",
      divisionOrAgency = "5 - Disability",
      senator = "Siewert",
      topic = "NDIS the provision of advocacy services.",
      pdfURL = None,
      dateReceived = Option.apply(LocalDate.of(2015, 7, 23))
    )
  )
) {

}
