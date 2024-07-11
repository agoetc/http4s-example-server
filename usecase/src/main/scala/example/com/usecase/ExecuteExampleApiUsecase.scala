package example.com.usecase

import cats.effect.IO
import example.com.domain.exampleApi.ExampleApiAdapter
import example.com.domain.exampleApi.ExampleApiAdapter.{ExampleRequest, ExampleResponse}

/** 実行することが目的となっているのでexecuteというClass名になっている
  *
  * 実際にはUsecaseらしい名称になる
  */
class ExecuteExampleApiUsecase(adapter: ExampleApiAdapter) {

  def execute(req: ExampleRequest): IO[ExampleResponse] = {
    adapter.example(req)
  }

}
