package com.example.api.app

import cats.effect.{ IO, IOApp }

object Main extends IOApp.Simple:
  val run = Server.run[IO]
