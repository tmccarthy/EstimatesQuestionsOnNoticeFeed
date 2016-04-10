package au.id.tmm.estimatesqon.controller.twitter

import au.id.tmm.estimatesqon.model.{AnswerUpdate, AnswerUpdateType}

private[twitter] object TweetComposition {

  def forUpdate(answerUpdate: AnswerUpdate): Tweet = {
    Tweet(tweetContentFor(answerUpdate))
  }

  private def tweetContentFor(answerUpdate: AnswerUpdate): String = {
    answerUpdate.updateType match {
      case AnswerUpdateType.NO_CHANGE =>
        s"""Question "${answerUpdate.qonId}" for the ${answerUpdate.estimates.printableString} has not been updated at ${answerUpdate.estimates.pageURL}"""

      case AnswerUpdateType.EXISTING =>
        s"""Question "${answerUpdate.qonId}" has been registered for the for the ${answerUpdate.estimates.printableString} at ${answerUpdate.estimates.pageURL}"""

      case AnswerUpdateType.NEW =>
        s"""Question "${answerUpdate.qonId}" has been added to the page for the ${answerUpdate.estimates.printableString} at ${answerUpdate.estimates.pageURL}"""

      case AnswerUpdateType.REMOVED =>
        s"""Question "${answerUpdate.qonId}" has been removed from the page for the ${answerUpdate.estimates.printableString} at ${answerUpdate.estimates.pageURL}"""

      case AnswerUpdateType.ANSWERED =>
        s"""Question "${answerUpdate.qonId}" has been answered on the page for the ${answerUpdate.estimates.printableString} at ${answerUpdate.estimates.pageURL}"""

      case AnswerUpdateType.MARKED_AS_ANSWERED =>
        s"""Question "${answerUpdate.qonId}" has been marked as answered on the page for the ${answerUpdate.estimates.printableString} at ${answerUpdate.estimates.pageURL}"""

      case AnswerUpdateType.DETAILS_ALTERED =>
        s"""Question "${answerUpdate.qonId}" has had its details altered on the page for the ${answerUpdate.estimates.printableString} at ${answerUpdate.estimates.pageURL}"""

    }
  }
}
