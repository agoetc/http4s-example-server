package example.com.app.controller

import io.circe.{Decoder, Encoder}

case class HttpErrorResponse(message: String) derives Encoder, Decoder
