package dapex.dropoff.domain.endpoint

import cats.effect.MonadCancel
import cats.implicits._
import dapex.dropoff.domain.ServiceResponse
import dapex.dropoff.domain.orchestrator.DropOffOrchestatorAlgebra
import dapex.guardrail.definitions.DapexMessageRequest
import dapex.guardrail.dropoff.DropoffResource.DropOffRequestResponse
import dapex.guardrail.dropoff.DropoffResource.DropOffRequestResponse.{
  BadRequest,
  NoContent,
  ServiceUnavailable
}
import dapex.guardrail.dropoff.{DropoffHandler, DropoffResource}
import dapex.messaging.DapexMessage
import org.typelevel.log4cats.Logger

class DropOffEndpointHandler[F[_]: Logger: MonadCancel[*[_], Throwable]](
    orc: DropOffOrchestatorAlgebra[F]
) extends DropoffHandler[F] {

  override def dropOffRequest(
      respond: DropoffResource.DropOffRequestResponse.type
  )(body: DapexMessageRequest): F[DropoffResource.DropOffRequestResponse] =
    for {
      _ <- Logger[F].info(s"Drop-Off Message: $body")
      messageOption = DapexTransformer.transformRequest(body)
      response <- messageOption match {
        case Some(message) =>
          if (DapexMessage.isValid(message))
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
