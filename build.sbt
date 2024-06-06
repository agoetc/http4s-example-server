import Dependencies.*

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
        Seq(
          Cats.catsCore,
          CatsEffect.catsEffect
        )
  ) dependsOn apiApp
