package com.example.api.http.route

import cats.data.Kleisli
import cats.effect.IO
import cats.implicits.toSemigroupKOps
import com.example.api.app.controller.ExampleController
import com.example.api.http.ControllerContainer
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*

import scala.util.chaining.scalaUtilChainingOps
import org.http4s.dsl.Http4sDsl

class ExampleRoute(
    authenticateMiddleware: AuthenticateMiddleware,
    cc: ControllerContainer
) {

  private val dsl = new Http4sDsl[IO] {}
  import dsl.*

  private val authedRoute =
    AuthedRoutes
      .of[LoginInfo, IO] { case GET -> Root / "authed-route" as loginInfo =>
        val request = ExampleController.ExampleControllerRequest(
          loginInfo.user.name,
          loginInfo.user.age
        )
        for {
          res <- cc.exampleController.execute(request)
          resp <- Ok(res)
        } yield resp
      }
      .pipe(authenticateMiddleware.buildMiddleware)

  private val publicRoute: HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case req @ GET -> Root / "example" =>
          for {
            // decode http4s.circe.CirceEntityCodec.circeEntityDecoder
            reqBody <- req.as[ExampleController.ExampleControllerRequest]
            res <- cc.exampleController.execute(reqBody)
            // encode http4s.circe.CirceEntityCodec.circeEntityEncoder
            resp <- Ok(res)
          } yield resp
        case GET -> Root / "example" / "from-db" =>
          for {
            res <- cc.exampleController.executeByDB()
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

  def route: HttpRoutes[IO] = {
    publicRoute <+> authedRoute
  }
}
