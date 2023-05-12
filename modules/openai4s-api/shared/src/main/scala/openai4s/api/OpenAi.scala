package openai4s.api

import openai4s.api.chat.ChatApi
import openai4s.api.completions.CompletionsApi

/** @author Kevin Lee
  * @since 2023-04-04
  */
trait OpenAi[F[*]] {

  def chat: ChatApi[F]

  def completions: CompletionsApi[F]

}
object OpenAi {
  def apply[F[*]](chat: ChatApi[F], completions: CompletionsApi[F]): OpenAi[F] =
    new OpenAiF[F](chat, completions)

  private final class OpenAiF[F[*]](
    override val chat: ChatApi[F],
    override val completions: CompletionsApi[F],
  ) extends OpenAi[F]

}
