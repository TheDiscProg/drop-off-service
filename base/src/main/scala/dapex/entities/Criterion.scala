package dapex.entities

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Criterion(
    field: String,
    value: String,
    operator: String
)

object Criterion {
  implicit val encoder: Encoder[Criterion] = deriveEncoder
  implicit val decoder: Decoder[Criterion] = deriveDecoder
}
