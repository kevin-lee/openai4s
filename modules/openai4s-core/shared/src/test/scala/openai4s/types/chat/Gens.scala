package openai4s.types.chat

import cats.data.NonEmptyList
import hedgehog.*
import hedgehog.extra.NumGens
import openai4s.compat.TypesCompat
import openai4s.types
import openai4s.types.common.*

import java.time.Instant

/** @author Kevin Lee
  * @since 2023-04-02
  */
object Gens extends TypesCompat {

  def genModel: Gen[Model] =
    Gen.elementUnsafe(Model.supportedValues)

  object chat {

    def genTemperature: Gen[Temperature] =
      Gen.double(Range.linearFrac(0d, 2d)).map(d => Temperature.unsafeFrom(d.toFloat))

    def genMaxTokens: Gen[MaxTokens] =
      RefinedNumGens
        .genPosInt(PosInt(1), PosInt(10000))
        .map(MaxTokens(_))

    def genChat: Gen[Chat] =
      for {
        model       <- Gens.genModel
        messages    <- types
                         .Gens
                         .genMessage
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
      StringGens
        .genNonEmptyStringMinMax(Gen.alphaNum, PosInt(1), PosInt(20))
        .list(Range.singleton(2))
        .map(_.reduce(_ ++ NonEmptyString("-") ++ _))
        .map(Response.Id(_))
    }

    def genObject: Gen[Response.Object] =
      StringGens
        .genNonEmptyStringMinMax(Gen.choice1(Gen.alphaNum, Gen.constant('.')), PosInt(1), PosInt(10))
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

    def genFinishReason: Gen[FinishReason] =
      Gen.string(Gen.alphaNum, Range.linear(3, 10)).map(FinishReason(_))

    def genMessageAndFinishReason: Gen[(Message, FinishReason)] =
      for {
        message      <- types.Gens.genMessage
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
                             Response.Choice(message, finishReason, Index.unsafeFrom(index))
                         }
                     )
      } yield Response(id = id, `object` = obj, created = created, model = model, usage = usage, choices = choices)
  }

}
