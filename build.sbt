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
    name := "api-app",
    libraryDependencies ++= Http4s.all ++ Seq(
      Cats.catsCore,
      CatsEffect.catsEffect,
    ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x                   => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )

lazy val apiHttp = (project in file("adapter/http"))
  .settings(
    name := "adapter-http",
    fork := true,
    libraryDependencies ++= Seq(
      Cats.catsCore,
      CatsEffect.catsEffect,
      Circe.parser,
      Circe.generic
    )
  )
