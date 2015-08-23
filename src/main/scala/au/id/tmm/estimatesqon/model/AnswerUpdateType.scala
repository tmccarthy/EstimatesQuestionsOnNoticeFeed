package au.id.tmm.estimatesqon.model

object AnswerUpdateType extends Enumeration {

  type AnswerUpdateType = Value

  val NO_CHANGE, EXISTING, NEW, REMOVED, ANSWERED, MARKED_AS_ANSWERED, DETAILS_ALTERED = Value

}
