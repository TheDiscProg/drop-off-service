package dapex.entities

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class Client(
    clientId: String,
    requestId: String,
    sourceEndpoint: String,
    authorisation: String
)

object Client {
  implicit val encoder: Encoder[Client] = deriveEncoder
  implicit val decoder: Decoder[Client] = deriveDecoder
}
