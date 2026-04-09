package example.com.app.endpoint

import cats.effect.IO
import cats.syntax.all.*
import example.com.domain.UserId
import example.com.usecase.GetUserUsecase
import io.circe.{Decoder, Encoder}
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

class ExampleLogic(getUserUsecase: GetUserUsecase) {
  import ExampleEndpoint.*

  def execute(
      req: ExampleEndpointRequest
  ): IO[Either[HttpErrorResponse, ExampleEndpointResponse]] =
    IO.pure(
      Right(
        ExampleEndpointResponse(
          s"Hello, ${req.name}, you are ${req.age} years old!"
        )
      )
    )

  def executeByDB(): IO[Either[HttpErrorResponse, ExampleEndpointResponse]] = {
    val userId = UserId(1)
    getUserUsecase
      .execute(userId)
      .map { user =>
        Right(
          ExampleEndpointResponse(
            s"Hello, ${user.get.name}, you are ${user.get.age} years old!"
          )
        )
      }
      .handleError(e => Left(HttpErrorResponse(e.getMessage)))
  }
}

class ExampleEndpoint(val logic: ExampleLogic) {
  import ExampleEndpoint.*

  def example: ServerEndpoint[Any, IO] =
    endpoint.get
      .in("example")
      .in(jsonBody[ExampleEndpointRequest])
      .out(jsonBody[ExampleEndpointResponse])
      .errorOut(jsonBody[HttpErrorResponse])
      .serverLogic[IO](logic.execute)
      .name("example")
      .description("Echo example")

  def exampleFromDb: ServerEndpoint[Any, IO] =
    endpoint.get
      .in("example" / "from-db")
      .out(jsonBody[ExampleEndpointResponse])
      .errorOut(jsonBody[HttpErrorResponse])
      .serverLogic[IO](_ => logic.executeByDB())
      .name("exampleFromDb")
      .description("Fetch user from DB")

  def useOpaqueType: ServerEndpoint[Any, IO] =
    endpoint.get
      .in("use-opaque-type")
      .out(jsonBody[Long])
      .serverLogicSuccess[IO] { _ =>
        val userId = UserId(1)
        IO.pure(userId.value)
      }
      .name("useOpaqueType")
      .description("Return opaque UserId")

  def allEndpoints: List[ServerEndpoint[Any, IO]] =
    List(example, exampleFromDb, useOpaqueType)
}

object ExampleEndpoint {
  case class ExampleEndpointRequest(
      name: String,
      age: Int
  ) derives Decoder,
        Encoder

  case class ExampleEndpointResponse(
      message: String
  ) derives Decoder,
        Encoder
}
