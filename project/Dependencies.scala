import sbt.*

object Dependencies {

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
  }

  object Log4Cats {
    val core = "org.typelevel" %% "log4cats-core" % "2.6.0"
    val slf4j = "org.typelevel" %% "log4cats-slf4j" % "2.6.0"
  }

  object Pureconfig {
    val core = "com.github.pureconfig" %% "pureconfig-core" % "0.17.6"
    val ip4s = "com.github.pureconfig" %% "pureconfig-ip4s" % "0.17.6"
    val catsEffect = "com.github.pureconfig" %% "pureconfig-cats-effect" % "0.17.6"
  }

}
