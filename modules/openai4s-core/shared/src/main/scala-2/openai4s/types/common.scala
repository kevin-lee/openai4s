package openai4s.types

import cats.syntax.all.*
import cats.{Eq, Show}
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.cats.*
import eu.timepit.refined.numeric.Interval
import eu.timepit.refined.types.numeric.{NonNegInt, PosInt}
import extras.render.Render
import extras.render.refined.*
import io.circe.refined.*
import io.circe.{Decoder, Encoder}
import io.estatico.newtype.macros.newtype

/** @author Kevin Lee
  * @since 2023-03-24
  */
object common {

  type Temperature = Float Refined Interval.Closed[0f, 2f]
  object Temperature extends RefinedTypeOps.Numeric[Temperature, Float] {

    implicit val temperatureEq: Eq[Temperature] = Eq.fromUniversalEquals

    implicit val temperatureShow: Show[Temperature] = Show.catsShowForFloat.contramap(_.value)

    implicit val temperatureEncoder: Encoder[Temperature] = Encoder.encodeFloat.contramap(_.value)
    implicit val temperatureDecoder: Decoder[Temperature] = Decoder.decodeFloat.emap(from)
  }

  @newtype case class MaxTokens(value: PosInt)
  object MaxTokens {
    implicit val maxTokensEq: Eq[MaxTokens] = deriving

    implicit val maxTokensShow: Show[MaxTokens] = deriving

    implicit val maxTokensEncoder: Encoder[MaxTokens] = deriving
    implicit val maxTokensDecoder: Decoder[MaxTokens] = deriving
  }

  @newtype case class Index(value: NonNegInt)
  object Index {
    def unsafeFrom(index: Int): Index = Index(NonNegInt.unsafeFrom(index))

    implicit val indexEq: Eq[Index] = deriving

    implicit val indexShow: Show[Index]     = deriving
    implicit val indexRender: Render[Index] = deriving

    implicit val indexEncoder: Encoder[Index] = deriving
    implicit val indexDecoder: Decoder[Index] = deriving
  }

  @newtype case class FinishReason(value: String)
  object FinishReason {
    implicit val finishReasonEq: Eq[FinishReason] = deriving

    implicit val finishReasonShow: Show[FinishReason]     = deriving
    implicit val finishReasonRender: Render[FinishReason] = deriving

    implicit val finishReasonEncoder: Encoder[FinishReason] = deriving
    implicit val finishReasonDecoder: Decoder[FinishReason] = deriving
  }
}
