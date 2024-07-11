package example.com.adapter.rdb

import cats.effect.*
import example.com.domain.{User, UserId, UserRepository}
import example.com.domain.auth.Sub
import doobie.*
import doobie.implicits.*

class UserRepositoryImpl(xa: Transactor[IO]) extends UserRepository {

  def find(id: UserId): IO[Option[User]] = {

    sql"SELECT * FROM users WHERE id = ${id.value}"
      .query[User]
      .option
      .transact(xa)
  }

  def findBySub(sub: Sub): IO[Option[User]] = {
    sql"SELECT * FROM users WHERE sub = ${sub.value}"
      .query[User]
      .option
      .transact(xa)
  }
}
