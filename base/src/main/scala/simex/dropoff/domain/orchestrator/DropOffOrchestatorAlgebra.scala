package simex.dropoff.domain.orchestrator

import simex.messaging.Simex

trait DropOffOrchestatorAlgebra[F[_]] {

  def handleDapexMessage(message: Simex): F[Unit]
}
