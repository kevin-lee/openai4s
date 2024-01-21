package openai4s.types.completions

import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import extras.render.Render
import io.circe.generic.extras.Configuration
import io.circe.refined.*
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import openai4s.types.common.*
import refined4s.compat.RefinedCompatAllTypes.*

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

  implicit val textConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val textEq: Eq[Text] = cats.derived.semiauto.eq

  @SuppressWarnings(Array("org.wartremover.warts.Equals"))
  implicit val textShow: Show[Text] = cats.derived.semiauto.show

  implicit val textEncoder: Encoder[Text] =
    io.circe.generic.extras.semiauto.deriveConfiguredEncoder[Text].mapJson(_.deepDropNullValues)
  implicit val textDecoder: Decoder[Text] = io.circe.generic.extras.semiauto.deriveConfiguredDecoder

  @newtype case class Prompt(value: String)
  object Prompt {
    implicit val promptEq: Eq[Prompt] = deriving

    implicit val promptShow: Show[Prompt]     = deriving
    implicit val promptRender: Render[Prompt] = deriving

    implicit val promptEncoder: Encoder[Prompt] = deriving
    implicit val promptDecoder: Decoder[Prompt] = deriving
  }

  @newtype case class TopP(value: NonNegFloat)
  object TopP {
    implicit val topPEq: Eq[TopP] = deriving

    implicit val topPShow: Show[TopP] = deriving

    implicit val topPEncoder: Encoder[TopP] = deriving
    implicit val topPDecoder: Decoder[TopP] = deriving
  }

  @newtype case class N(value: Int)
  object N {
    implicit val nEq: Eq[N] = deriving

    implicit val nShow: Show[N] = deriving

    implicit val nEncoder: Encoder[N] = deriving
    implicit val nDecoder: Decoder[N] = deriving
  }

  sealed trait Stream
  object Stream {
    case object IsStream extends Stream
    case object NotStream extends Stream

    def isStream: Stream  = IsStream
    def notStream: Stream = NotStream

    def isStreamIfTrue(stream: Boolean): Stream =
      if (stream) isStream else notStream

    implicit val streamEq: Eq[Stream] = Eq.fromUniversalEquals

    @SuppressWarnings(Array("org.wartremover.warts.Equals"))
    implicit val streamShow: Show[Stream] = cats.derived.semiauto.show

    implicit val streamEncoder: Encoder[Stream] = Encoder[Boolean].contramap {
      case IsStream => true
      case NotStream => false
    }
    implicit val streamDecoder: Decoder[Stream] = Decoder[Boolean].map(isStreamIfTrue)
  }

  @newtype case class Logprobs(value: Int)
  object Logprobs {
    implicit val logprobsEq: Eq[Logprobs] = deriving

    implicit val logprobsShow: Show[Logprobs] = deriving

    implicit val logprobsEncoder: Encoder[Logprobs] = deriving
    implicit val logprobsDecoder: Decoder[Logprobs] = deriving
  }

  @newtype case class Stop(value: NonEmptyString)
  object Stop {
    implicit val stopEq: Eq[Stop] = deriving

    implicit val stopShow: Show[Stop] = deriving

    implicit val stopEncoder: Encoder[Stop] = deriving
    implicit val stopDecoder: Decoder[Stop] = deriving
  }
}
