import Dependencies._

inThisBuild(
  Seq(
    organization := "com.example",
    scalaVersion := "3.4.2",
    version := "0.0.1-SNAPSHOT"
  )
)

lazy val apiApp = (project in file("api-app"))
  .settings(
    name := "apiApp",
    libraryDependencies ++=
      Circe.all ++
        Seq(
          Http4s.http4sEmberClient,
          Http4s.http4sCirce,
          Cats.catsCore,
          CatsEffect.catsEffect
        ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x                   => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )

lazy val apiHttp = (project in file("api-http"))
  .settings(
    name := "apiHttp",
    fork := true,
    libraryDependencies ++=
      Http4s.all ++
        Circe.all ++
        Log4Cats.all ++
        Seq(
          Auth.jwtCirce,
          LogBackClassic.logbackClassic,
          Cats.catsCore,
          CatsEffect.catsEffect
        )
  ) dependsOn apiApp

lazy val adapter = (project in file("adapter"))
  .settings(
    name := "adapter",
    libraryDependencies ++=
      Doobie.all ++
        Seq(
          Cats.catsCore,
          CatsEffect.catsEffect
        )
  ) dependsOn (apiApp, domain)

lazy val domain = (project in file("domain"))
  .settings(
    name := "domain",
    libraryDependencies ++=
      Seq(
        Cats.catsCore,
        CatsEffect.catsEffect
      )
  )
