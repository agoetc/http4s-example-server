package com.example.api.app.controller

import cats.effect.IO
import cats.syntax.all.*

class ExampleController:
  import com.example.api.app.controller.ExampleController.*

  def execute(req: ExampleControllerRequest): IO[ExampleControllerResponse] = {
    ExampleControllerResponse(
        s"Hello, ${req.name}, you are ${req.age} years old!"
    ).pure[IO]
  }

object ExampleController:
  import io.circe.*

  case class ExampleControllerRequest(
      name: String,
      age: Int
  ) derives Decoder

  final case class ExampleControllerResponse(
      message: String
  ) derives Encoder
