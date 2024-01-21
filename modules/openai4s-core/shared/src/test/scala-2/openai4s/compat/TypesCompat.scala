package openai4s.compat

import eu.timepit.refined.api.{RefType, Refined, Validate}
import extras.render.Render
import refined4s.compat.RefinedCompatAllTypes

/** @author Kevin Lee
  * @since 2023-04-24
  */
trait TypesCompat
    extends RefinedCompatAllTypes
    with extras.refinement.syntax.all
    with io.circe.refined.CirceCodecRefined {

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
