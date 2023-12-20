package simex

import cats.Parallel
import cats.data.NonEmptyList
import cats.effect.std.Dispatcher
import cats.effect.{Async, Resource}
import cats.implicits._
import com.comcast.ip4s._
import io.circe.config.parser
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.{Logger => Log4CatsLogger}
import shareprice.config.ServerConfiguration
import simex.dropoff.domain.endpoint.DropOffEndpointHandler
import simex.dropoff.domain.orchestrator.DropOffOrchestrator
import simex.guardrail.dropoff.DropoffResource
import simex.guardrail.healthcheck.HealthcheckResource
import simex.rabbitmq.Rabbit
import simex.rabbitmq.publisher.SimexMQPublisher
import simex.server.domain.healthcheck.{
  HealthCheckService,
  HealthChecker,
  HealthcheckAPIHandler,
  SelfHealthCheck
}

object AppServer {

  def createServer[F[
      _
  ]: Async: Log4CatsLogger: Parallel](): Resource[F, Server] =
    for {
      conf <- Resource.eval(parser.decodePathF[F, ServerConfiguration](path = "server"))

      //RabbitMQ Client and publisher
      rmqDispatcher <- Dispatcher.parallel
      client <- Resource.eval(Rabbit.getRabbitClient(conf.rabbitMQ, rmqDispatcher))
      rmqPublisher = new SimexMQPublisher[F](client)

      // Orchestrator
      orchestrator = new DropOffOrchestrator[F](rmqPublisher)

      // Endpoint handler
      endpointHandler = new DropOffEndpointHandler[F](orchestrator)
      dropOffRoutes = new DropoffResource().routes(endpointHandler)

      // Health checkers
      checkers = NonEmptyList.of[HealthChecker[F]](SelfHealthCheck[F])
      healthCheckers = HealthCheckService(checkers)
      healthRoutes = new HealthcheckResource().routes(
        new HealthcheckAPIHandler[F](healthCheckers)
      )

      // Routes and HTTP App
      allRoutes = (healthRoutes <+> dropOffRoutes).orNotFound
      httpApp = Logger.httpApp(logHeaders = true, logBody = true)(allRoutes)

      // Build server
      httpPort = Port.fromInt(conf.http.port)
      httpHost = Ipv4Address.fromString(conf.http.hostAddress)
      server <- EmberServerBuilder.default
        .withPort(httpPort.getOrElse(port"8080"))
        .withHost(httpHost.getOrElse(ipv4"0.0.0.0"))
        .withHttpApp(httpApp)
        .build
    } yield server
}
