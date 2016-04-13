package au.id.tmm.estimatesqon.controller.scraping

import java.time.LocalDate

import au.id.tmm.estimatesqon.controller.TestResources
import au.id.tmm.estimatesqon.model.ExampleEstimates

class SocialServicesEstimatesScraperSpec extends AbstractEstimatesScraperSpec(
  estimatesToTest = ExampleEstimates.SOCIAL_SERVICES_2015_BUDGET,
  resourceURL = TestResources.socialServices20152016BudgetEstimates,
  expectedNumAnswers = 446,
  answerAssertions = Iterable(

    new AnswerAssertionInfo(
      answerDescription = "first",
      findAnswerIn = _.headOption,
      qonNumber = "SQ15-000340",
      divisionOrAgency = "3 - Ageing",
      senator = "Polley",
      topic = "ACAR",
      pdfURL = Option.apply("http://www.aph.gov.au/~/media/Committees/clac_ctte/estimates/bud_1516/Social Services/Answers/340.pdf"),
      dateReceived = Option.apply(LocalDate.of(2015, 7, 24))
    ),

    new AnswerAssertionInfo(
      answerDescription = "last",
      findAnswerIn = _.lastOption,
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
