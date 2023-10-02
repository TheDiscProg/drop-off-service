package dapex.dropoff.domain.orchestrator

import cats.effect.IO
import dapex.dropoff.fixture.DropOffFixture
import dapex.messaging.DapexMessage
import dapex.rabbitmq.RabbitQueue
import dapex.rabbitmq.publisher.DapexMQPublisherAlgebra
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class DropOffOrchestratorTest
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with DropOffFixture {
  import cats.effect.unsafe.implicits.global
  private implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  val publisher = new DapexMQPublisherAlgebra[IO] {

    override def publishMessageToQueue(message: DapexMessage, queue: RabbitQueue): IO[Unit] = IO(())
  }

  val sut = new DropOffOrchestrator[IO](publisher)

  it should "publish message to queue" in {
    val result = sut.handleDapexMessage(selectRequestMessage).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe ()
    }
  }
}
