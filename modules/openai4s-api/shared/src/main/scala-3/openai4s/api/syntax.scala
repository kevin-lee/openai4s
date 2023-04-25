package openai4s.api

import extras.render.syntax.*
import openai4s.config.ApiKey
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Request}

/** @author Kevin Lee
  * @since 2023-04-08
  */
object syntax {
  extension [F[*]](request: Request[F]) {

    def setApiKeyHeader(apiKey: ApiKey): Request[F] =
      request.putHeaders(
        Authorization(Credentials.Token(AuthScheme.Bearer, apiKey.render))
      )
  }
}
