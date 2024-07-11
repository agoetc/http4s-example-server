package example.com.domain.exampleApi

import ExampleApiAdapter.*

import io.circe.{Decoder, Encoder}
import cats.effect.IO

trait ExampleApiAdapter {
  def example(req: ExampleRequest): IO[ExampleResponse]
}

object ExampleApiAdapter {
  case class ExampleRequest(
      name: String,
      age: Int
  ) derives Encoder

  case class ExampleResponse(
      message: String
  ) derives Decoder

}
