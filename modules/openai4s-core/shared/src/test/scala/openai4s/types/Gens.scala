package openai4s.types

import hedgehog.{Gen, Range}
import openai4s.compat.TypesCompat

/** @author Kevin Lee
  * @since 2023-04-02
  */
object Gens extends TypesCompat {

  def genRole: Gen[Message.Role] =
    Gen
      .string(Gen.unicode, Range.linear(1, 10))
      .map(role => Message.Role(NonEmptyString.unsafeFrom(role)))

  def genContent: Gen[Message.Content] =
    Gen
      .string(Gen.unicode, Range.linear(1, 800))
      .map(content => Message.Content(NonEmptyString.unsafeFrom(content)))

  def genMessage: Gen[Message] =
    for {
      role    <- genRole
      content <- genContent
    } yield Message(role, content)

}
