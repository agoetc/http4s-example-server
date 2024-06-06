package com.example.api.app.route

import cats.effect.IO
import com.example.api.app.controller.ExampleController
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.{ HttpRoutes, circe }

object ExampleRoute:

  def route(E: ExampleController): HttpRoutes[IO] =
    val dsl = new Http4sDsl[IO] {}
    import dsl.*

    HttpRoutes.of[IO] {
      case req @ GET -> Root / "example" =>
        for {
          body <- req.as[ExampleController.ExampleControllerRequest]
          exampleResponse <- E.execute(body)
          resp <- Ok(exampleResponse)
        } yield resp
      case GET -> Root / "aaa" =>
        for {
          resp <- Ok("aa")
        } yield resp
    }
