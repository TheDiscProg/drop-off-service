package dapex.entities

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Originator(
    clientId: String,
    requestId: String,
    sourceEndpoint: String
)

object Originator {
  implicit val encoder: Encoder[Originator] = deriveEncoder
  implicit val decoder: Decoder[Originator] = deriveDecoder
}
