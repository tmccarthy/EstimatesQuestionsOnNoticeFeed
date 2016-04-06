package au.id.tmm.estimatesqon.data

import au.id.tmm.estimatesqon.model.Estimates

class UnregisteredEstimatesException(val estimates: Estimates)
  extends Exception(s"Estimates $estimates was not registered in the database") {
}
