package simex.dropoff.domain.validation

import simex.messaging.Simex

trait DropOffRequestValidatorAlgebra {

  def validate(request: Simex): Validation
}
