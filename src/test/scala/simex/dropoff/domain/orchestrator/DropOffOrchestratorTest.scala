package simex.dropoff.domain.orchestrator

import cats.effect.IO
import simex.rabbitmq.RabbitQueue
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import simex.messaging.Simex
import simex.rabbitmq.publisher.SimexMQPublisherAlgebra
import simex.test.SimexTestFixture

class DropOffOrchestratorTest
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with SimexTestFixture {
  import cats.effect.unsafe.implicits.global
  private implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  val publisher = new SimexMQPublisherAlgebra[IO] {

    override def publishMessageToQueue(message: Simex, queue: RabbitQueue): IO[Unit] = IO(())
  }

  val sut = new DropOffOrchestrator[IO](publisher)

  it should "publish message to queue" in {
    val result = sut.handleDapexMessage(authenticationRequest).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe ()
    }
  }
}
