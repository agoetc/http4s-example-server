import sbt.*

object Dependencies {

  object Cats {
    val catsCore = "org.typelevel" %% "cats-core" % "2.12.0"
  }
  object CatsEffect {
    val catsEffect = "org.typelevel" %% "cats-effect" % "3.5.4"
  }

  object Http4s {
    val Http4sVersion = "0.23.27"
    val http4sEmberServer = "org.http4s" %% "http4s-ember-server" % "0.23.27"
    val http4sEmberClient = "org.http4s" %% "http4s-ember-client" % "0.23.27"
    val http4sCirce = "org.http4s" %% "http4s-circe" % "0.23.27"
    val http4sDsl = "org.http4s" %% "http4s-dsl" % "0.23.27"

    val all: Seq[ModuleID] = Seq(
      http4sEmberServer,
      http4sEmberClient,
      http4sCirce,
      http4sDsl
    )
  }

  object LogBack {
    val logBack = "ch.qos.logback" % "logback-classic" % "1.5.6"
  }

  object Circe {
    val generic = "io.circe" %% "circe-generic" % "0.14.7"
    val parser = "io.circe" %% "circe-parser" % "0.14.7"
    val literal = "io.circe" %% "circe-literal" % "0.14.7"

    val all: Seq[ModuleID] = Seq(
      generic,
      parser,
      literal
    )
  }

  object Log4Cats {
    val core = "org.typelevel" %% "log4cats-core" % "2.6.0"
    val slp4s = "org.typelevel" %% "log4cats-slf4j" % "2.6.0"

    val all: Seq[ModuleID] = Seq(
      core,
      slp4s
    )
  }
  
  object LogBackClassic {
    val logbackClassic = "ch.qos.logback" % "logback-classic" % "1.5.6"
  }

  object Pureconfig {
    val core = "com.github.pureconfig" %% "pureconfig-core" % "0.17.6"
    val ip4s = "com.github.pureconfig" %% "pureconfig-ip4s" % "0.17.6"
    val catsEffect = "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.6"
  }

  object Doobie {
    val doobieCore = "org.tpolecat" %% "doobie-core" % "1.0.0-M5"
    val doobieHikari = "org.tpolecat" %% "doobie-hikari" % "1.0.0-M5"

    val all: Seq[ModuleID] = Seq(
      doobieCore,
      doobieHikari
    )
  }

  object Auth {
    val jwtCirce = "com.github.jwt-scala" %% "jwt-circe" % "10.0.1"
  }

}
