package openai4s.compat

import eu.timepit.refined.api.RefType
import extras.render.Render

/** @author Kevin Lee
  * @since 2023-04-24
  */
trait TypesCompat extends extras.refinement.syntax.all {
  type PosInt = eu.timepit.refined.types.numeric.PosInt
  val PosInt = eu.timepit.refined.types.numeric.PosInt

  type NonEmptyString = eu.timepit.refined.types.string.NonEmptyString
  val NonEmptyString = eu.timepit.refined.types.string.NonEmptyString

  type Uri = refined4s.strings.Uri
  val Uri = refined4s.strings.Uri

  implicit def refTypeRender[F[_, _], T: Render, P](implicit rt: RefType[F]): Render[F[T, P]] =
    extras.render.refined.refTypeRender
}
