package simex.dropoff.domain.validation
import cats.Applicative
import simex.messaging.{Endpoint, Simex}
import simex.webservice.validation.{
  SimexRequestValidatorAlgebra,
  Validation,
  ValidationFailed,
  ValidationPassed
}
import cats.syntax.all._
import thediscprog.simexfield.SimexFieldUtil.isFieldDefined
class DropOffRequestValidator[F[_]: Applicative]() extends SimexRequestValidatorAlgebra[F] {

  /** For drop off validation, the following should be defined:
    * - The destination
    * - destination.resource should be defined
    * - destination.method should be defined
    */
  override def validateRequest(request: Simex): F[Validation] =
    Option(request.destination) match {
      case Some(ep) => checkDestination(ep).pure[F]
      case None => (ValidationFailed("Destination is not defined"): Validation).pure[F]
    }

  private def checkDestination(ep: Endpoint): Validation =
    if (!(isFieldDefined(ep.resource) && isFieldDefined(ep.method)))
      ValidationFailed("Destination resource and method not defined")
    else if (!isFieldDefined(ep.method))
      ValidationFailed("Destination method is not defined")
    else if (!isFieldDefined(ep.resource))
      ValidationFailed("Destination resource is not defined")
    else
      ValidationPassed("Okay")

}
