package example.com.app.controller

import example.com.app.controller.ExampleBackGroundController.ExampleBackGroundControllerResponse
import example.com.app.controller.ExampleController.{
  ExampleControllerRequest,
  ExampleControllerResponse
}
import example.com.app.controller.ExampleHttpRunController.ExampleHttpRunControllerResponse
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*

object TapirEndpoints:

  // 1. GET /health-check
  val healthCheck: PublicEndpoint[Unit, Unit, String, Any] =
    endpoint.get
      .in("health-check")
      .out(stringBody)
      .name("healthCheck")
      .description("Health check endpoint")

  // 2. GET /example
  val example
      : PublicEndpoint[ExampleControllerRequest, HttpErrorResponse, ExampleControllerResponse, Any] =
    endpoint.get
      .in("example")
      .in(jsonBody[ExampleControllerRequest])
      .errorOut(jsonBody[HttpErrorResponse])
      .out(jsonBody[ExampleControllerResponse])
      .name("example")
      .description("Echo example")

  // 3. GET /example/from-db
  val exampleFromDb: PublicEndpoint[Unit, HttpErrorResponse, ExampleControllerResponse, Any] =
    endpoint.get
      .in("example" / "from-db")
      .errorOut(jsonBody[HttpErrorResponse])
      .out(jsonBody[ExampleControllerResponse])
      .name("exampleFromDb")
      .description("Fetch user from DB")

  // 4. GET /example/http-run
  val exampleHttpRun
      : PublicEndpoint[Unit, HttpErrorResponse, ExampleHttpRunControllerResponse, Any] =
    endpoint.get
      .in("example" / "http-run")
      .errorOut(jsonBody[HttpErrorResponse])
      .out(jsonBody[ExampleHttpRunControllerResponse])
      .name("exampleHttpRun")
      .description("Call external API")

  // 5. GET /example/background-run
  val exampleBackgroundRun
      : PublicEndpoint[Unit, HttpErrorResponse, ExampleBackGroundControllerResponse, Any] =
    endpoint.get
      .in("example" / "background-run")
      .errorOut(jsonBody[HttpErrorResponse])
      .out(jsonBody[ExampleBackGroundControllerResponse])
      .name("exampleBackgroundRun")
      .description("Spawn background fiber")

  // 6. GET /use-opaque-type
  val useOpaqueType: PublicEndpoint[Unit, Unit, Long, Any] =
    endpoint.get
      .in("use-opaque-type")
      .out(jsonBody[Long])
      .name("useOpaqueType")
      .description("Return opaque UserId")

  // 7. GET /authed-route (Bearer JWT)
  val authedRoute
      : Endpoint[String, Unit, HttpErrorResponse, ExampleControllerResponse, Any] =
    endpoint.get
      .in("authed-route")
      .securityIn(auth.bearer[String]())
      .errorOut(jsonBody[HttpErrorResponse])
      .out(jsonBody[ExampleControllerResponse])
      .name("authedRoute")
      .description("JWT authenticated endpoint")
