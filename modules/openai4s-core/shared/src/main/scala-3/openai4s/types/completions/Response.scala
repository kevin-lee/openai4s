package openai4s.types.completions

import cats.syntax.all.*
import cats.{Eq, Show}
import extras.render.Render
import io.circe.derivation.{Configuration, ConfiguredCodec, ConfiguredDecoder, ConfiguredEncoder}
import io.circe.{Codec, Decoder, Encoder}
import newtype4s.Newtype
import openai4s.types
import openai4s.types.common.*
import refined4s.strings.NonEmptyString

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
)
object Response {

  given responseConfiguration: Configuration = Configuration.default.withSnakeCaseMemberNames

  given responseEq: Eq[Response] = Eq.fromUniversalEquals

  given responseShow: Show[Response] = cats.derived.semiauto.show

  given encoder: Encoder[Response] = ConfiguredEncoder.derived[Response].mapJson(_.deepDropNullValues)

  given decoder: Decoder[Response] = ConfiguredDecoder.derived

  type Id = Id.Type
  object Id extends Newtype[NonEmptyString] {
    extension (id: Id) {
      def toValue: String = id.value.value
    }

    given idEq: Eq[Id] = Eq.fromUniversalEquals

    given idRender: Render[Id] = Render[String].contramap(_.toValue)
    given idShow: Show[Id]     = Show[String].contramap(_.toValue)

    given idEncoder: Encoder[Id] = Encoder[String].contramap(_.toValue)
    given idDecoder: Decoder[Id] = Decoder[String].emap(NonEmptyString.from).map(Id(_))
  }

  type Object = Object.Type
  object Object extends Newtype[NonEmptyString] {
    extension (obj: Object) {
      def toValue: String = obj.value.value
    }
    given objectEq: Eq[Object] = Eq.fromUniversalEquals

    given objectRender: Render[Object] = Render[String].contramap(_.toValue)
    given objectShow: Show[Object]     = Show[String].contramap(_.toValue)

    given objectEncoder: Encoder[Object] = Encoder[String].contramap(_.toValue)
    given objectDecoder: Decoder[Object] = Decoder[String].emap(NonEmptyString.from).map(Object(_))
  }

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
  )
  object Choice {
    given choiceEq: Eq[Choice] = Eq.fromUniversalEquals

    given choiceShow: Show[Choice] = cats.derived.semiauto.show

    given choiceCodec: Codec[Choice] = ConfiguredCodec.derived

    type Text = Text.Type
    object Text extends Newtype[NonEmptyString] {
      given textEq: Eq[Text] = Eq.fromUniversalEquals

      given textShow: Show[Text]     = Show.show(_.value.value)
      given textRender: Render[Text] = Render.render(_.value.value)

      given textEncoder: Encoder[Text] = Encoder[String].contramap(_.value.value)
      given textDecoder: Decoder[Text] = Decoder[String].emap(NonEmptyString.from).map(Text(_))
    }

    type Logprobs = Logprobs.Type
    object Logprobs extends Newtype[Int] {
      given logprobsEq: Eq[Logprobs] = Eq.fromUniversalEquals

      given logprobsShow: Show[Logprobs] = Show.catsShowForInt.contramap(_.value)

      given logprobsEncoder: Encoder[Logprobs] = Encoder.encodeInt.contramap(_.value)
      given logprobsDecoder: Decoder[Logprobs] = Decoder.decodeInt.map(Logprobs(_))
    }

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

    type PromptTokens = PromptTokens.Type
    object PromptTokens extends Newtype[Int] {

      given promptTokensEq: Eq[PromptTokens] = Eq.fromUniversalEquals

      given promptTokensShow: Show[PromptTokens]     = Show.catsShowForInt.contramap(_.value)
      given promptTokensRender: Render[PromptTokens] = Render.intRender.contramap(_.value)

      given promptTokensEncoder: Encoder[PromptTokens] = Encoder.encodeInt.contramap(_.value)
      given promptTokensDecoder: Decoder[PromptTokens] = Decoder.decodeInt.map(PromptTokens(_))
    }

    type CompletionTokens = CompletionTokens.Type
    object CompletionTokens extends Newtype[Int] {

      given completionTokensEq: Eq[CompletionTokens] = Eq.fromUniversalEquals

      given completionTokensShow: Show[CompletionTokens]     = Show.catsShowForInt.contramap(_.value)
      given completionTokensRender: Render[CompletionTokens] = Render.intRender.contramap(_.value)

      given completionTokensEncoder: Encoder[CompletionTokens] = Encoder.encodeInt.contramap(_.value)
      given completionTokensDecoder: Decoder[CompletionTokens] = Decoder.decodeInt.map(CompletionTokens(_))
    }

    type TotalTokens = TotalTokens.Type
    object TotalTokens extends Newtype[Int] {
      given totalTokensEq: Eq[TotalTokens] = Eq.fromUniversalEquals

      given totalTokensShow: Show[TotalTokens]     = Show.catsShowForInt.contramap(_.value)
      given totalTokensRender: Render[TotalTokens] = Render.intRender.contramap(_.value)

      given totalTokensEncoder: Encoder[TotalTokens] = Encoder.encodeInt.contramap(_.value)
      given totalTokensDecoder: Decoder[TotalTokens] = Decoder.decodeInt.map(TotalTokens(_))
    }

  }

}
