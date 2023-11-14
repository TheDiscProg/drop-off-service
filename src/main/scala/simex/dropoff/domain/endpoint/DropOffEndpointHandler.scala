package simex.dropoff.domain.endpoint

import cats.effect.MonadCancel
import cats.implicits._
import org.typelevel.log4cats.Logger
import simex.dropoff.domain.ServiceResponse
import simex.dropoff.domain.orchestrator.DropOffOrchestatorAlgebra
import simex.guardrail.definitions.SimexMessageRequest
import simex.guardrail.dropoff.DropoffResource.DropOffRequestResponse
import simex.guardrail.dropoff.DropoffResource.DropOffRequestResponse.{
  BadRequest,
  NoContent,
  ServiceUnavailable
}
import simex.guardrail.dropoff.{DropoffHandler, DropoffResource}
import simex.messaging.Simex

class DropOffEndpointHandler[F[_]: Logger: MonadCancel[*[_], Throwable]](
    orc: DropOffOrchestatorAlgebra[F]
) extends DropoffHandler[F] {

  override def dropOffRequest(
      respond: DropoffResource.DropOffRequestResponse.type
  )(body: SimexMessageRequest): F[DropoffResource.DropOffRequestResponse] =
    for {
      _ <- Logger[F].info(s"Drop-Off Message: $body")
      messageOption = SimexMessageTransformer.transformRequest(body)
      response <- messageOption match {
        case Some(message) =>
          if (Simex.checkEndPointValidity(message))
            orc
              .handleDapexMessage(message)
              .map { _ =>
                ServiceResponse[DropOffRequestResponse](NoContent)
              }
              .recoverWith { e =>
                Logger[F].warn(s"DropOffEndpoint Handler error: [${e.getMessage}]") *>
                  ServiceResponse[DropOffRequestResponse](ServiceUnavailable).pure[F]
              }
          else
            ServiceResponse[DropOffRequestResponse](BadRequest)
              .pure[F]
        case None =>
          ServiceResponse[DropOffRequestResponse](BadRequest).pure[F]
      }
    } yield response.returnType

}
