package example.com.app.endpoint

import cats.effect.{IO, Ref}
import cats.effect.std.Supervisor
import cats.effect.unsafe.implicits.global
import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpec

import scala.concurrent.duration._

class ExampleBackGroundLogicSpec extends AnyFreeSpec with Diagrams {

  "execute" - {
    "正常系: バックグラウンドタスクを生成しレスポンスを返す" in {
      val result = Supervisor[IO](await = true).use { supervisor =>
        val logic = new ExampleBackGroundLogic(supervisor)
        logic.execute()
      }.unsafeRunSync()

      val response = result.toOption.get
      assert(response.message == "Hello")
    }
  }

  "Supervisor" - {
    "await=true: 解放時にfiberの完了を待つ" in {
      val result = (for {
        ref <- Ref.of[IO, Boolean](false)
        _ <- Supervisor[IO](await = true).use { supervisor =>
          supervisor.supervise(IO.sleep(100.millis) *> ref.set(true))
        }
        completed <- ref.get
      } yield completed).unsafeRunSync()

      assert(result == true)
    }

    "await=false: 解放時にfiberをキャンセルする" in {
      val result = (for {
        ref <- Ref.of[IO, Boolean](false)
        _ <- Supervisor[IO](await = false).use { supervisor =>
          supervisor.supervise(IO.sleep(3.seconds) *> ref.set(true))
        }
        completed <- ref.get
      } yield completed).unsafeRunSync()

      assert(result == false)
    }
  }
}
