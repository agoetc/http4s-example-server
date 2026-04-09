package example.com.http.server.route

import cats.effect.IO
import example.com.adapter.auth0.Auth0Validator
import example.com.app.endpoint.{ExampleEndpoint, HttpErrorResponse}
import example.com.domain.auth.LoginInfo
import example.com.http.server.EndpointModule
import org.http4s.HttpRoutes
import pdi.jwt.JwtClaim
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

import scala.util.{Failure, Success}

class ExampleRoute(
    auth0Validator: Auth0Validator,
    module: EndpointModule,
    getLoginInfo: JwtClaim => IO[Either[String, LoginInfo]]
):

  // --- Health check ---
  private val healthCheckSE: ServerEndpoint[Any, IO] =
    endpoint.get
      .in("health-check")
      .out(stringBody)
      .serverLogicSuccess[IO](_ => IO.pure("OK"))
      .name("healthCheck")
      .description("Health check endpoint")

  // --- Authed route ---
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
    endpoint.get
      .in("authed-route")
      .securityIn(auth.bearer[String]())
      .out(jsonBody[ExampleEndpoint.ExampleEndpointResponse])
      .errorOut(jsonBody[HttpErrorResponse])
      .serverSecurityLogic[LoginInfo, IO](securityLogic)
      .serverLogic { loginInfo => _ =>
        val request = ExampleEndpoint.ExampleEndpointRequest(
          loginInfo.user.name,
          loginInfo.user.age
        )
        module.exampleEndpoint.logic.execute(request)
      }
      .name("authedRoute")
      .description("JWT authenticated endpoint")

  // --- 全ServerEndpointを集約 ---
  val serverEndpoints: List[ServerEndpoint[Any, IO]] =
    List(healthCheckSE) ++
      module.exampleEndpoint.allEndpoints ++
      module.exampleHttpRunEndpoint.allEndpoints ++
      module.exampleBackGroundEndpoint.allEndpoints ++
      List(authedRouteSE)

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
