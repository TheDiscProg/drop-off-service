package simex.dropoff.domain.endpoint

import cats.effect.IO
import cats.implicits.catsSyntaxApplicativeId
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import simex.dropoff.domain.orchestrator.DropOffOrchestatorAlgebra
import simex.dropoff.domain.security.DropOffSecurityService
import simex.dropoff.domain.validation.DropOffRequestValidator
import simex.dropoff.fixture.DefaultFutureSetting
import simex.messaging.Method.INSERT
import simex.messaging.Simex
import simex.test.SimexTestFixture
import simex.webservice.HttpResponseResource.HttpResponse
import simex.webservice.HttpResponseResource.HttpResponse.{
  BadRequest,
  Forbidden,
  NoContent,
  ServiceUnavailable
}

class DropOffEndPointRequestHandlerTest
    extends AnyFlatSpec
    with Matchers
    with DefaultFutureSetting
    with SimexTestFixture {
  private implicit def unsafeLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  import cats.effect.unsafe.implicits.global

  val URL = "drop-off"

  val orc = new DropOffOrchestatorAlgebra[IO] {
    override def handleDapexMessage(message: Simex): IO[Unit] =
      if (message.destination.method == "select")
        ().pure[IO]
      else
        IO.raiseError(new Throwable("problem"))
  }

  val request = authenticationRequest

  val sut = new DropOffEndPointRequestHandler[IO](
    URL,
    new DropOffSecurityService[IO](),
    orc,
    new DropOffRequestValidator()
  )

  it should "return NoContent for a valid request" in {
    val result = sut.handleSimexRequest(HttpResponse)(request).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe NoContent
    }
  }

  it should "return Forbidden when it fails security check" in {
    val forbiddenRequest = request.copy(client = authenticationRequest.client.copy(clientId = "  "))

    val result = sut.handleSimexRequest(HttpResponse)(forbiddenRequest).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe Forbidden
    }
  }

  it should "return Forbidden when the destination resource is incorrect" in {
    val forbiddenRequest =
      request.copy(destination = request.destination.copy(resource = "unavailable"))

    val result = sut.handleSimexRequest(HttpResponse)(forbiddenRequest).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe Forbidden
    }
  }

  it should "handle an invalid request that fails to decode" in {
    val badRequest = request.copy(destination = null)

    val result = sut.handleSimexRequest(HttpResponse)(badRequest).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe BadRequest
    }
  }

  it should "return Service Unavailable when a service is not available" in {
    val request = getMessage(INSERT, None, Vector())

    val result = sut.handleSimexRequest(HttpResponse)(request).unsafeToFuture()

    whenReady(result) { r =>
      r shouldBe ServiceUnavailable
    }
  }
}
