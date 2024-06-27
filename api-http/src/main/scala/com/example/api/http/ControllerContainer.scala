package com.example.api.http

import cats.effect.IO
import com.example.api.app.controller.{
  ExampleController,
  ExampleHttpRunController
}
import org.http4s.client.Client
import com.example.usecase.GetUserUsecase
import com.example.adapter.rdb.UserRepositoryImpl

import doobie.util.transactor.Transactor

class ControllerContainer(client: Client[IO], xa: Transactor[IO]) {

  lazy val userRepository = new UserRepositoryImpl(xa)

  lazy val getUserUsecase = new GetUserUsecase(userRepository)

  lazy val exampleController = new ExampleController(getUserUsecase)

  lazy val exampleHttpRunController = new ExampleHttpRunController(client)
}
