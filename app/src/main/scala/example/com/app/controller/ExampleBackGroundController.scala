package example.com.app.controller

import cats.effect.IO
import cats.syntax.all.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class ExampleBackGroundController {

  import ExampleBackGroundController.*

  // loggerは、インスタンス化した際に名前が決まるため各クラスでインスタンス化を行う。
  // 出力例) httpServer [io-compute-2] INFO  d.c.j.a.c.ExampleBackGroundController - Start background process
  private val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def execute(): IO[ExampleBackGroundControllerResponse] = {
    for {
      _ <- stream.compile.drain.start // Fiberを利用してバックグラウンドで処理を行う。
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

}

object ExampleBackGroundController:
  import io.circe.*

  final case class ExampleBackGroundControllerResponse(
      message: String
  ) derives Decoder,
        Encoder
