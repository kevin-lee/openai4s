package openai4s.types.completions

import cats.syntax.all.*
import hedgehog.*
import hedgehog.extra.NumGens
import openai4s.compat.TypesCompat
import hedgehog.cats.instances.*
import openai4s.types.common.*

import java.time.Instant

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
        .map(prompt => Text.Prompt(prompt))

    def genMaxTokens: Gen[MaxTokens] =
      Gen
        .int(Range.linear(1, Int.MaxValue))
        .map(maxTokens => MaxTokens(PosInt.unsafeFrom(maxTokens)))

    def genTemperature: Gen[Temperature] =
      Gen.double(Range.linearFrac(0d, 2d)).map(d => Temperature.unsafeFrom(d.toFloat))

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

  object response {

    @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
    def genId: Gen[Response.Id] = {
      Gen
        .string(Gen.alphaNum, Range.linear(1, 20))
        .map(NonEmptyString.unsafeFrom)
        .list(Range.singleton(2))
        .map(_.reduce(_ ++ NonEmptyString("-") ++ _))
        .map(Response.Id(_))
    }

    def genObject: Gen[Response.Object] =
      Gen
        .string(Gen.choice1(Gen.alphaNum, Gen.constant('.')), Range.linear(1, 10))
        .map(NonEmptyString.unsafeFrom)
        .map(Response.Object(_))

    def genCreated: Gen[Response.Created] =
      for {
        now        <- Gen.constant(Instant.now())
        (min, max) <- NumGens.genLongMinMaxPair(0L, 10L * 60)
        diff       <- Gen.long(Range.linear(min, max))
        created = Instant.ofEpochSecond(now.minusSeconds(diff).getEpochSecond)
      } yield Response.Created(created)

    def genPromptTokens: Gen[Response.Usage.PromptTokens] =
      Gen.int(Range.linear(1, 100)).map(Response.Usage.PromptTokens(_))

    def genCompletionTokens: Gen[Response.Usage.CompletionTokens] =
      Gen.int(Range.linear(1, 100)).map(Response.Usage.CompletionTokens(_))

    def genUsage: Gen[Response.Usage] =
      for {
        promptTokens     <- genPromptTokens
        completionTokens <- genCompletionTokens
        totalTokens = Response.Usage.TotalTokens(promptTokens.value + completionTokens.value)

      } yield Response.Usage(
        promptTokens = promptTokens,
        completionTokens = completionTokens,
        totalTokens = totalTokens,
      )

    def genText: Gen[Response.Choice.Text] =
      Gen
        .string(Gen.unicode, Range.linear(1, 800))
        .map(content => Response.Choice.Text(NonEmptyString.unsafeFrom(content)))

    def genFinishReason: Gen[FinishReason] =
      Gen.string(Gen.alphaNum, Range.linear(3, 10)).map(FinishReason(_))

    def genLogprobs: Gen[Response.Choice.Logprobs] =
      Gen.int(Range.linear(1, Int.MaxValue)).map(Response.Choice.Logprobs(_))

    def genChoice(index: Index): Gen[Response.Choice] =
      for {
        text         <- genText
        logprobs     <- genLogprobs.option
        finishReason <- genFinishReason
      } yield Response.Choice(text, index, logprobs, finishReason)

    def genResponse: Gen[Response] =
      for {
        id      <- genId
        obj     <- genObject
        created <- genCreated
        model   <- Gens.genModel
        usage   <- genUsage
        choices <- Gen
                     .int(Range.linear(0, 10))
                     .flatMap(upto =>
                       (0 to upto)
                         .toList
                         .traverse { index =>
                           genChoice(Index.unsafeFrom(index))
                         }
                     )
      } yield Response(id = id, `object` = obj, created = created, model = model, usage = usage, choices = choices)
  }
}
