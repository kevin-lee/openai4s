package openai4s.types.chat

import cats.data.NonEmptyList
import hedgehog.*
import hedgehog.extra.NumGens
import openai4s.compat.TypesCompat
import openai4s.types

import java.time.Instant

/** @author Kevin Lee
  * @since 2023-04-02
  */
object Gens extends TypesCompat {

  def genModel: Gen[Model] =
    Gen.element1(
      Model.gpt_4_1106_Preview,
      Model.gpt_4_Vision_Preview,
      Model.gpt_4,
      Model.gpt_4_32k,
      Model.gpt_4_0613,
      Model.gpt_4_32k_0613,
      Model.gpt_4_0314,
      Model.gpt_4_32k_0314,
      Model.gpt_3_5_Turbo,
      Model.gpt_3_5_turbo_16k,
      Model.gpt_3_5_turbo_0613,
      Model.gpt_3_5_turbo_16k_0613,
      Model.gpt_3_5_Turbo_0301,
    )

  object chat {

    def genTemperature: Gen[Chat.Temperature] =
      Gen.double(Range.linearFrac(0d, 2d)).map(d => Chat.Temperature.unsafeFrom(d.toFloat))

    def genMaxTokens: Gen[Chat.MaxTokens] =
      Gen.int(Range.linear(1, 10000)).map(n => Chat.MaxTokens(PosInt.unsafeFrom(n)))

    def genChat: Gen[Chat] =
      for {
        model       <- Gens.genModel
        messages    <- types
                         .Gens
                         .genMessage
                         .map(Chat.Message(_))
                         .list(Range.linear(1, 10))
                         .map(NonEmptyList.fromListUnsafe)
        temperature <- genTemperature.option
        maxTokens   <- genMaxTokens.option
      } yield Chat(
        model = model,
        messages = messages,
        temperature = temperature,
        maxTokens = maxTokens,
      )
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

    def genFinishReason: Gen[Response.Choice.FinishReason] =
      Gen.string(Gen.alphaNum, Range.linear(3, 10)).map(Response.Choice.FinishReason(_))

    def genMessageAndFinishReason: Gen[(Response.Choice.Message, Response.Choice.FinishReason)] =
      for {
        message      <- types.Gens.genMessage.map(Response.Choice.Message(_))
        finishReason <- genFinishReason
      } yield (message, finishReason)

    def genResponse: Gen[Response] =
      for {
        id      <- genId
        obj     <- genObject
        created <- genCreated
        model   <- Gens.genModel
        usage   <- genUsage
        choices <- genMessageAndFinishReason
                     .list(Range.linear(1, 10))
                     .map(
                       _.zipWithIndex
                         .map {
                           case ((message, finishReason), index) =>
                             Response.Choice(message, finishReason, Response.Choice.Index.unsafeFrom(index))
                         }
                     )
      } yield Response(id = id, `object` = obj, created = created, model = model, usage = usage, choices = choices)
  }

}
