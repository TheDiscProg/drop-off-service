package simex.dropoff.domain.validation
import simex.messaging.{Endpoint, Simex}

class DropOffRequestValidator() extends DropOffRequestValidatorAlgebra {

  /** For drop off validation, the following should be defined:
    * - The destination
    * - destination.resource should be defined
    * - destination.method should be defined
    */
  override def validate(request: Simex): Validation =
    Option(request.destination) match {
      case Some(ep) => checkDestination(ep)
      case None => ValidationFailed("Destination is not defined")
    }

  private def checkDestination(ep: Endpoint): Validation =
    if (isFieldNotDefined(ep.resource) && isFieldNotDefined(ep.method))
      ValidationFailed("Neither destination resource nor method defined")
    else if (isFieldNotDefined(ep.resource))
      ValidationFailed("Destination resource is not defined")
    else if (isFieldNotDefined(ep.method))
      ValidationFailed("Destination method is not defined")
    else
      ValidationPassed("Okay")

  def isFieldNotDefined(value: String): Boolean =
    Option(value).forall(_.trim.isEmpty)

}
