package example.com.http.server.route

import cats.data.Kleisli
import cats.effect.IO
import cats.implicits.toSemigroupKOps
import example.com.app.controller.ExampleController
import example.com.domain.UserId
import example.com.domain.auth.LoginInfo
import example.com.http.server.ControllerContainer
import org.http4s.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dsl.Http4sDsl
import org.http4s.server.AuthMiddleware

import scala.util.chaining.scalaUtilChainingOps

class ExampleRoute(
    dsl: Http4sDsl[IO],
    authMiddleware: AuthMiddleware[IO, LoginInfo],
    cc: ControllerContainer
) {
  import dsl.*

  private val healthCheckRoute: HttpRoutes[IO] =
    HttpRoutes.of[IO] { case GET -> Root / "health-check" =>
      println(authMiddleware)
      Ok("OK")
    }

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
      .pipe(authMiddleware(_))

  private val publicRoute: HttpRoutes[IO] =
    HttpRoutes
      .of[IO] {
        case req @ GET -> Root / "example" =>
          for {
            reqBody <- req.as[ExampleController.ExampleControllerRequest]
            res <- cc.exampleController.execute(reqBody)
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
        case GET -> Root / "example" / "background-run" =>
          for {
            res <- cc.exampleBackGroundController.execute()
            resp <- Ok(res)
          } yield resp
        case GET -> Root / "use-opaque-type" =>
          val userId = UserId(1)
          for {
            resp <- Ok(userId)
          } yield resp
      }

  def route: HttpRoutes[IO] = {
    healthCheckRoute <+> publicRoute <+> authedRoute
  }
}
