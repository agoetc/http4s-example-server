package com.example.api.http.route

import cats.effect.IO
import com.example.api.app.controller.ExampleController
import com.example.api.http.ControllerContainer
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.{ HttpRoutes, circe }

object ExampleRoute:

  def route(cc: ControllerContainer): HttpRoutes[IO] =
    val dsl = new Http4sDsl[IO] {}
    import dsl.*

    HttpRoutes.of[IO] {
      case req @ GET -> Root / "example" =>
        for {
          // decode http4s.circe.CirceEntityCodec.circeEntityDecoder
          reqBody <- req.as[ExampleController.ExampleControllerRequest]

          res <- cc.exampleController.execute(reqBody)

          // encode http4s.circe.CirceEntityCodec.circeEntityEncoder
          resp <- Ok(res)
        } yield resp
      case GET -> Root / "example" / "http-run" =>
        for {
          res <- cc.exampleHttpRunController.execute()
          resp <- Ok(res)
        } yield resp
      case GET -> Root / "aaa" =>
        for {
          resp <- Ok("aaa")
        } yield resp
    }
