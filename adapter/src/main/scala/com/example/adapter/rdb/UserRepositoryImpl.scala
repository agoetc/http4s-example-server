package com.example.adapter.rdb
import cats.effect.IO
import com.example.domain.{ User, UserRepository }

import doobie.*
import doobie.implicits.*
import cats.effect.*

class UserRepositoryImpl(xa: Transactor[IO]) extends UserRepository {

  def find(id: Long): IO[Option[User]] = {
    sql"SELECT * FROM users WHERE id = $id"
      .query[User]
      .option
      .transact(xa)
  }
}
