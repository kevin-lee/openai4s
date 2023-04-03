package openai4s.response

import eu.timepit.refined.types.numeric.PosInt
import eu.timepit.refined.types.string.NonEmptyString
import extras.refinement.syntax.all.*
import hedgehog.*
import hedgehog.extra.{NumGens, refined}
import openai4s.types

import java.time.Instant

/** @author Kevin Lee
  * @since 2023-04-02
  */
object Gens {

  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  def genId: Gen[Response.Id] = {
    refined
      .StringGens
      .genNonWhitespaceString(PosInt(20))
      .list(Range.singleton(2))
      .map(_.reduce(_ ++ NonEmptyString("-") ++ _))
      .map(Response.Id(_))
  }

  def genObject: Gen[Response.Object] =
    refined
      .StringGens
      .genNonWhitespaceString(PosInt(10))
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
      model   <- types.Gens.genModel
      usage   <- genUsage
      choices <- genMessageAndFinishReason
                   .list(Range.linear(1, 10))
                   .map(
                     _.zipWithIndex
                       .map {
                         case ((message, finishReason), index) =>
                           Response.Choice(message, finishReason, Response.Choice.Index(index))
                       }
                   )
    } yield Response(id = id, `object` = obj, created = created, model = model, usage = usage, choices = choices)
}
