package com.example.api.app.controller

import cats.effect.IO
import cats.syntax.all.*
import com.example.usecase.GetUserUsecase

class ExampleController(getUserUsecase: GetUserUsecase):
  import com.example.api.app.controller.ExampleController.*

  def execute(req: ExampleControllerRequest): IO[ExampleControllerResponse] = {
    ExampleControllerResponse(
      s"Hello, ${req.name}, you are ${req.age} years old!"
    ).pure[IO]
  }

  def executeByDB(): IO[ExampleControllerResponse] = {
    val userId = 1
    getUserUsecase.execute(userId).map { user =>
      ExampleControllerResponse(
        s"Hello, ${user.get.name}, you are ${user.get.age} years old!"
      )
    }
  }

object ExampleController:
  import io.circe.*

  case class ExampleControllerRequest(
      name: String,
      age: Int
  ) derives Decoder,
        Encoder // http requestで使いたくなったのでEncoderを追加

  final case class ExampleControllerResponse(
      message: String
  ) derives Decoder,
        Encoder
