package openai4s.api.completions

import openai4s.api.ApiCore
import openai4s.types.completions.{Response, Text}
import org.http4s.*
import org.http4s.client.dsl.Http4sClientDsl

/** https://platform.openai.com/docs/api-reference/completions
  * https://platform.openai.com/docs/api-reference/completions/create
  *
  * @author Kevin Lee
  * @since 2023-04-30
  */
trait CompletionsApi[F[*]] {
  def completions(text: Text): F[Response]
}
object CompletionsApi {
  @SuppressWarnings(Array("org.wartremover.warts.TripleQuestionMark"))
  def apply[F[*]](completionsUri: Uri, apiCore: ApiCore[F]): CompletionsApi[F] =
    new CompletionsApiF(completionsUri, apiCore)

  private final class CompletionsApiF[F[*]](completionsUri: Uri, apiCore: ApiCore[F])
      extends CompletionsApi[F]
      with Http4sClientDsl[F] {
    import org.http4s.circe.CirceEntityCodec.*

    override def completions(text: Text): F[Response] = {
      val request = Request[F](method = Method.POST, uri = completionsUri).withEntity(text)

      val authedRequest = apiCore.preprocess(request)
      apiCore.httpClient.send[Response](authedRequest)
    }
  }
}
