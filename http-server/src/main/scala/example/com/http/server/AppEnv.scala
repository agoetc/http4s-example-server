package example.com.http.server

import cats.effect.IO

enum AppEnv {
  case DEV
  case LOCAL // Localにてdockerから起動する場合の環境
  case PROD

  def isDevelopment: Boolean = this == DEV || this == LOCAL
}

object AppEnv {
  def fromEnvIO: IO[AppEnv] =
    IO.fromOption(
      sys.env
        .get("APP_ENV")
        .map(e => this.valueOf(e.toUpperCase))
    )(new RuntimeException(s"APP_ENV is invalid : ${sys.env.get("APP_ENV")}"))

}
