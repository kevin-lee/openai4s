package openai4s.api

import openai4s.api.syntax.*
import openai4s.config.ApiKey
import openai4s.http.HttpClient
import org.http4s.Request

/** @author Kevin Lee
  * @since 2023-08-17
  */
trait ApiCore[F[*]] {
  def preprocess(request: Request[F]): Request[F]
  def httpClient: HttpClient[F]
}
object ApiCore {

  def apply[F[*]](apiKey: ApiKey, httpClient: HttpClient[F]): ApiCore[F] = new ApiCoreF[F](apiKey, httpClient)

  private final class ApiCoreF[F[*]](
    private val apiKey: ApiKey,
    override val httpClient: HttpClient[F],
  ) extends ApiCore[F] {
    def preprocess(request: Request[F]): Request[F] = request.setApiKeyHeader(apiKey)
  }

}
