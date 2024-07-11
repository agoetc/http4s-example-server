package example.com.usecase

import cats.effect.IO
import example.com.domain.{User, UserId, UserRepository}

class GetUserUsecase(userRepository: UserRepository) {
  def execute(userId: UserId): IO[Option[User]] = {
    userRepository.find(userId)
  }
}
