package au.id.tmm.estimatesqon.controller.scraping

import java.time.{LocalDate, Month}

import au.id.tmm.estimatesqon.controller.TestResources
import au.id.tmm.estimatesqon.model.ExampleEstimates

class AttorneyGeneralsEstimatesScraperSpec extends AbstractEstimatesScraperSpec(
  estimatesToTest = ExampleEstimates.AG_2015_BUDGET,
  resourceURL = TestResources.attnyGeneral20152016BudgetEstimates,
  expectedNumAnswers = 115,
  answerAssertions = Iterable(

    new AnswerAssertionInfo(
      answerDescription = "first",
      findAnswerIn = _.headOption,
      qonNumber = "BE15/001",
      divisionOrAgency = "AC",
      senator = "Collins",
      topic = "Funded projects through the grants programme",
      pdfURL = None,
      dateReceived = None
    ),

    new AnswerAssertionInfo(
      answerDescription = "20th",
      findAnswerIn = answers => if (answers.size >= 20) Option.apply(answers(19)) else Option.empty,
      qonNumber = "BE15/020",
      divisionOrAgency = "AGD",
      senator = "Bilyk",
      topic = "Redfern Legal Centre",
      pdfURL = Option.apply("http://www.aph.gov.au/~/media/Committees/legcon_ctte/estimates/bud_1516/AGD/AGD_BE15-020.pdf"),
      dateReceived = Option.apply(LocalDate.of(2015, Month.JULY, 3))
    ),

    new AnswerAssertionInfo(
      answerDescription = "last",
      findAnswerIn = _.lastOption,
      qonNumber = "BE15/115",
      divisionOrAgency = "AGD",
      senator = "Ludwig",
      topic = "Departmental Dispute Resolution",
      pdfURL = None,
      dateReceived = None
    )
  )
) {

}
