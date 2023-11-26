package openai4s.types.completions

import cats.syntax.all.*
import cats.{Eq, Show}
import extras.render.Render
import io.circe.*
import io.circe.derivation.*
import newtype4s.Newtype
import openai4s.types
import openai4s.types.common.*
import refined4s.*
import refined4s.numeric.*
import refined4s.strings.NonEmptyString

import scala.annotation.targetName

/** https://platform.openai.com/docs/api-reference/completions#:~:text=prompt%20and%20parameters.-,Request%20body,-model
  * @author Kevin Lee
  * @since 2023-05-01
  */
final case class Text(
  model: Model,
  prompt: Option[Text.Prompt],
  maxTokens: Option[MaxTokens],
  temperature: Option[Temperature],
  topP: Option[Text.TopP],
  n: Option[Text.N],
  stream: Option[Text.Stream],
  logprobs: Option[Text.Logprobs],
  stop: Option[Text.Stop],
)
object Text {

  given textConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  given textEq: Eq[Text] = Eq.fromUniversalEquals

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  given textShow: Show[Text] = cats.derived.semiauto.show

  given textEncoder: Encoder[Text] = ConfiguredEncoder.derived[Text].mapJson(_.deepDropNullValues)

  given textDecoder: Decoder[Text] = ConfiguredDecoder.derived

  type Prompt = Prompt.Type
  object Prompt extends Newtype[String] {
    given promptEq: Eq[Prompt] = Eq.fromUniversalEquals

    given promptShow: Show[Prompt]     = Show.show(_.value)
    given promptRender: Render[Prompt] = Render.render(_.value)

    given promptEncoder: Encoder[Prompt] = Encoder[String].contramap(_.value)
    given promptDecoder: Decoder[Prompt] = Decoder[String].map(Prompt(_))
  }

  type TopP = TopP.Type
  object TopP extends Newtype[NonNegFloat] {

    @targetName("fromFloat")
    inline def apply(inline a: Float): Type = toType(NonNegFloat(a))

    extension (topP: TopP) {
      def toValue: Float = topP.value.value
    }

    given topPEq: Eq[TopP] = Eq.fromUniversalEquals

    given topPShow: Show[TopP] = Show[Float].contramap(_.value.value)

    given topPEncoder: Encoder[TopP] = Encoder[Float].contramap(_.value.value)
    given topPDecoder: Decoder[TopP] = Decoder[Float].emap(NonNegFloat.from).map(TopP(_))
  }

  type N = N.Type
  object N extends Newtype[Int] {
    given nEq: Eq[N] = Eq.fromUniversalEquals

    given nShow: Show[N] = Show[Int].contramap(_.value)

    given nEncoder: Encoder[N] = Encoder[Int].contramap(_.value)
    given nDecoder: Decoder[N] = Decoder[Int].map(N(_))
  }

  enum Stream {
    case IsStream
    case NotStream

  }
  object Stream {

    def isStream: Stream  = IsStream
    def notStream: Stream = NotStream

    def isStreamIfTrue(stream: Boolean): Stream =
      if (stream) isStream else notStream

    given streamEq: Eq[Stream] = Eq.fromUniversalEquals

    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    given streamShow: Show[Stream] = cats.derived.semiauto.show

    given streamEncoder: Encoder[Stream] = Encoder[Boolean].contramap {
      case IsStream => true
      case NotStream => false
    }
    given streamDecoder: Decoder[Stream] = Decoder[Boolean].map(isStreamIfTrue)
  }

  type Logprobs = Logprobs.Type
  object Logprobs extends Newtype[Int] {
    given logprobsEq: Eq[Logprobs] = Eq.fromUniversalEquals

    given logprobsShow: Show[Logprobs] = Show[Int].contramap(_.value)

    given logprobsEncoder: Encoder[Logprobs] = Encoder[Int].contramap(_.value)
    given logprobsDecoder: Decoder[Logprobs] = Decoder[Int].map(Logprobs(_))
  }

  type Stop = Stop.Type
  object Stop extends Newtype[NonEmptyString] {

    @targetName("fromString")
    inline def apply(inline a: String): Type = toType(NonEmptyString(a))

    extension (stop: Stop) {
      def toValue: String = stop.value.value
    }

    given stopEq: Eq[Stop] = Eq.fromUniversalEquals

    given stopShow: Show[Stop] = Show.show(_.value.value)

    given stopEncoder: Encoder[Stop] = Encoder[String].contramap(_.value.value)
    given stopDecoder: Decoder[Stop] = Decoder[String].emap(NonEmptyString.from).map(Stop(_))
  }
}
