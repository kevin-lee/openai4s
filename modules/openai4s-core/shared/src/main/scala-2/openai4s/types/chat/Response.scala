package openai4s.types.chat

import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.NonEmptyString
import extras.render.Render
import extras.render.refined.*
import io.circe.generic.extras.Configuration
import io.circe.refined.*
import io.circe.{Codec, Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import openai4s.types.common.*

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
)
object Response {

  implicit val responseConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val responseEq: Eq[Response] = cats.derived.semiauto.eq

  implicit val responseShow: Show[Response] = cats.derived.semiauto.show

  implicit val responseCodec: Codec[Response] = io.circe.generic.extras.semiauto.deriveConfiguredCodec

  @newtype case class Id(value: NonEmptyString)
  object Id {
    implicit val idEq: Eq[Id] = deriving

    implicit val idRender: Render[Id] = deriving
    implicit val idShow: Show[Id]     = deriving

    implicit val idEncoder: Encoder[Id] = deriving
    implicit val idDecoder: Decoder[Id] = deriving
  }

  @newtype case class Object(value: NonEmptyString)
  object Object {
    implicit val objectEq: Eq[Object] = deriving

    implicit val objectRender: Render[Object] = deriving
    implicit val objectShow: Show[Object]     = deriving

    implicit val objectEncoder: Encoder[Object] = deriving
    implicit val objectDecoder: Decoder[Object] = deriving
  }

  @newtype case class Created(value: Instant)
  object Created {
    implicit val createdEq: Eq[Created] = Eq.fromUniversalEquals

    implicit val createdRender: Render[Created] = Render.fromToString
    implicit val createdShow: Show[Created]     = Show.fromToString

    implicit val createdEncoder: Encoder[Created] = Encoder[Long].contramap(_.value.getEpochSecond)
    implicit val createdDecoder: Decoder[Created] = Decoder[Long].map(Instant.ofEpochSecond).map(Created(_))
  }

  final case class Usage(
    promptTokens: Usage.PromptTokens,
    completionTokens: Usage.CompletionTokens,
    totalTokens: Usage.TotalTokens,
  )
  object Usage {
    implicit val usageEq: Eq[Usage] = cats.derived.semiauto.eq

    implicit val usageShow: Show[Usage] = cats.derived.semiauto.show

    implicit val usageCodec: Codec[Usage] = io.circe.generic.extras.semiauto.deriveConfiguredCodec

    @newtype case class PromptTokens(value: Int)
    object PromptTokens {
      implicit val promptTokensEq: Eq[PromptTokens] = deriving

      implicit val promptTokensShow: Show[PromptTokens]     = deriving
      implicit val promptTokensRender: Render[PromptTokens] = deriving

      implicit val promptTokensEncoder: Encoder[PromptTokens] = deriving
      implicit val promptTokensDecoder: Decoder[PromptTokens] = deriving
    }

    @newtype case class CompletionTokens(value: Int)
    object CompletionTokens {
      implicit val completionTokensEq: Eq[CompletionTokens] = deriving

      implicit val completionTokensShow: Show[CompletionTokens]     = deriving
      implicit val completionTokensRender: Render[CompletionTokens] = deriving

      implicit val completionTokensEncoder: Encoder[CompletionTokens] = deriving
      implicit val completionTokensDecoder: Decoder[CompletionTokens] = deriving
    }

    @newtype case class TotalTokens(value: Int)
    object TotalTokens {
      implicit val totalTokensEq: Eq[TotalTokens] = deriving

      implicit val totalTokensShow: Show[TotalTokens]     = deriving
      implicit val totalTokensRender: Render[TotalTokens] = deriving

      implicit val totalTokensEncoder: Encoder[TotalTokens] = deriving
      implicit val totalTokensDecoder: Decoder[TotalTokens] = deriving
    }

  }

  final case class Choice(message: Message, finishReason: FinishReason, index: Index)
  object Choice {
    implicit val choiceEq: Eq[Choice] = cats.derived.semiauto.eq

    implicit val choiceShow: Show[Choice] = cats.derived.semiauto.show

    implicit val choiceCodec: Codec[Choice] = io.circe.generic.extras.semiauto.deriveConfiguredCodec

  }

}
