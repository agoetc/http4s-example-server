package example.com.http.server

import cats.data.EitherT
import cats.effect.*
import com.comcast.ip4s.*
import com.zaxxer.hikari.HikariConfig
import example.com.adapter.auth0.Auth0Validator
import example.com.domain.auth.Sub
import example.com.domain.config.DBConfig
import example.com.http.server.config.AppConfigLoader
import example.com.http.server.route.ExampleRoute
import doobie.hikari.HikariTransactor
import doobie.util.ExecutionContexts
import fs2.io.net.Network
import org.http4s.*
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.middleware.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.language.postfixOps
import scala.util.chaining.*

object Main extends IOApp.Simple {

  private def buildHikariConfig(
      dbConfig: DBConfig
  ): HikariConfig =
    new HikariConfig()
      .tap { c =>
        c.setDriverClassName(dbConfig.driver)
        c.setJdbcUrl(dbConfig.url)
        c.setUsername(dbConfig.username)
        c.setPassword(dbConfig.password)
      }

  val run: IO[Unit] = {
    val logger = Slf4jLogger.getLogger[IO]

    for {
      appEnv <- AppEnv.fromEnvIO.toResource

      _ <- logger
        .debug((Runtime.getRuntime.availableProcessors() * 2).toString)
        .toResource

      blockingEc <- ExecutionContexts.fixedThreadPool[IO](
        Runtime.getRuntime.availableProcessors() * 2
      )

      configLoader = AppConfigLoader(appEnv)

      // db settings
      hikariConfig <- configLoader.loadDbConfig
        .map(buildHikariConfig)
        .toResource

      xa <- HikariTransactor.fromHikariConfig[IO](hikariConfig, blockingEc)
      client <- EmberClientBuilder.default[IO].build

      // controller用 DI container
      cc = ControllerContainer(client, xa)

      // Auth0
      auth0Validator <- configLoader.loadAuth0Config.toResource
        .map(Auth0Validator(_))

      // Tapir route
      exampleRoute = ExampleRoute(
        auth0Validator,
        cc,
        getLoginInfo = { claim =>
          (for {
            sub <- EitherT(IO(claim.subject.map(Sub(_)).toRight("sub not found")))
            info <- EitherT(cc.getLoginInfoBySubUsecase.execute(sub)).leftMap(e =>
              e.getMessage
            )
          } yield info).value
        }
      )

      httpApp: HttpApp[IO] = {
        val app = exampleRoute.routes.orNotFound

        // 開発環境のときはRequestの内容をログ出力する
        if (appEnv.isDevelopment) {
          Logger.httpApp(logHeaders = true, logBody = true)(app)
        } else {
          app
        }
      }

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

}
