package example.com.app.controller

import cats.effect.IO
import cats.syntax.all.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import sttp.tapir.server.ServerEndpoint

class ExampleBackGroundController {

  import ExampleBackGroundController.*

  private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def execute(): IO[ExampleBackGroundControllerResponse] = {
    for {
      _ <- stream.compile.drain.start
      res <- ExampleBackGroundControllerResponse(
        s"Hello"
      ).pure[IO]
    } yield res
  }

  private def stream: fs2.Stream[IO, Unit] = {
    import scala.concurrent.duration._
    fs2.Stream.eval(
      for {
        _ <- logger.info("Start background process")
        _ <- IO.sleep(5.seconds)
        _ <- logger.info("5 seconds passed")
      } yield ()
    )
  }

  // --- Server Endpoint ---

  val backgroundRunEndpoint: ServerEndpoint[Any, IO] =
    TapirEndpoints.exampleBackgroundRun.serverLogic[IO] { _ =>
      execute()
        .map(Right(_))
        .handleError(e => Left(HttpErrorResponse(e.getMessage)))
    }
}

object ExampleBackGroundController:
  import io.circe.*

  final case class ExampleBackGroundControllerResponse(
      message: String
  ) derives Decoder,
        Encoder
