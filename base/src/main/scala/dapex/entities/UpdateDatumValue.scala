package dapex.entities

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UpdateDatumValue(
    field: String,
    oldValue: Option[String],
    newValue: String
)

object UpdateDatumValue {
  implicit val encoder: Encoder[UpdateDatumValue] = deriveEncoder
  implicit val decoder: Decoder[UpdateDatumValue] = deriveDecoder
}
