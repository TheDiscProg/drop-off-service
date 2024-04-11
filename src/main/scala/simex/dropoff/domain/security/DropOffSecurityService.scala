package simex.dropoff.domain.security

import cats.Applicative
import cats.syntax.all._
import shareprice.config.ServiceDefinition
import simex.messaging.Simex
import simex.webservice.security.SecurityResponseResource.SecurityResponse
import simex.webservice.security.SecurityResponseResource.SecurityResponse.{
  SecurityFailed,
  SecurityPassed
}
import simex.webservice.security.{SecurityResponseResource, SimexMessageSecurityServiceAlgebra}

/** There is no security around a drop-off service except that it checks for:
  * - A valid resource is defined in destination
  * - Client ID is defined in client
  * - Request ID is defined in client
  * - Authorization Token is defined in client
  */
class DropOffSecurityService[F[_]: Applicative]() extends SimexMessageSecurityServiceAlgebra[F] {

  override def handleSimexRequest(respond: SecurityResponseResource.SecurityResponse.type)(
      body: Simex
  ): F[SecurityResponseResource.SecurityResponse] =
    (if (minimumSecurityCheck(body))
       SecurityPassed: SecurityResponse
     else
       SecurityFailed: SecurityResponse).pure[F]

  private def minimumSecurityCheck(msg: Simex): Boolean =
    ServiceDefinition.ServiceAccessibleList.map(_.service).contains(msg.destination.resource) &&
      msg.client.clientId.trim.nonEmpty &&
      msg.client.requestId.trim.nonEmpty
}
