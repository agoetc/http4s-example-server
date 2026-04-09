package example.com.app.endpoint

import cats.effect.{IO, Ref}
import cats.effect.std.Supervisor
import cats.effect.unsafe.implicits.global
import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpec

import scala.concurrent.duration._

class ExampleBackGroundLogicSpec extends AnyFreeSpec with Diagrams {

  "execute" - {
    "正常系: レスポンスを即座に返す" in {
      val result = Supervisor[IO].use { supervisor =>
        val logic = new ExampleBackGroundLogic(supervisor, IO.sleep(100.millis))
        logic.execute()
      }.unsafeRunSync()

      val response = result.toOption.get
      assert(response.message == "Hello")
    }

    "正常系: Supervisorがバックグラウンドタスクを実行する" in {
      val result = (for {
        ref <- Ref.of[IO, Boolean](false)
        completed <- Supervisor[IO].use { supervisor =>
          val task = IO.sleep(100.millis) *> ref.set(true)
          val logic = new ExampleBackGroundLogic(supervisor, task)
          logic.execute() *> IO.sleep(200.millis) *> ref.get
        }
      } yield completed).unsafeRunSync()

      assert(result == true)
    }

    "正常系: Supervisor(await=true)解放時にバックグラウンドタスクの完了を待つ" in {
      val result = (for {
        ref <- Ref.of[IO, Boolean](false)
        _ <- Supervisor[IO](await = true).use { supervisor =>
          val task = IO.sleep(100.millis) *> ref.set(true)
          val logic = new ExampleBackGroundLogic(supervisor, task)
          logic.execute()
        }
        // Supervisor解放後 = fiber完了済み
        completed <- ref.get
      } yield completed).unsafeRunSync()

      assert(result == true)
    }

    "正常系: Supervisor(await=false)解放時にバックグラウンドタスクをキャンセルする" in {
      val result = (for {
        ref <- Ref.of[IO, Boolean](false)
        _ <- Supervisor[IO](await = false).use { supervisor =>
          val task = IO.sleep(100.millis) *> ref.set(true)
          val logic = new ExampleBackGroundLogic(supervisor, task)
          logic.execute()
        }
        completed <- ref.get
      } yield completed).unsafeRunSync()

      assert(result == false)
    }
  }
}
