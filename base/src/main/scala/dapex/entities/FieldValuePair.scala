package dapex.entities

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class FieldValuePair(field: String, value: String)

object FieldValuePair {
  implicit val encoder: Encoder[FieldValuePair] = deriveEncoder
  implicit val decoder: Decoder[FieldValuePair] = deriveDecoder
}
