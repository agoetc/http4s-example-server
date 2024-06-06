package com.example.api.http.route

import cats.effect.IO
import com.example.api.app.controller.ExampleController
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.{ HttpRoutes, circe }

object ExampleRoute:

  def route(exampleController: ExampleController): HttpRoutes[IO] =
    val dsl = new Http4sDsl[IO] {}
    import dsl.*

    HttpRoutes.of[IO] {
      case req @ GET -> Root / "example" =>
        for {
          // decode http4s.circe.CirceEntityCodec.circeEntityDecoder
          body <- req.as[ExampleController.ExampleControllerRequest]
          
          exampleResponse <- exampleController.execute(body)

          // encode http4s.circe.CirceEntityCodec.circeEntityEncoder
          resp <- Ok(exampleResponse)
        } yield resp
      case GET -> Root / "aaa" =>
        for {
          resp <- Ok("aaa")
        } yield resp
    }
