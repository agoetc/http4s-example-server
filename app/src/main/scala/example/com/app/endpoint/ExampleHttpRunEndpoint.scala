package example.com.app.endpoint

import cats.effect.IO
import example.com.domain.exampleApi.ExampleApiAdapter
import example.com.usecase.ExecuteExampleApiUsecase
import io.circe.{Decoder, Encoder}
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

class ExampleHttpRunLogic(usecase: ExecuteExampleApiUsecase) {
  import ExampleHttpRunEndpoint.*

  def execute(): IO[Either[HttpErrorResponse, ExampleHttpRunEndpointResponse]] = {
    val req = ExampleApiAdapter.ExampleRequest("Alice", 20)
    usecase
      .execute(req)
      .map(res => Right(ExampleHttpRunEndpointResponse(res.message)))
      .handleError(e => Left(HttpErrorResponse(e.getMessage)))
  }
}

class ExampleHttpRunEndpoint(logic: ExampleHttpRunLogic) {
  import ExampleHttpRunEndpoint.*

  def httpRun: ServerEndpoint[Any, IO] =
    endpoint.get
      .in("example" / "http-run")
      .out(jsonBody[ExampleHttpRunEndpointResponse])
      .errorOut(jsonBody[HttpErrorResponse])
      .serverLogic[IO](_ => logic.execute())
      .name("exampleHttpRun")
      .description("Call external API")

  def allEndpoints: List[ServerEndpoint[Any, IO]] =
    List(httpRun)
}

object ExampleHttpRunEndpoint {
  case class ExampleHttpRunEndpointResponse(
      message: String
  ) derives Decoder,
        Encoder
}
