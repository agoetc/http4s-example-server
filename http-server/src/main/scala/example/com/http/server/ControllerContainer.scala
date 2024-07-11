package example.com.http.server

import cats.effect.IO
import org.http4s.client.Client
import doobie.util.transactor.Transactor
import example.com.adapter.exampleApi.ExampleApiAdapterImpl
import example.com.adapter.rdb.UserRepositoryImpl
import example.com.app.controller.*
import example.com.usecase.{ExecuteExampleApiUsecase, GetUserUsecase}
import example.com.usecase.auth.GetLoginInfoBySubUsecase

class ControllerContainer(client: Client[IO], xa: Transactor[IO]) {

  private lazy val userRepository = new UserRepositoryImpl(xa)

  private lazy val exampleApiAdapter = new ExampleApiAdapterImpl(client)

  private lazy val getUserUsecase = new GetUserUsecase(userRepository)

  private lazy val executeExampleApiUsecase = new ExecuteExampleApiUsecase(
    exampleApiAdapter
  )

  lazy val getLoginInfoBySubUsecase = new GetLoginInfoBySubUsecase(
    userRepository
  )

  lazy val exampleController = new ExampleController(getUserUsecase)

  lazy val exampleHttpRunController = new ExampleHttpRunController(
    executeExampleApiUsecase
  )

  lazy val exampleBackGroundController = new ExampleBackGroundController()
}
