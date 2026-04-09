package example.com.http.server

import cats.effect.IO
import cats.effect.std.Supervisor
import org.http4s.client.Client
import doobie.util.transactor.Transactor
import example.com.adapter.exampleApi.ExampleApiAdapterImpl
import example.com.adapter.rdb.UserRepositoryImpl
import example.com.app.endpoint.*
import example.com.usecase.{ExecuteExampleApiUsecase, GetUserUsecase}
import example.com.usecase.auth.GetLoginInfoBySubUsecase

class EndpointModule(client: Client[IO], xa: Transactor[IO], supervisor: Supervisor[IO]) {

  private lazy val userRepository = new UserRepositoryImpl(xa)

  private lazy val exampleApiAdapter = new ExampleApiAdapterImpl(client)

  private lazy val getUserUsecase = new GetUserUsecase(userRepository)

  private lazy val executeExampleApiUsecase = new ExecuteExampleApiUsecase(
    exampleApiAdapter
  )

  lazy val getLoginInfoBySubUsecase = new GetLoginInfoBySubUsecase(
    userRepository
  )

  // Logic
  private lazy val exampleLogic = new ExampleLogic(getUserUsecase)
  private lazy val exampleHttpRunLogic = new ExampleHttpRunLogic(executeExampleApiUsecase)
  private lazy val exampleBackGroundLogic = new ExampleBackGroundLogic(supervisor)

  // Endpoint
  lazy val exampleEndpoint = new ExampleEndpoint(exampleLogic)
  lazy val exampleHttpRunEndpoint = new ExampleHttpRunEndpoint(exampleHttpRunLogic)
  lazy val exampleBackGroundEndpoint = new ExampleBackGroundEndpoint(exampleBackGroundLogic)
}
