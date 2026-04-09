package example.com.app.endpoint

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import example.com.domain.exampleApi.ExampleApiAdapter
import example.com.domain.exampleApi.ExampleApiAdapter.{ExampleRequest, ExampleResponse}
import example.com.usecase.ExecuteExampleApiUsecase
import org.scalatest.BeforeAndAfterEach
import org.scalatest.diagrams.Diagrams
import org.scalatest.freespec.AnyFreeSpec

class ExampleHttpRunLogicSpec extends AnyFreeSpec with Diagrams with BeforeAndAfterEach {

  private val adapter = new StubExampleApiAdapter
  private val logic = new ExampleHttpRunLogic(new ExecuteExampleApiUsecase(adapter))

  override def beforeEach(): Unit = {
    super.beforeEach()
    adapter.response = IO.pure(ExampleResponse("default"))
  }

  "execute" - {
    "正常系: 外部APIからレスポンスを返す" in {
      adapter.response = IO.pure(ExampleResponse("Hello, Alice!"))

      val result = logic.execute().unsafeRunSync()

      val response = result.toOption.get
      assert(response.message == "Hello, Alice!")
    }

    "異常系: API呼び出し失敗時にエラーを返す" in {
      adapter.response = IO.raiseError(new RuntimeException("Connection refused"))

      val result = logic.execute().unsafeRunSync()

      val error = result.left.toOption.get
      assert(error.message == "Connection refused")
    }
  }

  private class StubExampleApiAdapter extends ExampleApiAdapter {
    var response: IO[ExampleResponse] = IO.pure(ExampleResponse("default"))
    def example(req: ExampleRequest): IO[ExampleResponse] = response
  }
}
