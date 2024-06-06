package com.example.api.app

import cats.effect.{ IO, IOApp }
import com.comcast.ip4s.*
import com.example.api.app.controller.ExampleController
import com.example.api.app.route.Routes
import fs2.io.net.Network
import org.http4s.HttpApp
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Server
import org.http4s.server.middleware.Logger

import scala.language.postfixOps
import scala.util.chaining.*

object Main extends IOApp.Simple:
  val run: IO[Unit] = {
    for {
      client <- EmberClientBuilder.default[IO].build
      httpApp: HttpApp[IO] =
        Routes
          .exampleRoutes(new ExampleController)
          .orNotFound
          .pipe(Logger.httpApp(logHeaders = true, logBody = true))

      _: Server <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(httpApp)
          .build
    } yield ()
  }.useForever
