package openai4s.api.chat

import openai4s.api.syntax.*
import openai4s.config.ApiKey
import openai4s.http.HttpClient
import openai4s.types.chat.{Chat, Response}
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.{Method, Request, Uri}

/** https://platform.openai.com/docs/api-reference/chat/create
  * @author Kevin Lee
  * @since 2023-04-07
  */
trait ChatApi[F[*]] {
  def completion(chat: Chat): F[Response]
}
object ChatApi {
  def apply[F[*]](chatCompletionsUri: Uri, apiKey: ApiKey, httpClient: HttpClient[F]): ChatApi[F] =
    new ChatApiF[F](chatCompletionsUri, apiKey, httpClient)

  private final class ChatApiF[F[*]](chatCompletionsUri: Uri, apiKey: ApiKey, httpClient: HttpClient[F])
      extends ChatApi[F]
      with Http4sClientDsl[F] {

    import org.http4s.circe.CirceEntityCodec.*

    override def completion(chat: Chat): F[Response] = {
      val request       = Request[F](method = Method.POST, uri = chatCompletionsUri).withEntity(chat)
      val authedRequest = request.setApiKeyHeader(apiKey)
      httpClient.send[Response](authedRequest)
    }
  }
}
