package au.id.tmm.estimatesqon.controller

import java.time.{Month, LocalDate}

import au.id.tmm.estimatesqon.model.ExampleEstimates

import scala.io.Source

class TreasuryEstimatesScraperSpec extends AbstractEstimatesScraperSpec(
  estimatesToTest = ExampleEstimates.TREASURY_2015_BUDGET,
  resourceURL = TestResources.treasury20152016BudgetEstimates,
  expectedNumAnswers = 168,
  answerAssertions = Iterable(

    new AnswerAssertionInfo(
      answerDescription = "first",
      getAnswer = _.headOption,
      qonNumber = "1",
      divisionOrAgency = "Small Business Tax Division",
      senator = "Wong, Penny",
      topic = "BET 1 - Instant Asset Write-off",
      pdfURL = None,
      dateReceived = None
    ),

    new AnswerAssertionInfo(
      answerDescription = "94th",
      getAnswer = answers => if (answers.size >= 94) Option.apply(answers(93)) else Option.empty,
      qonNumber = "155-162",
      divisionOrAgency = "Australian Taxation Office",
      senator = "Xenophon, Nick",
      topic = "BET 155-162 - Un-taxed earnings",
      pdfURL = Option.apply("http://www.aph.gov.au/~/media/Committees/economics_ctte/estimates/bud_1516/Treasury/answers/BET155-162_Xenophon.pdf"),
      dateReceived = Option.apply(LocalDate.of(2015, 7, 20))
    ),

    new AnswerAssertionInfo(
      answerDescription = "last",
      getAnswer = _.lastOption,
      qonNumber = "543",
      divisionOrAgency = "Australian Securities and Investment Commission",
      senator = "McAllister, Jenny",
      topic = "BET 543 - Existing enforcement provisions",
      pdfURL = None,
      dateReceived = None
    )
  )
){

}
