package example.com.app.controller

import cats.effect.IO
import cats.syntax.all.*
import example.com.domain.exampleApi.ExampleApiAdapter
import example.com.usecase.ExecuteExampleApiUsecase
import sttp.tapir.server.ServerEndpoint

class ExampleHttpRunController(usecase: ExecuteExampleApiUsecase) {
  import ExampleHttpRunController.*

  def execute(): IO[ExampleHttpRunControllerResponse] = {
    val req = ExampleApiAdapter.ExampleRequest("Alice", 20)
    usecase.execute(req).map { res =>
      ExampleHttpRunControllerResponse(res.message)
    }
  }

  // --- Server Endpoint ---

  val httpRunEndpoint: ServerEndpoint[Any, IO] =
    TapirEndpoints.exampleHttpRun.serverLogic[IO] { _ =>
      execute()
        .map(Right(_))
        .handleError(e => Left(HttpErrorResponse(e.getMessage)))
    }
}

object ExampleHttpRunController {

  import io.circe.*

  case class ExampleHttpRunControllerRequest(
      name: String,
      age: Int
  ) derives Decoder,
        Encoder

  final case class ExampleHttpRunControllerResponse(
      message: String
  ) derives Decoder,
        Encoder
}
