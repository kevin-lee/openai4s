package openai4s.api

import openai4s.api.chat.ChatApi

/** @author Kevin Lee
  * @since 2023-04-04
  */
trait OpenAi[F[*]] {

  def chat: ChatApi[F]

}
object OpenAi {
  def apply[F[*]](chat: ChatApi[F]): OpenAi[F] =
    new OpenAiF[F](chat)

  private final class OpenAiF[F[*]](override val chat: ChatApi[F]) extends OpenAi[F]
}
