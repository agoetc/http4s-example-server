package example.com.app.controller

import cats.effect.IO
import cats.syntax.all.*
import example.com.domain.UserId
import example.com.usecase.GetUserUsecase
import sttp.tapir.server.ServerEndpoint

class ExampleController(getUserUsecase: GetUserUsecase):
  import ExampleController.*

  def execute(req: ExampleControllerRequest): IO[ExampleControllerResponse] = {
    ExampleControllerResponse(
      s"Hello, ${req.name}, you are ${req.age} years old!"
    ).pure[IO]
  }

  def executeByDB(): IO[ExampleControllerResponse] = {
    val userId = UserId(1)
    getUserUsecase.execute(userId).map { user =>
      ExampleControllerResponse(
        s"Hello, ${user.get.name}, you are ${user.get.age} years old!"
      )
    }
  }

  // --- Server Endpoints ---

  val exampleEndpoint: ServerEndpoint[Any, IO] =
    TapirEndpoints.example.serverLogic[IO] { req =>
      execute(req)
        .map(Right(_))
        .handleError(e => Left(HttpErrorResponse(e.getMessage)))
    }

  val exampleFromDbEndpoint: ServerEndpoint[Any, IO] =
    TapirEndpoints.exampleFromDb.serverLogic[IO] { _ =>
      executeByDB()
        .map(Right(_))
        .handleError(e => Left(HttpErrorResponse(e.getMessage)))
    }

  val useOpaqueTypeEndpoint: ServerEndpoint[Any, IO] =
    TapirEndpoints.useOpaqueType.serverLogicSuccess[IO] { _ =>
      val userId = UserId(1)
      IO.pure(userId.value)
    }

object ExampleController:
  import io.circe.*

  case class ExampleControllerRequest(
      name: String,
      age: Int
  ) derives Decoder,
        Encoder

  final case class ExampleControllerResponse(
      message: String
  ) derives Decoder,
        Encoder
