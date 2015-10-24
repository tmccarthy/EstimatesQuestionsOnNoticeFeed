package au.id.tmm.estimatesqon.model

import java.time.Instant

class AnswerUpdateBundle protected (val timestamp: Instant,
                                    val questionsOnNoticePage: QuestionsOnNoticePage,
                                    val updates: Set[AnswerUpdate]) {
}

object AnswerUpdateBundle {
  def fromUpdates(answerUpdates: Set[AnswerUpdate], questionsOnNoticePage: QuestionsOnNoticePage, timestamp: Instant) =
    new AnswerUpdateBundle(timestamp, questionsOnNoticePage, answerUpdates)
}
