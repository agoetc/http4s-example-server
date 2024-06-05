package com.example.http4sexampleserver

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = Http4sexampleserverServer.run[IO]
