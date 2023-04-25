package openai4s.types.chat

import cats.{Eq, Show}
import cats.syntax.all.*
import refined4s.strings.NonEmptyString
import extras.render.Render
import io.circe.derivation.{Configuration, ConfiguredCodec, ConfiguredDecoder, ConfiguredEncoder}
import io.circe.*
import io.circe.{Codec, Decoder, Encoder}
import openai4s.types

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
) derives Codec.AsObject
object Response {

  implicit val responseConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  implicit val responseEq: Eq[Response] = Eq.fromUniversalEquals

  implicit val responseShow: Show[Response] = cats.derived.semiauto.show

  type Id = Id.Id
  object Id {
    opaque type Id = NonEmptyString
    def apply(id: NonEmptyString): Id = id

    given idCanEqual: CanEqual[Id, Id] = CanEqual.derived

    extension (id: Id) {
      def value: NonEmptyString = id
    }
    implicit val idEq: Eq[Id] = Eq.fromUniversalEquals

    implicit val idRender: Render[Id] = Render[String].contramap(_.value)
    implicit val idShow: Show[Id]     = Show[String].contramap(_.value)

    implicit val idEncoder: Encoder[Id] = Encoder[String].contramap(_.value)
    implicit val idDecoder: Decoder[Id] = Decoder[String].emap(NonEmptyString.from)
  }

  type Object = Object.Object
  object Object {
    opaque type Object = NonEmptyString
    def apply(obj: NonEmptyString): Object = obj

    given objectCanEqual: CanEqual[Object, Object] = CanEqual.derived

    extension (obj: Object) {
      def value: NonEmptyString = obj
    }
    given objectEq: Eq[Object] = Eq.fromUniversalEquals

    given objectRender: Render[Object] = Render[String].contramap(_.value)
    given objectShow: Show[Object]     = Show[String].contramap(_.value)

    given objectEncoder: Encoder[Object] = Encoder[String].contramap(_.value)
    given objectDecoder: Decoder[Object] = Decoder[String].emap(NonEmptyString.from)
  }

  type Created = Created.Created
  object Created {
    opaque type Created = Instant
    def apply(created: Instant): Created = created

    given createdCanEqual: CanEqual[Created, Created] = CanEqual.derived

    extension (created: Created) {
      def value: Instant = created
    }

    given createdEq: Eq[Created] = Eq.fromUniversalEquals

    given createdRender: Render[Created] = Render.fromToString
    given createdShow: Show[Created]     = Show.fromToString

    given createdEncoder: Encoder[Created] = Encoder[Long].contramap(_.value.getEpochSecond)
    given createdDecoder: Decoder[Created] = Decoder[Long].map(Instant.ofEpochSecond).map(Created(_))
  }

  final case class Usage(
    promptTokens: Usage.PromptTokens,
    completionTokens: Usage.CompletionTokens,
    totalTokens: Usage.TotalTokens,
  )
  object Usage {
    given usageEq: Eq[Usage] = Eq.fromUniversalEquals

    given usageShow: Show[Usage] = cats.derived.semiauto.show

    given usageCodec: Codec[Usage] = ConfiguredCodec.derived

    type PromptTokens = PromptTokens.PromptTokens
    object PromptTokens {
      opaque type PromptTokens = Int
      def apply(promptTokens: Int): PromptTokens = promptTokens

      given promptTokensCanEqual: CanEqual[PromptTokens, PromptTokens] = CanEqual.derived

      extension (promptTokens: PromptTokens) {
        def value: Int = promptTokens
      }

      given promptTokensEq: Eq[PromptTokens] = Eq.fromUniversalEquals

      given promptTokensShow: Show[PromptTokens]     = Show.catsShowForInt.contramap(_.value)
      given promptTokensRender: Render[PromptTokens] = Render.intRender.contramap(_.value)

      given promptTokensEncoder: Encoder[PromptTokens] = Encoder.encodeInt.contramap(_.value)
      given promptTokensDecoder: Decoder[PromptTokens] = Decoder.decodeInt.map(PromptTokens(_))
    }

    type CompletionTokens = CompletionTokens.CompletionTokens
    object CompletionTokens {
      opaque type CompletionTokens = Int
      def apply(completionTokens: Int): CompletionTokens = completionTokens

      given completionTokensCanEqual: CanEqual[CompletionTokens, CompletionTokens] = CanEqual.derived

      extension (completionTokens: CompletionTokens) {
        def value: Int = completionTokens
      }

      given completionTokensEq: Eq[CompletionTokens] = Eq.fromUniversalEquals

      given completionTokensShow: Show[CompletionTokens]     = Show.catsShowForInt.contramap(_.value)
      given completionTokensRender: Render[CompletionTokens] = Render.intRender.contramap(_.value)

      given completionTokensEncoder: Encoder[CompletionTokens] = Encoder.encodeInt.contramap(_.value)
      given completionTokensDecoder: Decoder[CompletionTokens] = Decoder.decodeInt.map(CompletionTokens(_))
    }

    type TotalTokens = TotalTokens.TotalTokens
    object TotalTokens {
      opaque type TotalTokens = Int
      def apply(totalTokens: Int): TotalTokens = totalTokens

      given totalTokensCanEqual: CanEqual[TotalTokens, TotalTokens] = CanEqual.derived

      extension (totalTokens: TotalTokens) {
        def value: Int = totalTokens
      }
      given totalTokensEq: Eq[TotalTokens] = Eq.fromUniversalEquals

      given totalTokensShow: Show[TotalTokens]     = Show.catsShowForInt.contramap(_.value)
      given totalTokensRender: Render[TotalTokens] = Render.intRender.contramap(_.value)

      given totalTokensEncoder: Encoder[TotalTokens] = Encoder.encodeInt.contramap(_.value)
      given totalTokensDecoder: Decoder[TotalTokens] = Decoder.decodeInt.map(TotalTokens(_))
    }

  }

  final case class Choice(message: Choice.Message, finishReason: Choice.FinishReason, index: Choice.Index)
  object Choice {
    given choiceEq: Eq[Choice] = Eq.fromUniversalEquals

    given choiceShow: Show[Choice] = cats.derived.semiauto.show

    given choiceCodec: Codec[Choice] = ConfiguredCodec.derived

    type Message = Message.Message
    object Message {
      opaque type Message = types.Message
      def apply(message: types.Message): Message = message

      given messageCanEqual: CanEqual[Message, Message] = CanEqual.derived

      extension (message: Message) {
        def value: types.Message = message
      }

      given messageEq: Eq[Message] = types.Message.messageEq

      given messageShow: Show[Message] = types.Message.messageShow

      given messageCodec: Codec[Message] = types.Message.messageCodec

    }

    type FinishReason = FinishReason.FinishReason
    object FinishReason {
      opaque type FinishReason = String
      def apply(finishReason: String): FinishReason = finishReason

      given finishReasonCanEqual: CanEqual[FinishReason, FinishReason] = CanEqual.derived

      extension (finishReason: FinishReason) {
        def value: String = finishReason
      }
      given finishReasonEq: Eq[FinishReason] = Eq.fromUniversalEquals

      given finishReasonShow: Show[FinishReason]     = Show.catsShowForString
      given finishReasonRender: Render[FinishReason] = Render.stringRender

      given finishReasonEncoder: Encoder[FinishReason] = Encoder.encodeString
      given finishReasonDecoder: Decoder[FinishReason] = Decoder.decodeString
    }

    type Index = Index.Index
    object Index {
      opaque type Index = Int
      def apply(index: Int): Index = index

      given indexCanEqual: CanEqual[Index, Index] = CanEqual.derived

      extension (index: Index) {
        def value: Int = index
      }
      given indexEq: Eq[Index] = Eq.fromUniversalEquals

      given indexShow: Show[Index]     = Show.catsShowForInt
      given indexRender: Render[Index] = Render.intRender

      given indexEncoder: Encoder[Index] = Encoder.encodeInt
      given indexDecoder: Decoder[Index] = Decoder.decodeInt
    }

  }

}
