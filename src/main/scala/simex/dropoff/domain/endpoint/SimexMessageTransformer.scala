package simex.dropoff.domain.endpoint

import io.scalaland.chimney.dsl._
import simex.guardrail.definitions.SimexMessage
import simex.messaging.Simex

import scala.util.Try

object SimexMessageTransformer {
  def transformRequest(request: SimexMessage): Option[Simex] =
    Try {
      request
        .into[Simex]
        .withFieldComputed(_.originator.messageTTL, r => r.originator.messageTtl)
        .transform
    }.toOption

  def transformDapexMessage(simexMessage: Simex): SimexMessage =
    simexMessage
      .into[SimexMessage]
      .withFieldComputed(_.originator.messageTtl, s => s.originator.messageTTL)
      .transform

}
