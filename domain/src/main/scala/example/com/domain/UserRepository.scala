package example.com.domain

import cats.effect.IO
import example.com.domain.auth.Sub

trait UserRepository {
  def find(id: UserId): IO[Option[User]]

  def findBySub(sub: Sub): IO[Option[User]]
}
