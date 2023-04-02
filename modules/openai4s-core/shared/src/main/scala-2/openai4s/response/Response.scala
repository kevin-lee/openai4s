package openai4s.response

import cats.{Eq, Show}
import eu.timepit.refined.cats.*
import eu.timepit.refined.types.string.NonEmptyString
import extras.render.Render
import extras.render.refined.*
import io.circe.generic.extras.Configuration
import io.circe.refined.*
import io.circe.{Codec, Decoder, Encoder}
import io.estatico.newtype.macros.newtype
import openai4s.types
import openai4s.types.Model

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

  implicit val responseEq: Eq[Response] = Eq.fromUniversalEquals

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
    implicit val usageEq: Eq[Usage] = Eq.fromUniversalEquals

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

  final case class Choice(message: Choice.Message, finishReason: Choice.FinishReason, index: Choice.Index)
  object Choice {
    implicit val choiceEq: Eq[Choice] = Eq.fromUniversalEquals

    implicit val choiceShow: Show[Choice] = cats.derived.semiauto.show

    implicit val choiceCodec: Codec[Choice] = io.circe.generic.extras.semiauto.deriveConfiguredCodec

    @newtype case class Message(value: types.Message)
    object Message {
      implicit val messageEq: Eq[Message] = deriving

      implicit val messageShow: Show[Message] = deriving

      implicit val messageCodec: Codec[Message] = deriving
    }

    @newtype case class FinishReason(value: String)
    object FinishReason {
      implicit val finishReasonEq: Eq[FinishReason] = deriving

      implicit val finishReasonShow: Show[FinishReason]     = deriving
      implicit val finishReasonRender: Render[FinishReason] = deriving

      implicit val finishReasonEncoder: Encoder[FinishReason] = deriving
      implicit val finishReasonDecoder: Decoder[FinishReason] = deriving
    }

    @newtype case class Index(value: Int)
    object Index {
      implicit val indexEq: Eq[Index] = deriving

      implicit val indexShow: Show[Index]     = deriving
      implicit val indexRender: Render[Index] = deriving

      implicit val indexEncoder: Encoder[Index] = deriving
      implicit val indexDecoder: Decoder[Index] = deriving
    }

  }

}
