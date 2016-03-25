package au.id.tmm.estimatesqon.controller

import java.time.{Month, LocalDate}

import au.id.tmm.estimatesqon.model.ExampleEstimates

import scala.io.Source

class CommunicationsEstimatesScraperSpec extends AbstractEstimatesScraperSpec(
  estimatesToTest = ExampleEstimates.COMMUNICATIONS_2015_BUDGET,
  resourceURL = TestResources.communications20152016BudgetEstimates,
  expectedNumAnswers = 138,
  answerAssertions = Iterable(

    new AnswerAssertionInfo(
      answerDescription = "first",
      findAnswerIn = _.headOption,
      qonNumber = "1",
      divisionOrAgency = "Programme 1.1",
      senator = "Carr",
      topic = "Cooperative intelligent transport systems",
      pdfURL = Option.apply("http://www.aph.gov.au/~/media/Committees/ec_ctte/estimates/bud_1516/communications/q1.pdf"),
      dateReceived = Option.apply(LocalDate.of(2015, 7, 9))
    ),

    new AnswerAssertionInfo(
      answerDescription = "last",
      findAnswerIn = _.lastOption,
      qonNumber = "138",
      divisionOrAgency = "Portfolio wide",
      senator = "Ludwig",
      topic = "Departmental Dispute Resolution",
      pdfURL = Option.apply("http://www.aph.gov.au/~/media/Committees/ec_ctte/estimates/bud_1516/communications/q138_ACMA.pdf"),
      dateReceived = Option.apply(LocalDate.of(2015, 7, 28))
    )
  )
) {

}
