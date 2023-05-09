package dapex.dropoff.domain.rabbitmq

import dapex.entities.DapexMessage

trait DapexMQPublisherAlgebra[F[_]] {

  def publishDapexMessage(message: DapexMessage): F[Unit]
}
