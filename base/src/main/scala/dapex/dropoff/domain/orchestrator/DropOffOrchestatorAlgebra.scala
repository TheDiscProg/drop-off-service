package dapex.dropoff.domain.orchestrator

import dapex.entities.DapexMessage

trait DropOffOrchestatorAlgebra[F[_]] {

  def handleDapexMessage(message: DapexMessage): F[Unit]
}
