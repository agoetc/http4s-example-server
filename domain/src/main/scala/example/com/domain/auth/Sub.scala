package example.com.domain.auth

import doobie.util.Read

opaque type Sub = String
object Sub:
  def apply(value: String): Sub = value
  extension (sub: Sub) def value: String = sub

  given Read[Sub] = Read.fromGet[String].map(Sub(_))
