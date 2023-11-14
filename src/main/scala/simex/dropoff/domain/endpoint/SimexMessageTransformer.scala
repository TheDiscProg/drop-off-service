package simex.dropoff.domain.endpoint

import io.scalaland.chimney.dsl._
import simex.guardrail.definitions.SimexMessageRequest
import simex.messaging.Simex

import scala.util.Try

object SimexMessageTransformer {
  def transformRequest(request: SimexMessageRequest): Option[Simex] =
    Try {
      request
        .into[Simex]
        .transform
    }.toOption

  def transformDapexMessage(simexMessage: Simex): SimexMessageRequest =
    simexMessage
      .into[SimexMessageRequest]
      .transform

}
