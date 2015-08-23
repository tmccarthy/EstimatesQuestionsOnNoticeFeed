package au.id.tmm.estimatesqon.model

import au.id.tmm.estimatesqon.model.AnswerUpdateType.AnswerUpdateType

class AnswerUpdate protected (val oldAnswer: Option[Answer],
                              val newAnswer: Option[Answer],
                              val updateType: AnswerUpdateType) {

  protected def this(oldAnswer: Option[Answer], newAnswer: Option[Answer]) =
    this(oldAnswer, newAnswer, AnswerUpdate.typeOfUpdate(oldAnswer, newAnswer))
}

object AnswerUpdate {
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
    throwIfDifferentQuestion(oldAnswer, newAnswer)

    if (oldAnswer.isDefined && newAnswer.isEmpty) {
      AnswerUpdateType.REMOVED

    } else if (oldAnswer.isEmpty && newAnswer.isDefined) {
      AnswerUpdateType.NEW

    } else if (oldAnswer.get.pdfURLs.isEmpty && newAnswer.get.pdfURLs.nonEmpty) {
      AnswerUpdateType.ANSWERED

    } else if (oldAnswer.get.datesReceived.isEmpty && newAnswer.get.datesReceived.nonEmpty) {
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
}