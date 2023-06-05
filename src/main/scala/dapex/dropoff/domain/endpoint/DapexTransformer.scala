package dapex.dropoff.domain.endpoint

import dapex.guardrail.definitions.DapexMessageRequest
import dapex.messaging.DapexMessage
import io.scalaland.chimney.dsl._

import scala.util.Try

object DapexTransformer {
  def transformRequest(request: DapexMessageRequest): Option[DapexMessage] =
    Try {
      request
        .into[DapexMessage]
        .transform
    }.toOption

  def transformDapexMessage(dapexMessage: DapexMessage): DapexMessageRequest =
    dapexMessage
      .into[DapexMessageRequest]
      .transform

}
