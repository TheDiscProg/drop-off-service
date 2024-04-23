package simex.dropoff.domain.orchestrator

import cats.effect.kernel.Sync
import cats.implicits._
import org.typelevel.log4cats.Logger
import shareprice.rabbitmq.SharepriceQueue
import simex.messaging.Simex
import simex.rabbitmq.publisher.SimexMQPublisherAlgebra

class DropOffOrchestrator[F[_]: Sync: Logger](rmqPublisher: SimexMQPublisherAlgebra[F])
    extends DropOffOrchestatorAlgebra[F] {

  override def handleDapexMessage(message: Simex): F[Unit] =
    for {
      _ <- Logger[F].info(s"Publishing request to RMQ: $message")
      _ <- rmqPublisher.publishMessageToQueue(
        message,
        SharepriceQueue.queueWithName(message.destination.resource)
      )
    } yield ()
}
