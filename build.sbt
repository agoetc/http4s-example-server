import Dependencies.*

lazy val root = (project in file("."))
  .settings(
    organization := "com.example",
    name := "http4s-example-server",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.3.3",
    libraryDependencies ++=
      Http4s.all ++
        Seq(
          Circe.generic,
          Circe.parser
        ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x                   => (assembly / assemblyMergeStrategy).value.apply(x)
    }
  )
