package dapex.dropoff.domain.rabbitmq

import cats.Applicative
import cats.data.Kleisli
import cats.effect._
import cats.implicits._
import dapex.dropoff.domain.rabbitmq.RabbitQueue.DAPEX_MESSAGE_QUEUE
import dapex.entities.DapexMessage
import dev.profunktor.fs2rabbit.effects.MessageEncoder
import dev.profunktor.fs2rabbit.interpreter.RabbitClient
import dev.profunktor.fs2rabbit.json.Fs2JsonEncoder
import dev.profunktor.fs2rabbit.model.AmqpMessage
import io.circe.Encoder
import org.typelevel.log4cats.Logger

class DapexMQPublisher[F[_]: Sync: Logger](rabbitClient: RabbitClient[F])
    extends DapexMQPublisherAlgebra[F] {
  import DapexMQPublisher._

  override def publishDapexMessage(message: DapexMessage): F[Unit] =
    for {
      _ <- Logger[F].info(s"Publishing message to RMQ: $message")
      _ <- rabbitClient.createConnectionChannel
        .use { implicit channel =>
          rabbitClient
            .createPublisher(
              DAPEX_MESSAGE_QUEUE.exchange,
              DAPEX_MESSAGE_QUEUE.routingKey
            )(channel, encoder[F, DapexMessage])
            .flatMap { f =>
              f(message)
            }
        }

    } yield ()

}

object DapexMQPublisher {
  object ioEncoder extends Fs2JsonEncoder

  implicit def encoder[F[_]: Applicative, A](implicit enc: Encoder[A]): MessageEncoder[F, A] =
    Kleisli { (a: A) =>
      val message = enc(a).noSpaces
      AmqpMessage.stringEncoder[F].run(message)
    }
}
