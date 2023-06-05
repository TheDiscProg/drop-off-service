package dapex.dropoff.domain.rabbitmq

import dapex.messaging.DapexMessage

trait DapexMQPublisherAlgebra[F[_]] {

  def publishDapexMessage(message: DapexMessage): F[Unit]
}
