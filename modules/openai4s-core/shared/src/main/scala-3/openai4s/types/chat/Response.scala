package openai4s.types.chat

import cats.derived.*
import cats.{Eq, Show}
import extras.render.Render
import io.circe.*
import io.circe.derivation.{Configuration, ConfiguredCodec}
import openai4s.types
import openai4s.types.common.*
import refined4s.*
import refined4s.modules.cats.derivation.*
import refined4s.modules.circe.derivation.*
import refined4s.modules.circe.derivation.types.all.given
import refined4s.modules.extras.derivation.*
import refined4s.modules.extras.derivation.types.all.given
import refined4s.types.all.*

import java.time.Instant

/** @author Kevin Lee
  * @since 2023-03-28
  */
final case class Response(
  id: Response.Id,
  `object`: Response.Object,
  created: Response.Created,
  model: Model,
  usage: Response.Usage,
  choices: List[Response.Choice],
) derives ConfiguredCodec,
      Eq,
      Show
object Response {

  given responseConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

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

    given createdEq: Eq[Created] = wrapTC(Eq.fromUniversalEquals[Instant])

    given createdShow: Show[Created]     = wrapTC(Show.fromToString)
    given createdRender: Render[Created] = wrapTC(Render.fromToString)

    inline given createdEncoder: Encoder[Created] = Encoder[Long].contramap(_.value.getEpochSecond)
    inline given createdDecoder: Decoder[Created] = Decoder[Long].map(Instant.ofEpochSecond).map(Created(_))
  }

  final case class Usage(
    promptTokens: Usage.PromptTokens,
    completionTokens: Usage.CompletionTokens,
    totalTokens: Usage.TotalTokens,
  ) derives ConfiguredCodec,
        Eq,
        Show
  object Usage {

    type PromptTokens = PromptTokens.Type
    object PromptTokens extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int], ExtrasRender[Int]

    type CompletionTokens = CompletionTokens.Type
    object CompletionTokens extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int], ExtrasRender[Int]

    type TotalTokens = TotalTokens.Type
    object TotalTokens extends Newtype[Int], CatsEqShow[Int], CirceNewtypeCodec[Int], ExtrasRender[Int]

  }

  final case class Choice(message: Message, finishReason: FinishReason, index: Index) derives Eq, Show
  object Choice {

    given choiceCodec: Codec[Choice] = ConfiguredCodec.derived

  }

}
