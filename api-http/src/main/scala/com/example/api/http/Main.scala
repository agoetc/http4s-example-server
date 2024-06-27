package com.example.api.http

import cats.effect.{IO, IOApp}
import com.comcast.ip4s.*
import com.example.api.http.route.{
  AuthenticateConfig,
  AuthenticateMiddleware,
  ExampleRoute
}
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.{HttpApp, Response}
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.language.postfixOps
import scala.util.chaining.*
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContextExecutor

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*
import pureconfig.module.catseffect.syntax.*

import cats.effect.kernel.Resource
import pureconfig.ConfigSource
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor

case class DBConfig(
    driver: String,
    url: String,
    username: String,
    password: String
) derives ConfigReader

object Main extends IOApp.Simple:
  val run: IO[Unit] = {

    val ec: ExecutionContextExecutor = ExecutionContext.global

    val logger = Slf4jLogger.getLogger[IO]

    for {
      client <- EmberClientBuilder.default[IO].build

      hikariConfig <- this.hikariConfigResource(ec)

      xa <- HikariTransactor.fromHikariConfig[IO](hikariConfig, ec)

      cc = ControllerContainer(client, xa)

      authenticateMiddleware: AuthenticateMiddleware = {
        val authenticateConfig =
          AuthenticateConfig("secretKey") // TODO Configから取得

        AuthenticateMiddleware(authenticateConfig)
      }

      route = ExampleRoute(authenticateMiddleware, cc)

      httpApp: HttpApp[IO] =
        route.route.orNotFound
          .pipe(
            org.http4s.server.middleware.Logger
              .httpApp(logHeaders = true, logBody = true)
          )

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

  def hikariConfigResource(
      ec: ExecutionContext
  ): Resource[IO, HikariConfig] = {
    ConfigSource.default // TODO Configのload処理が散らばってしまいそうなので、ConfigContainerみたいなの作った方が良さそう
      .at("db.default")
      .loadF[IO, DBConfig]()
      .map { config =>
        new HikariConfig().tap { c =>
          c.setDriverClassName(config.driver)
          c.setJdbcUrl(config.url)
          c.setUsername(config.username)
          c.setPassword(config.password)
        }
      }
      .pipe(Resource.eval)
  }
