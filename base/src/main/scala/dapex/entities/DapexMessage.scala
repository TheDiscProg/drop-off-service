package dapex.entities

import dapex.entities.Method.UNSUPPORTED
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DapexMessage(
    endpoint: Endpoint,
    client: Client,
    originator: Originator,
    criteria: Vector[Criterion],
    update: Vector[UpdateDatumValue],
    insert: Vector[FieldValuePair],
    process: Vector[FieldValuePair],
    response: Option[Response]
)

object DapexMessage {
  implicit val encoder: Encoder[DapexMessage] = deriveEncoder
  implicit val decoder: Decoder[DapexMessage] = deriveDecoder

  def isValid(message: DapexMessage): Boolean = {
    val method = Method.fromString(message.endpoint.method)
    method match {
      case Method.SELECT =>
        checkSelectValidity(message)
      case Method.UPDATE =>
        checkUpdateValidity(message)
      case Method.INSERT =>
        checkInsertValidity(message)
      case Method.PROCESS =>
        checkProcessValidity(message)
      case Method.RESPONSE =>
        checkResponseValidity(message)
      case Method.UNSUPPORTED =>
        false
    }
  }

  def checkSelectValidity(message: DapexMessage): Boolean =
    checkEndPointValidity(message)

  def checkUpdateValidity(message: DapexMessage): Boolean =
    checkEndPointValidity(message) &&
      message.update.nonEmpty

  def checkInsertValidity(message: DapexMessage): Boolean =
    checkEndPointValidity(message) &&
      message.insert.nonEmpty

  def checkProcessValidity(message: DapexMessage): Boolean =
    checkEndPointValidity(message)

  def checkResponseValidity(message: DapexMessage): Boolean =
    checkEndPointValidity(message) &&
      message.response.isDefined

  def checkEndPointValidity(message: DapexMessage): Boolean = {
    val isResourceDefined = Option(message.endpoint.resource) match {
      case Some(value) => value.nonEmpty
      case _ => false
    }

    val isMethodDefined = Method.fromString(message.endpoint.method) !=
      UNSUPPORTED

    isResourceDefined && isMethodDefined
  }
}
