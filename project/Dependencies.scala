import sbt._

object Dependencies {

  lazy val all = Seq(
    "simex" %% "simex-web-service" % "1.0.0",
    "thediscprog" %% "simex-utils" % "0.3.0",
    "Shareprice" %% "shareprice-config" % "0.11.3",
    "org.typelevel" %% "cats-effect" % "3.4.8",
    "org.http4s" %% "http4s-dsl" % "0.23.18",
    "org.http4s" %% "http4s-ember-server" % "0.23.18",
    "org.http4s" %% "http4s-ember-client" % "0.23.18",
    "org.http4s" %% "http4s-circe" % "0.23.18",
    "io.circe" %% "circe-core" % "0.14.5",
    "io.circe" %% "circe-generic" % "0.14.5",
    "io.circe" %% "circe-parser" % "0.14.5",
    "io.circe" %% "circe-refined" % "0.14.5",
    "io.circe" %% "circe-generic-extras" % "0.14.3",
    "io.circe" %% "circe-config" % "0.10.0",
    "eu.timepit" %% "refined" % "0.10.2",
    "org.typelevel" %% "munit-cats-effect-2" % "1.0.7" % Test,
    "org.scalactic" %% "scalactic" % "3.2.15",
    "org.scalatest" %% "scalatest" % "3.2.15" % Test,
    "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % Test,
    "com.beachape" %% "enumeratum" % "1.7.2",
    "com.beachape" %% "enumeratum-circe" % "1.7.2",
    "io.scalaland" %% "chimney" % "0.8.4",
    "ch.qos.logback" % "logback-classic" % "1.4.11",
    "org.typelevel" %% "log4cats-core"    % "2.6.0",
    "org.typelevel" %% "log4cats-slf4j"   % "2.6.0"
  )
}
