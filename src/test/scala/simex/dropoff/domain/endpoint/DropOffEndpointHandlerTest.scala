package simex.dropoff.domain.endpoint

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import simex.dropoff.domain.orchestrator.DropOffOrchestatorAlgebra
import simex.dropoff.fixture.DefaultFutureSetting
import simex.guardrail.dropoff.DropoffResource.DropOffRequestResponse
import simex.guardrail.dropoff.DropoffResource.DropOffRequestResponse.{
  BadRequest,
  NoContent,
  ServiceUnavailable
}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import simex.messaging.Method.INSERT
import simex.messaging.Simex
import simex.test.SimexTestFixture

class DropOffEndpointHandlerTest
    extends AnyFlatSpec
    with Matchers
    with DefaultFutureSetting
    with SimexTestFixture {
  private implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  import cats.effect.unsafe.implicits.global

  val orc = new DropOffOrchestatorAlgebra[IO] {
    override def handleDapexMessage(message: Simex): IO[Unit] =
      if (message.endpoint.method == "select")
        ().pure[IO]
      else
        IO.raiseError[Unit](new Throwable("problem!"))
  }

  val sut = new DropOffEndpointHandler[IO](orc)

  it should "handle a valid request" in {
    val request = SimexMessageTransformer.transformDapexMessage(authenticationRequest)

    val result = sut.dropOffRequest(DropOffRequestResponse)(request).unsafeToFuture()

    whenReady(result) { r: DropOffRequestResponse =>
      r shouldBe NoContent
    }
  }

  it should
    "handle an invalid request" in {
      val request = SimexMessageTransformer.transformDapexMessage(authenticationRequest)
      val badRequest = request.copy(endpoint = request.endpoint.copy(method = ""))

      val result = sut.dropOffRequest(DropOffRequestResponse)(badRequest).unsafeToFuture()

      whenReady(result) { r =>
        r shouldBe BadRequest
      }
    }

  it should "handle an invalid request that fails to decode" in {
    val request = SimexMessageTransformer.transformDapexMessage(authenticationRequest)
    val badRequest = request.copy(endpoint = null)

    val result = sut.dropOffRequest(DropOffRequestResponse)(badRequest).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe BadRequest
    }
  }

  it should "handle error when a service is not available" in {
    val request = SimexMessageTransformer.transformDapexMessage(getMessage(INSERT, None, Vector()))

    val result = sut.dropOffRequest(DropOffRequestResponse)(request).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe ServiceUnavailable
    }
  }
}
