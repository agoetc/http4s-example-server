package example.com.adapter.exampleApi

import cats.effect.IO
import example.com.domain.exampleApi.ExampleApiAdapter
import example.com.domain.exampleApi.ExampleApiAdapter.{ExampleRequest, ExampleResponse}
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.circe.jsonOf
import org.http4s.client.Client

class ExampleApiAdapterImpl(client: Client[IO]) extends ExampleApiAdapter {
  def example(req: ExampleRequest): IO[ExampleResponse] = {
    val request = Request[IO](
      method = Method.GET,
      uri = org.http4s.Uri.unsafeFromString("http://localhost:8080/example")
    ).withEntity(
      ExampleRequest("John", 30)
    )

    client
      .expect(request)(jsonOf[IO, ExampleResponse])
      .map { res =>
        ExampleResponse(
          message = res.message
        )
      }
  }
}
