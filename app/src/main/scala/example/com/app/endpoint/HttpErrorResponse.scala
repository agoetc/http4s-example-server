package example.com.app.endpoint

import io.circe.{Decoder, Encoder}

case class HttpErrorResponse(message: String) derives Encoder, Decoder
