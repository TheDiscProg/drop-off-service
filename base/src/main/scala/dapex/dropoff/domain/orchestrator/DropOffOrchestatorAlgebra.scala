package dapex.dropoff.domain.orchestrator

import dapex.messaging.DapexMessage

trait DropOffOrchestatorAlgebra[F[_]] {

  def handleDapexMessage(message: DapexMessage): F[Unit]
}
