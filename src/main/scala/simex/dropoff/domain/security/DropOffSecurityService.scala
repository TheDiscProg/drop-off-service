package simex.dropoff.domain.security

import cats.Applicative
import cats.syntax.all._
import shareprice.config.ServiceDefinition
import simex.messaging.{Endpoint, Simex}
import simex.webservice.security.SecurityResponseResource.SecurityResponse
import simex.webservice.security.SecurityResponseResource.SecurityResponse.{
  SecurityFailed,
  SecurityPassed
}
import simex.webservice.security.{SecurityResponseResource, SimexMessageSecurityServiceAlgebra}
import thediscprog.simexfield.SimexFieldUtil._

/** There is no security around a drop-off service except that it checks for:
  * - A valid resource is defined in destination
  * - Client ID is defined in client
  * - Request ID is defined in client
  * - Authorization Token is defined in client
  */
class DropOffSecurityService[F[_]: Applicative]() extends SimexMessageSecurityServiceAlgebra[F] {

  override def checkSecurityForRequest(
      request: Simex
  ): F[SecurityResponseResource.SecurityResponse] =
    (if (minimumSecurityCheck(request))
       SecurityPassed: SecurityResponse
     else
       SecurityFailed: SecurityResponse).pure[F]

  private def minimumSecurityCheck(msg: Simex): Boolean =
    isFieldDefined[Endpoint](msg.destination) &&
      isFieldDefined(msg.destination.resource) &&
      ServiceDefinition.ServiceAccessibleList.map(_.service).contains(msg.destination.resource) &&
      isFieldDefined(msg.client.clientId) &&
      isFieldDefined(msg.client.requestId)
}
