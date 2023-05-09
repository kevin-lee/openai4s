package openai4s.types.chat

import cats.data.NonEmptyList
import cats.{Eq, Show}
import cats.syntax.all.*
import io.circe.derivation.*
import io.circe.*
import io.circe.{Decoder, Encoder}
import newtype4s.Newtype
import openai4s.types
import refined4s.Refined
import refined4s.numeric.PosInt

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
  given chatConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  given chatEq: Eq[Chat] = Eq.fromUniversalEquals

  @SuppressWarnings(Array("org.wartremover.warts.Equals", "org.wartremover.warts.FinalVal"))
  given chatShow: Show[Chat] = cats.derived.semiauto.show

  given chatEncoder: Encoder[Chat] = ConfiguredEncoder.derived[Chat].mapJson(_.deepDropNullValues)

  given chatDecoder: Decoder[Chat] = ConfiguredDecoder.derived

  type Message = Message.Type
  object Message extends Newtype[types.Message] {
    given messageEq: Eq[Message] = Eq.fromUniversalEquals

    given messageShow: Show[Message] = Show.show(_.value.show)

    given messageEncoder: Encoder[Message] = Codec[types.Message].contramap(_.value)
    given messageDecoder: Decoder[Message] = Codec[types.Message].map(Message(_))

  }

  type Temperature = Temperature.Type
  object Temperature extends Refined[Float] {

    override def invalidReason(a: Float): String =
      "The temperature must be a Float between 0f and 2f (inclusive) but got [" + a + "]"

    inline override def predicate(a: Float): Boolean = a >= 0f && a <= 2f

    given temperatureEq: Eq[Temperature] = Eq.fromUniversalEquals

    given temperatureShow: Show[Temperature] = Show[Float].contramap(_.value)

    given temperatureEncoder: Encoder[Temperature] = Encoder[Float].contramap(_.value)
    given temperatureDecoder: Decoder[Temperature] = Decoder[Float].emap(from)
  }

  type MaxTokens = MaxTokens.Type
  object MaxTokens extends Newtype[PosInt] {
    extension (maxTokens: MaxTokens) {
      def toValue: Int = maxTokens.value.value
    }

    given maxTokensEq: Eq[MaxTokens] = Eq.fromUniversalEquals

    given maxTokensShow: Show[MaxTokens] = Show[Int].contramap(_.toValue)

    given maxTokensEncoder: Encoder[MaxTokens] = Encoder[Int].contramap(_.toValue)
    given maxTokensDecoder: Decoder[MaxTokens] = Decoder[Int].emap(PosInt.from).map(MaxTokens(_))
  }

}
