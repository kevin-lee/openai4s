package openai4s.types.chat

import cats.data.NonEmptyList
import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import io.circe.generic.extras.Configuration
import io.circe.refined.*
import io.circe.{Decoder, Encoder}
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
  implicit val chatConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val chatEq: Eq[Chat] = cats.derived.semiauto.eq

  @SuppressWarnings(Array("org.wartremover.warts.Equals", "org.wartremover.warts.FinalVal"))
  implicit val chatShow: Show[Chat] = cats.derived.semiauto.show

  implicit val chatEncoder: Encoder[Chat] =
    io.circe.generic.extras.semiauto.deriveConfiguredEncoder[Chat].mapJson(_.deepDropNullValues)
  implicit val chatDecoder: Decoder[Chat] = io.circe.generic.extras.semiauto.deriveConfiguredDecoder

}
