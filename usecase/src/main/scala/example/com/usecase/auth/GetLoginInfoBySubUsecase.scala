package example.com.usecase.auth

import cats.data.EitherT
import cats.effect.IO
import example.com.domain.UserRepository
import example.com.domain.auth.{LoginInfo, Sub}

import scala.util.chaining.*

class GetLoginInfoBySubUsecase(userRepository: UserRepository) {
  import GetLoginInfoBySubUsecase.*

  def execute(
      sub: Sub
  ): IO[Either[GetLoginInfoBySubUsecaseError, LoginInfo]] =
    (for {
      user <- userRepository
        .findBySub(sub)
        .map(_.toRight(GetLoginInfoBySubUsecaseError.NotFoundUser(sub)))
        .pipe(EitherT(_))
    } yield {
      LoginInfo(user)
    }).value

}

object GetLoginInfoBySubUsecase {

  enum GetLoginInfoBySubUsecaseError(msg: String) extends Exception(msg):
    case NotFoundUser(sub: Sub)
        extends GetLoginInfoBySubUsecaseError(s"User not found. sub: $sub")

}
