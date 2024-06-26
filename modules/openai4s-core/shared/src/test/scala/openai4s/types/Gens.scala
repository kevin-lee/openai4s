package openai4s.types

import hedgehog.{Gen, Range}
import openai4s.compat.TypesCompat
import openai4s.types.chat.*

/** @author Kevin Lee
  * @since 2023-04-02
  */
object Gens extends TypesCompat {

  def genRole: Gen[Message.Role] =
    Gen
      .string(Gen.unicode, Range.linear(1, 10))
      .map(role => Message.Role(role))

  def genContent: Gen[Message.Content] =
    Gen
      .string(Gen.unicode, Range.linear(1, 800))
      .map(content => Message.Content(content))

  def genMessage: Gen[Message] =
    for {
      role    <- genRole
      content <- genContent
    } yield Message(role, content)

}
