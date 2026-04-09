package example.com.app.endpoint

import cats.effect.IO
import cats.effect.std.Supervisor
import cats.syntax.all.*
import io.circe.{Decoder, Encoder}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sttp.tapir.*
import sttp.tapir.generic.auto.*
import sttp.tapir.json.circe.*
import sttp.tapir.server.ServerEndpoint

class ExampleBackGroundLogic(supervisor: Supervisor[IO]) {
  import ExampleBackGroundEndpoint.*

  private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def execute(): IO[Either[HttpErrorResponse, ExampleBackGroundEndpointResponse]] =
    (for {
      _ <- supervisor.supervise(task)
      res <- ExampleBackGroundEndpointResponse("Hello").pure[IO]
    } yield Right(res))
      .handleError(e => Left(HttpErrorResponse(e.getMessage)))

  private def task: IO[Unit] = {
    import scala.concurrent.duration._
    for {
      _ <- logger.info("Start background process")
      _ <- IO.sleep(5.seconds)
      _ <- logger.info("5 seconds passed")
    } yield ()
  }
}

class ExampleBackGroundEndpoint(logic: ExampleBackGroundLogic) {
  import ExampleBackGroundEndpoint.*

  def backgroundRun: ServerEndpoint[Any, IO] =
    endpoint.get
      .in("example" / "background-run")
      .out(jsonBody[ExampleBackGroundEndpointResponse])
      .errorOut(jsonBody[HttpErrorResponse])
      .serverLogic[IO](_ => logic.execute())
      .name("exampleBackgroundRun")
      .description("Spawn background fiber")

  def allEndpoints: List[ServerEndpoint[Any, IO]] =
    List(backgroundRun)
}

object ExampleBackGroundEndpoint {
  case class ExampleBackGroundEndpointResponse(
      message: String
  ) derives Decoder,
        Encoder
}
