package openai4s.request

import cats.data.NonEmptyList
import eu.timepit.refined.types.numeric.{NonNegDouble, PosInt}
import hedgehog.*
import openai4s.types

/** @author Kevin Lee
  * @since 2023-04-02
  */
object Gens {

  def genTemperature: Gen[Chat.Temperature] =
    Gen.double(Range.linearFrac(0d, 200d)).map(d => Chat.Temperature(NonNegDouble.unsafeFrom(d)))

  def genMaxTokens: Gen[Chat.MaxTokens] =
    Gen.int(Range.linear(1, 10000)).map(n => Chat.MaxTokens(PosInt.unsafeFrom(n)))

  def genChat: Gen[Chat] =
    for {
      model    <- types.Gens.genModel
      messages <- types.Gens.genMessage.map(Chat.Message(_)).list(Range.linear(1, 10)).map(NonEmptyList.fromListUnsafe)
      temperature <- genTemperature.option
      maxTokens   <- genMaxTokens.option
    } yield Chat(
      model = model,
      messages = messages,
      temperature = temperature,
      maxTokens = maxTokens,
    )
}
