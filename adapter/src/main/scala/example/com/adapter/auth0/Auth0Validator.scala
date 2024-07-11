package example.com.adapter.auth0

import com.auth0.jwk.Jwk
import com.auth0.jwk.UrlJwkProvider
import example.com.domain.config.Auth0Config
import pdi.jwt.*
import pdi.jwt.JwtClaim

import java.time.Clock
import scala.util.Failure
import scala.util.Success
import scala.util.Try

/** refs: https://github.com/auth0-blog/scala-api-sample
  * @param config
  */
class Auth0Validator(config: Auth0Config) {

  implicit val clock: Clock = Clock.systemUTC

  // A regex that defines the JWT pattern and allows us to
  // extract the header, claims and signature
  private val jwtRegex = """(.+?)\.(.+?)\.(.+?)""".r

  // Validates a JWT and potentially returns the claims if the token was successfully parsed
  def validateJwt(token: String): Try[JwtClaim] =
    for {
      jwk <- getJwk(token) // Get the secret key for this token
      claims <-
        JwtCirce.decode(
          token,
          jwk.getPublicKey,
          Seq(JwtAlgorithm.RS256)
        ) // Decode the token using the secret key
      _ <- validateClaims(claims) // validate the data stored inside the token
    } yield claims

  // Splits a JWT into it's 3 component parts
  private val splitToken = (jwt: String) =>
    jwt match {
      case jwtRegex(header, body, sig) => Success((header, body, sig))
      case _ =>
        Failure(new Exception("Token does not match the correct pattern"))
    }

  // As the header and claims data are base64-encoded, this function
  // decodes those elements
  private val decodeElements = (data: Try[(String, String, String)]) =>
    data map { case (header, body, sig) =>
      (JwtBase64.decodeString(header), JwtBase64.decodeString(body), sig)
    }

  // Gets the JWK from the JWKS endpoint using the jwks-rsa library
  private def getJwk(token: String): Try[Jwk] =
    (splitToken andThen decodeElements)(token) flatMap { case (header, _, _) =>
      val jwtHeader = JwtCirce.parseHeader(header)
      val jwkProvider = new UrlJwkProvider(config.domain)

      // Use jwkProvider to load the JWKS data and return the JWK
      jwtHeader.keyId.map { k =>
        Try(jwkProvider.get(k))
      } getOrElse Failure(new Exception("Unable to retrieve kid"))
    }

  // Validates the claims inside the token. isValid checks the issuedAt, expiresAt,
  // issuer and audience fields.
  private def validateClaims(claims: JwtClaim): Try[JwtClaim] =
    if (claims.isValid(config.issuer, config.audience)) {
      Success(claims)
    } else {
      Failure(new Exception("The JWT did not pass validation"))
    }
}
