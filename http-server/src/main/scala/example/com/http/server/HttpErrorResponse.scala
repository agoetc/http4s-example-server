package example.com.http.server

import io.circe.Encoder

case class HttpErrorResponse(message: String) derives Encoder
