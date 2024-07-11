package example.com.domain.config

import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*

case class Auth0Config(
    domain: String,
    audience: String,
    clientId: String,
    clientSecret: String
) derives ConfigReader {
  val issuer: String = s"https://$domain/"
}
