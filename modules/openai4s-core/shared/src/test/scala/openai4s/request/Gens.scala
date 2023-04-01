package openai4s.request

import cats.data.NonEmptyList
import eu.timepit.refined.types.numeric.{NonNegDouble, PosInt}
import eu.timepit.refined.types.string.NonEmptyString
import hedgehog._
import openai4s.types.Model

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

  def genMessage: Gen[Chat.Message] =
    for {
      role    <- Gen
                   .string(Gen.unicode, Range.linear(1, 10))
                   .map(role => Chat.Message.Role(NonEmptyString.unsafeFrom(role)))
      content <- Gen
                   .string(Gen.unicode, Range.linear(1, 800))
                   .map(content => Chat.Message.Content(NonEmptyString.unsafeFrom(content)))
    } yield Chat.Message(role, content)

  def genTemperature: Gen[Chat.Temperature] =
    Gen.double(Range.linearFrac(0d, 200d)).map(d => Chat.Temperature(NonNegDouble.unsafeFrom(d)))

  def genMaxTokens: Gen[Chat.MaxTokens] =
    Gen.int(Range.linear(1, 10000)).map(n => Chat.MaxTokens(PosInt.unsafeFrom(n)))

  def genChat: Gen[Chat] =
    for {
      model       <- genModel
      messages    <- genMessage.list(Range.linear(1, 10)).map(NonEmptyList.fromListUnsafe)
      temperature <- genTemperature.option
      maxTokens   <- genMaxTokens.option
    } yield Chat(
      model = model,
      messages = messages,
      temperature = temperature,
      maxTokens = maxTokens,
    )
}
