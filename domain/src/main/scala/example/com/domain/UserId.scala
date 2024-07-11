package example.com.domain

import io.circe.{Decoder, Encoder}
import doobie.util.Read

opaque type UserId = Long

object UserId {
  def apply(value: Long): UserId = value

  given Read[UserId] = Read.fromGet[Long].map(UserId(_))
  given Encoder[UserId] = Encoder.encodeLong
  given Decoder[UserId] = Decoder.decodeLong

  extension (userId: UserId) {
    def value: Long = userId
  }
}
