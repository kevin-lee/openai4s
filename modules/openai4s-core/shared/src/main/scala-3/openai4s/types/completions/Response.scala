package openai4s.types.completions

import cats.{Eq, Show}
import cats.derived.*
import extras.render.Render
import io.circe.derivation.{Configuration, ConfiguredCodec, ConfiguredDecoder, ConfiguredEncoder}
import io.circe.{Codec, Decoder, Encoder}
import refined4s.*
import openai4s.types
import openai4s.types.common.*
import refined4s.types.all.*
import refined4s.modules.cats.derivation.*

import refined4s.modules.circe.derivation.*
import refined4s.modules.circe.derivation.types.all.given
import refined4s.modules.extras.derivation.*
import refined4s.modules.extras.derivation.types.all.given

import java.time.Instant

/** @author Kevin Lee
  * @since 2023-05-01
  */
final case class Response(
  id: Response.Id,
  `object`: Response.Object,
  created: Response.Created,
  model: Model,
  usage: Response.Usage,
  choices: List[Response.Choice],
) derives Eq,
      Show
object Response {

  given responseConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  given encoder: Encoder[Response] = ConfiguredEncoder.derived[Response].mapJson(_.deepDropNullValues)
  given decoder: Decoder[Response] = ConfiguredDecoder.derived

  type Id = Id.Type
  object Id
      extends Newtype[NonEmptyString],
        CatsEqShow[NonEmptyString],
        CirceNewtypeCodec[NonEmptyString],
        ExtrasRender[NonEmptyString]

  type Object = Object.Type
  object Object
      extends Newtype[NonEmptyString],
        CatsEqShow[NonEmptyString],
        CirceNewtypeCodec[NonEmptyString],
        ExtrasRender[NonEmptyString]

  type Created = Created.Type
  object Created extends Newtype[Instant] {

    given createdEq: Eq[Created] = Eq.fromUniversalEquals

    given createdRender: Render[Created] = Render.fromToString
    given createdShow: Show[Created]     = Show.fromToString

    given createdEncoder: Encoder[Created] = Encoder[Long].contramap(_.value.getEpochSecond)
    given createdDecoder: Decoder[Created] = Decoder[Long].map(Instant.ofEpochSecond).map(Created(_))
  }

  final case class Choice(
    text: Choice.Text,
    index: Index,
    logprobs: Option[Choice.Logprobs],
    finishReason: FinishReason,
  ) derives Eq,
        Show,
        ConfiguredCodec
  object Choice {

    type Text = Text.Type
    object Text
        extends Newtype[NonEmptyString],
          CatsEqShow[NonEmptyString],
          CirceNewtypeCodec[NonEmptyString],
          ExtrasRender[NonEmptyString]

    type Logprobs = Logprobs.Type
    object Logprobs extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int], ExtrasRender[Int]

  }

  final case class Usage(
    promptTokens: Usage.PromptTokens,
    completionTokens: Usage.CompletionTokens,
    totalTokens: Usage.TotalTokens,
  ) derives Eq,
        Show,
        ConfiguredCodec
  object Usage {

    type PromptTokens = PromptTokens.Type
    object PromptTokens extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int], ExtrasRender[Int]

    type CompletionTokens = CompletionTokens.Type
    object CompletionTokens extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int], ExtrasRender[Int]

    type TotalTokens = TotalTokens.Type
    object TotalTokens extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int], ExtrasRender[Int]

  }

}
