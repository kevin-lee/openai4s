package openai4s.types.completions

import hedgehog.*
import openai4s.compat.TypesCompat

/** @author Kevin Lee
  * @since 2023-05-08
  */
object Gens extends TypesCompat {

  def genModel: Gen[Model] =
    Gen.element1(
      Model.text_Davinci_003,
      Model.text_Davinci_002,
      Model.text_Curie_001,
      Model.text_Babbage_001,
      Model.text_Ada_001,
    )

  object text {
    def genPrompt: Gen[Text.Prompt] =
      Gen
        .string(Gen.unicode, Range.linear(1, 800))
        .map(prompt => Text.Prompt(NonEmptyString.unsafeFrom(prompt)))

    def genMaxTokens: Gen[Text.MaxTokens] =
      Gen
        .int(Range.linear(1, Int.MaxValue))
        .map(maxTokens => Text.MaxTokens(PosInt.unsafeFrom(maxTokens)))

    def genTemperature: Gen[Text.Temperature] =
      Gen.double(Range.linearFrac(0d, 2d)).map(d => Text.Temperature.unsafeFrom(d.toFloat))

    def genTopP: Gen[Text.TopP] =
      Gen.double(Range.linearFrac(0d, 2d)).map(d => Text.TopP(NonNegFloat.unsafeFrom(d.toFloat)))

    def genN: Gen[Text.N] =
      Gen.int(Range.linear(1, Int.MaxValue)).map(Text.N(_))

    def genStream: Gen[Text.Stream] =
      Gen.element1(Text.Stream.isStream, Text.Stream.notStream)

    def genLogprobs: Gen[Text.Logprobs] =
      Gen.int(Range.linear(1, Int.MaxValue)).map(Text.Logprobs(_))

    def genStop: Gen[Text.Stop] =
      Gen
        .string(Gen.unicode, Range.linear(1, 800))
        .map(s => Text.Stop(NonEmptyString.unsafeFrom(s)))

    def genText: Gen[Text] =
      for {
        model       <- Gens.genModel
        prompt      <- genPrompt.option
        maxTokens   <- genMaxTokens.option
        temperature <- genTemperature.option
        topP        <- genTopP.option
        n           <- genN.option
        stream      <- genStream.option
        logprobs    <- genLogprobs.option
        stop        <- genStop.option
      } yield Text(model, prompt, maxTokens, temperature, topP, n, stream, logprobs, stop)
  }
}
