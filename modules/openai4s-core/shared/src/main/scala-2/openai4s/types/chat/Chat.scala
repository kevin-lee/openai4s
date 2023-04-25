package openai4s.types.chat

import cats.data.NonEmptyList
import cats.{Eq, Show}
import cats.syntax.all.*
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.cats.*
import eu.timepit.refined.numeric.*
import eu.timepit.refined.types.numeric.*
import io.circe.generic.extras.Configuration
import io.circe.refined.*
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import openai4s.types

/** @author Kevin Lee
  * @since 2023-03-24
  */
final case class Chat(
  model: Model,
  messages: NonEmptyList[Chat.Message],
  temperature: Option[Chat.Temperature],
  maxTokens: Option[Chat.MaxTokens],
)

object Chat {
  implicit val chatConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val chatEq: Eq[Chat] = Eq.fromUniversalEquals

  @SuppressWarnings(Array("org.wartremover.warts.Equals", "org.wartremover.warts.FinalVal"))
  implicit val chatShow: Show[Chat] = cats.derived.semiauto.show

  implicit val chatEncoder: Encoder[Chat] =
    io.circe.generic.extras.semiauto.deriveConfiguredEncoder[Chat].mapJson(_.deepDropNullValues)
  implicit val chatDecoder: Decoder[Chat] = io.circe.generic.extras.semiauto.deriveConfiguredDecoder

  @newtype case class Message(value: types.Message)

  object Message {
    implicit val messageEq: Eq[Message] = deriving

    implicit val messageShow: Show[Message] = deriving

    implicit val messageEncoder: Encoder[Message] = deriving
    implicit val messageDecoder: Decoder[Message] = deriving

  }

  type Temperature = Float Refined Interval.Closed[0f, 2f]

  object Temperature extends RefinedTypeOps.Numeric[Temperature, Float] {

    implicit val temperatureEq: Eq[Temperature] = Eq.fromUniversalEquals

    implicit val temperatureShow: Show[Temperature] = Show.catsShowForFloat.contramap(_.value)

    implicit val temperatureEncoder: Encoder[Temperature] = Encoder.encodeFloat.contramap(_.value)
    implicit val temperatureDecoder: Decoder[Temperature] = Decoder.decodeFloat.emap(from)
  }

  @newtype case class MaxTokens(value: PosInt)

  object MaxTokens {
    implicit val maxTokensEq: Eq[MaxTokens] = deriving

    implicit val maxTokensShow: Show[MaxTokens] = deriving

    implicit val maxTokensEncoder: Encoder[MaxTokens] = deriving
    implicit val maxTokensDecoder: Decoder[MaxTokens] = deriving
  }

}
