package dapex.entities

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class Insert(data: List[FieldValuePair])

object Insert {
  implicit val encoder: Encoder[Insert] = deriveEncoder
  implicit val decoder: Decoder[Insert] = deriveDecoder
}
