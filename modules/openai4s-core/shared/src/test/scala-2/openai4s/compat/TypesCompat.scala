package openai4s.compat

import eu.timepit.refined.api.{RefType, Refined, Validate}
import eu.timepit.refined.types.{numeric, string}
import extras.render.Render
import refined4s.strings

/** @author Kevin Lee
  * @since 2023-04-24
  */
trait TypesCompat extends extras.refinement.syntax.all with io.circe.refined.CirceCodecRefined {
  type PosInt = eu.timepit.refined.types.numeric.PosInt
  val PosInt: numeric.PosInt.type = eu.timepit.refined.types.numeric.PosInt

  type NonNegFloat = eu.timepit.refined.types.numeric.NonNegFloat
  val NonNegFloat: numeric.NonNegFloat.type = eu.timepit.refined.types.numeric.NonNegFloat

  type NonEmptyString = eu.timepit.refined.types.string.NonEmptyString
  val NonEmptyString: string.NonEmptyString.type = eu.timepit.refined.types.string.NonEmptyString

  type Uri = refined4s.strings.Uri
  val Uri: strings.Uri.type = refined4s.strings.Uri

  import eu.timepit.refined.macros.RefineMacro

  implicit def autoRefineV[T, P](t: T)(
    implicit rt: RefType[Refined],
    v: Validate[T, P],
  ): Refined[T, P] = macro RefineMacro.impl[Refined, T, P]

  implicit def refTypeRender[F[_, _], T: Render, P](implicit rt: RefType[F]): Render[F[T, P]] =
    extras.render.refined.refTypeRender

  val CommonGens = hedgehog.extra.Gens

  val RefinedNumGens = hedgehog.extra.refined.NumGens
  val StringGens     = hedgehog.extra.refined.StringGens
  val NetGens        = hedgehog.extra.refined.NetGens

}
