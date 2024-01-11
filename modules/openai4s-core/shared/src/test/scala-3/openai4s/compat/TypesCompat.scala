package openai4s.compat

/** @author Kevin Lee
  * @since 2023-04-24
  */
trait TypesCompat extends refined4s.syntax {
  type PosInt = refined4s.types.all.PosInt
  val PosInt = refined4s.types.all.PosInt

  type NonNegInt = refined4s.types.all.NonNegInt
  val NonNegInt = refined4s.types.all.NonNegInt

  type NonNegFloat = refined4s.types.all.NonNegFloat
  val NonNegFloat = refined4s.types.all.NonNegFloat

  type NonEmptyString = refined4s.types.all.NonEmptyString
  val NonEmptyString = refined4s.types.all.NonEmptyString

  type Uri = refined4s.types.all.Uri
  val Uri = refined4s.types.all.Uri

  val CommonGens = hedgehog.extra.Gens

  val RefinedNumGens = hedgehog.extra.refined4s.gens.NumGens
  val StringGens     = hedgehog.extra.refined4s.gens.StringGens
  val NetGens        = hedgehog.extra.refined4s.gens.NetworkGens

}
