package com.example.api.http
import cats.effect.IO
import org.http4s.{ InvalidMessageBodyFailure, Response, Status }

def errorHandler: PartialFunction[Throwable, IO[Response[IO]]] =
  case e: InvalidMessageBodyFailure =>
    IO.println(e.getMessage) *>
      IO.pure(Response(Status.BadRequest).withEntity(e.getMessage))
