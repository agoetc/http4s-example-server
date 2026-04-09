package example.com.app.endpoint

import scala.compiletime.uninitialized

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import example.com.domain.{User, UserId, UserRepository}
import example.com.domain.auth.Sub
import example.com.usecase.GetUserUsecase
import org.scalatest.BeforeAndAfterEach
import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpec

class ExampleLogicSpec extends AnyFreeSpec with Diagrams with BeforeAndAfterEach {

  private var userRepository: StubUserRepository = uninitialized
  private var logic: ExampleLogic = uninitialized

  override def beforeEach(): Unit = {
    super.beforeEach()
    userRepository = new StubUserRepository(None)
    logic = new ExampleLogic(new GetUserUsecase(userRepository))
  }

  "execute" - {
    "正常系: リクエストからグリーティングメッセージを返す" in {
      val req = ExampleEndpoint.ExampleEndpointRequest("Alice", 30)

      val result = logic.execute(req).unsafeRunSync()

      val response = result.toOption.get
      assert(response.message == "Hello, Alice, you are 30 years old!")
    }
  }

  "executeByDB" - {
    "正常系: DBからユーザー情報を取得して返す" in {
      val user = User(UserId(1), Sub("sub-1"), "Bob", 25)
      userRepository.result = Some(user)
      logic = new ExampleLogic(new GetUserUsecase(userRepository))

      val result = logic.executeByDB().unsafeRunSync()

      val response = result.toOption.get
      assert(response.message == "Hello, Bob, you are 25 years old!")
    }

    "異常系: ユーザーが見つからない場合エラーを返す" in {
      val result = logic.executeByDB().unsafeRunSync()

      assert(result.isLeft)
    }
  }

  private class StubUserRepository(var result: Option[User]) extends UserRepository {
    def find(id: UserId): IO[Option[User]] = IO.pure(result)
    def findBySub(sub: Sub): IO[Option[User]] = IO.pure(result)
  }
}
