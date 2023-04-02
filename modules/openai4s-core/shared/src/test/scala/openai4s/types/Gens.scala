package openai4s.types

import eu.timepit.refined.types.string.NonEmptyString
import hedgehog.{Gen, Range}

/** @author Kevin Lee
  * @since 2023-04-02
  */
object Gens {

  def genModel: Gen[Model] =
    Gen.element1(
      Model.gpt_4,
      Model.gpt_4_0314,
      Model.gpt_4_32k,
      Model.gpt_4_32k_0314,
      Model.gpt_3_5_Turbo,
      Model.gpt_3_5_Turbo_0301,
      Model.text_Davinci_003,
      Model.text_Davinci_002,
      Model.code_Davinci_002,
    )

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
