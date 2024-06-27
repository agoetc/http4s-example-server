package com.example.usecase

import com.example.domain.UserRepository
import cats.effect.IO
import com.example.domain.User

class GetUserUsecase(userRepository: UserRepository) {
  def execute(userId: Long): IO[Option[User]]= {
   userRepository.find(userId)
  }
}