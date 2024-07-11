package example.com.http.server.config

import cats.effect.IO
import pureconfig.module.catseffect.syntax.*
import pureconfig.{ConfigObjectSource, ConfigSource}
import example.com.domain.config.{Auth0Config, DBConfig}
import example.com.http.server.AppEnv

class AppConfigLoader(env: AppEnv) {

  // env によって読み込む設定ファイルを変える
  private val configSource: ConfigObjectSource =
    ConfigSource.resources(s"application.${env.toString.toLowerCase}.conf")

  val loadDbConfig: IO[DBConfig] =
    configSource
      .at("db.default")
      .loadF[IO, DBConfig]()

  val loadAuth0Config: IO[Auth0Config] =
    configSource
      .at("auth0")
      .loadF[IO, Auth0Config]()
}
