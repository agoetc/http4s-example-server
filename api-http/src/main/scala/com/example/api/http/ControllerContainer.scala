package com.example.api.http

import cats.effect.IO
import com.example.api.app.controller.{ ExampleController, ExampleHttpRunController }
import org.http4s.client.Client

class ControllerContainer(client: Client[IO]) {

  lazy val exampleController = new ExampleController
  lazy val exampleHttpRunController = new ExampleHttpRunController(client)
}
