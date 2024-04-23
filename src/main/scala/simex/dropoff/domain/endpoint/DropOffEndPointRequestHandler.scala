package simex.dropoff.domain.endpoint

import cats.effect.MonadCancel
import cats.syntax.all._
import org.typelevel.log4cats.Logger
import simex.dropoff.domain.orchestrator.DropOffOrchestatorAlgebra
import simex.messaging.Simex
import simex.webservice.HttpResponseResource
import simex.webservice.HttpResponseResource.HttpResponse
import simex.webservice.HttpResponseResource.HttpResponse.{NoContent, ServiceUnavailable}
import simex.webservice.handler.SimexMessageHandlerAlgebra
import simex.webservice.security.SimexMessageSecurityServiceAlgebra
import simex.webservice.validation.SimexRequestValidatorAlgebra
class DropOffEndPointRequestHandler[F[_]: MonadCancel[*[_], Throwable]: Logger](
    url: String,
    securityService: SimexMessageSecurityServiceAlgebra[F],
    orc: DropOffOrchestatorAlgebra[F],
    validator: SimexRequestValidatorAlgebra[F]
) extends SimexMessageHandlerAlgebra[F](url, securityService, validator) {

  override def handleValidatedSimexRequest(request: Simex): F[HttpResponseResource.HttpResponse] =
    orc
      .handleDapexMessage(request)
      .map { _ =>
        NoContent: HttpResponse
      }
      .recoverWith { e =>
        Logger[F].warn(
          s"DropOff Service HTTP Endpoint Handler Error from Orchestrator: ${e.getMessage}"
        ) *>
          (ServiceUnavailable: HttpResponse).pure[F]
      }
}
