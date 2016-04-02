package au.id.tmm.estimatesqon.model

case class AnswerUpdateBundle protected (estimates: Estimates,
                                         updates: Set[AnswerUpdate]) {
}

object AnswerUpdateBundle {
  def fromUpdates(answerUpdates: Set[AnswerUpdate], estimates: Estimates) =
    new AnswerUpdateBundle(estimates, answerUpdates)
}
