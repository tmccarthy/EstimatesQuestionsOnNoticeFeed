package au.id.tmm.estimatesqon.model

import org.scalatest.FreeSpec

class AnswerSpec extends FreeSpec {

  "an answer with question on notice number 42" - {
    val answer42: Answer = Answer.create(null, 42, null, null, null, null, null)

    "should be compared to as higher than a question with number 41" in {
      val answer41: Answer = Answer.create(null, 41, null, null, null, null, null)

      assert(answer42.compare(answer41) > 0)
    }

    "should be compared to as lower than a question with number 43" in {
      val answer41: Answer = Answer.create(null, 43, null, null, null, null, null)

      assert(answer42.compare(answer41) < 0)
    }
  }

}
