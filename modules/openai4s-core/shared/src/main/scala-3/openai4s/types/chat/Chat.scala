package openai4s.types.chat

import cats.data.NonEmptyList
import cats.{Eq, Show}
import cats.syntax.all.*
import io.circe.derivation.*
import io.circe.*
import io.circe.{Decoder, Encoder}
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

  Codec.AsObject
  given chatEncoder: Encoder[Chat] = ConfiguredEncoder.derived[Chat].mapJson(_.deepDropNullValues)

  given chatDecoder: Decoder[Chat] = ConfiguredDecoder.derived

  type Message = Message.Message
  object Message {
    opaque type Message = types.Message
    def apply(message: types.Message): Message = message

    given messageCanEqual: CanEqual[Message, Message] = CanEqual.derived

    extension (message: Message) {
      def value: types.Message = message
    }

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

  type MaxTokens = MaxTokens.MaxTokens
  object MaxTokens {
    opaque type MaxTokens = PosInt
    def apply(maxTokens: PosInt): MaxTokens = maxTokens

    given maxTokensCanEqual: CanEqual[MaxTokens, MaxTokens] = CanEqual.derived

    extension (maxTokens: MaxTokens) {
      def value: PosInt = maxTokens
      def toValue: Int  = value
    }

    given maxTokensEq: Eq[MaxTokens] = Eq.fromUniversalEquals

    given maxTokensShow: Show[MaxTokens] = Show[Int].contramap(_.value)

    given maxTokensEncoder: Encoder[MaxTokens] = Encoder[Int].contramap(_.value)
    given maxTokensDecoder: Decoder[MaxTokens] = Decoder[Int].emap(PosInt.from).map(MaxTokens(_))
  }

}
