package au.id.tmm.estimatesqon.model

import java.time.Instant

import au.id.tmm.estimatesqon.data.databasemodel.EstimatesRow

class AnswerUpdateBundle protected (val timestamp: Instant,
                                    val estimates: Estimates,
                                    val updates: Set[AnswerUpdate]) {
}

object AnswerUpdateBundle {
  def fromUpdates(answerUpdates: Set[AnswerUpdate], estimates: Estimates, timestamp: Instant) =
    new AnswerUpdateBundle(timestamp, estimates, answerUpdates)
}
