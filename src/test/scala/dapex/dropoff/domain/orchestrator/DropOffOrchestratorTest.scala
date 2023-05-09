package dapex.dropoff.domain.orchestrator

import cats.effect.IO
import dapex.dropoff.domain.rabbitmq.DapexMQPublisherAlgebra
import dapex.dropoff.fixture.DropOffFixture
import dapex.entities.DapexMessage
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DropOffOrchestratorTest
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with DropOffFixture {
  import cats.effect.unsafe.implicits.global

  val publisher = new DapexMQPublisherAlgebra[IO] {

    override def publishDapexMessage(message: DapexMessage): IO[Unit] =
      IO(())
  }

  val sut = new DropOffOrchestrator[IO](publisher)

  it should "publish message to queue" in {
    val result = sut.handleDapexMessage(selectRequestMessage).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe ()
    }
  }
}
