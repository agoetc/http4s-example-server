import Dependencies.*

inThisBuild(
  Seq(
    organization := "example.com",
    scalaVersion := "3.4.2",
    version := "0.0.1-SNAPSHOT"
  )
)

lazy val httpServer = (project in file("http-server"))
  .settings(
    name := "httpServer",
    fork := true,
    libraryDependencies ++=
      Http4s.all ++
        Circe.all ++
        Log4Cats.all ++
        PureConfig.all ++
        Seq(
          Auth.jwtCirce,
          LogBackClassic.logbackClassic,
          Cats.catsCore,
          CatsEffect.catsEffect
        ),
    packMain := Map("httpServer" -> "com.example.http.server.Main"),
    packJvmOpts := Map(
      "http4s-server" -> Seq("-Xms256M", "-Xmx512M", "-Djava.awt.headless=true")
    )
  )
  .dependsOn(app, domain, adapter)
  .enablePlugins(PackPlugin)

lazy val app = (project in file("app"))
  .settings(
    name := "app",
    libraryDependencies ++=
      Circe.all ++
        Log4Cats.all ++
        Seq(
          Cats.catsCore,
          CatsEffect.catsEffect
        )
  )
  .dependsOn(domain, usecase)

lazy val adapter = (project in file("adapter"))
  .settings(
    name := "adapter",
    libraryDependencies ++=
      Doobie.all ++
        Seq(
          Cats.catsCore,
          CatsEffect.catsEffect,
          MySQL.mysqlConnectorJava,
          Auth.jwtCirce,
          Auth.jwks,
          Http4s.http4sCirce,
          Http4s.http4sEmberClient
        )
  )
  .dependsOn(domain)

lazy val usecase = (project in file("usecase"))
  .settings(
    name := "usecase",
    libraryDependencies ++=
      Seq(
        Cats.catsCore,
        CatsEffect.catsEffect
      )
  )
  .dependsOn(domain)

lazy val domain = (project in file("domain"))
  .settings(
    name := "domain",
    libraryDependencies ++=
      // これらのライブラリは、decode / encode 等の mapping を定義するために使う。
      // RDB との接続や、Configへの直接アクセスは行わない。
      Seq(
        Cats.catsCore,
        CatsEffect.catsEffect,
        PureConfig.core,
        Circe.parser,
        Doobie.doobieCore
      )
  )
