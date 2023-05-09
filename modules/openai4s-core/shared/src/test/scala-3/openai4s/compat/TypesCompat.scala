package openai4s.compat

/** @author Kevin Lee
  * @since 2023-04-24
  */
trait TypesCompat {
  type PosInt = refined4s.numeric.PosInt
  val PosInt = refined4s.numeric.PosInt

  type NonNegFloat = refined4s.numeric.NonNegFloat
  val NonNegFloat = refined4s.numeric.NonNegFloat

  type NonEmptyString = refined4s.strings.NonEmptyString
  val NonEmptyString = refined4s.strings.NonEmptyString

  type Uri = refined4s.strings.Uri
  val Uri = refined4s.strings.Uri
}
