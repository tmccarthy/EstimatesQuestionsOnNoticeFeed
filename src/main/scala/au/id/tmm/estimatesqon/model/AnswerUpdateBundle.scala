package au.id.tmm.estimatesqon.model

import java.time.Instant

class AnswerUpdateBundle protected (val timestamp: Instant,
                                    val updates: Set[AnswerUpdate]) {

}

object AnswerUpdateBundle {
  def forUpdatesAt(timestamp: Instant, updates: Iterable[AnswerUpdate]) =
    new AnswerUpdateBundle(timestamp, updates.toSet)
}
