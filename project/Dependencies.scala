import sbt.*

object Dependencies {

  object Cats {
    val catsCore = "org.typelevel" %% "cats-core" % "2.13.0"
  }
  object CatsEffect {
    val catsEffect = "org.typelevel" %% "cats-effect" % "3.7.0"
  }

  object Http4s {
    val http4sEmberServer = "org.http4s" %% "http4s-ember-server" % "0.23.33"
    val http4sEmberClient = "org.http4s" %% "http4s-ember-client" % "0.23.33"
    val http4sCirce = "org.http4s" %% "http4s-circe" % "0.23.33"
    val http4sDsl = "org.http4s" %% "http4s-dsl" % "0.23.33"

    val all: Seq[ModuleID] = Seq(
      http4sEmberServer,
      http4sEmberClient,
      http4sCirce,
      http4sDsl
    )
  }

  object LogBack {
    val logBack = "ch.qos.logback" % "logback-classic" % "1.5.32"
  }

  object Circe {
    val generic = "io.circe" %% "circe-generic" % "0.14.15"
    val parser = "io.circe" %% "circe-parser" % "0.14.15"
    val literal = "io.circe" %% "circe-literal" % "0.14.15"

    val all: Seq[ModuleID] = Seq(
      generic,
      parser,
      literal
    )
  }

  object Log4Cats {
    val core = "org.typelevel" %% "log4cats-core" % "2.8.0"
    val slp4s = "org.typelevel" %% "log4cats-slf4j" % "2.8.0"

    val all: Seq[ModuleID] = Seq(
      core,
      slp4s
    )
  }

  object LogBackClassic {
    val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.5.32"
  }

  object PureConfig {
    val catsEffect =
      "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.10"
    val core = "com.github.pureconfig" %% "pureconfig-core" % "0.17.10"
    val ip4s = "com.github.pureconfig" %% "pureconfig-ip4s" % "0.17.10"

    val all: Seq[ModuleID] = Seq(
      catsEffect,
      core,
      ip4s
    )
  }

  object Doobie {
    val doobieCore = "org.tpolecat" %% "doobie-core" % "1.0.0-M5"
    val doobieHikari = "org.tpolecat" %% "doobie-hikari" % "1.0.0-M5"

    val all: Seq[ModuleID] = Seq(
      doobieCore,
      doobieHikari
    )
  }

  object MySQL {
    val mysqlConnectorJava = "mysql" % "mysql-connector-java" % "8.0.33"
  }

  object Tapir {
    private val version = "1.13.15"
    val http4sServer = "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % version
    val jsonCirce = "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % version
    val swaggerUiBundle = "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % version

    val all: Seq[ModuleID] = Seq(http4sServer, jsonCirce, swaggerUiBundle)
  }

  object ScalaTest {
    val scalatest = "org.scalatest" %% "scalatest" % "3.2.20" % Test
  }

  object Auth {
    val jwtCirce = "com.github.jwt-scala" %% "jwt-circe" % "11.0.4"
    val jwks = "com.auth0" % "jwks-rsa" % "0.23.0"

  }

}
