package dapex

import cats.data.NonEmptyList
import cats.effect.{Bracket, ConcurrentEffect, ContextShift, Resource, Sync, Timer}
import cats.{Applicative, Monad, MonadError, Parallel}
import dapex.config.ServerConfiguration
import dapex.guardrail.healthcheck.{HealthcheckHandler, HealthcheckResource}
import dapex.server.domain.healthcheck.{
  HealthCheckService,
  HealthChecker,
  HealthcheckAPIHandler,
  SelfHealthCheck
}
import io.circe.config.parser
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.{Logger => Log4CatsLogger}

object AppServer {

  def createServer[F[
      _
  ]: Monad: ConcurrentEffect: Timer: Log4CatsLogger: ContextShift: Parallel: Applicative: Bracket[*[
    _
  ], Throwable]: Sync: MonadError[*[_], Throwable]](): Resource[F, Server] =
    for {
      conf <- Resource.eval(parser.decodePathF[F, ServerConfiguration](path = "server"))

      // Health checkers
      checkers = NonEmptyList.of[HealthChecker[F]](SelfHealthCheck[F])
      healthCheckers = HealthCheckService(checkers)
      healthRoutes = new HealthcheckResource().routes(
        new HealthcheckAPIHandler[F](healthCheckers)
      )

      // Routes and HTTP App
      allRoutes = healthRoutes.orNotFound
      httpApp = Logger.httpApp(logHeaders = true, logBody = true)(allRoutes)

      // Build server
      server <- EmberServerBuilder.default
        .withPort(conf.http.port.value)
        .withHost(conf.http.host.value)
        .withHttpApp(httpApp)
        .build
    } yield server
}
