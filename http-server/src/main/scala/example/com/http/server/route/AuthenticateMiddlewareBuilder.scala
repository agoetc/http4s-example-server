package example.com.http.server.route

import cats.data.{EitherT, Kleisli, OptionT}
import cats.effect.IO
import example.com.adapter.auth0.Auth0Validator
import example.com.http.server.HttpErrorResponse
import org.http4s.*
import org.http4s.Credentials.Token
import org.http4s.Status.Forbidden
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.Authorization
import org.http4s.server.AuthMiddleware
import org.typelevel.log4cats.Logger
import pdi.*
import pdi.jwt.*

import scala.util.chaining.*
import scala.util.{Failure, Success}

class AuthenticateMiddlewareBuilder(
    dsl: Http4sDsl[IO],
    auth0Validator: Auth0Validator,
    logger: Logger[IO]
) {

  import dsl.*

  def build[A](f: JwtClaim => IO[Either[String, A]]): AuthMiddleware[IO, A] =
    AuthMiddleware(authenticate(f), onFailure)

  /** 認証失敗時の処理
    */
  private val onFailure: AuthedRoutes[String, IO] =
    Kleisli { req =>
      (for {
        _ <- logger.debug(s"Authentication failed: ${req.context}")
        res <- Forbidden(HttpErrorResponse("Authentication failed"))
      } yield res).pipe(OptionT.liftF)
    }

  private def authenticate[A](
      f: JwtClaim => IO[Either[String, A]]
  ): Kleisli[IO, Request[IO], Either[String, A]] = // left : error message
    Kleisli { request =>
      (for {
        claim <- EitherT(IO(extractClaim(request)))
        result <- EitherT(f(claim))
      } yield result).value
    }

  private def extractClaim(request: Request[IO]): Either[String, JwtClaim] =
    request.headers.get[Authorization] match {
      case Some(Authorization(Token(AuthScheme.Bearer, token))) =>
        auth0Validator.validateJwt(token) match {
          case Success(claim) => Right(claim)
          case Failure(e)     => Left("Invalid token : " + e.getMessage)
        }

      case Some(_) =>
        Left("Invalid Authorization header")
      case None =>
        Left("Token not found")
    }
}
