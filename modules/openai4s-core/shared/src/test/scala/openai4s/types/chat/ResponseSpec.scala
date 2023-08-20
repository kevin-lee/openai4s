package openai4s.types.chat

import extras.hedgehog.circe.RoundTripTester
import extras.render.syntax.*
import hedgehog.*
import hedgehog.runner.*
import io.circe.Json
import openai4s.types.chat.Response

/** @author Kevin Lee
  * @since 2023-04-02
  */
object ResponseSpec extends Properties {
  override def tests: List[Test] = List(
    property("round-trip test for Response", roundTripTestResponse),
    property("test encoding Response", testEncodingResponse),
  )

  def roundTripTestResponse: Property =
    for {
      response <- Gens.response.genResponse.log("response")
    } yield {
      RoundTripTester(response).test()
    }

  def testEncodingResponse: Property = for {
    response <- Gens.response.genResponse.log("response")
  } yield {
    import io.circe.literal.*
    import io.circe.syntax.*

    def toJson(choice: Response.Choice): Json =
      json"""
      {
        "message": {
          "role": ${choice.message.value.role.render},
          "content": ${choice.message.value.content.render}
        },
        "finish_reason": ${choice.finishReason.render},
        "index": ${choice.index.value.value}
      }
      """

    val expected =
      json"""
      {
        "id": ${response.id.render},
        "object": ${response.`object`.render},
        "created": ${response.created.value.getEpochSecond},
        "model": ${response.model.render},
        "usage": {
          "prompt_tokens": ${response.usage.promptTokens.value},
          "completion_tokens": ${response.usage.completionTokens.value},
          "total_tokens": ${response.usage.totalTokens.value}
        },
        "choices": ${response.choices.map(toJson).asJson}
      }""".deepDropNullValues

    val actual = response.asJson

    actual ==== expected
  }

}
