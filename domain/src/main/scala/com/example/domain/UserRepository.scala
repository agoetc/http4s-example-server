package com.example.domain
import cats.effect.IO

trait UserRepository {
  def find(id: Long): IO[Option[User]]
}
