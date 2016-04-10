package au.id.tmm.estimatesqon.model

import au.id.tmm.estimatesqon.model.AnswerUpdateType.AnswerUpdateType

case class AnswerUpdate (oldAnswer: Option[Answer],
                         newAnswer: Option[Answer],
                         updateType: AnswerUpdateType) {

  protected def this(oldAnswer: Option[Answer], newAnswer: Option[Answer]) =
    this(oldAnswer, newAnswer, AnswerUpdate.typeOfUpdate(oldAnswer, newAnswer))

  AnswerUpdate.throwIfDifferentEstimates(oldAnswer, newAnswer)
  AnswerUpdate.throwIfDifferentQuestion(oldAnswer, newAnswer)

  val estimates: Estimates = oldAnswer.getOrElse(newAnswer.get).estimates

  val qonId: String = oldAnswer.getOrElse(newAnswer.get).qonIdentifier
}

object AnswerUpdate {

  def fromSetsOfOldAndNewAnswers(oldAnswers: Set[Answer], newAnswers: Set[Answer]): Set[AnswerUpdate] = {
    val oldAnswersByIdentifier = oldAnswers.groupBy(_.qonIdentifier)
    val newAnswersByIdentifier = newAnswers.groupBy(_.qonIdentifier)

    ensureNoDuplicateQonIds(oldAnswersByIdentifier)
    ensureNoDuplicateQonIds(newAnswersByIdentifier)

    val allQonIds = oldAnswersByIdentifier.keySet ++ newAnswersByIdentifier.keySet

    allQonIds.map(qonId => {
      val oldAnswer = oldAnswersByIdentifier.get(qonId).flatMap(_.headOption)
      val newAnswer = newAnswersByIdentifier.get(qonId).flatMap(_.headOption)

      withOldAndNewAnswers(oldAnswer, newAnswer)
    })
  }

  private def ensureNoDuplicateQonIds(answersByIdentifier: Map[String, Set[Answer]]) = {
    val containsDuplicateQonIds = answersByIdentifier.values.exists(_.size > 1)

    if (containsDuplicateQonIds) {
      throw new IllegalArgumentException("Cannot determine updates if there are duplicate QON ids")
    }
  }

  def withOldAndNewAnswers(oldAnswer: Answer, newAnswer: Answer): AnswerUpdate =
    withOldAndNewAnswers(Some(oldAnswer), Some(newAnswer))

  def withOldAndNewAnswers(oldAnswer: Option[Answer], newAnswer: Option[Answer]): AnswerUpdate =
    new AnswerUpdate(oldAnswer, newAnswer)

  /**
   * Used to register an AnswerUpdate the first time, where we have no data about previous answers. This is distinct
   * from the case where the answer is new.
   */
  def forExistingAnswer(existingAnswer: Answer) =
    new AnswerUpdate(None, Some(existingAnswer), AnswerUpdateType.EXISTING)

  private def typeOfUpdate(oldAnswer: Option[Answer], newAnswer: Option[Answer]): AnswerUpdateType = {
    if (oldAnswer.isDefined && newAnswer.isEmpty) {
      AnswerUpdateType.REMOVED

    } else if (oldAnswer.isEmpty && newAnswer.isDefined) {
      AnswerUpdateType.NEW

    } else if (oldAnswer.get.pdfURLs.isEmpty && newAnswer.get.pdfURLs.nonEmpty) {
      AnswerUpdateType.ANSWERED

    } else if (oldAnswer.get.latestDateReceived.isEmpty && newAnswer.get.latestDateReceived.nonEmpty) {
      AnswerUpdateType.MARKED_AS_ANSWERED

    } else if (oldAnswer.get.hasDifferentAnswerDetailsTo(newAnswer.get)) {
      AnswerUpdateType.DETAILS_ALTERED

    } else {
      AnswerUpdateType.NO_CHANGE

    }
  }

  private def throwIfDifferentQuestion(oldAnswer: Option[Answer], newAnswer: Option[Answer]) = {
    if (oldAnswer.isDefined && newAnswer.isDefined && oldAnswer.get.hasDifferentQONIdentifierTo(newAnswer.get)) {
      throw new IllegalArgumentException("The answers are to two different questions")
    }
  }

  private def throwIfDifferentEstimates(oldAnswer: Option[Answer], newAnswer: Option[Answer]) = {
    if (oldAnswer.isDefined && newAnswer.isDefined && oldAnswer.get.hasDifferentEstimatesTo(newAnswer.get)) {
      throw new IllegalArgumentException("The answers are for two different estimates")
    }
  }
}