package dapex.dropoff.domain.orchestrator

import dapex.dropoff.domain.rabbitmq.DapexMQPublisherAlgebra
import dapex.entities.DapexMessage

class DropOffOrchestrator[F[_]](rmqPublisher: DapexMQPublisherAlgebra[F])
    extends DropOffOrchestatorAlgebra[F] {

  override def handleDapexMessage(message: DapexMessage): F[Unit] =
    rmqPublisher.publishDapexMessage(message)
}
