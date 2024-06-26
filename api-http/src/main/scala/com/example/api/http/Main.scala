package com.example.api.http

import cats.effect.{ IO, IOApp }
import com.comcast.ip4s.*
import com.example.api.http.route.{ AuthenticateConfig, AuthenticateMiddleware, ExampleRoute }
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.{ HttpApp, Response }
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.language.postfixOps
import scala.util.chaining.*

object Main extends IOApp.Simple:
  val run: IO[Unit] = {

    val authenticateConfig: AuthenticateConfig =
      AuthenticateConfig("secretKey") // TODO Configから取得

    for {
      client <- EmberClientBuilder.default[IO].build

      logger = Slf4jLogger.getLogger[IO]

      cc = ControllerContainer(client)

      authenticateMiddleware = AuthenticateMiddleware(authenticateConfig)

      route = ExampleRoute(authenticateMiddleware, cc)

      httpApp: HttpApp[IO] =
        route.route.orNotFound
          .pipe(org.http4s.server.middleware.Logger.httpApp(logHeaders = true, logBody = true))

      _: Server <-
        EmberServerBuilder
          .default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withLogger(logger)
          .withHttpApp(httpApp)
          .withErrorHandler(errorHandler)
          .build
    } yield ()
  }.useForever
