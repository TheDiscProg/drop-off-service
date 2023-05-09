package dapex.dropoff.domain.rabbitmq

import dev.profunktor.fs2rabbit.model.{ExchangeName, ExchangeType, QueueName, RoutingKey}
import enumeratum._

import scala.collection.immutable

sealed trait RabbitQueue extends EnumEntry {
  val name: QueueName
  val routingKey: RoutingKey
  val exchange: ExchangeName
  val dlx: Option[String]
  val exchangeType: ExchangeType
  val messageTTL: Option[Long]
  val consumers: Boolean
}

case object RabbitQueue extends Enum[RabbitQueue] {

  case object DAPEX_MESSAGE_QUEUE extends RabbitQueue {
    override val name: QueueName = QueueName("")
    override val routingKey: RoutingKey = RoutingKey("")
    override val exchange: ExchangeName = ExchangeName("")
    override val dlx: Option[String] = Some("dlg.")
    override val exchangeType: ExchangeType = ExchangeType.Direct
    override val messageTTL: Option[Long] = None
    override val consumers: Boolean = false
  }

  override def values: immutable.IndexedSeq[RabbitQueue] = findValues
}
