package example.com.domain

import example.com.domain.auth.Sub

case class User(
    id: UserId,
    sub: Sub, // loginInfoが持った方が良さそう？認証以外で使わない方が良さそう
    name: String,
    age: Int
)
