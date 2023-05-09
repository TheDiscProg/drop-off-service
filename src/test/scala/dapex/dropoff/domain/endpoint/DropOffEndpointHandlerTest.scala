package dapex.dropoff.domain.endpoint

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import dapex.dropoff.domain.orchestrator.DropOffOrchestatorAlgebra
import dapex.dropoff.fixture.DropOffFixture
import dapex.entities.DapexMessage
import dapex.guardrail.dropoff.DropoffResource.DropOffRequestResponse
import dapex.guardrail.dropoff.DropoffResource.DropOffRequestResponse.{
  BadRequest,
  NoContent,
  ServiceUnavailable
}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class DropOffEndpointHandlerTest
    extends AnyFlatSpec
    with Matchers
    with ScalaFutures
    with DropOffFixture {
  private implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  import cats.effect.unsafe.implicits.global

  val orc = new DropOffOrchestatorAlgebra[IO] {
    override def handleDapexMessage(message: DapexMessage): IO[Unit] =
      if (message.endpoint.method == "select")
        ().pure[IO]
      else
        IO.raiseError[Unit](new Throwable("problem!"))
  }

  val sut = new DropOffEndpointHandler[IO](orc)

  it should "handle a valid request" in {
    val request = DapexTransformer.transformDapexMessage(selectRequestMessage)

    val result = sut.dropOffRequest(DropOffRequestResponse)(request).unsafeToFuture()

    whenReady(result) { r: DropOffRequestResponse =>
      r shouldBe NoContent
    }
  }

  it should
    "handle an invalid request" in {
      val request = DapexTransformer.transformDapexMessage(selectRequestMessage)
      val badRequest = request.copy(endpoint = request.endpoint.copy(method = null))

      val result = sut.dropOffRequest(DropOffRequestResponse)(badRequest).unsafeToFuture()

      whenReady(result) { r =>
        r shouldBe BadRequest
      }
    }

  it should "handle an invalid request that fails to decode" in {
    val request = DapexTransformer.transformDapexMessage(selectRequestMessage)
    val badRequest = request.copy(endpoint = null)

    val result = sut.dropOffRequest(DropOffRequestResponse)(badRequest).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe BadRequest
    }
  }

  it should "handle error when a service is not available" in {
    val request = DapexTransformer.transformDapexMessage(updateRequestMessage)

    val result = sut.dropOffRequest(DropOffRequestResponse)(request).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe ServiceUnavailable
    }
  }
}
