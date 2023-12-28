package openai4s.types

import cats.{Eq, Show}
import extras.render.*
import io.circe.*
import openai4s.types
import refined4s.*
import refined4s.modules.cats.derivation.*
import refined4s.modules.cats.derivation.types.all.given
import refined4s.modules.circe.derivation.*
import refined4s.modules.circe.derivation.types.all.given
import refined4s.syntax.*
import refined4s.types.all.*

import scala.annotation.targetName

/** @author Kevin Lee
  * @since 2023-03-24
  */
object common {

  type Temperature = Temperature.Type
  object Temperature extends InlinedRefined[Float] with CatsEqShow[Float] with CirceRefinedCodec[Float] {
    override inline val inlinedExpectedValue = "a Float between 0f and 2f (inclusive)"

    override inline def inlinedPredicate(inline a: Float): Boolean = a >= 0f && a <= 2f

    override def invalidReason(a: Float): String =
      "The temperature must be a Float between 0f and 2f (inclusive) but got [" + a + "]"

    inline override def predicate(a: Float): Boolean = a >= 0f && a <= 2f

  }

  type MaxTokens = MaxTokens.Type
  object MaxTokens extends Newtype[PosInt], CatsEqShow[PosInt], CirceNewtypeCodec[PosInt] {

    @targetName("fromInt")
    inline def apply(inline token: Int): Type = wrap(PosInt(token))

  }

  type Index = Index.Type
  object Index extends Newtype[NonNegInt] with CatsEqShow[NonNegInt] with CirceNewtypeCodec[NonNegInt] {

    @targetName("fromInt")
    inline def apply(inline index: Int): Index = wrap(NonNegInt(index))

    def unsafeFrom(index: Int): Index = wrap(NonNegInt.unsafeFrom(index))

    given indexRender: Render[Index] = Render.intRender.contramap(_.toValue)

  }

  type FinishReason = FinishReason.Type

  object FinishReason extends Newtype[String] with CatsEqShow[String] with CirceNewtypeCodec[String] {

    given finishReasonRender: Render[FinishReason] = deriving

  }

}
