package openai4s.types.chat

import cats.data.NonEmptyList
import cats.{Eq, Show}
import io.circe.derivation.*
import io.circe.*
import openai4s.types

import openai4s.types.common.*

/** @author Kevin Lee
  * @since 2023-03-24
  */
final case class Chat(
  model: Model,
  messages: NonEmptyList[Message],
  temperature: Option[Temperature],
  maxTokens: Option[MaxTokens],
)

object Chat {
  given chatConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  given chatEq: Eq[Chat] = Eq.fromUniversalEquals

  @SuppressWarnings(Array("org.wartremover.warts.Equals", "org.wartremover.warts.FinalVal"))
  given chatShow: Show[Chat] = cats.derived.semiauto.show

  given chatEncoder: Encoder[Chat] = ConfiguredEncoder.derived[Chat].mapJson(_.deepDropNullValues)

  given chatDecoder: Decoder[Chat] = ConfiguredDecoder.derived

}
