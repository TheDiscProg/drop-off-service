package simex.dropoff.domain.endpoint

import cats.effect.MonadCancel
import cats.syntax.all._
import org.typelevel.log4cats.Logger
import simex.dropoff.domain.orchestrator.DropOffOrchestatorAlgebra
import simex.dropoff.domain.validation
import simex.dropoff.domain.validation.DropOffRequestValidatorAlgebra
import simex.messaging.Simex
import simex.webservice.HttpResponseResource
import simex.webservice.HttpResponseResource.HttpResponse
import simex.webservice.HttpResponseResource.HttpResponse.{
  BadRequest,
  Forbidden,
  NoContent,
  ServiceUnavailable
}
import simex.webservice.handler.SimexMessageHandlerAlgebra
import simex.webservice.security.SecurityResponseResource.SecurityResponse
import simex.webservice.security.{SecurityResponseResource, SimexMessageSecurityServiceAlgebra}
class DropOffEndPointRequestHandler[F[_]: MonadCancel[*[_], Throwable]: Logger](
    url: String,
    securityService: SimexMessageSecurityServiceAlgebra[F],
    orc: DropOffOrchestatorAlgebra[F],
    validator: DropOffRequestValidatorAlgebra
) extends SimexMessageHandlerAlgebra[F](url, securityService) {

  override def handleSimexRequest(
      respond: HttpResponseResource.HttpResponse.type
  )(message: Simex): F[HttpResponseResource.HttpResponse] =
    for {
      _ <- Logger[F].info(s"DropOff Service HTTP Endpoint Handler - received request [$message]")
      messageValidation = validator.validate(message)
      response <- messageValidation match {
        case validation.ValidationFailed(status) =>
          Logger[F].warn(s"DropOff Service HTTP Endpoint Handler: Bad request: $status") *>
            (BadRequest: HttpResponse).pure[F]
        case validation.ValidationPassed(_) =>
          for {
            securityCheck <- securityService.handleSimexRequest(
              SecurityResponseResource.SecurityResponse
            )(message)
            _ <- Logger[F].info(s"DropOff Service HTTP Endpoint Handler Security: [$securityCheck]")
            resp <- securityCheck match {
              case SecurityResponse.SecurityFailed => (Forbidden: HttpResponse).pure[F]
              case SecurityResponse.SecurityPassed =>
                orc
                  .handleDapexMessage(message)
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
          } yield resp
      }
    } yield response
}
