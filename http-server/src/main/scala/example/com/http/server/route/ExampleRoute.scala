package example.com.http.server.route

import cats.effect.IO
import example.com.adapter.auth0.Auth0Validator
import example.com.app.controller.{ExampleController, HttpErrorResponse, TapirEndpoints}
import example.com.domain.auth.LoginInfo
import example.com.http.server.ControllerContainer
import org.http4s.HttpRoutes
import pdi.jwt.JwtClaim
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.util.{Failure, Success}

class ExampleRoute(
    auth0Validator: Auth0Validator,
    cc: ControllerContainer,
    getLoginInfo: JwtClaim => IO[Either[String, LoginInfo]]
):

  // --- Health check (serverLogic不要のシンプルなもの) ---
  private val healthCheckSE: ServerEndpoint[Any, IO] =
    TapirEndpoints.healthCheck.serverLogicSuccess[IO](_ => IO.pure("OK"))

  // --- Authed route (Auth0Validatorがhttp-server層にあるためここで定義) ---
  private def securityLogic(
      token: String
  ): IO[Either[HttpErrorResponse, LoginInfo]] =
    auth0Validator.validateJwt(token) match
      case Failure(e) =>
        IO.pure(Left(HttpErrorResponse(s"Invalid token: ${e.getMessage}")))
      case Success(claim) =>
        getLoginInfo(claim).map {
          case Right(info) => Right(info)
          case Left(msg) => Left(HttpErrorResponse(s"Authentication failed: $msg"))
        }

  private val authedRouteSE: ServerEndpoint[Any, IO] =
    TapirEndpoints.authedRoute
      .serverSecurityLogic[LoginInfo, IO](securityLogic)
      .serverLogic { loginInfo => _ =>
        val request = ExampleController.ExampleControllerRequest(
          loginInfo.user.name,
          loginInfo.user.age
        )
        cc.exampleController
          .execute(request)
          .map(Right(_))
          .handleError(e => Left(HttpErrorResponse(e.getMessage)))
      }

  // --- 全ServerEndpointを集約 ---
  val serverEndpoints: List[ServerEndpoint[Any, IO]] = List(
    healthCheckSE,
    cc.exampleController.exampleEndpoint,
    cc.exampleController.exampleFromDbEndpoint,
    cc.exampleController.useOpaqueTypeEndpoint,
    cc.exampleHttpRunController.httpRunEndpoint,
    cc.exampleBackGroundController.backgroundRunEndpoint,
    authedRouteSE
  )

  // --- Swagger UI ---
  val swaggerEndpoints: List[ServerEndpoint[Any, IO]] =
    SwaggerInterpreter().fromServerEndpoints[IO](
      serverEndpoints,
      "http4s-example-server",
      "0.0.1"
    )

  // --- Convert to http4s HttpRoutes ---
  val routes: HttpRoutes[IO] =
    Http4sServerInterpreter[IO]().toRoutes(serverEndpoints ++ swaggerEndpoints)
