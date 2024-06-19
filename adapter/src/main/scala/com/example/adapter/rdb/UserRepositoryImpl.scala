package com.example.adapter.rdb
import cats.effect.IO
import com.example.domain.{ User, UserRepository }

class UserRepositoryImpl extends UserRepository {

  def find(id: Long): IO[Option[User]] = {
    // TODO 本来はDBからデータを取得する
    IO.pure(Some(User(id, "dummy")))
  }
}
