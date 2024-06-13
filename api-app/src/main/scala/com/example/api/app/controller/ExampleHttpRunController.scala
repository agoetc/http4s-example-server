package com.example.api.app.controller

import cats.effect.IO
import cats.syntax.all.*
import com.example.api.app.controller.ExampleController.{ ExampleControllerRequest, ExampleControllerResponse }
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.circe.jsonOf
import org.http4s.client.Client

class ExampleHttpRunController(client: Client[IO]):
  import com.example.api.app.controller.ExampleHttpRunController.*

  def execute(): IO[ExampleHttpRunControllerResponse] = {

    /** TODO: adapterに移動 http4s-ember-clientを使ってリクエストを送信している
      */
    val request = Request[IO](
      method = Method.GET,
      uri = org.http4s.Uri.unsafeFromString("http://localhost:8080/example")
    ).withEntity(
      ExampleControllerRequest("John", 30)
    )

    client
      .expect(request)(jsonOf[IO, ExampleControllerResponse])
      .map { res =>
        ExampleHttpRunControllerResponse(
          message = res.message
        )
      }
  }

object ExampleHttpRunController:

  import io.circe.*

  case class ExampleHttpRunControllerRequest(
      name: String,
      age: Int
  ) derives Decoder,
        Encoder

  final case class ExampleHttpRunControllerResponse(
      message: String
  ) derives Decoder,
        Encoder
