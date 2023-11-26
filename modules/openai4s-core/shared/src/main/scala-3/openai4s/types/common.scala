package openai4s.types

import cats.syntax.all.*
import cats.{Eq, Show}
import io.circe.*
import newtype4s.Newtype
import openai4s.types
import refined4s.*
import refined4s.numeric.*

import extras.render.*

import scala.annotation.targetName
import scala.compiletime.*

/** @author Kevin Lee
  * @since 2023-03-24
  */
object common {

  type Temperature = Temperature.Type
  object Temperature extends InlinedRefined[Float] {

    override inline def inlinedInvalidReason(inline a: Float): String =
      "The temperature must be a Float between 0f and 2f (inclusive) but got [" + codeOf(a) + "]"

    override inline def inlinedPredicate(inline a: Float): Boolean = a >= 0f && a <= 2f

    override def invalidReason(a: Float): String =
      "The temperature must be a Float between 0f and 2f (inclusive) but got [" + a + "]"

    inline override def predicate(a: Float): Boolean = a >= 0f && a <= 2f

    given temperatureEq: Eq[Temperature] = Eq.fromUniversalEquals

    given temperatureShow: Show[Temperature] = Show[Float].contramap(_.value)

    given temperatureEncoder: Encoder[Temperature] = Encoder[Float].contramap(_.value)
    given temperatureDecoder: Decoder[Temperature] = Decoder[Float].emap(from)
  }

  type MaxTokens = MaxTokens.Type
  object MaxTokens extends Newtype[PosInt] {

    @targetName("fromInt")
    inline def apply(inline token: Int): MaxTokens = toType(PosInt(token))

    extension (maxTokens: MaxTokens) {
      def toValue: Int = maxTokens.value.value
    }

    given maxTokensEq: Eq[MaxTokens]     = Eq.fromUniversalEquals
    given maxTokensShow: Show[MaxTokens] = Show[Int].contramap(_.toValue)

    given maxTokensEncoder: Encoder[MaxTokens] = Encoder[Int].contramap(_.toValue)
    given maxTokensDecoder: Decoder[MaxTokens] = Decoder[Int].emap(PosInt.from).map(MaxTokens(_))
  }

  type Index = Index.Type
  object Index extends Newtype[NonNegInt] {

    @targetName("fromInt")
    inline def apply(inline index: Int): Index = toType(NonNegInt(index))

    def unsafeFrom(index: Int): Index = apply(NonNegInt.unsafeFrom(index))

    given indexEq: Eq[Index] = Eq.fromUniversalEquals

    given indexShow: Show[Index]     = Show.catsShowForInt.contramap(_.value.value)
    given indexRender: Render[Index] = Render.intRender.contramap(_.value.value)

    given indexEncoder: Encoder[Index] = Encoder.encodeInt.contramap(_.value.value)
    given indexDecoder: Decoder[Index] = Decoder.decodeInt.emap(NonNegInt.from).map(Index(_))
  }

  type FinishReason = FinishReason.Type

  object FinishReason extends Newtype[String] {
    given finishReasonEq: Eq[FinishReason] = Eq.fromUniversalEquals

    given finishReasonShow: Show[FinishReason]     = Show.catsShowForString.contramap(_.value)
    given finishReasonRender: Render[FinishReason] = Render.stringRender.contramap(_.value)

    given finishReasonEncoder: Encoder[FinishReason] = Encoder.encodeString.contramap(_.value)
    given finishReasonDecoder: Decoder[FinishReason] = Decoder.decodeString.map(FinishReason(_))
  }

}
