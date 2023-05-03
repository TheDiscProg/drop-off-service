import sbt._

object Dependencies {

  val http4sV = "0.22.6"
  val circeV = "0.14.3"
  val logbackClassicV = "1.2.3"
  val munitCatsEffectV = "0.12.0"
  val kindProjectorV = "0.13.2"
  val betterMonadicForV = "0.3.1"
  val circeConfigV = "0.10.0"
  val refinedV = "0.10.3"
  val circeExtrasV = "0.14.3"
  val scalaTestV = "3.2.15"
  val scalaTestPlusV = "3.2.15.0"

  lazy val all = Seq(
    "org.http4s" %% "http4s-dsl" % http4sV,
    "org.http4s" %% "http4s-ember-server" % http4sV,
    "org.http4s" %% "http4s-ember-client" % http4sV,
    "org.http4s" %% "http4s-circe" % http4sV,
    "io.circe" %% "circe-core" % circeV,
    "io.circe" %% "circe-generic" % circeV,
    "io.circe" %% "circe-parser" % circeV,
    "io.circe" %% "circe-refined" % circeV,
    "io.circe" %% "circe-generic-extras" % circeExtrasV,
    "io.circe" %% "circe-config" % circeConfigV,
    "eu.timepit" %% "refined" % refinedV,
    "ch.qos.logback" % "logback-classic" % logbackClassicV,
    "org.typelevel" %% "munit-cats-effect-2" % munitCatsEffectV % Test,
    "org.scalactic" %% "scalactic" % scalaTestV,
    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "org.scalatestplus" %% "mockito-4-6" % scalaTestPlusV % Test
  )
}
