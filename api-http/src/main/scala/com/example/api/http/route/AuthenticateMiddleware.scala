package com.example.api.http.route

import cats.data.Kleisli
import cats.effect.IO
import cats.implicits.*
import org.http4s.*
import org.http4s.Credentials.Token
import org.http4s.headers.Authorization
import pdi.*
import pdi.jwt.*

import cats.data.OptionT
import org.http4s.Status.Forbidden
import com.example.domain.User

// TODO Configから取得するようにする
case class AuthenticateConfig(secretKey: String)

class AuthenticateMiddleware(config: AuthenticateConfig) {
  import org.http4s.dsl.Http4sDsl
  val dsl: Http4sDsl[IO] = new Http4sDsl[IO] {}
  import dsl.*
  import org.http4s.server.AuthMiddleware

  val authLogin: Kleisli[IO, Request[IO], Either[String, LoginInfo]] =
    Kleisli { request =>
      val tokenResult: Either[String, JwtClaim] = extractToken(request)
      tokenResult.map(authenticate).pure[IO]
    }

  val onFailure: AuthedRoutes[String, IO] =
    Kleisli { req =>
      OptionT.liftF(Forbidden(req.context))
    }

  def buildMiddleware: AuthMiddleware[IO, LoginInfo] =
    AuthMiddleware(authLogin, onFailure)

  private def extractToken(request: Request[IO]): Either[String, JwtClaim] =
    request.headers.get[Authorization] match
      case Some(Authorization(Token(AuthScheme.Bearer, token))) =>
        Jwt
          .decode(token, config.secretKey, Seq(pdi.jwt.JwtAlgorithm.HS256))
          .toEither
          .leftMap(_ => "Invalid token")
      case Some(_) | None =>
        "Bearer token not found".asLeft[JwtClaim]

  private def authenticate(claim: JwtClaim): LoginInfo = {
    // TODO ここで認証処理を行う
    LoginInfo(User(1, claim.content, 20))
  }
}
