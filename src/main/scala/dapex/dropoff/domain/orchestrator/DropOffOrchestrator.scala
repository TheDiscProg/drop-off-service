package dapex.dropoff.domain.orchestrator

import cats.effect.kernel.Sync
import dapex.messaging.DapexMessage
import dapex.rabbitmq.publisher.DapexMQPublisherAlgebra
import org.typelevel.log4cats.Logger
import cats.implicits._
import shareprice.rabbitmq.SharepriceQueue

class DropOffOrchestrator[F[_]: Sync: Logger](rmqPublisher: DapexMQPublisherAlgebra[F])
    extends DropOffOrchestatorAlgebra[F] {

  override def handleDapexMessage(message: DapexMessage): F[Unit] =
    for {
      _ <- Logger[F].info(s"Publishing message to RMQ: $message")
      _ <- rmqPublisher.publishMessageToQueue(
        message,
        SharepriceQueue.queueWithName(message.endpoint.resource)
      )
    } yield ()
}
