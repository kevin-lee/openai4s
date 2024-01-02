package openai4s.types.completions

import cats.*
import extras.render.Render
import io.circe.*
import io.circe.derivation.*
import openai4s.types
import openai4s.types.common.*
import refined4s.*
import refined4s.types.all.*
import refined4s.modules.cats.derivation.*
import refined4s.modules.cats.derivation.types.all.given
import refined4s.modules.circe.derivation.*
import refined4s.modules.circe.derivation.types.all.given
import refined4s.modules.extras.derivation.*
import refined4s.modules.extras.derivation.types.all.given

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
  object Prompt extends Newtype[String], CatsEqShow[String], ExtrasRender[String], CirceNewtypeCodec[String]

  type TopP = TopP.Type
  object TopP extends Newtype[NonNegFloat], CatsEqShow[NonNegFloat], CirceNewtypeCodec[NonNegFloat] {

    @targetName("fromFloat")
    inline def apply(inline a: Float): Type = wrap(NonNegFloat(a))

  }

  type N = N.Type
  object N extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int]

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
  object Logprobs extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int]

  type Stop = Stop.Type
  object Stop
      extends Newtype[NonEmptyString],
        CatsEqShow[NonEmptyString],
        ExtrasRender[NonEmptyString],
        CirceNewtypeCodec[NonEmptyString] {

    @targetName("fromString")
    inline def apply(inline a: String): Type = wrap(NonEmptyString(a))

  }
}
