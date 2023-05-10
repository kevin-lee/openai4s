package openai4s.types.completions

import extras.hedgehog.circe.RoundTripTester
import extras.render.syntax.*
import hedgehog.*
import hedgehog.runner.*
import io.circe.Json
import openai4s.compat.TypesCompat

/** @author Kevin Lee
  * @since 2023-05-09
  */
object ResponseSpec extends Properties with TypesCompat {
  override def tests: List[Test] = List(
    property("round-trip test Response", roundTripTestResponse),
    property("test encoding Response", testEncodingResponse),
  )

  def roundTripTestResponse: Property =
    for {
      response <- Gens.response.genResponse.log("response")
    } yield {
      RoundTripTester(response).test()
    }

  def testEncodingResponse: Property =
    for {
      response <- Gens.response.genResponse.log("response")
    } yield {
      import io.circe.literal.*
      import io.circe.syntax.*

      def toJson(choice: Response.Choice): Json =
        json"""
          {
            "text": ${choice.text.render},
            "logprobs": ${choice.logprobs.map(_.value)},
            "index": ${choice.index.value},
            "finish_reason": ${choice.finishReason.render}
          }
        """

      val expected =
        json"""
          {
            "id": ${response.id.render},
            "object": ${response.`object`.render},
            "created": ${response.created.value.getEpochSecond},
            "model": ${response.model.render},
            "choices": ${response.choices.map(toJson).asJson},
            "usage": {
              "prompt_tokens": ${response.usage.promptTokens.value},
              "completion_tokens": ${response.usage.completionTokens.value},
              "total_tokens": ${response.usage.totalTokens.value}
            }
          }""".deepDropNullValues

      val actual = response.asJson
      actual ==== expected
    }

}
